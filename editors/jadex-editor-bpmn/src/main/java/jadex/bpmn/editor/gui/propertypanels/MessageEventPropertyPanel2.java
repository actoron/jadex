package jadex.bpmn.editor.gui.propertypanels;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;

/**
 * 
 */
public class MessageEventPropertyPanel2 extends BasePropertyPanel
{
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public MessageEventPropertyPanel2(final ModelContainer container, Object selection)
	{
		super("Message Event Properties", container);
		VActivity vact = (VActivity) selection;
		setLayout(new BorderLayout());
	
		add(createTabPanel(vact), BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	protected JTabbedPane createTabPanel(VActivity vact)
	{
		JTabbedPane tabpane = new JTabbedPane();
		tabpane.addTab("Provided Service Method", new ProvidedServicePropertyPanel(getModelContainer(), vact));
		tabpane.addTab("Message Definition", new MessageEventPropertyPanel(getModelContainer(), vact));
		return tabpane;
	}
}