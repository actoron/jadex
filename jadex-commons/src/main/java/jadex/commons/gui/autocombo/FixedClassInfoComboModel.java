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
		return val==null || val.getTypeName().length()==0? null: SReflect.getUnqualifiedTypeName(val.getTypeName());
	}
	
	/**
	 * 
	 */
	public boolean containsVal(ClassInfo val)
	{
		boolean ret = false;
		if(val == null)// || val.trim().isEmpty())
		{	
			ret = true;
		}
		else
		{
			String tn = val.getTypeName();
			boolean	suffix = tn.indexOf(".")==-1;
			for(ClassInfo item : entries)
			{
				if(item.equals(val) || (suffix && item.getTypeName().endsWith(tn)))
				{
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public ClassInfo getModelValue(String val)
	{
		ClassInfo ret = null;
		
		boolean	suffix = val.indexOf(".")==-1;
		for(ClassInfo item : entries)
		{
			if(item.equals(val) || (suffix && item.getTypeName().endsWith(val)))
			{
				ret = item;
				break;
			}
		}
		
		return ret;
	}
}
