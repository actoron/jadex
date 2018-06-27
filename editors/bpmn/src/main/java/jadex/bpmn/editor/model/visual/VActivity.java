package jadex.bpmn.editor.model.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

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
import jadex.bpmn.model.MTask;

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
	
	/** Set of internal parameters */
	public Set<String> internalparameters = new HashSet<String>();
	
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
			ret += MTask.TASK;// MBpmnModel.TASK;
		}
		return ret;
	}
	
	/**
	 *  Returns the MActivity.
	 * 
	 *  @return The MActivity.
	 */
	public MActivity getMActivity()
	{
		return (MActivity) getBpmnElement();
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
			if (parent != null && (!(parent instanceof VElement) || ((VElement) parent).getBpmnElement() != null))
			{
				if (mactivity.isEventHandler())
				{
					MActivity mparent = (MActivity) ((VActivity) parent).getBpmnElement();
					if(mparent.getEventHandlers() == null || !mparent.getEventHandlers().contains(mactivity))
						mparent.addEventHandler(mactivity);
					mactivity.setPool(mparent.getPool());
					mactivity.setLane(mactivity.getLane());
				}
				else if (parent instanceof VLane)
				{
					MLane mlane = ((MLane) ((VLane) parent).getBpmnElement());
					if(mlane.getActivities() == null || !mlane.getActivities().contains(mactivity))
						mlane.addActivity(mactivity);
					mactivity.setLane((MLane) ((VLane) parent).getBpmnElement());
					mactivity.setPool((MPool) ((VLane) parent).getPool().getBpmnElement());
				}
				else if (parent instanceof VSubProcess)
				{
					MSubProcess msp = ((MSubProcess) ((VSubProcess) parent).getBpmnElement());
					if(msp.getActivities() == null || !msp.getActivities().contains(mactivity))
						msp.addActivity(mactivity);
					mactivity.setPool(msp.getPool());
					mactivity.setLane(msp.getLane());
				}
				else
				{
					MPool mp = ((MPool) ((VPool) parent).getBpmnElement());
					if(mp.getActivities() == null || !mp.getActivities().contains(mactivity))
						mp.addActivity(mactivity);
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
	
	/**
	 *  Set geometry.
	 */
	public void setGeometry(mxGeometry geometry)
	{
		super.setGeometry(geometry);
		refreshParameterObjectGeometry();
	}
	
	/**
	 *  Set BPMN element.
	 */
	public void setBpmnElement(MIdElement bpmnelement)
	{
		super.setBpmnElement(bpmnelement);
//		if (MBpmnModel.TASK.equals(getMActivity().getActivityType()) ||
//			MBpmnModel.SUBPROCESS.equals(getMActivity().getActivityType()))
		if (getMActivity() instanceof MTask ||
			MBpmnModel.SUBPROCESS.equals(getMActivity().getActivityType()))
		{
			createParameterObjects();
		}
	}
	
	/**
	 *  Adds a parameter to the internal parameter set.
	 *  
	 *  @param paramname The parameter name.
	 */
	public void addInternalParameter(String paramname)
	{
		MActivity mact = (MActivity) getBpmnElement();
		MParameter param = mact.getParameters().get(paramname);
		if (param != null)
		{
			internalparameters.add(paramname);
			refreshParameter(param);
		}
	}
	
	/**
	 *  Removes a parameter from the internal parameter set.
	 *  
	 *  @param paramname The parameter name.
	 */
	public void removeInternalParameter(String paramname)
	{
		MActivity mact = (MActivity) getBpmnElement();
		MParameter param = mact.getParameters().get(paramname);
		if (param != null)
		{
			internalparameters.remove(paramname);
			refreshParameter(param);
		}
	}
	
	/**
	 *  Returns if a parameter is in the internal parameter set.
	 *  
	 *  @return True, if contained.
	 */
	public boolean isInternalParameters(String paramname)
	{
		return internalparameters.contains(paramname);
	}
	
	/**
	 *  Sets the internal parameter set.
	 */
	public void setInternalParameters(Collection<String> internalparameters)
	{
		this.internalparameters.addAll(internalparameters);
	}
	
	/**
	 *  Gets the internal parameter set.
	 */
	public Set<String> getInternalParameters()
	{
		return internalparameters;
	}
	
	/**
	 *  Refresh the parameter geometry.
	 */
	public void refreshParameterObjectGeometry()
	{
		Map<String, Object[]> parametermap = new LinkedHashMap<String, Object[]>();
		for (int i = 0; i < getChildCount(); ++i)
		{
			mxICell child = getChildAt(i);
			if (child instanceof VOutParameter)
			{
				Object[] parampair = parametermap.get(((VOutParameter) child).getParameter().getName());
				if (parampair == null)
				{
					parampair = new Object[2];
					parametermap.put(((VOutParameter) child).getParameter().getName(), parampair);
				}
				parampair[1] = child;
			}
			else if (child instanceof VInParameter)
			{
				Object[] parampair = parametermap.get(((VInParameter) child).getParameter().getName());
				if (parampair == null)
				{
					parampair = new Object[2];
					parametermap.put(((VInParameter) child).getParameter().getName(), parampair);
				}
				parampair[0] = child;
			}
		}
		
		double height = getGeometry().getHeight();
		int firstsize = (int) Math.ceil(parametermap.size() * 0.5);
		int secondsize = parametermap.size() - firstsize;
		double padding = height * 0.5 - (firstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		padding /=firstsize + 1;
		double padding2 = height * 0.5 - (secondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		padding2 /= secondsize + 1;
		
		double inposx = 0.0;
		double outposx = getGeometry().getWidth() - BpmnStylesheetColor.PARAMETER_PORT_SIZE;
		double posy = 0.0;
		double posy2 = height * 0.5;
		
		for (Object[] pair : parametermap.values())
		{
			
			if (firstsize == 0)
			{
				posy = posy2;
				padding = padding2;
			}
			
			VInParameter inparam = (VInParameter) pair[0];
			VOutParameter outparam = (VOutParameter) pair[1];
			
			posy += padding;
			
			if (inparam != null)
			{
				inparam.getGeometry().setX(inposx);
				inparam.getGeometry().setY(posy);
			
				insert(inparam);
			}
			
			if (outparam != null)
			{
				outparam.getGeometry().setX(outposx);
				outparam.getGeometry().setY(posy);
				
				insert(outparam);
			}
			
			posy += BpmnStylesheetColor.PARAMETER_PORT_SIZE;
			
			--firstsize;
		}
		
//		List<VOutParameter> outparameters = new ArrayList<VOutParameter>();
//		List<VInParameter> inparameters = new ArrayList<VInParameter>();
//		
//		for (int i = 0; i < getChildCount(); ++i)
//		{
//			mxICell child = getChildAt(i);
//			if (child instanceof VOutParameter)
//			{
//				outparameters.add((VOutParameter) child);
//			}
//			else if (child instanceof VInParameter)
//			{
//				inparameters.add((VInParameter) child);
//			}
//		}
//		
//		double height = getGeometry().getHeight();
//		int outfirstsize = (int) Math.ceil(outparameters.size() * 0.5);
//		int outsecondsize = outparameters.size() - outfirstsize;
//		double outpadding = height * 0.5 - (outfirstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
//		outpadding /= outfirstsize + 1;
//		double outpadding2 = height * 0.5 - (outsecondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
//		outpadding2 /= outsecondsize + 1;
//		
//		double outposx = getGeometry().getWidth() - BpmnStylesheetColor.PARAMETER_PORT_SIZE;
//		double outposy = 0.0;
//		double outposy2 = height * 0.5;
//		
//		for (VOutParameter vparam : outparameters)
//		{
//			if (outfirstsize == 0)
//			{
//				outposy = outposy2;
//				outpadding = outpadding2;
//			}
//			
//			outposy += outpadding;
//			vparam.getGeometry().setX(outposx);
//			vparam.getGeometry().setY(outposy);
//			outposy += BpmnStylesheetColor.PARAMETER_PORT_SIZE;
//			
//			insert(vparam);
//			
//			--outfirstsize;
//		}
//		
//		int infirstsize = (int) Math.ceil(inparameters.size() * 0.5);
//		int insecondsize = inparameters.size() - infirstsize;
//		double inpadding = height * 0.5 - (infirstsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
//		inpadding /= infirstsize + 1;
//		double inpadding2 = height * 0.5 - (insecondsize * BpmnStylesheetColor.PARAMETER_PORT_SIZE);
//		inpadding2 /= insecondsize + 1;
//		
//		double inposx = 0.0;
//		double inposy = 0.0;
//		double inposy2 = height * 0.5;
//		
//		for (VInParameter vparam : inparameters)
//		{
//			if (infirstsize == 0)
//			{
//				inposy = inposy2;
//				inpadding = inpadding2;
//			}
//			
//			inposy += inpadding;
//			vparam.getGeometry().setX(inposx);
//			vparam.getGeometry().setY(inposy);
//			inposy += BpmnStylesheetColor.PARAMETER_PORT_SIZE;
//			
//			insert(vparam);
//			
//			--infirstsize;
//		}
//		
//		if (graph != null)
//		{
//			((BpmnGraph) graph).refreshCellView(this);
//		}
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
	 *  Called when a parameters are removed.
	 *  
	 *  @param param The parameters.
	 */
	public void removedParameter(Set<MParameter> params)
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
				
				if (params.contains(cparam))
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
		refreshParameterObjectGeometry();
		((BpmnGraph) graph).refreshCellView(this);
	}
	
	/**
	 *  Called when a parameters need to be refreshed.
	 *  
	 *  @param rparam The parameter.
	 */
	public void refreshParameter(MParameter rparam)
	{
		boolean expectedin = !(MParameter.DIRECTION_OUT.equals(rparam.getDirection()) || isInternalParameters(rparam.getName()));
		boolean expectedout = !(MParameter.DIRECTION_IN.equals(rparam.getDirection()));
		boolean noin = true;
		boolean noout = true;
		for (int i = 0; i < getChildCount(); ++i)
		{
			mxICell child = getChildAt(i);
			if (child instanceof VInParameter || 
				child instanceof VOutParameter)
			{
				MParameter cparam = null;
				boolean isinparam = false;
				if (child instanceof VOutParameter)
				{
					cparam = ((VOutParameter) child).getParameter();
				}
				else
				{
					cparam = ((VInParameter) child).getParameter();
					isinparam = true;
				}
				
				if (rparam.getName().equals(cparam.getName()))
				{
					if ((isinparam && !expectedin) ||
						(!isinparam && !expectedout))
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
					else
					{
						if (isinparam)
						{
							noin = false;
						}
						
						if (!isinparam)
						{
							noout = false;
						}
					}
				}
			}
		}
		
		if (noin && expectedin)
		{
			VInParameter vparam = new VInParameter(graph, rparam);
			insert(vparam);
		}
		
		if (noout && expectedout)
		{
			VOutParameter vparam = new VOutParameter(graph, rparam);
			insert(vparam);
		}
		
		refreshParameterObjectGeometry();
		((BpmnGraph) graph).refreshCellView(this);
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
				if (!internalparameters.contains(param.getName()) &&
					(MParameter.DIRECTION_IN.equals(param.getDirection()) ||
					MParameter.DIRECTION_INOUT.equals(param.getDirection())))
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
