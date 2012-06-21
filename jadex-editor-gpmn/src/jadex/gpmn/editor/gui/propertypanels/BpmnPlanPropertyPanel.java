package jadex.gpmn.editor.gui.propertypanels;

import jadex.gpmn.editor.gui.DocumentAdapter;
import jadex.gpmn.editor.gui.IModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IBpmnPlan;
import jadex.gpmn.editor.model.visual.VPlan;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

/**
 *  Panel displaying BPMN plan properties.
 *
 */
public class BpmnPlanPropertyPanel extends BasePropertyPanel
{
	/** The plan. */
	protected VPlan plan;
	
	/**
	 *  Creates a new goal property panel.
	 */
	public BpmnPlanPropertyPanel(IModelContainer container, VPlan vplan)
	{
		super(container);
		this.plan = vplan;
		
		setLayout(new GridLayout(1, 2));
		
		setBorder(new TitledBorder("Plan"));
		
		int y = 0;
		JPanel column = new JPanel();
		column.setLayout(new GridBagLayout());
		add(column);
		
		JLabel label = new JLabel("Name");
		JTextArea textarea = new NameArea(getGraph().getModel(), plan);
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("BPMN Plan Reference");
		textarea = new JTextArea(getBpmnPlan().getPlanref());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getBpmnPlan().setPlanref(SGuiHelper.getText(e.getDocument()));
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Context Condition");
		textarea = new JTextArea(getBpmnPlan().getContextCondition());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getBpmnPlan().setContextCondition(SGuiHelper.getText(e.getDocument()));
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
		
		column = new JPanel();
		add(column);
	}
	
	protected IBpmnPlan getBpmnPlan()
	{
		return (IBpmnPlan) plan.getPlan();
	}
}
