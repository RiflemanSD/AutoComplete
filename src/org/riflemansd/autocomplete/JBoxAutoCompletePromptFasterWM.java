package org.riflemansd.autocomplete;

/**
 * AutoComplete project using JComboBox
 * 
 * (c) Copyright | Sotiris Doudis | 2014-2015
 * Github: RiflemanSD - https://github.com/RiflemanSD
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextField;
import org.riflemansd.customcombobox.CustomComboBox;
import org.riflemansd.utils.TimeCalc;

/**
 * JComboBoxAutoComplete with prompt text
 * 
 * @author RiflemanSD
 */
public class JBoxAutoCompletePromptFasterWM extends CustomComboBox implements KeyListener,FocusListener {
    private ArrayList<AutoCompleteEvent> listeners;
    
    private int caretPos;
    private WordWithMap[] words;
    private String promptText;
    private HashMap<Character, Character> greekToneChars;
    boolean isPrompt;

    public JBoxAutoCompletePromptFasterWM(String... words) {
        init(null, 4, words);
        initPromptText("");
    }
    public JBoxAutoCompletePromptFasterWM(String prompt, boolean p, String... words) {
        init(null, 4, words);
        initPromptText(prompt);
    }
    public JBoxAutoCompletePromptFasterWM(String prompt, Font font, int maxRowCount, String... words) {
        init(font, maxRowCount, words);
        initPromptText(prompt);
    }
    
    private void init(Font font, int maxRowCount, String... words) {
        this.listeners = new ArrayList<AutoCompleteEvent>();
        this.initGreekToneHashMap();
        initWords(words);
        
        this.getEditor().getEditorComponent().addKeyListener(this);
        if (font != null) this.setFont(font);
        
        this.setEditable(true);
        this.setMaximumRowCount(maxRowCount);
    }
    
    private void initGreekToneHashMap() {
        Character[] toneChars   = {'ά','έ','ί','ή','ύ','ό','ώ', 'Ά','Έ','Ί','Ή','Ύ','Ό','Ώ'};
        Character[] noToneChars = {'α','ε','ι','η','υ','ο','ω', 'Α','Ε','Ι','Η','Υ','Ο','Ω'};
        
        this.greekToneChars = new HashMap<>();
        
        for (int i = 0; i < toneChars.length; i++) {
            greekToneChars.put(toneChars[i], noToneChars[i]);
        }
    }
    private void initWords(String... words) {
        TimeCalc t = new TimeCalc();
        t.start();
        this.words = new WordWithMap[words.length];
        
        for (int i1 = 0; i1 < words.length; i1++) {
            this.words[i1] = new WordWithMap(words[i1]);
            int i = 0;
            for (Character c : this.words[i1].getWord().toCharArray()) {
                Character gtc;
                if (greekToneChars.containsKey(c)) {
                    gtc = greekToneChars.get(c);
                }
                else {
                    gtc = c;
                }
                gtc = gtc.toString().toLowerCase().charAt(0);
                
                this.words[i1].addLetter(gtc, i);
                i++;
            }
        }
        t.end();
        System.out.println("Init words made: " + t.getMillis());
    }
    private void initPromptText(String promptText) {
        isPrompt = true;
        this.promptText = promptText;
        this.getEditor().getEditorComponent().setForeground(Color.lightGray);
        this.getEditor().setItem(this.promptText);
        this.getEditor().getEditorComponent().addFocusListener(this);
    }
    
