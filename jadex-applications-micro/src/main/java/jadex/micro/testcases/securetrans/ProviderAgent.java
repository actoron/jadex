package jadex.micro.testcases.securetrans;

import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.testcases.stream.StreamProviderAgent;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	@SecureTransmission
	public IFuture<Void> secMethod(String msg)
	{
		System.out.println("Called secMethod: "+msg);
		return IFuture.DONE;
	}
	
	/**
	 *  Call a method that can use any transport.
	 */
	public IFuture<Void> unsecMethod(String msg)
	{
		System.out.println("Called unsecMethod: "+msg);
		return IFuture.DONE;
	}
	
	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	public IFuture<IInputConnection> getInputStream()
	{
		Future<IInputConnection> ret = new Future<IInputConnection>();
		ServiceOutputConnection oc = new ServiceOutputConnection();
		StreamProviderAgent.write(oc, agent);
		ret.setResult(oc.getInputConnection());
		return ret;
	}
	
	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	public IFuture<IOutputConnection> getOutputStream()
	{
		Future<IOutputConnection> ret = new Future<IOutputConnection>();
		ServiceInputConnection ic = new ServiceInputConnection();
		StreamProviderAgent.read(ic);
		ret.setResult(ic.getOutputConnection());
		return ret;
	}
	
	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	public IFuture<Long> passInputStream(IInputConnection con)
	{
		return StreamProviderAgent.read(con);
	}
	
	/**
	 *  Pass an output stream from the user.
	 *  @param con The output stream.
	 */
	public IFuture<Long> passOutputStream(IOutputConnection con)
	{
		return StreamProviderAgent.write(con, agent);
	}
}
