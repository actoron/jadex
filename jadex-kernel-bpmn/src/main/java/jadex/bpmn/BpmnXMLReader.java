package jadex.bpmn;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MAnnotation;
import jadex.bpmn.model.MAnnotationDetail;
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
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.commons.IFilter;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanAccessInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

/**
 *  Reader for loading Bpmn XML models into a Java representation states.
 */
public class BpmnXMLReader
{
	//-------- constants --------
	
	// Copied from jadex.tools.bpmn.editor.properties.AbstractJadexPropertySection
	
	/** 
	 * String delimiter for list elements <p>
	 * <p><code>0x241F</code> (9247) SYMBOL FOR UNIT SEPARATOR</p>
	 */
	public static final String LIST_ELEMENT_DELIMITER = "\u241F"; // "<*>";
	
	
	/** 
	 * String delimiter for element attributes  <p>
	 * <p><code>0x240B</code> (9227) SYMBOL FOR VERTICAL TABULATION</p>
	 */
	public static final String LIST_ELEMENT_ATTRIBUTE_DELIMITER = "\u240B"; //"#|#";

	
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
		ret.setFilename(rinfo.getFilename());
		ret.setLastModified(rinfo.getLastModified());
		String name = new File(rinfo.getFilename()).getName();
		name = name.substring(0, name.length()-5);
		ret.setName(name);
		rinfo.getInputStream().close();
		return ret;
	}
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		String uri = "http://stp.eclipse.org/bpmn";
		String xmiuri = "http://www.omg.org/XMI";
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "BpmnDiagram")}), new ObjectInfo(MBpmnModel.class, new BpmnModelPostProcessor()), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo(new QName("http://www.omg.org/XMI", "version"), null, AccessInfo.IGNORE_READWRITE)),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("pools", "pool")),
			new SubobjectInfo(new AccessInfo("artifacts", "artifact")),
			new SubobjectInfo(new AccessInfo("messages", "messagingEdge")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("eAnnotations"), new ObjectInfo(MAnnotation.class), 
			new MappingInfo(null, new BeanAccessInfo[]{
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("details", "detail")),
			})));
		
		types.add(new TypeInfo(new XMLInfo("details"), new ObjectInfo(MAnnotationDetail.class)));
		
		types.add(new TypeInfo(new XMLInfo("pools"), new ObjectInfo(MPool.class, new PoolPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("vertices", "activity")),
			new SubobjectInfo(new AccessInfo("sequenceEdges", "sequenceEdge")),
			new SubobjectInfo(new AccessInfo("lanes", "lane")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("artifacts"), new ObjectInfo(MArtifact.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("associations", "association")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("associations"), new ObjectInfo(MAssociation.class, new AssociationPostProcessor()), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("lanes"), new ObjectInfo(MLane.class, new LanePostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")), 
			new AttributeInfo(new AccessInfo("activities", "activitiesDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("eventHandlers"), new ObjectInfo(MActivity.class, new EventHandlerPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("vertices", 
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("Activity");
				}
			}), 
			new ObjectInfo(MActivity.class, new ActivityPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("lanes", "laneDescription")),
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("activityType", "activityType", null, MBpmnModel.TASK)),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("incomingMessages", "incomingMessageDescription")),
			new SubobjectInfo(new AccessInfo("outgoingMessages", "outgoingMessageDescription")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("vertices", 
			new IFilter()
			{
				public boolean filter(Object obj)
				{
					String type = (String)((Map)obj).get("type");
					return type.endsWith("SubProcess");
				}
			}),
			new ObjectInfo(MSubProcess.class, new ActivityPostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")),
			new AttributeInfo(new AccessInfo("outgoingEdges", "outgoingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("incomingEdges", "incomingSequenceEdgesDescription")),
			new AttributeInfo(new AccessInfo("lanes", "laneDescription")),
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("activityType", "activityType", null, MBpmnModel.SUBPROCESS)),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))	
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("incomingMessages", "incomingMessageDescription")),
			new SubobjectInfo(new AccessInfo("outgoingMessages", "outgoingMessageDescription")),
			new SubobjectInfo(new AccessInfo("eventHandlers", "eventHandler")),
			new SubobjectInfo(new AccessInfo("vertices", "Activity")),
			new SubobjectInfo(new AccessInfo("sequenceEdges", "sequenceEdge")),
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("sequenceEdges"), new ObjectInfo(MSequenceEdge.class, new SequenceEdgePostProcessor()),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")), 
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE))
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("eAnnotations"), new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("messagingEdges"), new ObjectInfo(MMessagingEdge.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name", "description")), 
			new AttributeInfo(new AccessInfo("associations", "associationsDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("incomingMessages"), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(xmiuri, "type"), null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("href", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));

		types.add(new TypeInfo(new XMLInfo("outgoingMessages"), new ObjectInfo(HashMap.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(xmiuri, "type"), null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("href", null, null, null, new BeanAccessInfo(AccessInfo.THIS))),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		types.add(new TypeInfo(new XMLInfo("messages"), new ObjectInfo(MMessagingEdge.class), 
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("source", "sourceDescription")),
			new AttributeInfo(new AccessInfo("target", "targetDescription")),
			new AttributeInfo(new AccessInfo("iD", null, AccessInfo.IGNORE_READWRITE)),
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("eAnnotations", "annotation"))
			})));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 * /
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
	}*/
	
	/**
	 *  Activity post processor.
	 */
	static class ActivityPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
			MActivity act = (MActivity)object;
			
