/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeConnectionTool;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeCreationTool;

/**
 * @generated
 */
public class GpmnPaletteFactory
{

	/**
	 * Remove tools corresponding to removed domain elements from palette.
	 * 
	 * @generated NOT
	 */
	public void fillPalette(PaletteRoot paletteRoot)
	{
		fillPaletteGen(paletteRoot);

		//
		//  On palette creation we don't have access to the (file specific) domain.
		//  For disable palette tools we use this static Hack.
		// 

		//TransactionalEditingDomain editDomain = TransactionalEditingDomain.Registry.INSTANCE
		//	.getEditingDomain(GpmnDiagramEditor.EDITING_DOMAIN_ID);

		//if (editDomain != null && editDomain instanceof GpmnDiagramEditDomain)
		//{
		//	GpmnDiagramEditDomain domain = (GpmnDiagramEditDomain) editDomain;
		//	Set removedElementTypes = domain.getRemovedElementTypes();

		Set removedElementTypes = GpmnDiagramEditor.staticRemovedElementTypes;

		for (Object obj : paletteRoot.getChildren())
		{
			if (obj instanceof PaletteContainer)
			{
				PaletteContainer container = (PaletteContainer) obj;

				for (Object toolEntry : container.getChildren())
				{
					if (toolEntry instanceof NodeToolEntry)
					{
						NodeToolEntry entry = (NodeToolEntry) toolEntry;
						for (Object type : entry.elementTypes)
						{
							if (removedElementTypes.contains(type))
							{
								container.remove(entry);
							}
						}
					}
					else if (toolEntry instanceof LinkToolEntry)
					{
						LinkToolEntry entry = (LinkToolEntry) toolEntry;
						for (Object type : entry.relationshipTypes)
						{
							if (removedElementTypes.contains(type))
							{
								container.remove(entry);
							}
						}
					}
				}
			}
			//	}
		}

	}

	//	/**
	//	 * Hack to remove Process, MessageGoal and MessageConnection from palette.
	//	 * 
	//	 * @generated NOT
	//	 */
	//	public void fillPalette(PaletteRoot paletteRoot)
	//	{
	//		// X X X: temporary remove some tools from palette
	//		paletteRoot.add(createGeneral1Group());
	//		paletteRoot.add(createGoalsandPlans2Group());
	//		paletteRoot.add(createConnectors3Group());
	//
	//		for (Object obj : paletteRoot.getChildren())
	//		{
	//			if (obj instanceof PaletteContainer)
	//			{
	//				String toolIdToRemove = null;
	//				PaletteContainer container = (PaletteContainer) obj;
	//				if (container.getId().equals("createGeneral1Group"))
	//				{
	//					toolIdToRemove = "createProcess1CreationTool";
	//				}
	//				else if (container.getId().equals("createGoalsandPlans2Group"))
	//				{
	//					toolIdToRemove = "createMessageGoal8CreationTool";
	//				}
	//				else if (container.getId().equals("createConnectors3Group"))
	//				{
	//					toolIdToRemove = "createMessagingEdge4CreationTool";
	//				}
	//
	//				if (toolIdToRemove != null)
	//				{
	//					for (Object toolEntry : container.getChildren())
	//					{
	//						ToolEntry entry = (ToolEntry) toolEntry;
	//						if (toolIdToRemove.equals(entry.getId()))
	//						{
	//							container.remove(entry);
	//							break;
	//						}
	//					}
	//				}
	//			}
	//		}
	//
	//	}

	/**
	 * @generated
	 */
	public void fillPaletteGen(PaletteRoot paletteRoot)
	{
		paletteRoot.add(createGeneral1Group());
		paletteRoot.add(createGoalsandPlans2Group());
		paletteRoot.add(createConnectors3Group());
	}

