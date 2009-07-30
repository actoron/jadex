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

import java.io.File;
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
		
		ret.setLastModified(rinfo.getLastModified());
		ret.setFilename(rinfo.getFilename());
		
		String name = new File(rinfo.getFilename()).getName();
		name = name.substring(0, name.length()-5);
		ret.setName(name);
		
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

}
