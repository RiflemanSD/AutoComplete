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
    public void onAutoComplete(String word) {
        this.word = word;
        
        System.out.println(word);
    }
}
