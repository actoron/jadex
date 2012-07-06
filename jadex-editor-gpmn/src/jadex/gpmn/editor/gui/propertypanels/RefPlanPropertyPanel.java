package jadex.gpmn.editor.gui.propertypanels;

import jadex.gpmn.editor.gui.DocumentAdapter;
import jadex.gpmn.editor.gui.IModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IRefPlan;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VPlan.VPlanType;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

/**
 *  Panel displaying referred plan properties.
 *
 */
public class RefPlanPropertyPanel extends BasePropertyPanel
{
	/** The plan. */
	protected VPlan plan;
	
	/**
	 *  Creates a new goal property panel.
	 */
	public RefPlanPropertyPanel(IModelContainer container, VPlan vplan)
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
		JTextArea textarea = new NameArea(modelcontainer, plan);
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Plan Reference");
		textarea = new JTextArea(getRefPlan().getPlanref());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String ref = SGuiHelper.getText(e.getDocument());
				getRefPlan().setPlanref(ref);
				modelcontainer.setDirty(true);
				VPlanType pt = (VPlanType) plan.getChildAt(0);
				if (ref != null && ref.endsWith(".bpmn"))
				{
					modelcontainer.getGraph().getModel().setValue(pt, "BPMN");
				}
				else
				{
					modelcontainer.getGraph().getModel().setValue(pt, "Java");
				}
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Context Condition");
		textarea = new JTextArea(getRefPlan().getContextCondition());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getRefPlan().setContextCondition(SGuiHelper.getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
		
		column = new JPanel();
		add(column);
	}
	
	protected IRefPlan getRefPlan()
	{
		return (IRefPlan) plan.getPlan();
	}
}
