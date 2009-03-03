package jadex.bdi.interpreter;

import jadex.commons.xml.Reader;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.io.xml.OAVMappingInfo;
import jadex.rules.state.io.xml.OAVObjectHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		Set typeinfos = new HashSet();
		typeinfos.add(new OAVMappingInfo("agent", OAVBDIMetaModel.agent_type));
		typeinfos.add(new OAVMappingInfo("capabilities/capability", OAVBDIMetaModel.capabilityref_type));
		typeinfos.add(new OAVMappingInfo("capability", OAVBDIMetaModel.capability_type));
		typeinfos.add(new OAVMappingInfo("import", OAVJavaType.java_string_type));
		typeinfos.add(new OAVMappingInfo("belief", OAVBDIMetaModel.belief_type));
		typeinfos.add(new OAVMappingInfo("beliefref", OAVBDIMetaModel.beliefreference_type));
		typeinfos.add(new OAVMappingInfo("beliefset", OAVBDIMetaModel.beliefset_type));
		typeinfos.add(new OAVMappingInfo("beliefsetref", OAVBDIMetaModel.beliefsetreference_type));
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
		typeinfos.add(new OAVMappingInfo("plan", OAVBDIMetaModel.plan_type));
		typeinfos.add(new OAVMappingInfo("internalevent", OAVBDIMetaModel.internalevent_type));
		typeinfos.add(new OAVMappingInfo("internaleventref", OAVBDIMetaModel.internaleventreference_type));
		typeinfos.add(new OAVMappingInfo("messageevent", OAVBDIMetaModel.messageevent_type));
		typeinfos.add(new OAVMappingInfo("messageeventref", OAVBDIMetaModel.messageeventreference_type));
		typeinfos.add(new OAVMappingInfo("expression", OAVBDIMetaModel.expression_type));
		typeinfos.add(new OAVMappingInfo("property", OAVBDIMetaModel.expression_type));
		typeinfos.add(new OAVMappingInfo("fact", OAVBDIMetaModel.expression_type));
		typeinfos.add(new OAVMappingInfo("facts", OAVBDIMetaModel.expression_type));
		typeinfos.add(new OAVMappingInfo("value", OAVBDIMetaModel.expression_type));
		typeinfos.add(new OAVMappingInfo("values", OAVBDIMetaModel.expression_type));
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
		typeinfos.add(new OAVMappingInfo("concrete", OAVJavaType.java_string_type));

		Map linkinfos = new HashMap();
		linkinfos.put("agent/properties/property", OAVBDIMetaModel.capability_has_properties);
		linkinfos.put("agent/goals/performgoal", OAVBDIMetaModel.capability_has_goals);
		linkinfos.put("agent/goals/achievegoal", OAVBDIMetaModel.capability_has_goals);
		linkinfos.put("agent/goals/querygoal", OAVBDIMetaModel.capability_has_goals);
		linkinfos.put("agent/goals/maintaingoal", OAVBDIMetaModel.capability_has_goals);
		linkinfos.put("agent/goals/metagoal", OAVBDIMetaModel.capability_has_goals);
		linkinfos.put("agent/goals/achievegoalref", OAVBDIMetaModel.capability_has_goalrefs);
		linkinfos.put("agent/goals/querygoalref", OAVBDIMetaModel.capability_has_goalrefs);
		linkinfos.put("agent/goals/maintaingoalref", OAVBDIMetaModel.capability_has_goalrefs);
		linkinfos.put("agent/goals/metagoalref", OAVBDIMetaModel.capability_has_goalrefs);
		
		Set ignoredattrs = new HashSet();
		ignoredattrs.add("schemaLocation");
		
		reader = new Reader(new OAVObjectHandler(typeinfos, linkinfos, ignoredattrs));
	}
	
	public static Reader	getReader()
	{
		return reader;
	}
}
