package jadex.android.standalone.platformapp;

import jadex.android.commons.JadexDexClassLoader;
import jadex.android.commons.Logger;
import jadex.android.exception.JadexAndroidError;
import jadex.android.platformapp.R;
import jadex.android.service.JadexPlatformManager;
import jadex.android.standalone.JadexClientLauncherActivity;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.android.standalone.clientservice.UniversalClientService;
import jadex.android.standalone.clientservice.UniversalClientService.UniversalClientServiceBinder;
import jadex.bdiv3.AsmDexBdiClassGenerator;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
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

public class JadexMiddlewareActivity extends FragmentActivity implements ServiceConnection
{
	private static String defaultEntryActivityName = "jadex.android.platformapp.DefaultApplication";
	private LayoutInflater clientAppInflater;
	private Context clientAppContext;
	private ClientAppFragment clientFragment;
	private UniversalClientServiceBinder universalService;
	private Resources resources;
	
	/** this ClassLoader will change depending on the foreground app **/
	private ClassLoader currentCl;
	
	private ApplicationInfo userAppInfo;
	private static Map<String,ClassLoader> clCache = new HashMap<String, ClassLoader>();
	
	//** TODO: remove **/
	public static ApplicationInfo APPINFO;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// set default layout inflater and classLoader during onCreate()
		clientAppInflater = super.getLayoutInflater();
		currentCl = this.getClassLoader();
		
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent != null && JadexClientLauncherActivity.INTENT_ACTION_LOADAPP.equals(intent.getAction()))
		{
			userAppInfo = intent.getParcelableExtra(JadexClientLauncherActivity.EXTRA_KEY_APPLICATIONINFO);
			String appPath = userAppInfo.sourceDir;
			String className = intent.getStringExtra(JadexClientLauncherActivity.EXTRA_KEY_ACTIVITYCLASS);
			String originalAction = intent.getStringExtra(JadexClientLauncherActivity.EXTRA_KEY_ORIGINALACTION);
			int[] windowFeatures = intent.getIntArrayExtra(JadexClientLauncherActivity.EXTRA_KEY_WINDOWFEATURES);

			intent.setAction(originalAction);
			intent.removeExtra(JadexClientLauncherActivity.EXTRA_KEY_ACTIVITYCLASS);
			intent.removeExtra(JadexClientLauncherActivity.EXTRA_KEY_ORIGINALACTION);
			intent.removeExtra(JadexClientLauncherActivity.EXTRA_KEY_APPLICATIONINFO);
			intent.removeExtra(JadexClientLauncherActivity.EXTRA_KEY_WINDOWFEATURES);

			if (className == null)
			{
				className = defaultEntryActivityName;
			}

			if (appPath != null)
			{
				this.currentCl = getClassLoaderForExternalDex(getClass().getClassLoader(), appPath);
				JadexPlatformManager.getInstance().setAppClassLoader(appPath, currentCl);
				ClientAppFragment act = createClientFragment(currentCl, className, intent, userAppInfo);
		
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
		
		initUserAppContext(userAppInfo.packageName);
		
		activateClientFragment(clientFragment, false);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		universalService = null;
		throw new JadexAndroidError("UniversalClientService disconnected. User Service bindings may be invalid!");
	}

	private void initUserAppContext(String userApplicationPackage)
	{
		try
		{
			Context userContext = getApplicationContext().createPackageContext(userApplicationPackage, 0);
			clientAppContext = userContext;
			// This LayoutInflater will make sure User Layouts are found
			clientAppInflater = LayoutInflater.from(clientAppContext);
			// This Factory will load custom Widget Classes, while android widgets
			// are loaded by the ClassLoader inside the LayoutInflater.
			clientAppInflater.setFactory(layoutFactory);
			// Enable the use of R.id.<layoutId> or R.string.<stringId> inside the user app
			resources = new ResourceSet(getResources(), clientAppContext.getResources());
			// TODO: same with assets?
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public Context getApplicationContext()
	{
		if (clientAppContext == null)
		{
			return super.getApplicationContext();
		}
		else
		{
			return clientAppContext;
		}
	}

	@Override
	protected void onResume()
	{
		Logger.d("Resuming JadexApplicationLoader");
		Intent intent = getIntent();
		if (userAppInfo != null) {
			Logger.d("setting ClassLoader for " + userAppInfo.sourceDir);
			String appPath = userAppInfo.sourceDir;
			this.currentCl = getClassLoaderForExternalDex(getClass().getClassLoader(), appPath);
		} else {
			Logger.e("No appinfo found, resuming not possible!");
		}
		super.onResume();
	}

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
		return clientAppInflater;
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
		return currentCl;
	}

	@Override
	public AssetManager getAssets()
	{
		return getApplicationContext().getAssets();
	}

	/**
	 * Instantiates a {@link ClientAppFragment} and starts its lifecycle.
	 * @param cl The classloader that is used to instanciate the Fragment
	 * @param className Classname of the ClientAppFragment
	 * @param intent The Intent to pass to the Fragment.
	 * @param appInfo
	 * @return The new ClientAppFragment
	 */
	private ClientAppFragment createClientFragment(ClassLoader cl, String className, Intent intent, ApplicationInfo appInfo)
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
			e.printStackTrace();
		}
		
		act.setApplicationInfo(appInfo);
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
	
	private ClassLoader getClassLoaderForExternalDex(ClassLoader parent, String appPath)
	{
		// clCache must exist during whole platform lifetime = in universalService?
		ClassLoader result = clCache.get(appPath);
		if (result == null) {
			// File dexInternalStoragePath = new File(getDir("dex",
			// Context.MODE_PRIVATE), "jadex.jar");
			final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);
			AsmDexBdiClassGenerator.OUTPATH = optimizedDexOutputPath;
			
			result = new JadexDexClassLoader(appPath, optimizedDexOutputPath.getAbsolutePath(), null, parent);
			clCache.put(appPath, result);
		}
		
		return result;
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
				loadClass = currentCl.loadClass(name);
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
		ApplicationInfo appInfo = ((ClientAppFragment) fragment).getApplicationInfo();
		ClientAppFragment newFragment = createClientFragment(currentCl, className, intent, appInfo);
		// TODO: check for external activities
		activateClientFragment(newFragment, true);
	}
	
}
