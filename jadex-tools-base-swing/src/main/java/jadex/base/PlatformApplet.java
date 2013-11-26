package jadex.base;

import jadex.commons.BrowserLauncher2;

import java.applet.Applet;
import java.util.StringTokenizer;

/**
 *  An applet used to start the Jadex platform.
 */
public class PlatformApplet extends Applet
{
	public void init()
	{
		// Initialize browser launcher.
		BrowserLauncher2.setApplet(this);
		
		// Start platform.
		String[]	args;
		String	pargs	= getParameter("args");
		if(pargs!=null)
		{
			StringTokenizer	stok	= new StringTokenizer(pargs);
			args	= new String[stok.countTokens()];
			for(int i=0; i<args.length; i++)
				args[i]	= stok.nextToken();
		}
		else
		{
			args	= new String[0];
		}
		
		try
		{
			Starter.main(args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// Todo: shutdown
}
