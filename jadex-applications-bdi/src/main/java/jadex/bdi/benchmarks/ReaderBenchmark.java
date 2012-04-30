package jadex.bdi.benchmarks;

import jadex.bdi.OAVBDIXMLReader;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.commons.collection.MultiCollection;
import jadex.component.ComponentXMLReader;
import jadex.rules.state.IOAVState;
import jadex.rules.state.io.xml.OAVObjectReaderHandler;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.xml.reader.Reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  Benchmark for OAV state memory consumption.
 *  Loads a model using jibx and OAV and compares the memory usage.
 */
public class ReaderBenchmark
{
	/**
	 *  Main for testing.
	 *  @throws IOException 
	 */
	public static void	main(String[] args) throws Exception
	{
		if(args.length!=1)
		{
			System.out.println("USAGE: ReaderBenchmark <model>");
			return;
		}

	//	Configuration.setFallbackConfiguration("jadex/config/batch_conf.properties");
		
		// Do not measure first loading.
		Reader	reader	= OAVBDIXMLReader.getReader(); 
		IOAVState	state	= OAVStateFactory.createOAVState(OAVBDIMetaModel.bdimm_type_model);
//		IOAVState	state	= new JenaOAVState();
		
//		Properties kernelprops = new Properties("", "", "");
//		kernelprops.addProperty(new Property("", "messagetype", "new jadex.bridge.fipa.FIPAMessageType()"));
		Map kernelprops = new HashMap();
		kernelprops.put("messagetype_fipa", new jadex.bridge.fipa.FIPAMessageType());
		
		Map	user	= new HashMap();
		user.put(OAVObjectReaderHandler.CONTEXT_STATE, state);
		user.put(ComponentXMLReader.CONTEXT_ENTRIES, new MultiCollection());
		Object	obj	= reader.read(OAVBDIXMLReader.getReaderManager(), OAVBDIXMLReader.getReaderHandler(), new FileInputStream(args[0]), null, user);
		
		// Start tests.
		int cnt	= 100;
		gc();
		long	startmem	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		// Load OAV models.
		long	starttime	= System.currentTimeMillis();
		IOAVState[]	states	= loadOAVModels(args[0], reader, cnt);
		long	statetime	= System.currentTimeMillis() - starttime;
		gc();
		long	statemem	= Runtime.getRuntime().totalMemory() - startmem - Runtime.getRuntime().freeMemory();

		System.out.println("Start memory: "+calcKB(startmem)+" kb");
		System.out.println("State memory: "+calcKB(statemem)+" kb correponds to "+calcKB(statemem/(double)states.length)+" kb per agent.");
		System.out.println("State time: "+statetime+" millis correponds to "+statetime/states.length+" millis per agent.");
		
		// Keep VM alive for profiling.
		while(true)
		{
			synchronized(obj)
			{
				try
				{
					obj.wait();
				}
				catch(InterruptedException e){}
			}
		}
	}


	protected static IOAVState[] loadOAVModels(String arg, Reader reader, int cnt) throws Exception
	{
		IOAVState[] states	= new IOAVState[cnt];
		for(int i=0; i<states.length; i++)
		{
			states[i]	= OAVStateFactory.createOAVState(OAVBDIMetaModel.bdimm_type_model);
//			states[i]	= new JenaOAVState();
			Map	user	= new HashMap();
			user.put(OAVObjectReaderHandler.CONTEXT_STATE, states[i]);
			user.put(ComponentXMLReader.CONTEXT_ENTRIES, new MultiCollection());
			reader.read(OAVBDIXMLReader.getReaderManager(), OAVBDIXMLReader.getReaderHandler(), new FileInputStream(arg), null, user);
		}
		return states;
	}
	
	
	protected static void	gc()
	{
//		for(int i=0; i<3; i++)
//		{
//			System.gc();
//			try
//			{
//				Thread.sleep(300);
//			}
//			catch(InterruptedException e){}
//		}
		System.gc();
	}
	
	protected static double	calcKB(double bytes)
	{
		return ((long)bytes*10/1024)/10.0;
	}
}
