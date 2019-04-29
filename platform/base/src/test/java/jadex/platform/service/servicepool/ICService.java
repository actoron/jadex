package jadex.platform.service.servicepool;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Tag;
import jadex.bridge.service.annotation.Tags;
import jadex.commons.future.IFuture;

/**
 *  Example service a.
 */
@Service
@Tags({@Tag("tag1"), @Tag("tag2")})
public interface ICService
{
	/**
	 *  Example method 1.
	 */
	public IFuture<String> ma1(String s); 
}
