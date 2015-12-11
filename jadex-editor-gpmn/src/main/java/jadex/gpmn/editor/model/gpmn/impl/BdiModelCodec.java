package jadex.gpmn.editor.model.gpmn.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.IParameter;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.IRefPlan;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

/**
 * Codec for generating BDI agent models.
 * 
 */
public class BdiModelCodec extends AbstractModelCodec
{
	/**
	 * Creates a new model codec.
	 * 
	 * @param model
	 */
	public BdiModelCodec(GpmnModel model)
	{
		super(model);
	}
	
	/**
	 * Writes the model to a file.
	 * 
	 * @param file
	 *            The target file.
	 * @param graph
	 *            The visual graph.
	 * @param model
	 *            The GPMN intermediate model.
	 */
	public void writeModel(File file, mxGraph graph) throws IOException
	{
		GpmnModel gm = (GpmnModel) model;
		
		File tmpfile = File.createTempFile("bdisave", ".agent.xml");
		PrintStream ps = new PrintStream(tmpfile, "UTF-8");
		
		int ind = 0;
		
		printlnIndent(ps, ind, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		printlnIndent(ps, ind, "<!--");
		printlnIndent(ps, ind,
				gm.getDescription() == null ? "" : gm.getDescription());
		printlnIndent(ps, ind, "-->");
		printlnIndent(ps, ind++,
				"<agent xmlns=\"http://www.activecomponents.org/jadex\"");
		printlnIndent(ps, ind,
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		printlnIndent(
				ps,
				ind,
				"xsi:schemaLocation=\"http://www.activecomponents.org/jadex http://www.activecomponents.org/jadex-bdi-2.0.xsd\"");
		printIndent(ps, ind, "name=\"");
		String modelname = file.getName();
		modelname = modelname.substring(0, modelname.lastIndexOf(".agent.xml"));
		ps.print(modelname);
		ps.println("\"");
		printIndent(ps, ind, "package=\"");
		ps.print(gm.getPackage() == null ? "" : gm.getPackage());
		ps.println("\">");
		
		ps.println();
		
		// Beliefs/Context
		List<IParameter> params = gm.getContext().getParameters();
		if (params.size() > 0)
		{
			printlnIndent(ps, ind++, "<beliefs>");
			for (IParameter param : params)
			{
				if (param.isSet())
				{
					printIndent(ps, ind++, "<beliefset name=\"");
				}
				else
				{
					printIndent(ps, ind++, "<belief name=\"");
				}
				
				ps.print(param.getName());
				ps.print("\" class=\"");
				ps.print(param.getType());
				ps.println("\">");
				
				printIndent(ps, ind, "<fact>");
				ps.print(param.getValue());
				ps.println("</fact>");
				
				if (param.isSet())
				{
					printlnIndent(ps, --ind, "</beliefset>");
				}
				else
				{
					printlnIndent(ps, --ind, "</belief>");
				}
			}
			
			printlnIndent(ps, --ind, "</beliefs>");
		
			ps.println();
		}
		
		Map<String, List<String>> inhibitions = new HashMap<String, List<String>>();
		Set<IEdge> edges = gm.getEdgeSet(ISuppressionEdge.class);
		for (IEdge edge : edges)
		{
			List<String> targets = inhibitions.get(edge.getSource().getName());
			if (targets == null)
			{
				targets = new ArrayList<String>();
				inhibitions.put(edge.getSource().getName(), targets);
			}
			targets.add(edge.getTarget().getName());
		}
		
		List<String> initialgoals = new ArrayList<String>();
		List<String> initialmgoals = new ArrayList<String>();
		
		printlnIndent(ps, ind++, "<goals>");
		
		Set<INode> nodes = gm.getNodeSet(IGoal.class);
		for (INode node : nodes)
		{
			Goal goal = (Goal) node;
			
			boolean initial = true;
			for (IEdge tedge : goal.getTargetEdges())
			{
				if (tedge instanceof IActivationEdge)
				{
					initial = false;
					break;
				}
			}
			
			String goaltypename = null;
			if (ModelConstants.ACHIEVE_GOAL_TYPE.equals(goal.getGoalType()))
			{
				goaltypename = "achievegoal";
				if (initial)
				{
					initialgoals.add(goal.getName());
				}
			}
			else if (ModelConstants.PERFORM_GOAL_TYPE
					.equals(goal.getGoalType()))
			{
				goaltypename = "performgoal";
				if (initial)
				{
					initialgoals.add(goal.getName());
				}
			}
			else if (ModelConstants.MAINTAIN_GOAL_TYPE.equals(goal
					.getGoalType()))
			{
				goaltypename = "maintaingoal";
				if (initial)
				{
					initialmgoals.add(goal.getName());
				}
			}
			else if (ModelConstants.QUERY_GOAL_TYPE.equals(goal.getGoalType()))
			{
				goaltypename = "querygoal";
			}
			
			printIndent(ps, ind++, "<");
			ps.print(goaltypename);
			ps.print(" name =\"");
			
			ps.print(goal.getName());
			ps.print("\"");
			
			if (goal.getExclude() != null && !ModelConstants.DEFAULT_EXCLUDE.equals(goal.getExclude()))
			{
				ps.print(" exclude=\"");
				ps.print(goal.getExclude().replaceAll(" ", "_"));
				ps.print("\"");
			}
			
			ps.println(">");
			
			List<String> inhtgts = inhibitions.get(goal.getName());
			if (inhtgts != null)
			{
				printlnIndent(ps, ind++, "<deliberation>");
				for (String inhtgt : inhtgts)
				{
					printIndent(ps, ind, "<inhibits ref=\"");
					ps.print(inhtgt);
					ps.println(" inhibit=\"when_in_process\" />");
				}
				printlnIndent(ps, --ind, "</deliberation>");
			}
			
			if (goal.getCreationCondition() != null)
			{
				printIndent(ps, ind++, "<creationcondition");
				if (goal.getCreationConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getContextConditionLanguage());
					ps.print("\"");
				}
				ps.println(">");
				
				printlnIndent(ps, ind, goal.getCreationCondition());
				
				printlnIndent(ps, --ind, "</creationcondition>");
			}
			
			if (goal.getContextCondition() != null)
			{
				printIndent(ps, ind++, "<contextcondition");
				if (goal.getContextConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getContextConditionLanguage());
					ps.print("\"");
				}
				ps.println(">");
				
				printlnIndent(ps, ind, goal.getContextCondition());
				
				printlnIndent(ps, --ind, "</contextcondition>");
			}
			
			if (goal.getDropCondition() != null)
			{
				printIndent(ps, ind++, "<dropcondition");
				if (goal.getDropConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getDropConditionLanguage());
					ps.print("\"");
				}
				ps.println(">");
				
				printlnIndent(ps, ind, goal.getDropCondition());
				
				printlnIndent(ps, --ind, "</dropcondition>");
			}
			
			if (ModelConstants.MAINTAIN_GOAL_TYPE.equals(goal.getGoalType())
					&& goal.getMaintainCondition() != null)
			{
				printIndent(ps, ind++, "<maintaincondition");
				if (goal.getMaintainConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getMaintainConditionLanguage());
					ps.print("\"");
				}
				ps.println(">");
				
				printlnIndent(ps, ind, goal.getMaintainCondition());
				
				printlnIndent(ps, --ind, "</maintaincondition>");
			}
			
