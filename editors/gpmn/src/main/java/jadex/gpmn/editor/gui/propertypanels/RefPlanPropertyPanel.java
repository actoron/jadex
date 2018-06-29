package jadex.gpmn.editor.gui.propertypanels;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import jadex.gpmn.editor.gui.DocumentAdapter;
import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IRefPlan;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VPlan.VPlanType;

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
	public RefPlanPropertyPanel(ModelContainer container, VPlan vplan)
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
		JPanel refarea = createTextButtonPanel();
		
		textarea = (JTextArea) refarea.getComponent(0);
		textarea.setText(getRefPlan().getPlanref());
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
		JButton button = (JButton) refarea.getComponent(1);
		button.setAction(new AbstractAction("Find Class...")
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] classes = modelcontainer.getProjectClasses();
				if (classes != null && classes.length > 0)
				{
					String planname = (String) JOptionPane.showInputDialog(
					                     RefPlanPropertyPanel.this,
					                     "Select a plan implementation...",
					                     "Select Plan",
					                     JOptionPane.PLAIN_MESSAGE,
					                     null,
					                     classes,
					                     null);
					if ((planname != null) && (planname.length() > 0))
					{
						((IRefPlan) plan.getPlan()).setPlanref(planname);
						((JTextArea) ((JButton) e.getSource()).getParent().getComponent(0)).setText(planname);
					}
				}
			}
		});
		
		configureAndAddInputLine(column, label, refarea, y++);
		
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