    public String getValue() {
        if (isPrompt) return "";
        return (String) this.getEditor().getItem();
    }
    public void setValue(String value) {
        this.getEditor().setItem(value);
        if (isPrompt) {
            this.getEditor().getEditorComponent().setForeground(Color.black);
            isPrompt = false;
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        if (isPrompt) {
            this.getEditor().setItem("");
            this.getEditor().getEditorComponent().setForeground(Color.black);
            isPrompt = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (((String)this.getEditor().getItem()).length() == 0) {
            this.getEditor().setItem(promptText);
            this.getEditor().getEditorComponent().setForeground(Color.lightGray);
            isPrompt = true;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        JTextField textField = (JTextField)editor.getEditorComponent();
        caretPos = textField.getCaretPosition();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        //enter
        if (k == 10) {
            String[] sel = (String[]) this.getSelectedItem();
            this.getEditor().setItem(sel);
            
            for (AutoCompleteEvent hl : listeners) {
                hl.updateWord(sel[0]);
                hl.onAutoComplete();
            }
        }
        //System.out.println("t " + (String)this.getEditor().getItem());
        //typed = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        //Check if the key pressed is a character or digit
        if (!(k == 8) && !Character.isDigit(e.getKeyChar()) && !Character.isLetter(e.getKeyChar())) return;
        String w;
        w = "";

        //System.out.println(this.getEditor().getItem());
        
        //Remove all items from ComboBox
        w = (String)this.getEditor().getItem();
        this.removeAllItems();

        String nt = "";
        //There is at least a character at editor
        if (w.length() == 0) { //|| Character.isSpaceChar(w.charAt(0))
            for (AutoCompleteEvent hl : listeners) {
                hl.updateWord("");
                hl.onAutoComplete();
            }
            return;
        }
        TimeCalc t = new TimeCalc();
        t.start();
        
        //For all strings in ComboBox
        for (WordWithMap word : this.words) {
            //Lowercase all letters, replace all tone characters (for greek characters) 
            //and find the first string from editor that contains into the current word
            //System.out.println("w " + word.getWord());
            ArrayList<Integer> poses = getResults(w, word);
            //System.out.println("Pos: " + poses.size());
            if (poses.size() > 0) {
                nt += word.getWord();
                String[] ws = {word.getWord(), this.highlight(poses, word.getWord(), w, "red")};
                //Add the string into ComboBox items
                this.addItem(ws);
            }
        }
        
        t.end();
        //How much time (miliseconds) did to highlight all words
        System.out.println("T: " + t.getMillis());
        
        String[] wws = {w, w};
        
        this.getEditor().setItem(wws);
        try {
            JTextField textField = (JTextField)editor.getEditorComponent();
            if (k == 8) textField.setCaretPosition(caretPos);
            else textField.setCaretPosition(caretPos+1);
        }
        catch (IllegalArgumentException exception){
            System.err.println("e: " + exception.getMessage());
        }
        
        this.setPopupVisible(false);
        if (! (nt.length() == 0)) this.setPopupVisible(true);
        
        for (AutoCompleteEvent hl : listeners) {
            hl.updateWord(w);
            hl.onAutoComplete();
        }
    }

    public String[] getWords() {
        String[] wordsToString = new String[this.words.length];
        
        for (int i = 0; i < this.words.length; i++) {
            wordsToString[i] = this.words[i].getWord();
        }
        
        return wordsToString;
    }
    public void setWords(String[] words) {
        initWords(words);
    }

    public void addAutoCompleteListener(AutoCompleteEvent toAdd) {
        listeners.add(toAdd);
    }
    
    public String getPromptText() {
        return promptText;
    }
    public void setPromptText(String promptText) {
        this.promptText = promptText;
        if (((String)this.getEditor().getItem()).length() == 0) {
            this.getEditor().setItem(promptText);
            this.getEditor().getEditorComponent().setForeground(Color.lightGray);
            isPrompt = true;
        }
    }
    
    public ArrayList<Integer> getResults(String test, WordWithMap w) {
        //String test = "πα";
        //System.out.println("Start " + test);
        //System.out.println(w.getWord());
        
        char[] arChars = test.toCharArray();
        Character[] testChars = new Character[arChars.length];
        
        int i = 0;
        for (char c : arChars) {
            if (greekToneChars.containsKey(c)) {
                testChars[i] = greekToneChars.get(c);
            }
            else {
                testChars[i] = c;
            }
            testChars[i] = testChars[i].toString().toLowerCase().charAt(0);
            i++;
        }
        
        //Ένα η λέξη έχει μόνο ένα γράμμα, απλά επιστρέψουμε την λίστα των θέσεων για αυτό το γράμμα
        if (testChars.length == 1) {
            if (w.getLetterList(testChars[0]) == null) {
                return new ArrayList<>();
            }
            return w.getLetterList(testChars[0]);
        }
        
        HashMap<Character, Integer> testCharPos = new HashMap<>();
        
        for (Character c : testChars) {
            if (!testCharPos.containsKey(c)) {
                testCharPos.put(c, 0);
            }
        }
        
        ArrayList<Integer> result = new ArrayList<>();
        
        Character c = null;
        Character prevC = null;
        boolean stop = false;
        
        i = 0;
        int j = 0;
        while (!stop) {
            if (prevC == null) {
                prevC = testChars[i];
                i++;
            }
            else {
                c = testChars[i];
                
                if (w.getLetterList(prevC) == null) break;
                if (w.getLetterList(c) == null) break;
                
                int ppos = testCharPos.get(prevC);
                if (w.getLetterList(prevC).size() <= ppos) {
                    stop = true;
                    //System.out.println("THE END HAVE COME 2");
                    //System.out.println(prevC + ": " + (w.getLetterList(prevC).size()) + " " + ppos);
                    break;
                }
                int prev = w.getLetterList(prevC).get(ppos);
                int npos = testCharPos.get(c);
                if (w.getLetterList(c).size() <= npos) {
                    stop = true;
                    //System.out.println("THE END HAVE COME 1");
                    //System.out.println(c + ": " + (w.getLetterList(c).size()) + " " + npos);
                    break;
                }
                int now =  w.getLetterList(c).get(npos);
                
                int value = prev - now;
                //System.out.println(prevC+"[" + ppos + "] = " + prev + " " + c + "[" + npos + "] = " + now);
                //System.out.println("v = " + value + " " + i);
                
                if (value == -1) {
                    i++;
                    //System.out.println("p " + prevC + " " + c);
                    testCharPos.put(prevC, ppos + 1);
                    //testCharPos.put(c, npos + 1);
                    
                    j++;
                    if (j == testChars.length-1) {
                        result.add(now - (testChars.length - 1));
                        //System.out.println("r = " + (now - (testChars.length - 1)));
                        i = 0;
                        j = 0;
                        
                        prevC = testChars[0];
                        i++;
                    }
                    else {
                        prevC = c;
                    }
                }
                else if (value > 0) {
                    testCharPos.put(c, npos + 1);
                    
//                    if (w.getLetterList(c).size() <= npos) {
//                        stop = true;
//                        System.out.println("THE END HAVE COME 3");
//                    }
                }
                else if (value < 0) {
                    testCharPos.put(prevC, ppos + 1);
                    
//                    if (w.getLetterList(prevC).size() <= ppos) {
//                        stop = true;
//                        System.out.println("THE END HAVE COME 4");
//                    }
                }
            }
        }
        
        return result;
    }
    
    public static String highlight(ArrayList<Integer> index, String w, String subw, String color) {
        String newWord = "";
        
        if (color == null) {
            newWord = "<html>" + w.replace(subw, "<b>" + subw + "</b>") + "</html>";
        }
        else {
            String hlfs = "<font color='" + color + "'>";
            String hlfe = "</font>";
            
            String sb = w;
            for (int i = index.size()-1; i >= 0; i--) {
                //System.out.println(index);
                int in = index.get(i);
                sb = new StringBuilder(sb).insert(in+subw.length(), hlfe).insert(in, hlfs).toString();
            }
            newWord = "<html>" + sb + "</html>";
        }
        
        return newWord;
    }
    
    public String greekToneCase(String word) {
        //System.out.println(word);
        
        String newWord = word;
        String[] toneChars   = {"ά","έ","ί","ή","ύ","ό","ώ", "Ά","Έ","Ί","Ή","Ύ","Ό","Ώ"};
        String[] noToneChars = {"α","ε","ι","η","υ","ο","ω", "Α","Ε","Ι","Η","Υ","Ο","Ω"};
        
        for (int i = 0; i < toneChars.length; i++) {
            newWord = newWord.replaceAll(toneChars[i], noToneChars[i]);
        }
        
        //System.out.println(newWord);
        
        return newWord;
    }
    
    private boolean isGreek(String word) {
        Character gtc = word.charAt(0);
        if (greekToneChars.containsKey(gtc)) {
            gtc = greekToneChars.get(gtc);
        }
        int asc = (int)gtc;
        
        //System.out.println(asc + " " + word.charAt(0));
        if ((asc >= 945 && asc < 970) || (asc >= 913 && asc < 938) && asc != 930) {
            return true;
        }
        
        return false;
    }
}
