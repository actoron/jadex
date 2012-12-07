package jadex.tools.security;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.ComponentSelectorDialog;
import jadex.base.gui.PlatformSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.bridge.service.types.security.ParameterInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringObjectConverter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * 
 */
public class AcquireCertificatePanel extends JPanel
{
	protected IExternalAccess ea;
	
	protected ISecurityService secser;
	
	protected List<MechanismInfo> mechanisms;
	
	protected JComboBox cbmechs;
	
	/**
	 * 
	 */
	public AcquireCertificatePanel(IExternalAccess ea, ISecurityService secser, final CMSUpdateHandler cmshandler)
	{
		this.ea = ea;
		this.secser = secser;
		
		final ObjectCardLayout ocl = new ObjectCardLayout();
		final JPanel pdetail = new JPanel(ocl);

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
					final Class<?> cl = mi.getClazz();
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
					ocl.show(cl);
				}
			}
		});
				
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
			"Certificate Acquisition Mechanism"));
		this.setLayout(new BorderLayout(4,4));
		this.add(cbmechs, BorderLayout.NORTH);
		this.add(pdetail, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	public void setMechanisms(List<MechanismInfo> mechanisms)
	{
		ItemListener[] lis = cbmechs.getItemListeners();
		for(int i=0; i<lis.length; i++)
		{
			cbmechs.removeItemListener(lis[i]);
		}
		
		this.mechanisms = mechanisms;
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
	 * 
	 */
	public void setSelectedMechanism(int sel)
	{
		if(mechanisms!=null)
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
	}
	
	/**
	 * 
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
	}
	
	/**
	 * 
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
	}
	
	/**
	 * 
	 */
	protected void createCidChooser(PropertiesPanel pp, final ParameterInfo pi, final MechanismInfo mi, final CMSUpdateHandler cmshandler)
	{
		final JTextField tf = new JTextField();
		tf.setToolTipText(pi.getDescription());
		final JButton bu = new JButton("...");
		bu.setMargin(new Insets(0,0,0,0));
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(tf, BorderLayout.CENTER);
		p.add(bu, BorderLayout.EAST);
		
		pp.addComponent(pi.getName(), p);

		final PlatformSelectorDialog csd = new PlatformSelectorDialog(SGUI.getWindowParent(AcquireCertificatePanel.this), ea, cmshandler, new ComponentIconCache(ea));
		
		bu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentIdentifier cid = csd.selectAgent(null);
				if(cid!=null)
				{
					tf.setText(cid.getName());
					AcquireCertificatePanel.this.secser.setAcquisitionMechanismParameterValue(mi.getClazz(), pi.getName(), cid);
				}
			}
		});
		addTextFieldListener(new Runnable()
		{
			public void run()
			{
				String name = tf.getText();
				IComponentIdentifier cid = name.length()>0? new ComponentIdentifier(name): null; 
				AcquireCertificatePanel.this.secser.setAcquisitionMechanismParameterValue(mi.getClazz(), pi.getName(), cid);
			}
		}, tf);
	}
	
	/**
	 * 
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
