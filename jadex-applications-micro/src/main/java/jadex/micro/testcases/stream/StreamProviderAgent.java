package jadex.micro.testcases.stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 *  Agent that provides a service with a stream.
 */
@ProvidedServices(@ProvidedService(type=IStreamService.class, implementation=@Implementation(expression="$component")))
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
	public IFuture<InputStream> getInputStream()
	{
		Future<InputStream> ret = new Future<InputStream>();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final PrintWriter pw = new PrintWriter(bos);

		final int[] cnt = new int[]{0};
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				pw.write("step ");
				if(cnt[0]++<5)
					agent.waitFor(1000, this);
				return IFuture.DONE;
			}
		};
		
		agent.waitFor(1000, step);
		
		ret.setResult(null);//;new MagicInputStream(bos));
		
		return ret;
	}
	
	
}
