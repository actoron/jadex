package jadex.gpmn.editor.gui.propertypanels;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import com.mxgraph.model.mxCell;

import jadex.gpmn.editor.gui.DocumentAdapter;
import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.ModelConstants;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;

/**
 *  Panel displaying goal properties.
 *
 */
public class GoalPropertyPanel extends BasePropertyPanel
{
	/** The goal. */
	protected VGoal goal;
	
	/**
	 *  Creates a new goal property panel.
	 */
	public GoalPropertyPanel(ModelContainer container, VGoal vgoal)
	{
		super(container);
		this.goal = vgoal;
		
		setLayout(new GridBagLayout());
		
		setBorder(new TitledBorder("Goal"));
		
		// First column
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("Name");
		JTextArea textarea = new NameArea(modelcontainer, goal);
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Type");
		JComboBox cbox = new JComboBox(ModelConstants.GOAL_TYPES);
		cbox.setEditable(false);
		cbox.setSelectedItem(goal.getGoal().getGoalType());
		cbox.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				goal.setGoalType((String) ((JComboBox)e.getSource()).getSelectedItem());
				SGuiHelper.refreshCellView(getGraph(), goal);
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, cbox, y++);
		
		label = new JLabel("Context Condition");
		textarea = new JTextArea(goal.getGoal().getContextCondition());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				goal.getGoal().setContextCondition(SGuiHelper.getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		if (ModelConstants.ACHIEVE_GOAL_TYPE.equals(goal.getGoal().getGoalType()))
		{
			label = new JLabel("Target Condition");
			textarea = new JTextArea(goal.getGoal().getTargetCondition());
			textarea.getDocument().addDocumentListener(new DocumentAdapter()
			{
				public void update(DocumentEvent e)
				{
					goal.getGoal().setTargetCondition(SGuiHelper.getText(e.getDocument()));
					modelcontainer.setDirty(true);
				}
			});
			configureAndAddInputLine(column, label, textarea, y++);
		}
		
		if (ModelConstants.MAINTAIN_GOAL_TYPE.equals(goal.getGoal().getGoalType()))
		{
			label = new JLabel("Maintain Condition");
			textarea = new JTextArea(goal.getGoal().getMaintainCondition());
			textarea.getDocument().addDocumentListener(new DocumentAdapter()
			{
				public void update(DocumentEvent e)
				{
					goal.getGoal().setMaintainCondition(SGuiHelper.getText(e.getDocument()));
					modelcontainer.setDirty(true);
				}
			});
			configureAndAddInputLine(column, label, textarea, y++);
		}
		
		label = new JLabel("Creation Condition");
		textarea = new JTextArea(goal.getGoal().getCreationCondition());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				goal.getGoal().setCreationCondition(SGuiHelper.getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Drop Condition");
		textarea = new JTextArea(goal.getGoal().getDropCondition());
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				goal.getGoal().setDropCondition(SGuiHelper.getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
		
		// Second column
		y = 0;
		column = createColumn(colnum++);
		
		label = new JLabel("Activation Plan Semantics");
		cbox = new JComboBox();
		cbox.setEditable(false);
		setPlanSemantics(cbox);
		cbox.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox cbox = (JComboBox) e.getSource();
				for (int i = 0; i < goal.getEdgeCount(); ++i)
				{
					VEdge edge = (VEdge) goal.getEdgeAt(i);
					if (edge.getEdge() != null && edge.getEdge().getTarget() instanceof IActivationPlan)
					{
						VPlan aplan = (VPlan) edge.getTarget();
						((IActivationPlan) aplan.getPlan()).setMode((String) cbox.getSelectedItem());
						SGuiHelper.refreshCellView(getGraph(), aplan);
						for (int j = 0; j < aplan.getEdgeCount(); ++j)
						{
							VEdge aplanedge = (VEdge) aplan.getEdgeAt(j);
							if (aplanedge.getEdge() instanceof IPlanEdge)
							{
								SGuiHelper.refreshCellView(getGraph(), (mxCell) aplanedge.getSource());
							}
						}
						modelcontainer.setDirty(true);
					}
				}
			}
		});
		configureAndAddInputLine(column, label, cbox, y++);
		
		JCheckBox cb1 = new JCheckBox();
		cb1.setMargin(new Insets(0, 0, 0, 0));
		cb1.setBorder(new EmptyBorder(new Insets(2, 0, 2, 0)));
		cb1.setSelected(goal.getGoal().isRandomSelection());
		cb1.setAction(new AbstractAction("Random Plan Selection")
		{
			public void actionPerformed(ActionEvent e)
			{
				goal.getGoal().setRandomSelection(((JCheckBox)e.getSource()).isSelected());
				modelcontainer.setDirty(true);
			}
		});
		
