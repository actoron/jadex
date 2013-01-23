package jadex.android.classloading;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import dalvik.system.DexClassLoader;

public class JadexClassLoading
{
	
	private Context context;
	private ClassLoader defLoader;

	public JadexClassLoading(Context ctx)
	{
		this.context = ctx;
	}

	public ClassLoader getClassLoaderWithoutParent() {
		copyDex();
		defLoader = this.getClass().getClassLoader();
		ClassLoader jarCl = getClassLoaderForExternalDex(ClassLoader.getSystemClassLoader());
		ClassLoader apkCl = getClassLoaderForApk(jarCl);
		
		return apkCl;
	}
	
	public ClassLoader getClassLoaderWithDefaultParent() {
		copyDex();
		defLoader = this.getClass().getClassLoader();
		ClassLoader jarCl = getClassLoaderForExternalDex(this.getClass().getClassLoader());
		ClassLoader apkCl = getClassLoaderForApk(jarCl);
		
		return apkCl;
	}
	

	private void copyDex()
	{
		System.out.println("COPYING DEX");
		File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE), "jadex.jar");
		BufferedInputStream bis = null;
		OutputStream dexWriter = null;

		final int BUF_SIZE = 8 * 1024;
		try
		{
			bis = new BufferedInputStream(context.getAssets().open("jadex.jar"));
			dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
			byte[] buf = new byte[BUF_SIZE];
			int len;
			while ((len = bis.read(buf, 0, BUF_SIZE)) > 0)
			{
				dexWriter.write(buf, 0, len);
			}
			dexWriter.close();
			bis.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private DexClassLoader getClassLoaderForExternalDex(ClassLoader parent)
	{
		File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE), "jadex.jar");
		final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);

		DexClassLoader cl = new VerboseDexClassLoader(dexInternalStoragePath.getAbsolutePath(), optimizedDexOutputPath.getAbsolutePath(), null,
				parent, defLoader,"jar");

		return cl;
	}
	
	private ClassLoader getClassLoaderForApk(ClassLoader parent)
	{
		PackageManager pm = context.getPackageManager();

		ApplicationInfo applicationInfo = context.getApplicationInfo();
		
		File dexInternalStoragePath = context.getDir("dex", Context.MODE_PRIVATE);
		
		return new VerboseDexClassLoader(applicationInfo.sourceDir, dexInternalStoragePath.getAbsolutePath(), null, parent, defLoader, "apk");
	}
}
