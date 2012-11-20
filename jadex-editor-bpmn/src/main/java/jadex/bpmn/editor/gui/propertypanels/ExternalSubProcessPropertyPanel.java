package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.model.MSubProcess;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

/**
 *  Property panel for timer event activities.
 *
 */
public class ExternalSubProcessPropertyPanel extends BasePropertyPanel
{
	/** The external subprocess. */
	protected VExternalSubProcess subprocess;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public ExternalSubProcessPropertyPanel(ModelContainer container, VExternalSubProcess subproc)
	{
		super("External Sub-Process", container);
		this.subprocess = subproc;
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("File");
		JTextArea filearea = new JTextArea();
		String txt = (String) ((MSubProcess) subprocess.getBpmnElement()).getPropertyValue("file");
		txt = txt != null? txt : "";
		filearea.setText(txt);
		filearea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				if (subprocess.isCollapsed())
				{
					((MSubProcess) subprocess.getBpmnElement()).setPropertyValue("file", getText(e.getDocument()));
				}
				else
				{
					modelcontainer.getGraph().getModel().setValue(subprocess, getText(e.getDocument()));
				}
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, filearea, y++);
		
		addVerticalFiller(column, y);
	}
}
