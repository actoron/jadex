package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.javaparser.SJavaParser;

/**
 *  ECA property panel for timer event activities.
 */
public class ECARuleEventPropertyPanel extends BasePropertyPanel
{
	/** The event activity. */
	protected VActivity event;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public ECARuleEventPropertyPanel(final ModelContainer container, Object selection)
	{
		super("ECA Rule Event", container);
		VActivity event = (VActivity) selection;
		setLayout(new GridBagLayout());
		this.event = event;
		
		JLabel label = new JLabel("Condition: ");
		JTextField ettf = new JTextField();
		UnparsedExpression cond = (UnparsedExpression)((MActivity)event.getBpmnElement()).getPropertyValue("condition");
		if(cond!=null)
		{
			ettf.setText(cond.getValue());
		}
		
		ettf.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				UnparsedExpression dur = new UnparsedExpression("condition", "java.lang.String", getText(e.getDocument()), null);
				((MActivity)ECARuleEventPropertyPanel.this.event.getBpmnElement()).setPropertyValue("condition", dur);
				container.setDirty(true);
			}
		});
		
		UnparsedExpression evt = (UnparsedExpression)((MActivity)event.getBpmnElement()).getPropertyValue("eventtypes");
		String vals[] = null;
		if(evt!=null)
		{
			try
			{
				vals = (String[])SJavaParser.getParsedValue(evt, container.getBpmnModel().getModelInfo().getAllImports(), null, container.getProjectClassLoader());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		final AddRemoveTablePanel p = new AddRemoveTablePanel("Event Types", container.getSettings().getImageProvider(), "Event", vals);
		
		p.getTable().getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				StringBuffer buf = new StringBuffer();
				
				buf.append("new java.lang.String[]{");
				
				int cnt = p.getTable().getModel().getRowCount();
				if(cnt>0)
				{
					for(int i=0; i<cnt; i++)
					{
						buf.append("\"").append(p.getTable().getModel().getValueAt(i, 0)).append("\"");
						if(i+1<cnt)
							buf.append(", ");
					}
				}
				else
				{
					buf.append("0");
				}
				buf.append("}");
				
				UnparsedExpression dur = new UnparsedExpression(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES, "java.lang.String[]", buf.toString(), null);
				((MActivity)ECARuleEventPropertyPanel.this.event.getBpmnElement()).setPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES, dur);
				container.setDirty(true);
			}
		});
		
		add(label, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		add(ettf, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
		add(p, new GridBagConstraints(0,1,2,1,1,1,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
//		addVerticalFiller(column, y);
	}
}
