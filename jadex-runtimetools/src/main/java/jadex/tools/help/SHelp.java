package jadex.tools.help;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;

/**
 *
 */
public class SHelp
{
	//-------- constants --------

	/** The jadex help set. */
	protected static HelpBroker jadex_hb;

	/** tells if a help set has been searched for */
	protected static boolean    searched_for_help_set;

	/**
	 *  @param cmp
     *  @param helpID
     *  @return helpbroker
     */
    public static HelpBroker setupHelp(Component cmp, String helpID)
    {
        HelpBroker hb = getJadexHelpBroker();
        if(hb != null)
        {
            CSH.setHelpIDString(cmp, helpID);
            hb.enableHelpKey(cmp, helpID, hb.getHelpSet());
        }

        return hb;
    }

	/*
 	 *  Get the Jadex help broker.
	 */
	public static HelpBroker getJadexHelpBroker()
	{
		if(!searched_for_help_set)
		{
			searched_for_help_set=true;
			// Create main help set and broker
			try
			{
				ClassLoader cl = SHelp.class.getClassLoader();
				URL url = HelpSet.findHelpSet(cl, "jadex/tools/help/jhelpset");
				ExtendedHelpSet hs =  new ExtendedHelpSet(cl, url);
				Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
				int w = (int)(sd.getWidth()*0.8);
				int h = (int)(sd.getHeight()*0.8);
				int xs = (int)(sd.getWidth()*0.1);
				int ys = (int)(sd.getHeight()*0.1);
				HelpSet.Presentation pr = new HelpSet.Presentation("def", true, true, new Dimension(w, h), new Point(xs, ys), "Jadex Help", null, false, null);
				hs.addPresentation(pr, true);
				jadex_hb = hs.createHelpBroker();
			}
			catch(Exception e)
			{
				System.out.println("Help Set JadexHelp not found.");
			}
			catch(ExceptionInInitializerError ex)
			{
				System.out.println("Help set initialization error.");
				//ex.getException().printStackTrace();
			}
		}
		return jadex_hb;
	}


}
