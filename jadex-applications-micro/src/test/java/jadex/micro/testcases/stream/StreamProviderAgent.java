package jadex.micro.testcases.stream;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

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
	protected IInternalAccess agent;
	
	/**
	 *  Pass an input stream to the user.
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
	 *  Pass an output stream to the user.
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
	 *  Pass an input stream to the user.
	 *  @return The input stream.
	 */
	@SecureTransmission
	public IFuture<IInputConnection> getSecureInputStream()
	{
		return getInputStream();
	}

	/**
	 *  Pass an output stream to the user.
	 *  @return The input stream.
	 */
	@SecureTransmission
	public IFuture<IOutputConnection> getSecureOutputStream()
	{
		return getOutputStream();
	}

	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	@SecureTransmission
	public IFuture<Long> passSecureInputStream(IInputConnection con)
	{
//		System.out.println("rec: "+con.getNonFunctionalProperties());
		Map<String, Object> props = con.getNonFunctionalProperties();
		Boolean sec = props!=null? (Boolean)props.get(SecureTransmission.SECURE_TRANSMISSION): null;
		if(sec==null || !sec.booleanValue())
		{
			return new Future<Long>(new RuntimeException("Received unsecure stream in 'passSecureInputStream'"));
		}
		else
		{
			return passInputStream(con);
		}
	}
	
	/**
	 *  Pass an output stream from the user.
	 *  @param con The output stream.
	 */
	@SecureTransmission
	public IFuture<Long> passSecureOutputStream(IOutputConnection con)
	{
		System.out.println("rec: "+con.getNonFunctionalProperties());
		Map<String, Object> props = con.getNonFunctionalProperties();
		Boolean sec = props!=null? (Boolean)props.get(SecureTransmission.SECURE_TRANSMISSION): null;
		if(sec==null || !sec.booleanValue())
		{
			return new Future<Long>(new RuntimeException("Received unsecure stream in 'passSecureOutputStream'"));
		}
		else
		{
			return passOutputStream(con);
		}
	}
	
	/**
	 *  Read data from an input connection.
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
				ret.setResult(Long.valueOf(size[0]));
			}
			public void intermediateResultAvailable(byte[] result)
			{
				size[0] += result.length;
//				System.out.println("Intermediate result: "+SUtil.arrayToString(result));
			}
			public void finished()
			{
//				System.out.println("finished, size: "+size[0]);
				ret.setResult(Long.valueOf(size[0]));
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
	 *  Write data to a output connection.
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
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(50, this);
				}
				else
				{
					con.close();
					ret.setResult(Long.valueOf(size[0]));
				}
				return IFuture.DONE;
			}
		};
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, step);
		
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
