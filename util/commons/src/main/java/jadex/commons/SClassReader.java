package jadex.commons;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Class using the internal fast class path scanner to provide
 *  some utility methods for inspecting raw binary classes.
 *
 */
public class SClassReader
{
	/**
	 *  Get infos about a class.
	 * 
	 *  @param inputstream The input stream of the class file. 
	 *  @return The class infos.
	 */
	public static final ClassInfo getClassInfo(InputStream inputstream)
	{
		return getClassInfo(inputstream, false, false);
	}
	
	/**
	 *  Get infos about a class.
	 * 
	 *  @param inputstream The input stream of the class file. 
	 *  @return The class infos.
	 */
	public static final ClassInfo getClassInfo(InputStream inputstream, boolean includefields, boolean includemethods)
	{
		ClassInfo ret = new ClassInfo();
		
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(inputstream, 16384));
			if (0xCAFEBABE != is.readInt())
				throw new IllegalArgumentException("Not a class file.");
			
			skip(is, 4);
			
			Map<Integer, byte[]> strings = readConstantPoolStrings(is);
			
			ret.setAccessFlags(is.readUnsignedShort());
			
			int classnameindex = is.readUnsignedShort();
			try
			{
				String classname = decodeModifiedUtf8(strings.get(SUtil.bytesToShort(strings.get(classnameindex), 0) & 0xFFFF));
				classname = classname.replace('/', '.');
				ret.setClassName(classname);
			}
			catch (Exception e)
			{
			}
			
			skip(is, 2);
			
			int ifacecount = is.readUnsignedShort();
			
			skip(is, ifacecount << 1);
			
			if (includefields)
				ret.setFieldInfos(readFields(is, strings));
			else
				skipFieldsOrMethods(is);
			
			if (includemethods)
				ret.setMethodInfos(readMethods(is, strings));
			else
				skipFieldsOrMethods(is);
			
