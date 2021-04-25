package jadex.extension.rs.publish;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpSession;

import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoWSD;
import jadex.commons.future.Future;

/**
 *  Class representing a nano server.
 */
public abstract class NanoHttpServer extends NanoWSD//NanoHTTPD 
{
	/** The request handler. */
	protected IRequestHandlerService handler;
	
	public NanoHttpServer(int port, IRequestHandlerService handler) 
	{
		super(port);
		this.handler = handler;
	}
	
	@Override 
	public Response serveHttp(IHTTPSession session) 
	{
		//System.out.println("serve called: "+session.getUri());
		
		Response[] ret = new Response[1];
		
		NanoHttpServletResponseWrapper resp = new NanoHttpServletResponseWrapper(session);
		NanoHttpServletRequestWrapper req = new NanoHttpServletRequestWrapper(session, resp);
		
		// todo: make handle request use async context return 
		handler.handleRequest(req, resp, null).get();
		
		Future<Void> wait = new Future<>();
		
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				IStatus status = Response.Status.lookup(resp.getStatus());
				String mimetype = resp.getContentType();
				byte[] out = resp.getOutput().toByteArray();
				InputStream is = new ByteArrayInputStream(out);
				ret[0] = newFixedLengthResponse(status, mimetype, is, out.length);
				for(String hn: resp.getHeaderNames())
					ret[0].addHeader(hn, resp.getHeader(hn));
				HttpSession ses = req.getSession(false);
				if(ses!=null)
				{
					ret[0].addHeader("Set-Cookie", NanoHttpServletRequestWrapper.HEADER_NANO_SESSIONID+"="+ses.getId());
					//ret[0].addHeader(NanoHttpServletRequestWrapper.HEADER_NANO_SESSIONID, ses.getId());
				}
			}
		};
		
		if(req.isAsyncStarted())
		{
			req.getAsyncContext().addListener(new AsyncListener()
			{
				@Override
				public void onTimeout(AsyncEvent event) throws IOException
				{
				}
				
				@Override
				public void onStartAsync(AsyncEvent event) throws IOException
				{
				}
				
				@Override
				public void onError(AsyncEvent event) throws IOException
				{
				}
				
				@Override
				public void onComplete(AsyncEvent event) throws IOException
				{
					run.run();
					wait.setResult(null);
				}
			});
		}
		else
		{
			run.run();
			wait.setResult(null);
		}
		
		wait.get();
		
		return ret[0];
	}
}