package jadex.android.classloading;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class SystemActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		copyDex();
		
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		ClassLoader externalCl = getClassLoaderForExternalDex(ClassLoader.getSystemClassLoader().getParent());
		ClassLoader internalCl = getClassLoaderForInternalClasses(externalCl);
		
		Class userActivity = null;
		try
		{
			Class<?> ccf = internalCl.loadClass("jadex.component.ComponentComponentFactory");
			// System.out.println("LOADING USER ACTIVITY with default class loader");
			// userActivity =
			// othercl.loadClass("jadex.android.classloading.UserActivity");

			System.out.println("Loading JadexAndroidActivity");
			internalCl.loadClass("jadex.android.JadexAndroidActivity");
			System.out.println("Loading user activity");
			userActivity = internalCl.loadClass("jadex.android.classloading.UserActivity");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (userActivity != null)
		{
			try
			{
				Object userAct = userActivity.newInstance();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			// using an intent will cause the system to use the default class loader :(
			
//			Intent intent = new Intent(this, userActivity);
//			intent.setClassName("jadex.android.classloading", "jadex.android.classloading.UserActivity");
//			System.out.println("STARTING USER ACTIVITY");
//			startActivity(intent);
		}
	}

	private void copyDex()
	{
		System.out.println("COPYING DEX");
		File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE), "jadex.jar");
		BufferedInputStream bis = null;
		OutputStream dexWriter = null;

		final int BUF_SIZE = 8 * 1024;
		try
		{
			bis = new BufferedInputStream(getAssets().open("jadex.jar"));
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
		File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE), "jadex.jar");
		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);

		DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(), optimizedDexOutputPath.getAbsolutePath(), null,
				parent);

		return cl;
	}
	
	private ClassLoader getClassLoaderForInternalClasses(ClassLoader parent)
	{
		PackageManager pm = getPackageManager();

		ApplicationInfo applicationInfo = this.getApplicationInfo();
		
		File dexInternalStoragePath = getDir("dex", Context.MODE_PRIVATE);
		
		return new DexClassLoader(applicationInfo.sourceDir, dexInternalStoragePath.getAbsolutePath(), null, parent);
		
//		return new ParentLastPathClassLoader(applicationInfo.sourceDir, parent);
		
//		for (ApplicationInfo app : pm.getInstalledApplications(0)) {
//			if (applicationInfo.packageName.equals(app.packageName)) {
//				
//			}
//		  Log.d("PackageList", "package: " + app.packageName + ", sourceDir: " + app.sourceDir);
//		}
	}
}
