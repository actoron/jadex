package jadex.bdi.interpreter;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.Reader;
import jadex.commons.xml.TypeInfo;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.io.xml.OAVObjectHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Reader for loading BDI XML models into OAV states.
 */
public class OAVBDIXMLReader
{
	//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static Reader	reader;
	
	// Initialize reader instance.
	static
	{
		// Post processors.
		IPostProcessor	expost	= new ExpressionProcessor();
		IPostProcessor	tepost	= new ClassPostProcessor(OAVBDIMetaModel.typedelement_has_classname, OAVBDIMetaModel.typedelement_has_class); 
		IPostProcessor	bopost	= new ClassPostProcessor(OAVBDIMetaModel.body_has_classname, OAVBDIMetaModel.body_has_class); 
		
		Set typeinfos = new HashSet();

		typeinfos.add(new TypeInfo("agent", OAVBDIMetaModel.agent_type, OAVBDIMetaModel.modelelement_has_description, null));

//		typeinfos.add(new TypeInfo("import", OAVJavaType.java_string_type));

		typeinfos.add(new TypeInfo("capabilities/capability", OAVBDIMetaModel.capabilityref_type));
		typeinfos.add(new TypeInfo("capability", OAVBDIMetaModel.capability_type, OAVBDIMetaModel.modelelement_has_description, null));
		
		typeinfos.add(new TypeInfo("belief", OAVBDIMetaModel.belief_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new TypeInfo("beliefref", OAVBDIMetaModel.beliefreference_type));
		typeinfos.add(new TypeInfo("beliefset", OAVBDIMetaModel.beliefset_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new TypeInfo("beliefsetref", OAVBDIMetaModel.beliefsetreference_type));
		typeinfos.add(new TypeInfo("fact", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("facts", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo("performgoal", OAVBDIMetaModel.performgoal_type));
		typeinfos.add(new TypeInfo("performgoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new TypeInfo("achievegoal", OAVBDIMetaModel.achievegoal_type));
		typeinfos.add(new TypeInfo("achievegoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new TypeInfo("querygoal", OAVBDIMetaModel.querygoal_type));
		typeinfos.add(new TypeInfo("querygoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new TypeInfo("maintaingoal", OAVBDIMetaModel.maintaingoal_type));
		typeinfos.add(new TypeInfo("maintaingoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new TypeInfo("metagoal", OAVBDIMetaModel.metagoal_type));
		typeinfos.add(new TypeInfo("metagoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new TypeInfo("creationcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("dropcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("targetcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("maintaincondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("recurcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("metagoal/trigger", OAVBDIMetaModel.metagoaltrigger_type));
		typeinfos.add(new TypeInfo("inhibits", OAVBDIMetaModel.inhibits_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("deliberation", null));

		typeinfos.add(new TypeInfo("plan", OAVBDIMetaModel.plan_type));
		typeinfos.add(new TypeInfo("body", OAVBDIMetaModel.body_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.body_has_classname}), bopost));
		typeinfos.add(new TypeInfo("precondition", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("contextcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("trigger", OAVBDIMetaModel.plantrigger_type));
		typeinfos.add(new TypeInfo("trigger/internalevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo("trigger/messageevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo("trigger/goalfinished", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo("trigger/goal", OAVBDIMetaModel.triggerreference_type));
//		typeinfos.add(new TypeInfo("trigger/factadded", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo("trigger/factremoved", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo("trigger/factchanged", OAVJavaType.java_string_type));
		typeinfos.add(new TypeInfo("waitqueue", OAVBDIMetaModel.trigger_type));
		typeinfos.add(new TypeInfo("waitqueue/internalevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo("waitqueue/messageevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new TypeInfo("waitqueue/goalfinished", OAVBDIMetaModel.triggerreference_type));
		
		typeinfos.add(new TypeInfo("internalevent", OAVBDIMetaModel.internalevent_type));
		typeinfos.add(new TypeInfo("internaleventref", OAVBDIMetaModel.internaleventreference_type));
		typeinfos.add(new TypeInfo("messageevent", OAVBDIMetaModel.messageevent_type));
		typeinfos.add(new TypeInfo("messageeventref", OAVBDIMetaModel.messageeventreference_type));
		typeinfos.add(new TypeInfo("match", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo("expression", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
//		typeinfos.add(new TypeInfo("expression/parameter", OAVBDIMetaModel.expressionparameter_type));
		typeinfos.add(new TypeInfo("condition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo("property", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new TypeInfo("parameter", OAVBDIMetaModel.parameter_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new TypeInfo("parameterset", OAVBDIMetaModel.parameterset_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new TypeInfo("plan/parameter", OAVBDIMetaModel.planparameter_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new TypeInfo("plan/parameterset", OAVBDIMetaModel.planparameterset_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new TypeInfo("value", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new TypeInfo("values", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
//		typeinfos.add(new TypeInfo("goalmapping", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo("messageeventmapping", OAVJavaType.java_string_type));
//		typeinfos.add(new TypeInfo("internaleventmapping", OAVJavaType.java_string_type));
		typeinfos.add(new TypeInfo("bindingoptions", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
				
//		typeinfos.add(new TypeInfo("concrete", OAVJavaType.java_string_type));

		typeinfos.add(new TypeInfo("configurations", null, null, null, SUtil.createHashMap(new String[]{"default"}, new Object[]{OAVBDIMetaModel.capability_has_defaultconfiguration}), null));
		typeinfos.add(new TypeInfo("configuration", OAVBDIMetaModel.configuration_type));
		typeinfos.add(new TypeInfo("initialcapability", OAVBDIMetaModel.initialcapability_type));
		typeinfos.add(new TypeInfo("initialbelief", OAVBDIMetaModel.configbelief_type));
		typeinfos.add(new TypeInfo("initialbeliefset", OAVBDIMetaModel.configbeliefset_type));
		typeinfos.add(new TypeInfo("initialgoal", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("initialplan", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("initialinternalevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("initialmessageevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("endgoal", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("endplan", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("endinternalevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("endmessageevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new TypeInfo("initialgoal/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("initialgoal/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("initialplan/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("initialplan/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("initialinternalevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("initialinternalevent/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("initialmessageevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("initialmessageevent/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("endgoal/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("endgoal/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("endplan/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("endplan/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("endinternalevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("endinternalevent/parameterset", OAVBDIMetaModel.configparameterset_type));
		typeinfos.add(new TypeInfo("endmessageevent/parameter", OAVBDIMetaModel.configparameter_type));
		typeinfos.add(new TypeInfo("endmessageevent/parameterset", OAVBDIMetaModel.configparameterset_type));

		Set linkinfos = new HashSet();
		linkinfos.add(new LinkInfo("properties/property", OAVBDIMetaModel.capability_has_properties));
		linkinfos.add(new LinkInfo("goals/performgoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new LinkInfo("goals/achievegoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new LinkInfo("goals/querygoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new LinkInfo("goals/maintaingoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new LinkInfo("goals/metagoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new LinkInfo("goals/performgoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new LinkInfo("goals/achievegoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new LinkInfo("goals/querygoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new LinkInfo("goals/maintaingoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new LinkInfo("goals/metagoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new LinkInfo("events/messageeventref", OAVBDIMetaModel.capability_has_messageeventrefs));
		linkinfos.add(new LinkInfo("events/internaleventref", OAVBDIMetaModel.capability_has_internaleventrefs));
		linkinfos.add(new LinkInfo("plan/parameter", OAVBDIMetaModel.parameterelement_has_parameters));
		linkinfos.add(new LinkInfo("plan/parameterset", OAVBDIMetaModel.parameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("initialplan/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("initialplan/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("initialgoal/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("initialgoal/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("initialinternalevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("initialinternalevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("initialmessageevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("initialmessageevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("endplan/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("endplan/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("endgoal/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("endgoal/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("endinternalevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("endinternalevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("endmessageevent/parameter", OAVBDIMetaModel.configparameterelement_has_parameters));
		linkinfos.add(new LinkInfo("endmessageevent/parameterset", OAVBDIMetaModel.configparameterelement_has_parametersets));
		linkinfos.add(new LinkInfo("beliefref", OAVBDIMetaModel.capability_has_beliefrefs));
		linkinfos.add(new LinkInfo("beliefsetref", OAVBDIMetaModel.capability_has_beliefsetrefs));
		linkinfos.add(new LinkInfo("values", OAVBDIMetaModel.parameterset_has_valuesexpression));
		linkinfos.add(new LinkInfo("facts", OAVBDIMetaModel.beliefset_has_factsexpression));
			
		Set ignoredattrs = new HashSet();
		ignoredattrs.add("schemaLocation");
		
		reader = new Reader(new OAVObjectHandler(), typeinfos, linkinfos, ignoredattrs);
	}
	
	/**
	 *  Get the reader instance.
	 */
	public static Reader	getReader()
	{
		return reader;
	}

	//-------- helper classes --------
	
	/**
	 *  Parse expression text.
	 */
	static class ExpressionProcessor	implements IPostProcessor
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Parse expression text.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			IOAVState state = (IOAVState)context;
			Object	ret	= null;
			String	value	= (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_content);
			
			if(value!=null)
			{
				String lang = (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_language);
				
				if(state.getType(object).isSubtype(OAVBDIMetaModel.condition_type))
				{
					// Conditions now parsed in createAgentModelEntry...
					
//					System.out.println("Found condition: "+se.object);

					if(lang==null || "clips".equals(lang))
					{
						List	errors	= null;//new ArrayList();
						try
						{
							ret = ParserHelper.parseClipsCondition(value, state.getTypeModel(), getImports(state, root), errors);
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
//						if(!errors.isEmpty())
//						{
//							for(int i=0; i<errors.size(); i++)
//							{
//								report.put(se, errors.get(i));
//							}
//						}
					}
					else if(lang.equals("jcl"))
					{
						// Java conditions parsed later in createAgentModelEntry()
					}
					else
					{
//						report.put(se, "Unknown condition language: "+lang);
						throw new RuntimeException("Unknown condition language: "+lang);
					}	
//					System.out.println(ret);

				}
				else
				{
//					System.out.println("Found expression: "+se.object);
					if(lang==null || "java".equals(lang))
					{
						try
						{
							ret = exp_parser.parseExpression(value, getImports(state, root), null, state.getTypeModel().getClassLoader());
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
					}
					else if("clips".equals(lang))
					{
						// Conditions now parsed in createAgentModelEntry...

						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseClipsCondition(value, state.getTypeModel(), getImports(state, root), errors);
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
						if(!errors.isEmpty())
						{
//							for(int i=0; i<errors.size(); i++)
//							{
//								report.put(se, errors.get(i));
//							}
							throw new RuntimeException("Parse errors: "+value+" "+errors);
						}
					}
					else if(lang.equals("jcl"))
					{
						// Java conditions parsed later in createAgentModelEntry()
					}
					else
					{
//						report.put(se, "Unknown condition language: "+lang);
						throw new RuntimeException("Unknown condition language: "+lang);
					}
				}
			}
			
			if(ret!=null)
				state.setAttributeValue(object, OAVBDIMetaModel.expression_has_content, ret);
		}
	}
	
	/**
	 *  Load class.
	 */
	static class ClassPostProcessor	implements IPostProcessor
	{
		//-------- attributes --------
		
		/** The class name attribute. */
		protected OAVAttributeType	classnameattr;
		
		/** The class attribute. */
		protected OAVAttributeType	classattr;
		
		//-------- constructors --------
		
		/**
		 *  Create a class post processor.
		 */
		public ClassPostProcessor(OAVAttributeType classnameattr, OAVAttributeType classattr)
		{
			this.classnameattr	= classnameattr;
			this.classattr	= classattr;
		}
		
		//-------- IPostProcessor interface --------
		
		/**
		 *  Load class.
		 */
		public void postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			IOAVState state = (IOAVState)context;
			String	value	= (String)state.getAttributeValue(object, classnameattr);
			if(value!=null)
			{
				try
				{
					Class	clazz = SReflect.findClass(value, getImports(state, root), state.getTypeModel().getClassLoader());
					state.setAttributeValue(object, classattr, clazz);
				}
				catch(Exception e)
				{
//					report.put(se, e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *  Extract imports from ADF.
	 */
	protected static String[] getImports(IOAVState state, Object root)
	{
		Collection	coll	= state.getAttributeValues(root, OAVBDIMetaModel.capability_has_imports);
		String[] imports	= coll!=null ? (String[])coll.toArray(new String[coll.size()]) : null;
		String	pkg	= (String)state.getAttributeValue(root, OAVBDIMetaModel.capability_has_package);
		if(pkg!=null)
		{
			if(imports!=null)
			{
				String[]	newimports	= new String[imports.length+1];
				System.arraycopy(imports, 0, newimports, 1, imports.length);
				imports	= newimports;
			}
			else
			{
				imports	= new String[1];
			}
			imports[0]	= pkg+".*";
		}
		return imports;
	}
}
