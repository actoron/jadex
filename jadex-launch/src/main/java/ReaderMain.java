import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.io.xml.OAVObjectHandler;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.stateviewer.OAVPanel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

public class ReaderMain
{
	/**
	 *  Main for testing.
	 *  @param args
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Map typeinfos = new HashMap();
		typeinfos.put("agent", OAVBDIMetaModel.agent_type);
		typeinfos.put("capabilities/capability", OAVBDIMetaModel.capabilityref_type);
		typeinfos.put("capability", OAVBDIMetaModel.capability_type);
		typeinfos.put("import", OAVJavaType.java_string_type);
		typeinfos.put("belief", OAVBDIMetaModel.belief_type);
		typeinfos.put("beliefref", OAVBDIMetaModel.beliefreference_type);
		typeinfos.put("beliefset", OAVBDIMetaModel.beliefset_type);
		typeinfos.put("beliefsetref", OAVBDIMetaModel.beliefsetreference_type);
		typeinfos.put("performgoal", OAVBDIMetaModel.performgoal_type);
		typeinfos.put("performgoalref", OAVBDIMetaModel.goalreference_type);
		typeinfos.put("achievegoal", OAVBDIMetaModel.achievegoal_type);
		typeinfos.put("achievegoalref", OAVBDIMetaModel.goalreference_type);
		typeinfos.put("querygoal", OAVBDIMetaModel.querygoal_type);
		typeinfos.put("querygoalref", OAVBDIMetaModel.goalreference_type);
		typeinfos.put("maintaingoal", OAVBDIMetaModel.maintaingoal_type);
		typeinfos.put("maintaingoalref", OAVBDIMetaModel.goalreference_type);
		typeinfos.put("metagoal", OAVBDIMetaModel.metagoal_type);
		typeinfos.put("metagoalref", OAVBDIMetaModel.goalreference_type);
		typeinfos.put("plan", OAVBDIMetaModel.plan_type);
		typeinfos.put("internalevent", OAVBDIMetaModel.internalevent_type);
		typeinfos.put("internaleventref", OAVBDIMetaModel.internaleventreference_type);
		typeinfos.put("messageevent", OAVBDIMetaModel.messageevent_type);
		typeinfos.put("messageeventref", OAVBDIMetaModel.messageeventreference_type);
		typeinfos.put("expression", OAVBDIMetaModel.expression_type);
		typeinfos.put("property", OAVBDIMetaModel.expression_type);
		typeinfos.put("fact", OAVBDIMetaModel.expression_type);
		typeinfos.put("facts", OAVBDIMetaModel.expression_type);
		typeinfos.put("value", OAVBDIMetaModel.expression_type);
		typeinfos.put("values", OAVBDIMetaModel.expression_type);
		typeinfos.put("configuration", OAVBDIMetaModel.configuration_type);
		typeinfos.put("initialbelief", OAVBDIMetaModel.configbelief_type);
		typeinfos.put("initialbeliefset", OAVBDIMetaModel.configbeliefset_type);
		typeinfos.put("initialgoal", OAVBDIMetaModel.configelement_type);
		typeinfos.put("initialplan", OAVBDIMetaModel.configelement_type);
		typeinfos.put("initialinternalevent", OAVBDIMetaModel.configelement_type);
		typeinfos.put("initialmessageevent", OAVBDIMetaModel.configelement_type);
		typeinfos.put("endgoal", OAVBDIMetaModel.configelement_type);
		typeinfos.put("endplan", OAVBDIMetaModel.configelement_type);
		typeinfos.put("endinternalevent", OAVBDIMetaModel.configelement_type);
		typeinfos.put("endmessageevent", OAVBDIMetaModel.configelement_type);
		typeinfos.put("concrete", OAVJavaType.java_string_type);

		Map linkinfos = new HashMap();
		linkinfos.put("agent/properties/property", "mcapability_has_mproperties");
		linkinfos.put("agent/goals/performgoal", "mcapability_has_mgoals");
		linkinfos.put("agent/goals/achievegoal", "mcapability_has_mgoals");
		linkinfos.put("agent/goals/querygoal", "mcapability_has_mgoals");
		linkinfos.put("agent/goals/maintaingoal", "mcapability_has_mgoals");
		linkinfos.put("agent/goals/metagoal", "mcapability_has_mgoals");
		linkinfos.put("agent/goals/achievegoalref", "mcapability_has_mgoalrefs");
		linkinfos.put("agent/goals/querygoalref", "mcapability_has_mgoalrefs");
		linkinfos.put("agent/goals/maintaingoalref", "mcapability_has_mgoalrefs");
		linkinfos.put("agent/goals/metagoalref", "mcapability_has_mgoalrefs");
		
		Set ignoredattrs = new HashSet();
		ignoredattrs.add("schemaLocation");
		
		jadex.commons.xml.Reader reader = new jadex.commons.xml.Reader(new OAVObjectHandler(typeinfos, linkinfos, ignoredattrs));
		
		OAVTypeModel typemodel	= new OAVTypeModel("test_typemodel", null);
		// Requires runtime meta model, because e.g. user conditions can refer to runtime elements (belief, goal, etc.) 
		typemodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		IOAVState state	= OAVStateFactory.createOAVState(typemodel);
		
		InputStream	input = new FileInputStream(args!=null && args.length==1? args[0]: "C:/projects/jadexv2/jadex-applications-bdi/src/main/java/jadex/bdi/examples/booktrading/buyer/Buyer.agent.xml");
		Object o = reader.read(input, null, state);
		JFrame frame = OAVPanel.createOAVFrame("test", state);
		frame.setVisible(true);
		System.out.println(o);
	}
}