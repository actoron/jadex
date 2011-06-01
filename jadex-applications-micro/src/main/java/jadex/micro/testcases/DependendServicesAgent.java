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
 *  Starts two agents a and b
 *  a has service sa 
 *  b has service sb
 *  init of service sb searches for sa and uses the service
 *  
 *  (problem is that component a has finished its init but must execute the service call for sb)
 *  
 *  // todo: make real testcase
 */
@Description("Test if services of (earlier) sibling components can be found and used.")
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
