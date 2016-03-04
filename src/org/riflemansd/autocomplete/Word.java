package org.riflemansd.autocomplete;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sotir
 */
public class Word {
    private int ascPosFix; // Μας χρειάζεται για να μετατρέπουμε τον κώδικα asci του γράμματος στη κλήμακα της λίστας μας, το οποίο είναι διαφορετικό για κάθε γλώσσα
    private String word;
    public ArrayList<Integer>[] lettersPos;

    /**
     * Για ελληνικές λέξεις μόνο
     * @param word 
     */
    public Word(String word) {
        this.word = word;
        this.ascPosFix = 945;
        this.lettersPos = new ArrayList[25];
        
        for (int i = 0; i < 25; i++) {
            this.lettersPos[i] = new ArrayList<>();
        }
    }
    
    /**
     * Προς το παρόν, υποστηρίζονται μόνο δύο γλώσσες
     * @param word
     * @param language EN for englihs, GR for greek
     */
    public Word(String word, String language) {
        int numberOfLetters = 25;
        this.ascPosFix = 945;
        
        if (language.equals("EN")) {
            numberOfLetters = 26;
            this.ascPosFix = 97;
        }
        else if (language.equals("GR")) {
            numberOfLetters = 25;
            this.ascPosFix = 945;
        }
        
        this.word = word;
        
        this.lettersPos = new ArrayList[numberOfLetters];
        
        for (int i = 0; i < numberOfLetters; i++) {
            this.lettersPos[i] = new ArrayList<>();
        }
    }
    //Σωτήρης - Svt;hrhw | Φαψεβοοκ - Facebook

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getAscPosFix() {
        return ascPosFix;
    }
    
}
