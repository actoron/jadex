package jadex.commons.gui.autocombo;

import javax.swing.plaf.metal.MetalComboBoxEditor;

import jadex.bridge.ClassInfo;
import jadex.commons.SUtil;

/**
 * 
 */
public class ComboBoxEditor extends MetalComboBoxEditor//BasicComboBoxEditor()
{
	/** The value. */
	protected Object val;
	
	/** The model. */
	protected AbstractAutoComboModel<Object> model;
	
	/**
	 *  Create a new editor.
	 */
	public ComboBoxEditor(AbstractAutoComboModel<?> model)
	{
		this.model = (AbstractAutoComboModel<Object>)model;
	}

	/**
	 *  Set the item.
	 */
	public void setItem(Object obj)
	{
		if(obj==null || SUtil.equals(val, obj))
			return;
		
		String text = obj instanceof ClassInfo? model.convertToString(obj): "";
	    if(text!=null && !text.equals(editor.getText())) 
	    {
	    	val = obj;
	    	editor.setText(text);
	    }
	}
	
	/**
	 *  Get the item.
	 */
	public Object getItem()
	{
		return val;
	}
}
