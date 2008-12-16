package jadex.bdi.benchmarks;

import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.commons.collection.MultiCollection;
import jadex.rules.state.IOAVState;
import jadex.rules.state.io.xml.IOAVXMLMapping;
import jadex.rules.state.io.xml.Reader;
import jadex.rules.state.javaimpl.OAVState;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	public static void	main(String[] args) throws IOException
	{
		if(args.length!=1)
		{
			System.out.println("USAGE: ReaderBenchmark <model>");
			return;
		}

	//	Configuration.setFallbackConfiguration("jadex/config/batch_conf.properties");
		
		// Do not measure first loading.
		Reader	reader	= new Reader();
		IOAVState	state	= new OAVState(OAVBDIMetaModel.bdimm_type_model);
//		IOAVState	state	= new JenaOAVState();
		
//		Properties kernelprops = new Properties("", "", "");
//		kernelprops.addProperty(new Property("", "messagetype", "new jadex.adapter.base.fipa.FIPAMessageType()"));
		Map kernelprops = new HashMap();
		kernelprops.put("messagetype_fipa", new jadex.adapter.base.fipa.FIPAMessageType());
		
		IOAVXMLMapping	xmlmapping	= OAVBDIMetaModel.getXMLMapping(kernelprops);
		Object	obj	= reader.read(new FileInputStream(args[0]), state, xmlmapping, new MultiCollection());
		
		// Start tests.
		int cnt	= 100;
		gc();
		long	startmem	= Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		// Load OAV models.
		long	starttime	= System.currentTimeMillis();
		IOAVState[]	states	= loadOAVModels(args[0], reader, xmlmapping, cnt);
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


	protected static IOAVState[] loadOAVModels(String arg, Reader reader, IOAVXMLMapping xmlmapping, int cnt) throws IOException, FileNotFoundException
	{
		IOAVState[] states	= new IOAVState[cnt];
		for(int i=0; i<states.length; i++)
		{
			states[i]	= new OAVState(OAVBDIMetaModel.bdimm_type_model);
//			states[i]	= new JenaOAVState();
			reader.read(new FileInputStream(arg), states[i], xmlmapping, new MultiCollection());
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
