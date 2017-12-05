package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Description("This agent is an empty minimal calculator.")
@RequiredServices({
	@RequiredService(name="addservice", type=IAddService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)),
	@RequiredService(name="subservice", type=ISubService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
//@Configurations({
//	@Configuration(name="Without services", bindings={@Binding(name="addservice"), @Binding(name="subservice")}),
//	@Configuration(name="With service components", bindings=
//	{
//		@Binding(name="addservice", create=true, componentfilename="jadex.micro.testcases.semiautomatic.compositeservice.Adder.component.xml"), 
//		@Binding(name="subservice", create=true, componentfilename="jadex.micro.testcases.semiautomatic.compositeservice.Subtractor.component.xml")
//	})
//})
@Agent
public class CompositeCalculatorAgent
{
	@Agent 
	protected IInternalAccess agent; 
	
	/**
	 *  The body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		add(1,1).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				System.out.println("Result is: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Service invocation failed: "+exception);
			}
		});
		
//		add(1,2).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				System.out.println("Result is: "+result);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("exe ");
//				super.exceptionOccurred(exception);
//			}
//		});
		
		return new Future<Void>(); // never kill?!
	}
	
	/**
	 *  Add two numbers by calling the add service.
	 */
	protected IFuture add(final double a, final double b)
	{
		final Future ret = new Future();
		agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("addservice").addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAddService add = (IAddService)result;
				add.add(a, b).addResultListener(new DelegationResultListener(ret));
			}
			public void exceptionOccurred(Exception exception)
			{
//				exception.printStackTrace();
				System.out.println("Could not get required add service: "+exception);
			}
		});
		return ret;
	}
}

