package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.AbstractTableModel;

public class DataEdgePropertyPanel extends BasePropertyPanel
{
	/** The column names for the mapping table */
	protected String[] MAPPINGS_COLUMN_NAMES = { "Name", "Value" };
	
	/** The data edge */
	protected VDataEdge dataedge;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public DataEdgePropertyPanel(ModelContainer container, VDataEdge edge)
	{
		super("Data Edge", container);
		this.dataedge = edge;
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("Value Mapping");
		JTextArea textarea = new JTextArea();
		
		if (getBpmnDataEdge().getParameterMapping() != null)
		{
			textarea.setText(getBpmnDataEdge().getParameterMapping().getFirstEntity() != null? getBpmnDataEdge().getParameterMapping().getFirstEntity().getValue() : "");
		}
		
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String exp = getText(e.getDocument());
				UnparsedExpression unusedexp = getBpmnDataEdge().getParameterMapping() != null? getBpmnDataEdge().getParameterMapping().getSecondEntity() : null;
				getBpmnDataEdge().setParameterMapping(exp != null? new UnparsedExpression(null, (String) null, exp, null) : null, unusedexp);
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Index Mapping");
		textarea = new JTextArea();
		if (getBpmnDataEdge().getParameterMapping() != null)
		{
			textarea.setText(getBpmnDataEdge().getParameterMapping().getSecondEntity() != null? getBpmnDataEdge().getParameterMapping().getSecondEntity().getValue() : "");
		}
		
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String exp = getText(e.getDocument());
				UnparsedExpression unusedexp = getBpmnDataEdge().getParameterMapping() != null? getBpmnDataEdge().getParameterMapping().getFirstEntity() : null;
				getBpmnDataEdge().setParameterMapping(unusedexp, exp != null? new UnparsedExpression(null, (String) null, exp, null) : null);
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(y);
	}
	
	protected MDataEdge getBpmnDataEdge()
	{
		return (MDataEdge) dataedge.getBpmnElement();
	}
	
}