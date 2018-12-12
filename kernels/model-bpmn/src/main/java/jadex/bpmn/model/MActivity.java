package jadex.bpmn.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.task.annotation.TaskArgument;
import jadex.bpmn.model.task.annotation.TaskBody;
import jadex.bpmn.model.task.annotation.TaskComponent;
import jadex.bpmn.model.task.annotation.TaskResult;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;

/**
 *  Base class for all kinds of activities.
 */
public class MActivity extends MAssociationTarget
{
	protected static final MethodInfo MI_NOTFOUND = new MethodInfo();

	/** Constant for the return parameter name. */
	public static final String RETURNPARAM = "returnparam";
	
	/** The interface name. */
	public static final String IFACE = "iface";
	
	/** The method name. */
	public static final String METHOD = "method";
	
	/** Is service constant. */
	public static final String ISSERVICE = "isService";
	
	/** Constant for the sequential result execution mode . */
	public static final String ISSEQUENTIAL = "isSequential";

	/** Constant for the result param name. */
	public static final String RESULTNAME = "resultName";

	/** Constant for the result param type (needed for distinguishing collection results from single values). */
	public static final String RESULTTYPE = "resultType";

	
	//-------- attributes --------
	
	/** The lane description. */
	protected String lanedescription;

	/** The outgoing sequence edges description. */
	protected String outgoingsequenceedgesdescription;
	
	/** The incoming sequence edges description. */
	protected String incomingsequenceedgesdescription;
	
	/** The incoming messages description. */
	protected List<Object> incomingmessagesdescriptions;
	
	/** The outgoing messages description. */
	protected List<Object> outgoingmessagesdescriptions;

	
	/** The outgoing sequence edges. */
	protected List<MSequenceEdge> outseqedges;
	
	/** The incoming sequence edges. */
	protected List<MSequenceEdge> inseqedges;

	/** The outgoing message edges. */
	protected List<MMessagingEdge> outmsgedges;
	
	/** The incoming message edges. */
	protected List<MMessagingEdge> inmsgedges;
	
	/** The outgoing data edges. */
	protected List<MDataEdge> outdataedges;
	
	/** The incoming data edges. */
	protected List<MDataEdge> indataedges;
	
	
	/** The type. */
	protected String type;
	
	/** The activity type. */
	protected String activitytype;

	/** The looping flag. */
	protected boolean looping;
	
	/** The throwing flag. */
	protected boolean throwing;

	/** The event handlers. */
	protected List<MActivity> eventhandlers;
	
	/** The pool. */
	protected MPool pool;
		
	/** The lane (if any). */
	protected MLane lane;
		
	/** The flag if this activity is an event handler. */
	protected boolean eventhandler;
	
	//-------- added --------
	
	/** The parameters (name -> MParameter). */
	protected IndexMap<String, MParameter>	parameters;
	
	/** The properties (name -> MProperty). */
	protected IndexMap<String, MProperty> properties;
	
	/** The class. */
	protected ClassInfo clazz;
	
	/** Non-functional hard constraints for service searches. */
//	protected List<MHardConstraint> searchconstraints;
	
	
	/** The body method cached for speed. */
	protected volatile MethodInfo bodymethod;

	/** The body method cached for speed. */
	protected volatile MethodInfo cancelmethod;
	
	/** The component injection targets. */
	protected volatile List<FieldInfo> componentinjections;

//	/** The parent injection targets. */
//	protected volatile List<FieldInfo> parentinjections;

	/** The argument injection targets. */
	protected volatile Map<String, List<FieldInfo>> argumentinjections;

	/** The result injection targets. */
	protected volatile Map<String, FieldInfo> resultinjections;

	
	//-------- methods --------
	
	/**
	 *  Get the xml lane description.
	 *  @return The lane description.
	 */
	public String getLaneDescription()
	{
		return this.lanedescription;
	}

	/**
	 *  Set the xml lane description.
	 *  @param lanedescription The lane description to set.
	 */
	public void setLaneDescription(String lanedescription)
	{
		this.lanedescription = lanedescription;
	}
	
