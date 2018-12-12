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
		copyEntries();
		
//		System.out.println("all: "+allentries);
	}
	
	/**
	 * 
	 */
	protected void copyEntries()
	{
		int size1 = entries.size();
		entries.clear();
		for(int i=0; i<allentries.size() && (max==-1 || i<max); i++)
		{
			this.entries.add(allentries.get(i));
		}
		int size2 = entries.size();
		
		fireChangeEvents(size1, size2);
		
//		fireIntervalAdded(this, size1, size2 - 1);
//		fireContentsChanged(this, 0, size1 - 1);
	}
	
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<T> doSetPattern(final String pattern)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		ISubscriptionIntermediateFuture<T> ret;
		
		if(pattern==null && entries.size()==0)
		{
			copyEntries();
//			if(entries.size()>0)
//				getCombo().setSelectedItem(getElementAt(0));
		}
		else
		{		
			int size1 = entries.size();
			entries.clear();
//
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
			int size2 = entries.size();
			
//			System.out.println("entries: "+entries);
			
//			fireChangeEvents(size1, size2);
			fireContentsChanged(this, 0, size2);
			
//			getCombo().setSelectedItem(pattern);
			getCombo().updatePopup();
		}
		
		ret = new SubscriptionIntermediateFuture<T>();
		((SubscriptionIntermediateFuture<T>)ret).setResult(null);
		return ret;
	}

}

