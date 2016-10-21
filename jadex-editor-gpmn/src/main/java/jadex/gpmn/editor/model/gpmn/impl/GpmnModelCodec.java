package jadex.gpmn.editor.model.gpmn.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import jadex.commons.SUtil;
import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.IParameter;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.IRefPlan;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;
import jadex.gpmn.editor.model.gpmn.ModelConstants;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VElement;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;
import jadex.xml.stax.XmlUtil;

public class GpmnModelCodec extends AbstractModelCodec
{
	/** The format version */
	protected String VERSION = "3";
	
	/** Enable consistency check */
	protected static final boolean DEBUG_ENABLE_CONSISTENCY_CHECK = true;
	
	/** Carriage return. */
	private static final String CR = new String(new byte[] { (byte) 13 }, SUtil.UTF8);
	
	/** Line feed. */
	private static final String LF = new String(new byte[] { (byte) 10 }, SUtil.UTF8);
	
	/**
	 *  Creates a new model codec.
	 *  
	 *  @param model
	 */
	public GpmnModelCodec(GpmnModel model)
	{
		super(model);
	}
	
	/**
	 *  Writes the model to a file.
	 * 
	 *  @param file The target file.
	 *  @param graph The visual graph.
	 *  @param model The GPMN intermediate model.
	 */
	public void writeModel(File file, mxGraph graph) throws IOException
	{
		GpmnModel gm = (GpmnModel) model;
		
		File tmpfile = File.createTempFile("gpmnsave", ".gpmn");
		PrintStream ps = new PrintStream(tmpfile, "UTF-8");
		int ind = 0;
		printlnIndent(ps, ind, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		printlnIndent(ps, ind++, "<gpmn:gpmn xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gpmn=\"http://jadex.sourceforge.net/gpmn\" version=\"" + VERSION + "\">");
		
		printlnIndent(ps, ind++, "<gpmn:gpmnmodel>");
		
		printIndent(ps, ind, "<gpmn:modeldescription>");
		ps.print(gm.getDescription());
		ps.println("</gpmn:modeldescription>");
		
		printIndent(ps, ind, "<gpmn:modelpackage>");
		ps.print(gm.getPackage());
		ps.println("</gpmn:modelpackage>");
		
		List<IParameter> cp = gm.getContext().getParameters();
		if (cp.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:context>");
			
			for (IParameter p : cp)
			{
				printlnIndent(ps, ind++, "<gpmn:parameter>");
				
				printIndent(ps, ind, "<gpmn:parametername>");
				ps.print(p.getName());
				ps.println("</gpmn:parametername>");
				
				printIndent(ps, ind, "<gpmn:parametertype>");
				ps.print(p.getType());
				ps.println("</gpmn:parametertype>");
				
				printIndent(ps, ind, "<gpmn:parametervalue>");
				ps.print(p.getValue());
				ps.println("</gpmn:parametervalue>");
				
				if (p.isSet())
				{
					printlnIndent(ps, ind, "<gpmn:parameterset />");
				}
				
				printlnIndent(ps, --ind, "</gpmn:parameter>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:context>");
		}
		
		printlnIndent(ps, ind++, "<gpmn:goals>");
		Set<INode> goals = gm.getNodeSet(IGoal.class);
		for (INode node : goals)
		{
			Goal goal = (Goal) node;
			printlnIndent(ps, ind++, "<gpmn:goal>");
			
			printIndent(ps, ind, "<gpmn:goalname>");
			ps.print(escapeString(goal.getName()));
			ps.println("</gpmn:goalname>");
			
			if (!ModelConstants.DEFAULT_GOAL_TYPE.equals(goal.getGoalType()))
			{
				printIndent(ps, ind, "<gpmn:goaltype>");
				ps.print(goal.getGoalType());
				ps.println("</gpmn:goaltype>");
			}
			
			if (goal.getCreationCondition() != null)
			{
				printIndent(ps, ind, "<gpmn:creationcondition");
				if (goal.getCreationConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getCreationConditionLanguage());
					ps.print("\"");
				}
				ps.print(">");
				ps.print(goal.getCreationCondition());
				ps.println("</gpmn:creationcondition>");
			}
			
			if (goal.getContextCondition() != null)
			{
				printIndent(ps, ind, "<gpmn:contextcondition");
				if (goal.getContextConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getContextConditionLanguage());
					ps.print("\"");
				}
				ps.print(">");
				ps.print(goal.getContextCondition());
				ps.println("</gpmn:contextcondition>");
			}
			
			if (goal.getDropCondition() != null)
			{
				printIndent(ps, ind, "<gpmn:dropcondition");
				if (goal.getDropConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getDropConditionLanguage());
					ps.print("\"");
				}
				ps.print(">");
				ps.print(goal.getDropCondition());
				ps.println("</gpmn:dropcondition>");
			}
			
			if (goal.getTargetCondition() != null &&
				ModelConstants.ACHIEVE_GOAL_TYPE.equals(goal.getGoalType()))
			{
				printIndent(ps, ind, "<gpmn:targetcondition");
				if (goal.getTargetConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getTargetConditionLanguage());
					ps.print("\"");
				}
				ps.print(">");
				ps.print(goal.getTargetCondition());
				ps.println("</gpmn:targetcondition>");
			}
			
