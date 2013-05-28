package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.modelinfo.UnparsedExpression;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
		
		
	}
}
