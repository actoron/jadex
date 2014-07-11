package jadex.bpmn.editor.gui.propertypanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MParameter;
import jadex.bridge.modelinfo.UnparsedExpression;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

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
		
		JLabel label = new JLabel("Value Mapping");// + " " + getBpmnDataEdge().getId());
		JTextArea textarea = new JTextArea();
		
		if (getBpmnDataEdge().getParameterMapping() != null)
		{
			textarea.setText(getBpmnDataEdge().getParameterMapping() != null? getBpmnDataEdge().getParameterMapping().getValue() : "");
		}
		
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String exp = getText(e.getDocument());
//				UnparsedExpression unusedexp = getBpmnDataEdge().getParameterMapping() != null? getBpmnDataEdge().getParameterMapping().getSecondEntity() : null;
				getBpmnDataEdge().setParameterMapping(exp != null? new UnparsedExpression(null, (String) null, exp, null) : null);
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		// Source parameter name.
		if(edge.getSource() instanceof VActivity)
		{
			MActivity src = (MActivity)((VActivity)edge.getSource()).getMActivity();
			
			label = new JLabel("Source Parameter");
			final JComboBox pbox = new JComboBox();
			pbox.addItem(null);
			//textarea = new JTextArea();
			
			List<MParameter> params = src.getParameters(new String[]{MParameter.DIRECTION_INOUT, MParameter.DIRECTION_OUT});
			if(params!=null)
			{
				for(MParameter param: params)
				{
					pbox.addItem(param.getName());
				}
			}
			
			if(getBpmnDataEdge().getParameterMapping() != null)
			{
//				textarea.setText(getBpmnDataEdge().getSourceParameter() != null? getBpmnDataEdge().getSourceParameter() : "");
				pbox.setSelectedItem(getBpmnDataEdge().getSourceParameter());
			}
			
			pbox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String name = (String)pbox.getSelectedItem();
					name = name.length() == 0 ? null : name;
					getBpmnDataEdge().setSourceParameter(name);
					modelcontainer.setDirty(true);
				}
			});
			
//			textarea.getDocument().addDocumentListener(new DocumentAdapter()
//			{
//				public void update(DocumentEvent e)
//				{
//					String name = getText(e.getDocument());
//					name = name.length() == 0 ? null : name;
//					getBpmnDataEdge().setSourceParameter(name);
//					modelcontainer.setDirty(true);
//				}
//			});
			configureAndAddInputLine(column, label, pbox, y++);
		}
		
		// Target parameter name.
		if (edge.getTarget() instanceof VActivity)
		{
			label = new JLabel("Target Parameter");
			textarea = new JTextArea();
			
			if (getBpmnDataEdge().getParameterMapping() != null)
			{
				textarea.setText(getBpmnDataEdge().getTargetParameter() != null? getBpmnDataEdge().getTargetParameter() : "");
			}
			
			textarea.getDocument().addDocumentListener(new DocumentAdapter()
			{
				public void update(DocumentEvent e)
				{
					String name = getText(e.getDocument());
					name = name.length() == 0 ? null : name;
					getBpmnDataEdge().setTargetParameter(name);
					modelcontainer.setDirty(true);
				}
			});
			configureAndAddInputLine(column, label, textarea, y++);
		}
		
//		label = new JLabel("Index Mapping");
//		textarea = new JTextArea();
//		if (getBpmnDataEdge().getParameterMapping() != null)
//		{
//			textarea.setText(getBpmnDataEdge().getParameterMapping().getSecondEntity() != null? getBpmnDataEdge().getParameterMapping().getSecondEntity().getValue() : "");
//		}
//		
//		textarea.getDocument().addDocumentListener(new DocumentAdapter()
//		{
//			public void update(DocumentEvent e)
//			{
//				String exp = getText(e.getDocument());
//				UnparsedExpression unusedexp = getBpmnDataEdge().getParameterMapping() != null? getBpmnDataEdge().getParameterMapping().getFirstEntity() : null;
//				getBpmnDataEdge().setParameterMapping(unusedexp, exp != null? new UnparsedExpression(null, (String) null, exp, null) : null);
//				modelcontainer.setDirty(true);
//			}
//		});
//		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
	}
	
	protected MDataEdge getBpmnDataEdge()
	{
		return (MDataEdge) dataedge.getBpmnElement();
	}
	
}