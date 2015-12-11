package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.PlatformSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.bridge.service.types.security.ParameterInfo;
import jadex.commons.ICommand;
import jadex.commons.SReflect;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;

/**
 *  Panel that displays the available mechanisms and
 *  allows for customizing their properties.
 */
public class AcquireCertificatePanel extends JPanel
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;
	
	/** The platform running the gui. */
	protected IExternalAccess jccaccess;
	
	/** The security service. */
	protected ISecurityService secser;
	
	/** The combobox with mechanisms. */
	protected JComboBox cbmechs;
	
	/** The layout. */
	protected ObjectCardLayout ocl;
	
	/** The mechanism panel. */
	protected JPanel pdetail;
	
	/** The cmshandler. */
	protected CMSUpdateHandler cmshandler;
	
	/** The update actions. */
	protected Map<String, ICommand<Object>> updateactions;

	//-------- constructors --------

	/**
	 *  Create the acquire certificate panel.
	 */
	public AcquireCertificatePanel(IExternalAccess ea, IExternalAccess jccaccess, ISecurityService secser, final CMSUpdateHandler cmshandler)
	{
		this.ea = ea;
		this.jccaccess = jccaccess;
		this.secser = secser;
		this.cmshandler = cmshandler;
		this.updateactions = new HashMap<String, ICommand<Object>>();
		
		this.ocl = new ObjectCardLayout();
		this.pdetail = new JPanel(ocl);

		cbmechs = new JComboBox();
		cbmechs.addItem("None");
		cbmechs.setRenderer(new BasicComboBoxRenderer()
		{
		    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
		    {
		    	String	val	= value instanceof MechanismInfo? SReflect.getInnerClassName(((MechanismInfo)value).getClazz()): (String)value;
		    	return super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
		    }
		});
		cbmechs.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{			
				Object o = cbmechs.getSelectedItem();
				final MechanismInfo mi = o instanceof MechanismInfo? (MechanismInfo)o: null;
				AcquireCertificatePanel.this.secser.setAcquisitionMechanism(mi!=null? mi.getClazz(): null);
				
				if(mi==null)
				{
					if(ocl.getComponent("none")==null)
					{
						JPanel p = new JPanel(new BorderLayout());
						p.add(new JLabel("Deactivated", JLabel.CENTER), BorderLayout.CENTER);
						pdetail.add(p, "none");
					}
					ocl.show("none");
				}
				else
				{
					createMechanismPanel(mi);
					ocl.show(mi.getClazz());
				}
			}
		});
				
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
			"Certificate Acquisition Mechanism"));
		this.setLayout(new BorderLayout(4,4));
		this.add(cbmechs, BorderLayout.NORTH);
		this.add(pdetail, BorderLayout.CENTER);
	}
	
	//-------- methods --------

	/**
	 *  Create the mechanism panel.
	 */
	protected PropertiesPanel createMechanismPanel(MechanismInfo mi)
	{
		Class<?> cl = mi.getClazz();
		if(ocl.getComponent(cl)==null)
		{
			PropertiesPanel pp = new PropertiesPanel();
			pp.addFullLineComponent("Settings", new JLabel("Settings"));
			List<ParameterInfo> pis = mi.getParameterInfos();
			for(final ParameterInfo pi: pis)
			{
				Class<?> tcl = pi.getType();
				if(boolean.class.equals(tcl) || Boolean.class.equals(tcl))
				{
					createCheckBox(pp, pi, mi);
				}
				else if(IComponentIdentifier.class.equals(tcl))
				{
					createCidChooser(pp, pi, mi, cmshandler);
				}
				else
				{
					createTextField(pp, pi, mi);
				}
			}
			pdetail.add(pp, cl);
		}
		return (PropertiesPanel)ocl.getComponent(cl);
	}
	
	/**
	 *  Set a parameter value.
	 *  Uses a previously created update action to actually perform
	 *  the update.
	 */
	public void setParameterValue(String mechname, String name, Object value)
	{
		ICommand<Object> update = updateactions.get(mechname+"."+name);
		update.execute(value);
	}
	
	/**
	 *  Set the acquisition mechanisms.
	 */
	public void setMechanisms(List<MechanismInfo> mechanisms)
	{
		ItemListener[] lis = cbmechs.getItemListeners();
		for(int i=0; i<lis.length; i++)
		{
			cbmechs.removeItemListener(lis[i]);
		}
		
		cbmechs.removeAllItems();
		for(MechanismInfo mi: mechanisms)
		{
			cbmechs.addItem(mi);
		}
		cbmechs.addItem("None");
		
		for(int i=0; i<lis.length; i++)
		{
			cbmechs.addItemListener(lis[i]);
		}
	}
	
	/**
	 *  Set (select) the acquisition mechanism.
	 */
	public void setSelectedMechanism(int sel)
	{
		if(sel==-1)
		{
			cbmechs.setSelectedIndex(cbmechs.getItemCount()-1);
		}
		else
		{
			cbmechs.setSelectedIndex(sel);
		}
	}
	
	/**
	 *  Create input text field.
	 */
	protected void createTextField(PropertiesPanel pp, final ParameterInfo pi, final MechanismInfo mi)
	{
		final JTextField tf = pp.createTextField(pi.getName(), pi.getValue()==null? "": ""+pi.getValue(), true);
		tf.setToolTipText(pi.getDescription());
		final Runnable action = new Runnable()
		{
			public void run()
			{
				Object val = tf.getText();
				Class<?> cl = pi.getType();
				if(!String.class.equals(cl))
				{
					IStringObjectConverter conv = BasicTypeConverter.getBasicStringConverter(cl);
					if(conv==null) // todo:
						return;
					
					try
					{
						val = conv.convertString((String)val, null);
					}
					catch(Exception ex)
					{
						throw new RuntimeException(ex);
					}
				}
				AcquireCertificatePanel.this.secser.setAcquisitionMechanismParameterValue(mi.getClazz(), pi.getName(), val);
			}
		};
		addTextFieldListener(action, tf);
		
		updateactions.put(mi.getClazz().getName()+"."+pi.getName(), new ICommand<Object>()
		{
			public void execute(Object val) 
			{
				Class<?> cl = pi.getType();
				if(!String.class.equals(cl))
				{
					IObjectStringConverter conv = BasicTypeConverter.getBasicObjectConverter(cl);
					if(conv!=null)
					{
						try
						{
							val = conv.convertObject(val, null);
						}
						catch(Exception ex)
						{
							throw new RuntimeException(ex);
						}
					}
				}
				tf.setText(""+val);
			}
		});
	}
	
	/**
	 *  Create checkbox for boolean choices.
	 */
	protected void createCheckBox(PropertiesPanel pp, final ParameterInfo pi, final MechanismInfo mi)
	{
		final JCheckBox cb = pp.createCheckBox(pi.getName(), ((Boolean)pi.getValue()).booleanValue(), true);
		cb.setToolTipText(pi.getDescription());
		cb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AcquireCertificatePanel.this.secser.setAcquisitionMechanismParameterValue(mi.getClazz(), pi.getName(), 
					cb.isSelected()? Boolean.TRUE: Boolean.FALSE);
			}
		});
		
		updateactions.put(mi.getClazz().getName()+"."+pi.getName(), new ICommand<Object>()
		{
			public void execute(Object val) 
			{
				cb.setSelected(((Boolean)val).booleanValue());
			}
		});
	}
	
	/**
	 *  Create chooser for cid.
	 */
	protected void createCidChooser(PropertiesPanel pp, final ParameterInfo pi, final MechanismInfo mi, final CMSUpdateHandler cmshandler)
	{
		final JTextField tf = new JTextField();
		tf.setToolTipText(pi.getDescription());
		final JButton bu = new JButton("...");
//		Insets in = bu.getInsets();
//		bu.setMargin(new Insets(0,in.left,0,in.right));
		
		if(pi.getValue()!=null)
			tf.setText(((IComponentIdentifier)pi.getValue()).getPlatformPrefix());
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(tf, BorderLayout.CENTER);
		p.add(bu, BorderLayout.EAST);
		
		pp.addComponent(pi.getName(), p);

		final PlatformSelectorDialog csd = new PlatformSelectorDialog(SGUI.getWindowParent(AcquireCertificatePanel.this), ea, jccaccess, cmshandler, null, new ComponentIconCache(ea));
		
		bu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentIdentifier cid = csd.selectAgent(null);
				if(cid!=null)
				{
//					tf.setText(cid.getName());
					AcquireCertificatePanel.this.secser.setAcquisitionMechanismParameterValue(mi.getClazz(), pi.getName(), cid);
				}
			}
		});
		addTextFieldListener(new Runnable()
		{
			public void run()
			{
				String name = tf.getText();
				IComponentIdentifier cid = name.length()>0? new BasicComponentIdentifier(name): null; 
				AcquireCertificatePanel.this.secser.setAcquisitionMechanismParameterValue(mi.getClazz(), pi.getName(), cid);
			}
		}, tf);
		
		updateactions.put(mi.getClazz().getName()+"."+pi.getName(), new ICommand<Object>()
		{
			public void execute(Object val) 
			{
				tf.setText(val==null? "": ((IComponentIdentifier)val).getName());
			}
		});
	}
	
	/**
	 *  Add listeners on the text field.
	 */
	protected void addTextFieldListener(final Runnable action, JTextField tf)
	{
		tf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				action.run();
			}
		});
		tf.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				action.run();
			}

			public void focusGained(FocusEvent e)
			{
			}
		});
	}
}
