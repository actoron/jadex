package jadex.base.gui.componenttree;

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.future.SwingResultListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *  Panel for showing service properties.
 */
public class NFPropertyProperties extends PropertiesPanel
{
	protected JButton bufetch;
	protected JComboBox counits;
	protected JTextField tfval;
	protected IExternalAccess provider;
	protected INFPropertyMetaInfo propmi;
	protected IServiceIdentifier sid;
	protected MethodInfo mi;
	
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
		counits = new JComboBox(new DefaultComboBoxModel());
		tfval = new JTextField();
		tfval.setEditable(false);
		p.add(tfval, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		p.add(counits, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		p.add(bufetch, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		addComponent("Value", p);
		
		bufetch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(provider!=null && propmi!=null)
				{
					final IResultListener<Object> lis = new SwingResultListener<Object>(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							tfval.setText(result==null? "n/a": result.toString());
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					});
					
					final Object u = counits.getSelectedItem();
					
					if(sid!=null)
					{
						IFuture<IService> fut = SServiceProvider.getService(provider.getServiceProvider(), sid);
						fut.addResultListener(new SwingResultListener<IService>(new IResultListener<IService>()
						{
							public void resultAvailable(IService ser) 
							{
								if(mi!=null)
								{
									ser.getMethodNFPropertyValue(mi, propmi.getName(), u).addResultListener(lis);
								}
								else
								{
									ser.getNFPropertyValue(propmi.getName(), u).addResultListener(lis);
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
							}
						}));
					}
					else
					{
						IFuture<Object> fut = provider.getNFPropertyValue(propmi.getName(), u);
						fut.addResultListener(lis);
					}
				}
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Set the nf prop.
	 */
	public void	setProperty(final INFPropertyMetaInfo propmi, IExternalAccess provider, 
		IServiceIdentifier sid, MethodInfo mi)
	{
		this.provider = provider;
		this.propmi = propmi;
		this.sid = sid;
		this.mi = mi;
			
		getTextField("Name").setText(propmi.getName());
		getTextField("Type").setText(SReflect.getUnqualifiedTypeName(propmi.getType().getName()));
		if(propmi.getUnit()!=null)
		{
			getTextField("Unit").setText(SReflect.getUnqualifiedTypeName(propmi.getUnit().getName()));
			Class<?> ucl = propmi.getUnit();
			if(Enum.class.isAssignableFrom(ucl))
			{
				Object[] vals = ucl.getEnumConstants();
				DefaultComboBoxModel com = (DefaultComboBoxModel)counits.getModel();
				com.removeAllElements();
				if(vals!=null)
				{
					for(Object v: vals)
					{
						com.addElement(v);
					}
				}
			}
		}
		getTextField("Target").setText(propmi.getTarget().toString());
	}
}
