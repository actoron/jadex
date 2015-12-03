package jadex.bpmn.editor.gui.propertypanels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MProperty;
import jadex.bridge.modelinfo.UnparsedExpression;

public class SignalEventHandlerPropertyPanel extends BasePropertyPanel
{

	public SignalEventHandlerPropertyPanel(ModelContainer container, Object selection)
	{
		super("Signal", container);
		VActivity vsighandler = (VActivity) selection;
		final MActivity msighandler = vsighandler.getMActivity();
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("Trigger Result");
		JTextArea trigarea = new JTextArea();
		UnparsedExpression trig = (UnparsedExpression) msighandler.getPropertyValue(MBpmnModel.SIGNAL_EVENT_TRIGGER);
		if (trig != null)
		{
			trigarea.setText(trig.getValue());
		}
		trigarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String triggername = getText(e.getDocument());
				if (triggername != null && !triggername.isEmpty())
				{
					UnparsedExpression exp = new UnparsedExpression(MBpmnModel.SIGNAL_EVENT_TRIGGER, (String) null, "\"" + triggername +"\"", null); 
					MProperty prop = new MProperty(null, MBpmnModel.SIGNAL_EVENT_TRIGGER, exp);
					msighandler.addProperty(prop);
				}
				else
				{
					msighandler.removeProperty(MBpmnModel.SIGNAL_EVENT_TRIGGER);
				}
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, trigarea, y++);
		
		addVerticalFiller(column, y);
		
	}
}
