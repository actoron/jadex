package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSubProcess;

import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

/**
 *  Visual representation of an activity.
 *
 */
public class VActivity extends VNamedNode
{
	/**
	 *  Creates a new activity.
	 * 
	 *  @param graph The graph where this element is used.
	 */
	public VActivity(mxGraph graph)
	{
		super(graph, VActivity.class.getSimpleName());
		setValue("");
	}
	
	/**
	 *  Gets the style.
	 */
	public String getStyle()
	{
		String ret = VActivity.class.getSimpleName() + "_";
		if (getBpmnElement() != null)
		{
			String at = ((MActivity) getBpmnElement()).getActivityType();
			if (at.startsWith("Event"))
			{
				ret += EventShape.class.getSimpleName();
				if (at.startsWith("EventStart"))
				{
					ret += "_START";
				}
				else if (at.startsWith("EventIntermediate"))
				{
					if (((MActivity) getBpmnElement()).isEventHandler())
					{
						ret += "_BOUNDARY";
					}
					else
					{
						ret += "_INTERMEDIATE";
					}
				}
				else
				{
					ret += "_END";
				}
			}
			else
			{
				ret += at;
			}
		}
		else
		{
			ret +=  MBpmnModel.TASK;
		}
		return ret;
	}
	
	/**
	 *  Sets the parent.
	 */
	public void setParent(mxICell parent)
	{
		MActivity mactivity = (MActivity) getBpmnElement();
		if (mactivity != null)
		{
			if (getParent() != null)
			{
				VNode oldparent = (VNode) getParent();
				if (mactivity.isEventHandler())
				{
					MActivity mparent = (MActivity) ((VActivity) oldparent).getBpmnElement();
					mparent.removeEventHandler(mactivity);
					mactivity.setLane(null);
					mactivity.setPool(null);
				}
				else if (oldparent instanceof VLane)
				{
					((MLane) ((VLane) oldparent).getBpmnElement()).removeActivity(mactivity);
					mactivity.setLane(null);
					mactivity.setPool(null);
				}
				else if (oldparent instanceof VSubProcess)
				{
					MSubProcess msp = ((MSubProcess) ((VSubProcess) oldparent).getBpmnElement());
					msp.removeActivity(mactivity);
					mactivity.setLane(null);
					mactivity.setPool(null);
				}
				else
				{
					((MPool) ((VPool) oldparent).getBpmnElement()).removeActivity(mactivity);
					mactivity.setPool(null);
				}
			}
			if (parent != null)
			{
				if (mactivity.isEventHandler())
				{
					MActivity mparent = (MActivity) ((VActivity) parent).getBpmnElement();
					mparent.addEventHandler(mactivity);
					mactivity.setPool(mparent.getPool());
					mactivity.setLane(mactivity.getLane());
				}
				else if (parent instanceof VLane)
				{
					((MLane) ((VLane) parent).getBpmnElement()).addActivity(mactivity);
					mactivity.setLane((MLane) ((VLane) parent).getBpmnElement());
					mactivity.setPool((MPool) ((VLane) parent).getPool().getBpmnElement());
				}
				else if (parent instanceof VSubProcess)
				{
					MSubProcess msp = ((MSubProcess) ((VSubProcess) parent).getBpmnElement());
					msp.addActivity(mactivity);
					mactivity.setPool(msp.getPool());
					mactivity.setLane(msp.getLane());
				}
				else
				{
					((MPool) ((VPool) parent).getBpmnElement()).addActivity(mactivity);
					mactivity.setPool((MPool) ((VPool) parent).getBpmnElement());
				}
			}
		}
		super.setParent(parent);
	}
	
	public void setGeometry(mxGeometry geometry)
	{
		super.setGeometry(geometry);
		refreshParameterObjects();
	}
	
	public void refreshParameterObjects(){}
	
	public void refreshParameterObjects0()
	{
		for (int i = 0; i < getChildCount(); ++i)
		{
			mxICell child = getChildAt(i);
			if (child instanceof VOutParameter ||
				child instanceof VInParameter)
			{
				remove(i);
				--i;
			}
		}
		
		MActivity mactivity = (MActivity) getBpmnElement();
		if (mactivity != null && mactivity.getParameters() != null)
		{
			List<MParameter> params = mactivity.getParameters().getAsList();
			
			int outsize = 0;
			int insize = 0;
			for (MParameter param : params)
			{
				if (MParameter.DIRECTION_OUT.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					++outsize;
				}
				if (MParameter.DIRECTION_IN.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					++insize;
				}
			}
			
			double height = getGeometry().getHeight();
			int outfirstsize = (int) Math.ceil(outsize * 0.5);
			int outsecondsize = outsize - outfirstsize;
			double outpadding = height * 0.5 - (outfirstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
			outpadding /= outfirstsize + 1;
			double outpadding2 = height * 0.5 - (outsecondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
			outpadding2 /= outsecondsize + 1;
			
			double outposx = getGeometry().getWidth() - BpmnStylesheetColor.PARAMETER_PORT_SIZE;
			double outposy = 0.0;
			double outposy2 = height * 0.5;
			
			int infirstsize = (int) Math.ceil(insize * 0.5);
			int insecondsize = insize - infirstsize;
			double inpadding = height * 0.5 - (infirstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
			inpadding /= infirstsize + 1;
			double inpadding2 = height * 0.5 - (insecondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
			inpadding2 /= insecondsize + 1;
			
			double inposx = 0.0;
			double inposy = 0.0;
			double inposy2 = height * 0.5;
			
			for (MParameter param : params)
			{
				if (MParameter.DIRECTION_OUT.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					VOutParameter vparam = new VOutParameter(graph, param);
					
					if (outfirstsize == 0)
					{
						outposy = outposy2;
						outpadding = outpadding2;
					}
					
					outposy += outpadding;
					vparam.getGeometry().setX(outposx);
					vparam.getGeometry().setY(outposy);
					outposy += BpmnStylesheetColor.PARAMETER_PORT_SIZE;
					
					insert(vparam);
					
					--outfirstsize;
				}
				if (MParameter.DIRECTION_IN.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					VInParameter vparam = new VInParameter(graph, param);
					
					if (infirstsize == 0)
					{
						inposy = inposy2;
						inpadding = inpadding2;
					}
					
					inposy += inpadding;
					vparam.getGeometry().setX(inposx);
					vparam.getGeometry().setY(inposy);
					inposy += BpmnStylesheetColor.PARAMETER_PORT_SIZE;
					
					insert(vparam);
					
					--infirstsize;
				}
			}
			
			
			((BpmnGraph) graph).refreshCellView(this);
		}
	}
}
