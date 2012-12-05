package jadex.tools.security;

import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.bridge.service.types.security.ParameterInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringObjectConverter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * 
 */
public class AcquireCertificatePanel extends JPanel
{
	protected ISecurityService secser;
	
	protected List<MechanismInfo> mechanisms;
	
	protected JComboBox cbmechs;
	
	/**
	 * 
	 */
	public AcquireCertificatePanel(ISecurityService secser, List<MechanismInfo> mechanisms, int sel)
	{
		this.secser = secser;
		this.mechanisms = mechanisms;
		
		final ObjectCardLayout ocl = new ObjectCardLayout();
		final JPanel pdetail = new JPanel(ocl);

		cbmechs = mechanisms==null? new JComboBox(): new JComboBox(mechanisms.toArray());
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
							final JTextField tf = pp.createTextField(pi.getName(), pi.getValue()==null? "": ""+pi.getValue(), true);
							final Runnable act = new Runnable()
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
							tf.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									act.run();
								}
							});
							tf.addFocusListener(new FocusListener()
							{
								public void focusLost(FocusEvent e)
								{
									act.run();
								}
	
								public void focusGained(FocusEvent e)
								{
								}
							});
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
		this.mechanisms = mechanisms;
		cbmechs.removeAllItems();
		for(MechanismInfo mi: mechanisms)
		{
			cbmechs.addItem(mi);
		}
		cbmechs.addItem("None");
	}
}
