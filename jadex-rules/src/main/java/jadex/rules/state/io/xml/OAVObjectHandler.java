package jadex.rules.state.io.xml;

import jadex.commons.xml.IObjectHandler;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamReader;

/**
 *  Handler for reading XML into OAV objects.
 */
public class OAVObjectHandler implements IObjectHandler
{
	//-------- static part --------
	
	/** The built-in types. */
	protected static Set builtintypes;
	
	static
	{
		builtintypes = new HashSet();
		builtintypes.add(String.class);
		builtintypes.add(int.class);
		builtintypes.add(Integer.class);
		builtintypes.add(long.class);
		builtintypes.add(Long.class);
		builtintypes.add(float.class);
		builtintypes.add(Float.class);
		builtintypes.add(double.class);
		builtintypes.add(Double.class);
		builtintypes.add(boolean.class);
		builtintypes.add(Boolean.class);
		builtintypes.add(short.class);
		builtintypes.add(Short.class);
		builtintypes.add(byte.class);
		builtintypes.add(Byte.class);
		builtintypes.add(char.class);
		builtintypes.add(Character.class);
	}
	
	//-------- attributes --------
	
	/** The type mappings. */
	protected Set typeinfos;
	
	/** The link object infos. */
	protected Map linkinfos;
	
	/** The ignored attribute types. */
	protected Set ignoredattrs;
	
	/** The comment method name used for setting comments. */
//	protected String comname;
	
	//-------- constructors --------

	/**
	 *  Create a new bean object handler.
	 */
	public OAVObjectHandler(Set typeinfos, Map linkinfos, Set ignoredattrs)//, String comname)
	{
		this.typeinfos = typeinfos!=null? typeinfos: Collections.EMPTY_SET;
		this.linkinfos = linkinfos!=null? linkinfos: Collections.EMPTY_MAP;
		this.ignoredattrs = ignoredattrs!=null? ignoredattrs: Collections.EMPTY_SET;

//		this.comname = comname;
	}
	
	//-------- methods --------
	
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @param comment The preceding xml comment.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(XMLStreamReader parser, String comment, Object context, List stack) throws Exception
	{
		Object ret = null;
		IOAVState state = (IOAVState)context;
		
		OAVMappingInfo mapinfo = null;//typeinfos.get(parser.getLocalName());
		
		if(mapinfo!=null)
		{
			OAVObjectType type = mapinfo.getType();
			
			if(type instanceof OAVJavaType && isBuiltInType(((OAVJavaType)type).getClazz()))
			{
				String strval;
				if(parser.getAttributeCount()==1)
				{	
					strval = parser.getAttributeValue(0);
				}
				else 
				{	
					strval = parser.getElementText();
				}
				ret = convertBuiltInTypes(((OAVJavaType)type).getClazz(), strval);
			}
			else
			{
				boolean root = stack.size()==0;
				
				if(root)
				{
					ret = state.createRootObject(type);
				}
				else
				{
					ret = state.createObject(type);
				}
				
				// Handle attributes
				for(int i=0; i<parser.getAttributeCount(); i++)
				{
					String attrname = parser.getAttributeLocalName(i);
					
					if(!ignoredattrs.contains(attrname))
					{
						String attrval = parser.getAttributeValue(i);
						
						OAVAttributeType attrtype = null;
						OAVObjectType tmptype = type;
						while(attrtype==null && tmptype!=null)
						{
							String tmpnamesin = tmptype.getName()+"_has_"+attrname;
							
							attrtype = tmptype.getDeclaredAttributeType0(tmpnamesin);
							
							if(attrtype==null)
								tmptype = tmptype.getSupertype();
						}
						
						if(attrtype!=null)
						{
							Object arg = attrtype.getObjectType() instanceof OAVJavaType?
								convertBuiltInTypes(((OAVJavaType)attrtype.getObjectType()).getClazz(), attrval): attrval;
					
							if(attrtype.getMultiplicity().equals(OAVAttributeType.NONE))
							{
								state.setAttributeValue(ret, attrtype, arg);
							}
							else
							{
								state.addAttributeValue(ret, attrtype, arg);
							}
						}
						else
						{
							System.out.println("Could not find attribute: "+attrtype);
						}
					}
				}
				
				// If comment method name is set, set the comment.
//				if(comname!=null)
//				{
//					Method m = SReflect.getMethod(type, comname, new Class[]{String.class});
//					if(m!=null)
//						m.invoke(ret, new Object[]{comment});
//				}
			}
		}
		else
		{
			System.out.println("No mapping found: "+parser.getLocalName());
		}
		
		return ret;
	}
	
