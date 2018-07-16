package jadex.commons;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.lukehutch.fastclasspathscanner.scanner.AnnotationInfo;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanSpec;
import io.github.lukehutch.fastclasspathscanner.utils.LogNode;
import jadex.bytecode.vmhacks.VmHacks;
import jadex.javaparser.javaccimpl.ParseException;

/**
 *  Class using the internal fast class path scanner to provide
 *  some utility methods for inspecting raw binary classes.
 *
 */
public class SFastClassUtils
{
	/** Flag if class has lazily initialized. */
	protected volatile static boolean INITIALIZED = false;
	
	/** Constructor for ClassfileBinaryParser */
	protected static MethodHandle CLASSFILEBINARYPARSER_CON;
	
	/** Method readClassInfoFromClassfileHeader. */
	protected static MethodHandle READCLASSINFOFROMCLASSFILEHEADER;
	
	/** Field for the annotation infos. */
	protected static MethodHandle CLASSANNOTATIONS_FIELD;
	
	/**
	 *  Get the annotation infos for a class.
	 *  
	 *  @param filepath The file path to the class.
	 *  @param cl The classloader.
	 *  @return The annotation infos.
	 */
	public static List<AnnotationInfo> getAnnotationInfos(String filepath, ClassLoader cl)
	{
		initialize();
		
		List<AnnotationInfo> ret = null;
		InputStream is = null;
		try
		{
			ResourceInfo ri = SUtil.getResourceInfo0(filepath, cl);
//			ri = SUtil.getResourceInfo0(filepath, cl);
			String relpath = filepath.substring(0, filepath.length()-6).replace('.', '/')+".class";
			relpath = ".class";
			if (ri == null)
				throw new FileNotFoundException("Could not load file " + filepath + " from classloader " + cl);
			is = ri.getInputStream();
			
			ScanSpec spec = new ScanSpec(new String[0], null);
			
			Object cbp = CLASSFILEBINARYPARSER_CON.invokeExact();
			String[] dummclassname = new String[1];
			LogNode ln = new LogNode()
			{
				public LogNode log(String msg)
				{
					int ind = msg.indexOf(" is at incorrect relative path");
					if (ind > 0)
					{ 
						dummclassname[0] = msg.substring(0, ind).substring(6).replace('.', '/');
					}
					return this;
				}
			};
			Object ciu = READCLASSINFOFROMCLASSFILEHEADER.invoke(cbp, null, relpath, is, spec, ln);
			if (dummclassname[0] != null)
			{
				// Reload after we figured out the relpath length.
				relpath = dummclassname[0] + ".class";
				SUtil.close(is);
				ri = SUtil.getResourceInfo0(filepath, cl);
				is = ri.getInputStream();
				ciu = READCLASSINFOFROMCLASSFILEHEADER.invoke(cbp, null, relpath, is, spec, ln);
			}
			
			if (ciu == null)
				throw new ParseException("Could not parse class: " + filepath);
			
			ret = (List<AnnotationInfo>) CLASSANNOTATIONS_FIELD.invoke(ciu);
		}
		catch (Throwable e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			SUtil.close(is);
		}
		return ret;
	}
	
	/**
	 *  Lazy static initialization for the class.
	 */
	protected static void initialize()
	{
		if (INITIALIZED)
		{
			return;
		}
		
		synchronized (SFastClassUtils.class)
		{
			if (INITIALIZED)
			{
				return;
			}
			
			try
			{
				Lookup lookup = MethodHandles.lookup();
				Class<?> cbpclazz = Class.forName("io.github.lukehutch.fastclasspathscanner.scanner.ClassfileBinaryParser");
				Constructor<?> cbpcon = cbpclazz.getDeclaredConstructor();
				VmHacks.get().setAccessible(cbpcon, true);
				CLASSFILEBINARYPARSER_CON = lookup.unreflectConstructor(cbpcon).asType(MethodType.genericMethodType(0));
				
				Class<?> ceclazz = Class.forName("io.github.lukehutch.fastclasspathscanner.scanner.ClasspathElement");
				
				Class<?> ciuclazz = Class.forName("io.github.lukehutch.fastclasspathscanner.scanner.ClassInfoUnlinked");
				
				Method readclassinfofromclassfileheader = cbpclazz.getDeclaredMethod("readClassInfoFromClassfileHeader", ceclazz, String.class, InputStream.class, ScanSpec.class, LogNode.class);
				VmHacks.get().setAccessible(readclassinfofromclassfileheader, true);
				READCLASSINFOFROMCLASSFILEHEADER = lookup.unreflect(readclassinfofromclassfileheader);
				
				Field classannotations = ciuclazz.getDeclaredField("classAnnotations");
				VmHacks.get().setAccessible(classannotations, true);
				CLASSANNOTATIONS_FIELD = lookup.unreflectGetter(classannotations);
				
				INITIALIZED = true;
			}
			catch (Exception e)
			{
			}
		}
	}
	
