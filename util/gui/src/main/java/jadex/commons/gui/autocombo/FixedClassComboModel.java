package jadex.commons.gui.autocombo;

import java.util.List;

import jadex.commons.SReflect;

/**
 * 
 */
public class FixedClassComboModel extends AbstractFixedAutoComboModel<Class<?>>
{
	/**
	 *  Create a new AbstractFixedAutoComboModel. 
	 */
	public FixedClassComboModel(AutoCompleteCombo combo, int max, List<Class<?>> allentries)
	{
		super(combo, max, allentries);
	}
	
	/**
	 * 
	 */
	public Class<?> convertFromString(String val)
	{
		return SReflect.findClass0(val, null, getCombo().getClassLoader());
	}
	
	/**
	 * 
	 */
	public String convertToString(Class<?> val)
	{
		return val==null? "": SReflect.getInnerClassName(val);
	}
}
