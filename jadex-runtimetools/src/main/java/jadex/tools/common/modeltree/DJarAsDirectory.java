package jadex.tools.common.modeltree;


import java.io.File;
import java.util.zip.ZipEntry;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.delegate.ADelegate;

/**
 *
 */
public class DJarAsDirectory extends ADelegate
{
	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz,IAssembler asm) throws Exception
	{
		JarAsDirectory	jad	=  new JarAsDirectory((String)asm.getAttributeValue("fullpath"));
		jad.jarpath	= (String)asm.getAttributeValue("jarpath");
		String	entry	= (String)asm.getAttributeValue("entry");
		if(entry!=null)
			jad.entry	= new ZipEntry(entry);
		jad.entries	= (File[])asm.getAttributeValue("entries");
		return jad;
	}


	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{
		JarAsDirectory	jad	= (JarAsDirectory)o;
		int	entries_id	= mill.declare(jad.entries, classloader);
		mill.startConcept(o);
		mill.put("fullpath", jad.toString());
		mill.put("jarpath", jad.jarpath);
		if(jad.entry!=null)
			mill.put("entry", jad.entry.getName());
		mill.put("entries", entries_id);
	}
	
	/** 
	 * @param clazz
	 * @param value
	 * @return the boolean expression
	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		return new JarAsDirectory((String)value);
	}
}
