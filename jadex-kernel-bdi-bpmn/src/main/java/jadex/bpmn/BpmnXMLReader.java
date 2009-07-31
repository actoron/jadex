package jadex.bpmn;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MArtifact;
import jadex.bpmn.model.MAssociation;
import jadex.bpmn.model.MAssociationTarget;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MMessagingEdge;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.commons.IFilter;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.Reader;
import jadex.commons.xml.TypeInfo;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Reader for loading Bpmn XML models into a Java representation states.
 */
public class BpmnXMLReader
{
	//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static Reader	reader;
	
	//-------- methods --------
	
	// Initialize reader instance.
	static
	{
		Set ignored = new HashSet();
		ignored.add("xmi");
		ignored.add("iD");
		ignored.add("version");
		reader = new Reader(new BeanObjectHandler(), getXMLMapping(), getXMLLinkInfos(), ignored);
	}
	
	/**
	 *  Get the reader instance.
	 * /
	public static Reader	getReader()
	{
		return reader;
	}*/
	
	/**
	 *  Read properties from xml.
	 *  @param info	The resource info.
	 *  @param classloader The classloader.
 	 */
	protected static MBpmnModel read(ResourceInfo rinfo, ClassLoader classloader) throws Exception
	{
		MBpmnModel ret = (MBpmnModel)reader.read(rinfo.getInputStream(), classloader, null);
		ret.setFilename(rinfo.getFilename());
		ret.setLastModified(rinfo.getLastModified());
		return ret;
	}
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		types.add(new TypeInfo("BpmnDiagram", MBpmnModel.class, null, null, 
			null, new BpmnModelPostProcessor()));
		
		types.add(new TypeInfo("pools", MPool.class, null, null,
			SUtil.createHashMap(new String[]{"name", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("associationsDescription")}), new PoolPostProcessor()));
		