	/**
	 *  Get the xml outgoing sequence edges desription.
	 *  @return The outgoing sequence edges description.
	 */
	public String getOutgoingSequenceEdgesDescription()
	{
		return this.outgoingsequenceedgesdescription;
	}

	/**
	 *  Set the xml outgoing edges desription.
	 *  @param outgoingedges The outgoing edges to set.
	 */
	public void setOutgoingSequenceEdgesDescription(String outgoingedges)
	{
		this.outgoingsequenceedgesdescription = outgoingedges;
	}
	
	/**
	 *  Get the xml incoming edges description.
	 *  @return The incoming edges description.
	 */
	public String getIncomingSequenceEdgesDescription()
	{
		return this.incomingsequenceedgesdescription;
	}

	/**
	 *  Set the xml incoming edges description.
	 *  @param incomingedges The incoming edges to set.
	 */
	public void setIncomingSequenceEdgesDescription(String incomingedges)
	{
		this.incomingsequenceedgesdescription = incomingedges;
	}
	
	/**
	 *  Get the xml outgoing messages descriptions.
	 *  @return The outgoing messages descriptions. 
	 */
	public List<Object> getOutgoingMessagesDescriptions()
	{
		return outgoingmessagesdescriptions;
	}

	/**
	 *  Add an outgoing message description.
	 *  @param desc The description.
	 */
	public void addOutgoingMessageDescription(Object desc)
	{
		if(outgoingmessagesdescriptions==null)
			outgoingmessagesdescriptions = new ArrayList<Object>();
		outgoingmessagesdescriptions.add(desc);
	}
	
	/**
	 *  Remove an outgoing message description.
	 *  @param desc The description.
	 */
	public void removeOutgoingMessageDescription(Object desc)
	{
		if(outgoingmessagesdescriptions!=null)
			outgoingmessagesdescriptions.remove(desc);
	}
	
	/**
	 *  Get the incoming messages description.
	 *  @return The incoming messages descriptions.
	 */
	public List<Object> getIncomingMessagesDescriptions()
	{
		return incomingmessagesdescriptions;
	}

	/**
	 *  Add an incoming message description.
	 *  @param desc The description.
	 */
	public void addIncomingMessageDescription(Object desc)
	{
		if(incomingmessagesdescriptions==null)
			incomingmessagesdescriptions = new ArrayList<Object>();
		incomingmessagesdescriptions.add(desc);
	}
	
	/**
	 *  Remove an incoming message description.
	 *  @param desc The description.
	 */
	public void removeIncomingMessageDescription(Object desc)
	{
		if(incomingmessagesdescriptions!=null)
			incomingmessagesdescriptions.remove(desc);
	}
	
	
	/**
	 *  Get the outgoing sequence edges.
	 *  @return The outgoing edges.
	 */
	public List<MSequenceEdge> getOutgoingSequenceEdges()
	{
		return outseqedges;
	}
	
	/**
	 * Set the outgoing sequence edges.
	 * @param outseqedges The outgoing sequence edges.
	 */
	public void setOutgoingSequenceEdges(List<MSequenceEdge> outseqedges)
	{
		this.outseqedges = outseqedges;
	}

