package jadex.micro.testcases;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * 
 */
@Description("A simple test showing how the test center works with micro agents.")
@Results(@Result(name="testresults", typename="Testcase"))
@ComponentTypes({
	@ComponentType(name="a", filename="AAgent.class"), 
	@ComponentType(name="b", filename="BAgent.class"), 
})
@Configurations(@Configuration(name="def", components={
	@Component(type="a"), 
	@Component(type="b")
}))
public class DependendServicesAgent extends MicroAgent
{
}
