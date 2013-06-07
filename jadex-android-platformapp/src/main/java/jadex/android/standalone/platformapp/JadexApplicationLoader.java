package jadex.android.standalone.platformapp;

import jadex.android.commons.Logger;
import jadex.android.platformapp.R;
import jadex.android.service.JadexPlatformManager;
import jadex.android.standalone.JadexApplication;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.android.standalone.clientservice.UniversalClientService;
import jadex.android.standalone.clientservice.UniversalClientService.UniversalClientServiceBinder;

import java.io.File;
import java.lang.reflect.Constructor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import dalvik.system.DexClassLoader;

public class JadexApplicationLoader extends FragmentActivity implements ServiceConnection
{
	private static String defaultEntryActivityName = "jadex.android.platformapp.DefaultApplication";
	private LayoutInflater userAppInflater;
	private String userAppPackage;
	private Context userAppContext;
	private ClientAppFragment defaultActivity;
	private UniversalClientServiceBinder universalService;
	private Resources resources;
	private String className;
	private ClassLoader cl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// set default layout inflater and classLoader during onCreate()
		userAppInflater = super.getLayoutInflater();
		cl = this.getClassLoader();
		
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent != null && JadexApplication.INTENT_ACTION_LOADAPP.equals(intent.getAction()))
		{
			ApplicationInfo info = intent.getParcelableExtra(JadexApplication.EXTRA_KEY_APPLICATIONINFO);
			String appPath = info.sourceDir;
			userAppPackage = info.packageName;
			String className = intent.getStringExtra(JadexApplication.EXTRA_KEY_ACTIVITYCLASS);
			String originalAction = intent.getStringExtra(JadexApplication.EXTRA_KEY_ORIGINALACTION);

			intent.setAction(originalAction);
			intent.removeExtra(JadexApplication.EXTRA_KEY_ACTIVITYCLASS);
			intent.removeExtra(JadexApplication.EXTRA_KEY_ORIGINALACTION);
			intent.removeExtra(JadexApplication.EXTRA_KEY_APPLICATIONINFO);

			if (className == null)
			{
				className = defaultEntryActivityName;
			}

			this.className = className;

			if (appPath != null)
			{
				this.cl = getClassLoaderForExternalDex(getClass().getClassLoader(), appPath);
				ClientAppFragment act = loadAndCreateUserActivity(appPath, className);
				act.setIntent(intent);
				act.onPrepare(this);
				this.defaultActivity = act;
			}
			else
			{
				Logger.e("Please specify an Activity class to start with EXTRA_KEY_ACTIVITYCLASS!");
				finish();
				return;
			}

		}
		else
		{
			Logger.e("Please start this application with action net.sourceforge.jadex.LOAD_APPLICATION");
			finish();
			return;
		}

		Intent serviceIntent = new Intent(this, UniversalClientService.class);
		bindService(serviceIntent, this, BIND_AUTO_CREATE);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.universalService = (UniversalClientServiceBinder) service;
		defaultActivity.setUniversalClientService(universalService);
		
		try
		{
			Context userContext = getApplicationContext().createPackageContext(userAppPackage, Context.CONTEXT_IGNORE_SECURITY);
			userAppContext = userContext;
			initUserAppContext(userAppPackage);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ta = manager.beginTransaction();


		ta.add(R.id.fragmentContainer, defaultActivity);

		setContentView(R.layout.loaderlayout);
		ta.commit();
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		universalService = null;
		System.err.println("UniversalClientService disconnected. User Service bindings may be invalid.");
		// TODO: crash here!?
	}

	private void initUserAppContext(String userApplicationPackage)
	{
		// This LayoutInflater will make sure User Layouts are found
		userAppInflater = LayoutInflater.from(userAppContext);
		// This Factory will load custom Widget Classes, while android widgets
		// are loaded by the ClassLoader inside the LayoutInflater.
		userAppInflater.setFactory(layoutFactory);
		// Enable the use of R.id.<layoutId> or R.string.<stringId> inside the user app
		resources = new ResourceSet(getResources(), userAppContext.getResources());
		// TODO: same with assets?
	}

	@Override
	public Context getApplicationContext()
	{
		if (userAppContext == null)
		{
			return super.getApplicationContext();
		}
		else
		{
			System.out.println("Custom context returned");
			return userAppContext;
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
		if (universalService != null)
		{
			unbindService(this);
		}
		if (defaultActivity != null)
		{
			defaultActivity.onDestroy();
		}
		super.onDestroy();
	}

	@Override
	public LayoutInflater getLayoutInflater()
	{
		return userAppInflater;
	}

	@Override
	public Resources getResources()
	{
		if (resources != null)
		{
			return resources;
		}
		else
		{
			return super.getResources();
		}
	}
	
	@Override
	public ClassLoader getClassLoader()
	{
		System.out.println("getClassLoader");
		return cl;
	}

	@Override
	public AssetManager getAssets()
	{
		System.out.println("getAssets");
		return getApplicationContext().getAssets();
	}

	private ClientAppFragment loadAndCreateUserActivity(String appPath, String className)
	{
		JadexPlatformManager.getInstance().setAppClassLoader(appPath, cl);
		try
		{
			Class<ClientAppFragment> actClass = (Class<ClientAppFragment>) cl.loadClass(className);
			ClientAppFragment act = actClass.newInstance();
			return act;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private DexClassLoader getClassLoaderForExternalDex(ClassLoader parent, String appPath)
	{
		// File dexInternalStoragePath = new File(getDir("dex",
		// Context.MODE_PRIVATE), "jadex.jar");
		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);

		DexClassLoader cl = new DexClassLoader(appPath, optimizedDexOutputPath.getAbsolutePath(), null, parent) {
			@Override
			public String toString()
			{
				return "Custom DexClassLoader " + super.toString();
			}
		};
		return cl;
	}

	private ClassLoader getClassLoaderForInternalClasses(ClassLoader parent)
	{
		PackageManager pm = getPackageManager();

		ApplicationInfo applicationInfo = this.getApplicationInfo();

		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);

		return new DexClassLoader(applicationInfo.sourceDir, optimizedDexOutputPath.getAbsolutePath(), null, parent);
	}
	
	
	private Factory layoutFactory = new Factory()
	{
		 private final Class[] mConstructorSignature = new Class[] {
	            Context.class, AttributeSet.class};
		 
		 private final Object[] mConstructorArgs = new Object[2];
		 
		@Override
		public View onCreateView(String name, Context context, AttributeSet attrs)
		{
			ClassLoader mCl = context.getClassLoader();
			try
			{
				Class<?> loadClass = cl.loadClass(name);
				Constructor<?> constructor = loadClass.getConstructor(mConstructorSignature);
				Object[] args = mConstructorArgs;
				args[0] = context;
				args[1] = attrs;

				return (View) constructor.newInstance(args);
			}
			catch (Exception e)
			{
				Logger.d("Class " + name + " not found in user application.");
			}
			return null;
		}
	};

}
