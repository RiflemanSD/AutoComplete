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
import javax.swing.JTextField;
import org.riflemansd.customcombobox.CustomComboBox;
import org.riflemansd.utils.TimeCalc;

/**
 * JComboBoxAutoComplete with prompt text
 * 
 * @author RiflemanSD
 */
public class JBoxAutoCompletePrompt extends CustomComboBox implements KeyListener,FocusListener {
    private ArrayList<AutoCompleteEvent> listeners;
    
    private int caretPos;
    private String[] words;
    private String promptText;
    boolean isPrompt;

    public JBoxAutoCompletePrompt(String... words) {
        init(null, 4, words);
        initPromptText("");
    }
    public JBoxAutoCompletePrompt(String prompt, boolean p, String... words) {
        init(null, 4, words);
        initPromptText(prompt);
    }
    public JBoxAutoCompletePrompt(String prompt, Font font, int maxRowCount, String... words) {
        init(font, maxRowCount, words);
        initPromptText(prompt);
    }
    
    private void init(Font font, int maxRowCount, String... words) {
        this.listeners = new ArrayList<AutoCompleteEvent>();
        this.words = words;
        
        this.getEditor().getEditorComponent().addKeyListener(this);
        if (font != null) this.setFont(font);
        
        this.setEditable(true);
        this.setMaximumRowCount(maxRowCount);
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
        for (String word : words) {
            //Lowercase all letters, replace all tone characters (for greek characters) 
            //and find the first string from editor that contains into the current word
            int index = greekToneCase(word.toLowerCase()).indexOf(greekToneCase(w.toLowerCase()));
            if (index != -1) {
                nt += word + "\n";
                //System.out.println(this.highlihgt(word, w));
                //System.out.println(word + ", " + w);
                
                //Highlight the words
                String[] ws = {word, this.highlight(index, word, w, "red")};
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
        return words;
    }
    public void setWords(String[] words) {
        this.words = words;
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
    
    public String highlight(int index, String w, String subw, String color) {
        String newWord = "";
        
        if (color == null) {
            newWord = "<html>" + w.replace(subw, "<b>" + subw + "</b>") + "</html>";
        }
        else {
            //newWord = "<html>" + w.replace(subw, "<font color='" + color + "'>" + subw + "</font>") + "</html>";
            String hlfs = "<font color='" + color + "'>";
            String hlfe = "</font>";
            
            String sb = w;
            //index = w.indexOf(subw);
            while (index >= 0) {
                sb = new StringBuilder(sb).insert(index+subw.length(), hlfe).insert(index, hlfs).toString();
                //System.out.println(index);
                index = greekToneCase(sb.toLowerCase()).indexOf(greekToneCase(subw.toLowerCase()), index + subw.length() + hlfe.length() + hlfs.length());
                //System.out.println("i = " + index);
            }
            //System.out.println(sb);
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
    
}
