package jadex.webservice.pathvar;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

@Service
public interface IMyService
{
	/**
	 * 
	 */
	@Path("abc/{path}/method1")
	public IFuture<String> method1(@PathParam("path") String path);
	
	/**
	 * 
	 */
	@Path("renamed")
	public IFuture<String> method2();
	
	/**
	 * 
	 */
	@Path("abc/renamed")
	public IFuture<String> method3();
	
	/**
	 * 
	 */
	@Path("{p1}/abc/{p2}")
	public IFuture<String> method4(@PathParam("p1") String p1, @PathParam("p2") String p2);
}
