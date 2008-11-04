package jadex.tools.common;

import java.net.URL;

import javax.help.HelpSet;
import javax.help.HelpSetException;

/**
 *  Workaround for javax.help.HelpSet which does not allow presentations
 *  being added programmatically at runtime. As some properties like the
 *  screen size are only known at runtime, this extended help set makes
 *  public the addPresentation() method. 
 */
public class ExtendedHelpSet extends javax.help.HelpSet
{
	/**
     * Creates a HelpSet.  The locale for the data is either that indicated in
     * the <tt>lang</tt> attribute of the <tt>helpset</tt> tag, or
     * <tt>Locale.getDefault()</tt> if the <tt>lang</tt> attribute is not present.
     *
     * @param loader The class loader to use to locate any classes
     * required by the navigators in the Helpset
     * If loader is null, the default ClassLoader is used.
     * @param helpset The URL to the HelpSet "file"
     *
     * @exception javax.help.HelpSetException if there are problems parsing the helpset
     */
    public ExtendedHelpSet(ClassLoader loader, URL helpset) throws HelpSetException
	{
		super(loader, helpset);
    }

	/**
     * Adds a HelpSet.Presentation to the current list.
     */
    public void addPresentation(HelpSet.Presentation presentation, boolean defaultPres)
	{
		super.addPresentation(presentation, defaultPres);
    }
}
