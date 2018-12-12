package jadex.commons.gui.autocombo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import jadex.commons.SUtil;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

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
		for(int i=0; i<allentries.size() && (max==-1 || i<max); i++)
		{
			this.entries.add(allentries.get(i));
		}
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
