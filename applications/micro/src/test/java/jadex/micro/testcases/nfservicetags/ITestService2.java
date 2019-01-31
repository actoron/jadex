package jadex.micro.testcases.nfservicetags;

import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.annotation.Tags;

@Tags(value={"hello", "$component.getId().toString()", TagProperty.PLATFORM_NAME}, argumentname="tagarg")
public interface ITestService2
{
}
