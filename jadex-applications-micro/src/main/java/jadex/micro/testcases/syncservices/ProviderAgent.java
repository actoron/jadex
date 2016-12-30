package jadex.micro.testcases.syncservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;

/**
 *  Agent that provides a synchronous service.
 */
@Agent(autoprovide=true)
@Service
public class ProviderAgent implements ISynchronousExampleService
{
	/**
	 *  Void method.
	 */
	public void doVoid()
	{
		throw new ServiceInvalidException("Test if void method can throw exception");
	}
	
	/**
	 *  Int method.
	 */
	public int getInt()
	{
		return 3;
	}
	
	/**
	 *  Collection method.
	 */
	public Collection<String> getCollection()
	{
		List<String> ret = new ArrayList<String>();
		ret.add("abc");
		ret.add("def");
		return ret;
	}
}
