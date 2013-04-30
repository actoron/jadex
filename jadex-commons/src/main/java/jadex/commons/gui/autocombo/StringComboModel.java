package jadex.commons.gui.autocombo;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

/**
 * 
 */
public class StringComboModel extends AbstractAutoComboModel<String>
{
	protected List<String> allentries;
	
	/**
	 *  Create a new StringComboModel. 
	 */
	public StringComboModel(AutoCompleteCombo combo, int max, List<String> allentries)
	{
		super(combo, max);
		this.allentries = allentries;
	}
	
	/**
	 * 
	 */
	public String convertFromString(String val)
	{
		return val;
	}
	
	/**
	 * 
	 */
	public String convertToString(String val)
	{
		return val;
	}
	
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<String> doSetPattern(final String pattern)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		ISubscriptionIntermediateFuture<String> ret;
		
		if(pattern==null || pattern.isEmpty())
		{
			if(entries.size()>0)
				getCombo().setSelectedItem(getElementAt(0));
			
			
		}
		else
		{				
			entries.clear();

			final Pattern pat = SUtil.createRegexFromGlob(pattern+"*");
			
			for(String tst: allentries)
			{
				Matcher m = pat.matcher(tst);
				if(m.matches())
				{
					entries.add(tst);
				}
			}
			
			getCombo().setSelectedItem(pattern);
			getCombo().updatePopup();
		}
		
		ret = new SubscriptionIntermediateFuture<String>();
		((SubscriptionIntermediateFuture<String>)ret).setResult(null);
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public boolean contains(Class<?> val)
//	{
//		if(val == null)// || val.trim().isEmpty())
//			return true;
//		
////		val = val.toLowerCase();
//		for(Class<?> item : entries)
//		{
//			if(item.equals(val))
//			{
//				return true;
//			}
//		}
//		return false;
//	}
}
