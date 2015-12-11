package jadex.commons.gui.autocombo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingResultListener;

/**
 * 
 */
public abstract class AbstractAutoComboModel<T> extends AbstractListModel implements ComboBoxModel
{
	protected T selected;

	protected int max	= 20;

	protected List<T> entries;
	
	protected AutoCompleteCombo<T> combo;
	
	/**
	 * 
	 */
	public AbstractAutoComboModel(AutoCompleteCombo<T> combo, int max)
	{
		this.combo = combo;
		this.max = max;
		this.entries = new ArrayList<T>();
	}
	
	/**
	 * 
	 */
	public abstract T convertFromString(String val);
	
	/**
	 * 
	 */
	public abstract String convertToString(T val);

	/**
	 * 
	 */
	public void addToTop(String val)
	{
		addValToTop(convertFromString(val));
	}
	
	/**
	 * 
	 */
	public boolean contains(String val)
	{
		return containsVal(convertFromString(val));
	}
		
	/**
	 * 
	 */
	public void addValToTop(T val)
	{
		if(val == null || entries.contains(val))
			return;

		if(entries.size() == 0)
		{
			entries.add(val);
		}
		else
		{
			entries.add(0, val);
		}
		
		while(max!=-1 && entries.size()>max)
		{
			int index = entries.size() - 1;
			entries.remove(index);
		}

//		fireIntervalAdded(AbstractAutoComboModel.this, size1, size2 - 1);
		fireContentsChanged(AbstractAutoComboModel.this, 0, entries.size() - 1);
		
//		setPattern(null);
		setSelectedItem(val);
	}

	/**
	 * 
	 */
	public Object getSelectedItem()
	{
		return selected;
	}

	/**
	 * 
	 */
	public void setSelectedItem(Object obj)
	{
//		System.out.println("sel obj is: "+obj);
		
//		if(obj instanceof String)
//			System.out.println("herer");
		
		if((selected != null && !selected.equals(obj))
			|| (selected == null && obj != null))
		{
			selected = (T)obj;
			fireContentsChanged(this, -1, -1);
		}
	}
	
	/**
	 * 
	 */
	public void setSelectedItemQuiet(Object obj)
	{
		System.out.println("sel quiet is: "+obj);
		selected = (T)obj;
	}

	/**
	 * 
	 */
	public int getSize()
	{
		return entries.size();
	}

	/**
	 * 
	 */
	public T getElementAt(int index)
	{
		return entries.get(index);
	}
	
	/**
	 * 
	 */
	public boolean containsVal(T val)
	{
		if(val == null)// || val.trim().isEmpty())
			return true;
		
//		val = val.toLowerCase();
		for(T item : entries)
		{
			if(item.equals(val))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  Get the combo.
	 *  @return The combo.
	 */
	public AutoCompleteCombo<T> getCombo()
	{
		return combo;
	}

	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<T> setPattern(String pattern)
	{
		final int size1 = getSize();

		ISubscriptionIntermediateFuture<T> ret = doSetPattern(pattern);
		
		ret.addResultListener(new SwingResultListener<Collection<T>>(new IResultListener<Collection<T>>()
		{
			public void resultAvailable(Collection<T> result)
			{
				int size2 = getSize();

//				System.out.println("sizes: "+size1+" "+size2);
				
				fireChangeEvents(size1, size2);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}));

		return ret;
	}
	
	/**
	 * 
	 */
	public abstract ISubscriptionIntermediateFuture<T> doSetPattern(final String pattern);
	
	/**
	 *  Get value from model that equals converted value.
	 */
	public T getModelValue(String text)
	{
		T ret = null;
		T o = convertFromString(text);
		for(T tmp: entries)
		{
			if(tmp.equals(o))
			{
				ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected void fireChangeEvents(int size1, int size2)
	{
		fireContentsChanged(this, 0, size2-1);
		
		if(size1<size2)
		{
			fireIntervalAdded(this, size1, size2 - 1);
			fireContentsChanged(this, 0, size1 - 1);
		}
		else if(size1>size2)
		{
			fireIntervalRemoved(this, size2, size1 - 1);
			fireContentsChanged(this, 0, size2 - 1);
		}
	}
}
