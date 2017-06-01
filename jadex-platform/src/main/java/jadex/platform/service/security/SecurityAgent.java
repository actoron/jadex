package jadex.platform.service.security;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the security service.
 */
@Agent
@Arguments({
	@Argument(name="usepass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="printpass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="trustedlan", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="networkname", clazz=String[].class),
	@Argument(name="networkpass", clazz=String[].class),
	@Argument(name="virtualnames", clazz=String[].class),
	@Argument(name="validityduration", clazz=long.class)
})
@ProvidedServices(@ProvidedService(type=ISecurityService.class,
	implementation=@Implementation(expression="new jadex.platform.service.security.SecurityService($args.usepass, $args.printpass, $args.trustedlan, $args.networkname==null? null: new String[]{$args.networkname}, $args.networkpass==null? null: new String[]{$args.networkpass}, null, $args.virtualnames, $args.validityduration)")))
@Properties(value=@NameValue(name="system", value="true"))
public class SecurityAgent
{
}
