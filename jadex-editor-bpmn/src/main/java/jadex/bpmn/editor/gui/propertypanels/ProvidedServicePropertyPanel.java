package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MProperty;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.ClassInfoComboBoxRenderer;
import jadex.commons.gui.autocombo.ComboBoxEditor;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;
import jadex.javaparser.SJavaParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * 
 */
public class ProvidedServicePropertyPanel extends BasePropertyPanel
{
	/** The visual event */
	protected VActivity vact;
	
	/** The task combo box. */
	protected AutoCompleteCombo ibox;
	
	/** The method combo box. */
	protected JComboBox mbox;
	
	/**
	 *  Create a new panel.
	 *  @param container The model container.
	 *  @param vmsgevent The vactivity.
	 */
	public ProvidedServicePropertyPanel(ModelContainer container, VActivity vact)
	{
		super("Message Event", container);
		this.vact = vact;
		setLayout(new BorderLayout());
		
		add(createServicePanel(), BorderLayout.CENTER);
		
		refresh();
	}
	
	/**
	 * 
	 */
	protected JPanel createServicePanel()
	{
		PropertiesPanel pp = new PropertiesPanel();

		final ClassLoader cl = getModelContainer().getProjectClassLoader()!=null? getModelContainer().getProjectClassLoader()
			: ProvidedServicePropertyPanel.class.getClassLoader();
			
		// Hack, side effect :-(
		ibox = new AutoCompleteCombo(null, cl);
		final FixedClassInfoComboModel model = new FixedClassInfoComboModel(ibox, -1, new ArrayList<ClassInfo>(modelcontainer.getInterfaces()));
		ibox.setModel(model);
		ibox.setEditor(new ComboBoxEditor(model));
		ibox.setRenderer(new ClassInfoComboBoxRenderer());
		
		JPanel cboxpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		cboxpanel.add(ibox, gc);
		
		pp.addComponent("Service interface:", ibox);
		mbox = pp.createComboBox("Method name", null);
		mbox.setRenderer(new BasicComboBoxRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
			{
				Method method = (Method)value;
				String txt = null;
				if(method!=null)
					txt = SReflect.getMethodSignature(method);
				return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
			}
		});
		
		ibox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ClassInfo iface = (ClassInfo)ibox.getSelectedItem();
				if(iface==null)
					return;
				
				Class<?> ifacecl = iface.getType(cl);
					
				vact.getMActivity().setProperty("iface", iface==null? null: iface.toString()+".class", false);
				
				if(iface!=null)
				{
					ActionListener[] als = mbox.getActionListeners();
					for(ActionListener al: als)
						mbox.removeActionListener(al);
					
					DefaultComboBoxModel mo = ((DefaultComboBoxModel)mbox.getModel());
					mo.removeAllElements();
					mo.addElement(null);
					Method[] ms = ifacecl.getMethods();
					for(Method m: ms)
					{
						mo.addElement(m);
					}
					
					for(ActionListener al: als)
						mbox.addActionListener(al);
				}
			}
		});
		
		mbox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Method method = (Method)mbox.getSelectedItem();
				
				vact.getMActivity().setProperty("method", method==null? null: SReflect.getMethodSignature(method), true);
			}
		});
		
		return pp;
	}
	
//	/**
//	 * 
//	 */
//	protected void setProperty(String name, String value, boolean string)
//	{
////		System.out.println("setProp: "+name+" "+value+" "+string);
//		
//		if(value==null)
//		{
//			vact.getMActivity().removeProperty(name);
//		}
//		else
//		{
//			MProperty mprop = vact.getMActivity().getProperties()!=null? vact.getMActivity().getProperties().get(name): null;
//			if(mprop==null)
//			{
//				vact.getMActivity().addProperty(name, value, string);
//			}
//			else
//			{
//				UnparsedExpression uexp = new UnparsedExpression(null, 
//					String.class, string? "\""+value+"\"": value, null);
//				mprop.setInitialValue(uexp);
//			}
//		}
//	}
	
	/**
	 * 
	 */
	protected void refresh()
	{
		MProperty mprop = vact.getMActivity().getProperties()!=null? vact.getMActivity().getProperties().get("iface"): null;
		if(mprop!=null)
		{
			try
			{
				final ClassLoader cl = getModelContainer().getProjectClassLoader()!=null? getModelContainer().getProjectClassLoader()
					: ProvidedServicePropertyPanel.class.getClassLoader();
				Class<?> iface = (Class<?>)SJavaParser.parseExpression(mprop.getInitialValue(), getModel().getModelInfo().getAllImports(), cl).getValue(null);
				ibox.setSelectedItem(new ClassInfo(iface.getName()));
				
				mprop = vact.getMActivity().getProperties()!=null? vact.getMActivity().getProperties().get("method"): null;
				if(mprop!=null)
				{
					String mname = (String)SJavaParser.parseExpression(mprop.getInitialValue(), getModel().getModelInfo().getAllImports(), cl).getValue(null);
					if(mname!=null)
					{
						for(Method m: iface.getDeclaredMethods())
						{
							if(mname.equals(SReflect.getMethodSignature(m)))
							{
								mbox.setSelectedItem(m);
								break;
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				System.out.println("Refresh problem: "+e);
			}
		}
	}
}
