package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.policies.ConnectionHandleEditPolicyEx;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;

public abstract class AbstractEditPartSupport extends ShapeNodeEditPart
{

	public AbstractEditPartSupport(View view)
	{
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies()
	{
		super.createDefaultEditPolicies();

		// replace ConnectionHandleEditPolicy
		removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
		installEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE,
				new ConnectionHandleEditPolicyEx());

	}

	/**
	 * @generated NOT
	 */
	public List _getMARelTypesOnSourceAndTarget(IGraphicalEditPart targetEditPart)
	{
		List<org.eclipse.gmf.runtime.emf.type.core.IElementType> types = new ArrayList<org.eclipse.gmf.runtime.emf.type.core.IElementType>();

		// subgoal edge only to goal
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
//		if (targetEditPart instanceof MessageGoalEditPart)
//		{
//			types.add(GpmnElementTypes.SubGoalEdge_4002);
//		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}

		// plan edge only to plan
		if (targetEditPart instanceof PlanEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		return types;
	}

	/**
	 * @generated NOT
	 */
	public List _getMATypesForTarget(IElementType relationshipType)
	{
		List<org.eclipse.gmf.runtime.emf.type.core.IElementType> types = new ArrayList<org.eclipse.gmf.runtime.emf.type.core.IElementType>();

		// subgoal edge only to goal
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
//		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
//		{
//			types.add(GpmnElementTypes.MessageGoal_2008);
//		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}

		// plan edge only to plan
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		return types;
	}

	/**
	 * @generated NOT
	 */
	public List _getMATypesForSource(IElementType relationshipType)
	{
		List<org.eclipse.gmf.runtime.emf.type.core.IElementType> types = new ArrayList<org.eclipse.gmf.runtime.emf.type.core.IElementType>();

		// associations
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.Context_2011);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.TextAnnotation_2012);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.DataObject_2013);
		}

		// incoming subgoal edge from goal
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
//		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
//		{
//			types.add(GpmnElementTypes.MessageGoal_2008);
//		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		
		// incoming subgoal edges can have a plan as source
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}

		// plan edge only from goal
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
//		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
//		{
//			types.add(GpmnElementTypes.MessageGoal_2008);
//		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}

		return types;
	}

	
	// ---- original generated methods ----
	// ---- COPIED FROM GENERRATED EDIT PART ----
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSource()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.SubGoalEdge_4002);
		types.add(GpmnElementTypes.PlanEdge_4003);
		types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		return types;
	}

	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof MessageGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof PlanEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof GenericGpmnElementEditPart)
		{
			types.add(GpmnElementTypes.SubGoalEdge_4002);
		}
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof MessageGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof PlanEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof GenericGpmnElementEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4003);
		}
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof MessageGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof PlanEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		if (targetEditPart instanceof GenericGpmnElementEditPart)
		{
			types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForTarget(
			IElementType relationshipType)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnTarget()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.Association_4001);
		types.add(GpmnElementTypes.SubGoalEdge_4002);
		types.add(GpmnElementTypes.PlanEdge_4003);
		types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		return types;
	}

	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForSource(
			IElementType relationshipType)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.Context_2011);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.TextAnnotation_2012);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.DataObject_2013);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.SubGoalEdge_4002)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4003)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.GenericGpmnEdge_4005)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		return types;
	}
}
