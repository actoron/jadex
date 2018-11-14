package jadex.bpmn.model.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MContextVariable;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MMessagingEdge;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.MTask;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceScope;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.IndexMap;

/**
 *  Writer for BPMN models.
 *
 */
public class SBpmnModelWriter
{
	/** The build number */
	public static final int BUILD = 46;
	
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
		ACT_TYPE_MAPPING.put(MTask.TASK, "task");
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
		ACT_TYPE_MAPPING.put(MBpmnModel.EVENT_END_TERMINATE, END_EVENT_TAG);
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
		File file = File.createTempFile(outputfile.getName(), ".bpmn2");
		OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
		writeModel(os, mmodel, vmodelwriter);
		os.close();
		
		SUtil.moveFile(file, outputfile);
	}
	
	/**
	 *  Writes a BPMN model.
	 *  
	 *  @param os The output stream.
	 *  @param mmodel The BPMN model.
	 *  @param vmodelwriter The visual model writer, can be null.
	 */
	public static final void writeModel(OutputStream os, MBpmnModel mmodel, IBpmnVisualModelWriter vmodelwriter) throws IOException
	{
		PrintStream out = new PrintStream(os, false, "UTF-8");
		
		writeInitialBoilerPlate(out);
		
		int ind = 1;
		
		writeJadexModelInfo(out, ind, mmodel);
		
		List<MPool> pools = mmodel.getPools();
		
		writePoolSemantics(out, ind, pools);
		
		//writePoolCollaborations(out, pools);
		
		if (vmodelwriter != null)
		{
			vmodelwriter.writeVisualModel(out);
		}
		
		out.println("</semantic:definitions>");
		out.println();
		
		out.flush();
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
		out.println("xmlns:jadex=\"http://www.activecomponents.org/bpmnextensions\"");
		out.print(indent);
		out.println("xmlns:jadexvisual=\"http://www.activecomponents.org/bpmnvisualextensions\">");
	}
	
	/**
	 *  Writes the Jadex-specific model information
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param mmodel The model.
	 */
	protected static final void writeJadexModelInfo(PrintStream out, int ind, MBpmnModel mmodel)
	{
		out.print(getIndent(ind));
		out.println("<semantic:extension>");
		++ind;
		
		String name = mmodel.getModelInfo().getName();
		if (name != null && name.length() > 0)
		{
			out.print(getIndent(ind));
			out.print("<jadex:modelname>");
			out.print(escapeString(name));
			out.println("</jadex:modelname>");
		}
		
		String desc = mmodel.getModelInfo().getDescription();
		if (desc != null && desc.length() > 0)
		{
			out.print(getIndent(ind));
			out.print("<jadex:description>");
			out.print(escapeString(desc));
			out.println("</jadex:description>");
		}
		
		String pkg = mmodel.getModelInfo().getPackage();
		if (pkg != null && pkg.length() > 0)
		{
			out.print(getIndent(ind));
			out.print("<jadex:package>");
			out.print(escapeString(pkg));
			out.println("</jadex:package>");
		}
		
		boolean suspend = Boolean.TRUE.equals(mmodel.getModelInfo().getSuspend(null));
//		boolean master = Boolean.TRUE.equals(mmodel.getModelInfo().getMaster(null));
//		boolean daemon = Boolean.TRUE.equals(mmodel.getModelInfo().getDaemon(null));
//		boolean autoshutdown = Boolean.TRUE.equals(mmodel.getModelInfo().getAutoShutdown(null));
		boolean synchronous = Boolean.TRUE.equals(mmodel.getModelInfo().getSynchronous(null));
//		boolean persistable = Boolean.TRUE.equals(mmodel.getModelInfo().getPersistable(null));
		String monitoring = mmodel.getModelInfo().getMonitoring(null)!=null ? mmodel.getModelInfo().getMonitoring(null).toString() : null;
		boolean keepalive = Boolean.TRUE.equals(mmodel.isKeepAlive());
		
		if (suspend || monitoring!=null || synchronous || keepalive)
//		if (suspend || master || daemon || autoshutdown || monitoring!=null || synchronous || keepalive)
		{
			out.print(getIndent(ind));
			out.print("<jadex:componentflags suspend=\"");
			out.print(escapeString(String.valueOf(suspend)));
			out.print("\" master=\"");
//			out.print(escapeString(String.valueOf(master)));
//			out.print("\" daemon=\"");
//			out.print(escapeString(String.valueOf(daemon)));
//			out.print("\" autoshutdown=\"");
//			out.print(escapeString(String.valueOf(autoshutdown)));
			out.print("\" synchronous=\"");
			out.print(escapeString(String.valueOf(synchronous)));
//			out.print("\" persistable=\"");
//			out.print(escapeString(String.valueOf(persistable)));
			if(monitoring!=null)
			{
				out.print("\" monitoring=\"");
				out.print(escapeString(String.valueOf(monitoring)));
			}
			out.print("\" keepalive=\"");
			out.print(escapeString(String.valueOf(keepalive)));
			out.println("\"/>");
		}
		
		writeImports(out, ind, mmodel.getModelInfo().getImports());
		
		writeSubcomponents(out, ind, mmodel.getModelInfo().getSubcomponentTypes());
		
		writeArguments(out, ind, false, mmodel.getModelInfo().getArguments());
		
		writeArguments(out, ind, true, mmodel.getModelInfo().getResults());
		
		writeContextVariables(out, ind, mmodel);
		
		writeProvidedServices(out, ind, mmodel);
		
		writeRequiredServices(out, ind, mmodel);
		
		writeConfigurations(out, ind, mmodel, mmodel.getModelInfo().getConfigurations());
		
		--ind;
		out.print(getIndent(ind));
		out.println("</semantic:extension>");
	}
	
	/**
	 *  Writes the imports.
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param imports The imports.
	 */
	protected static final void writeImports(PrintStream out, int ind, String[] imports)
	{
		if (imports.length > 0)
		{
			out.print(getIndent(ind));
			out.println("<jadex:imports>");
			++ind;
			
			for (int i = 0; i < imports.length; ++i)
			{
				out.print(getIndent(ind));
				out.print("<jadex:import>");
				out.print(escapeString(imports[i]));
				out.println("</jadex:import>");
			}
			
			--ind;
			out.print(getIndent(ind));
			out.println("</jadex:imports>");
		}
	}
	
	/**
	 *  Writes the subcomponents.
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param scti The subcomponent type infos.
	 */
	protected static final void writeSubcomponents(PrintStream out, int ind, SubcomponentTypeInfo[] scti)
	{
		if (scti.length > 0)
		{
			out.print(getIndent(ind));
			out.println("<jadex:subcomponents>");
			++ind;
			
			for (int i = 0; i < scti.length; ++i)
			{
				out.print(getIndent(ind));
				out.print("<jadex:subcomponent name=\"");
				out.print(escapeString(scti[i].getName()));
				out.print("\">");
				out.print(escapeString(scti[i].getFilename()));
				out.println("</jadex:subcomponent>");
			}
			
			--ind;
			out.print(getIndent(ind));
			out.println("</jadex:subcomponents>");
		}
	}
	
	/**
	 *  Writes the arguments or results.
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param results Set true for writing results.
	 *  @param args The arguments or results.
	 */
	protected static final void writeArguments(PrintStream out, int ind, boolean results, IArgument[] args)
	{
		String prefix = results? "result" : "argument";
		
		if (args.length > 0)
		{
			out.print(getIndent(ind));
			out.print("<jadex:");
			out.print(prefix);
			out.println("s>");
			++ind;
			
			for (int i = 0; i < args.length; ++i)
			{
				if(args[i].getName() != null && args[i].getName().length() > 0)
				{
					IArgument arg = args[i];
					
					boolean hasdesc = arg.getDescription() != null && arg.getDescription().length() > 0;
					boolean hasval = arg.getDefaultValue() != null && arg.getDefaultValue().getValue() != null && arg.getDefaultValue().getValue().length() > 0;
					
					out.print(getIndent(ind));
					out.print("<jadex:");
					out.print(prefix);
					out.print(" name=\"");
					out.print(escapeString(arg.getName()));
					out.print("\" type=\"");
					String type = arg.getClazz() != null? arg.getClazz().getGenericTypeName() != null? arg.getClazz().getGenericTypeName() : "" : "";
					out.print(escapeString(type));
					
					if (hasdesc || hasval)
					{
						out.println("\">");
						++ind;
						
						if (hasdesc)
						{
							out.print(getIndent(ind));
							out.print("<jadex:description>");
							out.print(escapeString(arg.getDescription()));
							out.println("</jadex:description>");
						}
						
						if (hasval)
						{
							out.print(getIndent(ind));
							out.print("<jadex:value>");
							out.print(escapeString(arg.getDefaultValue().getValue()));
							out.println("</jadex:value>");
						}
						
						--ind;
						out.print(getIndent(ind));
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
	 *  @param ind The indentation level.
	 *  @param mmodel The BPMN model.
	 */
	protected static final void writeContextVariables(PrintStream out, int ind, MBpmnModel mmodel)
	{
		List<MContextVariable> ctvs = mmodel.getContextVariables();
		if (ctvs.size() > 0)
		{
			out.print(getIndent(ind));
			out.println("<jadex:contextvariables>");
			++ind;
			
			for (MContextVariable ctv : ctvs)
			{
				ClassInfo ci = ctv.getClazz();
				String cn = ci != null? ci.getGenericTypeName() : "";
				
				out.print(getIndent(ind));
				out.print("<jadex:contextvariable name=\"");
				out.print(escapeString(ctv.getName()));
				out.print("\" type=\"");
				out.print(escapeString(cn));
				
				//UnparsedExpression exp = mmodel.getContextVariableExpression(ctvname, null);
				//if (exp != null && exp.getValue() != null && exp.getValue().length() > 0)
				if (ctv.getValue() != null && ctv.getValue().length() > 0)
				{
					out.println("\">");
					++ind;
					
					out.print(getIndent(ind));
					out.print("<jadex:value>");
					out.print(escapeString(ctv.getValue()));
					out.println("</jadex:value>");
					
					--ind;
					out.print(getIndent(ind));
					out.println("</jadex:contextvariable>");
				}
				else
				{
					out.println("\"/>");
				}
			}
			
			--ind;
			out.print(getIndent(ind));
			out.println("</jadex:contextvariables>");
		}
	}
	
	/**
	 *  Writes the provided services.
	 *  
	 *  @param out The output.
	 *  @param mmodel The BPMN model.
	 */
	protected static final void writeProvidedServices(PrintStream out, int ind, MBpmnModel mmodel)
	{
		ProvidedServiceInfo[] pss = mmodel.getModelInfo().getProvidedServices();
		if (pss != null && pss.length > 0)
		{
			out.print(getIndent(ind));
			out.println("<jadex:providedservices>");
			++ind;
			
			for (ProvidedServiceInfo ps : pss)
			{
				out.print(getIndent(ind));
				out.print("<jadex:providedservice name=\"");
				out.print(escapeString(ps.getName()));
				
				String iface = ps.getType() != null? ps.getType().getGenericTypeName() : null;
				if (iface != null)
				{
					out.print("\" interface=\"");
					out.print(escapeString(ps.getType().getGenericTypeName()));
				}
				
				if (ps.getImplementation() != null)
				{
					ClassInfo implclass = ps.getImplementation().getClazz();
					if (implclass != null && implclass.getGenericTypeName() != null && implclass.getGenericTypeName().length() > 0)
					{
						out.print("\" class=\"");
						out.print(escapeString(implclass.getGenericTypeName()));
					}
					
					String proxytype = ps.getImplementation().getProxytype();
					if (proxytype != null && proxytype.length() > 0)
					{
						out.print("\" proxytype=\"");
						out.print(escapeString(proxytype));
					}
					
					String impl = ps.getImplementation().getValue();
					if (impl != null && impl.length() > 0)
					{
						out.print("\" implementation=\"");
						out.print(escapeString(impl));
					}
				}
				
				out.println("\"/>");
			}
			
			--ind;
			out.print(getIndent(ind));
			out.println("</jadex:providedservices>");
		}
	}
	
	/**
	 *  Writes the required services.
	 *  
	 *  @param out The output.
	 *  @param mmodel The BPMN model.
	 */
	protected static final void writeRequiredServices(PrintStream out, int ind, MBpmnModel mmodel)
	{
		RequiredServiceInfo[] rss = mmodel.getModelInfo().getServices();
		if (rss != null && rss.length > 0)
		{
			out.print(getIndent(ind));
			out.println("<jadex:requiredservices>");
			++ind;
			
			for (RequiredServiceInfo rs : rss)
			{
				out.print(getIndent(ind));
				out.print("<jadex:requiredservice name=\"");
				out.print(escapeString(rs.getName()));
				
				String iface = rs.getType() != null? rs.getType().getGenericTypeName() : null;
				if (iface != null)
				{
					out.print("\" interface=\"");
					out.print(escapeString(iface));
				}
				
				if (rs.isMultiple())
				{
					out.print("\" multi=\"");
					out.print(escapeString(String.valueOf(rs.isMultiple())));
				}
				
				if (rs.getDefaultBinding() != null)
				{
					ServiceScope scope = rs.getDefaultBinding().getScope();
//					if (scope != null && scope.length() > 0)
					{
						out.print("\" scope=\"");
						out.print(escapeString(scope.name()));
					}
					
					// dropped in v4???
//					boolean dyn = rs.getDefaultBinding().isDynamic();
//					out.print("\" dynamic=\"");
//					out.print(dyn);
//					
//					boolean create = rs.getDefaultBinding().isCreate();
//					out.print("\" create=\"");
//					out.print(create);
				}
				
				out.println("\"/>");
			}
			
			--ind;
			out.print(getIndent(ind));
			out.println("</jadex:requiredservices>");
		}
	}
	
	/**
	 *  Writes the configurations.
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param configurations The configurations.
	 */
	protected static final void writeConfigurations(PrintStream out, int ind, MBpmnModel mmodel, ConfigurationInfo[] configurations)
	{
		if (configurations.length > 0)
		{
			List<MContextVariable> ctvs = mmodel.getContextVariables();
			Map<String, Map<String, String>> ctvconfexp = new HashMap<String, Map<String, String>>();
			for (MContextVariable ctv : ctvs)
			{
				for (int i = 0; i < configurations.length; ++i)
				{
					//UnparsedExpression cexp = mmodel.getContextVariableExpression(ctvname, configurations[i].getName());
					UnparsedExpression cexp = ctv.getConfigValue(configurations[i].getName());
					if (cexp != null && cexp.getValue() != null && cexp.getValue().length() > 0)
					{
						Map<String, String> confctvs = ctvconfexp.get(configurations[i].getName());
						if (confctvs == null)
						{
							confctvs = new HashMap<String, String>();
							ctvconfexp.put(configurations[i].getName(), confctvs);
						}
						confctvs.put(ctv.getName(), cexp.getValue());
					}
				}
			}
			
			out.print(getIndent(ind));
			out.println("<jadex:configurations>");
			
			++ind;
			for (int i = 0; i < configurations.length; ++i)
			{
				ConfigurationInfo conf = configurations[i];
				
				out.print(getIndent(ind));
				out.print("<jadex:configuration name=\"");
				out.print(escapeString(conf.getName()));
				out.print("\"");
				
				if (conf.getSuspend() != null)
				{
					out.print(" suspend=\"");
					out.print(conf.getSuspend().booleanValue());
					out.print("\"");
				}
				
//				if (conf.getMaster() != null)
//				{
//					out.print(" master=\"");
//					out.print(conf.getMaster().booleanValue());
//					out.print("\"");
//				}
//				
//				if (conf.getDaemon() != null)
//				{
//					out.print(" daemon=\"");
//					out.print(conf.getDaemon().booleanValue());
//					out.print("\"");
//				}
//				
//				if (conf.getAutoShutdown() != null)
//				{
//					out.print(" autoshutdown=\"");
//					out.print(conf.getAutoShutdown().booleanValue());
//					out.print("\"");
//				}

				if (conf.getSynchronous() != null)
				{
					out.print(" synchronous=\"");
					out.print(conf.getSynchronous().booleanValue());
					out.print("\"");
				}

//				if (conf.getPersistable() != null)
//				{
//					out.print(" persistable=\"");
//					out.print(conf.getPersistable().booleanValue());
//					out.print("\"");
//				}

				out.println(">");
				++ind;
				
				if (conf.getDescription() != null && conf.getDescription().length() > 0)
				{
					out.print(getIndent(ind));
					out.print("<jadex:description>");
					out.print(escapeString(conf.getDescription()));
					out.println("</jadex:description>");
				}
				
//				String poollane = mmodel.getPoolLane(conf.getName());
//				if (poollane != null && poollane.length() > 0)
//				{
//					out.print(getIndent(ind));
//					out.print("<jadex:poollane>");
//					out.print(escapeString(poollane));
//					out.println("</jadex:poollane>");
//				}
				
				List<MNamedIdElement> startelements = mmodel.getStartElements(conf.getName());
				if (startelements != null && startelements.size() > 0)
				{
					for (MNamedIdElement element : startelements)
					{
						out.print(getIndent(ind));
						out.print("<jadex:startElement>");
						out.print(escapeString(element.getId()));
						out.println("</jadex:startElement>");
					}
				}
				
				if (conf.getArguments().length > 0 || conf.getResults().length > 0 || ctvconfexp.containsKey(conf.getName()))
				{
					UnparsedExpression[] args = conf.getArguments();
					if (args.length > 0)
					{
						out.print(getIndent(ind));
						out.println("<jadex:argumentvalues>");
						++ind;
						
						for (int j = 0; j < args.length; ++j)
						{
							if (args[j].getValue() != null && args[j].getValue().length() > 0)
							{
								out.print(getIndent(ind));
								out.print("<jadex:value name=\"");
								out.print(escapeString(args[j].getName()));
								out.print("\">");
								out.print(escapeString(args[j].getValue()));
								out.println("</jadex:value>");
							}
						}
						
						--ind;
						out.print(getIndent(ind));
						out.println("</jadex:argumentvalues>");
					}
					
					UnparsedExpression[] res = conf.getResults();
					if (res.length > 0)
					{
						out.print(getIndent(ind));
						out.println("<jadex:resultvalues>");
						++ind;
						
						for (int j = 0; j < res.length; ++j)
						{
							if (res[j].getValue() != null && res[j].getValue().length() > 0)
							{
								out.print(getIndent(ind));
								out.print("<jadex:value name=\"");
								out.print(escapeString(res[j].getName()));
								out.print("\">");
								out.print(escapeString(res[j].getValue()));
								out.println("</jadex:value>");
							}
						}
						
						--ind;
						out.print(getIndent(ind));
						out.println("</jadex:resultvalues>");
					}
					
					Map<String, String> confctvmap = ctvconfexp.get(conf.getName());
					if (confctvmap != null && confctvmap.size() > 0)
					{
						out.print(getIndent(ind));
						out.println("<jadex:contextvariablevalues>");
						++ind;
						
						for (Map.Entry<String, String> entry : confctvmap.entrySet())
						{
							if (entry.getValue() != null && entry.getValue().length() > 0)
							{
								out.print(getIndent(ind));
								out.print("<jadex:value name=\"");
								out.print(escapeString(entry.getKey()));
								out.print("\">");
								out.print(escapeString(entry.getValue()));
								out.println("</jadex:value>");
							}
						}
						
						--ind;
						out.print(getIndent(ind));
						out.println("</jadex:contextvariablevalues>");
					}
				}
				
				ProvidedServiceInfo[] pss = conf.getProvidedServices();
				if (pss != null && pss.length > 0)
				{
					out.print(getIndent(ind));
					out.println("<jadex:providedserviceconfigurations>");
					++ind;
					
					for (ProvidedServiceInfo ps : pss)
					{
						if (ps.getImplementation() != null)
						{
							out.print(getIndent(ind));
							out.print("<jadex:providedserviceconfiguration name=\"");
							out.print(escapeString(ps.getName()));
							
							if (ps.getImplementation().getClazz() != null && ps.getImplementation().getClazz().getGenericTypeName() != null && ps.getImplementation().getClazz().getGenericTypeName().length() > 0)
							{
								out.print("\" class=\"");
								out.print(escapeString(ps.getImplementation().getClazz().getGenericTypeName()));
							}
							
							if (ps.getImplementation().getProxytype() != null && ps.getImplementation().getProxytype().length() > 0)
							{
								out.print("\" proxytype=\"");
								out.print(escapeString(ps.getImplementation().getProxytype()));
							}
							
							if (ps.getImplementation().getValue() != null && ps.getImplementation().getValue().length() > 0)
							{
								out.print("\" implementation=\"");
								out.print(escapeString(ps.getImplementation().getValue()));
							}
							
							out.println("\"/>");
						}
					}
					
					--ind;
					out.print(getIndent(ind));
					out.println("</jadex:providedserviceconfigurations>");
				}
				
				RequiredServiceInfo[] rss = conf.getServices();
				if (rss != null && rss.length > 0)
				{
					out.print(getIndent(ind));
					out.println("<jadex:requiredserviceconfigurations>");
					++ind;
					
					for (RequiredServiceInfo rs : rss)
					{
						if (rs.getDefaultBinding() != null)
						{
							out.print(getIndent(ind));
							out.print("<jadex:requiredserviceconfiguration name=\"");
							out.print(escapeString(rs.getName()));
							
							if (rs.getDefaultBinding().getScope() != null)
							{
								out.print("\" scope=\"");
								out.print(escapeString(rs.getDefaultBinding().getScope().name()));
							}
							
							out.println("\"/>");
						}
					}
					
					--ind;
					out.print(getIndent(ind));
					out.println("</jadex:requiredserviceconfigurations>");
				}
				
				--ind;
				out.print(getIndent(ind));
				out.println("</jadex:configuration>");
			}
			--ind;
			
			out.print(getIndent(ind));
			out.println("</jadex:configurations>");
		}
	}
	
	/**
	 *  Writes the pools of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param pools The pools.
	 */
	protected static final void writePoolSemantics(PrintStream out, int ind, List<MPool> pools)
	{
		if (pools != null && pools.size() > 0)
		{
			for (MPool pool : pools)
			{
				out.print(getIndent(ind) + "<semantic:process name=\"");
				out.print(pool.getName());
				out.print("\" id=\"");
				out.print(escapeString(pool.getId()));
				out.println("\">");
				++ind;
				
				List<MLane> lanes = pool.getLanes();
				if (lanes != null && lanes.size() > 0)
				{
					writeLaneSemantics(out, ind, lanes);
				}
				
				List<MActivity> activities = getPoolActivities(pool);
				
				List<MSequenceEdge> seqedges = new ArrayList<MSequenceEdge>();
				List<MMessagingEdge> medges = new ArrayList<MMessagingEdge>();
				List<MDataEdge> dataedges = new ArrayList<MDataEdge>();
				writeActivitySemantics(out, activities, null, ind, seqedges, medges, dataedges);
				
				writeSequenceEdgeSemantics(out, seqedges, ind);
				writeMessagingEdgeSemantics(out, medges, ind);
				writePoolExtensions(out, ind, dataedges);
				
				--ind;
				out.println(getIndent(ind) + "</semantic:process>");
			}
		}
	}
	
	/**
	 *  Writes the pools of the collaboration sections.
	 *  
	 *  @param out The output.
	 *  @param pools The pools.
	 */
//	protected static final void writePoolCollaborations(PrintStream out, List<MPool> pools)
//	{
//		out.println(getIndent(1) + "<semantic:collaboration>");
//		for (MPool pool : pools)
//		{
//			out.print(getIndent(2) + "<semantic:participant name=\"");
//			out.print(pool.getName());
//			out.print("\" processRef=\"");
//			out.print(pool.getId());
//			out.println("\"/>");
//		}
//		out.println(getIndent(1) + "</semantic:collaboration>");
//	}
	
	/**
	 *  Writes the pool extension elements (e.g. data edges).
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param seqedges The sequence edges.
	 */
	protected static final void writePoolExtensions(PrintStream out, int ind, List<MDataEdge> dataedges)
	{
		out.println(getIndent(ind) + "<semantic:extensionElements>");
		++ind;
		for (MDataEdge dedge : dataedges)
		{
			out.print(getIndent(ind));
			out.print("<jadex:dataFlow ");
			if (dedge.getName() != null && dedge.getName().length() > 0)
			{
				out.print("name=\"");
				out.print(escapeString(dedge.getName()));
				out.print("\" ");
			}
			out.print("id=\"");
			out.print(escapeString(dedge.getId()));
			out.print("\" sourceRef=\"");
			out.print(escapeString(dedge.getSource().getId()));
			out.print("\" sourceParam=\"");
			out.print(escapeString(handleNullStr(dedge.getSourceParameter())));
			out.print("\" targetRef=\"");
			out.print(escapeString(dedge.getTarget().getId()));
			out.print("\" targetParam=\"");
			out.print(escapeString(handleNullStr(dedge.getTargetParameter())));
			
			if (dedge.getParameterMapping() != null &&
				dedge.getParameterMapping().getValue() != null &&
				dedge.getParameterMapping().getValue().length() > 0)
			{
				out.println("\">");
				++ind;
				out.print(getIndent(ind));
				out.print("<jadex:dataFlowValueMapping>");
				out.print(escapeString(dedge.getParameterMapping().getValue()));
				out.println("</jadex:dataFlowValueMapping>");
				--ind;
				out.print(getIndent(ind));
				out.println("</jadex:dataFlow>");
			}
			else
			{
				out.println("\"/>");
			}
		}
		--ind;
		out.println(getIndent(ind) + "</semantic:extensionElements>");
	}
	
	/**
	 *  Writes the lanes of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param ind The indentation level.
	 *  @param lanes The lanes.
	 */
	protected static final void writeLaneSemantics(PrintStream out, int ind, List<MLane> lanes)
	{
		out.println(getIndent(ind) + "<semantic:laneSet>");
		++ind;
		
		//TODO: Child lane sets
		
		for (MLane lane : lanes)
		{
			out.print(getIndent(ind) + "<semantic:lane name=\"");
			out.print(lane.getName());
			out.print("\" id=\"");
			out.print(escapeString(lane.getId()));
			out.println("\">");
			++ind;
			
			// Write activity references
			List<MActivity> activities = lane.getActivities();
			if (activities != null)
			{
				for (MActivity activity : activities)
				{
					out.print(getIndent(ind) + "<semantic:flowNodeRef>");
					out.print(escapeString(activity.getId()));
					out.println("</semantic:flowNodeRef>");
				}
			}
			
			--ind;
			out.println(getIndent(ind) + "</semantic:lane>");
		}
		
		--ind;
		out.println(getIndent(ind) + "</semantic:laneSet>");
	}
	
	/**
	 *  Writes the activities of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param activities The activities.
	 */
	protected static final void writeActivitySemantics(PrintStream out, List<MActivity> activities, String evthandlerref, int baseind, List<MSequenceEdge> seqedges, List<MMessagingEdge> medges, List<MDataEdge> dataedges)
	{
		for(MActivity activity : activities)
		{
			// As activities are also contained in the pool in the old bpmn model
			if(activity.isEventHandler() && evthandlerref==null)
				continue; 
			
			if (activity.getOutgoingDataEdges() != null)
			{
				dataedges.addAll(activity.getOutgoingDataEdges());
			}
			
			if (activity.getOutgoingSequenceEdges() != null)
			{
				seqedges.addAll(activity.getOutgoingSequenceEdges());
			}
			
			if (activity.getOutgoingMessagingEdges() != null)
			{
				medges.addAll(activity.getOutgoingMessagingEdges());
			}
			
			out.print(getIndent(baseind) + "<semantic:");
			String mappedacttype = ACT_TYPE_MAPPING.get(activity.getActivityType());
			
			boolean event = false;
			if(activity.getActivityType().startsWith("Event"))
			{
				event = true;
				if(activity.getActivityType().contains("Intermediate"))
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
				out.print(escapeString(activity.getName()));
				out.print("\"");
			}
			out.print(" id=\"");
			out.print(activity.getId());
			
			if (activity instanceof MSubProcess &&
				MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess) activity).getSubprocessType()))
			{
				out.print("\" triggeredByEvent=\"true");
			}
			
			List<MSequenceEdge> edges = activity.getOutgoingSequenceEdges();
			if (edges != null)
			{
				for (MSequenceEdge edge : edges)
				{
					if (edge.isDefault())
					{
						out.print("\" default=\"");
						out.print(escapeString(edge.getId()));
						break;
					}
				}
			}
			
			if(activity.isEventHandler())
			{
				out.print("\" attachedToRef=\"");
				out.print(escapeString(evthandlerref));
			}
			
			out.println("\">");
			
			edges = activity.getIncomingSequenceEdges();
			if (edges != null)
			{
				for (MSequenceEdge edge : edges)
				{
					out.print(getIndent(baseind + 1) + "<semantic:incoming>");
					out.print(escapeString(edge.getId()));
					out.println("</semantic:incoming>");
				}
			}
			
			edges = activity.getOutgoingSequenceEdges();
			if (edges != null)
			{
				for (MSequenceEdge edge : edges)
				{
					out.print(getIndent(baseind + 1) + "<semantic:outgoing>");
					out.print(escapeString(edge.getId()));
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
						out.print(escapeString(((UnparsedExpression) activity.getPropertyValue("duration")).getValue()));
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
					out.println("<semantic:compensateEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Cancel"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:cancelEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Terminate"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:terminateEventDefinition/>");
				}
				else if (activity.getActivityType().contains("Multipl"))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:multipleEventDefinition/>");
				}
			}
			
			boolean issubproc = MBpmnModel.SUBPROCESS.equals(activity.getActivityType());
			String procref = null;
			boolean isprocrefexp = false;
			if(issubproc)
			{
				MSubProcess subproc = (MSubProcess)activity;
				
				String tp = subproc.getSubprocessType();
				if(MSubProcess.SUBPROCESSTYPE_SEQUENTIAL.equals(tp) || MSubProcess.SUBPROCESSTYPE_PARALLEL.equals(tp))
				{
					out.print(getIndent(baseind + 1));
					out.println("<semantic:multiInstanceLoopCharacteristics isSequential=\""+(MSubProcess.SUBPROCESSTYPE_SEQUENTIAL.equals(tp)? "true": "false")+"\" />");
				}
				
				if(subproc.hasPropertyValue("file"))
				{
					UnparsedExpression fileexp = (UnparsedExpression)subproc.getPropertyValue("file"); 
					procref = fileexp.getValue();
					isprocrefexp = true;
				}
				else if(subproc.hasPropertyValue("filename"))
				{
					procref = subproc.getPropertyValue("filename").getValue();
					if (procref != null && procref.length() >= 2)
					{
						procref = procref.substring(1);
						procref = procref.substring(0, procref.length() - 1);
					}
				}
				else
				{
					List<MActivity> subactivities = subproc.getActivities();
					if (subactivities != null && subactivities.size() > 0)
					{
						writeActivitySemantics(out, subactivities, null, baseind + 1, seqedges, medges, dataedges);
					}
					
//					List<MSequenceEdge> subseqedges = subproc.getSequenceEdges();
//					if (subseqedges != null && subseqedges.size() > 0)
//					{
//						writeSequenceEdgeSemantics(out, subseqedges, baseind + 1);
//					}
				}
			}
			
//			boolean istask = MBpmnModel.TASK.equals(activity.getActivityType()) || issubproc;
			boolean hasclass = activity.getClazz()!=null && activity.getClazz().getGenericTypeName() != null && activity.getClazz().getGenericTypeName().length() > 0;
//			boolean hastaskparams = istask && activity.getParameters()!=null && activity.getParameters().size()>0;
			boolean hastaskparams = activity.getParameters()!=null && activity.getParameters().size()>0;
			boolean hasprops = activity.getProperties()!=null && activity.getProperties().size()>0;
			
			if(hasclass || hastaskparams || hasprops || procref != null)
			{
				out.println(getIndent(baseind + 1) + "<semantic:extensionElements>");
				
				if(hasclass)
				{
					out.print(getIndent(baseind + 2) + "<jadex:class>");
					out.print(escapeString(activity.getClazz().getGenericTypeName()));
					out.println("</jadex:class>");
				}
				
				if(hastaskparams)
				{
					IndexMap<String, MParameter> params = activity.getParameters();
					for (String key: params.keySet())
					{
						MParameter param = params.get(key);
						out.print(getIndent(baseind + 2) + "<jadex:parameter direction=\"");
						out.print(escapeString(param.getDirection()));
						out.print("\" name=\"");
						out.print(escapeString(param.getName()));
						out.print("\" type=\"");
						out.print(escapeString(param.getClazz().getGenericTypeName()));
						out.print("\"");
						
						String inival = param.getInitialValue() != null? param.getInitialValue().getValue() : null;
						if (inival != null && inival.length() > 0)
						{
							out.print(">");
							out.print(escapeString(inival));
							out.println("</jadex:parameter>");
						}
						else
						{
							out.println("/>");
						}
					}
				}
				
				if(hasprops)
				{
					IndexMap<String, MProperty> props = activity.getProperties();
					for(String key: props.keySet())
					{
						MProperty prop = props.get(key);
						out.print(getIndent(baseind + 2) + "<jadex:property name=\"");
						out.print(escapeString(prop.getName()));
						if (prop.getClazz() != null)
						{
							out.print("\" type=\"");
							out.print(escapeString(prop.getClazz().getGenericTypeName()));
						}
						out.print("\"");
						
						String inival = prop.getInitialValue() != null ? prop.getInitialValue().getValue() : null;
						if(inival != null && inival.length() > 0)
						{
							out.print(">");
							out.print(escapeString(inival));
							out.println("</jadex:property>");
						}
						else
						{
							out.println("/>");
						}
					}
				}
				
				if(procref != null)
				{
					String tagpart = "jadex:subprocessref>";
					if (isprocrefexp)
					{
						tagpart = "jadex:subprocessexpressionref>";
					}
					out.print(getIndent(baseind + 2) + "<" + tagpart);
					out.print(escapeString(procref));
					out.println("</" + tagpart);
				}
				
				out.println(getIndent(baseind + 1) + "</semantic:extensionElements>");
			}
			
			out.print(getIndent(baseind) + "</semantic:");
			out.print(mappedacttype);
			out.println(">");
			
			if (activity.getEventHandlers() != null && activity.getEventHandlers().size() > 0)
			{
				writeActivitySemantics(out, activity.getEventHandlers(), activity.getId(), baseind, seqedges, medges, dataedges);
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
			out.print(getIndent(baseind) + "<semantic:sequenceFlow ");
			if (edge.getName() != null && edge.getName().length() > 0)
			{
				out.print("name=\"");
				out.print(escapeString(edge.getName()));
				out.print("\" ");
			}
			out.print("sourceRef=\"");
			out.print(escapeString(edge.getSource().getId()));
			out.print("\" targetRef=\"");
			out.print(escapeString(edge.getTarget().getId()));
			out.print("\" id=\"");
			out.print(escapeString(edge.getId()));
			out.println("\">");
			
			if (edge.getCondition() != null)
			{
				String cond = edge.getCondition().getValue();
				if (cond != null && cond.length() > 0)
				{
					out.print(getIndent(baseind + 1) + "<semantic:conditionExpression>");
					out.print(escapeString(cond));
					out.println("</semantic:conditionExpression>");
				}
			}
			
//			Map<String, Tuple2<UnparsedExpression, UnparsedExpression>> mappings = edge.getParameterMappings();
			IndexMap<String, Tuple2<UnparsedExpression, UnparsedExpression>> mappings = edge.getParameterMappings();
			if(mappings != null && mappings.size() > 0)
			{
				out.println(getIndent(baseind + 1) + "<semantic:extensionElements>");
				
				for(Object key: mappings.keySet())
				{
					out.print(getIndent(baseind + 2) + "<jadex:parametermapping name=\"");
					out.print(escapeString((String) key));
					out.print("\">");
					out.print(escapeString(((Tuple2<UnparsedExpression, UnparsedExpression>)mappings.get(key)).getFirstEntity().getValue()));
					out.println("</jadex:parametermapping>");
				}
				
				out.println(getIndent(baseind + 1) + "</semantic:extensionElements>");
			}
			
			out.println(getIndent(baseind) + "</semantic:sequenceFlow>");
		}
	}
	
	/**
	 *  Writes the messaging edges of the semantics sections.
	 *  
	 *  @param out The output.
	 *  @param seqedges The messaging edges.
	 */
	protected static final void writeMessagingEdgeSemantics(PrintStream out, List<MMessagingEdge> medges, int baseind)
	{
		for (MMessagingEdge edge : medges)
		{
			out.print(getIndent(baseind) + "<semantic:messageFlow ");
			if (edge.getName() != null && edge.getName().length() > 0)
			{
				out.print("name=\"");
				out.print(escapeString(edge.getName()));
				out.print("\" ");
			}
			out.print("sourceRef=\"");
			if (edge.getSource() == null)
				System.out.println("IDD: " + edge.getId());
			out.print(escapeString(edge.getSource().getId()));
			out.print("\" targetRef=\"");
			out.print(escapeString(edge.getTarget().getId()));
			out.print("\" id=\"");
			out.print(escapeString(edge.getId()));
			out.println("\">");
			
			out.println(getIndent(baseind) + "</semantic:messageFlow>");
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
	
	/** Returns an empty string for null and null for an empty string. */
	public static final String handleNullStr(String input)
	{
		if (input == null)
		{
			input = "";
		}
		else if (input.length() == 0)
		{
			input = null;
		}
		return input;
	}
	
	/** Carriage return. */
	private static final String CR = makeCharStringTrustMeJava(13);
	
	/** Line feed. */
	private static final String LF = makeCharStringTrustMeJava(10);
	
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
	
	/**
	 *  Helper method to override stupid Java checks.
	 */
	private static final String makeCharStringTrustMeJava(int num)
	{
		String ret = null;
		try
		{
			ret = new String(new byte[] { (byte) num }, "UTF-8");
		}
		catch(Exception e)
		{
		}
		return ret;
	}
}
