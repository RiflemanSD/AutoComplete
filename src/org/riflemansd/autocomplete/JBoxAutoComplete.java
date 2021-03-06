package org.riflemansd.autocomplete;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Font;
import java.awt.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 *
 * @author RiflemanSD
 */
public class JBoxAutoComplete extends JComboBox implements KeyListener {

    private int caretPos;
    private String[] words;

    public JBoxAutoComplete(String... words) {
        this.words = words;
        
        this.getEditor().getEditorComponent().addKeyListener(this);
        //this.setFont(new Font("Serif", Font.BOLD, 20));
        
        this.setEditable(true);
        this.setMaximumRowCount(4);
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
            String sel = (String) this.getSelectedItem();
            this.getEditor().setItem(sel);
        }
        //System.out.println("t " + (String)this.getEditor().getItem());
        //typed = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (!(k == 8) && !Character.isDigit(e.getKeyChar()) && !Character.isLetter(e.getKeyChar())) return;
        String w;
        w = "";

        w = (String)this.getEditor().getItem();
        this.removeAllItems();

        String nt = "";
        if (w.length() == 0) { //|| Character.isSpaceChar(w.charAt(0))
            return;
        }

        for (String word : words) {
            if (word.contains(w)) {
                nt += word + "\n";
                this.addItem(word);
            }
        }
        
        this.getEditor().setItem(w);
        JTextField textField = (JTextField)editor.getEditorComponent();
        if (k == 8) textField.setCaretPosition(caretPos);
        else textField.setCaretPosition(caretPos+1);
        
        this.setPopupVisible(false);
        if (! (nt.length() == 0)) this.setPopupVisible(true);
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }
    
    /*private boolean ctrl;
    private boolean ctrla;
    private boolean backspace;
    private String[] words;
    

    public JBoxAutoComplete(String[] words) {
        this.words = words;
        
        this.getEditor().getEditorComponent().addKeyListener(this);
        //this.setFont(new Font("Serif", Font.BOLD, 20));
        
        this.setEditable(true);
        this.setMaximumRowCount(4);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        //if (e.isActionKey()) return;
        if (ctrl) return;
        String w,wu;
        w = wu = "";
        //String w = this.jTextField1.getText();
        if (!ctrla) {
            wu = (String)this.getEditor().getItem();
        } else {ctrla = false;}
        
        System.out.println(w);
        //this.jComboBox1.getEditor().setItem(w);
        this.removeAllItems();
        char c = e.getKeyChar();
        if (!backspace) w += wu + c;
        else w = wu.replace(""+c, "");
        
        //System.out.println(w);
        //System.out.println(e.getKeyChar() + " " + e.getKeyCode());
        String nt = "";
        if (!w.equals("")) {
            for (String word : words) {
                if (word.contains(w)) {
                    nt += word + "\n";
                    this.addItem(word);
                }
            }
        }
        
        this.getEditor().setItem(wu);
        this.setPopupVisible(false);
        if (! (nt.length() == 0)) this.setPopupVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        //System.out.println(k);
        //backspace
        if (k == 8) {
            backspace = true;
        }
        else {
            backspace = false;
        }
        //ctrl
        if (k == 17) {
            ctrl = true;
        }
        else if (k == 65 && ctrl) {
            ctrla = true;
        }
        //enter
        if (k == 10) {
            String sel = (String) this.getSelectedItem();
            this.getEditor().setItem(sel);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == 17) {
            ctrl = false;
        }
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }
    
    */
}
