package jadex.base.gui.componenttree;

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.future.SwingResultListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *  Panel for showing service properties.
 */
public class NFPropertyProperties extends PropertiesPanel
{
	protected JButton bufetch;
	protected JTextField tfval;
	protected IExternalAccess provider;
	protected INFPropertyMetaInfo propmi;
	
	//-------- constructors --------
	
	/**
	 *  Create new service properties panel.
	 */
	public NFPropertyProperties()
	{
		super(" Non-functional Criterion Properties ");

		createTextField("Name");
		createTextField("Type");
		createTextField("Unit");
		createTextField("Target");
		
		JPanel p = new JPanel(new GridBagLayout());
		bufetch = new JButton("Fetch");
		tfval = new JTextField();
		tfval.setEditable(false);
		p.add(tfval, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		p.add(bufetch, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		addComponent("Value", p);
		
		bufetch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(provider!=null && propmi!=null)
				{
					IFuture<Object> fut = provider.getNFPropertyValue(propmi.getName());
					fut.addResultListener(new SwingResultListener<Object>(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							tfval.setText(result==null? "n/a": result.toString());
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					}));
				}
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Set the nf prop.
	 */
	public void	setProperty(final INFPropertyMetaInfo propmi, IExternalAccess provider)
	{
		this.provider = provider;
		this.propmi = propmi;
			
		getTextField("Name").setText(propmi.getName());
		getTextField("Type").setText(propmi.getType().getName());
		if(propmi.getUnit()!=null)
			getTextField("Unit").setText(propmi.getUnit().toString());
		getTextField("Target").setText(propmi.getTarget().toString());
	}
}
