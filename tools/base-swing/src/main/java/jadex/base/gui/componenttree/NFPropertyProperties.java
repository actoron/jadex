package jadex.base.gui.componenttree;

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

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Panel for showing service properties.
 */
public class NFPropertyProperties extends PropertiesPanel
{
	protected JButton bufetch;
	protected JComboBox counits;
	protected JTextField tfval;
	protected IExternalAccess ea;
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
	}
	
	//-------- methods --------
	
	/**
	 *  Set the nf prop.
	 */
	public void	setProperty(final INFPropertyMetaInfo propmi, final IExternalAccess ea, 
		final IServiceIdentifier sid, final MethodInfo mi)
	{
		this.ea = ea;
		this.propmi = propmi;
		this.sid = sid;
		this.mi = mi;
		
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
				if(ea!=null && propmi!=null)
				{
					final IResultListener<Object> lis = new SwingResultListener<Object>(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							tfval.setText(result==null? "n/a": result.toString());
						}
						
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
						}
					});
					
					final Object u = counits.getSelectedItem();
					
					if(sid!=null)
					{
						IFuture<IService> fut = ea.searchService( new ServiceQuery<>( (Class<IService>)null).setServiceIdentifier(sid));
						fut.addResultListener(new SwingResultListener<IService>(new IResultListener<IService>()
						{
							public void resultAvailable(IService ser) 
							{
								if(mi!=null)
								{
//									((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).getMethodNFPropertyValue(mi, propmi.getName(), u).addResultListener(lis);
									ea.getMethodNFPropertyValue(ser.getServiceId(), mi, propmi.getName()).addResultListener(lis);
								}
								else
								{
//									((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).getNFPropertyValue(propmi.getName(), u).addResultListener(lis);
									ea.getNFPropertyValue(ser.getServiceId(), propmi.getName()).addResultListener(lis);
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
							}
						}));
					}
					else
					{
//						IFuture<Object> fut = ((INFPropertyProvider)ea.getExternalComponentFeature(INFPropertyComponentFeature.class)).getNFPropertyValue(propmi.getName(), u);
//						fut.addResultListener(lis);
						ea.getNFPropertyValue(propmi.getName(), u).addResultListener(lis);
					}
				}
			}
		});
		
		getTextField("Name").setText(propmi.getName());
		getTextField("Type").setText(propmi.getType().getClassNameOnly());
		if(propmi.getUnit()!=null)
		{
			getTextField("Unit").setText(propmi.getUnit().getClassNameOnly());
			Class<?> ucl = propmi.getUnit().getType(this.getClass().getClassLoader()); // hmm, which classloader?!
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
