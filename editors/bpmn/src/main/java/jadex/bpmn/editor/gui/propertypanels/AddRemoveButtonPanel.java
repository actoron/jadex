package jadex.bpmn.editor.gui.propertypanels;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jadex.bpmn.editor.gui.ImageProvider;

/**
 *  Generic class representing a button panel for adding and removing items.
 *
 */
public class AddRemoveButtonPanel extends JPanel
{
	/** Icon size for this panel's buttons. */
	protected static final int ICON_SIZE = 32;
	
	/** Icon color for this panel's buttons. */
	protected static final Color ICON_COLOR = new Color(126, 229, 80);
	
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
		int iconsize = getIconSize();
		Icon[] icons = imgprovider.generateGenericFlatImageIconSet(iconsize, ImageProvider.EMPTY_FRAME_TYPE, "add_+", getIconColor());
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
		icons = imgprovider.generateGenericFlatImageIconSet(iconsize, ImageProvider.EMPTY_FRAME_TYPE, "remove_-", getIconColor());
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
	
	/**
	 *  Returns the icon size used in this panel.
	 *  
	 *  @return The icon size.
	 */
	public int getIconSize()
	{
		return ICON_SIZE;
	}
	
	/**
	 *  Returns the icon color used in this panel.
	 *  
	 *  @return The icon color.
	 */
	public Color getIconColor()
	{
		return ICON_COLOR;
	}
}
