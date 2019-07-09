package jadex.webservice.pathvar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentResult;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Publish;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * Tests if agent arguments and results can be inferred from field declaration.
 */
@Agent
@ProvidedServices(@ProvidedService(name="myser", type=IMyService.class, scope=ServiceScope.PLATFORM,
	implementation=@Implementation(expression="$pojoagent"),
	publish=@Publish(publishtype=IPublishService.PUBLISH_RS, 
	publishid="[http://localhost:7000/]myservice"
)))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class PathVarAgent extends JunitAgentTest 
	implements IMyService
{
	@Agent
	protected IInternalAccess agent;

	@AgentArgument
	protected String text;
	
	@AgentArgument
	protected int cnt;
	
	@AgentResult
	protected String someresult;

	public PathVarAgent()
	{
		getConfig().setValue("jettyrspublish", true);
		//getConfig().getExtendedPlatformConfiguration().setDebugFutures(true);
	}
	
	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{
		String baseurl = "http://localhost:7000/myservice";
		
		final List<TestReport> reports = new ArrayList<>();
		
		new Thread(()->
		{
			Client client = ClientBuilder.newClient();
			
			//Future<String> ret = new Future<>();
			//RestInvocationHelper.performRequest(agent, baseurl, "abc/def/method1", null, null, null, GET.class, false, ret);
			//System.out.println(ret.get());

			Response result = null;
			String res = null;
			TestReport tr = null;
			
			result = client.target(baseurl).path("abc/def/method1").request(MediaType.TEXT_PLAIN).get();
			res = result.readEntity(String.class);
			tr = new TestReport("#1", "Test if method1 works.");
			if("def".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			result = client.target(baseurl).path("renamed").request(MediaType.TEXT_PLAIN).get();
			res = result.readEntity(String.class);
			tr = new TestReport("#2", "Test if method2 works.");
			if("method2".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			result = client.target(baseurl).path("abc/renamed").request(MediaType.TEXT_PLAIN).get();
			res = result.readEntity(String.class);
			tr = new TestReport("#3", "Test if method3 works.");
			if("method3".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			result = client.target(baseurl).path("p1/abc/p2").request(MediaType.TEXT_PLAIN).get();
			res = result.readEntity(String.class);
			tr = new TestReport("#4", "Test if method4 works.");
			if("p1 p2".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			result = client.target(baseurl).path("abc/def/method5").request(MediaType.TEXT_PLAIN).post(null);
			res = result.readEntity(String.class);
			tr = new TestReport("#5", "Test if method5 works.");
			if("def".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			result = client.target(baseurl).path("abc/xyz").queryParam("name", "hans").request(MediaType.TEXT_PLAIN).get();
			res = result.readEntity(String.class);
			tr = new TestReport("#6", "Test if method6 works.");
			if("abc hans".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			// Sending POST with values as url-endoded form content name1=val1&name2=val2&...
			//Map<String, String> vals = new HashMap<>();
			//vals.put("f1", "f1val");
			MultivaluedMap<String, String> vals2 = new MultivaluedHashMap<>();
			vals2.add("f1", "f1val");
			vals2.add("f2", "f2val");
			result = client.target(baseurl).path("jkl/p1val").request(MediaType.TEXT_PLAIN).post(Entity.form(vals2));
			res = result.readEntity(String.class);
			tr = new TestReport("#7", "Test if method7 works.");
			if("f1val p1val".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			// Sending POST with values map as json content 
			Map<String, Object> vals = new HashMap<>();
			vals.put("f2", 4711);
			//Date now = new Date();
			Date date = new GregorianCalendar(2019, Calendar.FEBRUARY, 23).getTime();
			vals.put("f1", date);
			String json = JsonTraverser.objectToString(vals, agent.getClassLoader());
			//System.out.println("json: "+json);
			result = client.target(baseurl).path("ok").request(MediaType.TEXT_PLAIN).post(Entity.json(json));
			res = result.readEntity(String.class);
			//System.out.println("result is: "+res);
			tr = new TestReport("#8", "Test if method8 works.");
			if(("4711 "+date.getTime()).equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			
			// Sending POST with values map as json content 
			vals = new HashMap<>();
			vals.put("f", 4711);
			json = JsonTraverser.objectToString(vals, agent.getClassLoader());
			//System.out.println("json: "+json);
			result = client.target(baseurl).path("ok2").request(MediaType.TEXT_PLAIN).post(Entity.json(json));
			res = result.readEntity(String.class);
			//System.out.println("result is: "+res);
			tr = new TestReport("#9", "Test if method9 works.");
			if("1".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			// Using get with 2 query parameters. One is fetched per explicit name, one per implicit name (=position)
			result = client.target(baseurl).path("ok3").queryParam("q1", "11").queryParam("1", "22").request(MediaType.TEXT_PLAIN).get();
			res = result.readEntity(String.class);
			tr = new TestReport("#10", "Test if method10 works.");
			if("11 22".equals(res))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong result: "+res);
			}
			reports.add(tr);
			
			//System.out.println(reports);
			
			agent.scheduleStep(ia->
			{
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", 
					new Testcase(reports.size(), reports.toArray(new TestReport[reports.size()])));
				agent.killComponent();
				return IFuture.DONE;
			});
			
		}).start();
	}
	
	@Path("abc/{path}/method1")
	public IFuture<String> method1(@PathParam("path") String path)
	{
		return new Future<String>(path);
	}
	
	@Path("renamed")
	public IFuture<String> method2()
	{
		return new Future<String>("method2");
	}
	
	@Path("abc/renamed")
	public IFuture<String> method3()
	{
		return new Future<String>("method3");
	}
	
	@Path("{p1}/abc/{p2}")
	public IFuture<String> method4(@PathParam("p1") String p1, @PathParam("p2") String p2)
	{
		return new Future<String>(p1+" "+p2);
	}
	
	@POST
	@Path("abc/{path}/method5")
	public IFuture<String> method5(@PathParam("path") String path)
	{
		return new Future<String>(path);
	}
	
	@Path("{p1}/xyz/")
	public IFuture<String> method6(@PathParam("p1") String p1, @QueryParam("name") String name)
	{
		return new Future<String>(p1+" "+name);
	}
	
	@Path("jkl/{p1}")
	public IFuture<String> method7(@FormParam("f1") String f1, @PathParam("p1") String p1)
	{
		return new Future<String>(f1+" "+p1);
	}
	
	@Path("ok")
	public IFuture<String> method8(@FormParam("f2") int f2, @FormParam("f1") Date f1)
	{
		return new Future<String>(f2+" "+f1.getTime());
	}
	
	@Path("ok2")
	public IFuture<String> method9(Map<String, Object> vals)
	{
		return new Future<String>(""+vals.size());
	}
	
	@Path("ok3")
	public IFuture<String> method10(@QueryParam("q1") Integer q1, Integer p2)
	{
		return new Future<String>(q1+" "+p2);
	}
	
	public static void main(String[] args)
	{
		Client client = ClientBuilder.newClient();
		 
		String baseurl = "http://localhost:8080/myservice";
		 
		Response result = client.target(baseurl).path("abc/def/method1").request(MediaType.APPLICATION_JSON).get();
		
		System.out.println(result.readEntity(String.class));
	}
}
