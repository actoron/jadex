package jadex.micro.testcases.nfservicetags;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Tag;
import jadex.bridge.service.annotation.Tags;

@Service
//@Tags(value={"hello", "$component.getId().toString()", TagProperty.PLATFORM_NAME}, argumentname="tagarg")
@Tags({@Tag("hello"), @Tag("$component.getId().toString()"), @Tag("TagProperty.PLATFORM_NAME"), @Tag("$component.getArguments().get(\"tagarg\")")})
public interface ITestService2
{
}
