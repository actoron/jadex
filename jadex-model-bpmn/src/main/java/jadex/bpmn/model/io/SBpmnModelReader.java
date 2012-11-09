package jadex.bpmn.model.io;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class SBpmnModelReader
{
	/** Activity type mapping. */
	public static final Map<String, String> ACT_TYPE_MAPPING = new HashMap<String, String>();
	static
	{
		for (Map.Entry<String, String> entry : SBpmnModelWriter.ACT_TYPE_MAPPING.entrySet())
		{
			if (!entry.getKey().contains("Event"))
			{
				ACT_TYPE_MAPPING.put(entry.getValue(), entry.getKey());
			}
		}
		
		ACT_TYPE_MAPPING.put("userTask", MBpmnModel.TASK);
		
	}
	
	/**
	 *  Loads the model from a file.
	 *  
	 *  @param file The model file.
	 *  @param vreader The visual model reader, may be null.
	 */
	public static final MBpmnModel readModel(File file, IBpmnVisualModelReader vreader) throws Exception
	{
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
		XMLInputFactory fac = XMLInputFactory.newInstance(); 
		XMLStreamReader reader = fac.createXMLStreamReader(fis);
		
		LinkedList<QName> tagstack = new LinkedList<QName>();
		LinkedList<Map<String, String>> attrstack = new LinkedList<Map<String,String>>();
		LinkedList<String> contentstack = new LinkedList<String>();
		
		MBpmnModel ret = new MBpmnModel();
		Map<String, MIdElement> bpmnelementmap = new HashMap<String, MIdElement>();
		Map<String, MLane> lanemap = new HashMap<String, MLane>();
		Map<String, String> laneparents = new HashMap<String, String>();
		Map<String, Object> buffer = new HashMap<String, Object>();
		
		buffer.put("unresolvedsequencesources", new HashMap<String, MSequenceEdge>());
		buffer.put("unresolvedsequencetargets", new HashMap<String, MSequenceEdge>());
		
		String sempre = null;
		
		while (reader.hasNext())
		{
		    reader.next();
		    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
		    	"definitions".equals(reader.getLocalName()) &&
		    	reader.getPrefix() != null &&
		    	reader.getPrefix().equals(reader.getNamespaceContext().getPrefix("http://www.omg.org/spec/BPMN/20100524/MODEL")))
		    {
				sempre = reader.getNamespaceContext().getPrefix("http://www.omg.org/spec/BPMN/20100524/MODEL");
				buffer.put("di", reader.getNamespaceContext().getPrefix("http://www.omg.org/spec/DD/20100524/DI"));
				buffer.put("dc", reader.getNamespaceContext().getPrefix("http://www.omg.org/spec/DD/20100524/DC"));
				buffer.put("bpmndi", reader.getNamespaceContext().getPrefix("http://www.omg.org/spec/BPMN/20100524/DI"));
				buffer.put("semantic", sempre);
		    }
		    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT)
		    {
		    	tagstack.push(reader.getName());
		    	
		    	Map<String, String> attrs = null;
		    	if (reader.getAttributeCount() > 0)
		    	{
		    		attrs = new HashMap<String, String>(reader.getAttributeCount());
			    	for (int i = 0; i < reader.getAttributeCount(); ++i)
			    	{
			    		attrs.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
			    	}
		    	}
		    	attrstack.push(attrs);
		    	
		    	if ("extension".equals(reader.getName().getLocalPart()) && sempre.equals(reader.getName().getPrefix()))
	    		{
	    			buffer.put("extension", null);
	    		}
		    	else if ("process".equals(reader.getName().getLocalPart()) && sempre.equals(reader.getName().getPrefix()))
	    		{
	    			handlePool(ret, attrs, bpmnelementmap, buffer);
	    		}
		    }
		    else if (reader.getEventType() == XMLStreamConstants.CHARACTERS)
		    {
		    	contentstack.push(reader.getText());
		    }
		    else if (reader.getEventType() == XMLStreamConstants.END_ELEMENT)
		    {
		    	if ("extension".equals(reader.getName().getLocalPart()) && sempre.equals(reader.getName().getPrefix()))
	    		{
	    			buffer.remove("extension");
	    		}
		    	
		    	handleElement(ret, bpmnelementmap, lanemap, laneparents, tagstack.pop(), tagstack, attrstack.pop(), contentstack.pop(), buffer, vreader);
		    }
		}
		
		return ret;
	}
	
	/**
	 *  Handles a pool.
	 *  
	 *  @param model The model.
	 *  @param lastobject The last object.
	 *  @param tag The current tag.
	 *  @param tagstack The tag stack.
	 *  @param attrs Current attributes.
	 *  @param content The tag content.
	 *  @param buffer Buffered information.
	 */
	protected static final void handlePool(MBpmnModel model, Map<String, String> attrs, Map<String, MIdElement> emap, Map<String, Object> buffer)
	{
		
		MPool currentpool = new MPool();
		currentpool.setName(attrs.get("name"));
		currentpool.setId(attrs.get("id"));
		emap.put(currentpool.getId(), currentpool);
		model.addPool(currentpool);
		buffer.put("lastobject", currentpool);
		buffer.put("pool", currentpool);
	}
	
	/**
	 *  Handles XML elements.
	 */
	protected static final void handleElement(MBpmnModel model,
											  Map<String, MIdElement> emap,
											  Map<String, MLane> lanemap,
											  Map<String, String> laneparents,
											  QName tag,
											  LinkedList<QName> tagstack,
											  Map<String, String> attrs,
											  String content,
											  Map<String, Object> buffer,
											  IBpmnVisualModelReader vreader)
	{
		if (buffer.get("semantic").equals(tag.getPrefix()))
		{
			handleSemanticElement(model, emap, lanemap, laneparents, tag, tagstack, attrs, content, buffer);
		}
		else if ("jadex".equals(tag.getPrefix()))
		{
			handleJadexElement(model, tag, tagstack, attrs, content, buffer);
		}
		else if (vreader != null && (buffer.get("bpmndi").equals(tag.getPrefix()) || buffer.get("dc").equals(tag.getPrefix()) || buffer.get("di").equals(tag.getPrefix())))
		{
			vreader.readElement(tag, attrs, laneparents, emap, buffer);
		}
	}
	
	/**
	 *  Handles a semantic section.
	 *  
	 *  @param model The model.
	 *  @param lastobject The last object.
	 *  @param tag The current tag.
	 *  @param tagstack The tag stack.
	 *  @param attrs Current attributes.
	 *  @param content The tag content.
	 *  @param buffer Buffered information.
	 */
	protected static final void handleSemanticElement(MBpmnModel model,
													  Map<String, MIdElement> emap,
													  Map<String, MLane> lanemap,
													  Map<String, String> laneparents,
													  QName tag,
													  LinkedList<QName> tagstack,
													  Map<String, String> attrs,
													  String content,
													  Map<String, Object> buffer)
	{
		/*if ("process".equals(tag.getLocalPart()))
		{
			MPool currentpool = new MPool();
			currentpool.setName(attrs.get("name"));
			currentpool.setId(attrs.get("id"));
			emap.put(currentpool.getId(), currentpool);
			model.addPool(currentpool);
			buffer.put("lastobject", currentpool);
			buffer.put("pool", currentpool);
		}*/
		if ("lane".equals(tag.getLocalPart()))
		{
			MLane lane = new MLane();
			lane.setName(attrs.get("name"));
			lane.setId(attrs.get("id"));
			emap.put(lane.getId(), lane);
			MPool pool = (MPool) buffer.get("pool");
			pool.addLane(lane);
			List<String> noderefs = (List<String>) buffer.remove("flownoderefs");
			if (noderefs != null)
			{
				for (String ref : noderefs)
				{
					lanemap.put(ref, lane);
				}
			}
			laneparents.put(lane.getId(), pool.getId());
		}
		else if ("flowNodeRef".equals(tag.getLocalPart()))
		{
			List<String> noderefs = (List<String>) buffer.get("flownoderefs");
			if (noderefs == null)
			{
				noderefs = new ArrayList<String>();
				buffer.put("flownoderefs", noderefs);
			}
			noderefs.add(content);
		}
		else if (ACT_TYPE_MAPPING.containsKey(tag.getLocalPart()))
		{
			MActivity act = new MActivity();
			
			if (attrs.containsKey("name"))
			{
				act.setName(attrs.get("name"));
			}
			act.setId(attrs.get("id"));
			
			if (attrs.containsKey("default"))
			{
				Set<String> de = (Set<String>) buffer.get("defaultedges");
				if (de == null)
				{
					de = new HashSet<String>();
					buffer.put("defaultedges", de);
				}
				de.add(attrs.get("default"));
			}
			
			act.setActivityType(ACT_TYPE_MAPPING.get(tag.getLocalPart()));
			
			if (buffer.containsKey("taskclass"))
			{
				act.setClazz(new ClassInfo((String) buffer.remove("taskclass")));
			}
			
			if (buffer.containsKey("parameters"))
			{
				List<MParameter> params = (List<MParameter>) buffer.remove("parameters");
				for (MParameter param : params)
				{
					act.addParameter(param);
				}
			}
			
			connectActivityEdges(act, buffer, emap);
			
			MPool pool = (MPool) buffer.get("pool");
			insertActivity(act, pool, lanemap, emap);
		}
		else if (tag.getLocalPart() != null && tag.getLocalPart().contains("Event") && !tag.getLocalPart().contains("Definition"))
		{
			MActivity evt = new MActivity();
			
			if (attrs.containsKey("name"))
			{
				evt.setName(attrs.get("name"));
			}
			evt.setId(attrs.get("id"));
			
			String acttype = "Event";
			if (tag.getLocalPart().startsWith("end") || tag.getLocalPart().contains("Throw"))
			{
				evt.setThrowing(true);
			}
			
			if (tag.getLocalPart().startsWith("start"))
			{
				acttype += "Start";
			}
			else if (tag.getLocalPart().startsWith("end"))
			{
				acttype += "End";
			}
			else
			{
				acttype += "Intermediate";
			}
			
			List<Tuple2<String, String>> evttypes = (List<Tuple2<String, String>>) buffer.remove("evttypes");
			if (evttypes == null || evttypes.size() == 0)
			{
				acttype += "Empty";
			}
			else if (evttypes.size() == 1)
			{
				acttype += evttypes.get(0).getFirstEntity();
			}
			else
			{
				//TODO: Multi
			}
			
			String dur = (String) buffer.remove("duration");
			if (dur != null)
			{
				evt.setPropertyValue("duration", new UnparsedExpression("duration", "java.lang.Number", dur, null));
			}
			
			evt.setActivityType(acttype);
			
			connectActivityEdges(evt, buffer, emap);
			
			MPool pool = (MPool) buffer.get("pool");
			insertActivity(evt, pool, lanemap, emap);
		}
		else if (tag.getLocalPart() != null && tag.getLocalPart().endsWith("EventDefinition"))
		{
			List<Tuple2<String, String>> evttypes = (List<Tuple2<String, String>>) buffer.get("evttypes");
			if (evttypes == null)
			{
				evttypes = new ArrayList<Tuple2<String,String>>();
				buffer.put("evttypes", evttypes);
			}
			
			if ("messageEventDefinition".equals(tag.getLocalPart()))
			{
				evttypes.add(new Tuple2("Message", null));
			}
			else if ("timerEventDefinition".equals(tag.getLocalPart()))
			{
				evttypes.add(new Tuple2("Timer", null));
			}
			else if ("conditionalEventDefinition".equals(tag.getLocalPart()))
			{
				evttypes.add(new Tuple2("Rule", null));
			}
			else if ("signalEventDefinition".equals(tag.getLocalPart()))
			{
				evttypes.add(new Tuple2("Signal", null));
			}
			else if ("errorEventDefinition".equals(tag.getLocalPart()))
			{
				evttypes.add(new Tuple2("Error", null));
			}
		}
		else if ("sequenceFlow".equals(tag.getLocalPart()))
		{
			MSequenceEdge edge = new MSequenceEdge();
			edge.setId(attrs.get("id"));
			MActivity src = (MActivity) emap.get(attrs.get("sourceRef"));
			MActivity tgt = (MActivity) emap.get(attrs.get("targetRef"));
			edge.setSource(src);
			edge.setTarget(tgt);
			
			Set<String> de = (Set<String>) buffer.get("defaultedges");
			if (de != null && de.contains(edge.getId()))
			{
				edge.setDefault(true);
			}
			
			String cond = (String) buffer.remove("condition");
			if (cond != null)
			{
				edge.setCondition(new UnparsedExpression("", "java.lang.Boolean", cond, null));
			}
			
			Map<String, String> mappings = (Map<String, String>) buffer.remove("parametermappings");
			if (mappings != null)
			{
				for (Map.Entry<String, String> entry : mappings.entrySet())
				{
					String name = entry.getKey();
					String expstring = entry.getValue();
					UnparsedExpression exp = new UnparsedExpression(name, "java.lang.Object", expstring, null);
					UnparsedExpression iexp	= null;
					
					if(name.endsWith("]") && name.indexOf("[")!=-1)
					{
						String	itext	= name.substring(name.indexOf("[")+1, name.length()-1);
						name = name.substring(0, name.indexOf("["));
						iexp = new UnparsedExpression(name, "java.lang.Object", itext, null);
					}
					edge.addParameterMapping(entry.getKey(), exp, iexp);
				}
			}
			
			if (src != null)
			{
				src.addOutgoingSequenceEdge(edge);
			}
			if (tgt != null)
			{
				tgt.addIncomingSequenceEdge(edge);
			}
			
			((MPool) buffer.get("pool")).addSequenceEdge(edge);
			emap.put(edge.getId(), edge);
		}
		else if ("conditionExpression".equals(tag.getLocalPart()))
		{
			buffer.put("condition", content);
		}
		else if ("parametermapping".equals(tag.getLocalPart()))
		{
			Map<String, String> mappings = (Map<String, String>) buffer.get("parametermappings");
			if (mappings == null)
			{
				mappings = new HashMap<String, String>();
				buffer.put("parametermappings", mappings);
			}
			mappings.put(attrs.get("name"), content);
		}
		else if ("timeDuration".equals(tag.getLocalPart()))
		{
			buffer.put("duration", content);
		}
		else if ("incoming".equals(tag.getLocalPart()))
		{
			List<String> incoming = (List<String>) buffer.get("incoming");
			if (incoming == null)
			{
				incoming = new ArrayList<String>();
				buffer.put("incoming", incoming);
			}
			incoming.add(content);
		}
		else if ("outgoing".equals(tag.getLocalPart()))
		{
			List<String> outgoing = (List<String>) buffer.get("outgoing");
			if (outgoing == null)
			{
				outgoing = new ArrayList<String>();
				buffer.put("outgoing", outgoing);
			}
			outgoing.add(content);
		}
	}
	
	/**
	 *  Handles a Jadex model extension.
	 *  
	 *  @param model The model.
	 *  @param lastobject The last object.
	 *  @param tag The current tag.
	 *  @param tagstack The tag stack.
	 *  @param attrs Current attributes.
	 *  @param content The tag content.
	 *  @param buffer Buffered information.
	 */
	protected static final void handleJadexElement(MBpmnModel model,
												   QName tag,
												   LinkedList<QName> tagstack,
												   Map<String, String> attrs,
												   String content,
												   Map<String, Object> buffer)
	{
		if ("description".equals(tag.getLocalPart()))
		{
			if ("extension".equals(tagstack.get(1)))
			{
				((ModelInfo) model.getModelInfo()).setDescription(content);
			}
			else
			{
				buffer.put(tag.getLocalPart(), content);
			}
		}
		else if ("poollane".equals(tag.getLocalPart()))
		{
			buffer.put(tag.getLocalPart(), content);
		}
		else if ("taskclass".equals(tag.getLocalPart()))
		{
			buffer.put(tag.getLocalPart(), content);
		}
		else if ("value".equals(tag.getLocalPart()))
		{
			List<MParameter> params = (List<MParameter>) buffer.get("parameters");
			if (params == null)
			{
				params = new ArrayList<MParameter>();
				buffer.put("parameters", params);
			}
			ClassInfo clazz = new ClassInfo(attrs.get("type"));
			String name = attrs.get("name");
			MParameter param = new MParameter(attrs.get("direction"), clazz, name, new UnparsedExpression(name, clazz.getTypeName(), content, null));
			params.add(param);
		}
		else if ("argumentvalues".equals(tag.getLocalPart()))
		{
			Object vals = buffer.remove("values");
			if (vals != null)
			{
				buffer.put("argvalues", vals);
			}
		}
		else if ("resultvalues".equals(tag.getLocalPart()))
		{
			Object vals = buffer.remove("values");
			if (vals != null)
			{
				buffer.put("resvalues", vals);
			}
		}
		else if ("contextvariablevalues".equals(tag.getLocalPart()))
		{
			Object vals = buffer.remove("values");
			if (vals != null)
			{
				buffer.put("ctvvalues", vals);
			}
		}
		else if ("value".equals(tag.getLocalPart()))
		{
			if (attrs.containsKey("name"))
			{
				Map<String, String> vals = (Map<String, String>) buffer.get("values");
				if (vals == null)
				{
					vals = new HashMap<String, String>();
					buffer.put("values", vals);
				}
				vals.put(attrs.get("name"), content);
			}
			else
			{
				buffer.put("value", content);
			}
		}
		else if (buffer.containsKey("extension"))
		{
			if ("package".equals(tag.getLocalPart()))
			{
				((ModelInfo) model.getModelInfo()).setPackage(content);
			}
			else if ("componentflags".equals(tag.getLocalPart()))
			{
				if (attrs.containsKey("suspend"))
				{
					((ModelInfo) model.getModelInfo()).setSuspend(Boolean.parseBoolean(attrs.get("suspend")));
				}
				if (attrs.containsKey("master"))
				{
					((ModelInfo) model.getModelInfo()).setMaster(Boolean.parseBoolean(attrs.get("master")));
				}
				if (attrs.containsKey("daemon"))
				{
					((ModelInfo) model.getModelInfo()).setDaemon(Boolean.parseBoolean(attrs.get("daemon")));
				}
				if (attrs.containsKey("autoshutdown"))
				{
					((ModelInfo) model.getModelInfo()).setAutoShutdown(Boolean.parseBoolean(attrs.get("autoshutdown")));
				}
				if (attrs.containsKey("keepalive"))
				{
					model.setKeepAlive(Boolean.parseBoolean(attrs.get("keepalive")));
				}
			}
			else if ("import".equals(tag.getLocalPart()))
			{
				model.addImport(content);
			}
			else if ("argument".equals(tag.getLocalPart()))
			{
				Argument arg = new Argument();
				arg.setName(attrs.get("name"));
				arg.setClazz(new ClassInfo(attrs.get("type")));
				
				if (buffer.containsKey("description"))
				{
					arg.setDescription((String) buffer.remove("description"));
				}
				
				if (buffer.containsKey("value"))
				{
					arg.setValue((String) buffer.remove("value"));
				}
				
				((ModelInfo) model.getModelInfo()).addArgument(arg);
			}
			else if ("result".equals(tag.getLocalPart()))
			{
				Argument arg = new Argument();
				arg.setName(attrs.get("name"));
				arg.setClazz(new ClassInfo(attrs.get("type")));
				
				if (buffer.containsKey("description"))
				{
					arg.setDescription((String) buffer.remove("description"));
				}
				
				if (buffer.containsKey("value"))
				{
					arg.setValue((String) buffer.remove("value"));
				}
				
				((ModelInfo) model.getModelInfo()).addResult(arg);
			}
			else if ("contextvariable".equals(tag.getLocalPart()))
			{
				String name = attrs.get("name");
				ClassInfo clazz = new ClassInfo(attrs.get("type"));
				UnparsedExpression exp = new UnparsedExpression(name, clazz.getTypeName(), (String) buffer.remove("value"), null);
				
				model.addContextVariable(name, clazz, exp, null);
			}
			else if ("configuration".equals(tag.getLocalPart()))
			{
				ConfigurationInfo conf = new ConfigurationInfo(attrs.get("name"));
				
				if (attrs.containsKey("suspend"))
				{
					conf.setSuspend(Boolean.parseBoolean(attrs.get("suspend")));
				}
				if (attrs.containsKey("master"))
				{
					conf.setMaster(Boolean.parseBoolean(attrs.get("master")));
				}
				if (attrs.containsKey("daemon"))
				{
					conf.setDaemon(Boolean.parseBoolean(attrs.get("daemon")));
				}
				if (attrs.containsKey("autoshutdown"))
				{
					conf.setAutoShutdown(Boolean.parseBoolean(attrs.get("autoshutdown")));
				}
				
				if (buffer.containsKey("description"))
				{
					conf.setDescription((String) buffer.remove("description"));
				}
				
				if (buffer.containsKey("poollane"))
				{
					model.addPoolLane(conf.getName(), (String) buffer.remove("poollane"));
				}
				
				Map<String, String> vals = (Map<String, String>) buffer.remove("argvalues");
				for (Map.Entry<String, String> entry : vals.entrySet())
				{
					UnparsedExpression exp = new UnparsedExpression();
					exp.setName(entry.getKey());
					exp.setClazz(model.getModelInfo().getArgument(exp.getName()).getClazz());
					exp.setValue(entry.getValue());
					conf.addArgument(exp);
				}
				
				vals = (Map<String, String>) buffer.remove("resvalues");
				for (Map.Entry<String, String> entry : vals.entrySet())
				{
					UnparsedExpression exp = new UnparsedExpression();
					exp.setName(entry.getKey());
					exp.setClazz(model.getModelInfo().getResult(exp.getName()).getClazz());
					exp.setValue(entry.getValue());
					conf.addResult(exp);
				}
				
				vals = (Map<String, String>) buffer.remove("ctvvalues");
				for (Map.Entry<String, String> entry : vals.entrySet())
				{
					UnparsedExpression exp = new UnparsedExpression();
					exp.setName(entry.getKey());
					exp.setClazz(model.getContextVariableClass(entry.getKey()));
					exp.setValue(entry.getValue());
					model.setContextVariableExpression(entry.getKey(), conf.getName(), exp);
				}
				
				((ModelInfo) model.getModelInfo()).addConfiguration(conf);
			}
		}
	}
	
	/**
	 *  Inserts an activity into the model and maps.
	 *  
	 *  @param act The activity.
	 *  @param pool The current pool.
	 *  @param lanemap The lane map.
	 *  @param emap The element map.
	 */
	protected static final void insertActivity(MActivity act, MPool pool, Map<String, MLane> lanemap, Map<String, MIdElement> emap)
	{
		MLane lane = lanemap.get(act.getId());
		if (lane != null)
		{
			lane.addActivity(act);
			act.setPool(pool);
			act.setLane(lane);
		}
		else
		{
			pool.addActivity(act);
			act.setPool(pool);
		}
		emap.put(act.getId(), act);
	}
	
	/**
	 *  Connects activity edges.
	 */
	protected static final void connectActivityEdges(MActivity act, Map<String, Object> buffer, Map<String, MIdElement> emap)
	{
		//FIXME: Support for other edges
		List<String> outgoing = (List<String>) buffer.remove("outgoing");
		if (outgoing != null)
		{
			for (String outid : outgoing)
			{
				MSequenceEdge edge = (MSequenceEdge) emap.get(outid);
				if (edge != null)
				{
					edge.setSource(act);
					act.addOutgoingSequenceEdge(edge);
				}
			}
		}
		
		List<String> incoming = (List<String>) buffer.remove("incoming");
		if (incoming != null)
		{
			for (String incid : incoming)
			{
				MSequenceEdge edge = (MSequenceEdge) emap.get(incid);
				if (edge != null)
				{
					edge.setTarget(act);
					act.addIncomingSequenceEdge(edge);
				}
			}
		}
	}
}
