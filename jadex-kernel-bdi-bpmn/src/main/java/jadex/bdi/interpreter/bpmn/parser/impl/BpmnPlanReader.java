package jadex.bdi.interpreter.bpmn.parser.impl;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.IBpmnTransition;
import jadex.bdi.interpreter.bpmn.model.state.AbstractState;
import jadex.bdi.interpreter.bpmn.model.state.EndLinkState;
import jadex.bdi.interpreter.bpmn.model.state.IntermediateErrorState;
import jadex.bdi.interpreter.bpmn.model.state.RoutingPointState;
import jadex.bdi.interpreter.bpmn.model.state.StartLinkState;
import jadex.bdi.interpreter.bpmn.model.state.XORGatewayState;
import jadex.bdi.interpreter.bpmn.model.state.task.BasicTask;
import jadex.bdi.interpreter.bpmn.model.state.task.CompoundTask;
import jadex.bdi.interpreter.bpmn.model.transition.AbstractStateTransition;
import jadex.bdi.interpreter.bpmn.model.transition.ConditionalFlow;
import jadex.bdi.interpreter.bpmn.model.transition.DataFlow;
import jadex.bdi.interpreter.bpmn.model.transition.SequenceFlow;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.Reader;
import jadex.commons.xml.TypeInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BpmnPlanReader
{
	/** The reader instance */
	protected static Reader bpmnReader;
	
	// static reader configuration
	static {
		
		ITypeConverter typeconv = new NetConverter();
		
		// attribute info = xml-attribute -> bean-attribute ? NEIN
		Map nodeAttrs = new HashMap();
		nodeAttrs.put("type", "typeName");
		nodeAttrs.put("MsgImplementation", "taskType");
		
		Map edgeAttrs = new HashMap();
		edgeAttrs.put("head", "targetId");
		edgeAttrs.put("tail", "sourceId");
		
		// type infos
		Set types = new HashSet();
		
		types.add(new TypeInfo("node", AbstractState.class, null, null, 
				SUtil.createHashMap(
						new String[]{"type", "id", "label"/*, "xposition", "yposition"*/}, 
						new BeanAttributeInfo[]{
								new BeanAttributeInfo("type", typeconv), 
								new BeanAttributeInfo("id"),
								new BeanAttributeInfo("label")}
						), null));
		types.add(new TypeInfo("edge", AbstractStateTransition.class, null, null, 
				SUtil.createHashMap(
						new String[]{"type", "id", "label"/*, "xposition", "yposition"*/}, 
						new BeanAttributeInfo[]{
						new BeanAttributeInfo("type", typeconv), 
						new BeanAttributeInfo("id"),
						new BeanAttributeInfo("label")}
				), null));

		types.add(new TypeInfo("node/attribute", String.class, null, null, 
				SUtil.createHashMap(
						new String[]{"name", "value"}, 
						new BeanAttributeInfo[]{
								new BeanAttributeInfo("type"), 
								new BeanAttributeInfo("id")}
						), null));
		
		// link infos
		Set links = new HashSet();
		
		
		Set ignored = new HashSet();
		ignored.add("schemaLocation");
		ignored.add("xposition");
		ignored.add("yposition");
		
		
		bpmnReader = new Reader(new BeanObjectHandler(), types, links, ignored);
		
	}
	
	/**
	 * Get the {@link Reader} instance
	 * @return reader
	 */
	public static Reader getReader()
	{
		return bpmnReader;
	}
	
	/**
	 *  Parse node types into implementing class.
	 */
	static class NetConverter	implements ITypeConverter
	{
		private static Map typeMapping;
		
		static {
			typeMapping = new HashMap();
			
			typeMapping.put(IBpmnState.STARTLINK, StartLinkState.class);
			typeMapping.put(IBpmnState.TASK, BasicTask.class);
			typeMapping.put(IBpmnState.COMPOUND_TASK, CompoundTask.class);
			typeMapping.put(IBpmnState.ROUTING_POINT, RoutingPointState.class);
			typeMapping.put(IBpmnState.XOR_GATEWAY, XORGatewayState.class);
			typeMapping.put(IBpmnState.ENDLINK, EndLinkState.class);
			typeMapping.put(IBpmnState.INTERMEDIATE_ERROR, IntermediateErrorState.class);
			
			typeMapping.put(IBpmnState.LOCAL_CONTEXT, null);
			typeMapping.put(IBpmnState.CONTEXT, null);
			
			typeMapping.put(IBpmnTransition.SEQUENCE_FLOW, SequenceFlow.class);
			typeMapping.put(IBpmnTransition.CONDITIONAL_FLOW, ConditionalFlow.class);
			typeMapping.put(IBpmnTransition.DATA_FLOW, DataFlow.class);
		}
		
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			//Class ret = SReflect.findClass0((String)val, ((MApplicationType)root).getAllImports(), classloader);
			Class ret = (Class) typeMapping.get(val);
			if(ret==null)
				throw new RuntimeException("Could not parse class: "+val);
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 */
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}
	}
}
