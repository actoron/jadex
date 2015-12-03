package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;

/**
 *  Factory for generating appropriate property panels.
 *
 */
public class SLegacyPropertyPanelFactory
{
	/** An empty panel. */
	public static final BasePropertyPanel EMPTY_PANEL = new BasePropertyPanel(null, null);
	
	/**
	 *  Creates a new property panel for the selected item.
	 *  
	 *  @param container The model container.
	 *  @return Property panel.
	 */
	public static BasePropertyPanel createPanel(Object selection, ModelContainer container)
	{
		BasePropertyPanel ret = EMPTY_PANEL;
		if(container.getSettings()==null || !container.getSettings().isJadexExtensions())
		{
			return ret;
		}
		
		if (selection instanceof VElement)
		{
			VElement velement = (VElement) selection;
			if (velement instanceof VPool || velement instanceof VLane)
			{
				ret = new BpmnPropertyPanel(container, selection);
			}
			else if ((velement instanceof VActivity &&
					((MActivity) velement.getBpmnElement()).getActivityType() != null &&
					((MActivity) velement.getBpmnElement()).getActivityType().matches("Event.*Error")))
			{
				ret = new ErrorEventPropertyPanel(container, (VActivity) velement);
			}
//			else if ((velement instanceof VActivity && MBpmnModel.TASK.equals(((MActivity) velement.getBpmnElement()).getActivityType())) ||
//					  (velement instanceof VSubProcess) ||
//					  (velement instanceof VExternalSubProcess) ||
//					  (velement instanceof VInParameter) ||
//					  (velement instanceof VOutParameter))
//			{
//				VActivity act = null;
//				MParameter selectedparameter = null;
//				if (velement instanceof VInParameter)
//				{
//					act = (VActivity) ((VInParameter) velement).getParent();
//					selectedparameter = ((VInParameter) velement).getParameter();
//				}
//				else if (velement instanceof VOutParameter)
//				{
//					act = (VActivity) ((VOutParameter) velement).getParent();
//					selectedparameter = ((VOutParameter) velement).getParameter();
//				}
//				else
//				{
//					act = (VActivity) velement;
//				}
//				ret = new TaskPropertyPanel(container, act, selectedparameter);
//			}
//			else if ((velement instanceof VActivity && MBpmnModel.TASK.equals(((MActivity) velement.getBpmnElement()).getActivityType())) ||
//				  (velement instanceof VSubProcess) ||
//				  (velement instanceof VExternalSubProcess) ||
//				  (velement instanceof VInParameter) ||
//				  (velement instanceof VOutParameter))
			else if (velement.getBpmnElement() instanceof MTask ||
					 (velement instanceof VSubProcess) ||
					 (velement instanceof VExternalSubProcess) ||
					 (velement instanceof VInParameter) ||
					 (velement instanceof VOutParameter))
			{
				if(velement instanceof VExternalSubProcess)
				{
					ret = new ExternalSubprocessPropertyPanel(container, selection);
				}
				else if(velement instanceof VSubProcess)
				{
					ret = new InternalSubprocessPropertyPanel(container, selection);
				}
				else
				{
					ret = new TaskPropertyPanel2(container, selection);
				}
			}
			else if (velement instanceof VActivity && ((MActivity) velement.getBpmnElement()).getActivityType().contains("Timer"))
			{
				ret = new TimerEventPropertyPanel(container, (VActivity) velement);
			}
			if (velement instanceof VActivity &&
				(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(((MActivity) velement.getBpmnElement()).getActivityType()) ||
				 MBpmnModel.EVENT_START_MESSAGE.equals(((MActivity) velement.getBpmnElement()).getActivityType()) ||
				 MBpmnModel.EVENT_END_MESSAGE.equals(((MActivity) velement.getBpmnElement()).getActivityType())))// &&
//				 ((MActivity) velement.getBpmnElement()).isThrowing())
			{
//				ret = new MessageEventPropertyPanel(container, (VActivity) velement);
				ret = new MessageEventPropertyPanel2(container, (VActivity) velement);
			}
			if (velement instanceof VActivity &&
				(MBpmnModel.EVENT_INTERMEDIATE_RULE.equals(((MActivity) velement.getBpmnElement()).getActivityType()) || 
				MBpmnModel.EVENT_START_RULE.equals(((MActivity) velement.getBpmnElement()).getActivityType())))
			{
				ret = new ECARuleEventPropertyPanel(container, (VActivity)velement);
			}
			else if(velement instanceof VActivity &&
				((MActivity)velement.getBpmnElement()).getActivityType().contains("Signal"))
			{
				if (((MActivity)velement.getBpmnElement()).isEventHandler())
				{
					ret = new SignalEventHandlerPropertyPanel(container, (VActivity)velement);
//					ret = new SignalPropertyPanel(container, (VActivity)velement);
				}
				else
				{
					ret = new SignalPropertyPanel(container, (VActivity)velement);
				}
			}
//			else if (velement instanceof VExternalSubProcess)
//			{
//				ret = new ExternalSubProcessPropertyPanel(container, (VExternalSubProcess) velement);
//			}
			else if (velement instanceof VSequenceEdge)
			{
				ret = new SequenceEdgePropertyPanel(container, (VSequenceEdge) velement);
			}
			else if (velement instanceof VDataEdge)
			{
				ret = new DataEdgePropertyPanel(container, (VDataEdge) velement);
			}
		}
		else if (selection == null)
		{
			ret = new BpmnPropertyPanel(container, selection);
		}
		
		return ret;
	}
}
