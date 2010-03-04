/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.edit.parts.AchieveGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.DataObjectNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnEdgeNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnElementNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MaintainGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessageGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessagingEdgeNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ParallelGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PerformGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ProcessNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.QueryGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SequentialGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeSequentialOrderEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessGoalNameEditPart;
import jadex.tools.gpmn.diagram.edit.parts.TextAnnotationNameEditPart;
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
	private IParser processName_5001Parser;

	/**
	 * @generated
	 */
	private IParser getProcessName_5001Parser()
	{
		if (processName_5001Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			processName_5001Parser = parser;
		}
		return processName_5001Parser;
	}

	/**
	 * @generated
	 */
	private IParser achieveGoalName_5002Parser;

	/**
	 * @generated
	 */
	private IParser getAchieveGoalName_5002Parser()
	{
		if (achieveGoalName_5002Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			achieveGoalName_5002Parser = parser;
		}
		return achieveGoalName_5002Parser;
	}

	/**
	 * @generated
	 */
	private IParser maintainGoalName_5003Parser;

	/**
	 * @generated
	 */
	private IParser getMaintainGoalName_5003Parser()
	{
		if (maintainGoalName_5003Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			maintainGoalName_5003Parser = parser;
		}
		return maintainGoalName_5003Parser;
	}

	/**
	 * @generated
	 */
	private IParser performGoalName_5004Parser;

	/**
	 * @generated
	 */
	private IParser getPerformGoalName_5004Parser()
	{
		if (performGoalName_5004Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			performGoalName_5004Parser = parser;
		}
		return performGoalName_5004Parser;
	}

	/**
	 * @generated
	 */
	private IParser queryGoalName_5005Parser;

	/**
	 * @generated
	 */
	private IParser getQueryGoalName_5005Parser()
	{
		if (queryGoalName_5005Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			queryGoalName_5005Parser = parser;
		}
		return queryGoalName_5005Parser;
	}

	/**
	 * @generated
	 */
	private IParser sequentialGoalName_5006Parser;

	/**
	 * @generated
	 */
	private IParser getSequentialGoalName_5006Parser()
	{
		if (sequentialGoalName_5006Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			sequentialGoalName_5006Parser = parser;
		}
		return sequentialGoalName_5006Parser;
	}

	/**
	 * @generated
	 */
	private IParser parallelGoalName_5007Parser;

	/**
	 * @generated
	 */
	private IParser getParallelGoalName_5007Parser()
	{
		if (parallelGoalName_5007Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			parallelGoalName_5007Parser = parser;
		}
		return parallelGoalName_5007Parser;
	}

	/**
	 * @generated
	 */
	private IParser messageGoalName_5008Parser;

	/**
	 * @generated
	 */
	private IParser getMessageGoalName_5008Parser()
	{
		if (messageGoalName_5008Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			messageGoalName_5008Parser = parser;
		}
		return messageGoalName_5008Parser;
	}

	/**
	 * @generated
	 */
	private IParser subProcessGoalName_5009Parser;

	/**
	 * @generated
	 */
	private IParser getSubProcessGoalName_5009Parser()
	{
		if (subProcessGoalName_5009Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			subProcessGoalName_5009Parser = parser;
		}
		return subProcessGoalName_5009Parser;
	}

	/**
	 * @generated
	 */
	private IParser planName_5010Parser;

	/**
	 * @generated
	 */
	private IParser getPlanName_5010Parser()
	{
		if (planName_5010Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			planName_5010Parser = parser;
		}
		return planName_5010Parser;
	}

	/**
	 * @generated
	 */
	private IParser textAnnotationName_5012Parser;

	/**
	 * @generated
	 */
	private IParser getTextAnnotationName_5012Parser()
	{
		if (textAnnotationName_5012Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			textAnnotationName_5012Parser = parser;
		}
		return textAnnotationName_5012Parser;
	}

	/**
	 * @generated
	 */
	private IParser dataObjectName_5013Parser;

	/**
	 * @generated
	 */
	private IParser getDataObjectName_5013Parser()
	{
		if (dataObjectName_5013Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			dataObjectName_5013Parser = parser;
		}
		return dataObjectName_5013Parser;
	}

	/**
	 * @generated
	 */
	private IParser genericGpmnElementName_5014Parser;

	/**
	 * @generated
	 */
	private IParser getGenericGpmnElementName_5014Parser()
	{
		if (genericGpmnElementName_5014Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			genericGpmnElementName_5014Parser = parser;
		}
		return genericGpmnElementName_5014Parser;
	}

	/**
	 * @generated
	 */
	private IParser subGoalEdgeSequentialOrder_6003Parser;

	/**
	 * @generated
	 */
	private IParser getSubGoalEdgeSequentialOrder_6003Parser()
	{
		if (subGoalEdgeSequentialOrder_6003Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getSubGoalEdge_SequentialOrder() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getSubGoalEdge_SequentialOrder() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			subGoalEdgeSequentialOrder_6003Parser = parser;
		}
		return subGoalEdgeSequentialOrder_6003Parser;
	}

	/**
	 * @generated
	 */
	private IParser messagingEdgeName_6001Parser;

	/**
	 * @generated
	 */
	private IParser getMessagingEdgeName_6001Parser()
	{
		if (messagingEdgeName_6001Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			messagingEdgeName_6001Parser = parser;
		}
		return messagingEdgeName_6001Parser;
	}

	/**
	 * @generated
	 */
	private IParser genericGpmnEdgeName_6004Parser;

	/**
	 * @generated
	 */
	private IParser getGenericGpmnEdgeName_6004Parser()
	{
		if (genericGpmnEdgeName_6004Parser == null)
		{
			EAttribute[] features = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			EAttribute[] editableFeatures = new EAttribute[] { GpmnPackage.eINSTANCE
					.getNamedObject_Name() };
			MessageFormatParser parser = new MessageFormatParser(features,
					editableFeatures);
			genericGpmnEdgeName_6004Parser = parser;
		}
		return genericGpmnEdgeName_6004Parser;
	}

	/**
	 * @generated
	 */
	protected IParser getParser(int visualID)
	{
		switch (visualID)
		{
			case ProcessNameEditPart.VISUAL_ID:
				return getProcessName_5001Parser();
			case AchieveGoalNameEditPart.VISUAL_ID:
				return getAchieveGoalName_5002Parser();
			case MaintainGoalNameEditPart.VISUAL_ID:
				return getMaintainGoalName_5003Parser();
			case PerformGoalNameEditPart.VISUAL_ID:
				return getPerformGoalName_5004Parser();
			case QueryGoalNameEditPart.VISUAL_ID:
				return getQueryGoalName_5005Parser();
			case SequentialGoalNameEditPart.VISUAL_ID:
				return getSequentialGoalName_5006Parser();
			case ParallelGoalNameEditPart.VISUAL_ID:
				return getParallelGoalName_5007Parser();
			case MessageGoalNameEditPart.VISUAL_ID:
				return getMessageGoalName_5008Parser();
			case SubProcessGoalNameEditPart.VISUAL_ID:
				return getSubProcessGoalName_5009Parser();
			case PlanNameEditPart.VISUAL_ID:
				return getPlanName_5010Parser();
			case TextAnnotationNameEditPart.VISUAL_ID:
				return getTextAnnotationName_5012Parser();
			case DataObjectNameEditPart.VISUAL_ID:
				return getDataObjectName_5013Parser();
			case GenericGpmnElementNameEditPart.VISUAL_ID:
				return getGenericGpmnElementName_5014Parser();
			case SubGoalEdgeSequentialOrderEditPart.VISUAL_ID:
				return getSubGoalEdgeSequentialOrder_6003Parser();
			case MessagingEdgeNameEditPart.VISUAL_ID:
				return getMessagingEdgeName_6001Parser();
			case GenericGpmnEdgeNameEditPart.VISUAL_ID:
				return getGenericGpmnEdgeName_6004Parser();
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
