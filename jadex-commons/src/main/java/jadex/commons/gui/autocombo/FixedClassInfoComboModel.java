package jadex.commons.gui.autocombo;

import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;

import java.util.List;

/**
 * 
 */
public class FixedClassInfoComboModel extends AbstractFixedAutoComboModel<ClassInfo>
{
	/**
	 *  Create a new AbstractFixedAutoComboModel. 
	 */
	public FixedClassInfoComboModel(AutoCompleteCombo combo, int max, List<ClassInfo> allentries)
	{
		super(combo, max, allentries);
	}
	
	/**
	 * 
	 */
	public ClassInfo convertFromString(String val)
	{
		return new ClassInfo(val);
	}
	
	/**
	 * 
	 */
	public String convertToString(ClassInfo val)
	{
		return val==null || val.getTypeName().length()==0? null: SReflect.getInnerClassName(val.getType(getCombo().getClassLoader()));
	}
}