			List<AnnotationInfo> annos = readVisibleAnnotations(is, strings, true);
			ret.setAnnotations(annos);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(inputstream);
		}
		
		return ret;
	}
	
	/**
	 *  Reads strings from the constant pool.
	 * 
	 *  @param is The input stream.
	 *  @return The constant pool strings.
	 */
	protected static final Map<Integer, byte[]> readConstantPoolStrings(DataInputStream is) throws IOException
	{
		Map<Integer, byte[]> ret = new HashMap<>();
		int cpcount = is.readUnsignedShort();
		for (int i = 1; i < cpcount; ++i)
		{
            byte tag = (byte) is.read();
            switch (tag)
            {
	            case 1:
	                int len = is.readUnsignedShort();
	                byte[] buf = new byte[2 + len];
	                SUtil.shortIntoBytes(len, buf, 0);
	                int off = 2;
	                int read = 0;
	    			while (read < len)
	    			{
	    				read = is.read(buf, off, len - read);
	    				off += read;
	    			}
	    			ret.put(i, buf);
	                
	                //skip(is, len);
	                //ret.put(i, "dummy");
	            	
//	            	ret.put(i, is.readUTF());
	                break;
	            case 3:
	            case 4:
	            case 9:
	            case 10:
	            case 11:
	            case 12:
	            case 18:
	                skip(is, 4);
	                break;
	            case 5:
	            case 6:
	            	skip(is, 8);
	                i++;
	                break;
	            case 7:
	            case 8:
	            	buf = new byte[2];
	            	int clen = 2;
	            	int cread = 0;
	    			while (cread < clen)
		    			cread += is.read(buf, cread, clen - cread);
	    			ret.put(i, buf);
	    			break;
	            case 16:
	            case 19:
	            case 20:
	            	skip(is, 2);
	                break;
	            case 15:
	                skip(is, 3);
	                break;
	            default:
	                throw new RuntimeException("Unknown constant pool tag: " + tag);
            }
        }
		return ret;
	}
	
	/**
	 *  Skips the field or method section of the class file.
	 *  
	 *  @param is The input stream.
	 *  @param strings
	 * @throws IOException
	 */
	protected static final void skipFieldsOrMethods(DataInputStream is) throws IOException
	{
		int fcount = is.readUnsignedShort();
		
		for (int i = 0; i < fcount; ++i)
		{
			skip(is, 6);
			skipAttributes(is);
		}
	}
	
	protected static final void skipAttributes(DataInputStream is) throws IOException
	{
		int ac = is.readUnsignedShort();
		for (int i = 0; i < ac; ++i)
		{
			skip(is, 2);
//			int index = is.readUnsignedShort();
			int len = is.readInt();
			skip(is, len);
		}
	}
	
	/**
	 *  Reads the class fields.
	 *  
	 *  @param is Inputstream.
	 *  @param strings Strings from constant table.
	 *  @return Fields.
	 */
	protected static final List<FieldInfo> readFields(DataInputStream is, Map<Integer, byte[]> strings) throws IOException
	{
		List<FieldInfo> ret = new ArrayList<>();
		int mcount = is.readUnsignedShort();
		for (int i = 0; i < mcount; ++i)
		{
			FieldInfo fi = new FieldInfo();
			fi.setAccessFlags(is.readUnsignedShort());
			
			int strind = is.readUnsignedShort();
			byte[] rawstr = strings.get(strind);
			fi.setFieldName(decodeModifiedUtf8(rawstr));
			
			strind = is.readUnsignedShort();
			rawstr = strings.get(strind);
			fi.setFieldDescriptor(decodeModifiedUtf8(rawstr));
			
			fi.setAnnotations(readVisibleAnnotations(is, strings, false));
			
			ret.add(fi);
		}
		return ret;
	}
	
	/**
	 *  Reads the class methods.
	 *  
	 *  @param is Inputstream.
	 *  @param strings Strings from constant table.
	 *  @return Methods.
	 */
	protected static final List<MethodInfo> readMethods(DataInputStream is, Map<Integer, byte[]> strings) throws IOException
	{
		List<MethodInfo> ret = new ArrayList<>();
		int mcount = is.readUnsignedShort();
		for (int i = 0; i < mcount; ++i)
		{
			MethodInfo mi = new MethodInfo();
			mi.setAccessFlags(is.readUnsignedShort());
			
			int strind = is.readUnsignedShort();
			byte[] rawstr = strings.get(strind);
			mi.setMethodName(decodeModifiedUtf8(rawstr));
			
			strind = is.readUnsignedShort();
			rawstr = strings.get(strind);
			mi.setMethodDescriptor(decodeModifiedUtf8(rawstr));
			
			mi.setAnnotations(readVisibleAnnotations(is, strings, false));
			
			ret.add(mi);
		}
		return ret;
	}
	
	/**
	 *  Read runtime visible annotations.
	 */
	protected static final List<AnnotationInfo> readVisibleAnnotations(DataInputStream is, Map<Integer, byte[]> strings, boolean cancelread) throws IOException
	{
		List<AnnotationInfo> ret = null;
		int ac = is.readUnsignedShort();
		for (int i = 0; i < ac; ++i)
		{
			int nameref = is.readUnsignedShort();
			if ("RuntimeVisibleAnnotations".equals(decodeModifiedUtf8(strings.get(nameref))))
			{
				skip(is, 4);
				ret = readAnnotations(is, strings);
				if (cancelread)
					break;
			}
			else
			{
				int len = is.readInt();
				skip(is, len);
			}
		}
		return ret;
	}
	
	/**
	 *  Read a set of annotations.
	 */
	protected static final List<AnnotationInfo> readAnnotations(DataInputStream is, Map<Integer, byte[]> strings) throws IOException
	{
		List<AnnotationInfo> ret = new ArrayList<>();
		int anocount = is.readUnsignedShort();
		for (int i = 0; i < anocount; ++i)
		{
			ret.add(readAnnotation(is, strings));
		}
		return ret;
	}
	
	/**
	 *  Read a specific annotation.
	 */
	protected final static AnnotationInfo readAnnotation(DataInputStream is,  Map<Integer, byte[]> strings) throws IOException
	{
		int typeref = is.readUnsignedShort();
		int paircount = is.readUnsignedShort();
		
		String type = decodeModifiedUtf8(strings.get(typeref));
//		if (type == null)
//			continue;
		type = convertTypeName(type);
		AnnotationInfo ret = new AnnotationInfo(type);
		
		for (int i = 0; i < paircount; ++i)
		{
			int nameind = is.readUnsignedShort();
			String name = decodeModifiedUtf8(strings.get(nameind));
			if (name != null)
			{
				Object value = readAnnotationValue(is, strings);
				
				if (value != null)
					ret.addValue(name, value);
				
//				if (value instanceof AnnotationInfos)
//				{
//					ret.addNestedAnnotations(name, (AnnotationInfos) value);
//				}
//				else if (value instanceof String)
//				{
//					ret.addStringValue(name, (String) value);
//				}
//				else if (value instanceof Object[])
//				{
//					Object[] arr = (Object[]) value;
//					for (int i = 0; i < arr.length; ++i)
//					{
//						i
//					}
//				}
			}
		}
		return ret;
	}
	
    /** Read an annotation value. */
    protected static final Object readAnnotationValue(DataInputStream is, Map<Integer, byte[]> strings) throws IOException
    {
    	Object ret = null;
        int tag = is.read() & 0xFF;
        switch (tag)
        {
	        case 'B':
	        case 'C':
	        case 'D':
	        case 'F':
	        case 'I':
	        case 'J':
	        case 'S':
	        case 'Z':
	        case 'c':
	        {
	        	skip(is, 2);
	        	break;
	        }
	        case 's':
	        {
	        	int strind = is.readUnsignedShort();
	        	byte[] enc = strings.get(strind);
	        	if (enc != null)
	        	{
	        		enc = strings.get(strind);
	        		if (enc != null)
	        			ret = decodeModifiedUtf8(enc);
	        	}
	        	break;
	        }
	        case 'e':
	        {
	        	int ind = is.readUnsignedShort();
	        	byte[] enc = strings.get(ind);
	        	String enumtype = enc != null ? decodeModifiedUtf8(enc) : null;
	        	enumtype = convertTypeName(enumtype);
	        	ind = is.readUnsignedShort();
	        	enc = strings.get(ind);
	        	String enumval = enc != null ? decodeModifiedUtf8(enc) : null;
	        	ret = new EnumInfo(enumtype, enumval);
//	            skip(is, 4);
	            break;
	        }
	        case '@':
	        {
	            ret = readAnnotation(is, strings);
	            break;
	        }
	        case '[':
	        {
				int count = is.readUnsignedShort();
				ret = new Object[count];
	            for (int i = 0; i < count; ++i)
	            	((Object[]) ret)[i] = readAnnotationValue(is, strings);
	            break;
	        }
	        default:
	            throw new RuntimeException("Unknown Annotation tag: " + tag);
        }
        return ret;
    }
    
    /**
     *  Decodes a Java-style modified UTF8 string as used
     *  in class files.
     * 
     *  @param data The string data.
     *  @return The string.
     */
    protected static final String decodeModifiedUtf8(byte[] data)
    {
    	if (data == null)
    		return null;
    	try
    	{
    		return (new DataInputStream(new ByteArrayInputStream(data))).readUTF();
    	}
    	catch (Exception e)
    	{
    		throw SUtil.throwUnchecked(e);
    	}
    }
    
    /**
     *  Skips a number of bytes.
     *  
     *  @param is Inputstream.
     *  @param len number of bytes skipped.
     */
    protected static final void skip(DataInputStream is, int len) throws IOException
    {
    	while (len > 0)
    		len -= is.skip(len);
    }
    
    /**
     *  Converts a type name to Java style.
     *  @param type Internal name.
     *  @return Converted name.
     */
    protected static final String convertTypeName(String type)
    {
    	if (type == null)
    		return null;
    	return type.substring(1, type.length() - 1).replace('/', '.');
    }
    
    /**
     *  Entity with optional annotations 
     * @author jander
     *
     */
    public static class AnnotatedEntity
    {
    	/** The annotations. */
    	protected Collection<AnnotationInfo> annotations;
    	
    	/** Class access flags. */
    	protected int accessflags;
    	
    	/**
		 *  Get the annotations.
		 *  @return the annotations
		 */
		public Collection<AnnotationInfo> getAnnotations()
		{
			return annotations;
		}

		/**
		 *  Set the annotations.
		 *  @param annotations the annotations to set
		 */
		protected void setAnnotations(Collection<AnnotationInfo> annotations)
		{
			this.annotations = annotations;
		}
		
		/**
		 *  Get the access flags.
		 *  @return the access flags
		 */
		public int getAccessFlags()
		{
			return accessflags;
		}
		
		/**
		 *  Set the access flags.
		 *  @param accessflags the access flags to set
		 */
		protected void setAccessFlags(int accessflags)
		{
			this.accessflags = accessflags;
		}
		
		/**
		 *  Tests if entity is public.
		 *  @return True, if public.
		 */
		public boolean isPublic()
		{
			return (accessflags & 0x00000001) != 0;
		}
		
		/**
		 *  Tests if entity is final.
		 *  @return True, if final.
		 */
		public boolean isFinal()
		{
			return (accessflags & 0x00000010) != 0;
		}
		
		/**
		 *  Tests if entity is synthetic.
		 *  @return True, if synthetic.
		 */
		public boolean isSynthetic()
		{
			return (accessflags & 0x00001000) != 0;
		}
		
		/**
		 *  Test if this entity has an annotation.
		 *  @param annname The annotation name.
		 */
		public boolean hasAnnotation(String anname)
		{
			boolean ret = false;
			if(annotations!=null)
			{
				for(AnnotationInfo ai: annotations)
				{
					if(anname.equals(ai.getType()))
					{
						ret = true;
						break;
					}
				}
			}
			return ret;
		}
		
		/**
		 *  Test if this entity has an annotation.
		 *  @param annname The annotation name.
		 */
		public AnnotationInfo getAnnotation(String anname)
		{
			AnnotationInfo ret = null;
			if(annotations!=null)
			{
				for(AnnotationInfo ai: annotations)
				{
					if(anname.equals(ai.getType()))
					{
						ret = ai;
						break;
					}
				}
			}
			return ret;
		}
    }
    
    /**
     *  Entity contained in a class.
     */
    public static class ClassEntity extends AnnotatedEntity
    {
    	/**
		 *  Tests if entity is private.
		 *  @return True, if private.
		 */
		public boolean isPrivate()
		{
			return (accessflags & 0x00000002) != 0;
		}
		
		/**
		 *  Tests if entity is protected.
		 *  @return True, if protected.
		 */
		public boolean isProtected()
		{
			return (accessflags & 0x00000004) != 0;
		}
		
		/**
		 *  Tests if entity is static.
		 *  @return True, if static.
		 */
		public boolean isStatic()
		{
			return (accessflags & 0x00000008) != 0;
		}
    }
    
    /**
     *  Info object describing a field.
     *
     */
    public static class FieldInfo extends ClassEntity
    {
    	/** The field name. */
    	protected String fieldname;
    	
    	/** The field descriptor. */
    	protected String fielddesc;
    	
    	protected FieldInfo()
		{
		}
    	
    	/**
		 *  Get the field name.
		 *  @return the field name.
		 */
    	public String getFieldName()
		{
			return fieldname;
		}
    	
    	/**
		 *  Get the field descriptor.
		 *  @return the field descriptor.
		 */
    	public String getFieldDescriptor()
		{
			return fielddesc;
		}
    	
    	/**
		 *  Tests if field is volatile.
		 *  @return True, if volatile.
		 */
		public boolean isVolatile()
		{
			return (accessflags & 0x00000040) != 0;
		}
		
		/**
		 *  Tests if field is transient.
		 *  @return True, if transient.
		 */
		public boolean isTransient()
		{
			return (accessflags & 0x00000080) != 0;
		}
		
		/**
		 *  Tests if field is an enum.
		 *  @return True, if an enum.
		 */
		public boolean isEnum()
		{
			return (accessflags & 0x00004000) != 0;
		}
    	
    	/**
		 *  Set the field name.
		 *  @param fieldname the field name to set
		 */
    	protected void setFieldName(String fieldname)
		{
			this.fieldname = fieldname;
		}
    	
    	/**
		 *  Set the field descriptor.
		 *  @param fielddesc the field descriptor to set
		 */
    	protected void setFieldDescriptor(String fielddesc)
		{
			this.fielddesc = fielddesc;
		}
    }
    
    /**
     *  Info object describing a method.
     *
     */
    public static class MethodInfo extends ClassEntity
    {
    	/** The method name. */
    	protected String methodname;
    	
    	/** The method descriptor. */
    	protected String methoddesc;
    	
    	/**
    	 *  Create mew method info.
    	 */
    	protected MethodInfo()
		{
		}
    	
    	/**
		 *  Get the method name.
		 *  @return the method name.
		 */
    	public String getMethodName()
		{
			return methodname;
		}
    	
    	/**
		 *  Get the method descriptor.
		 *  @return the method descriptor.
		 */
    	public String getMethodDescriptor()
		{
			return methoddesc;
		}
    	
    	/**
		 *  Set the method name.
		 *  @param methodname the method name to set
		 */
    	protected void setMethodName(String methodname)
		{
			this.methodname = methodname;
		}
    	
    	/**
		 *  Set the method descriptor.
		 *  @param methoddesc the method descriptor to set
		 */
    	protected void setMethodDescriptor(String methoddesc)
		{
			this.methoddesc = methoddesc;
		}
    	
    	/**
		 *  Tests if method is synchronized.
		 *  @return True, if synchronized.
		 */
		public boolean isSynchronized()
		{
			return (accessflags & 0x00000020) != 0;
		}
		
		/**
		 *  Tests if method is a bridge method.
		 *  @return True, if bridge method.
		 */
		public boolean isBridge()
		{
			return (accessflags & 0x00000040) != 0;
		}
		
		/**
		 *  Tests if method is a varargs method.
		 *  @return True, if varargs method.
		 */
		public boolean isVarArgs()
		{
			return (accessflags & 0x00000080) != 0;
		}
		
		/**
		 *  Tests if method is a native method.
		 *  @return True, if native method.
		 */
		public boolean isNative()
		{
			return (accessflags & 0x00000100) != 0;
		}
		
		/**
		 *  Tests if method is abstract.
		 *  @return True, if abstract.
		 */
		public boolean isAbstract()
		{
			return (accessflags & 0x00000400) != 0;
		}
		
		/**
		 *  Tests if method is strict.
		 *  @return True, if strict.
		 */
		public boolean isStrict()
		{
			return (accessflags & 0x00000800) != 0;
		}
    }
    
    /**
     *  Class for infos about a class.
     */
    public static class ClassInfo extends AnnotatedEntity
    {
    	/** The class name. */
    	protected String classname;
    	
    	/** Field infos, if available. */
    	protected List<FieldInfo> fieldinfos;
    	
    	/** Method infos, if available. */
    	protected List<MethodInfo> methodinfos;
    	
    	/**
    	 *  Create a new classinfo.
    	 */
    	protected ClassInfo()
		{
		}
    	
    	/**
    	 *  Create a new classinfo.
    	 */
		public ClassInfo(String classname, Collection<AnnotationInfo> annotations)
		{
			this.classname = classname;
			this.annotations = annotations;
		}

		/**
		 *  Get the classname.
		 *  @return the classname.
		 */
		public String getClassName()
		{
			return classname;
		}
		
		/**
		 *  Get the field infos.
		 *  @return the field infos.
		 */
		public List<FieldInfo> getFieldInfos()
		{
			return fieldinfos;
		}
		
		/**
		 *  Get the method infos.
		 *  @return the method infos.
		 */
		public List<MethodInfo> getMethodInfos()
		{
			return methodinfos;
		}
		
		/**
		 *  Tests if class is an interface.
		 *  @return True, if an interface.
		 */
		public boolean isInterface()
		{
			return (accessflags & 0x00000200) != 0;
		}
		
		/**
		 *  Tests if class is an annotation.
		 *  @return True, if an annotation.
		 */
		public boolean isAnnotation()
		{
			return (accessflags & 0x00002000) != 0;
		}
		
		/**
		 *  Tests if class is an enum.
		 *  @return True, if an enum.
		 */
		public boolean isEnum()
		{
			return (accessflags & 0x00004000) != 0;
		}
		
		/**
		 *  Tests if class is abstract.
		 *  @return True, if abstract.
		 */
		public boolean isAbstract()
		{
			return (accessflags & 0x00000400) != 0;
		}
		
		/**
		 *  Set the class name.
		 *  @param classname the class name to set
		 */
		protected void setClassName(String classname)
		{
			this.classname = classname;
		}
		
		/**
		 *  Set the field infos.
		 *  @param fieldinfos the field infos to set
		 */
		protected void setFieldInfos(List<FieldInfo> fieldinfos)
		{
			this.fieldinfos = fieldinfos;
		}
		
		/**
		 *  Set the method infos.
		 *  @param methodinfos the method infos to set
		 */
		protected void setMethodInfos(List<MethodInfo> methodinfos)
		{
			this.methodinfos = methodinfos;
		}

		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return "ClassInfo [classname=" + classname + ", annotations=" + annotations + "]";
		}
    }
    
    /**
     *  Class containing annotation infos.
     */
    public static class AnnotationInfo
    {
    	/** Fully qualified type. */
    	protected String type;
    	
    	/** Annotations nested in this annotation. */
    	Map<String, Object> values;
    	
    	/**
    	 *  Creates the info.
    	 *  
    	 *  @param type Annotation type.
    	 */
    	protected AnnotationInfo(String type)
		{
    		this.type = type;
		}
    	
    	public String getType()
    	{
    		return type;
    	}
    	
    	/**
    	 *  Returns a contained value.
    	 *  
    	 *  @return The contained  value.
    	 */
    	public Object getValue(String name)
		{
    		return SUtil.notNull(values).get(name);
		}
    	
    	/**
    	 *  Returns the contained values.
    	 *  
    	 *  @return The contained values.
    	 */
    	public Map<String, Object> getValues()
		{
			return values;
		}
    	
    	/**
    	 *  Adds a value.
    	 *  @param name Name of the value.
    	 *  @param value The value.
    	 */
    	protected void addValue(String name, Object value)
    	{
    		if (values == null)
    			values = new HashMap<>();
    		values.put(name, value);
    	}

    	/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return "AnnotationInfos [type=" + type + ", values=" + values + "]";
		}
    }
    
    /**
     *  Info object for an enum.
     *
     */
    public static class EnumInfo
    {
    	/** The enum type. */
    	protected String type;
    	
    	/** The enum value. */
    	protected String value;
    	
    	/**
    	 *  Create enum info.
    	 */
    	protected EnumInfo(String type, String value)
		{
    		this.type = type;
    		this.value = value;
		}
    	
    	/**
    	 *  Gets the type.
    	 *  @return Enum type.
    	 */
		public String getType()
		{
			return type;
		}
		
		/**
		 *  Gets the enum value.
		 *  @return The value.
		 */
		public String getValue()
		{
			return value;
		}
    }
}