			if (goal.getFailureCondition() != null)
			{
				printIndent(ps, ind, "<gpmn:targetcondition");
				if (goal.getFailureConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getFailureConditionLanguage());
					ps.print("\"");
				}
				ps.print(">");
				ps.print(goal.getFailureCondition());
				ps.println("</gpmn:targetcondition>");
			}
			
			if (goal.getMaintainCondition() != null &&
				ModelConstants.MAINTAIN_GOAL_TYPE.equals(goal.getGoalType()))
			{
				printIndent(ps, ind, "<gpmn:maintaincondition");
				if (goal.getMaintainConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getMaintainConditionLanguage());
					ps.print("\"");
				}
				ps.print(">");
				ps.print(goal.getMaintainCondition());
				ps.println("</gpmn:maintaincondition>");
			}
			
			if (goal.getDeliberation() != null)
			{
				printIndent(ps, ind, "<gpmn:deliberation>");
				ps.print(goal.getDeliberation());
				ps.println("</gpmn:deliberation>");
			}
			
			if (goal.getExclude() != null)
			{
				printIndent(ps, ind, "<gpmn:exclude>");
				ps.print(goal.getExclude());
				ps.println("</gpmn:exclude>");
			}
			
			if (goal.isPostToAll())
			{
				printlnIndent(ps, ind, "<gpmn:posttoall />");
			}
			
			if (goal.isRandomSelection())
			{
				printlnIndent(ps, ind, "<gpmn:randomselection />");
			}
			
			if (goal.isRecalculate())
			{
				printlnIndent(ps, ind, "<gpmn:recalculate />");
			}
			
			if (goal.isRecur())
			{
				printlnIndent(ps, ind, "<gpmn:recur delay=\"");
				ps.print(goal.getRecurDelay());
				ps.println("\" />");
			}
			
			if (goal.isRetry())
			{
				printIndent(ps, ind, "<gpmn:retry delay=\"");
				ps.print(goal.getRetryDelay());
				ps.println("\" />");
			}
			
			printlnIndent(ps, --ind, "</gpmn:goal>");
		}
		printlnIndent(ps, --ind, "</gpmn:goals>");
		
		ps.println();
		
		printlnIndent(ps, ind++, "<gpmn:plans>");
		printlnIndent(ps, ind++, "<gpmn:activationplans>");
		Set<INode> plans = gm.getNodeSet(IActivationPlan.class);
		for (INode node : plans)
		{
			printlnIndent(ps, ind++, "<gpmn:activationplan>");
			ActivationPlan plan = (ActivationPlan) node;
			
			printIndent(ps, ind, "<gpmn:planname>");
			ps.print(escapeString(plan.getName()));
			ps.println("</gpmn:planname>");
			
			if (!ModelConstants.ACTIVATION_MODE_DEFAULT.equals(plan.getMode()))
			{
				printIndent(ps, ind, "<gpmn:activationmode>");
				ps.print(plan.getMode());
				ps.println("</gpmn:activationmode>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:activationplan>");
		}
		printlnIndent(ps, --ind, "</gpmn:activationplans>");
		
		printlnIndent(ps, ind++, "<gpmn:refplans>");
		plans = gm.getNodeSet(IRefPlan.class);
		for (INode node : plans)
		{
			printlnIndent(ps, ind++, "<gpmn:refplan>");
			RefPlan plan = (RefPlan) node;
			
			printIndent(ps, ind, "<gpmn:planname>");
			ps.print(plan.getName());
			ps.println("</gpmn:planname>");
			
			//TODO: Throw exception if planref is undefined?
			if (plan.getPlanref() != null)
			{
				printIndent(ps, ind, "<gpmn:planref>");
				ps.print(plan.getPlanref());
				ps.println("</gpmn:planref>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:refplan>");
		}
		printlnIndent(ps, --ind, "</gpmn:refplans>");
		printlnIndent(ps, --ind, "</gpmn:plans>");
		
		ps.println();
		
		printlnIndent(ps, ind++, "<gpmn:edges>");
		Set<IEdge> edges = gm.getEdgeSet(IActivationEdge.class);
		if (edges.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:activationedges>");
			for (IEdge iedge : edges)
			{
				printIndent(ps, ind++, "<gpmn:activationedge");
				ActivationEdge edge = (ActivationEdge) iedge;
				ActivationPlan aplan = (ActivationPlan) edge.getSource();
				if (ModelConstants.ACTIVATION_MODE_SEQUENTIAL.equals(aplan.getMode()))
				{
					ps.print(" order=\"");
					ps.print(edge.getOrder());
					ps.print("\"");
				}
				ps.println(">");
				
				printIndent(ps, ind, "<gpmn:edgename>");
				ps.print(edge.getName());
				ps.println("</gpmn:edgename>");
				
				printIndent(ps, ind, "<gpmn:sourcename>");
				ps.print(aplan.getName());
				ps.println("</gpmn:sourcename>");
				
				Goal goal = (Goal) edge.getTarget();
				printIndent(ps, ind, "<gpmn:targetname>");
				ps.print(goal.getName());
				ps.println("</gpmn:targetname>");
				
				printlnIndent(ps, --ind, "</gpmn:activationedge>");
			}
			printlnIndent(ps, --ind, "</gpmn:activationedges>");
		}
		
		edges = gm.getEdgeSet(IPlanEdge.class);
		if (edges.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:planedges>");
			for (IEdge iedge : edges)
			{
				PlanEdge edge = (PlanEdge) iedge;
				printlnIndent(ps, ind++, "<gpmn:planedge>");
				
				printIndent(ps, ind, "<gpmn:edgename>");
				ps.print(edge.getName());
				ps.println("</gpmn:edgename>");
				
				Goal goal = (Goal) edge.getSource();
				printIndent(ps, ind, "<gpmn:sourcename>");
				ps.print(goal.getName());
				ps.println("</gpmn:sourcename>");
				
				AbstractPlan plan = (AbstractPlan) edge.getTarget();
				printIndent(ps, ind, "<gpmn:targetname>");
				ps.print(plan.getName());
				ps.println("</gpmn:targetname>");
				
				printlnIndent(ps, --ind, "</gpmn:planedge>");
			}
			printlnIndent(ps, --ind, "</gpmn:planedges>");
		}
		
		edges = gm.getEdgeSet(ISuppressionEdge.class);
		if (edges.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:suppressionedges>");
			for (IEdge iedge : edges)
			{
				SuppressionEdge edge = (SuppressionEdge) iedge;
				printlnIndent(ps, ind++, "<gpmn:suppressionedge>");
				
				printIndent(ps, ind, "<gpmn:edgename>");
				ps.print(edge.getName());
				ps.println("</gpmn:edgename>");
				
				Goal goal = (Goal) edge.getSource();
				printIndent(ps, ind, "<gpmn:sourcename>");
				ps.print(goal.getName());
				ps.println("</gpmn:sourcename>");
				
				goal = (Goal) edge.getTarget();
				printIndent(ps, ind, "<gpmn:targetname>");
				ps.print(goal.getName());
				ps.println("</gpmn:targetname>");
				
				printlnIndent(ps, --ind, "</gpmn:suppressionedge>");
			}
			printlnIndent(ps, --ind, "</gpmn:suppressionedges>");
		}
		printlnIndent(ps, --ind, "</gpmn:edges>");
		
		printlnIndent(ps, --ind, "</gpmn:gpmnmodel>");
		
		ps.println();
		
		printlnIndent(ps, ind++, "<gpmn:visualmodel>");
		// TODO: Hack?
		Object root = ((mxCell) graph.getModel().getRoot()).getChildAt(0);
		
		List<VGoal> vgoals = new ArrayList<VGoal>();
		List<VPlan> vplans = new ArrayList<VPlan>();
		List<VEdge> vedges = new ArrayList<VEdge>();
		List<VVirtualActivationEdge> virtactedges = new ArrayList<VVirtualActivationEdge>();
		for (int i = 0; i < graph.getModel().getChildCount(root); ++i)
		{
			VElement element = (VElement) graph.getModel().getChildAt(root, i);
			
			if (element instanceof VGoal)
			{
				vgoals.add((VGoal) element);
			}
			else if (element instanceof VPlan)
			{
				vplans.add((VPlan) element);
			}
			else if (element instanceof VVirtualActivationEdge)
			{
				virtactedges.add((VVirtualActivationEdge) element);
			}
			else if (element instanceof VEdge)
			{
				vedges.add((VEdge) element);
			}
		}
		
		if (vgoals.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:vgoals>");
			
			for (VGoal goal : vgoals)
			{
				printIndent(ps, ind++, "<gpmn:vgoal x=\"");
				mxGeometry geo = goal.getGeometry();
				ps.print(geo.getX());
				ps.print("\" y=\"");
				ps.print(geo.getY());
				ps.print("\" w=\"");
				ps.print(geo.getWidth());
				ps.print("\" h=\"");
				ps.print(geo.getHeight());
				ps.println("\">");
				
				printIndent(ps, ind, "<gpmn:goalname>");
				ps.print(goal.getGoal().getName());
				ps.println("</gpmn:goalname>");
				
				printlnIndent(ps, --ind, "</gpmn:vgoal>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:vgoals>");
		}
		
		if (vplans.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:vplans>");
			
			for (VPlan plan : vplans)
			{
				printIndent(ps, ind++, "<gpmn:vplan x=\"");
				mxGeometry geo = plan.getGeometry();
				ps.print(geo.getX());
				ps.print("\" y=\"");
				ps.print(geo.getY());
				ps.print("\" w=\"");
				ps.print(geo.getWidth());
				ps.print("\" h=\"");
				ps.print(geo.getHeight());
				if (!plan.isVisible())
				{
					ps.print("\" visible=\"false");
				}
				ps.println("\">");
				
				printIndent(ps, ind, "<gpmn:planname>");
				ps.print(plan.getPlan().getName());
				ps.println("</gpmn:planname>");
				
				printlnIndent(ps, --ind, "</gpmn:vplan>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:vplans>");
		}
		
		if (vedges.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:vedges>");
			
			for (VEdge edge : vedges)
			{
				printlnIndent(ps, ind++, "<gpmn:vedge>");
				
				printIndent(ps, ind, "<gpmn:edgename>");
				ps.print(edge.getEdge().getName());
				ps.println("</gpmn:edgename>");
				
				printlnIndent(ps, --ind, "</gpmn:vedge>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:vedges>");
		}
		
		if (virtactedges.size() > 0)
		{
			printlnIndent(ps, ind++, "<gpmn:virtualactivationedges>");
			
			for (VVirtualActivationEdge edge : virtactedges)
			{
				printlnIndent(ps, ind++, "<gpmn:virtualactivationedge>");
				
				printIndent(ps, ind, "<gpmn:planname>");
				ps.print(edge.getPlan().getPlan().getName());
				ps.println("</gpmn:planname>");
				
				Goal goal = (Goal) ((VGoal) edge.getSource()).getGoal();
				printIndent(ps, ind, "<gpmn:sourcename>");
				ps.print(goal.getName());
				ps.println("</gpmn:sourcename>");
				
				goal = (Goal) ((VGoal) edge.getTarget()).getGoal();
				printIndent(ps, ind, "<gpmn:targetname>");
				ps.print(goal.getName());
				ps.println("</gpmn:targetname>");
				
				printlnIndent(ps, --ind, "</gpmn:virtualactivationedge>");
			}
			
			printlnIndent(ps, --ind, "</gpmn:virtualactivationedges>");
		}
		
		printlnIndent(ps, --ind, "</gpmn:visualmodel>");
		
		printlnIndent(ps, --ind, "</gpmn:gpmn>");
		ps.close();
		
		SUtil.moveFile(tmpfile, file);
	}
	
	/**
	 *  Loads the model from a file.
	 *  
	 *  @param file The model file.
	 *  @param graph The visual graph.
	 */
	public mxIGraphModel readModel(File file) throws Exception
	{
		Set<IElement> vlinkedelems = new HashSet<IElement>();
		List<IElement> belems = new ArrayList<IElement>();
		
		FileInputStream fis = new FileInputStream(file);
		XMLInputFactory fac = XMLInputFactory.newInstance(); 
		XMLStreamReader reader = fac.createXMLStreamReader(fis);
		model.clear();
		mxIGraphModel graphmodel = null;//new mxGraphModel();
		Object root = null;//graphmodel.getRoot();
		Object parent = null;//graphmodel.getChildAt(root, 0);
		
		Map<String, Goal> goals = new HashMap<String, Goal>();
		Map<String, AbstractPlan> plans = new HashMap<String, AbstractPlan>();
		Map<String, AbstractEdge> edges = new HashMap<String, AbstractEdge>();
		Map<String, VGoal> vgoals = new HashMap<String, VGoal>();
		Map<String, VPlan> vplans = new HashMap<String, VPlan>();
		Map<String, List<VVirtualActivationEdge>> groups = new HashMap<String, List<VVirtualActivationEdge>>();
		
		Object current = null;
		String localname = null;
		while (reader.hasNext())
		{
		    reader.next();
		    if (reader.getEventType() == XMLStreamReader.START_ELEMENT)
		    {
		    	localname = reader.getLocalName();
		    	if ("parameter".equals(localname))
		    	{
		    		current = new Parameter();
		    	}
		    	else if ("goal".equals(localname))
		    	{
		    		Goal goal = (Goal) model.createNode(IGoal.class);
		    		belems.add(goal);
		    		goal.setRetry(false);
		    		current = goal;
		    	}
		    	else if ("posttoall".equals(localname))
		    	{
		    		((Goal) current).setPostToAll(true);
		    	}
		    	else if ("randomselection".equals(localname))
		    	{
		    		((Goal) current).setRandomSelection(true);
		    	}
		    	else if ("recalculate".equals(localname))
		    	{
		    		((Goal) current).setRecalculate(true);
		    	}
		    	else if ("recur".equals(localname))
		    	{
		    		((Goal) current).setRecur(true);
		    		String delaystr = reader.getAttributeValue("", "delay");
		    		if (delaystr != null)
		    		{
		    			int delay = Integer.parseInt(delaystr);
		    			((Goal) current).setRecurDelay(delay);
		    		}
		    	}
		    	else if ("retry".equals(localname))
		    	{
		    		((Goal) current).setRetry(true);
		    		String delaystr = reader.getAttributeValue("", "delay");
		    		if (delaystr != null)
		    		{
		    			int delay = Integer.parseInt(delaystr);
		    			((Goal) current).setRetryDelay(delay);
		    		}
		    	}
		    	else if ("activationplan".equals(localname))
		    	{
		    		ActivationPlan plan = (ActivationPlan) model.createNode(IActivationPlan.class);
		    		belems.add(plan);
		    		current = plan;
		    	}
		    	else if ("refplan".equals(localname))
		    	{
		    		RefPlan plan = (RefPlan) model.createNode(IRefPlan.class);
		    		belems.add(plan);
		    		current = plan;
		    	}
		    	else if ("activationedge".equals(localname))
		    	{
		    		Object[] obj = new Object[4];
		    		obj[0] = IActivationEdge.class;
		    		String orderstr = reader.getAttributeValue("", "order");
		    		if (orderstr != null)
		    		{
		    			obj[3] = Integer.parseInt(orderstr);
		    		}
		    		current = obj;
		    	}
		    	else if ("planedge".equals(localname))
		    	{
		    		current = new Object[3];
		    		((Object[]) current)[0] = IPlanEdge.class;
		    	}
		    	else if ("suppressionedge".equals(localname))
		    	{
		    		current = new Object[3];
		    		((Object[]) current)[0] = ISuppressionEdge.class;
		    	}
		    	else if ("creationcondition".equals(localname))
		    	{
		    		Goal goal = (Goal) current;
		    		String lang = reader.getAttributeValue("", "language");
		    		if (lang != null)
		    		{
		    			goal.setCreationConditionLanguage(lang);
		    		}
		    	}
		    	else if ("contextcondition".equals(localname))
		    	{
		    		Goal goal = (Goal) current;
		    		String lang = reader.getAttributeValue("", "language");
		    		if (lang != null)
		    		{
		    			goal.setContextConditionLanguage(lang);
		    		}
		    	}
		    	else if ("dropcondition".equals(localname))
		    	{
		    		Goal goal = (Goal) current;
		    		String lang = reader.getAttributeValue("", "language");
		    		if (lang != null)
		    		{
		    			goal.setDropConditionLanguage(lang);
		    		}
		    	}
		    	else if ("targetcondition".equals(localname))
		    	{
		    		Goal goal = (Goal) current;
		    		String lang = reader.getAttributeValue("", "language");
		    		if (lang != null)
		    		{
		    			goal.setTargetConditionLanguage(lang);
		    		}
		    	}
		    	else if ("failurecondition".equals(localname))
		    	{
		    		Goal goal = (Goal) current;
		    		String lang = reader.getAttributeValue("", "language");
		    		if (lang != null)
		    		{
		    			goal.setFailureConditionLanguage(lang);
		    		}
		    	}
		    	else if ("maintaincondition".equals(localname))
		    	{
		    		Goal goal = (Goal) current;
		    		String lang = reader.getAttributeValue("", "language");
		    		if (lang != null)
		    		{
		    			goal.setMaintainConditionLanguage(lang);
		    		}
		    	}
		    	else if ("visualmodel".equals(localname))
		    	{
		    		graphmodel = new mxGraphModel();
		    		root = graphmodel.getRoot();
		    		parent = graphmodel.getChildAt(root, 0);
		    	}
		    	else if ("vgoal".equals(localname))
		    	{
		    		double x = Double.parseDouble(reader.getAttributeValue("", "x"));
		    		double y = Double.parseDouble(reader.getAttributeValue("", "y"));
		    		double w = reader.getAttributeValue("", "w")!= null?Double.parseDouble(reader.getAttributeValue("", "w")):GuiConstants.DEFAULT_PLAN_WIDTH;
		    		double h = reader.getAttributeValue("", "h")!= null?Double.parseDouble(reader.getAttributeValue("", "h")):GuiConstants.DEFAULT_PLAN_HEIGHT;
		    		VGoal vgoal = new VGoal(null, new mxPoint(x, y));
		    		vgoal.setGeometry(new mxGeometry(x, y, w, h));
		    		current = vgoal;
		    	}
		    	else if ("vplan".equals(localname))
		    	{
		    		Object[] obj = new Object[3];
		    		obj[0] = "vplan";
		    		double x = Double.parseDouble(reader.getAttributeValue("", "x"));
		    		double y = Double.parseDouble(reader.getAttributeValue("", "y"));
		    		double w = reader.getAttributeValue("", "w")!= null?Double.parseDouble(reader.getAttributeValue("", "w")):GuiConstants.DEFAULT_PLAN_WIDTH;
		    		double h = reader.getAttributeValue("", "h")!= null?Double.parseDouble(reader.getAttributeValue("", "h")):GuiConstants.DEFAULT_PLAN_HEIGHT;
		    		obj[1] = new mxGeometry(x, y, w, h);
		    		String visiblestr = reader.getAttributeValue("", "visible");
		    		if (visiblestr != null)
		    		{
		    			obj[3] = (Boolean.parseBoolean(visiblestr));
		    		}
		    		current = obj;
		    	}
		    	else if ("vedge".equals(localname))
		    	{
		    		current = "vedge";
		    	}
		    	else if ("virtualactivationedge".equals(localname))
		    	{
		    		current = new Object[3];
		    		((Object[]) current)[0] = "virtualactivationedge";
		    	}
		    }
		    else if (reader.getEventType() == XMLStreamReader.END_ELEMENT)
		    {
		    	if ("parameter".equals(reader.getLocalName()))
		    	{
		    		model.getContext().addParameter((Parameter) current);
		    	}
		    	localname = null;
		    }
		    else if (reader.getEventType() == XMLStreamReader.CHARACTERS)
		    {
		    	if ("modeldescription".equals(localname))
		    	{
		    		model.setDescription(reader.getText());
		    	}
		    	else if ("modelpackage".equals(localname))
		    	{
		    		model.setPackage(reader.getText());
		    	}
		    	else if ("parametername".equals(localname))
		    	{
		    		String name = reader.getText();
		    		((Parameter) current).setName(name);
		    	}
		    	else if ("parametertype".equals(localname))
		    	{
		    		String type = reader.getText();
		    		((Parameter) current).setType(type);
		    	}
		    	else if ("parametervalue".equals(localname))
		    	{
		    		String val = reader.getText();
		    		((Parameter) current).setValue(val);
		    	}
		    	else if ("parameterset".equals(localname))
		    	{
		    		((Parameter) current).setSet(true);
		    	}
		    	else if ("goalname".equals(localname))
		    	{
		    		String name = XmlUtil.unescapeString(reader.getText());
		    		if (current instanceof Goal)
		    		{
			    		Goal goal = (Goal) current;
			    		goal.setName(name);
			    		goals.put(goal.getName(), goal);
		    		}
		    		else if (current instanceof VGoal)
		    		{
		    			VGoal vgoal = (VGoal) current;
		    			Goal goal = goals.get(name);
		    			vlinkedelems.add(goal);
		    			vgoal.setValue(goal);
		    			vgoals.put(name, vgoal);
		    			graphmodel.beginUpdate();
		    			graphmodel.add(parent, vgoal, graphmodel.getChildCount(parent));
		    			graphmodel.endUpdate();
		    		}
		    	}
		    	else if ("goaltype".equals(localname))
		    	{
		    		String goaltype = reader.getText();
		    		//TODO: Catch invalid types
		    		Goal goal = (Goal) current;
		    		goal.setGoalType(goaltype);
		    	}
		    	else if ("creationcondition".equals(localname))
		    	{
		    		String condition = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setCreationCondition(condition);
		    	}
		    	else if ("contextcondition".equals(localname))
		    	{
		    		String condition = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setContextCondition(condition);
		    	}
		    	else if ("dropcondition".equals(localname))
		    	{
		    		String condition = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setDropCondition(condition);
		    	}
		    	else if ("targetcondition".equals(localname))
		    	{
		    		String condition = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setTargetCondition(condition);
		    	}
		    	else if ("failurecondition".equals(localname))
		    	{
		    		String condition = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setFailureCondition(condition);
		    	}
		    	else if ("maintaincondition".equals(localname))
		    	{
		    		String condition = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setMaintainCondition(condition);
		    	}
		    	else if ("deliberation".equals(localname))
		    	{
		    		String val = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setDeliberation(val);
		    	}
		    	else if ("exclude".equals(localname))
		    	{
		    		String val = reader.getText();
		    		Goal goal = (Goal) current;
		    		goal.setExclude(val);
		    	}
		    	else if ("planname".equals(localname))
		    	{
		    		String name = XmlUtil.unescapeString(reader.getText());
		    		if (current instanceof AbstractPlan)
		    		{
			    		AbstractPlan plan = (AbstractPlan) current;
			    		plan.setName(name);
			    		plans.put(plan.getName(), plan);
		    		}
		    		else if (current instanceof Object[] && "vplan".equals(((Object[]) current)[0]))
		    		{
		    			Object[] obj = (Object[]) current;
		    			mxGeometry pos = (mxGeometry) obj[1];
		    			AbstractPlan plan = plans.get(name);
		    			VPlan vplan = new VPlan(plan, pos.getX(), pos.getY());
		    			vplan.setGeometry(pos);
		    			vlinkedelems.add(plan);
		    			if (obj[2] != null)
		    			{
		    				vplan.setVisible(((Boolean) obj[2]).booleanValue());
		    			}
		    			
		    			vplans.put(name, vplan);
		    			graphmodel.beginUpdate();
		    			graphmodel.add(parent, vplan, graphmodel.getChildCount(parent));
		    			graphmodel.endUpdate();
		    			/*graph.getModel().beginUpdate();
		    			graph.addCell(vplan);
		    			graph.getModel().endUpdate();*/
		    		}
		    		else if (current instanceof Object[] && "virtualactivationedge".equals(((Object[]) current)[0]))
		    		{
		    			((Object[]) current)[1] = name;
		    		}
		    	}
		    	else if ("activationmode".equals(localname))
		    	{
		    		String mode = reader.getText();
		    		((ActivationPlan) current).setMode(mode);
		    	}
		    	else if ("planref".equals(localname))
		    	{
		    		String ref = reader.getText();
		    		((RefPlan) current).setPlanref(ref);
		    	}
		    	else if ("edgename".equals(localname))
		    	{
		    		String name = reader.getText();
		    		if ("vedge".equals(current))
		    		{
		    			AbstractEdge edge = edges.get(name);
		    			String sourcename = edge.getSource().getName();
		    			String targetname = edge.getTarget().getName();
		    			VElement source = vgoals.get(sourcename);
		    			if (source == null)
		    			{
		    				source = vplans.get(sourcename);
		    			}
		    			VElement target = vgoals.get(targetname);
		    			if (target == null)
		    			{
		    				target = vplans.get(targetname);
		    			}
		    			VEdge vedge = new VEdge(source, target, edge);
		    			current = vedge;
		    			graphmodel.beginUpdate();
		    			graphmodel.add(parent, vedge, graphmodel.getChildCount(parent));
		    			graphmodel.endUpdate();
		    			//graph.addCell(vedge);
		    		}
		    		else if (current instanceof Object[])
		    		{
		    			((Object[]) current)[1] = name;
		    		}
		    	}
		    	else if ("sourcename".equals(localname))
		    	{
		    		String name = reader.getText();
		    		((Object[]) current)[2] = name;
		    	}
		    	else if ("targetname".equals(localname))
		    	{
		    		Object[] obj = (Object[]) current;
		    		String targetname = reader.getText();
		    		String sourcename = (String) obj[2];
		    		if (current instanceof Object[] && "virtualactivationedge".equals(((Object[]) current)[0]))
		    		{
		    			String planname = (String) obj[1];
		    			VPlan plan = vplans.get(planname);
		    			VElement source = vgoals.get(sourcename);
		    			if (source == null)
		    			{
		    				source = vplans.get(sourcename);
		    			}
		    			VElement target = vgoals.get(targetname);
		    			if (target == null)
		    			{
		    				target = vplans.get(targetname);
		    			}
		    			
		    			List<VVirtualActivationEdge> group = groups.get(planname);
		    			if (group == null)
		    			{
		    				group = new ArrayList<VVirtualActivationEdge>();
		    				groups.put(planname, group);
		    			}
		    			
		    			VVirtualActivationEdge edge = new VVirtualActivationEdge(source, target, group, plan);
		    			group.add(edge);
		    			
		    			graphmodel.beginUpdate();
		    			graphmodel.add(parent, edge, graphmodel.getChildCount(parent));
		    			graphmodel.endUpdate();
		    			/*graph.getModel().beginUpdate();
		    			graph.addCell(edge);
		    			graph.getModel().endUpdate();*/
		    		}
		    		else
		    		{
			    		IElement source = goals.get(sourcename);
			    		if (source == null)
			    		{
			    			source = plans.get(sourcename);
			    		}
			    		IElement target = goals.get(targetname);
			    		if (target == null)
			    		{
			    			target = plans.get(targetname);
			    		}
			    		IEdge edge = null;
			    		if (source != null && target != null)
			    		{
				    		edge = model.createEdge(source, target, (Class) obj[0]);
				    		String name = (String) obj[1];
				    		edge.setName(name);
				    		edges.put(name, (AbstractEdge) edge);
				    		if (edge instanceof ActivationEdge && obj[3] != null)
				    		{
				    			((ActivationEdge) edge).setOrder(((Integer) obj[3]).intValue());
				    		}
			    		}
			    		else
			    		{
			    			System.err.println("Dropping unconnected edge with source/target: " + source + " " + target);
			    		}
		    		}
		    	}
		    }
		}
		
		if (DEBUG_ENABLE_CONSISTENCY_CHECK)
		{
			for (IElement elem : belems)
			{
				if (!(elem instanceof IEdge) && !vlinkedelems.contains(elem))
				{
					System.err.println("Consistency check removed: " + elem);
					model.removeNode((INode) elem);
				}
			}
		}
		
		return graphmodel;
	}
	
	/**
	 *  Escapes strings for xml.
	 */
	private static final String escapeString(String string)
	{
//		if(string==null)
//			System.out.println("nullnull");
		string = string.replace("&", "&amp;");
		string = string.replace("\"", "&quot;");
		string = string.replace("'", "&apos;");
		string = string.replace("<", "&lt;");
		string = string.replace(">", "&gt;");
		string = string.replace("\\", "\\\\");
		string = string.replace(CR + LF, LF);
		string = string.replace(CR + CR, LF);
		string = string.replace(CR, LF);
		string = string.replace(LF, "\\n");
		string = string.replace("\n", "\\n");
		return string;
	}
}
