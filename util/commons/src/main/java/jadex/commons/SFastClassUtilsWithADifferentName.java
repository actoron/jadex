package jadex.commons;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Class using the internal fast class path scanner to provide
 *  some utility methods for inspecting raw binary classes.
 *
 */
public class SFastClassUtilsWithADifferentName
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
			if (0xCAFEBABE != readInt(is))
				throw new IllegalArgumentException("Not a class file.");
			
			is.skip(4);
			
			Map<Integer, String> strings = readConstantPoolStrings(is);
			
			is.skip(6);
			
			int ifacecount = readShort(is);
			
			is.skip(ifacecount << 1);
			
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
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(inputstream, 16384));
			if (0xCAFEBABE != readInt(is))
				throw new IllegalArgumentException("Not a class file.");
			
			is.skip(4);
			
			Map<Integer, String> strings = readConstantPoolStrings(is);
			
			is.skip(6);
			
			int ifacecount = readShort(is);
			
			is.skip(ifacecount << 1);
			
			skipFieldsOrMethods(is);
			
			skipFieldsOrMethods(is);
			
			int ac = readShort(is);
			for (int i = 0; i < ac; ++i)
			{
				int nameref = readShort(is);
				if ("RuntimeVisibleAnnotations".equals(strings.get(nameref)))
				{
					is.skip(4);
					
					int anocount = readShort(is);
					for (int j = 0; j < anocount; ++j)
					{
						int typeref = readShort(is);
						int paircount = readShort(is);
						
						String type = strings.get(typeref);
						if (type != null)
							type = type.substring(1, type.length() - 1).replace('/', '.');
						
						if (annotation.equals(type))
							return true;
						
						for (int k = 0; k < paircount; ++k)
						{
							is.skip(2);
							readAnnotationValue(is, strings);
						}
					}
					
					break;
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
	 *  Reads strings from the constant pool.
	 * 
	 *  @param is The input stream.
	 *  @return The constant pool strings.
	 */
	protected static final Map<Integer, String> readConstantPoolStrings(DataInputStream is) throws IOException
	{
		Map<Integer, String> ret = new HashMap<>();
		int cpcount = readShort(is);
		for (int i = 1; i < cpcount; ++i)
		{
            byte tag = (byte) is.read();
            switch (tag)
            {
	            case 1:
	                //int len = readUnsignedShort(is);
	                
	                //is.skip(len);
	                //ret.put(i, "dummy");
	            	ret.put(i, is.readUTF());
	                break;
	            case 3:
	            case 4:
	            case 9:
	            case 10:
	            case 11:
	            case 12:
	            case 18:
	                is.skip(4);
	                break;
	            case 5:
	            case 6:
	            	is.skip(8);
	                i++;
	                break;
	            case 7:
	            case 8:
	            case 16:
	            case 19:
	            case 20:
	            	is.skip(2);
	                break;
	            case 15:
	                is.skip(3);
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
	protected static final void skipFieldsOrMethods(InputStream is) throws IOException
	{
		int fcount = readShort(is);
		
		for (int i = 0; i < fcount; ++i)
		{
			is.skip(6);
			skipAttributes(is);
		}
	}
	
	protected static final void skipAttributes(InputStream is) throws IOException
	{
		int ac = readShort(is);
		for (int i = 0; i < ac; ++i)
		{
			is.skip(2);
			int len = readInt(is);
			is.skip(len);
		}
	}
	
	protected static final List<AnnotationInfos> readVisibleAnnotations(DataInputStream is, Map<Integer, String> strings) throws IOException
	{
		List<AnnotationInfos> ret = null;
		int ac = readShort(is);
		for (int i = 0; i < ac; ++i)
		{
			int nameref = readShort(is);
			if ("RuntimeVisibleAnnotations".equals(strings.get(nameref)))
			{
				is.skip(4);
				ret = readAnnotations(is, strings);
				break;
			}
			else
			{
//				System.out.println("Skipping " + strings.get(nameref) + " " + i);
				int len = readInt(is);
				is.skip(len);
			}
		}
		return ret;
	}
	
	protected static final List<AnnotationInfos> readAnnotations(DataInputStream is, Map<Integer, String> strings) throws IOException
	{
		List<AnnotationInfos> ret = new ArrayList<>();
		int anocount = readShort(is);
		for (int i = 0; i < anocount; ++i)
		{
			ret.add(readAnnotation(is, strings));
		}
		return ret;
	}
	
	protected final static AnnotationInfos readAnnotation(DataInputStream is,  Map<Integer, String> strings) throws IOException
	{
		int typeref = readShort(is);
		int paircount = readShort(is);
		
		String type = strings.get(typeref);
//		if (type == null)
//			continue;
		type = type.substring(1, type.length() - 1).replace('/', '.');
		AnnotationInfos ret = new AnnotationInfos(type);
		
		for (int i = 0; i < paircount; ++i)
		{
			int nameind = readShort(is);
			String name = strings.get(nameind);
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
    protected static final  Object readAnnotationValue(DataInputStream is, Map<Integer, String> strings) throws IOException
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
	        	is.skip(2);
	        	break;
	        case 'e':
	            is.skip(4);
	            break;
	        case '@':
	            ret = readAnnotation(is, strings);
	            break;
	        case '[':
				int count = readShort(is);
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
     *  Class containing annotation infos.
     *
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
    
    /**
     *  Reads an integer from the stream.
     * 
     *  @param is The stream.
     *  @return The integer.
     */
    protected static final int readInt(InputStream is) throws IOException
    {
    	int ret = is.read() & 0xFF;
    	ret <<= 8;
    	ret |= is.read() & 0xFF;
    	ret <<= 8;
    	ret |= is.read() & 0xFF;
    	ret <<= 8;
    	ret |= is.read() & 0xFF;
    	return ret;
    }
    
    protected static final int readShort(InputStream is) throws IOException
    {
    	int ret = is.read() & 0xFF;
    	ret <<= 8;
    	ret |= is.read() & 0xFF;
    	return ret;
    }
}
