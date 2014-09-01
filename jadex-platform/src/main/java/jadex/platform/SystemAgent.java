package jadex.platform;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

/**
 *  Just a simple system agent with no functionalities.
 */
@Agent
@Properties(value=@NameValue(name="system", value="true"))
public class SystemAgent
{
}
