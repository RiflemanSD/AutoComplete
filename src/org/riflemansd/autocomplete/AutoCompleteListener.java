/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.riflemansd.autocomplete;

/**
 *
 * @author sotir
 */
public class AutoCompleteListener implements AutoCompleteEvent {
    private String word;
    
    @Override
    public void updateWord(String word) {
        this.word = word;
    }

    @Override
    public void onAutoComplete() {
        System.out.println(word);
    }

    public String getWord() {
        return word;
    }
    
}
