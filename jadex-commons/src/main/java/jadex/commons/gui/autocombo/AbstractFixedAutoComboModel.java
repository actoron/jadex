package jadex.commons.gui.autocombo;

import jadex.commons.SUtil;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

/**
 * 
 */
public abstract class AbstractFixedAutoComboModel<T> extends AbstractAutoComboModel<T>
{
	protected List<T> allentries;
	
	/**
	 *  Create a new AbstractFixedAutoComboModel. 
	 */
	public AbstractFixedAutoComboModel(AutoCompleteCombo combo, int max, List<T> allentries)
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
	public ISubscriptionIntermediateFuture<T> doSetPattern(final String pattern)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		ISubscriptionIntermediateFuture<T> ret;
		
		if(pattern==null)
		{
//			if(entries.size()>0)
//				getCombo().setSelectedItem(getElementAt(0));
		}
		else
		{				
			entries.clear();

			final Pattern pat = SUtil.createRegexFromGlob(pattern+"*");
			
			for(T tst: allentries)
			{
				String str = convertToString(tst);
				Matcher m = pat.matcher(str);
				if(m.matches())
				{
					entries.add(tst);
				}
			}
			
//			getCombo().setSelectedItem(pattern);
			getCombo().updatePopup();
		}
		
		ret = new SubscriptionIntermediateFuture<T>();
		((SubscriptionIntermediateFuture<T>)ret).setResult(null);
		return ret;
	}
}

