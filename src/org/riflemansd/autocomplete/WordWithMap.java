package org.riflemansd.autocomplete;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sotir
 */
public class WordWithMap {
    private String word;
    private HashMap<Character, ArrayList<Integer>> lettersPos;

    /**
     * @param word 
     */
    public WordWithMap(String word) {
        this.word = word;
        this.lettersPos = new HashMap<>();
    }
    //Σωτήρης - Svt;hrhw | Φαψεβοοκ - Facebook

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
    
    public void addLetter(char c, int pos) {
        if (this.lettersPos.containsKey(c)) {
            this.lettersPos.get(c).add(pos);
        }
        else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(pos);
            this.lettersPos.put(c, list);
        }
    }
    
    public ArrayList<Integer> getLetterList(char c) {
        return this.lettersPos.get(c);
    }
    
}

