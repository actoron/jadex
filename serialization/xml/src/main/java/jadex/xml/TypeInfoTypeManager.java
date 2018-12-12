package jadex.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jadex.xml.stax.QName;

/**
 *  The type info type manager organizes type infos via their objects
 *  types. It allows fetching a type info for a given object type. 
 *  (E.g. the writer uses it for fetching type infos per object to write).
 */
public class TypeInfoTypeManager
{
	//-------- attributes --------
	
	/** The type mappings. */
	protected Map typeinfos;
		
	//-------- constructors --------
	
	/**
	 *  Create a type info manager.
	 */
	public TypeInfoTypeManager(Set typeinfos)
	{
		if(typeinfos!=null)
		{
			for(Iterator it=typeinfos.iterator(); it.hasNext(); )
			{
				addTypeInfo((TypeInfo)it.next());
			}
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Add a typeinfo.
	 */
	public synchronized void addTypeInfo(TypeInfo typeinfo)
	{
		if(typeinfos==null)
			typeinfos = new HashMap();
		
//		System.out.println("added typeinfo: "+typeinfo+" "+typeinfo.getTypeInfo());
//		TypeInfo mapinfo = (TypeInfo)it.next();
		TreeSet maps = (TreeSet)typeinfos.get(typeinfo.getTypeInfo());
		if(maps==null)
		{
			maps = new TreeSet(new AbstractInfo.SpecificityComparator());
			typeinfos.put(typeinfo.getTypeInfo(), maps);
		}
		maps.add(typeinfo);
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public synchronized TypeInfo getTypeInfo(Object type, QName[] fullpath)
	{
//		Object type = getObjectType(object, context);
//		System.out.println("type is: "+type);
		TypeInfo ret = findTypeInfo((Set)typeinfos.get(type), fullpath);
		
		return ret;
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public synchronized Set getTypeInfosByType(Object type)
	{
		return (Set)typeinfos.get(type);
	}
	
	/**
	 *  Find a type info from a set of possible matching typeinfos.
	 *  Note that here the typeinfo path is checked for compliance
	 *  with the stack path.
	 */
	public synchronized TypeInfo findTypeInfo(Set typeinfos, QName[] fullpath)
	{
		TypeInfo ret = null;
		if(typeinfos!=null)
		{
			for(Iterator it=typeinfos.iterator(); ret==null && it.hasNext(); )
			{
				TypeInfo ti = (TypeInfo)it.next();
				QName[] tmp = ti.getXMLPathElementsWithoutElement();
				boolean ok = tmp==null || tmp.length<=fullpath.length;;
				if(tmp!=null)
				{
					for(int i=1; i<=tmp.length && ok; i++)
					{
						ok = tmp[tmp.length-i].equals(fullpath[fullpath.length-i]);
					}
				}
				if(ok)
					ret = ti;
//				if(fullpath.endsWith(tmp.getXMLPathWithoutElement())) // && (tmp.getFilter()==null || tmp.getFilter().filter(rawattributes)))
			}
		}
		return ret;
	}
}
