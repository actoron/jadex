package jadex.micro.testcases.stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;
import java.util.List;

/**
 *  Agent that provides a service with a stream.
 */
@ProvidedServices(@ProvidedService(type=IStreamService.class, implementation=@Implementation(expression="$pojoagent")))
@Results(@Result(name="testcases", clazz=List.class))
@Service(IStreamService.class)
@Agent
public class StreamProviderAgent implements IStreamService
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	public IFuture<IInputConnection> getInputStream()
	{
		Future<IInputConnection> ret = new Future<IInputConnection>();
		ServiceOutputConnection oc = new ServiceOutputConnection();
		write(oc, agent);
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
		read(ic);
		ret.setResult(ic.getOutputConnection());
		return ret;
	}
	
	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	public IFuture<Long> passInputStream(IInputConnection con)
	{
		return read(con);
	}
	
	/**
	 *  Pass an output stream from the user.
	 *  @param con The output stream.
	 */
	public IFuture<Long> passOutputStream(IOutputConnection con)
	{
		return write(con, agent);
	}
	
	/**
	 * 
	 */
	public static IFuture<Long> read(IInputConnection con)
	{
		final Future<Long> ret = new Future<Long>();
		
		final long[] size = new long[1];
		con.aread().addResultListener(new IIntermediateResultListener<byte[]>()
		{
			public void resultAvailable(Collection<byte[]> result)
			{
//				System.out.println("Result: "+result);
				ret.setResult(new Long(size[0]));
			}
			public void intermediateResultAvailable(byte[] result)
			{
				size[0] += result.length;
//				System.out.println("Intermediate result: "+SUtil.arrayToString(result));
			}
			public void finished()
			{
//				System.out.println("finished, size: "+size[0]);
				ret.setResult(new Long(size[0]));
			}
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex:"+exception);
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static IFuture<Long> write(final IOutputConnection con, final IInternalAccess agent)
	{
		final Future<Long> ret = new Future<Long>();
		
		final long[] size = new long[1];
		final int[] cnt = new int[]{0};
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				con.write(new byte[]{(byte)cnt[0]});
				size[0]++;
				if(cnt[0]++<50)
				{
					agent.waitForDelay(50, this);
				}
				else
				{
					con.close();
					ret.setResult(new Long(size[0]));
				}
				return IFuture.DONE;
			}
		};
		agent.waitForDelay(1000, step);
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static long getWriteLength()
	{
		return 51;
	}
}
