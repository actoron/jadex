package jadex.bdiv3.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

//import com.android.dx.dex.DexFormat;

public class DexLoader
{
	
//	public static final String classLoaderName = "dalvik.system.DexClassLoader";
	
	public static final String classLoaderName = ParentLastDexClassLoader.class.getCanonicalName();
	
	
    public static ClassLoader load(ClassLoader parent, byte[] dex, File dexCache) throws IOException {
    	
//    	DexClassLoader helloWorldLoader = new ParentLastDexClassLoader(dexFile.getAbsolutePath(), optimizedDexPath.getAbsolutePath(), null, this.getClassLoader());
		
        /*
         * This implementation currently dumps the dex to the filesystem. It
         * jars the emitted .dex for the benefit of Gingerbread and earlier
         * devices, which can't load .dex files directly.
         *
         * TODO: load the dex from memory where supported.
         */
        File result = File.createTempFile("Generated", ".jar", dexCache);
        result.deleteOnExit();
        JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(result));
//        jarOut.putNextEntry(new JarEntry(DexFormat.DEX_IN_JAR_NAME));
        jarOut.write(dex);
        jarOut.closeEntry();
        jarOut.close();
        try {
            return (ClassLoader) Class.forName(classLoaderName)
                    .getConstructor(String.class, String.class, String.class, ClassLoader.class)
                    .newInstance(result.getPath(), dexCache.getAbsolutePath(), null, parent);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("load() requires a Dalvik VM", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (InstantiationException e) {
            throw new AssertionError();
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        } catch (IllegalAccessException e) {
            throw new AssertionError();
        }
    }

}