			if (!ModelConstants.PERFORM_GOAL_TYPE.equals(goal.getGoalType())
					&& goal.getTargetCondition() != null)
			{
				printIndent(ps, ind++, "<targetcondition");
				if (goal.getTargetConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getTargetConditionLanguage());
					ps.print("\"");
				}
				ps.println(">");
				
				printlnIndent(ps, ind, goal.getTargetCondition());
				
				printlnIndent(ps, --ind, "</targetcondition>");
			}
			
			if ((ModelConstants.ACHIEVE_GOAL_TYPE.equals(goal.getGoalType()) ||
				 ModelConstants.QUERY_GOAL_TYPE.equals(goal.getGoalType())) &&
				goal.getFailureCondition() != null)
			{
				printIndent(ps, ind++, "<failurecondition");
				if (goal.getFailureConditionLanguage() != null)
				{
					ps.print(" language=\"");
					ps.print(goal.getFailureConditionLanguage());
					ps.print("\"");
				}
				ps.println(">");
				
				printlnIndent(ps, ind, goal.getFailureCondition());
				
				printlnIndent(ps, --ind, "</failurecondition>");
			}
			
			printIndent(ps, --ind, "</");
			ps.print(goaltypename);
			ps.println(">");
		}
		
		printlnIndent(ps, --ind, "</goals>");
		
		inhibitions = null;
		
		printlnIndent(ps, ind++, "<plans>");
		
		Map <String, List<String>> goalplantriggers = new HashMap<String, List<String>>();
		edges = gm.getEdgeSet(IPlanEdge.class);
		for (IEdge edge : edges)
		{
			List<String> triggergoals = goalplantriggers.get(edge.getTarget().getName());
			if (triggergoals == null)
			{
				triggergoals = new ArrayList<String>();
				goalplantriggers.put(edge.getTarget().getName(), triggergoals);
			}
			triggergoals.add(edge.getSource().getName());
		}
		
		nodes = gm.getNodeSet(IRefPlan.class);
		for (INode node : nodes)
		{
			RefPlan rp = (RefPlan) node;
			
			printIndent(ps, ind++, "<plan name=\"");
			ps.print(rp.getName());
			ps.println("\">");
			
			printIndent(ps, ind, "<body class=\"");
			ps.print(rp.getPlanref() == null? "" : rp.getPlanref());
			ps.println("\" />");
			
			List<String> triggergoals = goalplantriggers.get(rp.getName());
			if (triggergoals != null && triggergoals.size() > 0)
			{
				printlnIndent(ps, ind++, "<trigger>");
				for (String goalname : triggergoals)
				{
					printIndent(ps, ind, "<goal ref=\"");
					ps.print(goalname);
					ps.println("\" />");
				}
				printlnIndent(ps, --ind, "</trigger>");
			}
			
			printlnIndent(ps, --ind, "</plan>");
		}
		
		nodes = gm.getNodeSet(IActivationPlan.class);
		for (INode node : nodes)
		{
			ActivationPlan ap = (ActivationPlan) node;
			
			printIndent(ps, ind++, "<plan name=\"");
			ps.print(ap.getName());
			ps.println("\">");
			
			List<IActivationEdge> aes = ap.getActivationEdges();
			if (!aes.isEmpty())
			{
				printlnIndent(ps, ind++, "<parameter name=\"goals\" class=\"String[]\">");
				printIndent(ps, ind, "<value>new String[] {");
				
				for (int i = 0; i < aes.size(); ++i)
				{
					IActivationEdge ae = aes.get(i);
					ps.print("\"");
					ps.print(ae.getTarget().getName());
					ps.print("\"");
					if (i < aes.size() - 1)
					{
						ps.print(", ");
					}
				}
				
				ps.println("}</value>");
				printlnIndent(ps, --ind, "</parameter>");
			}
			
			printIndent(ps, ind, "<body class=\"");
			if (ModelConstants.ACTIVATION_MODE_SEQUENTIAL.equals(ap.getMode()))
			{
				ps.print(ModelConstants.ACTIVATION_PLAN_CLASS_SEQUENTIAL);
			}
			else
			{
				ps.print(ModelConstants.ACTIVATION_PLAN_CLASS_PARALLEL);
			}
			ps.println("\" />");
			
			List<String> triggergoals = goalplantriggers.get(ap.getName());
			if (triggergoals != null && triggergoals.size() > 0)
			{
				printlnIndent(ps, ind++, "<trigger>");
				for (String goalname : triggergoals)
				{
					printIndent(ps, ind, "<goal ref=\"");
					ps.print(goalname);
					ps.println("\" />");
				}
				printlnIndent(ps, --ind, "</trigger>");
			}
			
			printlnIndent(ps, --ind, "</plan>");
		}
		
		ps.println();
		
		printlnIndent(ps, ind++, "<plan name=\"GPMNStartAndMonitorPlan\">" );
		
		printlnIndent(ps, ind++, "<parameterset name=\"goals\" class=\"String\">");
		
		for (int i = 0; i < initialgoals.size(); ++i)
		{
			printIndent(ps, ind, "<value>\"");
			ps.print(initialgoals.get(i));
			ps.println("\"</value>");
		}
		
		printlnIndent(ps, --ind, "</parameterset>");
		
		printlnIndent(ps, ind++, "<parameterset name=\"maintain_goals\" class=\"String\">");
		
		for (int i = 0; i < initialmgoals.size(); ++i)
		{
			printIndent(ps, ind, "<value>\"");
			ps.print(initialmgoals.get(i));
			ps.println("\"</value>");
		}
		
		printlnIndent(ps, --ind, "</parameterset>");
		
		printIndent(ps, ind, "<body class=\"");
		ps.print(ModelConstants.INITAL_PLAN_CLASS);
		ps.println("\" />");
		
		printlnIndent(ps, --ind, "</plan>");
		
		printlnIndent(ps, --ind, "</plans>");
		
		printlnIndent(ps, ind++, "<configurations>");
		
		printlnIndent(ps, ind++, "<configuration name=\"default\">");
		printlnIndent(ps, ind++, "<plans>");
		printlnIndent(ps, ind, "<initialplan ref=\"GPMNStartAndMonitorPlan\" />");
		printlnIndent(ps, --ind, "</plans>");
		printlnIndent(ps, --ind, "</configuration>");
		
		printlnIndent(ps, --ind, "</configurations>");
		
		printlnIndent(ps, --ind, "</agent>");
		
		ps.close();
		
		tmpfile.renameTo(file);
	}
	
	/**
	 * Loads the model from a file.
	 * 
	 * @param file
	 *            The model file.
	 * @param graph
	 *            The visual graph.
	 */
	public mxIGraphModel readModel(File file) throws Exception
	{
		return null;
	}
}
