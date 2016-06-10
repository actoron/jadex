package jadex.micro.testcases.nfservicetags;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.sensor.service.TagProperty;
import jadex.commons.future.IFuture;

/**
 *  Example service interface.
 */
//@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name="tag", value="\"mytag\"")))
@NFProperties(@NFProperty(value=TagProperty.class, parameters=@NameValue(name="argument", value="\"tag\"")))
public interface ITestService
{
	/**
	 *  A test method.
	 */
	public IFuture<Void> method(String msg);
}
