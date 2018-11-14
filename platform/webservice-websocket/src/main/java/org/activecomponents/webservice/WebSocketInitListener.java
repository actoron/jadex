package org.activecomponents.webservice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;


//@WebListener
public class WebSocketInitListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		try
		{
			SWebSocket.initContext(event.getServletContext()).get();
			
			final ServerContainer sc = (ServerContainer)event.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
			if(sc == null) 
				throw new RuntimeException("No javax.websocket.server.ServerContainer found in ServletContext! Make sure your Server is javax.websocket ready.");
	
			final String url = event.getServletContext().getInitParameter("websocket_url")!=null?
				(String)event.getServletContext().getInitParameter("websocket_url"): "/wswebapi";
			
			// Add the jadex.js download servlet
			ServletRegistration sr = event.getServletContext().addServlet("jadexjs-servlet", JadexjsDownloadServlet.class);
	//		sr.setInitParameter("servletInitName", "servletInitValue");
			sr.addMapping(url+"/jadex.js");
	
			System.out.println("Websocket created at: " + url);
		
			ServerEndpointConfig sec = new ServerEndpointConfig()
			{
				private Map<String, Object> props;
				
				@Override
				public String getPath()
				{
					return url;
				}

				@Override
				public Class<?> getEndpointClass()
				{
					return RestWebSocket.class;
				}

				@Override
				public Configurator getConfigurator()
				{
//					return null;
					return new Configurator()
					{
						private ServerEndpointConfig.Configurator containerDefaultConfigurator;

						ServerEndpointConfig.Configurator fetchContainerDefaultConfigurator()
						{
							for(ServerEndpointConfig.Configurator impl: ServiceLoader.load(javax.websocket.server.ServerEndpointConfig.Configurator.class))
							{
								return impl;
							}
							throw new RuntimeException("Cannot load platform configurator");
						}

						ServerEndpointConfig.Configurator getContainerDefaultConfigurator()
						{
							if(this.containerDefaultConfigurator == null)
							{
								this.containerDefaultConfigurator = fetchContainerDefaultConfigurator();
							}
							return this.containerDefaultConfigurator;

						}

						public String getNegotiatedSubprotocol(List<String> supported, List<String> requested)
						{
							return this.getContainerDefaultConfigurator().getNegotiatedSubprotocol(supported, requested);
						}

						public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested)
						{
							return this.getContainerDefaultConfigurator().getNegotiatedExtensions(installed, requested);
						}

						public boolean checkOrigin(String originHeaderValue)
						{
							return this.getContainerDefaultConfigurator().checkOrigin(originHeaderValue);
						}

						public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response)
						{
							// Does only work if the session is explicitly created in beforehand, e.g by a request listener :-(
//							HttpSession httpSession = (HttpSession)request.getHttpSession();
//							getUserProperties().put(HttpSession.class.getName(), httpSession);
						}

						public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException
						{
							return this.getContainerDefaultConfigurator().getEndpointInstance(endpointClass);
						}
					};
				}

				@Override
				public List<Class<? extends Decoder>> getDecoders()
				{
					return Collections.EMPTY_LIST;
				}

				@Override
				public List<Class<? extends Encoder>> getEncoders()
				{
					return Collections.EMPTY_LIST;
				}

				@Override
				public List<Extension> getExtensions()
				{
					return Collections.EMPTY_LIST;
				}

				@Override
				public List<String> getSubprotocols()
				{
					return Collections.EMPTY_LIST;
				}

				@Override
				public Map<String, Object> getUserProperties()
				{
					if(props==null)
						props = new HashMap<String, Object>();
					return props;
				}
			};
			
			// Make servlet context available
			sec.getUserProperties().put("servletcontext", event.getServletContext());
			
			sc.addEndpoint(sec);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		SWebSocket.cleanupContext(event.getServletContext()).get();
	}
}
