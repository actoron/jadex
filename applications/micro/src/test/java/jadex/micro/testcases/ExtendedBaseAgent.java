package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.ProvidedServiceConfigurationsAgent.MyAService;

/**
 *  Base class agent.
 */
@Agent
@Description("Extended base description")
@Imports({"eb1", "eb2"})
@Properties({@NameValue(name="a", value="\"eba\""), @NameValue(name="b", value="\"ebb\"")})
@RequiredServices(@RequiredService(name="clock", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_COMPONENT_ONLY)))
@ProvidedServices(@ProvidedService(name="myservice", type=IAService.class, implementation=@Implementation(MyAService.class)))
@Arguments(@Argument(name="arg1", defaultvalue="\"ebval\"", clazz=String.class))
@Results({@Result(name="res1", defaultvalue="\"ebres\"", clazz=String.class), @Result(name="testresults", clazz=Testcase.class)})
@Configurations(replace=true, value=@Configuration(name="ebconfig1"))
public class ExtendedBaseAgent extends BaseAgent
{
	@Agent
	/** The micro agent. */
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		List<TestReport> results = new ArrayList<TestReport>();
		
		TestReport tr = new TestReport("#1", "Test if top-level description is used");
		String desc = agent.getModel().getDescription();
//		System.out.println("desc: "+desc);
		if("Extended base description".equals(desc))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong description: "+desc);
		}
		results.add(tr);
		
		tr = new TestReport("#2", "Test if all imports are used");
		String[] imps = agent.getModel().getImports();
//		System.out.println("imps: "+SUtil.arrayToString(imps));
		Set<String> impset = SUtil.arrayToSet(imps);
		if(impset.contains("b1") && impset.contains("b2") && impset.contains("eb1") && impset.contains("eb2"))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong import set: "+impset);
		}
		results.add(tr);
		
		tr = new TestReport("#3", "Test properties");
		Map<String, Object> props = agent.getModel().getProperties();
//		System.out.println("props: "+props);
		if(((UnparsedExpression)props.get("a")).getValue().equals("\"eba\"") 
			&& ((UnparsedExpression)props.get("b")).getValue().equals("\"ebb\""))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong properties: "+props);
		}
		results.add(tr);
		
		tr = new TestReport("#4", "Required services");
		RequiredServiceInfo[] reqs = agent.getModel().getServices();
//		System.out.println("req sers: "+SUtil.arrayToString(reqs));
		if(reqs[0].getDefaultBinding().getScope().equals(RequiredServiceInfo.SCOPE_COMPONENT_ONLY))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong required services: "+Arrays.toString(reqs));
		}
		results.add(tr);
		
		tr = new TestReport("#5", "Provided services");
		ProvidedServiceInfo[] provs = agent.getModel().getProvidedServices();
//		System.out.println("pro sers: "+SUtil.arrayToString(provs));
		if(provs[0].getImplementation().getClazz().getType(agent.getClassLoader()).equals(MyAService.class))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong arguments: "+Arrays.toString(reqs));
		}
		results.add(tr);
		
		tr = new TestReport("#6", "Arguments");
		IArgument[] args = agent.getModel().getArguments();
//		System.out.println("args: "+SUtil.arrayToString(args));
		if(((UnparsedExpression)args[0].getDefaultValue()).getValue().equals("\"ebval\""))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong arguments: "+Arrays.toString(args));
		}
		results.add(tr);
		
		tr = new TestReport("#7", "Results");
		IArgument[] res = agent.getModel().getResults();
//		System.out.println("res: "+SUtil.arrayToString(res));
		if(((UnparsedExpression)res[0].getDefaultValue()).getValue().equals("\"ebres\""))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong results: "+Arrays.toString(res));
		}
		results.add(tr);
		
		tr = new TestReport("#8", "Configurations");
		ConfigurationInfo[] configs = agent.getModel().getConfigurations();
//		System.out.println("configs: "+SUtil.arrayToString(configs));
		if(configs.length==1)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Wrong configurations: "+Arrays.toString(configs));
		}
		results.add(tr);
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
}
