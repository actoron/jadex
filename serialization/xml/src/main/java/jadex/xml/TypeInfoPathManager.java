package jadex.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jadex.xml.stax.QName;

/**
 *  Type path info manager. This manager organizes type infos via their paths.
 *  This allows for searching for a type info via a specific tag or subpath of
 *  tags. (Used e.g. by the reader to determine which type to use for a tag).
 */
public class TypeInfoPathManager
{
	//-------- attributes --------
	
	/** The type mappings. */
	protected Map typeinfos;
	
	//-------- constructors --------
	
	/**
	 *  Create a new manager.
	 */
	public TypeInfoPathManager(Set typeinfos)
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
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public synchronized TypeInfo getTypeInfo(QName tag, QName[] fullpath, Map<String, String> rawattributes)
	{
		return findTypeInfo((Set)typeinfos.get(tag), fullpath, rawattributes);
	}
	
	/**
	 *  Add a type info.
	 *  @param typeinfo The type info.
	 */
	public synchronized void addTypeInfo(TypeInfo typeinfo)
	{
		if(typeinfos==null)
			typeinfos = new HashMap();
		
		if(typeinfo.getXMLTag()==null)
			throw new RuntimeException("XML tag must not be null: "+typeinfo);
		
		TreeSet tiset = (TreeSet)typeinfos.get(typeinfo.getXMLTag());
		if(tiset==null)
		{
			tiset = new TreeSet(new AbstractInfo.SpecificityComparator());
			typeinfos.put(typeinfo.getXMLTag(), tiset);
		}
		tiset.add(typeinfo);
	}
	
	/**
	 *  Find type find in the set of type infos.
	 */
	protected synchronized TypeInfo findTypeInfo(Set typeinfos, QName[] fullpath, Map rawattributes)
	{
		TypeInfo ret = null;
		if(typeinfos!=null)
		{
			for(Iterator it=typeinfos.iterator(); ret==null && it.hasNext(); )
			{
				TypeInfo ti = (TypeInfo)it.next();
				QName[] tmp = ti.getXMLPathElements();
				boolean ok = (ti.getFilter()==null || ti.getFilter().filter(rawattributes)) && 
					(tmp==null || tmp.length<=fullpath.length);
				if(tmp!=null)
				{
					for(int i=1; i<=tmp.length && ok; i++)
					{
						ok = tmp[tmp.length-i].equals(fullpath[fullpath.length-i]);
					}
				}
				if(ok)
					ret = ti;
			}
		}
		return ret;
	}
}
