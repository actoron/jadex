package jadex.wfms.guicomponents;

import java.lang.reflect.Array;
import java.util.Map;

public class SGuiHelper
{
	public static String beautifyName(String name)
	{
		return name.replaceAll("_", " ");
	}
	
	public static String beautifyName(String name, Map metaProperties)
	{
		String ret = (String) metaProperties.get("short_description");
		if (ret == null)
			ret = beautifyName(name);
		return ret;
	}
	
	public static Object[] selectFromArray(Object[] array, long selection)
	{
		selection = Math.abs(selection);
		selection = selection >> (63 - array.length);
		int size = Math.min(array.length, Long.bitCount(selection));
		Object[] selectArray = (Object[]) Array.newInstance(array.getClass().getComponentType(), size);
		int count = 0;
		for (int i = 0; i < array.length; ++i)
			if (((selection >> i) & 1) > 0)
				selectArray[count++] = array[i];
		return selectArray;
	}
}
