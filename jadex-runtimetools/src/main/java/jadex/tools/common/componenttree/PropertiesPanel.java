package jadex.tools.common.componenttree;

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
		gbc.anchor	= GridBagConstraints.NORTHWEST;
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
	 *  Get a check box.
	 */
	public JCheckBox	getCheckBox(String name)
	{
		return (JCheckBox)components.get(name);
	}
	
	/**
	 *  Create a text field and add it to the panel.
	 */
	protected void	createTextField(String name)
	{
		JTextField	tf	= new JTextField();
		tf.setEditable(false);
		components.put(name, tf);

		remove(dummy);
		
		gbc.weightx	= 0;
		add(new JLabel(name), gbc);
		gbc.weightx	= 1;
		add(tf, gbc);
		gbc.gridy++;
		
		gbc.weighty	= 1;
		add(dummy, gbc);
		gbc.weighty	= 0;

	}
	
	/**
	 *  Create a check box and add it to the panel.
	 */
	protected void	createCheckBox(String name)
	{
		JCheckBox	cb	= new JCheckBox("");
		cb.setMargin(new Insets(0,0,0,0));
		cb.setEnabled(false);
		components.put(name, cb);

		remove(dummy);

		gbc.weightx	= 0;
		add(new JLabel(name), gbc);
		gbc.weightx	= 1;
		add(cb, gbc);
		gbc.gridy++;
		
		gbc.weighty	= 1;
		add(dummy, gbc);
		gbc.weighty	= 0;
	}
}
