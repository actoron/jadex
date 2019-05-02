package jadex.platform.service.serialization;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.transformation.jsonserializer.JsonString;

@Agent
@Service
@ProvidedServices(@ProvidedService(type = IJsonStringService.class, implementation = @Implementation(expression = "$pojoagent")))
public class JsonStringAgent implements IJsonStringService
{
	public static final JsonString JSON_STRING = new JsonString("{\"hello\":\"World\"}");

	@Override
	public IFuture<JsonString> getJsonString()
	{
		return new Future<>(JSON_STRING);
	}
}
