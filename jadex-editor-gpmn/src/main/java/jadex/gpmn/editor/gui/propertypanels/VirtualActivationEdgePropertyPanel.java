package jadex.gpmn.editor.gui.propertypanels;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.mxgraph.model.mxCell;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.ModelConstants;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;

/**
 *  Panel displaying virtual edge properties.
 *
 */
public class VirtualActivationEdgePropertyPanel extends BasePropertyPanel
{
	/** The edge. */
	protected VVirtualActivationEdge edge;
	
	/**
	 *  Creates a new goal property panel.
	 */
	public VirtualActivationEdgePropertyPanel(ModelContainer container, VVirtualActivationEdge vedge)
	{
		super(container);
		this.edge = vedge;
		
		setLayout(new GridLayout(1, 2));
		
		setBorder(new TitledBorder("Virtual Activation Edge"));
		
		int y = 0;
		
		JPanel column = new JPanel();
		column.setLayout(new GridBagLayout());
		add(column);
		JLabel label = new JLabel("Activation Semantics");
		JComboBox cbox = new JComboBox(ModelConstants.ACTIVATION_MODES);
		cbox.setEditable(false);
		cbox.setSelectedItem(getPlan().getMode());
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getPlan().setMode((String) ((JComboBox)e.getSource()).getSelectedItem());
				
				for (int i = 0; i < edge.getPlan().getEdgeCount(); ++i)
				{
					VEdge pedge = (VEdge) edge.getPlan().getEdgeAt(i);
					if (pedge.getEdge() instanceof IPlanEdge)
					{
						SGuiHelper.refreshCellView(getGraph(), (mxCell) pedge.getSource());
					}
					SGuiHelper.refreshCellView(getGraph(), (mxCell) pedge);
				}
				
				List<VVirtualActivationEdge> group = edge.getEdgeGroup();
				for (VVirtualActivationEdge virtedge : group)
				{
					virtedge.setStyle();
					SGuiHelper.refreshCellView(getGraph(), virtedge);
				}
				modelcontainer.setDirty(true);
				SGuiHelper.refreshCellView(getGraph(), edge.getPlan());
			}
		});
		configureAndAddInputLine(column,label, cbox, y++);
		
		addVerticalFiller(column, y);
		
		column = new JPanel();
		add(column);
	}
	
	protected IActivationPlan getPlan()
	{
		return (IActivationPlan) edge.getPlan().getPlan();
	}
}
