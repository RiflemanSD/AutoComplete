package org.riflemansd.customcombobox;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Customer renderer for JComboBox
 * @author www.codejava.net
 *
 */
public class CustomItemRenderer extends JPanel implements ListCellRenderer {
	private JLabel labelItem = new JLabel();
	
	public CustomItemRenderer() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(2, 2, 2, 2);
		
		labelItem.setOpaque(true);
		labelItem.setHorizontalAlignment(JLabel.LEFT);
		
		add(labelItem, constraints);
		setBackground(Color.white);
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String[] values = (String[]) value;
                String itemValue = values[1];

		// set country name
		labelItem.setText(itemValue);
		
                //new JLabel("<html>Text color: <font color='red'>red</font></html>")
		
		if (isSelected) {
			labelItem.setBackground(Color.DARK_GRAY);
			labelItem.setForeground(Color.white);
                        
		} else {
			labelItem.setForeground(Color.BLACK);
			labelItem.setBackground(Color.white);
		}
		
		return this;
	}

}