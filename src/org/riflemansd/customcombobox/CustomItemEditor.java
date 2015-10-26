package org.riflemansd.customcombobox;

import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * Editor for JComboBox
 * @author wwww.codejava.net
 *
 */
public class CustomItemEditor extends BasicComboBoxEditor {
	private String selectedValue;
        private JTextField field = new JTextField();
	
	public CustomItemEditor() {
            super();
            
            //this.editor.setfore
	}
	
	public Component getEditorComponent() {
                return this.field;
	}
	
	public Object getItem() {
            return this.field.getText();
	}
	
	public void setItem(Object item) {
		if (item == null) {
			return;
		}
                String[] countryItem;
                
                if (item instanceof String) {
                    countryItem = new String[2];
                    countryItem[0] = (String)item;
                    countryItem[1] = (String)item;
                }
                else {
                    countryItem = (String[]) item;
                }
		selectedValue = countryItem[0];
                field.setText(selectedValue);
	}
}