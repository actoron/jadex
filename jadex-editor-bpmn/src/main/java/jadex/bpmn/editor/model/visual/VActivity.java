package jadex.bpmn.editor.model.visual;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSubProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

/**
 *  Visual representation of an activity.
 *
 */
public class VActivity extends VNamedNode
{
	/** Map to parameter input ports. */
	public Map<String, VInParameter> inports = new HashMap<String, VInParameter>();
	
	/** Map to parameter input ports. */
	public Map<String, VOutParameter> outports = new HashMap<String, VOutParameter>();
	
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
	
	/**
	 *  Gets the port for an input parameter.
	 *  
	 *  @param paramname The parameter name.
	 *  @return The port.
	 */
	public VInParameter getInputParameterPort(String paramname)
	{
		return inports.get(paramname);
	}
	
	/**
	 *  Gets the port for an output parameter.
	 *  
	 *  @param paramname The parameter name.
	 *  @return The port.
	 */
	public VOutParameter getOutputParameterPort(String paramname)
	{
		return outports.get(paramname);
	}
	
	public void setGeometry(mxGeometry geometry)
	{
		super.setGeometry(geometry);
		refreshParameterObjectGeometry();
	}
	
	public void setBpmnElement(MIdElement bpmnelement)
	{
		super.setBpmnElement(bpmnelement);
		createParameterObjects();
	}
	
	public void refreshParameterObjectGeometry()
	{
		List<VOutParameter> outparameters = new ArrayList<VOutParameter>();
		List<VInParameter> inparameters = new ArrayList<VInParameter>();
		
		for (int i = 0; i < getChildCount(); ++i)
		{
			mxICell child = getChildAt(i);
			if (child instanceof VOutParameter)
			{
				outparameters.add((VOutParameter) child);
			}
			else if (child instanceof VInParameter)
			{
				inparameters.add((VInParameter) child);
			}
		}
		
		double height = getGeometry().getHeight();
		int outfirstsize = (int) Math.ceil(outparameters.size() * 0.5);
		int outsecondsize = outparameters.size() - outfirstsize;
		double outpadding = height * 0.5 - (outfirstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		outpadding /= outfirstsize + 1;
		double outpadding2 = height * 0.5 - (outsecondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		outpadding2 /= outsecondsize + 1;
		
		double outposx = getGeometry().getWidth() - BpmnStylesheetColor.PARAMETER_PORT_SIZE;
		double outposy = 0.0;
		double outposy2 = height * 0.5;
		
		for (VOutParameter vparam : outparameters)
		{
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
		
		int infirstsize = (int) Math.ceil(inparameters.size() * 0.5);
		int insecondsize = inparameters.size() - infirstsize;
		double inpadding = height * 0.5 - (infirstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		inpadding /= infirstsize + 1;
		double inpadding2 = height * 0.5 - (insecondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		inpadding2 /= insecondsize + 1;
		
		double inposx = 0.0;
		double inposy = 0.0;
		double inposy2 = height * 0.5;
		
		for (VInParameter vparam : inparameters)
		{
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
		
		if (graph != null)
		{
			((BpmnGraph) graph).refreshCellView(this);
		}
	}
	
	/**
	 *  Called when a parameter is added.
	 *  
	 *  @param param The parameter.
	 */
	public void addedParameter(MParameter param)
	{
		if (MParameter.DIRECTION_OUT.equals(param.getDirection()) ||
			MParameter.DIRECTION_INOUT.equals(param.getDirection()))
		{
			VOutParameter vparam = new VOutParameter(graph, param);
			insert(vparam);
		}
		if (MParameter.DIRECTION_IN.equals(param.getDirection()) ||
			MParameter.DIRECTION_INOUT.equals(param.getDirection()))
		{
			VInParameter vparam = new VInParameter(graph, param);
			insert(vparam);
		}
		
		refreshParameterObjectGeometry();
		
		((BpmnGraph) graph).refreshCellView(this);
	}
	
	/**
	 *  Called when a parameter is removed.
	 *  
	 *  @param param The parameter.
	 */
	public void removedParameter(MParameter param)
	{
		for (int i = 0; i < getChildCount(); ++i)
		{
			mxICell child = getChildAt(i);
			if (child instanceof VOutParameter ||
				child instanceof VInParameter)
			{
				MParameter cparam = null;
				if (child instanceof VOutParameter)
				{
					cparam = ((VOutParameter) child).getParameter();
				}
				else
				{
					cparam = ((VInParameter) child).getParameter();
				}
				
				if (param.equals(cparam))
				{
					List<mxICell> edges = new ArrayList<mxICell>();
					for (int ei = 0; ei < child.getEdgeCount(); ++ei)
					{
						edges.add(child.getEdgeAt(ei));
					}
					graph.removeCells(edges.toArray());
					
					remove(i);
					--i;	
				}
			}
		}
	}
	
	protected void createParameterObjects()
	{
		for (int i = 0; i < getChildCount(); ++i)
		{
			mxICell child = getChildAt(i);
			if (child instanceof VOutParameter ||
				child instanceof VInParameter)
			{
				List<mxICell> edges = new ArrayList<mxICell>();
				for (int ei = 0; ei < child.getEdgeCount(); ++ei)
				{
					edges.add(child.getEdgeAt(ei));
				}
				graph.removeCells(edges.toArray());
				
				remove(i);
				--i;
			}
		}
		
		inports.clear();
		outports.clear();
		
		MActivity mactivity = (MActivity) getBpmnElement();
		if (mactivity != null && mactivity.getParameters() != null)
		{
			List<MParameter> params = mactivity.getParameters().getAsList();
			
			for (MParameter param : params)
			{
				if (MParameter.DIRECTION_OUT.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					VOutParameter vparam = new VOutParameter(graph, param);
					insert(vparam);
					outports.put(param.getName(), vparam);
				}
				if (MParameter.DIRECTION_IN.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					VInParameter vparam = new VInParameter(graph, param);
					insert(vparam);
					inports.put(param.getName(), vparam);
				}
			}
			
			refreshParameterObjectGeometry();
		}
	}
}
