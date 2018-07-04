package jadex.micro.testcases.nfservicetags;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.TagProperty;
import jadex.commons.future.IFuture;

/**
 *  Example service interface.
 */
// per default use component argument 'tag' (shortcut for the second)
@NFProperties(@NFProperty(value=TagProperty.class)) 
//@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name=TagProperty.ARGUMENT, value="\"tag\"")))

// directly add 'mytag'
//@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name=TagProperty.NAME, value="\"mytag\"")))

//@NFProperties(@NFProperty(value=TagProperty.class, parameters={
//	@NameValue(name=TagProperty.NAME, values={TagProperty.PLATFORM_NAME, TagProperty.JADEX_VERSION, "\"mytag\""}), 
//	@NameValue(name=TagProperty.ARGUMENT, value="\"tag\"") // additionally get tags from arguments 'tag'
//}))
public interface ITestService
{
	/**
	 *  A test method.
	 */
	public IFuture<Void> method(String msg);
}
