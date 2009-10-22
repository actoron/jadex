package jadex.wfms.service.execution;

import jadex.bridge.IElementFactory;
import jadex.bridge.ILoadableElementModel;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.wfms.IProcess;
import jadex.wfms.IProcessModel;
import jadex.wfms.IWfms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

/**
 *  The meta execution service wraps all specific process execution services.
 */
public class MetaExecutionService implements IExecutionService
{
	//-------- attributes --------
	
	/** The WFMS */
	protected IWfms wfms;
	
	/** The execution services. */
	protected List exeservices;
	
	/** Running process instances (id -> IProcess) */
	protected Map processes;
	
	/** Counter for instances of processes (IProcessModel -> num)*/
	protected Map instancecnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new execution service.
	 */
	public MetaExecutionService(IWfms wfms, List exeservices)
	{
		this.processes = new HashMap();
		this.wfms = wfms;
		
		if(exeservices==null || exeservices.size()==0)
			throw new RuntimeException("Meta execution service needs at least one sub service.");
		this.exeservices = exeservices;
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		if(exeservices!=null)
		{
			for(int i=0; i<exeservices.size(); i++)
			{
				((IService)exeservices.get(i)).start();
			}
		}
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		if(exeservices!=null)
		{
			for(int i=0; i<exeservices.size(); i++)
			{
				((IService)exeservices.get(i)).shutdown(null);
			}
		}
		if(listener!=null)
			listener.resultAvailable(null);
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public ILoadableElementModel loadModel(String model)
	{
		ILoadableElementModel ret = null;
		
		for(int i=0; ret==null && i<exeservices.size(); i++)
		{
			IExecutionService es = (IExecutionService)exeservices.get(i);
			if(es.isLoadable(model))
			{
				ret = es.loadModel(model);
			}
		}
		
		if(ret==null)
			throw new RuntimeException("Could not load process model: "+model, null);
		return ret;
	}
	
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @return The process model.
	 * /
	public IProcessModel loadModel(String filename, String[] imports)
	{
		IProcessModel ret = null;
		
		for(int i=0; ret==null && i<exeservices.size(); i++)
		{
			IExecutionService es = (IExecutionService)exeservices.get(i);
			if(es.isLoadable(filename))
			{
				ret = es.loadModel(filename, imports);
			}
		}
		
		if(ret==null)
			throw new RuntimeException("Could not load process model: "+filename, null);
		return ret;
	}*/
	
	/**
	 *  Start a process instance.
	 */
	public Object startProcess(String modelname, Object id, Map arguments, boolean stepmode)
	{
		Object ret = null;
		
		if(id==null)
			id = generateId(modelname);
			
		for(int i=0; ret==null && i<exeservices.size(); i++)
		{
			IExecutionService es = (IExecutionService)exeservices.get(i);
			if(es.isLoadable(modelname))
			{
				ret = es.startProcess(modelname, id, arguments, stepmode);
			}
		}
		
		if(ret==null)
			throw new RuntimeException("Could not create process: "+modelname, null);
		return id;
	}

	/**
	 *  Stop a process instance.
	 *  @param name The model name.
	 */
	public void stopProcess(IProcess id)
	{
//		IProcess ret = null;
//		
//		for(int i=0; ret==null && i<exeservices.size(); i++)
//		{
//			IExecutionService es = (IExecutionService)exeservices.get(i);
//			if(es.isLoadable(name))
//			{
//				ret = es.startProcess(client, name, arguments, stepmode);
//			}
//		}
//		
//		if(ret==null)
//			throw new RuntimeException("Could not create process: "+name, null);
//		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String name)
	{
		boolean ret = false;
		
		for(int i=0; !ret && i<exeservices.size(); i++)
		{
			IExecutionService es = (IExecutionService)exeservices.get(i);
			ret = es.isLoadable(name);
		}
		
		return ret;
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model)
	{
		boolean ret = false;
		
		for(int i=0; !ret && i<exeservices.size(); i++)
		{
			IExecutionService es = (IExecutionService)exeservices.get(i);
			ret = es.isStartable(model);
		}
		
		return ret;
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes()
	{
		String[]	ret	= new String[0];
		if(exeservices!=null)
		{
			for(Iterator it=exeservices.iterator(); it.hasNext(); )
			{
				IElementFactory fac = (IElementFactory)it.next();
				ret	= (String[])SUtil.joinArrays(ret, fac.getFileTypes());
			}
		}
		return ret;
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		Icon	ret = null;
		if(exeservices!=null)
		{
			for(Iterator it=exeservices.iterator(); it.hasNext() && ret==null; )
			{
				IElementFactory fac = (IElementFactory)it.next();
				ret = fac.getFileTypeIcon(type);
			}
		}
		return ret;
	}


	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		String	ret = null;
		if(exeservices!=null)
		{
			for(Iterator it=exeservices.iterator(); it.hasNext() && ret==null; )
			{
				IElementFactory fac = (IElementFactory)it.next();
				ret = fac.getFileType(model);
			}
		}
		return ret;
	}
	
	/**
	 *  Generate a process id.
	 */
	protected Object generateId(String modelname)
	{
		Integer ret = new Integer(0);
		if(instancecnt==null)
		{
			instancecnt = new HashMap();
		}
		else
		{
			ret = new Integer(((Integer)instancecnt.get(modelname)).intValue()+1);
		}
		
		instancecnt.put(modelname, ret);
		
		return modelname+"_"+ret;
	}
}
