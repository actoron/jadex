package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.part.GpmnDiagramMessages;
import jadex.tools.gpmn.figures.GPMNNodeFigure;
import jadex.tools.gpmn.figures.GoalFigure;
import jadex.tools.gpmn.figures.GpmnShapesDefaultSizes;
import jadex.tools.gpmn.figures.connectionanchors.impl.ConnectionAnchorFactory;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.diagram.ui.requests.ToggleConnectionLabelsRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.figures.connectionanchors.IConnectionAnchorFactory;
import org.eclipse.swt.widgets.Display;


/**
 * @generated NOT
 */
public abstract class GoalEditPartSupport extends AbstractEditPartSupport
{

	/**
	 * @generated NOT
	 */
	public GoalEditPartSupport(View view)
	{
		super(view);
	}

	/**
	 * Use generated subclass method
	 * 
	 * @generated NOT
	 * @return the primary shape for this edit part
	 */
	public abstract GoalFigure getPrimaryShape();
	
	/**
	 * @generated NOT
	 * @param goalFigure
	 *            the GoalFigure to modify
	 * @return the modified GoalFigure provided as parameter
	 */
	protected IFigure createNodeShapeSupport(GoalFigure goalFigure)
	{

		Goal goal = (Goal) getPrimaryView().getElement();
		setGoalTypeAndLabelAndLayout(goalFigure, goal);

		// initialize goal figure values
		goalFigure.setSequentialOrderMode(goal.isSequential());
		switch (goal.getGoalType().getValue())
		{
			case GoalType.SEQUENTIAL_GOAL_VALUE:
				goalFigure.setGoalTypeSequential(true);
				break;

			case GoalType.PARALLEL_GOAL_VALUE:
				goalFigure.setGoalTypeParallel(true);
				break;

			default:
				break;
		}

		setGoalTypeAndLabelAndLayout(goalFigure, goal);

		return goalFigure;
	}

	/**
	 * Ability to override EditPolicies.
	 * 
	 * @generated NOT
	 */
	protected void createDefaultEditPolicies()
	{
		super.createDefaultEditPolicies();
		// change some edit policies
	}
	
	/**
	 * @return An appropriate connection anchor factory
	 * @generated NOT
	 */
	protected IConnectionAnchorFactory getConnectionAnchorFactory()
	{
		return ConnectionAnchorFactory.INSTANCE;
	}

	/**
	 * Create a NodePlate with appropriate {@link IConnectionAnchorFactory} and a minimum size
	 * 
	 * @generated NOT
	 */
	protected NodeFigure createNodePlateSupport()
	{
		Dimension minSize = (Dimension) getMapMode().DPtoLP(
				GpmnShapesDefaultSizes.GOAL_FIGURE_SIZE);
		return new GPMNNodeFigure(getConnectionAnchorFactory(), minSize);
	}

	/**
	 * Update the figure with proper type, label and layout
	 * 
	 * @param goalFigure
	 * @param goal
	 * @return true if refreshVisual is recommended (there was a change)
	 * 
	 * @generated NOT
	 */
	private boolean setGoalTypeAndLabelAndLayout(GoalFigure goalFigure,
			Goal goal)
	{
		goalFigure.setGoalType(goal.getGoalType().getLiteral());
		boolean res = false;
		WrappingLabel wl = goalFigure.getFigureGoalNameFigure();
		wl.setTextWrap(true);

		if (goal.getName() == null)
		{
			if (goal.getGoalType().equals(GoalType.META_GOAL))
			{
				if (!GpmnDiagramMessages.GoalEditPart_goal_default_name
						.equals(wl.getText()))
				{
					wl
							.setText(GpmnDiagramMessages.GoalEditPart_goal_default_name);
				}
			}
			res = true;
		}

		// //FIXME: this works not as expected, compute bounds of tooltip!
		// //set label tooltip
		// WrappingLabel tooltip = null;
		// if (goal.getDescription() != null &&
		// !"".equals(goal.getDescription()))
		// {
		// tooltip = new WrappingLabel(go.getDescription());
		// }
		// else
		// {
		// tooltip = new WrappingLabel(goal.getName());
		// }
		// tooltip.setAlignment(PositionConstants.CENTER);
		// tooltip.setTextJustification(PositionConstants.CENTER);
		// tooltip.setTextWrap(true);
		// tooltip.setBorder(new MarginBorder(1, 1, 1, 1));
		// tooltip.setBackgroundColor(ColorConstants.lightBlue);
		// wl.setToolTip(tooltip);

		return setAlignments(goalFigure, goal, wl, res);
	}

	/**
	 * Align label in figure
	 * 
	 * @param goalFigure
	 * @param goal
	 * @param wl
	 * @param res
	 * @return
	 */
	private boolean setAlignments(GoalFigure goalFigure, Goal goal,
			WrappingLabel wl, boolean res)
	{

		if (!(goalFigure.getLayoutManager() instanceof StackLayout))
		{
			StackLayout layout = new StackLayout();
			goalFigure.setLayoutManager(layout);
			res = true;
		}
		wl.setAlignment(PositionConstants.CENTER);
		wl.setTextJustification(PositionConstants.CENTER);

		goalFigure.invalidate();
		return res;
	}

	/**
	 * Synchronizes the shape with the goal
	 * 
	 * @generated NOT
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart
	 *      #handlePropertyChangeEvent(java.beans.PropertyChangeEvent)
	 */
	protected void handleNotificationEvent(Notification notification)
	{
		if (notification.getEventType() == Notification.SET
				|| notification.getEventType() == Notification.UNSET)
		{
			if (GpmnPackage.eINSTANCE.getGoal_Sequential()
					.equals(notification.getFeature()))
			{
				// orderedMode changed
				getPrimaryShape().setSequentialOrderMode(
						notification.getNewBooleanValue());
				toggleOutgoingConnectionLabels(notification
						.getNewBooleanValue());

			}
			else if (GpmnPackage.eINSTANCE.getGoal_Recur().equals(
					notification.getFeature()))
			{
				// recure changed
				getPrimaryShape().setRecur(notification.getNewBooleanValue());
			}
			// ... and so on

		}

		super.handleNotificationEvent(notification);
	}

	/**
	 * Change the show/hide property of all outgoing Edges
	 * 
	 * @param showConnectionLabels
	 *            to show/hide the labels
	 * @generated NOT
	 */
	protected void toggleOutgoingConnectionLabels(boolean showConnectionLabels)
	{
		CompoundCommand cc = new CompoundCommand(
				"Toggle source connections labels");
		for (Object e : getSourceConnections())
		{
			if (e instanceof ConnectionEditPart)
			{
				final ConnectionEditPart cep = (ConnectionEditPart) e;
				final ToggleConnectionLabelsRequest tclr = new ToggleConnectionLabelsRequest(
						showConnectionLabels);
				
				// toggle connection label
				Display.getCurrent().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						cep.performRequest(tclr);
					}
				});
				
				//Command toggleLabelCmd = cep.getCommand(tclr);
				//cc.add(toggleLabelCmd);

				// ((ConnectionEditPart) e).getTarget().refresh();
			}
		}
		getDiagramEditDomain().getDiagramCommandStack().execute(cc);
	}

}
