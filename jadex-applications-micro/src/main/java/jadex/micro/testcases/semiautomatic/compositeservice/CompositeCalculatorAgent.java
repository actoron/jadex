package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Description("This agent is an empty minimal calculator.")
@RequiredServices({
	@RequiredService(name="addservice", type=IAddService.class),
	@RequiredService(name="subservice", type=ISubService.class)
})
//@Configurations({
//	@Configuration(name="Without services", bindings={@Binding(name="addservice"), @Binding(name="subservice")}),
//	@Configuration(name="With service components", bindings=
//	{
//		@Binding(name="addservice", create=true, componentfilename="jadex.micro.testcases.semiautomatic.compositeservice.Adder.component.xml"), 
//		@Binding(name="subservice", create=true, componentfilename="jadex.micro.testcases.semiautomatic.compositeservice.Subtractor.component.xml")
//	})
//})
public class CompositeCalculatorAgent extends MicroAgent
{
	/**
	 *  The body.
	 */
	public void executeBody()
	{
		add(1,1).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				System.out.println("Result is: "+result);
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
	}
	
	/**
	 *  Add two numbers by calling the add service.
	 */
	protected IFuture add(final double a, final double b)
	{
		final Future ret = new Future();
		getRequiredService("addservice").addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAddService add = (IAddService)result;
				add.add(a, b).addResultListener(createResultListener(new DelegationResultListener(ret)));
			}
		}));
		return ret;
	}
}