	/**
	 * Creates "General" palette tool group
	 * @generated
	 */
	private PaletteContainer createGeneral1Group()
	{
		PaletteDrawer paletteContainer = new PaletteDrawer(
				Messages.General1Group_title);
		paletteContainer.setId("createGeneral1Group"); //$NON-NLS-1$
		paletteContainer.setDescription(Messages.General1Group_desc);
		paletteContainer.add(createProcess1CreationTool());
		paletteContainer.add(createContext2CreationTool());
		paletteContainer.add(createDataObject3CreationTool());
		paletteContainer.add(createTextAnnotation4CreationTool());
		paletteContainer.add(createGenericGpmnElement5CreationTool());
		return paletteContainer;
	}

	/**
	 * Creates "Goals and Plans" palette tool group
	 * @generated
	 */
	private PaletteContainer createGoalsandPlans2Group()
	{
		PaletteDrawer paletteContainer = new PaletteDrawer(
				Messages.GoalsandPlans2Group_title);
		paletteContainer.setId("createGoalsandPlans2Group"); //$NON-NLS-1$
		paletteContainer.setDescription(Messages.GoalsandPlans2Group_desc);
		paletteContainer.add(createMaintainGoal1CreationTool());
		paletteContainer.add(createAchieveGoal2CreationTool());
		paletteContainer.add(createPerformGoal3CreationTool());
		paletteContainer.add(createSequentialGoal4CreationTool());
		paletteContainer.add(createParallelGoal5CreationTool());
		paletteContainer.add(createSubProcessGoal6CreationTool());
		paletteContainer.add(createQueryGoal7CreationTool());
		paletteContainer.add(createMessageGoal8CreationTool());
		paletteContainer.add(createPlan9CreationTool());
		return paletteContainer;
	}

	/**
	 * Creates "Connectors" palette tool group
	 * @generated
	 */
	private PaletteContainer createConnectors3Group()
	{
		PaletteDrawer paletteContainer = new PaletteDrawer(
				Messages.Connectors3Group_title);
		paletteContainer.setId("createConnectors3Group"); //$NON-NLS-1$
		paletteContainer.setDescription(Messages.Connectors3Group_desc);
		paletteContainer.add(createSubGoalEdge1CreationTool());
		paletteContainer.add(createPlanEdge2CreationTool());
		paletteContainer.add(createAssociation3CreationTool());
		paletteContainer.add(createMessagingEdge4CreationTool());
		paletteContainer.add(createGenericGpmnEdge5CreationTool());
		return paletteContainer;
	}

