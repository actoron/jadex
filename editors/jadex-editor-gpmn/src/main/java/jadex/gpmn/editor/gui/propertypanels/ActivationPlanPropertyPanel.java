package jadex.gpmn.editor.gui.propertypanels;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.mxgraph.model.mxCell;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.ModelConstants;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VPlan;

/**
 *  Panel displaying activation plan properties.
 *
 */
public class ActivationPlanPropertyPanel extends BasePropertyPanel
{
	/** The plan. */
	protected VPlan plan;
	
	/**
	 *  Creates a new goal property panel.
	 */
	public ActivationPlanPropertyPanel(ModelContainer container, VPlan vplan)
	{
		super(container);
		this.plan = vplan;
		
		setLayout(new GridLayout(1, 2));
		
		setBorder(new TitledBorder("Activation Plan"));
		
		int y = 0;
		
		JPanel column = new JPanel();
		column.setLayout(new GridBagLayout());
		add(column);
		
		JLabel label = new JLabel("Name");
		JTextArea textarea = new NameArea(modelcontainer, plan);
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Activation Plan Semantics");
		JComboBox cbox = new JComboBox(ModelConstants.ACTIVATION_MODES);
		cbox.setEditable(false);
		cbox.setSelectedItem(getPlan().getMode());
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getPlan().setMode((String) ((JComboBox)e.getSource()).getSelectedItem());
				
				for (int i = 0; i < plan.getEdgeCount(); ++i)
				{
					VEdge edge = (VEdge) plan.getEdgeAt(i);
					if (edge.getEdge() instanceof IPlanEdge)
					{
						SGuiHelper.refreshCellView(getGraph(), (mxCell) edge.getSource());
					}
					SGuiHelper.refreshCellView(getGraph(), (mxCell) edge);
				}
				
				SGuiHelper.refreshCellView(getGraph(), plan);
				
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column,label, cbox, y++);
		
		addVerticalFiller(column, y);
		
		column = new JPanel();
		add(column);
	}
	
	protected IActivationPlan getPlan()
	{
		return (IActivationPlan) plan.getPlan();
	}
}
