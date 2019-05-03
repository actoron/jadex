package jadex.platform.service.serialization;

import jadex.commons.future.IFuture;
import jadex.transformation.jsonserializer.JsonString;

public interface IJsonStringService
{
	IFuture<JsonString> getJsonString();
}
