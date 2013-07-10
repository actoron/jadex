package jadex.bdiv3;

import jadex.bdiv3.android.DexLoader;
import jadex.bdiv3.android.MethodInsManager;
import jadex.bdiv3.android.MyApplicationVisitor;
import jadex.bdiv3.model.BDIModel;
import jadex.commons.SUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ow2.asmdex.ApplicationReader;
import org.ow2.asmdex.ApplicationVisitor;
import org.ow2.asmdex.ApplicationWriter;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.lowLevelUtils.DexFileReader;

import android.util.Log;

import com.android.dx.dex.file.DexFile;

public class AsmDexBdiClassGenerator implements IBDIClassGenerator
{
	protected static Method methoddc1;
	protected static Method methoddc2;
	public static String APP_PATH;
	public static File OUTPATH;

//	static
//	{
//		try
//		{
//			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
//			{
//				public Object run() throws Exception
//				{
//					Class<?> cl = Class.forName("java.lang.ClassLoader");
//					methoddc1 = cl.getDeclaredMethod("defineClass", new Class[]
//					{String.class, byte[].class, int.class, int.class});
//					methoddc2 = cl.getDeclaredMethod("defineClass", new Class[]
//					{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
//					return null;
//				}
//			});
//		}
//		catch (PrivilegedActionException e)
//		{
//			throw new RuntimeException(e);
//		}
//	}

	@Override
	public Class<?> generateBDIClass(String classname, BDIModel micromodel, ClassLoader cl)
	{
		return generateBDIClass(classname, micromodel, cl, new HashSet<String>());
	}

	/**
	 * Generate class.
	 */
	public Class<?> generateBDIClass(final String clname, final BDIModel model, final ClassLoader cl, final Set<String> done)
	{
		Class<?> ret = null;

		final List<String> todo = new ArrayList<String>();
		done.add(clname);

		int api = Opcodes.ASM4;

		String iname = "L" + clname.replace('.', '/') + ";";
		// InputStream is;
		try
		{
			// is = SUtil.getResource(APP_PATH, cl);
			InputStream is = getFileInputStream(new File(APP_PATH));
			MethodInsManager rm = new MethodInsManager(); // Rules to apply
			ApplicationReader ar = new ApplicationReader(api, is);
			ApplicationWriter aw = new ApplicationWriter();
			ApplicationVisitor aa = new MyApplicationVisitor(api, rm, aw);

			// ar.accept(aa, new String[]
			// {"Ljadex/android/asmdexdemo/HelloWorld;"}, 0);

			ar.accept(aa, new String[]
			{iname}, 0);
			byte[] dex = aw.toByteArray();

			ClassLoader newCl = DexLoader.load(cl, dex, OUTPATH);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	private InputStream getFileInputStream(File apkFile)
	{
		InputStream result = null;
		String desiredFile = "classes.dex";
		 try  { 
		      FileInputStream fin = new FileInputStream(apkFile); 
		      ZipInputStream zin = new ZipInputStream(fin); 
		      ZipEntry ze = null; 
		      while ((ze = zin.getNextEntry()) != null) { 
		        Log.v("AsmDexBdi","Unzipping " + ze.getName()); 

		        if(!ze.isDirectory()) { 

		            /**** Changes made below ****/  
		              if (ze.getName().toString().equals(desiredFile)) {                  
		                result = zin;
		                break;
		              }

		          }

		          zin.closeEntry(); 
//		        } 

		      } 
//		      zin.close(); 
		    } catch(Exception e) { 
		      Log.e("Decompress", "unzip", e); 
		    }

		return result;
	}
}
