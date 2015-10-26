package org.riflemansd.customcombobox;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * A custom combo box with its own renderer and editor.
 * @author wwww.codejava.net
 *
 */
public class CustomComboBox extends JComboBox {
	private DefaultComboBoxModel model;
	
	public CustomComboBox() {
		model = new DefaultComboBoxModel();
		setModel(model);
		setRenderer(new CustomItemRenderer());
		setEditor(new CustomItemEditor());
	}
        
	/**
	 * Add an array items to this combo box.
	 * Each item is an array of two String elements:
	 * - first element is country name.
	 * - second element is path of an image file for country flag.
	 * @param items
	 */
	public void addItems(String[][] items) {
		for (String[] anItem : items) {
			model.addElement(anItem);
		}
	}
}