	/**
	 *  Add an outgoing edge.
	 *  @param edge The edge.
	 */
	public void addOutgoingSequenceEdge(MSequenceEdge edge)
	{
		if(outseqedges==null)
			outseqedges = new ArrayList<MSequenceEdge>();
		outseqedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing edge.
	 *  @param edge The edge.
	 */
	public void removeOutgoingSequenceEdge(MSequenceEdge edge)
	{
		if(outseqedges!=null)
			outseqedges.remove(edge);
	}
	
	/**
	 *  Get the incoming edges.
	 *  @return The incoming edges.
	 */
	public List<MSequenceEdge> getIncomingSequenceEdges()
	{
		return inseqedges;
	}
	
	/**
	 *  Set the incoming edges.
	 *  
	 *  @param inseqedges The incoming edges.
	 */
	public void getIncomingSequenceEdges(List<MSequenceEdge> inseqedges)
	{
		this.inseqedges = inseqedges;
	}
	
	/**
	 *  Add an incoming edge.
	 *  @param edge The edge.
	 */
	public void addIncomingSequenceEdge(MSequenceEdge edge)
	{
		if(inseqedges==null)
			inseqedges = new ArrayList<MSequenceEdge>();
		inseqedges.add(edge);
	}
	
	/**
	 *  Remove an incoming edge.
	 *  @param edge The edge.
	 */
	public void removeIncomingSequenceEdge(MSequenceEdge edge)
	{
		if(inseqedges!=null)
			inseqedges.remove(edge);
	}
	
	/**
	 *  Get the outgoing message edges.
	 *  @return The outgoing message edges.
	 */
	public List<MMessagingEdge> getOutgoingMessagingEdges()
	{
		return outmsgedges;
	}
	
	/**
	 *  Set the outgoing message edges.
	 *  @param outmsgedges The outgoing message edges.
	 */
	public void getOutgoingMessagingEdges(List<MMessagingEdge> outmsgedges)
	{
		this.outmsgedges = outmsgedges;
	}

	/**
	 *  Add an outgoing message edge.
	 *  @param edge The edge.
	 */
	public void addOutgoingMessagingEdge(MMessagingEdge edge)
	{
		if(outmsgedges==null)
			outmsgedges = new ArrayList<MMessagingEdge>();
		outmsgedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing message edge.
	 *  @param edge The edge.
	 */
	public void removeOutgoingMessagingEdge(MMessagingEdge edge)
	{
		if(outmsgedges!=null)
			outmsgedges.remove(edge);
	}
	
	/**
	 *  Get the incoming message edges.
	 *  @return the incoming message edges.
	 */
	public List<MMessagingEdge> getIncomingMessagingEdges()
	{
		return inmsgedges;
	}
	
	/**
	 *  Set the incoming message edges.
	 *  @param inmsgedges The incoming message edges.
	 */
	public void setIncomingMessagingEdges(List<MMessagingEdge> inmsgedges)
	{
		this.inmsgedges = inmsgedges;
	}
	
	/**
	 *  Add an incoming message edge.
	 *  @param edge The edge.
	 */
	public void addIncomingMessagingEdge(MMessagingEdge edge)
	{
		if(inmsgedges==null)
			inmsgedges = new ArrayList<MMessagingEdge>();
		inmsgedges.add(edge);
	}
	
	/**
	 *  Remove an incoming message edge.
	 *  @param edge The edge.
	 */
	public void removeIncomingMessagingEdge(MMessagingEdge edge)
	{
		if(inmsgedges!=null)
			inmsgedges.remove(edge);
	}
	
	/**
	 *  Add an outgoing edge.
	 *  @param edge The edge.
	 */
	public void addOutgoingDataEdge(MDataEdge edge)
	{
		if(outdataedges==null)
			outdataedges = new ArrayList<MDataEdge>();
		outdataedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing edge.
	 *  @param edge The edge.
	 */
	public void removeOutgoingDataEdge(MDataEdge edge)
	{
		if(outdataedges!=null)
			outdataedges.remove(edge);
	}
	
	/**
	 *  Add an incoming edge.
	 *  @param edge The edge.
	 */
	public void addIncomingDataEdge(MDataEdge edge)
	{
		if(indataedges==null)
			indataedges = new ArrayList<MDataEdge>();
		indataedges.add(edge);
	}
	
	/**
	 *  Remove an outgoing edge.
	 *  @param edge The edge.
	 */
	public void removeIncomingDataEdge(MDataEdge edge)
	{
		if(indataedges!=null)
			indataedges.remove(edge);
	}
	
	/**
	 *  Get the incoming data edges.
	 *  @return the incoming data edges.
	 */
	public List<MDataEdge> getIncomingDataEdges()
	{
		return indataedges;
	}
	
	/**
	 *  Set the incoming data edges.
	 *  @param indataedges The incoming data edges.
	 */
	public void setIncomingDataEdges(List<MDataEdge> indataedges)
	{
		this.indataedges = indataedges;
	}
	
	/**
	 *  Get the outgoing data edges.
	 *  @return the outgoing data edges.
	 */
	public List<MDataEdge> getOutgoingDataEdges()
	{
		return outdataedges;
	}
	
	/**
	 *  Set the outgoing data edges.
	 *  @param outdataedges The outgoing data edges.
	 */
	public void setOutgoingDataEdges(List<MDataEdge> outdataedges)
	{
		this.outdataedges = outdataedges;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the activity type.
	 *  @return The activity type.
	 */
	public String getActivityType()
	{
		return this.activitytype;
	}

	/**
	 *  Set the activity type.
	 *  @param activitytype The activity type to set.
	 */
	public void setActivityType(String activitytype)
	{
		this.activitytype = activitytype;
	}
	
	/**
	 *  Test if the activity is looping.
	 *  @return True, if looping.
	 */
	public boolean isLooping()
	{
		return this.looping;
	}

	/**
	 *  Set the looping state.
	 *  @param looping The looping state to set.
	 */
	public void setLooping(boolean looping)
	{
		this.looping = looping;
	}
	
	/**
	 *  Test if the activity is throwing.
	 *  @return True, if throwing.
	 */
	public boolean isThrowing()
	{
		return this.throwing;
	}

	/**
	 *  Set the throwing state.
	 *  @param throwing The throwing state to set.
	 */
	public void setThrowing(boolean throwing)
	{
		this.throwing = throwing;
	}
	
	
	/**
	 *  Get the event handlers.
	 *  @return The event handlers.
	 */
	public List<MActivity> getEventHandlers()
	{
		return eventhandlers;
	}
	
	/**
	 *  Add an event handler.
	 *  @param eventhandler The event handler.
	 */
	public void addEventHandler(MActivity eventhandler)
	{
		if(eventhandlers==null)
			eventhandlers = new ArrayList<MActivity>();
		eventhandlers.add(eventhandler);
	}
	
	/**
	 *  Remove an event handler.
	 *  @param eventhandler The event handler.
	 */
	public void removeEventHandler(MActivity eventhandler)
	{
		if(eventhandlers!=null)
			eventhandlers.remove(eventhandler);
	}
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public IndexMap<String, MParameter>	getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Get a parameter by name.
	 */
	public MParameter getParameter(String name)
	{
		return parameters!=null? parameters.get(name): null;
	}
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public IndexMap<String, MParameter>	getAllParameters(Map<String, Object> params, String[] imports, ClassLoader cl)
	{
		IndexMap<String, MParameter> ret = parameters !=null ? new IndexMap<String, MParameter>(parameters) : new IndexMap<String, MParameter>();
		
		if(clazz!=null)
		{
			Class<?> task = clazz.getType(cl, imports);
			try
			{
				Method m = task.getMethod("getExtraParameters", new Class[]{Map.class});
				ParameterMetaInfo[] ps = (ParameterMetaInfo[])m.invoke(null, new Object[]{params});
				for(ParameterMetaInfo pmi: ps)
				{
					MParameter mp = new MParameter(pmi.getDirection(), pmi.getClazz(), pmi.getName(), null); // has no initial value
					ret.put(mp.getName(), mp);
				}
			}
			catch(Exception e)
			{
				// ignore
			}
		}
		
		return ret;
	}
		
	/**
	 *  Get the in and inout parameters.
	 *  @return The in parameters.
	 * /
	public List getInParameters()
	{
		List inparams = new ArrayList();
		if(parameters!=null)
		{
			for(Iterator it=parameters.values().iterator(); it.hasNext(); )
			{
				MParameter param = (MParameter)it.next();
				if(MParameter.DIRECTION_IN.equals(param.getDirection())
					|| MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					inparams.add(param);
				}
			}
		}
		return inparams;
	}*/
	
	/**
	 *  Get the out and inout parameters.
	 *  @return The out parameters.
	 * /
	public List getOutParameters()
	{
		List outparams = new ArrayList();
		if(parameters!=null)
		{
			for(Iterator it=parameters.values().iterator(); it.hasNext(); )
			{
				MParameter param = (MParameter)it.next();
				if(MParameter.DIRECTION_OUT.equals(param.getDirection())
					|| MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					outparams.add(param);
				}
			}
		}
		return outparams;
	}*/
	
	/**
	 *  Get parameters of specific direction(s).
	 *  @return The in parameters.
	 */
	public List<MParameter> getParameters(String[] dirs)
	{
		Set<String> test = new HashSet<String>();
		if(dirs!=null)
		{
			for(int i=0; i<dirs.length; i++)
			{
				test.add(dirs[i]);
			}
		}
		List<MParameter> inparams = new ArrayList<MParameter>();
		if(parameters!=null)
		{
			for(Iterator<MParameter> it=parameters.values().iterator(); it.hasNext(); )
			{
				MParameter param = (MParameter)it.next();
				if(test.contains(param.getDirection()))
				{
					inparams.add(param);
				}
			}
		}
		return inparams;
	}
	
	/**
	 *  Sets the parameters.
	 *  @param parameters The parameters.
	 */
	public void setParameters(IndexMap<String, MParameter> parameters)
	{
		this.parameters = parameters;
	}
	
	/**
	 *  Test if a prop exists.
	 */
	public boolean hasParameter(String name)
	{
		return parameters!=null && parameters.containsKey(name);
	}
	
	/**
	 *  Add a parameter.
	 *  @param param The parameter.
	 */
	public void addParameter(MParameter param)
	{
		if(parameters==null)
			parameters = new IndexMap<String, MParameter>();
		parameters.put(param.getName(), param);
	}
	
	/**
	 *  Remove a parameter.
	 *  @param param The parameter.
	 */
	public void removeParameter(MParameter param)
	{
		if(parameters!=null)
			parameters.removeValue(param.getName());
	}
	
	/**
	 *  Remove a parameter.
	 *  @param param The parameter.
	 */
	public void removeParameter(String name)
	{
		if(parameters!=null)
			parameters.removeKey(name);
	}
	
	/**
	 *  Remove a parameter.
	 *  @param param The parameter.
	 */
	public void removeParameters()
	{
		if(parameters!=null)
			parameters.clear();
	}
	
	/**
	 *  Legacy conversion from unparsed expression.
	 * 
	 *  @param name Name
	 *  @param exp
	 */
	public void setPropertyValue(String name, UnparsedExpression exp)
	{		
		MProperty mprop = new MProperty();
		mprop.setName(name);
		mprop.setInitialValue(exp); 
		addProperty(mprop);
	}
	
	/**
	 *  Legacy conversion to unparsed expression.
	 * 
	 *  @param name Name
	 *  @param exp
	 */
	public void setPropertyValue(String name, IParsedExpression exp)
	{
		if(exp != null)
		{
			MProperty mprop = new MProperty();
			mprop.setName(name);
			UnparsedExpression uexp = new UnparsedExpression(name, (String) null, exp.getExpressionText(), null);
			uexp.setParsedExp(exp);
			mprop.setInitialValue(uexp); 
			addProperty(mprop);
		}
		else
		{
			MProperty mprop = new MProperty();
			mprop.setName(name);
			UnparsedExpression uexp = new UnparsedExpression(name, (String) null, null, null);
			SJavaParser.parseExpression(uexp, null, MActivity.class.getClassLoader());
			mprop.setInitialValue(uexp); 
			addProperty(mprop);
		}
	}
	
	/**
	 *  Get a property value string from the model.
	 *  @param name The name.
	 */
	public String getPropertyValueString(String name)
	{
		UnparsedExpression exp = getPropertyValue(name);
		return exp != null? exp.getValue() : null;
	}
	
	/**
	 *  Get a property value from the model.
	 *  @param name The name.
	 */
	public UnparsedExpression getPropertyValue(String name)
	{
		UnparsedExpression ret = null;
		if(properties!=null)
		{
			MProperty mprop = properties.get(name);
			ret = mprop != null? mprop.getInitialValue() : null;
		}
		return ret;
	}
	
	/**
	 *  Get a property value from the model.
	 *  @param name The name.
	 */
	public Object getParsedPropertyValue(String name)
	{
		UnparsedExpression upex = getPropertyValue(name);
		Object val = ((IParsedExpression)upex.getParsed()).getValue(null);
		
		return val;
	}
	
	/**
	 *  Returns the property names.
	 *  
	 *  @return The property names.
	 */
	public String[] getPropertyNames()
	{
		return properties != null? properties.keySet().toArray(new String[properties.size()]) : SUtil.EMPTY_STRING_ARRAY;
	}
	
	/**
	 *  Test, if a property is declared and has nonull unparsed expression.
	 *  @param name	The property name.
	 *  @return True, if the property is declared.
	 */
	public boolean hasPropertyValue(String name)
	{
		return properties!=null && properties.containsKey(name) && getPropertyValue(name)!=null;
	}
	
	/**
	 *  Test, if a property is declared and has nonull unparsed expression.
	 *  @param name	The property name.
	 *  @return True, if the property is declared.
	 */
	public boolean hasInitialPropertyValue(String name)
	{
		return hasPropertyValue(name) && getPropertyValue(name).getValue()!=null && getPropertyValue(name).getValue().length()>0;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public IndexMap<String, MProperty>	getProperties()
	{
		return properties;
	}
	
	/**
	 *  Set the properties.
	 *  
	 *  @param properties
	 */
	public void setProperties(IndexMap<String, MProperty> properties)
	{
		this.properties = properties;
	}
	
//	/**
//	 *  Gets the search constraints.
//	 *
//	 *  @return The search constraints.
//	 */
//	public List<MHardConstraint> getSearchConstraints()
//	{
//		return searchconstraints;
//	}
//
//	/**
//	 *  Sets the search constraints.
//	 *
//	 *  @param searchconstraints The search constraints to set.
//	 */
//	public void setSearchConstraints(List<MHardConstraint> searchconstraints)
//	{
//		this.searchconstraints = searchconstraints;
//	}

	/**
	 *  Test if a property exists.
	 */
	public boolean hasProperty(String name)
	{
		return properties!=null && properties.containsKey(name);
	}
	
	/**
	 *  Add a property.
	 *  @param prop The property.
	 */
	public void addProperty(MProperty prop)
	{
		if(properties==null)
			properties = new IndexMap<String, MProperty>();
		properties.put(prop.getName(), prop);
	}
	
	/**
	 *  Add a simple string-based property.
	 *  @param name Property name.
	 *  @param value The string value.
	 */
	public void addProperty(String name, String value)
	{
		addProperty(name, value, true);
	}
	
	/**
	 *  Add a simple string-based property.
	 *  @param name Property name.
	 *  @param value The string value.
	 */
	public void addProperty(String name, String value, boolean string)
	{
		MProperty mprop = new MProperty();
		mprop.setName(name);
		UnparsedExpression uexp = new UnparsedExpression(name, String.class, string? "\"" + value + "\"": value, null);
		SJavaParser.parseExpression(uexp, null, MActivity.class.getClassLoader());
		mprop.setInitialValue(uexp); 
		addProperty(mprop);
	}
	
	/**
	 *  Remove a property.
	 *  @param propname Name of the property.
	 */
	public void removeProperty(String propname)
	{
		if (properties != null)
			properties.removeKey(propname);
	}
	
	/**
	 *  Remove a property.
	 *  @param prop The property.
	 */
	public void removeProperty(MProperty prop)
	{
		if(properties!=null)
			removeProperty(prop.getName());
	}
	
	/**
	 *  Set a property value:
	 *  a) val==null -> remove property
	 *  b) val!=null && !hasProp(name) -> addProp(name, val)
	 *  c) val!=null && hasProp(name) -> setInitialVal(val)
	 */
	public void setProperty(String name, String value, boolean string)
	{
//		System.out.println("setProp: "+name+" "+value+" "+string);
		
		if(value==null)
		{
			removeProperty(name);
		}
		else
		{
			MProperty mprop = getProperties()!=null? getProperties().get(name): null;
			if(mprop==null)
			{
				addProperty(name, value, string);
			}
			else
			{
				UnparsedExpression uexp = new UnparsedExpression(null, 
					String.class, string? "\""+value+"\"": value, null);
				mprop.setInitialValue(uexp);
			}
		}
	}
	
	/**
	 *  Set a parameter value:
	 *  a) val==null -> remove property
	 *  b) val!=null && !hasProp(name) -> addProp(name, val)
	 *  c) val!=null && hasProp(name) -> setInitialVal(val)
	 */
	public void setParameter(String name, String value, Class<?> type, boolean string, String direction)
	{
//		System.out.println("setProp: "+name+" "+value+" "+string);
		
		if(value==null)
		{
			removeParameter(name);
		}
		else
		{
			MParameter mpara = getParameters()!=null? getParameters().get(name): null;
			UnparsedExpression uexp = new UnparsedExpression(null, 
				String.class, string? "\""+value+"\"": value, null);
			if(mpara==null)
			{
				addParameter(new MParameter(direction, new ClassInfo(type), name, uexp));
			}
			else
			{
				mpara.setInitialValue(uexp);
			}
		}
	}
	
	/**
	 *  Create a string representation of this activity.
	 *  @return A string representation of this activity.
	 */
	public String	toString()
	{		
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(name=");
		buf.append(getName());
		buf.append(", activityType=");
		buf.append(getActivityType());
		buf.append(")");
		return buf.toString();
	}

	/**
	 *  Get the pool of the activity.
	 *  @return The pool of the activity.
	 */
	public MPool getPool()
	{
		return pool;
	}

	/**
	 *  Set the pool of the activity.
	 *  @param pool The pool of the activity.
	 */
	public void setPool(MPool pool)
	{
		this.pool	= pool;
	}

	/**
	 *  Get the lane of the activity.
	 *  @return The lane of the activity.
	 */
	public MLane getLane()
	{
		return lane;
	}

	/**
	 *  Set the lane of the activity.
	 *  @param lane The lane of the activity.
	 */
	public void setLane(MLane lane)
	{
		// eclipse STP has bugs regarding lanes.
		// The following at least identifies some inconsistencies (activity in multiple lanes).
		if(this.lane!=null && lane!=this.lane && lane!=null)
			throw new RuntimeException("Cannot add activity "+this+" to lane '"+lane.getName()+"'. Already contained in '"+this.lane.getName()+"'");
		
		this.lane	= lane;
	}

//	/**
//	 *  Get a string to identify this activity in a tool such as the debugger.
//	 *  @return A unique but nicely readable name.
//	 */
//	public String getBreakpointId()
//	{
//		String	name	= getName();
//		if(name==null)
//			name	= getActivityType()+"("+getId()+")";
//		return name;
//	}

	/**
	 *  Get the eventhandler.
	 *  @return The eventhandler.
	 */
	public boolean isEventHandler()
	{
		return this.eventhandler;
	}

	/**
	 *  Set the eventhandler.
	 *  @param eventhandler The eventhandler to set.
	 */
	public void setEventHandler(boolean eventhandler)
	{
		this.eventhandler = eventhandler;
	}
	

	/**
	 *  Get the class.
	 *  @return The class.
	 */
	public ClassInfo getClazz()
	{
		return this.clazz;
	}

	/**
	 *  Set the class.
	 *  @param clazz The class to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 *  Test if activity is event.
	 *  @return True, if is event.
	 */
	public boolean isEvent()
	{
		return getActivityType().startsWith("Event");
	}
	
	/**
	 *  Test if activity is a start event.
	 *  @return True, if is event.
	 */
	public boolean isStartEvent()
	{
		return isEvent() && getActivityType().indexOf("Start")!=-1;
	}
	
	/**
	 *  Test if activity is a end event.
	 *  @return True, if is event.
	 */
	public boolean isEndEvent()
	{
		return isEvent() && getActivityType().indexOf("End")!=-1;
	}
	
	/**
	 *  Test if activity is an intermediate event.
	 *  @return True, if is event.
	 */
	public boolean isIntermediateEvent()
	{
		return isEvent() && getActivityType().indexOf("Intermediate")!=-1;
	}
	
	/**
	 *  Test if activity is event.
	 *  @return True, if is event.
	 */
	public boolean isMessageEvent()
	{
		return isEvent() && getActivityType().indexOf("Message")!=-1;
	}
	
	/**
	 *  Test if activity is event.
	 *  @return True, if is event.
	 */
	public boolean isSignalEvent()
	{
		return isEvent() && getActivityType().indexOf("Signal")!=-1;
	}
	
	/**
	 *  Test if activity is gateway.
	 *  @return True, if is gateway.
	 */
	public boolean isGateway()
	{
		return getActivityType().startsWith("Gateway");
	}
	
	/**
	 *  Get and save the body method info.
	 */
	public MethodInfo getBodyMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(bodymethod==null)
			{
				synchronized(this)
				{
					if(bodymethod==null)
					{
						Class<?> body = clazz.getType(cl);
						bodymethod = getMethod(body, TaskBody.class);
						if(bodymethod==null)
						{
							throw  new RuntimeException("Task has no body method: "+body);
						}
					}
				}
			}
		}
		
		return bodymethod;
	}
	
	/**
	 *  Get and save the cancel method info.
	 */
	public MethodInfo getCancelMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(cancelmethod==null)
			{
				synchronized(this)
				{
					if(cancelmethod==null)
					{
						Class<?> body = clazz.getType(cl);
						cancelmethod = getMethod(body, TaskBody.class);
						if(cancelmethod==null)
						{
							cancelmethod = MI_NOTFOUND;
						}
					}
				}
			}
		}
		
		return bodymethod;
	}
	
	/**
	 *  Get and save the body method info.
	 */
	public List<FieldInfo> getComponentInjections(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(componentinjections==null)
			{
				synchronized(this)
				{
					if(componentinjections==null)
					{
						Class<?> body = clazz.getType(cl);
						componentinjections = getFields(body, TaskComponent.class);
					}
				}
			}
		}
		
		return componentinjections;
	}
	
	/**
	 *  Get and save the body method info.
	 */
	public Map<String, List<FieldInfo>> getArgumentInjections(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(argumentinjections==null)
			{
				synchronized(this)
				{
					if(argumentinjections==null)
					{
						argumentinjections = new LinkedHashMap<String, List<FieldInfo>>();
								
						Class<?> body = clazz.getType(cl);
						List<FieldInfo> fis = getFields(body, TaskArgument.class);
						
						for(FieldInfo fi: fis)
						{
							Field f = fi.getField(cl);
							TaskArgument arg = f.getAnnotation(TaskArgument.class);
							String name = arg.value().length()>0? arg.value(): f.getName();
							List<FieldInfo> ais = argumentinjections.get(name);
							if(ais==null)
							{
								ais = new ArrayList<FieldInfo>();
								argumentinjections.put(name, ais);
							}
							ais.add(fi);
						}
					}
				}
			}
		}
		
		return argumentinjections;
	}
	
