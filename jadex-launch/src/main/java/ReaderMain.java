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
		Map types = new HashMap();
		types.put("agent", OAVBDIMetaModel.agent_type);
		types.put("capabilities/capability", OAVBDIMetaModel.capabilityref_type);
		types.put("capability", OAVBDIMetaModel.capability_type);
		types.put("import", OAVJavaType.java_string_type);
		types.put("belief", OAVBDIMetaModel.belief_type);
		types.put("beliefset", OAVBDIMetaModel.beliefset_type);
		types.put("performgoal", OAVBDIMetaModel.performgoal_type);
		types.put("achievegoal", OAVBDIMetaModel.achievegoal_type);
		types.put("querygoal", OAVBDIMetaModel.querygoal_type);
		types.put("maintaingoal", OAVBDIMetaModel.maintaingoal_type);
		types.put("metagoal", OAVBDIMetaModel.metagoal_type);
		types.put("plan", OAVBDIMetaModel.plan_type);
		types.put("expression", OAVBDIMetaModel.expression_type);
		types.put("property", OAVBDIMetaModel.expression_type);

		Set ignored = new HashSet();
		ignored.add("schemaLocation");
		
		jadex.commons.xml.Reader reader = new jadex.commons.xml.Reader(new OAVObjectHandler(types, ignored));
		
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