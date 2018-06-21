package jadex.webservice.grizzlytest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerChain;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;


public class GrizzlyTest
{
	public static void main(String[] args) throws Exception
	{
		HttpServer server = HttpServer.createSimpleServer();
		server.start();

		Class< ? > clazz = HttpHandlerChain.class;
		Field f = clazz.getDeclaredField("LOCAL_HOST");
		Field mf = Field.class.getDeclaredField("modifiers");
		mf.setAccessible(true);
		mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		f.setAccessible(true);

		f.set(null, "hugo");
		server.getServerConfiguration().addHttpHandler(new HttpHandler()
		{
			public void service(Request request, Response response)
					throws Exception
			{
				response.setContentType("text/plain");
				response.getWriter().write("hugo");
			}
		}, "/test");

		f.set(null, "localhost");
		server.getServerConfiguration().addHttpHandler(new HttpHandler()
		{
			public void service(Request request, Response response)
					throws Exception
			{
				response.setContentType("text/plain");
				response.getWriter().write("default");
			}
		}, "/test");

		Object mon = new Object();
		synchronized(mon)
		{
			mon.wait();
		}
	}
}
