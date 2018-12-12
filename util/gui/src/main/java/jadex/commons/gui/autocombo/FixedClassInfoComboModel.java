package jadex.commons.gui.autocombo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;

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
		super(combo, max, sort(allentries)); 
	}
	
	/**
	 * 
	 */
	public static List<ClassInfo> sort(List<ClassInfo> entries)
	{
		Collections.sort(entries, new Comparator<ClassInfo>()
		{
			public int compare(ClassInfo c1, ClassInfo c2)
			{
				return c1.getPrefixNotation().compareTo(c2.getPrefixNotation());
			}
		});
		return entries;
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
//		return val==null || val.getTypeName().length()==0? null: val.getPrefixNotation();
		return val==null || val.getTypeName().length()==0? null: SReflect.getUnqualifiedTypeName(val.getGenericTypeName());
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
			if(item.getTypeName().equals(val) || item.getGenericTypeName().equals(val) || (suffix && item.getTypeName().endsWith(val)))
			{
				ret = item;
				break;
			}
		}
		
		return ret;
	}
}
