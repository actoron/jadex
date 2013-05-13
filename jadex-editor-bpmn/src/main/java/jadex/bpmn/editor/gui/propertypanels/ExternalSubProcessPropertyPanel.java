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
	/** Label text for file name. */
	protected final String FILE_NAME_TEXT = "File";
	
	/** Label text for file expression. */
	protected final String FILE_EXPRESSION_TEXT = "File Expression";
	
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
		
		final JCheckBox expbox = new JCheckBox();
		
		final JLabel label = new JLabel(FILE_NAME_TEXT);
		final JTextArea filearea = new JTextArea();
		filearea.setWrapStyleWord(true);
		filearea.setLineWrap(true);
		MSubProcess msp = (MSubProcess) subprocess.getBpmnElement();
		String filename = null;
		if (msp.hasProperty("filename"))
		{
			UnparsedExpression exp = msp.getPropertyValue("filename");
			filename = exp != null? exp.getValue() : null;
			filename = filename != null? filename.substring(1, filename.length() - 2) : "";
			filearea.setText(filename);
			expbox.setSelected(false);
		}
		else
		{
			UnparsedExpression fileexp = (UnparsedExpression) msp.getPropertyValue("file");
			filearea.setText(fileexp.getValue());
			expbox.setSelected(true);
			label.setText(FILE_EXPRESSION_TEXT);
		}
		
		
		filearea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String val = getText(e.getDocument());
				if (subprocess.isCollapsed())
				{
					if (expbox.isSelected())
					{
						UnparsedExpression exp = new UnparsedExpression("file", String.class, val, null);
						MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
						((MSubProcess) subprocess.getBpmnElement()).addProperty(mprop);
					}
					else
					{
						UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"" + val + "\"", null);
						MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
						((MSubProcess) subprocess.getBpmnElement()).addProperty(mprop);
					}
				}
				else
				{
					modelcontainer.getGraph().getModel().setValue(subprocess, val);
				}
				modelcontainer.setDirty(true);
			}
		});
		JScrollPane sp =  new JScrollPane(filearea);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		expbox.setAction(new AbstractAction("Expression")
		{
			public void actionPerformed(ActionEvent e)
			{
				String val = DocumentAdapter.getText(filearea.getDocument());
				MSubProcess msp = (MSubProcess) subprocess.getBpmnElement();
				if (expbox.isSelected())
				{
					msp.removeProperty("filename");
					label.setText(FILE_EXPRESSION_TEXT);
					UnparsedExpression exp = new UnparsedExpression("file", String.class, val, null);
					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
					msp.addProperty(mprop);
				}
				else
				{
					msp.removeProperty("file");
					label.setText(FILE_NAME_TEXT);
					UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"" + val + "\"", null);
					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
					msp.addProperty(mprop);
				}
			}
		});
		
		JPanel expentrypanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;	
		expentrypanel.add(sp, gbc);
		filearea.setRows(3);
		sp.setMinimumSize(filearea.getPreferredSize());
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		expentrypanel.add(expbox, gbc);
		configureAndAddInputLine(column, label, expentrypanel, y++);
		
		addVerticalFiller(column, y);
	}
}
