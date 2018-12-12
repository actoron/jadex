package jadex.commons.gui.autocombo;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;

/**
 * 
 */
public class ClassInfoComboBoxRenderer extends BasicComboBoxRenderer
{
	/**
	 * 
	 */
	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus)
	{
		ClassInfo ci = (ClassInfo)value;
		
		String txt = null;
		
		if(getClassLoader()!=null)
		{
			Class<?> cl = ci.getType(getClassLoader());
			if(cl!=null)
			{
				txt = SReflect.getInnerClassName(cl)+" - "+cl.getPackage().getName();
			}
		}
		
		if(txt==null)
		{
			txt = ci.getPrefixNotation();
//			String fn = ci.getTypeName();
//			int idx = fn.lastIndexOf(".");
//			if(idx!=-1)
//			{
//				String cn = fn.substring(idx+1);
//				String pck = fn.substring(0, idx);
//				txt = cn+" - "+pck;
//			}
//			else
//			{
//				txt = fn;
//			}
		}
		
		return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
	}
	
	/**
	 *  Get the classloader for loading a class.
	 */
	protected ClassLoader getClassLoader()
	{
		return null;
	}
}
