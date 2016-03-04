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
public class JBoxAutoCompletePromptFaster extends CustomComboBox implements KeyListener,FocusListener {
    private ArrayList<AutoCompleteEvent> listeners;
    
    private int caretPos;
    private Word[] words;
    private String promptText;
    private HashMap<Character, Character> greekToneChars;
    boolean isPrompt;

    public JBoxAutoCompletePromptFaster(String... words) {
        init(null, 4, words);
        initPromptText("");
    }
    public JBoxAutoCompletePromptFaster(String prompt, boolean p, String... words) {
        init(null, 4, words);
        initPromptText(prompt);
    }
    public JBoxAutoCompletePromptFaster(String prompt, Font font, int maxRowCount, String... words) {
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
        this.words = new Word[words.length];
        
        for (int i1 = 0; i1 < words.length; i1++) {
            if (isGreek(words[i1])) {
                this.words[i1] = new Word(words[i1], "GR");
            }
            else {
                this.words[i1] = new Word(words[i1], "EN");
            }
            
            Word w = this.words[i1];
            String word = w.getWord();
            String lword = "";
            
            //System.out.println(word);
            int i = 0;
            for (Character c : w.getWord().toCharArray()) {
                if (c != ' ') {
                    //System.out.println("C: " + (int)c + " " + c);
                    Character gtc;
                    if (greekToneChars.containsKey(c)) {
                        gtc = greekToneChars.get(c);
                    }
                    else {
                        gtc = c;
                    }
                    String temp = gtc.toString().toLowerCase();
                    gtc = temp.charAt(0);
                    //System.out.println(c + ", " + gtc + " -> " + i);
                    lword += gtc;

                    //System.out.println("GTC: " + (int)gtc + " " + ((int)gtc-945));
                    //System.out.println(w.getAscPosFix());
                    try {
                        w.lettersPos[((int)gtc-w.getAscPosFix())].add(i);
                    }
                    catch (Exception e) {
                        w.setWord("");
                    }
                }
                i++;
            }
            //System.out.println(lword);
        }
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
        for (Word word : this.words) {
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
    
    public ArrayList<Integer> getResults(String test, Word w) {
        //String test = "πα";
        System.out.println(test);
        System.out.println(w.getWord());
        
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
        
        
        HashMap<Character, Integer> testCharPos = new HashMap<>();
        
        for (Character c : testChars) {
            if (!testCharPos.containsKey(c)) {
                testCharPos.put(c, 0);
            }
        }
        
        boolean stop = false;
        Character prevC = null;
        int prev = -10;
        int now = -10;
        int value = -10;
        int l = 0;
        ArrayList<Integer> result = new ArrayList<>();
        
        if (testChars.length == 1) {
            ArrayList<Integer> list = w.lettersPos[((int)testChars[0]-w.getAscPosFix())];
            
            for (int pos : list) {
                result.add(pos);
            }
        }
        else {
            while (!stop && l != w.getWord().length()) {
                int z = 1;
                while (z < testChars.length) {
                    Character c1 = testChars[z-1];
                    Character c2 = testChars[z];
                    ArrayList<Integer> list = w.lettersPos[((int)c1-945)];
                    ArrayList<Integer> list2 = w.lettersPos[((int)c2-945)];

                    if (list.size() <= testCharPos.get(c1)) {
                        stop = true;
                        break;
                    }
                    if (list2.size() <= testCharPos.get(c2)) {
                        stop = true;
                        break;
                    }
                    now = list.get(testCharPos.get(c1));
                    prev = list2.get(testCharPos.get(c2));

                    //System.out.println("[v = " +value+ "] " + now + ", " + prev + "(" + prevC+")" + " [c: " + c + "] [z " + z + "] [l: " + l + "]");

                    if (prev != -10) {
                        value = prev - now;
                        //System.out.println(c1 + "(" + now + "), " + c2 + "(" + prev + ") " + value);
                        if (value == 1) {
                            testCharPos.put(c1, testCharPos.get(c1) + 1);
                            //testCharPos.put(c2, testCharPos.get(c2) + 1);
                            z++;
                            if (z == testChars.length) {
                                //System.out.println("l = " + (prev - testChars.length + 1));
                                result.add(prev - testChars.length + 1);
                            }
                        }
                        else if (value > 0) {
                            testCharPos.put(c1, testCharPos.get(c1) + 1);
                        }
                        else if (value <= 0) {
                            testCharPos.put(c2, testCharPos.get(c2) + 1);
                        }
                    }
                    else {
                        testCharPos.put(c1, testCharPos.get(c1) + 1);
                        z++;
                    }

                    //prev = now;
                    //System.out.println(prevC + " - prev = " + prev);
                    //prevC = c;
                    l++;
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
