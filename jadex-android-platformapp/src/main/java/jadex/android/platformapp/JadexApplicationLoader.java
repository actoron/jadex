package jadex.android.platformapp;

import jadex.android.classloading.JadexApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import dalvik.system.DexClassLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class JadexApplicationLoader extends FragmentActivity
{
	public String entryActivityName;

	public JadexUserFragment adapter;
	
	public JadexApplicationLoader()
	{
		entryActivityName = "jadex.android.platformapp.DefaultApplication";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loaderlayout);
		Intent intent = getIntent();
		if (intent.getAction().equals(JadexApplication.INTENT_ACTION_LOADAPP)) {
			String appPath = intent.getStringExtra(JadexApplication.EXTRA_KEY_APPLICATIONPATH);
			String className = intent.getStringExtra(JadexApplication.EXTRA_KEY_ACTIVITYCLASS);
			if (className == null)
			{
				className = entryActivityName;
			}
			
			if (appPath != null) {
				JadexUserFragment act = loadAndCreateUserActivity(appPath, className);
				FragmentManager manager = getSupportFragmentManager();
				FragmentTransaction ta = manager.beginTransaction();
				ta.add(R.id.fragmentContainer, act);
				ta.commit();
//				setActivity(act);
				
			}
		} else {
			System.err.println("Please start this application with action net.sourceforge.jadex.LOAD_APPLICATION");
			Toast.makeText(this, "Please start this application with action net.sourceforge.jadex.LOAD_APPLICATION", Toast.LENGTH_LONG)
					.show();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private JadexUserFragment loadAndCreateUserActivity(String appPath, String className)
	{
		DexClassLoader cl = getClassLoaderForExternalDex(getClassLoader(), appPath);
		try
		{
			Class<JadexUserFragment> actClass = (Class<JadexUserFragment>) cl.loadClass(className);
			
			Constructor<JadexUserFragment> actCon = actClass.getConstructor(null);
			JadexUserFragment act = actClass.newInstance();
			return act;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void copyDex()
	{
		ApplicationInfo applicationInfo = this.getApplicationInfo();
		String ownDexDir = applicationInfo.sourceDir;
		
		String[] list = new File(ownDexDir).list();
		
		
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

	private DexClassLoader getClassLoaderForExternalDex(ClassLoader parent, String appPath)
	{
//		File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE), "jadex.jar");
		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);

		DexClassLoader cl = new DexClassLoader(appPath, optimizedDexOutputPath.getAbsolutePath(), null,
				parent);

		return cl;
	}
	
	
	private ClassLoader getClassLoaderForInternalClasses(ClassLoader parent)
	{
		PackageManager pm = getPackageManager();

		ApplicationInfo applicationInfo = this.getApplicationInfo();
		
		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);
		
		return new DexClassLoader(applicationInfo.sourceDir, optimizedDexOutputPath.getAbsolutePath(), null, parent);
		
//		return new ParentLastPathClassLoader(applicationInfo.sourceDir, parent);
		
//		for (ApplicationInfo app : pm.getInstalledApplications(0)) {
//			if (applicationInfo.packageName.equals(app.packageName)) {
//				
//			}
//		  Log.d("PackageList", "package: " + app.packageName + ", sourceDir: " + app.sourceDir);
//		}
	}

}
