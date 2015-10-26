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
interface AutoCompleteEvent {
    void updateWord(String word);
    void onAutoComplete();
}
