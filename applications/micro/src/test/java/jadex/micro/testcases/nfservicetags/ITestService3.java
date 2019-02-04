package jadex.micro.testcases.nfservicetags;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Tag;
import jadex.bridge.service.annotation.Tags;

@Service
@Tags({@Tag(value="in", include="true"), @Tag(value="out", include="false")})
public interface ITestService3
{
}
