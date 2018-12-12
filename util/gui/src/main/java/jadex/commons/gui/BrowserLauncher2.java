package jadex.commons.gui;

import java.applet.Applet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;


/**
 *  Provides a static method to start a browser.
 *  Relies on the original BrowserLauncher implementation,
 *  but provides additional support for applets and JNLP.
 */
public class BrowserLauncher2
{
	//---------- static fields ----------
	
	/** The applet (has to be set from the outside). */
	protected static Applet	applet;
	
	/**
	 *  Set the current applet.
	 */
	public static void	setApplet(Applet applet)
	{
		BrowserLauncher2.applet	= applet;
	}
	
	//---------- methods ----------
	
	/**
	 *  Open an url in a browser.
	 */
	public static void	openURL(String url)	throws IOException
	{
		// First try browser launcher.
		try
		{
			BrowserLauncher.openURL(url);
		}
		
		// In case of security exception try applet or JNLP.
		catch(SecurityException e)
		{
			if(applet!=null)
			{
				applet.getAppletContext().showDocument(new URL(url), "BrowserLauncher");
			}
			else
			{
				try
				{
					// Use reflection to avoid compile-time dependency to JNLP.
		
					Class	servicemanagerclass	= Class.forName("javax.jnlp.ServiceManager");
					Method	lookup	= servicemanagerclass.getMethod("lookup", new Class[]{String.class});
					Object	basicservice	= lookup.invoke(null, new Object[]{"javax.jnlp.BasicService"});
					
					// basicservice.showDocument(new URL(url));
					Class	basicserviceclass	= Class.forName("javax.jnlp.BasicService");
					Method	showdocument	= basicserviceclass.getMethod("showDocument", new Class[]{URL.class});
					showdocument.invoke(basicservice, new Object[]{new URL(url)});
				}
				catch(InvocationTargetException e2)
				{
					if(e2.getTargetException() instanceof IOException)
					{
						throw (IOException)e2.getTargetException();
					}
					else
					{
						StringWriter	sw	= new StringWriter();
						e2.printStackTrace(new PrintWriter(sw));
						throw new RuntimeException(sw.toString());
					}
				}
				catch(Exception e2)
				{
					StringWriter	sw	= new StringWriter();
					e2.printStackTrace(new PrintWriter(sw));
					throw new IOException(sw.toString());
				}
			}
		}
	}
}
