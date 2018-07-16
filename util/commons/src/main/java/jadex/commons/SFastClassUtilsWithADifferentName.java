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
	 *  @param origis The input stream with the class file data.
	 *  @param toponly If true, skips checking for nested annotations.
	 *  @return Annotation infos for the class.
	 */
	public static final List<AnnotationInfos> getAnnotationInfos(InputStream origis, boolean toponly)
	{
		try
		{
			DataInputStream is = new DataInputStream(new BufferedInputStream(origis, 16384));
			if (0xCAFEBABE != is.readInt())
				throw new IllegalArgumentException("Not a class file.");
			
			is.skip(4);
			
			Map<Integer, String> strings = readConstantPoolStrings(is);
			
			is.skip(6);
			
			int ifacecount = is.readUnsignedShort();
			
			is.skip(ifacecount << 1);
			
			skipFieldsOrMethods(is, strings);
			
			skipFieldsOrMethods(is, strings);
			
//			SUtil.readStream(buf, 0, 2, is);
			
			return readVisibleAnnotations(is, strings);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(origis);
		}
	}
	
	protected static final Map<Integer, String> readConstantPoolStrings(DataInputStream is) throws IOException
	{
		Map<Integer, String> ret = new HashMap<>();
		int cpcount = is.readUnsignedShort();
		for (int i = 1; i < cpcount; ++i)
		{
            byte tag = (byte) is.read();
            switch (tag)
            {
	            case 1:
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
	
	protected static final void skipFieldsOrMethods(DataInputStream is, Map<Integer, String> strings) throws IOException
	{
		int fcount = is.readUnsignedShort();
		
		for (int i = 0; i < fcount; ++i)
		{
			is.skip(6);
			skipAttributes(is);
		}
	}
	
	protected static final void skipAttributes(DataInputStream is) throws IOException
	{
		int ac = is.readUnsignedShort();
		for (int i = 0; i < ac; ++i)
		{
			is.skip(2);
			int len = is.readInt();
			is.skip(len);
		}
	}
	
	protected static final List<AnnotationInfos> readVisibleAnnotations(DataInputStream is, Map<Integer, String> strings) throws IOException
	{
		List<AnnotationInfos> ret = null;
		int ac = is.readUnsignedShort();
		for (int i = 0; i < ac; ++i)
		{
			int nameref = is.readUnsignedShort();
			if ("RuntimeVisibleAnnotations".equals(strings.get(nameref)))
			{
				is.skip(4);
				ret = readAnnotations(is, strings);
				break;
			}
			else
			{
//				System.out.println("Skipping " + strings.get(nameref) + " " + i);
				int len = is.readInt();
				is.skip(len);
			}
		}
		return ret;
	}
	
	public static final List<AnnotationInfos> readAnnotations(DataInputStream is, Map<Integer, String> strings) throws IOException
	{
		List<AnnotationInfos> ret = new ArrayList<>();
		int anocount = is.readUnsignedShort();
		for (int i = 0; i < anocount; ++i)
		{
			ret.add(readAnnotation(is, strings));
		}
		return ret;
	}
	
	protected final static AnnotationInfos readAnnotation(DataInputStream is,  Map<Integer, String> strings) throws IOException
	{
		int typeref = is.readUnsignedShort();
		int paircount = is.readUnsignedShort();
		
		String type = strings.get(typeref);
//		if (type == null)
//			continue;
		type = type.substring(1, type.length() - 1).replace('/', '.');
		AnnotationInfos ret = new AnnotationInfos(type);
		
		for (int i = 0; i < paircount; ++i)
		{
			int nameind = is.readUnsignedShort();
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
    private static final  Object readAnnotationValue(DataInputStream is, Map<Integer, String> strings) throws IOException
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
}
