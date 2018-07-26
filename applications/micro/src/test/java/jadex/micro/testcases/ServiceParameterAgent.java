package jadex.micro.testcases;


import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if pojo services can be passed as local parameters.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IDService.class, implementation=@Implementation(PojoDService.class)))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
@Configurations({@Configuration(name="first"), @Configuration(name="second")})
public class ServiceParameterAgent extends JunitAgentTest
{
}
