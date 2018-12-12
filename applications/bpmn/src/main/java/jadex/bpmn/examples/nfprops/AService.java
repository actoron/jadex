package jadex.bpmn.examples.nfprops;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public class AService implements IAService
{
	@ServiceComponent
	protected IInternalAccess comp;
	
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The test string. */
	protected long wait = (long)(Math.random()*1000);
	
	/** The invocation counter. */
	protected int cnt;
	
	/**
	 *  Test method.
	 */
	public IFuture<String> test()
	{
		System.out.println("invoked service: "+sid.getProviderId()+" cnt="+(++cnt)+" wait="+wait);
		comp.getFeature(IExecutionFeature.class).waitForDelay(wait).get();
		return new Future<String>(sid.toString());
	}
}