		types.add(new TypeInfo("artifacts", MArtifact.class, null, null,
			SUtil.createHashMap(new String[]{"name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description")}), null));
		
		types.add(new TypeInfo("associations", MAssociation.class, null, null, 
			null, new AssociationPostProcessor()));
		
		types.add(new TypeInfo("lanes", MLane.class, null, null,
			SUtil.createHashMap(new String[]{"name", "activities"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("activitiesDescription")}), new LanePostProcessor()));
		
		types.add(new TypeInfo("eventHandlers", MActivity.class, null, null,
			SUtil.createHashMap(new String[]{"outgoingEdges", "incomingEdges"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("outgoingSequenceEdgesDescription"),
			new BeanAttributeInfo("incomingSequenceEdgesDescription")}), new ActivityPostProcessor()));
		
		types.add(new TypeInfo("vertices", MActivity.class, null, null,
			SUtil.createHashMap(new String[]{"name", "outgoingEdges", "incomingEdges", "lanes", "associations", "activityType"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("outgoingSequenceEdgesDescription"),
			new BeanAttributeInfo("incomingSequenceEdgesDescription"),
			new BeanAttributeInfo("laneDescription"),
			new BeanAttributeInfo("associationsDescription"),
			new BeanAttributeInfo("activityType", null, null, MBpmnModel.TASK)}),
			new ActivityPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Activity");
				}
			}));
		
		types.add(new TypeInfo("vertices", MSubProcess.class, null, null,
			SUtil.createHashMap(new String[]{"name", "outgoingEdges", "incomingEdges", "lanes", "associations", "activityType"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"),
			new BeanAttributeInfo("outgoingSequenceEdgesDescription"),
			new BeanAttributeInfo("incomingSequenceEdgesDescription"),
			new BeanAttributeInfo("laneDescription"),
			new BeanAttributeInfo("associationsDescription"),
			new BeanAttributeInfo("activityType", null, null, MBpmnModel.SUBPROCESS)}),
			new ActivityPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("SubProcess");
				}
			}));
		
		types.add(new TypeInfo("sequenceEdges", MSequenceEdge.class, null, null,
			SUtil.createHashMap(new String[]{"name", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"), 
			new BeanAttributeInfo("associationsDescription")}), new SequenceEdgePostProcessor()));
		
		types.add(new TypeInfo("messagingEdges", MMessagingEdge.class, null, null,
			SUtil.createHashMap(new String[]{"name", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("description"), 
			new BeanAttributeInfo("associationsDescription")}), null));
		
		types.add(new TypeInfo("incomingMessages", HashMap.class, null, null, 
			SUtil.createHashMap(new String[]{"type", "href"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")}), null));

		types.add(new TypeInfo("outgoingMessages", HashMap.class, null, null, 
			SUtil.createHashMap(new String[]{"type", "href"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("messages", MMessagingEdge.class, null, null, 
			SUtil.createHashMap(new String[]{"source", "target"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("sourceDescription"),
			new BeanAttributeInfo("targetDescription")}), null));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 */
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();

		// bpmn diagram
		linkinfos.add(new LinkInfo("pools", new BeanAttributeInfo("pool")));
		linkinfos.add(new LinkInfo("artifacts", new BeanAttributeInfo("artifact")));
		linkinfos.add(new LinkInfo("messages", new BeanAttributeInfo("messagingEdge")));

		// pool
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("activity")));
		linkinfos.add(new LinkInfo("sequenceEdges", new BeanAttributeInfo("sequenceEdge")));
		linkinfos.add(new LinkInfo("lanes", new BeanAttributeInfo("lane")));
		
		// activities
		linkinfos.add(new LinkInfo("incomingMessages", new BeanAttributeInfo("incomingMessageDescription")));
		linkinfos.add(new LinkInfo("outgoingMessages", new BeanAttributeInfo("outgoingMessageDescription")));
		
		// subprocesses
		linkinfos.add(new LinkInfo("eventHandlers", new BeanAttributeInfo("eventHandler")));

		// artifacts
		linkinfos.add(new LinkInfo("associations", new BeanAttributeInfo("association")));
		
		return linkinfos;
	}
	
	/**
	 *  Activity post processor.
	 */
	static class ActivityPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MActivity act = (MActivity)object;

			// Make edge connections.
			Map edges = dia.getAllSequenceEdges();
			String indesc = act.getIncomingSequenceEdgesDescription();
			if(indesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(indesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					act.addIncomingSequenceEdge(edge);
					edge.setTarget(act);
				}
			}
			
			String outdesc = act.getOutgoingSequenceEdgesDescription();
			if(outdesc!=null)
			{
				StringTokenizer stok = new StringTokenizer(outdesc);
				while(stok.hasMoreElements())
				{
					String edgeid = stok.nextToken(); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					act.addOutgoingSequenceEdge(edge);
					edge.setSource(act);
				}
			}
			
			// Make message connections.
			// todo: message - pool connections
			Map allmessages = dia.getAllMessagingEdges();
			List inmsgs = act.getIncomingMessagesDescriptions();
			if(inmsgs!=null)
			{
				for(int i=0; i<inmsgs.size(); i++)
				{
					Map msgdesc = (Map)inmsgs.get(i);
					String id = ((String)msgdesc.get("href")).substring(1);
					MMessagingEdge msg = (MMessagingEdge)allmessages.get(id);
					if(msg==null)
						throw new RuntimeException("Could not find message: "+id);
					
					act.addIncomingMessagingEdge(msg);
					msg.setTarget(act);
				}
			}
			
			List outmsgs = act.getOutgoingMessagesDescriptions();
			if(outmsgs!=null)
			{
				for(int i=0; i<outmsgs.size(); i++)
				{
					Map msgdesc = (Map)outmsgs.get(i);
					String id = ((String)msgdesc.get("href")).substring(1);
					MMessagingEdge msg = (MMessagingEdge)allmessages.get(id);
					if(msg==null)
						throw new RuntimeException("Could not find message: "+id);
					
					act.addOutgoingMessagingEdge(msg);
					msg.setTarget(act);
				}
			}
			
			if(act.getDescription()!=null)
			{
				// first line: name
				// lines with = in it: properties
				// lines starting with in/out/inout: parameters
				
				StringTokenizer	stok = new StringTokenizer(act.getDescription(), "\r\n");
				JavaCCExpressionParser parser = new JavaCCExpressionParser();
				
				while(stok.hasMoreTokens())
				{
					String prop = stok.nextToken().trim();
					int	idx	= prop.indexOf("=");
					if(prop.startsWith("in") || prop.startsWith("out"))
					{
						// parameter
						StringTokenizer stok2 = new StringTokenizer(prop, " \t=");
						String paramdir = stok2.nextToken();
						String paramclazzname = stok2.nextToken();
						Class paramclazz = SReflect.findClass0(paramclazzname, dia.getAllImports(), classloader);
						if(paramclazz==null)
							throw new RuntimeException("Parameter class not found in imports: "+dia+", "+act+", "+paramclazzname+", "+SUtil.arrayToString(dia.getAllImports()));
						String paramname = stok2.nextToken();
						IParsedExpression paramexp = null;
						if(stok2.hasMoreTokens())
						{
							String proptext = prop.substring(idx+1).trim();
							paramexp = parser.parseExpression(proptext, dia.getAllImports(), null, classloader);
						}
						MParameter param = new MParameter(paramdir, paramclazz, paramname, paramexp);
						act.addParameter(param);
					}
					else if(idx!=-1)
					{
						// property
						String propname = prop.substring(0, idx).trim();
						String proptext = prop.substring(idx+1).trim();
						IParsedExpression propval = parser.parseExpression(proptext, dia.getAllImports(), null, classloader);
						act.setPropertyValue(propname, propval);
					}
					else
					{
						// line without "=" is name
						act.setName(prop);
					}
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
		}
	}
	
	/**
	 *  Pool post processor.
	 */
	static class PoolPostProcessor	extends NamePropertyPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			super.postProcess(context, object, root, classloader);
			
			// Set pool of activities.
			MPool	pool	= (MPool)object;
			List	activities	= pool.getActivities();
			if(activities!=null && !activities.isEmpty())
			{
				for(int i=0; i< activities.size(); i++)
				{
					((MActivity)activities.get(i)).setPool(pool);
				}
			}
		}
	}
	
	
	/**
	 *  Lane post processor.
	 */
	static class LanePostProcessor	extends NamePropertyPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			super.postProcess(context, object, root, classloader);
			
			// Resolve activities
			MLane	lane	= (MLane)object;
			String	actdesc	= lane.getActivitiesDescription();
			if(actdesc!=null)
			{
				MBpmnModel dia = (MBpmnModel)root;
				Map	activities	= dia.getAllActivities();
				StringTokenizer stok = new StringTokenizer(actdesc);
				while(stok.hasMoreElements())
				{
					String actid = stok.nextToken(); 
					MActivity activity = (MActivity)activities.get(actid);
					lane.addActivity(activity);
					activity.setLane(lane);
				}
			}
		}
	}
	
	/**
	 *  Association post processor.
	 */
	static class AssociationPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Set source and target of association.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MAssociation asso = (MAssociation)object;
			
			MArtifact source = (MArtifact)dia.getAllAssociationSources().get(asso.getId());
			MAssociationTarget target = (MAssociationTarget)dia.getAllAssociationTargets().get(asso.getId());
			
			if(source==null)
				throw new RuntimeException("Could not find association source: "+source);
			if(target==null)
				throw new RuntimeException("Could not find association target: "+target);
			
			asso.setSource(source);
			asso.setTarget(target);
			
			source.addAssociation(asso);
			target.addAssociation(asso);
		}
	
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
		}
	}
	
	/**
	 *  Sequence edge post processor.
	 */
	static class SequenceEdgePostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MSequenceEdge edge = (MSequenceEdge)object;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			if(edge.getDescription()!=null)
			{
				// first line: name
				// second line: condition
				// lines with = in it: parameters
				
				StringTokenizer	stok = new StringTokenizer(edge.getDescription(), "\r\n");
				String lineone = null;
				String linetwo = null;
				while(stok.hasMoreTokens())
				{
					
					String prop = stok.nextToken();
					int	idx	= prop.indexOf("=");
					if(idx!=-1)
					{
						String	propname = prop.substring(0, idx).trim();
						String	proptext = prop.substring(idx+1).trim();
						IParsedExpression exp = parser.parseExpression(proptext, dia.getAllImports(), null, classloader);
						IParsedExpression iexp	= null;

						if(propname.endsWith("]") && propname.indexOf("[")!=-1)
						{
							String	itext	= propname.substring(propname.indexOf("[")+1, propname.length()-1);
							propname	= propname.substring(0, propname.indexOf("["));
							iexp	= parser.parseExpression(itext, dia.getAllImports(), null, classloader);
						}

						edge.addParameterMapping(propname, exp, iexp);
					}
					else
					{
						// last line without "=" is assumed to be condition
						if(lineone==null)
							lineone = prop;
						else
							linetwo = prop;
					}
				}
				
				if(lineone!=null && linetwo!=null)
				{
					edge.setName(lineone);
					IParsedExpression cond = parser.parseExpression(linetwo, dia.getAllImports(), null, classloader);
					edge.setCondition(cond);
				}
				else if(lineone!=null)
				{
					IParsedExpression cond = parser.parseExpression(lineone, dia.getAllImports(), null, classloader);
					edge.setCondition(cond);
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
		}
	}
	
	/**
	 *  Named element post processor.
	 *  Can parse the name and an aribitrary number of properties.
	 */
	static class NamePropertyPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel dia = (MBpmnModel)root;
			MNamedIdElement namedelem = (MNamedIdElement)object;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			if(namedelem.getDescription()!=null)
			{
				// first line: name
				// lines with = in it: properties
				
				StringTokenizer	stok = new StringTokenizer(namedelem.getDescription(), "\r\n");
				while(stok.hasMoreTokens())
				{
					String prop = stok.nextToken();
					int	idx	= prop.indexOf("=");
					if(idx!=-1)
					{
						String propname = prop.substring(0, idx).trim();
						String proptext = prop.substring(idx+1).trim();
						Object propval = parser.parseExpression(proptext, dia.getAllImports(), null, classloader).getValue(null);
						namedelem.setPropertyValue(propname, propval);
					}
					else
					{
						// line without "=" is name
						namedelem.setName(prop);
					}
				}
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 3;
		}
	}
	
	/**
	 *  Bpmn Model post processor.
	 */
	static class BpmnModelPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MBpmnModel model = (MBpmnModel)root;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			List arts = model.getArtifacts();
			if(arts!=null)
			{
				if(arts.size()>1)
					throw new RuntimeException("Diagram must have one artifact for imports/package");
				
				String desc = ((MArtifact)arts.get(0)).getDescription();
				StringTokenizer	stok = new StringTokenizer(desc, "\r\n");
				List imports = new ArrayList();
				while(stok.hasMoreTokens())
				{
					String	prop	= stok.nextToken().trim();
					if(prop.endsWith(";"))
						prop = prop.substring(0, prop.length()-1);
					
					if(prop.startsWith("package"))
					{
						String packagename = prop.substring(prop.indexOf("package")+8).trim();
						model.setPackage(packagename);
					}
					else if(prop.startsWith("import"))
					{
						String imp = prop.substring(prop.indexOf("imports")+7).trim();
						imports.add(imp);
					}
					else
					{
						// context variable
						String	init	= null;
						int	idx	= prop.indexOf("=");
						if(idx!=-1)
						{
							init	= prop.substring(idx+1);
							prop	= prop.substring(0, idx);
						}
						StringTokenizer stok2 = new StringTokenizer(prop, " \t");
						if(stok2.countTokens()==2)
						{
							String clazzname = stok2.nextToken();
							String[]	imps	= (String[])imports.toArray(new String[imports.size()]);
							Class clazz = SReflect.findClass0(clazzname, imps, classloader);
							if(clazz!=null)
							{
								String name = stok2.nextToken();
								IParsedExpression exp = null;
								if(init!=null)
								{
									exp = parser.parseExpression(init, imps, null, classloader);
								}
								
								model.addContextVariable(name, clazz, exp);
							}
						}
					}
				}
				if(model.getPackage()!=null)
					imports.add(model.getPackage()+".*");
				model.setImports((String[])imports.toArray(new String[imports.size()]));
			}
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 2;
		}
	}
}
