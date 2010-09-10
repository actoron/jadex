package jadex.base.gui.componenttree;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
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
	protected JComponent	dummy;
	
	/** The created components (name->comp). */
	protected Map	components;
	
	//-------- constructors --------
	
	/**
	 *  Create new properties panel.
	 */
	public PropertiesPanel(String title)
	{
		super(new GridBagLayout());
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title));
		this.components	= new HashMap();

		this.gbc	= new GridBagConstraints();
		gbc.gridy	= 0;
		gbc.anchor	= GridBagConstraints.WEST;
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.insets	= new Insets(1,1,1,1);
		
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
	 *  Create a text field and add it to the panel.
	 */
	public void	createTextField(String name)
	{
		JTextField	tf	= new JTextField();
		tf.setEditable(false);
		addComponent(name, tf);
	}
	
	/**
	 *  Create a check box and add it to the panel.
	 */
	public void	createCheckBox(String name)
	{
		JCheckBox	cb	= new JCheckBox("");
		cb.setMargin(new Insets(0,0,0,0));
		cb.setEnabled(false);
		addComponent(name, cb);
	}
	
	/**
	 *  Add a component
	 */
	public void	addComponent(String name, JComponent comp)
	{
		components.put(name, comp);

		remove(dummy);

		gbc.weightx	= 0;
		gbc.gridwidth	= 1;
		add(new JLabel(name), gbc);
		gbc.weightx	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		add(comp, gbc);
		gbc.gridy++;
		
		gbc.weighty	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		add(dummy, gbc);
		gbc.gridwidth	= 1;
		gbc.weighty	= 0;
	}
	
	/**
	 *  Add a component that spans a full line.
	 *  No label is rendered.
	 */
	public void	addFullLineComponent(String name, JComponent comp)
	{
		components.put(name, comp);

		remove(dummy);

		gbc.weightx	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		add(comp, gbc);
		gbc.gridy++;
		
		gbc.weighty	= 1;
		add(dummy, gbc);
		gbc.gridwidth	= 1;
		gbc.weighty	= 0;
	}
}
