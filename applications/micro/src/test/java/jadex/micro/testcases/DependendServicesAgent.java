package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SUtil;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Starts two agents a and b
 *  a has service sa
 *  b has service sb
 *  init of service sb searches for sa and uses the service
 *
 *  (problem is that component a has finished its init but must execute the service call for sb)
 */
@Description("Test if services of (earlier) sibling components can be found and used.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@ComponentTypes({
    @ComponentType(name="a", clazz=AAgent.class),
    @ComponentType(name="b", clazz=BAgent.class)
})
@Configurations(@Configuration(name="def", components={
    @Component(type="a"),
    @Component(type="b")
}))
@Agent
public class DependendServicesAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
    /**
     *  Init code.
     */
	@AgentCreated
    public IFuture<Void> agentCreated()
    {
        final Future<Void> ret = new Future<Void>();
        getChildrenAccesses().addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, Void>(ret)
        {
            public void customResultAvailable(Collection<IExternalAccess> result)
            {
            	IExternalAccess[] childs = (IExternalAccess[])result.toArray(new IExternalAccess[0]);
            	System.out.println("childs: "+SUtil.arrayToString(childs));
                final CollectionResultListener<Collection<TestReport>> lis = new CollectionResultListener<Collection<TestReport>>(childs.length, true, 
                	agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Collection<Collection<TestReport>>>()
                {
                    public void resultAvailable(Collection<Collection<TestReport>> result)
                    {
						System.out.println("fini: "+result);
						List<TestReport> tests = new ArrayList<TestReport>();
//						Collection<TestReport> col = (Collection<TestReport>)result;
						for(Iterator<Collection<TestReport>> it=result.iterator(); it.hasNext(); )
						{
							Collection<TestReport> tmp = (Collection<TestReport>)it.next();
							tests.addAll(tmp);
						}
						agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(tests.size(), (TestReport[])tests.toArray(new TestReport[tests.size()])));
						
						agent.killComponent();
                    }
                    
                    public void exceptionOccurred(Exception exception)
                    {
						agent.killComponent(exception);
                    }
                }));

                for(int i=0; i<childs.length; i++)
                {
                    final IExternalAccess child = childs[i];
                    child.subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
						.addResultListener(new IntermediateDefaultResultListener<IMonitoringEvent>()
					{
						public void intermediateResultAvailable(IMonitoringEvent result)
						{
							child.getResults().addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Map<String, Object>>()
                            {
                                public void resultAvailable(Map<String, Object> res)
                                {
                                  System.out.println("del: "+child.getId()+" "+res);
//                                    Map res = (Map)result;
                                    List<TestReport> tests = (List<TestReport>)res.get("testcases");
                                    lis.resultAvailable(tests);
                                }
                                public void exceptionOccurred(Exception exception)
                                {
                                	lis.exceptionOccurred(exception);
                                }
                            }));
						}
						
						public void exceptionOccurred(Exception exception)
						{
                        	lis.exceptionOccurred(exception);
						}
					});
                }
//                ret.setException(null);	// ???
                ret.setResult(null);
            }
        }));
        return ret;
    }

    /**
     *  The agent body.
     */
	@AgentBody
    public IFuture<Void> executeBody()
    {
        getChildrenAccesses().addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<Collection<IExternalAccess>>()
        {
            public void resultAvailable(Collection<IExternalAccess> result)
            {
                IExternalAccess[] childs = (IExternalAccess[])result.toArray(new IExternalAccess[0]);
                for(int i=0; i<childs.length; i++)
                {
                    childs[i].killComponent();
                }
            }
        }));
        
        return new Future<Void>(); // never kill?
    }

	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<Collection<IExternalAccess>> getChildrenAccesses()
	{
		final Future<Collection<IExternalAccess>> ret = new Future<Collection<IExternalAccess>>();
		
		agent.getChildren(null, null).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier[], Collection<IExternalAccess>>(ret)
		{
			public void customResultAvailable(IComponentIdentifier[] children)
			{
				IResultListener<IExternalAccess>	crl	= new CollectionResultListener<IExternalAccess>(children.length, true,
					new DelegationResultListener<Collection<IExternalAccess>>(ret));
				for(int i=0; !ret.isDone() && i<children.length; i++)
				{
					agent.getExternalAccess(children[i]).addResultListener(crl);
				}
			}
		});
		
		return ret;
	}
}

