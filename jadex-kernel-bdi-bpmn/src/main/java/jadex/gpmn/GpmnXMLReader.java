package jadex.gpmn;

import jadex.commons.IFilter;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.Reader;
import jadex.commons.xml.TypeInfo;
import jadex.gpmn.model.MAchieveGoal;
import jadex.gpmn.model.MArtifact;
import jadex.gpmn.model.MAssociation;
import jadex.gpmn.model.MContext;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MMaintainGoal;
import jadex.gpmn.model.MParameter;
import jadex.gpmn.model.MPlan;
import jadex.gpmn.model.MProcess;
import jadex.gpmn.model.MProcessElement;
import jadex.gpmn.model.MSequenceEdge;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Reader for loading Gpmn XML models into a Java representation states.
 */
public class GpmnXMLReader
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
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public static MGpmnModel read(String filename, final ClassLoader classloader, final Object context) throws Exception
	{
		ResourceInfo rinfo = SUtil.getResourceInfo0(filename, classloader);
		if(rinfo==null)
			throw new RuntimeException("Could not find resource: "+filename);
		MGpmnModel ret = (MGpmnModel)reader.read(rinfo.getInputStream(), classloader, context);
//		String name = new File(rinfo.getFilename()).getName();
//		name = name.substring(0, name.length()-5);
//		ret.setName(name);
		rinfo.getInputStream().close();
		System.out.println("Loaded model: "+ret);
		return ret;
	}
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		types.add(new TypeInfo("GpmnDiagram", MGpmnModel.class, null, null,
			SUtil.createHashMap(new String[]{"ID"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id")}), null));
		
		types.add(new TypeInfo("processes", MProcess.class, null, null,
			SUtil.createHashMap(new String[]{"ID"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id")}), null));
		
		types.add(new TypeInfo("associations", MAssociation.class));
		
		types.add(new TypeInfo("vertices", MAchieveGoal.class, null, null,
			SUtil.createHashMap(new String[]{"ID", "exclude"},
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id"), new BeanAttributeInfo("excludeMode")}),
			new ProcessElementPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("AchieveGoal");
				}
			}));
		
		types.add(new TypeInfo("vertices", MMaintainGoal.class, null, null,
			SUtil.createHashMap(new String[]{"ID", "exclude"},
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id"), new BeanAttributeInfo("excludeMode")}),
			new ProcessElementPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("MaintainGoal");
				}
			}));
		
		types.add(new TypeInfo("vertices", MPlan.class, null, null,
			SUtil.createHashMap(new String[]{"ID"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id")}),
			new ProcessElementPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Plan");
				}
			}));
		
		types.add(new TypeInfo("sequenceEdges", MSequenceEdge.class, null, null,
			SUtil.createHashMap(new String[]{"ID", "associations"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id"),
			new BeanAttributeInfo("associationsDescription")}), null));
		
		types.add(new TypeInfo("staticElements", MParameter.class, null, null,
			SUtil.createHashMap(new String[]{"ID", "type", "initialValue"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id"),
			new BeanAttributeInfo("className"),
			new BeanAttributeInfo("initialValueDescription")}), null));
		
		types.add(new TypeInfo("artifacts", MArtifact.class, null, null,
			SUtil.createHashMap(new String[]{"ID"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id")}),
			null,
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return !type.endsWith("Context");
				}
			}));
		
		types.add(new TypeInfo("artifacts", MContext.class, null, null,
			SUtil.createHashMap(new String[]{"ID"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("Id")}),
			null,
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Context");
				}
			}));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 */
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();

		// gpmn model
		linkinfos.add(new LinkInfo("processes", new BeanAttributeInfo("process")));

		// goals
		linkinfos.add(new LinkInfo("creationcondition", new BeanAttributeInfo("creationCondition")));
		linkinfos.add(new LinkInfo("contextcondition", new BeanAttributeInfo("contextCondition")));
		linkinfos.add(new LinkInfo("dropcondition", new BeanAttributeInfo("dropCondition")));
		
		// achieve goal
		linkinfos.add(new LinkInfo("targetcondition", new BeanAttributeInfo("targetCondition")));

		// maintain goal
		linkinfos.add(new LinkInfo("maintaincondition", new BeanAttributeInfo("maintainCondition")));
		
		// process
		linkinfos.add(new LinkInfo("outgoingEdges", new BeanAttributeInfo("outgoingSequenceEdgesDescription")));
		linkinfos.add(new LinkInfo("incomingEdges", new BeanAttributeInfo("incomingSequenceEdgesDescription")));
		linkinfos.add(new LinkInfo("artifacts", new BeanAttributeInfo("artifact")));
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("goal"), new IFilter()
		{
			public boolean filter(Object obj)
			{
				String type = (String)((Map)obj).get("type");
				return type.endsWith("Goal");
			}
		}));
		linkinfos.add(new LinkInfo("vertices", new BeanAttributeInfo("plan"), new IFilter()
		{
			public boolean filter(Object obj)
			{
				String type = (String)((Map)obj).get("type");
				return type.endsWith("Plan");
			}
		}));
		linkinfos.add(new LinkInfo("sequenceEdges", new BeanAttributeInfo("sequenceEdge")));
		
		// artifacts
		linkinfos.add(new LinkInfo("associations", new BeanAttributeInfo("association")));
		
		// context
		linkinfos.add(new LinkInfo("staticElements", new BeanAttributeInfo("parameter")));

		
		return linkinfos;
	}
	
	/**
	 *  Process element post processor.
	 */
	static class ProcessElementPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			MGpmnModel dia = (MGpmnModel)root;
			MProcessElement pe = (MProcessElement)object;

			// Make edge connections.
			Map edges = dia.getAllSequenceEdges();
			List indescs = pe.getIncomingSequenceEdgesDescriptions();
			if(indescs!=null)
			{
				for(int i=0; i<indescs.size(); i++)
				{
					String edgeid = (String)indescs.get(i); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					pe.addIncomingSequenceEdge(edge);
					edge.setTarget(pe);
				}
			}
			
			List outdescs = pe.getOutgoingSequenceEdgesDescriptions();
			if(outdescs!=null)
			{
				for(int i=0; i<outdescs.size(); i++)
				{
					String edgeid = (String)outdescs.get(i); 
					MSequenceEdge edge = (MSequenceEdge)edges.get(edgeid);
					if(edge==null)
						System.out.println("asd");
					pe.addOutgoingSequenceEdge(edge);
					edge.setSource(pe);
				}
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
	
	/**
	 *  Pool post processor.
	 * /
	static class PoolPostProcessor	extends NamePropertyPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 * /
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
	}*/
	
	
	/**
	 *  Lane post processor.
	 * /
	static class LanePostProcessor	extends NamePropertyPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 * /
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
	}*/
	
	/**
	 *  Association post processor.
	 * /
	static class AssociationPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Set source and target of association.
		 * /
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
		 * /
		public int getPass()
		{
			return 3;
		}
	}*/
	
	/**
	 *  Sequence edge post processor.
	 * /
	static class SequenceEdgePostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 * /
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
						edge.addParameterMapping(propname, exp);
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
		 * /
		public int getPass()
		{
			return 3;
		}
	}*/
	
	/**
	 *  Named element post processor.
	 *  Can parse the name and an aribitrary number of properties.
	 * /
	static class NamePropertyPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 * /
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
		 * /
		public int getPass()
		{
			return 3;
		}
	}*/
	
	/**
	 *  Bpmn Model post processor.
	 * /
	static class BpmnModelPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 * /
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
		 * /
		public int getPass()
		{
			return 2;
		}
	}*/
}
