package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ImageProvider;

import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *  Generic class representing a button panel for adding and removing items.
 *
 */
public class AddRemoveButtonPanel extends JPanel
{
	/**
	 *  Creates a panel.
	 *  
	 *  @param imgprovider The image provider for the icons.
	 *  @param addaction The add action.
	 *  @param removeaction The remove action.
	 */
	public AddRemoveButtonPanel(ImageProvider imgprovider, Action addaction, Action removeaction)
	{
		setLayout(new GridLayout(2, 1));
		
		JButton addbutton = new JButton();
		int iconsize = 32;
		Icon[] icons = imgprovider.generateGenericFlatImageIconSet(iconsize, "add_+");
		addbutton.setAction(addaction);
		addbutton.setText(null);
		addbutton.setIcon(icons[0]);
		addbutton.setPressedIcon(icons[1]);
		addbutton.setRolloverIcon(icons[2]);
		addbutton.setContentAreaFilled(false);
		addbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		addbutton.setMargin(new Insets(0, 0, 0, 0));
		addbutton.setToolTipText((String) addaction.getValue(Action.NAME));
		
		JButton removebutton = new JButton();
		icons = imgprovider.generateGenericFlatImageIconSet(iconsize, "remove_-");
		removebutton.setAction(removeaction);
		removebutton.setText(null);
		removebutton.setIcon(icons[0]);
		removebutton.setPressedIcon(icons[1]);
		removebutton.setRolloverIcon(icons[2]);
		removebutton.setContentAreaFilled(false);
		removebutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		removebutton.setMargin(new Insets(0, 0, 0, 0));
		removebutton.setToolTipText((String) addaction.getValue(Action.NAME));
		
		add(addbutton);
		add(removebutton);
	}
}
