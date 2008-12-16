package jadex.bdi.benchmarks;

import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.rules.state.IOAVState;
import jadex.rules.state.io.xml.IOAVXMLMapping;
import jadex.rules.state.io.xml.Reader;
import jadex.rules.state.javaimpl.OAVState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Test class for the XML reader.
 */
public class ReaderTest
{
	/**
	 *  Main for testing.
	 *  @throws IOException 
	 */
	public static void	main(String[] args) throws IOException
	{
		if(args.length!=1)
		{
			System.out.println("USAGE: ReaderTest (<model>|<directory>)");
			args = new String[]{"jadex/bdi/examples/HelloWorld.agent.xml"};
			//return;
		}
		
//		Configuration.setFallbackConfiguration("../../../kernel/src/jadex/config/batch_conf.properties");

		Reader	reader	= new Reader();
		IOAVState	state	= new OAVState(OAVBDIMetaModel.bdimm_type_model);
//		Properties kernelprops = new Properties("", "", "");
//		kernelprops.addProperty(new Property("", "messagetype", "new jadex.adapter.base.fipa.FIPAMessageType()"));
		Map kernelprops = new HashMap();
		kernelprops.put("messagetype_fipa", new jadex.adapter.base.fipa.FIPAMessageType());
		IOAVXMLMapping	mapping	= OAVBDIMetaModel.getXMLMapping(kernelprops);
		
		File	file	= new File(args[0]);
		if(file.isDirectory())
		{
			List	files	= new ArrayList(); 
			files.addAll(Arrays.asList(file.listFiles()));
			for(int i=0; i<files.size(); i++)
			{
				file	= (File)files.get(i);
				if(file.getName().endsWith(".agent.xml") || file.getName().endsWith(".capability.xml"))
				{
					System.out.println(file);
					reader.read(new FileInputStream(file), state, mapping, new MultiCollection());
				}
				else if(file.isDirectory())
				{
					files.addAll(Arrays.asList(file.listFiles()));
				}
			}
		}
		else
		{
			reader.read(SUtil.getResource(args[0], null), state, mapping, new MultiCollection());

//			OAVTreeModel.createOAVFrame(file.getName(), state).setVisible(true);

			System.out.println(state);
		}
	}
}
