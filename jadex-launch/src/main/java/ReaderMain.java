import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.OAVBDIXMLReader;
import jadex.commons.xml.Reader;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.stateviewer.OAVPanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

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
//		String	testfile	= "../jadex-applications-bdi/src/main/java/jadex/bdi/examples/booktrading/buyer/Buyer.agent.xml";
//		String	testfile	= "../jadex-applications-bdi/src/main/java/jadex/bdi/examples/cleanerworld/cleaner/Cleaner.agent.xml";
		String	testfile	= "../jadex-applications-bdi/src/main/java/jadex/bdi/testcases/beliefs/BeanChanges.agent.xml";
//		String	testfile	= "../jadex-applications-bdi/src/main/java/jadex/bdi/testcases/beliefs/WaitForFactAdded.agent.xml";
//		String	testfile	= "../jadex-applib-bdi/src/main/java/jadex/bdi/planlib/protocols/cancelmeta/CancelMeta.capability.xml";
		
		Reader reader = OAVBDIXMLReader.getReader();
		File	classes	= new File("../jadex-applications-bdi/target/classes");
		ClassLoader	cl	= new URLClassLoader(new URL[]{classes.toURI().toURL()});

		OAVTypeModel typemodel	= new OAVTypeModel("test_typemodel", cl);
		// Requires runtime meta model, because e.g. user conditions can refer to runtime elements (belief, goal, etc.) 
		typemodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		IOAVState state	= OAVStateFactory.createOAVState(typemodel);
		
		InputStream	input = new FileInputStream(args!=null && args.length==1? args[0]: testfile);
		Object o = reader.read(input, null, state);
		JFrame frame = OAVPanel.createOAVFrame("test", state);
		frame.setVisible(true);
		System.out.println(o);
	}
}