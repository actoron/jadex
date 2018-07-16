package jadex.commons;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.List;

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
	public static List<AnnotationInfo> getAnnotationInfos(final String filepath, ClassLoader cl)
	{
		initialize();
		
		List<AnnotationInfo> ret = null;
		InputStream is = null;
		try
		{
			String fp = filepath;
			if(fp.endsWith(".class") && fp.indexOf("/")==-1 && fp.indexOf("\\")==-1)
				fp = fp.substring(0, fp.length()-6).replace('.', '/')+".class";
			
			ResourceInfo ri = SUtil.getResourceInfo0(fp, cl);
			
			String relpath = filepath;
			if(filepath.indexOf("/")==-1 && filepath.indexOf("\\")==-1)
				relpath = filepath.substring(0, filepath.length()-6).replace('.', '/')+".class";
			else if(filepath.indexOf("\\")!=-1)
				relpath = filepath.substring(0, filepath.length()-6).replace('\\', '/')+".class";
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
}
