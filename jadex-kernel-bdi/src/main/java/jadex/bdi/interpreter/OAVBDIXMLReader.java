package jadex.bdi.interpreter;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.Reader;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.io.xml.IPostProcessor;
import jadex.rules.state.io.xml.OAVLinkInfo;
import jadex.rules.state.io.xml.OAVMappingInfo;
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

		typeinfos.add(new OAVMappingInfo("agent", OAVBDIMetaModel.agent_type));

		typeinfos.add(new OAVMappingInfo("import", OAVJavaType.java_string_type));

		typeinfos.add(new OAVMappingInfo("capabilities/capability", OAVBDIMetaModel.capabilityref_type));
		typeinfos.add(new OAVMappingInfo("capability", OAVBDIMetaModel.capability_type));
		
		typeinfos.add(new OAVMappingInfo("belief", OAVBDIMetaModel.belief_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new OAVMappingInfo("beliefref", OAVBDIMetaModel.beliefreference_type));
		typeinfos.add(new OAVMappingInfo("beliefset", OAVBDIMetaModel.beliefset_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new OAVMappingInfo("beliefsetref", OAVBDIMetaModel.beliefsetreference_type));
		typeinfos.add(new OAVMappingInfo("fact", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("facts", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new OAVMappingInfo("performgoal", OAVBDIMetaModel.performgoal_type));
		typeinfos.add(new OAVMappingInfo("performgoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new OAVMappingInfo("achievegoal", OAVBDIMetaModel.achievegoal_type));
		typeinfos.add(new OAVMappingInfo("achievegoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new OAVMappingInfo("querygoal", OAVBDIMetaModel.querygoal_type));
		typeinfos.add(new OAVMappingInfo("querygoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new OAVMappingInfo("maintaingoal", OAVBDIMetaModel.maintaingoal_type));
		typeinfos.add(new OAVMappingInfo("maintaingoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new OAVMappingInfo("metagoal", OAVBDIMetaModel.metagoal_type));
		typeinfos.add(new OAVMappingInfo("metagoalref", OAVBDIMetaModel.goalreference_type));
		typeinfos.add(new OAVMappingInfo("creationcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("dropcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("targetcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("maintaincondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("metagoal/trigger", OAVBDIMetaModel.metagoaltrigger_type));

		typeinfos.add(new OAVMappingInfo("plan", OAVBDIMetaModel.plan_type));
		typeinfos.add(new OAVMappingInfo("body", OAVBDIMetaModel.body_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.body_has_classname}), bopost));
		typeinfos.add(new OAVMappingInfo("precondition", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("contextcondition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("trigger", OAVBDIMetaModel.plantrigger_type));
		typeinfos.add(new OAVMappingInfo("trigger/internalevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new OAVMappingInfo("trigger/messageevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new OAVMappingInfo("trigger/goalfinished", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new OAVMappingInfo("trigger/goal", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new OAVMappingInfo("trigger/factadded", OAVJavaType.java_string_type));
		typeinfos.add(new OAVMappingInfo("trigger/factremoved", OAVJavaType.java_string_type));
		typeinfos.add(new OAVMappingInfo("trigger/factchanged", OAVJavaType.java_string_type));
		typeinfos.add(new OAVMappingInfo("waitqueue", OAVBDIMetaModel.trigger_type));
		typeinfos.add(new OAVMappingInfo("waitqueue/internalevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new OAVMappingInfo("waitqueue/messageevent", OAVBDIMetaModel.triggerreference_type));
		typeinfos.add(new OAVMappingInfo("waitqueue/goalfinished", OAVBDIMetaModel.triggerreference_type));
		
		typeinfos.add(new OAVMappingInfo("internalevent", OAVBDIMetaModel.internalevent_type));
		typeinfos.add(new OAVMappingInfo("internaleventref", OAVBDIMetaModel.internaleventreference_type));
		typeinfos.add(new OAVMappingInfo("messageevent", OAVBDIMetaModel.messageevent_type));
		typeinfos.add(new OAVMappingInfo("messageeventref", OAVBDIMetaModel.messageeventreference_type));
		typeinfos.add(new OAVMappingInfo("match", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new OAVMappingInfo("expression", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("expression/parameter", OAVBDIMetaModel.expressionparameter_type));
		typeinfos.add(new OAVMappingInfo("condition", OAVBDIMetaModel.condition_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new OAVMappingInfo("property", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		
		typeinfos.add(new OAVMappingInfo("parameter", OAVBDIMetaModel.parameter_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new OAVMappingInfo("parameterset", OAVBDIMetaModel.parameterset_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new OAVMappingInfo("plan/parameter", OAVBDIMetaModel.planparameter_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new OAVMappingInfo("plan/parameterset", OAVBDIMetaModel.planparameterset_type, null, null, SUtil.createHashMap(new String[]{"class"}, new Object[]{OAVBDIMetaModel.typedelement_has_classname}), tepost));
		typeinfos.add(new OAVMappingInfo("value", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("values", OAVBDIMetaModel.expression_type, null, OAVBDIMetaModel.expression_has_content, null, expost));
		typeinfos.add(new OAVMappingInfo("goalmapping", OAVJavaType.java_string_type));
		typeinfos.add(new OAVMappingInfo("messageeventmapping", OAVJavaType.java_string_type));
		typeinfos.add(new OAVMappingInfo("internaleventmapping", OAVJavaType.java_string_type));
				
		typeinfos.add(new OAVMappingInfo("concrete", OAVJavaType.java_string_type));

		typeinfos.add(new OAVMappingInfo("configuration", OAVBDIMetaModel.configuration_type));
		typeinfos.add(new OAVMappingInfo("initialbelief", OAVBDIMetaModel.configbelief_type));
		typeinfos.add(new OAVMappingInfo("initialbeliefset", OAVBDIMetaModel.configbeliefset_type));
		typeinfos.add(new OAVMappingInfo("initialgoal", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("initialplan", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("initialinternalevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("initialmessageevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("endgoal", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("endplan", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("endinternalevent", OAVBDIMetaModel.configelement_type));
		typeinfos.add(new OAVMappingInfo("endmessageevent", OAVBDIMetaModel.configelement_type));

		Set linkinfos = new HashSet();
		linkinfos.add(new OAVLinkInfo("properties/property", OAVBDIMetaModel.capability_has_properties));
		linkinfos.add(new OAVLinkInfo("goals/performgoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new OAVLinkInfo("goals/achievegoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new OAVLinkInfo("goals/querygoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new OAVLinkInfo("goals/maintaingoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new OAVLinkInfo("goals/metagoal", OAVBDIMetaModel.capability_has_goals));
		linkinfos.add(new OAVLinkInfo("goals/achievegoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new OAVLinkInfo("goals/querygoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new OAVLinkInfo("goals/maintaingoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.add(new OAVLinkInfo("goals/metagoalref", OAVBDIMetaModel.capability_has_goalrefs));
		linkinfos.put("agent/plans/plan/parameter", OAVBDIMetaModel.parameterelement_has_parameters);
		linkinfos.put("agent/plans/plan/parameterset", OAVBDIMetaModel.parameterelement_has_parametersets);
		
		Set ignoredattrs = new HashSet();
		ignoredattrs.add("schemaLocation");
		
		reader = new Reader(new OAVObjectHandler(typeinfos, linkinfos, ignoredattrs));
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
		public void postProcess(IOAVState state, Object object, Object root)
		{
			Object	ret	= null;
			String	value	= (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_content);
			
			if(value!=null)
			{
				String lang = (String)state.getAttributeValue(object, OAVBDIMetaModel.expression_has_language);
				
				if(state.getType(object).isSubtype(OAVBDIMetaModel.condition_type))
				{
//					System.out.println("Found condition: "+se.object);

					if(lang==null || "clips".equals(lang))
					{
						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseCondition(value, state.getTypeModel(), errors);
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
							ret = exp_parser.parseExpression(value, imports, null, state.getTypeModel().getClassLoader());
						}
						catch(Exception e)
						{
//							report.put(se, e.toString());
							e.printStackTrace();
						}
					}
					else if("clips".equals(lang))
					{
						List	errors	= new ArrayList();
						try
						{
							ret = ParserHelper.parseCondition(value, state.getTypeModel(), errors);
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
							throw new RuntimeException("Parse errors: "+errors);
						}
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
		public void postProcess(IOAVState state, Object object, Object root)
		{
			String	value	= (String)state.getAttributeValue(object, classnameattr);
			if(value!=null)
			{
				try
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
					Class	clazz = SReflect.findClass(value, imports, state.getTypeModel().getClassLoader());
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
}
