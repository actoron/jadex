package jadex.bpmn.editor.gui.propertypanels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;

public class ConfigurationModel implements ComboBoxModel
{
	/** Listeners. */
	protected List<ListDataListener> listeners;
	
	/** The model info. */
	protected IModelInfo modelinfo;
	
	/** Selection. */
	protected int selection;
	
	/**
	 *  Creates the model.
	 *  
	 * 	@param modelinfo The model info.
	 */
	public ConfigurationModel(IModelInfo modelinfo)
	{
		this.listeners = new ArrayList<ListDataListener>();
		this.modelinfo = modelinfo;
		this.selection = 0;
	}
	
	/**
	 * 
	 */
	public int getSize()
	{
		int s = modelinfo.getConfigurations().length + 1;
		return s;
	}

	/**
	 * 
	 */
	public String getElementAt(int index)
	{
		String ret = index == 0 ? null : modelinfo.getConfigurations()[Math.min(getSize()-2, index-1)].getName();
		return ret;
	}

	/**
	 * 
	 */
	public void addListDataListener(ListDataListener l)
	{
		listeners.add(l);
	}

	/**
	 * 
	 */
	public void removeListDataListener(ListDataListener l)
	{
		listeners.remove(l);
	}

	/**
	 * 
	 */
	public void setSelectedItem(Object anItem)
	{
		selection = 0;
		ConfigurationInfo[] infos = modelinfo.getConfigurations();
		for (int i = 0; i < infos.length; ++i)
		{
			if (infos[i] != null && infos[i].getName().equals(anItem))
			{
				selection = i + 1;
				break;
			}
		}
	}

	/**
	 * 
	 */
	public Object getSelectedItem()
	{
		return getElementAt(selection);
	}

	/**
	 * 
	 */
	public void fireModelChange()
	{
		for (ListDataListener listener : listeners)
		{
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, Integer.MAX_VALUE));
		}
	}
}
