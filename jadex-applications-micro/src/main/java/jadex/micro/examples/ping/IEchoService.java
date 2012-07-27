package jadex.micro.examples.ping;

import jadex.bridge.service.annotation.Security;

/**
 *  Marker interface for echo component
 *  (sends back any received message).
 */
@Security(Security.UNRESTRICTED)
public interface IEchoService
{

}
