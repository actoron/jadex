package jadex.base.gui.componenttree;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Panel for showing service properties.
 */
public class NFRPropertyProperties extends PropertiesPanel
{
	protected JButton bufetch;
	protected JComboBox counits;
	protected JTextField tfval;
	protected IExternalAccess ea;
	protected INFPropertyMetaInfo propmi;
	protected MethodInfo mi;
	protected RequiredServiceInfo rinfo;
	
	//-------- constructors --------
	
	/**
	 *  Create new service properties panel.
	 */
	public NFRPropertyProperties()
	{
		super(" Non-functional Criterion Properties ");
	}
	
	//-------- methods --------
	
	/**
	 *  Set the nf prop.
	 */
	public void	setProperty(final INFPropertyMetaInfo propmi, final IExternalAccess ea, 
		final MethodInfo mi, final RequiredServiceInfo rinfo)
	{
		this.ea = ea;
		this.propmi = propmi;
		this.mi = mi;
		this.rinfo = rinfo;
		
		final JComboBox serbox = new JComboBox();
		final DefaultComboBoxModel serboxm = (DefaultComboBoxModel)serbox.getModel();
		JButton ref = new JButton("Refresh");
		JPanel pan = new JPanel(new GridBagLayout());
		pan.add(serbox, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.EAST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		pan.add(ref, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
		addComponent("Service", pan);
		createTextField("Name");
		createTextField("Type");
		createTextField("Unit");
		createTextField("Target");
		
		AbstractAction aa = new AbstractAction("Refresh")
		{
			public void actionPerformed(ActionEvent e)
			{
				final boolean fmultiple = rinfo.isMultiple();
				final String fname = rinfo.getName();
				ea.scheduleStep(new IComponentStep<Object>()
				{
					public IFuture<Object> execute(IInternalAccess ia)
					{
						Object res = fmultiple? ia.getComponentFeature(IRequiredServicesFeature.class).getLastRequiredServices(fname): ia.getComponentFeature(IRequiredServicesFeature.class).getLastRequiredService(fname);
						return new Future<Object>(res);
					}
				}).addResultListener(new SwingResultListener<Object>(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						if(result instanceof IService)
						{
							IService ser = (IService)result;
							serboxm.removeAllElements();
							serboxm.addElement(ser.getServiceIdentifier());
						}
						else if(result instanceof Collection)
						{
							Collection<IService> sers = (Collection<IService>)result;
							serboxm.removeAllElements();
							for(IService ser: sers)
							{
								serboxm.addElement(ser.getServiceIdentifier());
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("ex: "+exception);
					}
				}));
			}
		};
		ref.setAction(aa);
		aa.actionPerformed(null);
		
		
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
					final MethodInfo fmi = mi;
					final String fname = propmi.getName();
					final IServiceIdentifier sid = (IServiceIdentifier)serbox.getSelectedItem();
					if(sid!=null)
					{
						ea.scheduleStep(new IComponentStep<Object>()
						{
							public IFuture<Object> execute(IInternalAccess ia)
							{
								Future<Object> ret = new Future<Object>();
								INFMixedPropertyProvider pp = ia.getComponentFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(sid);
								if(fmi!=null)
								{
									pp.getMethodNFPropertyValue(fmi, fname, u).addResultListener(new DelegationResultListener<Object>(ret));
								}
								else
								{
									pp.getNFPropertyValue(fname, u).addResultListener(new DelegationResultListener<Object>(ret));
								}
								return ret;
							}
						}).addResultListener(new SwingResultListener<Object>(lis));
					}
				}
			}
		});
		
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