	/**
	 *  Link an object to its parent.
	 *  @param parser The parser.
	 *  @param elem The element.
	 *  @param paranet The parent element.
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent, Object context, List stack) throws Exception
	{
		IOAVState state = (IOAVState)context;
		
		// Find attribute where to set/add the child element.
		
		boolean set = false;
		
		OAVAttributeType attrtype = (OAVAttributeType)linkinfos.get(((Object[])stack.get(stack.size()-1))[2]);

		if(attrtype!=null)
		{
			setAttributeValue(state, parent, attrtype, elem);
			set= true;
		}
		
		if(!set)
		{
			set = internalLinkObjects(parser.getLocalName(), elem, parent, state);
		}
		
		if(!set && !(state.getType(elem) instanceof OAVJavaType 
			&& isBuiltInType(((OAVJavaType)state.getType(elem)).getClazz())))
		{
			set = internalLinkObjects(state.getType(elem).getName(), elem, parent, state);	
		}	
	}
	
	/**
	 * 
	 */
	protected boolean internalLinkObjects(String attrname, Object elem, Object parent, IOAVState state)
	{
		boolean ret = false;
		OAVAttributeType attrtype = null;
		OAVObjectType tmptype = state.getType(parent);
		
		String attrnameplu = attrname.endsWith("y")? attrname.substring(0, attrname.length()-1)+"ies": attrname+"s"; 
		
		System.out.println("test: "+attrnameplu);
		
		while(attrtype==null && tmptype!=null)
		{
			String tmpnamesin = tmptype.getName()+"_has_"+attrname;
			String tmpnameplu = tmptype.getName()+"_has_"+attrnameplu;
			
			attrtype = tmptype.getDeclaredAttributeType0(tmpnamesin);
			if(attrtype==null)
				attrtype = tmptype.getDeclaredAttributeType0(tmpnameplu);
			
			if(attrtype==null)
				tmptype = tmptype.getSupertype();
		}
		
		if(attrtype!=null)
		{
			setAttributeValue(state, parent, attrtype, elem);
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void setAttributeValue(IOAVState state, Object parent, OAVAttributeType attrtype, Object elem)
	{
		if(attrtype.getMultiplicity().equals(OAVAttributeType.NONE))
		{
			state.setAttributeValue(parent, attrtype, elem);
		}
		else
		{
			try
			{
				state.addAttributeValue(parent, attrtype, elem);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Convert a string value to a built-in type.
	 *  @param clazz The target clazz.
	 *  @param val The string valut to convert.
	 */
	protected Object convertBuiltInTypes(Class clazz, String val)
	{
		Object ret;
		
		if(clazz.equals(String.class))
		{
			ret = val;
		}
		else if(clazz.equals(int.class) || clazz.equals(Integer.class))
		{
			ret = new Integer(val);
		}
		else if(clazz.equals(long.class) || clazz.equals(Long.class))
		{
			ret = new Long(val);
		}
		else if(clazz.equals(float.class) || clazz.equals(Float.class))
		{
			ret = new Float(val);
		}
		else if(clazz.equals(double.class) || clazz.equals(Double.class))
		{
			ret = new Double(val);
		}
		else if(clazz.equals(boolean.class) || clazz.equals(Boolean.class))
		{
			ret = new Boolean(val);
		}
		else if(clazz.equals(short.class) || clazz.equals(Short.class))
		{
			ret = new Short(val);
		}
		else if(clazz.equals(byte.class) || clazz.equals(Byte.class))
		{
			ret = new Byte(val);
		}
		else if(clazz.equals(char.class) || clazz.equals(Character.class))
		{
			ret = new Character(val.charAt(0)); // ?
		}
		else
		{
			throw new RuntimeException("Unknown argument type: "+clazz);
		}
		
		return ret;
	}
	
	/**
	 *  Test if a clazz is a built-in type.
	 *  @param clazz The clazz.
	 *  @return True, if built-in type.
	 */
	protected boolean isBuiltInType(Class clazz)
	{
		return builtintypes.contains(clazz);
	}

}