	/**
	 * @generated
	 */
	private ToolEntry createProcess1CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.Process_2001);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Process1CreationTool_title,
				Messages.Process1CreationTool_desc, types);
		entry.setId("createProcess1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.Process_2001));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createContext2CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.Context_2011);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Context2CreationTool_title,
				Messages.Context2CreationTool_desc, types);
		entry.setId("createContext2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.Context_2011));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createDataObject3CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.DataObject_2013);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.DataObject3CreationTool_title,
				Messages.DataObject3CreationTool_desc, types);
		entry.setId("createDataObject3CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.DataObject_2013));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createTextAnnotation4CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.TextAnnotation_2012);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.TextAnnotation4CreationTool_title,
				Messages.TextAnnotation4CreationTool_desc, types);
		entry.setId("createTextAnnotation4CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.TextAnnotation_2012));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createGenericGpmnElement5CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.GenericGpmnElement_2014);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.GenericGpmnElement5CreationTool_title,
				Messages.GenericGpmnElement5CreationTool_desc, types);
		entry.setId("createGenericGpmnElement5CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.GenericGpmnElement_2014));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createMaintainGoal1CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.MaintainGoal_2003);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.MaintainGoal1CreationTool_title,
				Messages.MaintainGoal1CreationTool_desc, types);
		entry.setId("createMaintainGoal1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.MaintainGoal_2003));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createAchieveGoal2CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.AchieveGoal_2002);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.AchieveGoal2CreationTool_title,
				Messages.AchieveGoal2CreationTool_desc, types);
		entry.setId("createAchieveGoal2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.AchieveGoal_2002));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createPerformGoal3CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.PerformGoal_2004);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.PerformGoal3CreationTool_title,
				Messages.PerformGoal3CreationTool_desc, types);
		entry.setId("createPerformGoal3CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.PerformGoal_2004));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createSequentialGoal4CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.SequentialGoal_2006);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.SequentialGoal4CreationTool_title,
				Messages.SequentialGoal4CreationTool_desc, types);
		entry.setId("createSequentialGoal4CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.SequentialGoal_2006));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createParallelGoal5CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.ParallelGoal_2007);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.ParallelGoal5CreationTool_title,
				Messages.ParallelGoal5CreationTool_desc, types);
		entry.setId("createParallelGoal5CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.ParallelGoal_2007));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createSubProcessGoal6CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.SubProcessGoal_2009);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.SubProcessGoal6CreationTool_title,
				Messages.SubProcessGoal6CreationTool_desc, types);
		entry.setId("createSubProcessGoal6CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.SubProcessGoal_2009));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createQueryGoal7CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.QueryGoal_2005);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.QueryGoal7CreationTool_title,
				Messages.QueryGoal7CreationTool_desc, types);
		entry.setId("createQueryGoal7CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.QueryGoal_2005));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createMessageGoal8CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.MessageGoal_2008);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.MessageGoal8CreationTool_title,
				Messages.MessageGoal8CreationTool_desc, types);
		entry.setId("createMessageGoal8CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.MessageGoal_2008));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createPlan9CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.Plan_2010);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Plan9CreationTool_title,
				Messages.Plan9CreationTool_desc, types);
		entry.setId("createPlan9CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.Plan_2010));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createSubGoalEdge1CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.SubGoalEdge_4002);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.SubGoalEdge1CreationTool_title,
				Messages.SubGoalEdge1CreationTool_desc, types);
		entry.setId("createSubGoalEdge1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.SubGoalEdge_4002));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createPlanEdge2CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.PlanEdge_4003);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.PlanEdge2CreationTool_title,
				Messages.PlanEdge2CreationTool_desc, types);
		entry.setId("createPlanEdge2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.PlanEdge_4003));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createAssociation3CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.Association_4001);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.Association3CreationTool_title,
				Messages.Association3CreationTool_desc, types);
		entry.setId("createAssociation3CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.Association_4001));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createMessagingEdge4CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.MessagingEdge_4004);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.MessagingEdge4CreationTool_title,
				Messages.MessagingEdge4CreationTool_desc, types);
		entry.setId("createMessagingEdge4CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.MessagingEdge_4004));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createGenericGpmnEdge5CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.GenericGpmnEdge_4005);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.GenericGpmnEdge5CreationTool_title,
				Messages.GenericGpmnEdge5CreationTool_desc, types);
		entry.setId("createGenericGpmnEdge5CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.GenericGpmnEdge_4005));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private static class NodeToolEntry extends ToolEntry
	{

		/**
		 * @generated
		 */
		private final List elementTypes;

		/**
		 * @generated
		 */
		private NodeToolEntry(String title, String description,
				List elementTypes)
		{
			super(title, description, null, null);
			this.elementTypes = elementTypes;
		}

		/**
		 * @generated
		 */
		public Tool createTool()
		{
			Tool tool = new UnspecifiedTypeCreationTool(elementTypes);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}

	/**
	 * @generated
	 */
	private static class LinkToolEntry extends ToolEntry
	{

		/**
		 * @generated
		 */
		private final List relationshipTypes;

		/**
		 * @generated
		 */
		private LinkToolEntry(String title, String description,
				List relationshipTypes)
		{
			super(title, description, null, null);
			this.relationshipTypes = relationshipTypes;
		}

		/**
		 * @generated
		 */
		public Tool createTool()
		{
			Tool tool = new UnspecifiedTypeConnectionTool(relationshipTypes);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}
}
