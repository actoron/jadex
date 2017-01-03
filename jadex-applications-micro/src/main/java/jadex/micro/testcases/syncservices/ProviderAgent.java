package jadex.micro.testcases.syncservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ComponentTypes;

/**
 * 
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
		System.out.println("do void");
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
