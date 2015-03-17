/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author RiflemanSD
 */
public class Test extends JFrame {
    private String[] words = {"about", "abc", "Hello", "World", "war", "word", "lakis", "luck", "lakis2", "between", "wow", "warrior", "world", "wombocombo"};
    
    public Test() {
        this.setSize(250, 300);
        this.setLayout(new BorderLayout());
        
        JPanel p = new JPanel();
        p.setLayout(null);
        
        JBoxAutoComplete box = new JBoxAutoComplete(words);
        box.setBounds(50, 50, 120, 20);
        p.add(box);
        JComboBox box1 = new JComboBox();
        p.add(box1);
        
        this.add(p, BorderLayout.CENTER);
        this.setVisible(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) {
        Test l = new Test();
    }
}