//			System.out.println("Act: "+act.getName()+" "+act.getDescription());

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
//					System.out.println("prop: "+prop);
					int	idx	= prop.indexOf("=");
					if(prop.startsWith("in") || prop.startsWith("out"))
					{
						// parameter
						StringTokenizer stok2 = new StringTokenizer(prop, " \t=");
						String paramdir = stok2.nextToken();
						String paramclazzname = stok2.nextToken();
						Class paramclazz = SReflect.findClass0(paramclazzname, dia.getAllImports(), context.getClassLoader());
						if(paramclazz==null)
							throw new RuntimeException("Parameter class not found in imports: "+dia+", "+act+", "+paramclazzname+", "+SUtil.arrayToString(dia.getAllImports()));
						String paramname = stok2.nextToken();
						IParsedExpression paramexp = null;
						if(stok2.hasMoreTokens())
						{
							String proptext = prop.substring(idx+1).trim();
							paramexp = parser.parseExpression(proptext, dia.getAllImports(), null, context.getClassLoader());
						}
						MParameter param = new MParameter(paramdir, paramclazz, paramname, paramexp);
						act.addParameter(param);
					}
					else if(idx!=-1)
					{
						// property
						String propname = prop.substring(0, idx).trim();
						String proptext = prop.substring(idx+1).trim();
						IParsedExpression propval = parser.parseExpression(proptext, dia.getAllImports(), null, context.getClassLoader());
						act.setPropertyValue(propname, propval);
					}
					else
					{
						// line without "=" is name
						act.setName(prop);
					}
				}
			}
			
			// Read annotations from Jadex bpmn tool.
			
			List annos = act.getAnnotations();
			if(annos!=null)
			{
				JavaCCExpressionParser parser = new JavaCCExpressionParser();
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					List details = anno.getDetails();
					if(details!=null)
					{
						for(int j=0; j<details.size(); j++)
						{
							MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
							
							String key = detail.getKey();
							String value = detail.getValue();
							
							if("parameters".equals(key))
							{
								StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
								
								while(stok.hasMoreTokens())
								{
									String paramtext = stok.nextToken();
									StringTokenizer stok2 = new StringTokenizer(paramtext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);

									// Parameters of normal activities have 4 elements
									int tokcnt = stok2.countTokens();
									if(tokcnt==3 || tokcnt==4)
									{
										String dir = stok2.nextToken();
										String name = stok2.nextToken();
										String clazzname = stok2.nextToken();
										String val = stok2.hasMoreTokens()? stok2.nextToken(): null;
										
										// context variable
										Class clazz = SReflect.findClass0(clazzname, dia.getAllImports(), context.getClassLoader());
										if(clazz!=null)
										{
											IParsedExpression exp = null;
											if(val!=null)
											{
												exp = parser.parseExpression(val, dia.getAllImports(), null, context.getClassLoader());
											}
											MParameter param = new MParameter(dir, clazz, name, exp);
											act.addParameter(param);
//											System.out.println("Parameter: "+param);
										}
									}
									
									// Parameters of event handlers have 2 elements = are treated as properties?!
									else if(tokcnt==2)
									{
										String name = stok2.nextToken();
										String val = stok2.nextToken();
										
										// context variable
										IParsedExpression exp = null;
										if(val!=null)
										{
											exp = parser.parseExpression(val, dia.getAllImports(), null, context.getClassLoader());
										}
										act.setPropertyValue(name, exp);
//										System.out.println("Parameter/property: "+name+" "+exp);
									}
									else
									{
										throw new RuntimeException("Parameter specification error: "+stok2.countTokens()+" "+paramtext);
									}
								}
							}
							else // property
							{
								// Skip empty string (cannot be parsed to anything), for parsing empty string "" need to be used
								if(!"".equals(value))
								{
									try
									{
										IParsedExpression propval = parser.parseExpression(value, dia.getAllImports(), null, context.getClassLoader());
										act.setPropertyValue(key, propval);
	//									System.out.println("Property: "+key+" "+value);
									}
									catch(Exception e)
									{
										System.out.println("Property: "+key+" "+value);
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
			
			return null;
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
	 *  Event handler post processor.
	 */
	static class EventHandlerPostProcessor extends ActivityPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			Object ret = super.postProcess(context, object);
			if(ret==null)
			{
				((MActivity)object).setEventHandler(true);
			}
			else
			{
				((MActivity)ret).setEventHandler(true);
			}
			return ret;
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
		public Object postProcess(IContext context, Object object)
		{
			super.postProcess(context, object);
			
			// Set pool of activities.
			MPool pool = (MPool) object;
			setSubActivities(pool, pool);
			
			return null;
		}
		
		/**
		 *  Associate also subactivities with outer pool.
		 */
		protected void setSubActivities(MAssociationTarget parent, MPool pool)
		{
			List activities = parent instanceof MSubProcess? ((MSubProcess)parent).getActivities(): ((MPool)parent).getActivities();
			if (activities != null)
			{
				for (Iterator it = activities.iterator(); it.hasNext(); )
				{
					MActivity activity = (MActivity) it.next();
					activity.setPool(pool);
					if (activity instanceof MSubProcess)
						setSubActivities((MSubProcess) activity, pool);
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
		public Object postProcess(IContext context, Object object)
		{
			super.postProcess(context, object);
			
			// Resolve activities
			MLane	lane	= (MLane)object;
			String	actdesc	= lane.getActivitiesDescription();
			if(actdesc!=null)
			{
				MBpmnModel dia = (MBpmnModel)context.getRootObject();
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
			
			return null;
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
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
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
		
			return null;
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
	 *  Sequence edge post processor.
	 */
	static class SequenceEdgePostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
			MSequenceEdge edge = (MSequenceEdge)object;
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			// Read annotations from Jadex bpmn tool.
			
			List annos = edge.getAnnotations();
			if(annos!=null)
			{
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					List details = anno.getDetails();
					if(details!=null)
					{
						for(int j=0; j<details.size(); j++)
						{
							MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
							
							String key = detail.getKey();
							String value = detail.getValue();
							
							// todo: enhance mappings with index?
							
							if("mappings".equals(key))
							{
								StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
								while(stok.hasMoreTokens())
								{
									String maptext = stok.nextToken();
									
									StringTokenizer stok2 = new StringTokenizer(maptext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
									String propname = stok2.nextToken();
									String proptext = stok2.nextToken();
									
									IParsedExpression exp = parser.parseExpression(proptext, dia.getAllImports(), null, context.getClassLoader());
									IParsedExpression iexp	= null;

									if(propname.endsWith("]") && propname.indexOf("[")!=-1)
									{
										String	itext	= propname.substring(propname.indexOf("[")+1, propname.length()-1);
										propname	= propname.substring(0, propname.indexOf("["));
										iexp	= parser.parseExpression(itext, dia.getAllImports(), null, context.getClassLoader());
									}

									edge.addParameterMapping(propname, exp, iexp);
									
//									System.out.println("Mapping: "+propname+" "+exp);
								}
							}
							else if("condition".equals(key) && value!=null && value.length()>0)
							{
								IParsedExpression cond = parser.parseExpression(value, dia.getAllImports(), null, context.getClassLoader());
								edge.setCondition(cond);
								
//								System.out.println("Condition: "+key+" "+value);
							}
						}
					}
				}
			}
			
			// Only interpret description when no annotations were found.
			
			else if(edge.getDescription()!=null)
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
					boolean	comp	= idx>0 && (prop.charAt(idx-1)=='!' || prop.charAt(idx-1)=='<' || prop.charAt(idx-1)=='>');
					boolean	eq	= idx!=-1 && idx<prop.length()-1 && prop.charAt(idx+1)=='=';
					boolean	assignment	= idx!=-1 && !comp && !eq;
					if(assignment)
					{
						String	propname = prop.substring(0, idx).trim();
						String	proptext = prop.substring(idx+1).trim();
						IParsedExpression exp = parser.parseExpression(proptext, dia.getAllImports(), null, context.getClassLoader());
						IParsedExpression iexp	= null;

						if(propname.endsWith("]") && propname.indexOf("[")!=-1)
						{
							String	itext	= propname.substring(propname.indexOf("[")+1, propname.length()-1);
							propname	= propname.substring(0, propname.indexOf("["));
							iexp	= parser.parseExpression(itext, dia.getAllImports(), null, context.getClassLoader());
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
					IParsedExpression cond = parser.parseExpression(linetwo, 
						dia.getAllImports(), null, context.getClassLoader());
					edge.setCondition(cond);
				}
				else if(lineone!=null)
				{
					IParsedExpression cond = parser.parseExpression(lineone, 
						dia.getAllImports(), null, context.getClassLoader());
					edge.setCondition(cond);
				}
			}
			
			return null;
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
	 *  Named element post processor.
	 *  Can parse the name and an aribitrary number of properties.
	 */
	static class NamePropertyPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel dia = (MBpmnModel)context.getRootObject();
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
						Object propval = parser.parseExpression(proptext, dia.getAllImports(), 
							null, context.getClassLoader()).getValue(null);
						namedelem.setPropertyValue(propname, propval);
					}
					else
					{
						// line without "=" is name
						namedelem.setName(prop);
					}
				}
			}
			
			return null;
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
	 *  Bpmn Model post processor.
	 */
	static class BpmnModelPostProcessor implements IPostProcessor
	{
		//-------- IPostProcessor interface --------
		
		/**
		 *  Establish element connections.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MBpmnModel model = (MBpmnModel)context.getRootObject();
			JavaCCExpressionParser parser = new JavaCCExpressionParser();

			// Read information from artifact text (normal stp modeller)
			
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
					else if(prop.startsWith("argument"))
					{
						String argstr = prop.substring(prop.indexOf("argument")+8).trim();
						
						String[] imps = (String[])imports.toArray(new String[imports.size()]);
						IArgument arg = (IArgument)parser.parseExpression(argstr, imps, null, 
							context.getClassLoader()).getValue(null);
						
						model.addArgument(arg);
					}
					else if(prop.startsWith("result"))
					{
						String resstr = prop.substring(prop.indexOf("result")+6).trim();
						
						String[] imps = (String[])imports.toArray(new String[imports.size()]);
						IArgument res = (IArgument)parser.parseExpression(resstr, imps, null, 
							context.getClassLoader()).getValue(null);
						
						model.addResult(res);
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
							Class clazz = SReflect.findClass0(clazzname, imps, context.getClassLoader());
							if(clazz!=null)
							{
								String name = stok2.nextToken();
								IParsedExpression exp = null;
								if(init!=null)
								{
									exp = parser.parseExpression(init, imps, null, context.getClassLoader());
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
			
			// Handle the annotations of the model.
			
			List annos = model.getAnnotations();
			if(annos!=null)
			{
				for(int i=0; i<annos.size(); i++)
				{
					MAnnotation anno = (MAnnotation)annos.get(i);
					List details = anno.getDetails();
					if(details!=null)
					{
						for(int j=0; j<details.size(); j++)
						{
							MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
							
							String key = detail.getKey().toLowerCase();
							String value = detail.getValue();
							
							if("description".equals(key))
							{
								model.setDescription(value);
							}
							else if("imports".equals(key))
							{
								StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
								String[] imps = new String[stok.countTokens()];
								for(int k=0; stok.hasMoreElements(); k++)
								{
									imps[k] = stok.nextToken();
								}
								model.setImports(imps);
//								System.out.println("Imports: "+SUtil.arrayToString(imps));
							}
							else if("package".equals(key))
							{
								model.setPackage(value);
//								System.out.println("Package: "+value);
							}
							else if("parameters".equals(key))
							{
								StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
								while(stok.hasMoreTokens())
								{
									String paramtext = stok.nextToken();
									StringTokenizer stok2 = new StringTokenizer(paramtext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
									/*String dir =*/ stok2.nextToken();	// Todo: consider direction.
									String name = stok2.nextToken();
									String clazzname = stok2.nextToken();
									String val = stok2.nextToken();
									
									// context variable
									Class clazz = SReflect.findClass0(clazzname, model.getAllImports(), context.getClassLoader());
									if(clazz!=null)
									{
										IParsedExpression exp = null;
										if(val!=null)
										{
											exp = parser.parseExpression(val, model.getAllImports(), null, context.getClassLoader());
										}
										model.addContextVariable(name, clazz, exp);
//										System.out.println("Context variable: "+name);
									}
								}
							}
							else if("arguments".equals(key))
							{
								StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
								while(stok.hasMoreTokens())
								{
									String argtext = stok.nextToken();
									StringTokenizer stok2 = new StringTokenizer(argtext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
									String name = stok2.nextToken();
									String desc = stok2.nextToken();
									String typename = stok2.nextToken();
									String valtext = stok2.hasMoreTokens()? stok2.nextToken(): null;
									
									Object val = null;
									if(valtext!=null)
										val = parser.parseExpression(valtext, model.getAllImports(), null, context.getClassLoader()).getValue(null);
									IArgument arg = new Argument(name, desc, typename, val);
									
									model.addArgument(arg);
//									System.out.println("Argument: "+arg);
								}
							}
							else if("results".equals(key))
							{
								StringTokenizer stok = new StringTokenizer(value, LIST_ELEMENT_DELIMITER);
								while(stok.hasMoreTokens())
								{
									String argtext = stok.nextToken();
									StringTokenizer stok2 = new StringTokenizer(argtext, LIST_ELEMENT_ATTRIBUTE_DELIMITER);
									String name = stok2.nextToken();
									String desc = stok2.nextToken();
									String typename = stok2.nextToken();
									String valtext = stok2.hasMoreTokens()? stok2.nextToken(): null;
									
									Object val = null;
									if(valtext!=null)
										val = parser.parseExpression(valtext, model.getAllImports(), null, context.getClassLoader()).getValue(null);
									IArgument res = new Argument(name, desc, typename, val);
									
									model.addResult(res);
//									System.out.println("Argument: "+arg);
								}
							}
							else
							{
								System.out.println("Unknown: "+key+" "+value);
							}
						}
					}
				}
			}
			
			return null;
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 1;
		}
	}
}
