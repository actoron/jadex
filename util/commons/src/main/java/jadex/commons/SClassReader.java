package jadex.commons;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	 *  Gets the annotation infos of a class file for the class.
	 * 
	 *  @param inputstream The input stream of the class file. 
	 *  @return The annotations of the class.
	 */
	public static final List<AnnotationInfos> getAnnotationInfos(InputStream inputstream)
	{
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(inputstream, 16384));
			if (0xCAFEBABE != is.readInt())
				throw new IllegalArgumentException("Not a class file.");
			
			skip(is, 4);
			
			Map<Integer, byte[]> strings = readConstantPoolStrings(is);
			
			skip(is, 6);
			
			int ifacecount = is.readUnsignedShort();
			
			skip(is, ifacecount << 1);
			
			skipFieldsOrMethods(is);
			
			skipFieldsOrMethods(is);
			
			return readVisibleAnnotations(is, strings);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(inputstream);
		}
	}
	
	/**
	 *  Gets the annotation infos of a class file for the class.
	 * 
	 *  @param inputstream The input stream of the class file. 
	 *  @return The annotations of the class.
	 */
	public static final boolean hasTopLevelAnnotation(InputStream inputstream, String annotation)
	{
		annotation = "L" + annotation.replace('.', '/') + ";";
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(inputstream, 16384));
			if (0xCAFEBABE != is.readInt())
				throw new IllegalArgumentException("Not a class file.");
			
			skip(is, 4);
			
			Map<Integer, byte[]> strings = readConstantPoolStrings(is);
			
			skip(is, 6);
			
			int ifacecount = is.readUnsignedShort();
			
			skip(is, ifacecount << 1);
			
			skipFieldsOrMethods(is);
			
			skipFieldsOrMethods(is);
			
			int ac = is.readUnsignedShort();
			for (int i = 0; i < ac; ++i)
			{
				int nameref = is.readUnsignedShort();
				if ("RuntimeVisibleAnnotations".equals(decodeModifiedUtf8(strings.get(nameref))))
				{
					skip(is, 4);
					
					int anocount = is.readUnsignedShort();
					for (int j = 0; j < anocount; ++j)
					{
						int typeref = is.readUnsignedShort();
						int paircount = is.readUnsignedShort();
						
						String type = decodeModifiedUtf8(strings.get(typeref));
//						if (type != null)
//							type = type.substring(1, type.length() - 1).replace('/', '.');
						
						if (annotation.equals(type))
							return true;
						
						for (int k = 0; k < paircount; ++k)
						{
							skip(is, 2);
							readAnnotationValue(is, strings);
						}
					}
					
					break;
				}
				else
				{
					int len = is.readInt();
					skip(is, len);
				}
			}
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(inputstream);
		}
		return false;
	}
	
	/**
	 *  Gets the annotation infos of a class file for the class.
	 * 
	 *  @param inputstream The input stream of the class file. 
	 *  @return The annotations of the class.
	 */
	public static final Tuple2<Boolean, String> hasTopLevelAnnotationWithClassName(InputStream inputstream, String annotation)
	{
		String classname = null;
		annotation = "L" + annotation.replace('.', '/') + ";";
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(inputstream, 16384));
			if (0xCAFEBABE != is.readInt())
				throw new IllegalArgumentException("Not a class file.");
			
			skip(is, 4);
			
			Map<Integer, byte[]> strings = readConstantPoolStrings(is);
			
			skip(is, 2);
			
			int classnameindex = is.readUnsignedShort();
			try
			{
				classname = decodeModifiedUtf8(strings.get(SUtil.bytesToShort(strings.get(classnameindex), 0) & 0xFFFF));
				classname = classname.replace('/', '.');
			}
			catch (Exception e)
			{
			}
			
			skip(is, 2);
			
			int ifacecount = is.readUnsignedShort();
			
			skip(is, ifacecount << 1);
			
			skipFieldsOrMethods(is);
			
			skipFieldsOrMethods(is);
			
			int ac = is.readUnsignedShort();
			for (int i = 0; i < ac; ++i)
			{
				int nameref = is.readUnsignedShort();
				if ("RuntimeVisibleAnnotations".equals(decodeModifiedUtf8(strings.get(nameref))))
				{
					skip(is, 4);
					
					int anocount = is.readUnsignedShort();
					for (int j = 0; j < anocount; ++j)
					{
						int typeref = is.readUnsignedShort();
						int paircount = is.readUnsignedShort();
						
						String type = decodeModifiedUtf8(strings.get(typeref));
//						if (type != null)
//							type = type.substring(1, type.length() - 1).replace('/', '.');
						
						if (annotation.equals(type))
							return new Tuple2<Boolean, String>(true, classname);
						
						for (int k = 0; k < paircount; ++k)
						{
							skip(is, 2);
							readAnnotationValue(is, strings);
						}
					}
					
					break;
				}
				else
				{
					int len = is.readInt();
					skip(is, len);
				}
			}
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(inputstream);
		}
		return new Tuple2<Boolean, String>(false, classname);
	}
	
	/**
	 *  Get infos about a class.
	 * 
	 *  @param inputstream The input stream of the class file. 
	 *  @return The class infos.
	 */
	public static final ClassInfo getClassInfo(InputStream inputstream)
	{
		ClassInfo ret = new ClassInfo();
		
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(inputstream, 16384));
			if (0xCAFEBABE != is.readInt())
				throw new IllegalArgumentException("Not a class file.");
			
			skip(is, 4);
			
			Map<Integer, byte[]> strings = readConstantPoolStrings(is);
			
			skip(is, 2);
			
			int classnameindex = is.readUnsignedShort();
			try
			{
				String classname = decodeModifiedUtf8(strings.get(SUtil.bytesToShort(strings.get(classnameindex), 0) & 0xFFFF));
				classname = classname.replace('/', '.');
				ret.setClassname(classname);
			}
			catch (Exception e)
			{
			}
			
			skip(is, 2);
			
			int ifacecount = is.readUnsignedShort();
			
			skip(is, ifacecount << 1);
			
			skipFieldsOrMethods(is);
			
			skipFieldsOrMethods(is);
			
			List<AnnotationInfos> annos = readVisibleAnnotations(is, strings);
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
	            	buf = new byte[2];
	            	int clen = 2;
	            	int cread = 0;
	    			while (cread < clen)
		    			cread += is.read(buf, cread, clen - cread);
	    			ret.put(i, buf);
	    			break;
	            case 8:
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
			//skip(is, 2);
			int index = is.readUnsignedShort();
			int len = is.readInt();
			skip(is, len);
		}
	}
	
	protected static final List<AnnotationInfos> readVisibleAnnotations(DataInputStream is, Map<Integer, byte[]> strings) throws IOException
	{
		List<AnnotationInfos> ret = null;
		int ac = is.readUnsignedShort();
		for (int i = 0; i < ac; ++i)
		{
			int nameref = is.readUnsignedShort();
			if ("RuntimeVisibleAnnotations".equals(decodeModifiedUtf8(strings.get(nameref))))
			{
				skip(is, 4);
				ret = readAnnotations(is, strings);
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
	
	protected static final List<AnnotationInfos> readAnnotations(DataInputStream is, Map<Integer, byte[]> strings) throws IOException
	{
		List<AnnotationInfos> ret = new ArrayList<>();
		int anocount = is.readUnsignedShort();
		for (int i = 0; i < anocount; ++i)
		{
			ret.add(readAnnotation(is, strings));
		}
		return ret;
	}
	
	protected final static AnnotationInfos readAnnotation(DataInputStream is,  Map<Integer, byte[]> strings) throws IOException
	{
		int typeref = is.readUnsignedShort();
		int paircount = is.readUnsignedShort();
		
		String type = decodeModifiedUtf8(strings.get(typeref));
//		if (type == null)
//			continue;
		type = type.substring(1, type.length() - 1).replace('/', '.');
		AnnotationInfos ret = new AnnotationInfos(type);
		
		for (int i = 0; i < paircount; ++i)
		{
			int nameind = is.readUnsignedShort();
			String name = decodeModifiedUtf8(strings.get(nameind));
			if (name != null)
			{
				Object value = readAnnotationValue(is, strings);
				if (value instanceof AnnotationInfos)
				{
					ret.addNestedAnnotations(name, (AnnotationInfos) value);
				}
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
	        case 's':
	        	skip(is, 2);
	        	break;
	        case 'e':
	            skip(is, 4);
	            break;
	        case '@':
	            ret = readAnnotation(is, strings);
	            break;
	        case '[':
				int count = is.readUnsignedShort();
				ret = new Object[count];
	            for (int i = 0; i < count; ++i)
	            	((Object[]) ret)[i] = readAnnotationValue(is, strings);
	            break;
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
    
    protected static final void skip(DataInputStream is, int len) throws IOException
    {
    	while (len > 0)
    		len -= is.skip(len);
    }
    
    /**
     *  Class for infos about a class.
     */
    public static class ClassInfo
    {
    	/** The class name. */
    	protected String classname;
    	
    	/** The annotations. */
    	protected Collection<AnnotationInfos> annotations;

    	/**
    	 *  Create a new classinfo.
    	 */
		public ClassInfo()
		{
		}
    	
    	/**
    	 *  Create a new classinfo.
    	 */
		public ClassInfo(String classname, Collection<AnnotationInfos> annotations)
		{
			this.classname = classname;
			this.annotations = annotations;
		}

		/**
		 *  Get the classname.
		 *  @return the classname.
		 */
		public String getClassname()
		{
			return classname;
		}

		/**
		 *  Set the classname.
		 *  @param classname the classname to set
		 */
		public void setClassname(String classname)
		{
			this.classname = classname;
		}

		/**
		 *  Get the annotations.
		 *  @return the annotations
		 */
		public Collection<AnnotationInfos> getAnnotations()
		{
			return annotations;
		}

		/**
		 *  Set the annotations.
		 *  @param annotations the annotations to set
		 */
		public void setAnnotations(Collection<AnnotationInfos> annotations)
		{
			this.annotations = annotations;
		}
    }
    
    /**
     *  Class containing annotation infos.
     */
    public static class AnnotationInfos
    {
    	/** Fully qualified type. */
    	protected String type;
    	
    	/** Annotations nested in this annotation. */
    	Map<String, AnnotationInfos> nestedannotations;
    	
    	/**
    	 *  Creates the info.
    	 *  
    	 *  @param type Annotation type.
    	 */
    	public AnnotationInfos(String type)
		{
    		this.type = type;
		}
    	
    	public String getType()
    	{
    		return type;
    	}
    	
    	/**
    	 *  Returns the nested annotations.
    	 *  
    	 *  @return The nested annotations.
    	 */
    	public Map<String, AnnotationInfos> getNestedannotations()
		{
			return nestedannotations;
		}
    	
    	/**
    	 *  Adds a nested annotation.
    	 *  
    	 *  @param nestedannotation The nested annotation. 
    	 */
    	protected void addNestedAnnotations(String name, AnnotationInfos nestedannotation)
    	{
    		if (nestedannotations == null)
    			nestedannotations = new HashMap<>();
    		nestedannotations.put(name, nestedannotation);
    	}
    }
}
