package jadex.bpmn.model.io;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;
import jadex.commons.collection.IndexMap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Reader / Writer for BPMN models.
 *
 */
public class SBpmnModelWriter
{
	/** The indentation string. */
	public static final String INDENT_STRING = "  ";
	
	/** Tag for start events. */
	public static final String START_EVENT_TAG = "startEvent";
	
	/** Tag for intermediate events. */
	public static final String INTERMEDIATE_EVENT_TAG = "intermediate";
	
	/** Tag for end events. */
	public static final String END_EVENT_TAG = "endEvent";
	
	/** Activity type mapping. */
	public static final Map<String, String> ACT_TYPE_MAPPING = new HashMap<String, String>();
	static
	{
		ACT_TYPE_MAPPING.put(MBpmnModel.TASK, "task");
		ACT_TYPE_MAPPING.put(MBpmnModel.SUBPROCESS, "subProcess");
		ACT_TYPE_MAPPING.put(MBpmnModel.GATEWAY_PARALLEL, "parallelGateway");
		ACT_TYPE_MAPPING.put(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, "exclusiveGateway");
		ACT_TYPE_MAPPING.put(MBpmnModel.GATEWAY_DATABASED_INCLUSIVE, "inclusiveGateway");
		
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_START_EMPTY, START_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_START_MESSAGE, START_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_START_TIMER, START_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_START_RULE, START_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_START_SIGNAL, START_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_START_MULTIPLE, START_EVENT_TAG);
		
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_EMPTY, INTERMEDIATE_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_ERROR, INTERMEDIATE_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, INTERMEDIATE_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL, INTERMEDIATE_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE, INTERMEDIATE_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_TIMER, INTERMEDIATE_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, INTERMEDIATE_EVENT_TAG);
		
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_EMPTY, END_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_ERROR, END_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_MESSAGE, END_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_SIGNAL, END_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_COMPENSATION, END_EVENT_TAG);
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_CANCEL, END_EVENT_TAG);
	}
	
	/**
	 *  Writes a BPMN model.
	 *  
	 *  @param outputfile The output file.
	 *  @param mmodel The BPMN model.
	 *  @param vmodelwriter The visual model writer, can be null.
	 */
	public static final void writeModel(File outputfile, MBpmnModel mmodel, IBpmnVisualModelWriter vmodelwriter) throws IOException
	{
		File file = File.createTempFile(outputfile.getName(), ".bpmn");
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), false, "UTF-8");
		
		writeInitialBoilerPlate(out);
		
		writeJadexModelInfo(out, mmodel);
		
		List<MPool> pools = mmodel.getPools();
		
		writePoolSemantics(out, pools);
		
		//writePoolCollaborations(out, pools);
		
		if (vmodelwriter != null)
		{
			vmodelwriter.writeVisualMode(out);
		}
		
		out.println("</semantic:definitions>");
		out.println();
		
		out.close();
		
		file.renameTo(outputfile);
	}
	
	/**
	 *  Writes the initial XML boiler plate.
	 *  
	 *  @param out The output.
	 *  @param semid The semantics ID.
	 */
	protected static final void writeInitialBoilerPlate(PrintStream out)
	{
		String indent = "                      ";
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		out.print("<semantic:definitions ");
		out.println("targetNamespace=\"http://www.activecomponents.org/bpmn/\"");
		out.print(indent);
		out.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		out.print(indent);
		out.println("xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"");
		out.print(indent);
		out.println("xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"");
		out.print(indent);
		out.println("xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"");
		out.print(indent);
		out.println("xmlns:semantic=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"");
		out.print(indent);
		out.println("xmlns:jadex=\"http://www.activecomponents.org/bpmnextensions\">");
	}
	
	/**
	 *  Writes the Jadex-specific model information
	 *  
	 *  @param out The output.
	 *  @param mmodel The model.
	 */
	protected static final void writeJadexModelInfo(PrintStream out, MBpmnModel mmodel)
	{
		out.print(getIndent(1));
		out.println("<semantic:extension>");
		
		String name = mmodel.getModelInfo().getName();
		if (name != null && name.length() > 0)
		{
			out.print(getIndent(2));
			out.print("<jadex:modelname>");
			out.print(name);
			out.println("</jadex:modelname>");
		}
		
		String desc = mmodel.getModelInfo().getDescription();
		if (desc != null && desc.length() > 0)
		{
			out.print(getIndent(2));
			out.print("<jadex:description>");
			out.print(desc);
			out.println("</jadex:description>");
		}
		
		String pkg = mmodel.getModelInfo().getPackage();
		if (pkg != null && pkg.length() > 0)
		{
			out.print(getIndent(2));
			out.print("<jadex:package>");
			out.print(pkg);
			out.println("</jadex:package>");
		}
		
		boolean suspend = Boolean.TRUE.equals(mmodel.getModelInfo().getSuspend(null));
		boolean master = Boolean.TRUE.equals(mmodel.getModelInfo().getMaster(null));
		boolean daemon = Boolean.TRUE.equals(mmodel.getModelInfo().getDaemon(null));
		boolean autoshutdown = Boolean.TRUE.equals(mmodel.getModelInfo().getAutoShutdown(null));
		boolean keepalive = Boolean.TRUE.equals(mmodel.isKeepAlive());
		
		if (suspend || master || daemon || autoshutdown || keepalive)
		{
			out.print(getIndent(2));
			out.print("<jadex:componentflags suspend=\"");
			out.print(suspend);
			out.print("\" master=\"");
			out.print(master);
			out.print("\" daemon=\"");
			out.print(daemon);
			out.print("\" autoshutdown=\"");
			out.print(autoshutdown);
			out.print("\" keepalive=\"");
			out.print(keepalive);
			out.println("\"/>");
		}
		
		writeImports(out, mmodel.getModelInfo().getImports());
		
		writeArguments(out, false, mmodel.getModelInfo().getArguments());
		
		writeArguments(out, true, mmodel.getModelInfo().getResults());
		
		writeContextVariables(out, mmodel);
		
		writeConfigurations(out, mmodel, mmodel.getModelInfo().getConfigurations());
		
		mmodel.getModelInfo().getProperties();
		
		out.print(getIndent(1));
		out.println("</semantic:extension>");
	}
	
	/**
	 *  Writes the imports.
	 *  
	 *  @param out The output.
	 *  @param imports The imports.
	 */
	protected static final void writeImports(PrintStream out, String[] imports)
	{
		if (imports.length > 0)
		{
			out.print(getIndent(2));
			out.println("<jadex:imports>");
			
			for (int i = 0; i < imports.length; ++i)
			{
				out.print(getIndent(3));
				out.print("<jadex:import>");
				out.print(imports[i]);
				out.println("</jadex:import>");
			}
			
			out.print(getIndent(2));
			out.println("</jadex:imports>");
		}
	}
	
	/**
	 *  Writes the arguments or results.
	 *  
	 *  @param out The output.
	 *  @param results Set true for writing results.
	 *  @param args The arguments or results.
	 */
	protected static final void writeArguments(PrintStream out, boolean results, IArgument[] args)
	{
		String prefix = results? "result" : "argument";
		
		int ind = 2;
		
		if (args.length > 0)
		{
			out.print(getIndent(ind));
			out.print("<jadex:");
			out.print(prefix);
			out.println("s>");
			++ind;
			
			for (int i = 0; i < args.length; ++i)
			{
				if (args[i].getName() != null && args[i].getName().length() > 0)
				{
					IArgument arg = args[i];
					
					boolean hasdesc = arg.getDescription() != null && arg.getDescription().length() > 0;
					boolean hasval = arg.getDefaultValue() != null && arg.getDefaultValue().getValue() != null && arg.getDefaultValue().getValue().length() > 0;
					
					out.print(getIndent(ind));
					out.print("<jadex:");
					out.print(prefix);
					out.print(" name=\"");
					out.print(arg.getName());
					out.print("\" type=\"");
					String type = arg.getClazz() != null? arg.getClazz().getTypeName() != null? arg.getClazz().getTypeName() : "" : "";
					out.print(type);
					
					if (hasdesc && hasval)
					{
						out.println("\">");
						++ind;
						
						if (hasdesc)
						{
							out.print(getIndent(ind));
							out.print("<jadex:description>");
							out.print(arg.getDescription());
							out.println("</jadex:description>");
						}
						
						if (hasval)
						{
							out.print(getIndent(ind));
							out.print("<jadex:value>");
							out.print(arg.getDefaultValue().getValue());
							out.println("</jadex:value>");
						}
						
						--ind;
						out.print("</jadex:");
						out.print(prefix);
						out.println(">");
					}
					else
					{
						out.println("\"/>");
					}
				}
			}
			
			--ind;
			out.print(getIndent(ind));
			out.print("</jadex:");
			out.print(prefix);
			out.println("s>");
		}
	}
	
	/**
	 *  Writes the context variables.
	 *  
	 *  @param out The output.
	 *  @param mmodel The BPMN model.
	 */
	protected static final void writeContextVariables(PrintStream out, MBpmnModel mmodel)
	{
		int ind = 2;
		
		Set<String> ctvnames = mmodel.getContextVariables();
		if (ctvnames.size() > 0)
		{
			out.print(getIndent(ind));
			out.println("<jadex:contextvariables>");
			++ind;
			
			for (String ctvname : ctvnames)
			{
				ClassInfo ci = mmodel.getContextVariableClass(ctvname);
				String cn = ci != null? ci.getTypeName() : "";
				
				out.print(getIndent(ind));
				out.print("<jadex:contextvariable name=\"");
				out.print(ctvname);
				out.print("\" type=\"");
				out.print(cn);
				
				UnparsedExpression exp = mmodel.getContextVariableExpression(ctvname, null);
				if (exp != null && exp.getValue() != null && exp.getValue().length() > 0)
				{
					out.println("\">");
					++ind;
					
					out.print(getIndent(ind));
					out.print("<jadex:value>");
					out.print(exp.getValue());
					out.println("</jadex:value>");
					
					--ind;
					out.print(getIndent(ind));
					out.println("</jadex:contextvariable>");
				}
				else
				{
					out.print("\"/>");
				}
			}
			
			--ind;
			out.print(getIndent(ind));
			out.println("</jadex:contextvariables>");
		}
	}
	
	/**
	 *  Writes the configurations.
	 *  
	 *  @param out The output.
	 *  @param configurations The configurations.
	 */
	protected static final void writeConfigurations(PrintStream out, MBpmnModel mmodel, ConfigurationInfo[] configurations)
	{
		if (configurations.length > 0)
		{
			Set<String> ctvnames = mmodel.getContextVariables();
			Map<String, Map<String, String>> ctvconfexp = new HashMap<String, Map<String, String>>();
			for (String ctvname : ctvnames)
			{
				for (int i = 0; i < configurations.length; ++i)
				{
					UnparsedExpression cexp = mmodel.getContextVariableExpression(ctvname, configurations[i].getName());
					if (cexp != null && cexp.getValue() != null && cexp.getValue().length() > 0)
					{
						Map<String, String> confctvs = ctvconfexp.get(configurations[i].getName());
						if (confctvs == null)
						{
							confctvs = new HashMap<String, String>();
							ctvconfexp.put(configurations[i].getName(), confctvs);
						}
						confctvs.put(ctvname, cexp.getValue());
					}
				}
			}
			
			int indent = 2;
			
			out.print(getIndent(indent));
			out.println("<jadex:configurations>");
			
			++indent;
			for (int i = 0; i < configurations.length; ++i)
			{
				ConfigurationInfo conf = configurations[i];
				
				out.print(getIndent(indent));
				out.print("<jadex:configuration name=\"");
				out.print(conf.getName());
				out.print("\"");
				
				if (conf.getSuspend() != null)
				{
					out.print(" suspend=\"");
					out.print(conf.getSuspend().booleanValue());
					out.print("\"");
				}
				
				if (conf.getMaster() != null)
				{
					out.print(" master=\"");
					out.print(conf.getMaster().booleanValue());
					out.print("\"");
				}
				
				if (conf.getDaemon() != null)
				{
					out.print(" daemon=\"");
					out.print(conf.getDaemon().booleanValue());
					out.print("\"");
				}
				
				if (conf.getAutoShutdown() != null)
				{
					out.print(" autoshutdown=\"");
					out.print(conf.getAutoShutdown().booleanValue());
					out.print("\"");
				}
				out.println(">");
				++indent;
				
				if (conf.getDescription() != null && conf.getDescription().length() > 0)
				{
					out.print(getIndent(indent));
					out.print("<jadex:description>");
					out.print(conf.getDescription());
					out.println("</jadex:description>");
				}
				
				String poollane = mmodel.getPoolLane(conf.getName());
				if (poollane != null && poollane.length() > 0)
				{
					out.print(getIndent(indent));
					out.print("<jadex:poollane>");
					out.print(poollane);
					out.println("</jadex:poollane>");
				}
				
				if (conf.getArguments().length > 0 || conf.getResults().length > 0 || ctvconfexp.containsKey(conf))
				{
					UnparsedExpression[] args = conf.getArguments();
					if (args.length > 0)
					{
						out.print(getIndent(indent));
						out.println("<jadex:argumentvalues>");
						++indent;
						
						for (int j = 0; j < args.length; ++j)
						{
							if (args[j].getValue() != null && args[j].getValue().length() > 0)
							{
								out.print(getIndent(indent));
								out.print("<jadex:value name=\"");
								out.print(args[j].getName());
								out.print("\">");
								out.print(args[j].getValue());
								out.println("</jadex:value>");
							}
						}
						
						--indent;
						out.print(getIndent(indent));
						out.println("<jadex:argumentvalues>");
					}
					
					UnparsedExpression[] res = conf.getResults();
					if (res.length > 0)
					{
						out.print(getIndent(indent));
						out.println("<jadex:resultvalues>");
						++indent;
						
						for (int j = 0; j < res.length; ++j)
						{
							if (res[j].getValue() != null && res[j].getValue().length() > 0)
							{
								out.print(getIndent(indent));
								out.print("<jadex:value name=\"");
								out.print(res[j].getName());
								out.print("\">");
								out.print(res[j].getValue());
								out.println("</jadex:value>");
							}
						}
						
						--indent;
						out.print(getIndent(indent));
						out.println("<jadex:resultvalues>");
					}
					
					Map<String, String> confctvmap = ctvconfexp.get(conf.getName());
					if (confctvmap != null && confctvmap.size() > 0)
					{
						out.print(getIndent(indent));
						out.println("<jadex:contextvariablevalues>");
						++indent;
						
						for (Map.Entry<String, String> entry : confctvmap.entrySet())
						{
							if (entry.getValue() != null && entry.getValue().length() > 0)
							{
								out.print(getIndent(indent));
								out.print("<jadex:value name=\"");
								out.print(entry.getKey());
								out.print("\">");
								out.print(entry.getValue());
								out.println("</jadex:value>");
							}
						}
						
						--indent;
						out.print(getIndent(indent));
						out.println("</jadex:contextvariablevalues>");
					}
				}
				
				--indent;
				out.print(getIndent(indent));
				out.println("</jadex:configuration>");
			}
			--indent;
			
			out.print(getIndent(indent));
			out.println("</jadex:configurations>");
		}
	}
	
	/**
	 *  Writes the pools of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param pools The pools.
	 */
	protected static final void writePoolSemantics(PrintStream out, List<MPool> pools)
	{
		if (pools != null && pools.size() > 0)
		{
			for (MPool pool : pools)
			{
				out.print(getIndent(1) + "<semantic:process name=\"");
				out.print(pool.getName());
				out.print("\" id=\"");
				out.print(pool.getId());
				out.println("\">");
				
				List<MLane> lanes = pool.getLanes();
				if (lanes != null && lanes.size() > 0)
				{
					writeLaneSemantics(out, lanes);
				}
				
				List<MActivity> activities = getPoolActivities(pool);
				writeActivitySemantics(out, activities, null, 2);
				
				List<MSequenceEdge> seqedges = pool.getSequenceEdges();
				if (seqedges != null)
				{
					writeSequenceEdgeSemantics(out, seqedges, 2);
				}
				
				out.println(getIndent(1) + "</semantic:process>");
			}
		}
	}
	
	/**
	 *  Writes the pools of the collaboration sections.
	 *  
	 *  @param out The output.
	 *  @param pools The pools.
	 */
	protected static final void writePoolCollaborations(PrintStream out, List<MPool> pools)
	{
		out.println(getIndent(1) + "<semantic:collaboration>");
		for (MPool pool : pools)
		{
			out.print(getIndent(2) + "<semantic:participant name=\"");
			out.print(pool.getName());
			out.print("\" processRef=\"");
			out.print(pool.getId());
			out.println("\"/>");
		}
		out.println(getIndent(1) + "</semantic:collaboration>");
	}
	
	/**
	 *  Writes the lanes of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param lanes The lanes.
	 */
	protected static final void writeLaneSemantics(PrintStream out, List<MLane> lanes)
	{
		out.println(getIndent(2) + "<semantic:laneSet>");
		
		//TODO: Child lane sets
		
		for (MLane lane : lanes)
		{
			out.print(getIndent(3) + "<semantic:lane name=\"");
			out.print(lane.getName());
			out.print("\" id=\"");
			out.print(lane.getId());
			out.println("\">");
			
			// Write activity references
			List<MActivity> activities = lane.getActivities();
			for (MActivity activity : activities)
			{
				out.print(getIndent(4) + "<semantic:flowNodeRef>");
				out.print(activity.getId());
				out.println("</semantic:flowNodeRef>");
			}
			
			out.println(getIndent(3) + "</semantic:lane>");
		}
		
		out.println(getIndent(2) + "</semantic:laneSet>");
	}
	
	/**
	 *  Writes the activities of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param activities The activities.
	 */
	protected static final void writeActivitySemantics(PrintStream out, List<MActivity> activities, String evthandlerref, int baseind)
	{
		for (MActivity activity : activities)
		{
			out.print(getIndent(baseind) + "<semantic:");
			String mappedacttype = ACT_TYPE_MAPPING.get(activity.getActivityType());
			
			boolean event = false;
			if (activity.getActivityType().startsWith("Event"))
			{
				event = true;
				if (activity.getActivityType().contains("Intermediate"))
				{
					if (activity.isEventHandler())
					{
						mappedacttype = "boundaryEvent";
					}
					else if(activity.isThrowing())
					{
						mappedacttype += "ThrowEvent";
					}
					else
					{
						mappedacttype += "CatchEvent";
					}
				}
			}
			
			out.print(mappedacttype);
			
			if (activity.getName() != null && activity.getName().length() > 0)
			{
				out.print(" name=\"");
				out.print(activity.getName());
				out.print("\"");
			}
			out.print(" id=\"");
			out.print(activity.getId());
			
			List<MSequenceEdge> edges = activity.getOutgoingSequenceEdges();
			if (edges != null)
			{
				for (MSequenceEdge edge : edges)
				{
					if (edge.isDefault())
					{
						out.print("\" default=\"");
						out.print(edge.getId());
						break;
					}
				}
			}
			
			if (activity.isEventHandler())
			{
				out.print("\" attachedToRef=\"");
				out.print(evthandlerref);
			}
			
			out.println("\">");
			
			edges = activity.getIncomingSequenceEdges();
			if (edges != null)
			{
				for (MSequenceEdge edge : edges)
				{
					out.print(getIndent(baseind + 1) + "<semantic:incoming>");
					out.print(edge.getId());
					out.println("</semantic:incoming>");
				}
			}
			
			edges = activity.getOutgoingSequenceEdges();
			if (edges != null)
			{
				for (MSequenceEdge edge : edges)
				{
					out.print(getIndent(baseind + 1) + "<semantic:outgoing>");
					out.print(edge.getId());
					out.println("</semantic:outgoing>");
				}
			}
			
			if (event)
			{
				if (activity.getActivityType().contains("Message"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:messageEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Timer"))
				{
					out.print(getIndent(baseind + 1));
					out.print("<semantic:timerEventDefinition");
					if (activity.hasPropertyValue("duration") &&
					   ((UnparsedExpression) activity.getPropertyValue("duration")).getValue().length() > 0)
					{
						out.println(">");
						out.print(getIndent(baseind + 2));
						out.print("<semantic:timeDuration>");
						out.print(((UnparsedExpression) activity.getPropertyValue("duration")).getValue());
						out.println("</semantic:timeDuration>");
						out.print(getIndent(baseind + 1));
						out.println("</semantic:timerEventDefinition>");
					}
					else
					{
						out.println("/>");
					}
				}
				else if (activity.getActivityType().contains("Rule"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:conditionalEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Signal"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:signalEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Error"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:errorEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Compensation"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:compensateEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Cancel"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:cancelEventDefinition/>");
				}
			}
			
			boolean issubproc = MBpmnModel.SUBPROCESS.equals(activity.getActivityType());
			String procref = null;
			boolean isprocrefexp = false;
			if (issubproc)
			{
				MSubProcess subproc = (MSubProcess) activity;
				if (subproc.hasPropertyValue("file"))
				{
					UnparsedExpression fileexp = (UnparsedExpression) subproc.getPropertyValue("file"); 
					procref = fileexp.getValue();
					isprocrefexp = true;
				}
				else if (subproc.hasPropertyValue("filename"))
				{
					procref = (String) subproc.getPropertyValue("filename");
				}
				else
				{
					List<MActivity> subactivities = subproc.getActivities();
					if (subactivities != null && subactivities.size() > 0)
					{
						writeActivitySemantics(out, subactivities, null, baseind + 1);
					}
					
					List<MSequenceEdge> subseqedges = subproc.getSequenceEdges();
					if (subseqedges != null && subseqedges.size() > 0)
					{
						writeSequenceEdgeSemantics(out, subseqedges, baseind + 1);
					}
				}
			}
			
			boolean istask = MBpmnModel.TASK.equals(activity.getActivityType()) || issubproc;
			boolean hastaskclass = istask && activity.getClazz() != null && activity.getClazz().getTypeName() != null && activity.getClazz().getTypeName().length() > 0;
			boolean hastaskparams = istask && activity.getParameters() != null && activity.getParameters().size() > 0;
			
			if (hastaskclass || hastaskparams || procref != null)
			{
				out.println(getIndent(baseind + 1) + "<semantic:extensionElements>");
				
				if (hastaskclass)
				{
					out.print(getIndent(baseind + 2) + "<jadex:taskclass>");
					out.print(activity.getClazz().getTypeName());
					out.println("</jadex:taskclass>");
				}
				
				if (hastaskparams)
				{
					IndexMap params = activity.getParameters();
					for (int i = 0; i < params.size(); ++i)
					{
						MParameter param = (MParameter) params.get(i);
						out.print(getIndent(baseind + 2) + "<jadex:parameter direction=\"");
						out.print(param.getDirection());
						out.print("\" name=\"");
						out.print(param.getName());
						out.print("\" type=\"");
						out.print(param.getClazz().getTypeName());
						out.print("\"");
						
						String inival = param.getInitialValue().getValue();
						if (inival != null && inival.length() > 0)
						{
							out.print(">");
							out.print(inival);
							out.println("</jadex:parameter>");
						}
						else
						{
							out.println(" />");
						}
					}
				}
				
				if (procref != null)
				{
					String tagpart = "jadex:subprocessref>";
					if (isprocrefexp)
					{
						tagpart = "jadex:subprocessexpressionref>";
					}
					out.print(getIndent(baseind + 2) + "<" + tagpart);
					out.print(procref);
					out.println("</" + tagpart);
				}
				
				out.println(getIndent(baseind + 1) + "</semantic:extensionElements>");
			}
			
			out.print(getIndent(baseind) + "</semantic:");
			out.print(mappedacttype);
			out.println(">");
			
			if (activity.getEventHandlers() != null && activity.getEventHandlers().size() > 0)
			{
				writeActivitySemantics(out, activity.getEventHandlers(), activity.getId(), baseind);
			}
		}
	}
	
	/**
	 *  Writes the sequence edges of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param seqedges The sequence edges.
	 */
	protected static final void writeSequenceEdgeSemantics(PrintStream out, List<MSequenceEdge> seqedges, int baseind)
	{
		for (MSequenceEdge edge : seqedges)
		{
			out.print(getIndent(baseind) + "<semantic:sequenceFlow sourceRef=\"");
			out.print(edge.getSource().getId());
			out.print("\" targetRef=\"");
			out.print(edge.getTarget().getId());
			out.print("\" id=\"");
			out.print(edge.getId());
			out.println("\">");
			
			if (edge.getCondition() != null)
			{
				String cond = edge.getCondition().getValue();
				if (cond != null && cond.length() > 0)
				{
					out.print(getIndent(baseind + 1) + "<semantic:conditionExpression>");
					out.print(cond);
					out.println("</semantic:conditionExpression>");
				}
			}
			
			IndexMap mappings = edge.getParameterMappings();
			if (mappings != null && mappings.size() > 0)
			{
				out.println(getIndent(baseind + 1) + "<semantic:extensionElements>");
				
				for (int i = 0; i < mappings.size(); ++i)
				{
					out.print(getIndent(baseind + 2) + "<jadex:parametermapping name=\"");
					out.print(mappings.getKey(i));
					out.print("\">");
					out.print(((Tuple2<UnparsedExpression, UnparsedExpression>) mappings.get(i)).getFirstEntity().getValue());
					out.println("</jadex:parametermapping>");
				}
				
				out.println(getIndent(baseind + 1) + "</semantic:extensionElements>");
			}
			
			out.println(getIndent(baseind) + "</semantic:sequenceFlow>");
		}
	}
	
	/**
	 *  Gets all activities in a pool.
	 *  TODO: Support nested lanes.
	 *  
	 *  @param pool The pool.
	 *  @return The contained activities.
	 */
	public static final List<MActivity> getPoolActivities(MPool pool)
	{
		List<MActivity> ret = new ArrayList<MActivity>();
		if (pool.getActivities() != null && pool.getActivities().size() > 0)
		{
			ret.addAll(pool.getActivities());
		}
		
		List<MLane> lanes = pool.getLanes();
		if (lanes != null)
		{
			for (MLane lane : lanes)
			{
				if (lane.getActivities() != null && lane.getActivities().size() > 0)
				{
					ret.addAll(lane.getActivities());
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Generates indentation.
	 *  
	 *  @param num The indentation number.
	 *  @return Indentation string.
	 */
	public static final String getIndent(int num)
	{
		StringBuilder sb = new StringBuilder();
		while (num-- > 0)
		{
			sb.append(INDENT_STRING);
		}
		return sb.toString();
	}
}