		JCheckBox cb2 = new JCheckBox();
		cb2.setMargin(new Insets(0, 0, 0, 0));
		cb2.setBorder(new EmptyBorder(new Insets(2, 0, 2, 0)));
		cb2.setSelected(goal.getGoal().isPostToAll());
		cb2.setAction(new AbstractAction("Post to all")
		{
			public void actionPerformed(ActionEvent e)
			{
				goal.getGoal().setPostToAll(((JCheckBox)e.getSource()).isSelected());
				modelcontainer.setDirty(true);
			}
		});
		
		configureAndAddInputLine(column, cb1, cb2, y++);
		
		final JPanel retrydelaypanel = new JPanel(new GridBagLayout());
		retrydelaypanel.setVisible(goal.getGoal().isRetry());
		JLabel delaylabel = new JLabel("Delay");
		final JTextArea retrydelayarea = new JTextArea(String.valueOf(goal.getGoal().getRetryDelay()));
		retrydelayarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				try
				{
					int delay = Integer.parseInt(SGuiHelper.getText(e.getDocument()));
					retrydelayarea.setBackground(Color.WHITE);
					goal.getGoal().setRetryDelay(delay);
					modelcontainer.setDirty(true);
				}
				catch (NumberFormatException e1)
				{
					retrydelayarea.setBackground(Color.RED);
				}
			}
		});
		retrydelayarea.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				retrydelayarea.setText(String.valueOf(goal.getGoal().getRetryDelay()));
				retrydelayarea.setBackground(Color.WHITE);
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(retrydelaypanel, delaylabel, retrydelayarea, 0, false);
		
		cb1 = new JCheckBox();
		cb1.setMargin(new Insets(0, 0, 0, 0));
		cb1.setBorder(new EmptyBorder(new Insets(2, 0, 2, 0)));
		cb1.setSelected(goal.getGoal().isRetry());
		cb1.setAction(new AbstractAction("Retry Plans")
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean retry = ((JCheckBox)e.getSource()).isSelected();
				goal.getGoal().setRetry(retry);
				retrydelaypanel.setVisible(retry);
				modelcontainer.setDirty(true);
			}
		});
		
		configureAndAddInputLine(column, cb1, retrydelaypanel, y++);
		
		final JPanel recurdelaypanel = new JPanel(new GridBagLayout());
		recurdelaypanel.setVisible(goal.getGoal().isRecur());
		delaylabel = new JLabel("Delay");
		final JTextArea recurdelayarea = new JTextArea(String.valueOf(goal.getGoal().getRecurDelay()));
		recurdelayarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				try
				{
					int delay = Integer.parseInt(SGuiHelper.getText(e.getDocument()));
					recurdelayarea.setBackground(Color.WHITE);
					goal.getGoal().setRecurDelay(delay);
					modelcontainer.setDirty(true);
				}
				catch (NumberFormatException e1)
				{
					recurdelayarea.setBackground(Color.RED);
				}
			}
		});
		recurdelayarea.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				recurdelayarea.setText(String.valueOf(goal.getGoal().getRecurDelay()));
				recurdelayarea.setBackground(Color.WHITE);
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(recurdelaypanel, delaylabel, recurdelayarea, 0, false);
		
		cb1 = new JCheckBox();
		cb1.setMargin(new Insets(0, 0, 0, 0));
		cb1.setBorder(new EmptyBorder(new Insets(2, 0, 2, 0)));
		cb1.setSelected(goal.getGoal().isRecur());
		cb1.setAction(new AbstractAction("Recur")
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean recur = ((JCheckBox)e.getSource()).isSelected();
				goal.getGoal().setRecur(recur);
				recurdelaypanel.setVisible(recur);
				modelcontainer.setDirty(true);
			}
		});
		
		configureAndAddInputLine(column, cb1, recurdelaypanel, y++);
		
		addVerticalFiller(column, y);
	}
	
	protected void setPlanSemantics(JComboBox cbox)
	{
		String mode = goal.getPlanMode();
		if (mode != null)
		{
			cbox.setModel(new DefaultComboBoxModel(ModelConstants.ACTIVATION_MODES));
			cbox.setSelectedItem(mode);
		}
		else
		{
			cbox.setModel(new DefaultComboBoxModel(new Object[] { "Mixed/None" }));
			cbox.setEnabled(false);
		}
	}
}
