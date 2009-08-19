package jadex.gpmn;

import jadex.commons.IFilter;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.reader.Reader;
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

import javax.xml.namespace.QName;

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
		reader = new Reader(new BeanObjectReaderHandler(getXMLMapping()));
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
		
		types.add(new TypeInfo(null, "GpmnDiagram", MGpmnModel.class, null, null,
			new BeanAttributeInfo[]{
			new BeanAttributeInfo("ID", "Id"),
			new BeanAttributeInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AttributeInfo.IGNORE_READWRITE),
			new BeanAttributeInfo("version", null, AttributeInfo.IGNORE_READWRITE),
			new BeanAttributeInfo("iD", null, AttributeInfo.IGNORE_READWRITE)
			}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("processes", "process"))
		}));
		
		types.add(new TypeInfo(null, "processes", MProcess.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id")}, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("sequenceEdges", "sequenceEdge")),
			new SubobjectInfo(new BeanAttributeInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new SubobjectInfo(new BeanAttributeInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new SubobjectInfo(new BeanAttributeInfo("artifacts", "artifact")),
			new SubobjectInfo(new BeanAttributeInfo("vertices", "goal"), new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Goal");
				}
			}),
			new SubobjectInfo(new BeanAttributeInfo("vertices", "plan"),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Plan");
				}
			})
		}));	
		
		types.add(new TypeInfo(null, "associations", MAssociation.class));
		
		types.add(new TypeInfo(null, "vertices", MAchieveGoal.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id"), new BeanAttributeInfo("exclude", "excludeMode")},
			new ProcessElementPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("AchieveGoal");
				}
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("creationcondition", "creationCondition")),
			new SubobjectInfo(new BeanAttributeInfo("contextcondition", "contextCondition")),
			new SubobjectInfo(new BeanAttributeInfo("dropcondition", "dropCondition")),
			new SubobjectInfo(new BeanAttributeInfo("targetcondition", "targetCondition")),
//			new SubobjectInfo(new BeanAttributeInfo("sequenceEdges", "sequenceEdge"))
			new SubobjectInfo(new BeanAttributeInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new SubobjectInfo(new BeanAttributeInfo("incomingEdges", "incomingSequenceEdgesDescription")),

		}));
		
		
		types.add(new TypeInfo(null, "vertices", MMaintainGoal.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id"), new BeanAttributeInfo("exclude", "excludeMode")},
			new ProcessElementPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("MaintainGoal");
				}
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("creationcondition", "creationCondition")),
			new SubobjectInfo(new BeanAttributeInfo("contextcondition", "contextCondition")),
			new SubobjectInfo(new BeanAttributeInfo("dropcondition", "dropCondition")),
			new SubobjectInfo(new BeanAttributeInfo("maintaincondition", "maintainCondition")),
//			new SubobjectInfo(new BeanAttributeInfo("sequenceEdges", "sequenceEdge"))
			new SubobjectInfo(new BeanAttributeInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new SubobjectInfo(new BeanAttributeInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			}));
		
		types.add(new TypeInfo(null, "vertices", MPlan.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id")},
			new ProcessElementPostProcessor(),
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Plan");
				}
			},
			new SubobjectInfo[]{
//			new SubobjectInfo(new BeanAttributeInfo("sequenceEdges", "sequenceEdge")),
			new SubobjectInfo(new BeanAttributeInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new SubobjectInfo(new BeanAttributeInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			}));	
		
		types.add(new TypeInfo(null, "sequenceEdges", MSequenceEdge.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id"),
			new BeanAttributeInfo("associations", "associationsDescription")}, null));
		
		types.add(new TypeInfo(null, "staticElements", MParameter.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id"),
			new BeanAttributeInfo("type", "className"),
			new BeanAttributeInfo("initialValue", "initialValueDescription")}, null));
		
		types.add(new TypeInfo(null, "artifacts", MArtifact.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id")},
			null,
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return !type.endsWith("Context");
				}
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("associations", "association"))
			}	
		));
		
		types.add(new TypeInfo(null, "artifacts", MContext.class, null, null,
			new BeanAttributeInfo[]{new BeanAttributeInfo("ID", "Id")},
			null,
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Context");
				}
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("staticElements", "parameter"))
			}
		));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 * /
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
	}*/
	
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
