package jadex.micro.testcases.serviceinheritance;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  An example interface that extends another.
 */
@Service
public interface IExtendedService extends IBasicService
{
	/**
	 *  Example method returning some string value.
	 *  @return Some extended info.
	 */
	public IFuture<String> getExtendedInfo();
}
