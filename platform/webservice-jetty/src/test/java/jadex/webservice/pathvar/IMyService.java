package jadex.webservice.pathvar;

import java.util.Date;
import java.util.Map;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Interface for testing different parameter types.
 */
@Service
public interface IMyService
{
	@Path("abc/{path}/method1")
	public IFuture<String> method1(@PathParam("path") String path);
	
	@Path("renamed")
	public IFuture<String> method2();
	
	@Path("abc/renamed")
	public IFuture<String> method3();
	
	@Path("{p1}/abc/{p2}")
	public IFuture<String> method4(@PathParam("p1") String p1, @PathParam("p2") String p2);
	
	@POST
	@Path("abc/{path}/method5")
	public IFuture<String> method5(@PathParam("path") String path);
	
	@Path("{p1}/xyz/")
	public IFuture<String> method6(@PathParam("p1") String p1, @QueryParam("name") String name);

	@Path("jkl/{p1}")
	public IFuture<String> method7(@FormParam("f1") String f1, @PathParam("p1") String p1);
	
	@Path("ok")
	public IFuture<String> method8(@FormParam("f2") int f2, @FormParam("f1") Date f1);
	
	@Path("ok2")
	public IFuture<String> method9(Map<String, Object> vals);
	
	@Path("ok3")
	public IFuture<String> method10(@QueryParam("q1") Integer q1, Integer p2);
}
