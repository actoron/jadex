package jadex.micro.testcases.serviceinheritance;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Example service interface. Uses the @Service annotation
 *  to ensure that this service interface will be offered by
 *  the component, i.e. can be found via search even if it
 *  is not the top-level interface.
 */
@Service
public interface IBasicService
{
	/**
	 *  Example method returning some string value.
	 *  @return Some basic info.
	 */
	public IFuture<String> getBasicInfo();
}
