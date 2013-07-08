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
import java.lang.reflect.InvocationTargetException;

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
import android.support.v4.app.Fragment;
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
	private Context userAppContext;
	private ClientAppFragment clientFragment;
	private UniversalClientServiceBinder universalService;
	private Resources resources;
	private ClassLoader cl;
	
	private ApplicationInfo userAppInfo;

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
			userAppInfo = intent.getParcelableExtra(JadexApplication.EXTRA_KEY_APPLICATIONINFO);
			String appPath = userAppInfo.sourceDir;
			String className = intent.getStringExtra(JadexApplication.EXTRA_KEY_ACTIVITYCLASS);
			String originalAction = intent.getStringExtra(JadexApplication.EXTRA_KEY_ORIGINALACTION);
			int[] windowFeatures = intent.getIntArrayExtra(JadexApplication.EXTRA_KEY_WINDOWFEATURES);

			intent.setAction(originalAction);
			intent.removeExtra(JadexApplication.EXTRA_KEY_ACTIVITYCLASS);
			intent.removeExtra(JadexApplication.EXTRA_KEY_ORIGINALACTION);
			intent.removeExtra(JadexApplication.EXTRA_KEY_APPLICATIONINFO);
			intent.removeExtra(JadexApplication.EXTRA_KEY_WINDOWFEATURES);

			if (className == null)
			{
				className = defaultEntryActivityName;
			}

			if (appPath != null)
			{
				this.cl = getClassLoaderForExternalDex(getClass().getClassLoader(), appPath);
				JadexPlatformManager.getInstance().setAppClassLoader(appPath, cl);
				ClientAppFragment act = createClientFragment(className, intent);
		
				this.clientFragment = act;
			}
			else
			{
				Logger.e("Please specify an Activity class to start with EXTRA_KEY_ACTIVITYCLASS!");
				finish();
				return;
			}
			
			if (windowFeatures != null) {
				for (int i = 0; i < windowFeatures.length; i++)
				{
					requestWindowFeature(windowFeatures[i]);
				}
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
		
		try
		{
			Context userContext = getApplicationContext().createPackageContext(userAppInfo.packageName, Context.CONTEXT_IGNORE_SECURITY);
			userAppContext = userContext;
			initUserAppContext(userAppInfo.packageName);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		activateClientFragment(clientFragment, false);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		universalService = null;
		Logger.e("UniversalClientService disconnected. User Service bindings may be invalid.");
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
			return userAppContext;
		}
	}

//	@Override
//	protected void onResume()
//	{
//		super.onResume();
//	}

	@Override
	protected void onDestroy()
	{
		if (universalService != null)
		{
			unbindService(this);
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
		return cl;
	}

	@Override
	public AssetManager getAssets()
	{
		return getApplicationContext().getAssets();
	}

	/**
	 * Instantiates a {@link ClientAppFragment} and starts its lifecycle.
	 * @param className Classname of the ClientAppFragment
	 * @param intent The Intent to pass to the Fragment.
	 * @return
	 */
	private ClientAppFragment createClientFragment(String className, Intent intent)
	{
		ClientAppFragment act = null;
		try
		{
			Class<ClientAppFragment> actClass = (Class<ClientAppFragment>) cl.loadClass(className);
			act = actClass.newInstance();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		act.setIntent(intent);
		act.onPrepare(this);
		return act;
	}
	
	
	private void activateClientFragment(ClientAppFragment newFragment, boolean addToBackStack)
	{
		clientFragment = newFragment;
		newFragment.setUniversalClientService(universalService);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ta = manager.beginTransaction();
		ta.replace(R.id.fragmentContainer, newFragment);
		if (addToBackStack) {
			ta.addToBackStack(null);
		}
		setContentView(R.layout.loaderlayout);
		ta.commit();
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
			View result = null;
			Class<?> loadClass;
			try
			{
				loadClass = cl.loadClass(name);
				Constructor<?> constructor = loadClass.getConstructor(mConstructorSignature);
				Object[] args = mConstructorArgs;
				args[0] = context;
				args[1] = attrs;
				result = (View) constructor.newInstance(args);
			}
			catch (ClassNotFoundException e)
			{
				Logger.d("Class not found in user classes: " + name);
			}
			catch (SecurityException e)
			{
				Logger.d("Cannot access class: " + name);
			}
			catch (NoSuchMethodException e)
			{
				Logger.d("Cannot instanciate class (wrong constructor?): " + name);
			}
			catch (IllegalArgumentException e)
			{
				Logger.d("Cannot instanciate class (wrong constructor?): " + name);
			}
			catch (InstantiationException e)
			{
				Logger.d("Cannot instanciate class (wrong constructor?): " + name);
			}
			catch (IllegalAccessException e)
			{
				Logger.d("Cannot access class: " + name);
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			
			return result;
		}
	};
	
	
	// methods that are called from clientappfragments
	
	
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode)
	{
		final String className = intent.getComponent().getClassName();
		
		ClientAppFragment newFragment = createClientFragment(className, intent);
		
		activateClientFragment(newFragment, true);
	}
	
}
