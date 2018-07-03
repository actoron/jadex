package org.activecomponents.webservice;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;


/**
 * Standalone webapp starter using Jetty.
 */
public class EmbeddedJettyMain
{
	public static void main(String[] args)
	{
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);

		WebSocketInitListener init;
		server.addConnector(connector);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		ClassLoader classLoader = EmbeddedJettyMain.class.getClassLoader();
		URL resource = EmbeddedJettyMain.class.getResource("/WEB-INF/web.xml");
		webapp.setDescriptor(resource.toString());

		URL root = null;
		try
		{
			root = resource.toURI().resolve("..").toURL();
		}
		catch(URISyntaxException e)
		{
			e.printStackTrace();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}

		System.out.println("webapp root is: " + root);

		webapp.setResourceBase(root.toString());
		webapp.setParentLoaderPriority(true);
		webapp.setCompactPath(true);

		// for rewriting, e.g. angular routing:

		// RewriteHandler rewrite = new RewriteHandler();
		// rewrite.setRewriteRequestURI(true);
		// rewrite.setRewritePathInfo(false);
		// rewrite.setOriginalPathAttribute("requestedPath");
		//
		// RewriteRegexRule rule = new RewriteRegexRule("(/\\w+)+",
		// "/index.html") {
		// @Override
		// public String apply(String target, HttpServletRequest request,
		// HttpServletResponse response, Matcher matcher) throws IOException {
		//// System.out.println("rewriting: " + target);
		// if (target.equals("/wswebapi")) {
		// return target;
		// } else {
		// return super.apply(target, request, response, matcher);
		// }
		// }
		//
		// @Override
		// public void applyURI(Request request, String oldURI, String newURI)
		// throws IOException {
		// super.applyURI(request, oldURI, newURI);
		// }
		// };
		// rule.setTerminating(true);
		// rewrite.addRule(rule);
		//
		// rewrite.setHandler(webapp);
		//
		// HandlerList list = new HandlerList();
		//
		// list.addHandler(rewrite);
		// server.setHandler(list);

		server.setHandler(webapp);

		try
		{
			// Initialize javax.websocket layer
			ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(webapp);

			server.start();
			server.join();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				server.stop();
			}
			catch(Exception e1)
			{
				// e1.printStackTrace();
			}
		}
	}
}
