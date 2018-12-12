package jadex.commons.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *  Panel for showing properties.
 *  Provides reusable code for grid bag layout.
 */
public class PropertiesPanel	extends	JPanel
{
	//-------- attributes --------
	
	/** The grid bag constraints. */
	protected GridBagConstraints	gbc;
	
	/** The last component for extra space. */
	protected JComponent dummy;
	
	/** The created components (name->comp). */
	protected Map	components;

	/** Add dummy when weighty==0. */
	protected boolean adddummy;
	
	//-------- constructors --------

	/**
	 *  Create new properties panel.
	 */
	public PropertiesPanel()
	{
		this(null);
	}

	
	/**
	 *  Create new properties panel.
	 */
	public PropertiesPanel(String title)
	{
		super(new GridBagLayout());
		if(title!=null)
			setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title));
		this.components	= new HashMap();

		this.gbc	= new GridBagConstraints();
		gbc.gridy	= 0;
		gbc.anchor	= GridBagConstraints.PAGE_START;
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.insets	= new Insets(1,1,1,1);
		
		adddummy = true;
		dummy	= new JLabel();
		gbc.weighty	= 1;
		add(dummy, gbc);
		gbc.weighty	= 0;
	}
	
	//-------- methods --------
	
	/**
	 *  Get a text field.
	 */
	public JTextField	getTextField(String name)
	{
		return (JTextField)components.get(name);
	}
	
	/**
	 *  Get a component.
	 */
	public JComponent	getComponent(String name)
	{
		return (JComponent)components.get(name);
	}
	
	/**
	 *  Get a check box.
	 */
	public JCheckBox	getCheckBox(String name)
	{
		return (JCheckBox)components.get(name);
	}
	
	/**
	 *  Get a combo box.
	 */
	public JComboBox	getComboBox(String name)
	{
		return (JComboBox)components.get(name);
	}
	
	/**
	 *  Create a text field and add it to the panel.
	 */
	public JTextField createTextField(String name)
	{
		return createTextField(name, null);
	}
	
	/**
	 *  Create a text field and add it to the panel.
	 */
	public JTextField createTextField(String name, String defvalue)
	{
		return createTextField(name, defvalue, false, 0);
	}
	
	/**
	 *  Create a text field and add it to the panel.
	 */
	public JTextField createTextField(String name, String defvalue, boolean editable)
	{
		return createTextField(name, defvalue, editable, 0);
	}
	
	/**
	 *  Create a text field and add it to the panel.
	 */
	public JTextField createTextField(String name, String defvalue, boolean editable, double weighty)
	{
		return createTextField(name, defvalue, editable, weighty, null);
	}
	
	/**
	 *  Create a text field and add it to the panel.
	 */
	public JTextField createTextField(String name, String defvalue, boolean editable, double weighty, String tooltip)
	{
		JTextField	tf	= new JTextField(defvalue);
		tf.setEditable(editable);
		addComponent(name, tf, weighty, -1, -1, tooltip);
		return tf;
	}
	
	/**
	 *  Create a check box and add it to the panel.
	 */
	public JCheckBox createCheckBox(String name)
	{
		return createCheckBox(name, false, false, 0);
	}
	
	/**
	 *  Create a check box and add it to the panel.
	 */
	public JCheckBox createCheckBox(String name, boolean selected, boolean enabled)
	{
		return createCheckBox(name, selected, enabled, 0);
	}
	
	/**
	 *  Create a check box and add it to the panel.
	 */
	public JCheckBox createCheckBox(String name, boolean selected, boolean enabled, double weighty)
	{
		return createCheckBox(name, selected, enabled, weighty, null);
	}
	
	/**
	 *  Create a check box and add it to the panel.
	 */
	public JCheckBox createCheckBox(String name, boolean selected, boolean enabled, double weighty, String tooltip)
	{
		// Todo: checkbox name vs. checkbox label!?
		JCheckBox cb = new JCheckBox("", selected);
		cb.setMargin(new Insets(0,0,0,0));
		cb.setEnabled(enabled);
		addComponent(name, cb, weighty, -1, -1, tooltip);
		return cb;
	}
	
	/**
	 *  Create a combo box and add it to the panel.
	 */
	public JComboBox createComboBox(String name, Object[] values)
	{
		return createComboBox(name, values, false, 0);
	}
	
	/**
	 *  Create a combo box and add it to the panel.
	 */
	public JComboBox createComboBox(String name, Object[] values, boolean editable, double weighty)
	{
		JComboBox cb = values==null? new JComboBox(): new JComboBox(values);
		cb.setEditable(editable);
		addComponent(name, cb, weighty, -1);
		return cb;
	}
	
	/**
	 *  Create a button and add it to the panel.
	 */
	public JButton createButton(String name, String text)
	{
		return createButton(name, text, 0);
	}
	
	/**
	 *  Create a button and add it to the panel.
	 */
	public JButton createButton(String name, String text, double weighty)
	{
		JButton b = new JButton(text);
//		addComponent(name, b, weighty, GridBagConstraints.NONE);
		addComponent(name, b, weighty, GridBagConstraints.NONE, GridBagConstraints.WEST, null);
		return b;
	}
	
	/**
	 *  Create several buttons.
	 *  @param names The button names.
	 */
	public JButton[] createButtons(String groupname, String[] names, double weighty)
	{
		JButton[] ret = new JButton[names.length];
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.gridx = 0;
		con.gridy = 0;
		con.anchor = GridBagConstraints.EAST;
		con.fill = GridBagConstraints.NONE;
		con.insets = new Insets(1,1,1,1);
		con.weightx = 1;
		Dimension maxd = null;
		for(int i=0; i<names.length; i++)
		{
			ret[i] = new JButton(names[i]);
//			ret[i].setMargin(new Insets(0,0,0,0));
//			ret[i].setBorder(new EmptyBorder(new Insets(0,0,0,3)));
			p.add(ret[i], con);
			con.weightx = 0;
			con.gridx++;
			Dimension d = ret[i].getPreferredSize();
			if(maxd==null || d.width>maxd.width)
				maxd = d;
		}
		for(int i=0; i<ret.length; i++)
		{
			ret[i].setPreferredSize(maxd);
		}
		
		addFullLineComponent(groupname, p, weighty);
		
		return ret;
	}
	
	/**
	 *  Add a component that spans a full line.
	 *  No label is rendered.
	 */
	public void	addComponent(String name, JComponent comp)
	{
		addComponent(name, comp, 0, -1);
	}
	
	/**
	 *  Add a component
	 */
	public void	addComponent(String name, JComponent comp, double weighty)
	{
		addComponent(name, comp, weighty, -1);
	}
	
	/**
	 *  Add a component
	 */
	public void	addComponent(String name, JComponent comp, double weighty, int fill)
	{
		addComponent(name, comp, weighty, fill, -1, null);
	}
	
	/**
	 *  Add a component
	 */
	public void	addComponent(String name, JComponent comp, double weighty, int fill, int anchor, String tooltip)
	{
		components.put(name, comp);

		remove(dummy);

		gbc.weighty = weighty;
		
		
		gbc.weightx	= 0;
		gbc.gridwidth	= 1;
		gbc.anchor = anchor!=-1? anchor: GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.BOTH;
		JLabel lab = new JLabel(name);
		if(tooltip!=null)
			lab.setToolTipText(tooltip);
		add(lab, gbc);
		
		if(weighty==0)
		{
			gbc.fill = fill!=-1? fill: GridBagConstraints.HORIZONTAL;
		}
		else
		{
			adddummy = false;
		}
		
		gbc.weightx	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		if(tooltip!=null)
			comp.setToolTipText(tooltip);
		add(comp, gbc);
		gbc.gridy++;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		if(adddummy)
		{
			gbc.weighty	= 1;
			gbc.gridwidth	= GridBagConstraints.REMAINDER;
			add(dummy, gbc);
			gbc.gridwidth	= 1;
			gbc.weighty	= 0;
		}
	}
	
	/**
	 *  Add a component that spans a full line.
	 *  No label is rendered.
	 */
	public void	addFullLineComponent(String name, JComponent comp)
	{
		addFullLineComponent(name, comp, 0);
	}
	
	/**
	 *  Add a component that spans a full line.
	 *  No label is rendered.
	 */
	public void	addFullLineComponent(String name, JComponent comp, double weighty)
	{
		components.put(name, comp);

		remove(dummy);

		gbc.weighty = weighty;
		if(weighty>0)
		{
			gbc.fill = GridBagConstraints.BOTH;
			adddummy = false;
		}
		
		gbc.weightx	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		add(comp, gbc);
		gbc.gridy++;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		if(adddummy)
		{
			gbc.weighty	= 1;
			add(dummy, gbc);
			gbc.gridwidth	= 1;
			gbc.weighty	= 0;
		}
	}
}
