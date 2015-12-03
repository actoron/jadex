package jadex.base.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.gui.SGUI;

/**
 *  Dialog for component identifier.
 */
public class ComponentIdentifierDialog
{
	//-------- attributes --------

	/** The parent component. */
	protected Component	parent;
	
	/** The service provider. */
	protected IExternalAccess access;
	
	/** The dialog (created lazily). */
	protected JDialog	dia;
	
	/** Was the dialog aborted? */
	protected boolean	aborted;
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------

	/**
	 *  Create a new dialog.
	 */
	public ComponentIdentifierDialog(Component parent, IExternalAccess access)
	{
		this.parent	= parent;
		this.access = access;
	}

	//-------- methods --------
	
	/**
	 *  Open a modal dialog to select/enter an agent identifier.
	 *  @return	The selected agent identifier or null, when dialog was aborted.
	 */
	public IComponentIdentifier getComponentIdentifier(final IComponentIdentifier def)
	{
		// Create dialog.
		this.dia = createDialog(def, access);

		aborted	= false;
		dia.setVisible(true);

		return !aborted? cid: null;
	}
	
	/**
	 *  Create the dialog.
	 */
	public JDialog createDialog(IComponentIdentifier def, IExternalAccess access)
	{
		final ComponentIdentifierPanel pip = new ComponentIdentifierPanel(null, access);
		
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		Dimension md = cancel.getMinimumSize();
		Dimension pd = cancel.getPreferredSize();
		ok.setMinimumSize(md);
		ok.setPreferredSize(pd);
		
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				cid = pip.cid;
				dia.dispose();
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				aborted	= true; 
				dia.dispose();
			}
		});

		parent = SGUI.getWindowParent(parent);
		final JDialog	dia	= parent instanceof Frame
			? new JDialog((Frame)parent, "Enter Component Identifier", true)
			: new JDialog((Dialog)parent, "Enter Component Identifier", true);

		// Set aborted to [true], when dialog was aborted.
		dia.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				aborted	= true;
			}
		});
		
		dia.getContentPane().setLayout(new GridBagLayout());
		dia.getContentPane().add(pip, new GridBagConstraints(0,0, GridBagConstraints.REMAINDER,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		dia.getContentPane().add(new JLabel(),	new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		dia.getContentPane().add(ok, new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,4,4,2),0,0));
		dia.getContentPane().add(cancel,new GridBagConstraints(2,1, 1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,2,4,2),0,0));
//		dia.getContentPane().add(help, new GridBagConstraints(3,1, GridBagConstraints.REMAINDER,1,	0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,2,4,4),0,0));
		
		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition((Window)parent, dia));
		
		return dia;
	}
}
