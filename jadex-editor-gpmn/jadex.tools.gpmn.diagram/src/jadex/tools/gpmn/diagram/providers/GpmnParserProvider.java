/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeOrderEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessNameEditPart;
import jadex.tools.gpmn.diagram.parsers.MessageFormatParser;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.common.ui.services.parser.GetParserOperation;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserProvider;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserService;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.parser.ParserHintAdapter;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GpmnParserProvider extends AbstractProvider implements
		IParserProvider
{
	
	/**
	 * @generated
	 */
	private IParser activationPlanName_5001Parser;
	
	/**
	 * @generated
	 */
	private IParser getActivationPlanName_5001Parser()
	{
		if (activationPlanName_5001Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			activationPlanName_5001Parser = parser;
		}
		return activationPlanName_5001Parser;
	}
	
	/**
	 * @generated
	 */
	private IParser subProcessName_5002Parser;
	
	/**
	 * @generated
	 */
	private IParser getSubProcessName_5002Parser()
	{
		if (subProcessName_5002Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			subProcessName_5002Parser = parser;
		}
		return subProcessName_5002Parser;
	}
	
	/**
	 * @generated
	 */
	private IParser bpmnPlanName_5003Parser;
	
	/**
	 * @generated
	 */
	private IParser getBpmnPlanName_5003Parser()
	{
		if (bpmnPlanName_5003Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			bpmnPlanName_5003Parser = parser;
		}
		return bpmnPlanName_5003Parser;
	}
	
	/**
	 * @generated
	 */
	private IParser goalName_5004Parser;
	
	/**
	 * @generated
	 */
	private IParser getGoalName_5004Parser()
	{
		if (goalName_5004Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			goalName_5004Parser = parser;
		}
		return goalName_5004Parser;
	}
	
	/**
	 * @generated
	 */
	private IParser activationEdgeOrder_6001Parser;
	
	/**
	 * @generated
	 */
	private IParser getActivationEdgeOrder_6001Parser()
	{
		if (activationEdgeOrder_6001Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getActivationEdge_Order() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getActivationEdge_Order() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			activationEdgeOrder_6001Parser = parser;
		}
		return activationEdgeOrder_6001Parser;
	}
	
	/**
	 * @generated
	 */
	protected IParser getParser(int visualID)
	{
		switch (visualID)
		{
			case ActivationPlanNameEditPart.VISUAL_ID:
				return getActivationPlanName_5001Parser();
			case SubProcessNameEditPart.VISUAL_ID:
				return getSubProcessName_5002Parser();
			case BpmnPlanNameEditPart.VISUAL_ID:
				return getBpmnPlanName_5003Parser();
			case GoalNameEditPart.VISUAL_ID:
				return getGoalName_5004Parser();
			case ActivationEdgeOrderEditPart.VISUAL_ID:
				return getActivationEdgeOrder_6001Parser();
		}
		return null;
	}
	
	/**
	 * Utility method that consults ParserService
	 * @generated
	 */
	public static IParser getParser(IElementType type, EObject object,
			String parserHint)
	{
		return ParserService.getInstance().getParser(
				new HintAdapter(type, object, parserHint));
	}
	
	/**
	 * @generated
	 */
	public IParser getParser(IAdaptable hint)
	{
		String vid = (String) hint.getAdapter(String.class);
		if (vid != null)
		{
			return getParser(GpmnVisualIDRegistry.getVisualID(vid));
		}
		View view = (View) hint.getAdapter(View.class);
		if (view != null)
		{
			return getParser(GpmnVisualIDRegistry.getVisualID(view));
		}
		return null;
	}
	
	/**
	 * @generated
	 */
	public boolean provides(IOperation operation)
	{
		if (operation instanceof GetParserOperation)
		{
			IAdaptable hint = ((GetParserOperation) operation).getHint();
			if (GpmnElementTypes.getElement(hint) == null)
			{
				return false;
			}
			return getParser(hint) != null;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	private static class HintAdapter extends ParserHintAdapter
	{
		
		/**
		 * @generated
		 */
		private final IElementType elementType;
		
		/**
		 * @generated
		 */
		public HintAdapter(IElementType type, EObject object, String parserHint)
		{
			super(object, parserHint);
			assert type != null;
			elementType = type;
		}
		
		/**
		 * @generated
		 */
		public Object getAdapter(Class adapter)
		{
			if (IElementType.class.equals(adapter))
			{
				return elementType;
			}
			return super.getAdapter(adapter);
		}
	}
	
}