	/**
	 *  Get and save the body method info.
	 */
	public Map<String, FieldInfo> getResultInjections(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(resultinjections==null)
			{
				synchronized(this)
				{
					if(resultinjections==null)
					{
						resultinjections = new LinkedHashMap<String, FieldInfo>();
								
						Class<?> body = clazz.getType(cl);
						List<FieldInfo> fis = getFields(body, TaskResult.class);
						
						for(FieldInfo fi: fis)
						{
							Field f = fi.getField(cl);
							TaskResult res = f.getAnnotation(TaskResult.class);
							String name = res.value().length()>0? res.value(): f.getName();
							resultinjections.put(name, fi);
						}
					}
				}
			}
		}
		
		return resultinjections;
	}
	
	/**
	 *  Get method with an annotation.
	 */
	public static MethodInfo getMethod(Class<?> body, Class<? extends Annotation> type)
	{
		MethodInfo ret = null;
		
		Class<?> bcl = body;
		
		while(!Object.class.equals(bcl) && ret==null)
		{
			Method[] ms = bcl.getDeclaredMethods();
			for(Method m: ms)
			{
				if(m.isAnnotationPresent(type))
				{
					ret = new MethodInfo(m);
					break;
				}
			}
			
			bcl = bcl.getSuperclass();
		}
		return ret;
	}
	
	/**
	 *  Get method with an annotation.
	 */
	public static List<FieldInfo> getFields(Class<?> body, Class<? extends Annotation> type)
	{
		List<FieldInfo> ret = new ArrayList<FieldInfo>();
		
		Class<?> bcl = body;
		
		while(!Object.class.equals(bcl))
		{
			Field[] fs = bcl.getDeclaredFields();
			for(Field f: fs)
			{
				if(f.isAnnotationPresent(type))
				{
					ret.add(new FieldInfo(f));
				}
			}
			
			bcl = bcl.getSuperclass();
		}
		
		return ret;
	}
	
}