	public static final List<AnnotationInfos> getAnnotationInfos(InputStream origis)
	{
		try
		{
			DataInputStream is = new DataInputStream(origis);
			byte[] buf = new byte[4];
			SUtil.readStream(buf, 0, 4, is);
			if (0xCAFEBABE != SUtil.bytesToInt(buf))
				throw new IllegalArgumentException("Not a class file.");
			
			is.skip(4);
			
			Map<Integer, String> strings = readConstantPoolStrings(is, buf);
			
			is.skip(6);
			
			int ifacecount = readUnsignedShort(is);
			
			is.skip(2 * ifacecount);
			
			skipFieldsOrMethods(is, buf, strings);
			
			skipFieldsOrMethods(is, buf, strings);
			
//			SUtil.readStream(buf, 0, 2, is);
			
			return readVisibleAnnotations(is, strings, buf);
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
	
	protected static final Map<Integer, String> readConstantPoolStrings(DataInputStream is, byte[] buf) throws IOException
	{
		Map<Integer, String> ret = new HashMap<>();
		int cpcount = readUnsignedShort(is);
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
	                is.skip(4);
	                break;
	            case 5:
	            case 6:
	            	is.skip(8);
	                i++;
	                break;
	            case 7:
	            case 8:
	            	is.skip(2);
	                break;
	            case 9:
	            case 10:
	            case 11:
	                is.skip(4);
	                break;
	            case 12:
	            	is.skip(4);
	                break;
	            case 15:
	                is.skip(3);
	                break;
	            case 16:
	                is.skip(2);
	                break;
	            case 18:
	                is.skip(4);
	                break;
	            case 19:
	                is.skip(2);
	                break;
	            case 20:
	            	is.skip(2);
	                break;
	            default:
	                throw new RuntimeException("Unknown constant pool tag: " + tag);
            }
        }
		return ret;
	}
	
	protected static final void skipFieldsOrMethods(InputStream is, byte[] buf, Map<Integer, String> strings) throws IOException
	{
		int fcount = readUnsignedShort(is);
		
		for (int i = 0; i < fcount; ++i)
		{
			is.skip(6);
			skipAttributes(is, buf);
		}
	}
	
	protected static final void skipAttributes(InputStream is, byte[] buf) throws IOException
	{
		int ac = readUnsignedShort(is);
		for (int i = 0; i < ac; ++i)
		{
			is.skip(2);
			int len = readInt(is);
			is.skip(len);
		}
	}
	
	protected static final List<AnnotationInfos> readVisibleAnnotations(DataInputStream is, Map<Integer, String> strings, byte[] buf) throws IOException
	{
		List<AnnotationInfos> ret = null;
		int ac = readUnsignedShort(is);
		for (int i = 0; i < ac; ++i)
		{
			int nameref = readUnsignedShort(is);
			if ("RuntimeVisibleAnnotations".equals(strings.get(nameref)))
			{
				is.skip(4);
				ret = readAnnotations(is, buf, strings);
				break;
			}
			else
			{
				is.skip(2);
				int len = readInt(is);
				is.skip(len);
			}
		}
		return ret;
	}
	
	public static final List<AnnotationInfos> readAnnotations(DataInputStream is, byte[] buf, Map<Integer, String> strings) throws IOException
	{
		List<AnnotationInfos> ret = new ArrayList<>();
		int anocount = readUnsignedShort(is);
		for (int i = 0; i < anocount; ++i)
		{
			ret.add(readAnnotation(is, buf, strings));
		}
		return ret;
	}
	
	protected final static AnnotationInfos readAnnotation(DataInputStream is, byte[] buf, Map<Integer, String> strings) throws IOException
	{
		int typeref = readUnsignedShort(is);
		int paircount = readUnsignedShort(is);
		
		String type = strings.get(typeref);
//		if (type == null)
//			continue;
		type = type.substring(1, type.length() - 1).replace('/', '.');
		AnnotationInfos ret = new AnnotationInfos(type);
		
		for (int i = 0; i < paircount; ++i)
		{
			int nameind = readUnsignedShort(is);
			String name = strings.get(nameind);
			if (name != null)
			{
				Object value = readAnnotationValue(is, buf, strings);
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
    private static final  Object readAnnotationValue(DataInputStream is, byte[] buf, Map<Integer, String> strings) throws IOException
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
	            ret = readAnnotation(is, buf, strings);
	            break;
	        case '[':
				int count = readUnsignedShort(is);
				ret = new Object[count];
	            for (int i = 0; i < count; ++i)
	            	((Object[]) ret)[i] = readAnnotationValue(is, buf, strings);
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
    	int ret = readUnsignedShort(is);
    	ret <<= 16;
    	ret |= readUnsignedShort(is);
    	return ret;
    }
    
    protected static final int readUnsignedShort(InputStream is) throws IOException
    {
    	int ret = is.read() & 0xFF;
    	ret <<= 8;
    	ret |= is.read() & 0xFF;
    	return ret;
    }
}
