/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.tools.CustomPaletteTools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
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
	 * @generated
	 */
	public void fillPalette(PaletteRoot paletteRoot)
	{
		paletteRoot.add(createNodes1Group());
		paletteRoot.add(createConnectors2Group());
	}
	
	/**
	 * Creates "Nodes" palette tool group
	 * @generated NOT
	 */
	private PaletteContainer createNodes1Group()
	{
		PaletteDrawer paletteContainer = new PaletteDrawer(
				Messages.Nodes1Group_title);
		paletteContainer.setId("createNodes1Group"); //$NON-NLS-1$
		paletteContainer.setDescription(Messages.Nodes1Group_desc);
		//paletteContainer.add(createAchieveGoal1CreationTool());
		paletteContainer.add(CustomPaletteTools.TOOL_ENTRIES
				.get(CustomPaletteTools.ACHIEVE_GOAL_TOOL));
		paletteContainer.add(CustomPaletteTools.TOOL_ENTRIES
				.get(CustomPaletteTools.PERFORM_GOAL_TOOL));
		paletteContainer.add(CustomPaletteTools.TOOL_ENTRIES
				.get(CustomPaletteTools.MAINTAIN_GOAL_TOOL));
		paletteContainer.add(createActivationPlan2CreationTool());
		paletteContainer.add(createBpmnPlan3CreationTool());
		paletteContainer.add(createSubProcess4CreationTool());
		
		return paletteContainer;
	}
	
	/**
	 * Creates "Connectors" palette tool group
	 * @generated NOT
	 */
	private PaletteContainer createConnectors2Group()
	{
		PaletteDrawer paletteContainer = new PaletteDrawer(
				Messages.Connectors2Group_title);
		paletteContainer.setId("createConnectors2Group"); //$NON-NLS-1$
		paletteContainer.setDescription(Messages.Connectors2Group_desc);
		//paletteContainer.add(createActivationEdge1CreationTool());
		paletteContainer.add(CustomPaletteTools.TOOL_ENTRIES
				.get(CustomPaletteTools.ACTIVATION_EDGE_TOOL));
		paletteContainer.add(createPlanEdge2CreationTool());
		paletteContainer.add(createSuppressionEdge3CreationTool());
		return paletteContainer;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createAchieveGoal1CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.Goal_2004);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.AchieveGoal1CreationTool_title,
				Messages.AchieveGoal1CreationTool_desc, types);
		entry.setId("createAchieveGoal1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.Goal_2004));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createActivationPlan2CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.ActivationPlan_2001);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.ActivationPlan2CreationTool_title,
				Messages.ActivationPlan2CreationTool_desc, types);
		entry.setId("createActivationPlan2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.ActivationPlan_2001));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createBpmnPlan3CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.BpmnPlan_2003);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.BpmnPlan3CreationTool_title,
				Messages.BpmnPlan3CreationTool_desc, types);
		entry.setId("createBpmnPlan3CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.BpmnPlan_2003));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createSubProcess4CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.SubProcess_2002);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.SubProcess4CreationTool_title,
				Messages.SubProcess4CreationTool_desc, types);
		entry.setId("createSubProcess4CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.SubProcess_2002));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createActivationEdge1CreationTool()
	{
		ToolEntry entry = new ToolEntry(
				Messages.ActivationEdge1CreationTool_title,
				Messages.ActivationEdge1CreationTool_desc, null, null)
		{
		};
		entry.setId("createActivationEdge1CreationTool"); //$NON-NLS-1$
		return entry;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createPlanEdge2CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.PlanEdge_4002);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.PlanEdge2CreationTool_title,
				Messages.PlanEdge2CreationTool_desc, types);
		entry.setId("createPlanEdge2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.PlanEdge_4002));
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}
	
	/**
	 * @generated
	 */
	private ToolEntry createSuppressionEdge3CreationTool()
	{
		List/*<IElementType>*/types = new ArrayList/*<IElementType>*/(1);
		types.add(GpmnElementTypes.SuppressionEdge_4004);
		LinkToolEntry entry = new LinkToolEntry(
				Messages.SuppressionEdge3CreationTool_title,
				Messages.SuppressionEdge3CreationTool_desc, types);
		entry.setId("createSuppressionEdge3CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(GpmnElementTypes
				.getImageDescriptor(GpmnElementTypes.SuppressionEdge_4004));
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
