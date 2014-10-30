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

import java.io.File;
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
import android.util.Log;
import android.view.LayoutInflater;

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
	/** this AppInfo will change depending on the foreground app **/
	private ApplicationInfo clientAppInfo;
	
	private ClientAppLayoutFactory layoutFactory;
	
	private static Map<String,ClassLoader> clCache = new HashMap<String, ClassLoader>();
	
	//** TODO: remove **/
	public static ApplicationInfo APPINFO;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// set default layout inflater and classLoader during onCreate()
		clientAppInflater = super.getLayoutInflater();
		currentCl = this.getClassLoader();
		layoutFactory = new ClientAppLayoutFactory();
		
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent != null && JadexClientLauncherActivity.INTENT_ACTION_LOADAPP.equals(intent.getAction()))
		{
			clientAppInfo = intent.getParcelableExtra(JadexClientLauncherActivity.EXTRA_KEY_APPLICATIONINFO);
			String appPath = clientAppInfo.sourceDir;
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
				setCurrentCl(getClassLoaderForExternalDex(getClass().getClassLoader(), appPath));
				JadexPlatformManager.getInstance().setAppClassLoader(appPath, currentCl);
				ClientAppFragment act = createClientAppFragment(currentCl, className, intent, clientAppInfo);
		
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
	protected void onResume()
	{
		Logger.d("Resuming JadexApplicationLoader");
		Intent intent = getIntent();
		if (clientAppInfo != null) {
			Logger.d("setting ClassLoader for " + clientAppInfo.sourceDir);
			String appPath = clientAppInfo.sourceDir;
			setCurrentCl(getClassLoaderForExternalDex(getClass().getClassLoader(), appPath));
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
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.universalService = (UniversalClientServiceBinder) service;
		
		initUserAppContext(clientAppInfo.packageName);
		
		activateClientAppFragment(clientFragment, false);
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
			Context clientContext = createPackageContext(userApplicationPackage, 0);
			clientAppContext = new ClientAppContextWrapper(clientContext, getOriginalApplicationContext());
			// This LayoutInflater will make sure User Layouts are found
			clientAppInflater = LayoutInflater.from(clientAppContext);
			// This Factory will load custom Widget Classes, while android widgets
			// are loaded by the ClassLoader inside the LayoutInflater.
			clientAppInflater.setFactory(layoutFactory);
			// Enable the use of R.id.<layoutId> or R.string.<stringId> inside the user app
			resources = new ResourceSet(getResources(), clientAppContext.getResources());
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
	
	private Context getOriginalApplicationContext() {
		return super.getApplicationContext();
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
	
	private void setCurrentCl(ClassLoader cl)
	{
		this.currentCl = cl;
		layoutFactory.setClassLoader(cl);
	}

	/**
	 * Instantiates a {@link ClientAppFragment} and starts its lifecycle.
	 * @param cl The classloader that is used to instanciate the Fragment
	 * @param className Classname of the ClientAppFragment
	 * @param intent The Intent to pass to the Fragment.
	 * @param appInfo
	 * @return The new ClientAppFragment
	 */
	private ClientAppFragment createClientAppFragment(ClassLoader cl, String className, Intent intent, ApplicationInfo appInfo)
	{
		ClientAppFragment act = null;
		try
		{
			@SuppressWarnings("unchecked")
			Class<?> actClass = (Class<ClientAppFragment>) cl.loadClass(className);
			Object newInstance = actClass.newInstance();
			if (newInstance instanceof ClientAppFragment) {
				act = (ClientAppFragment) newInstance;
			} else {
				throw new JadexAndroidError("Could not start Fragment: <" + className
						+ "> of type: <" 
						+ newInstance.getClass().getName()
						+ "> \nJadex ClientApps must only consist of ClientAppFragments!");
			}
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
	
	
	private void activateClientAppFragment(ClientAppFragment newFragment, boolean addToBackStack)
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
//			AsmDexBdiClassGenerator.OUTPATH = optimizedDexOutputPath;
			
			result = new JadexDexClassLoader(appPath, optimizedDexOutputPath.getAbsolutePath(), null, parent);
			clCache.put(appPath, result);
		}
		
		return result;
	}

	// methods that are called from clientappfragments
	
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode)
	{
		ComponentName originalComponent = intent.getComponent();
		
		if (originalComponent == null) {
			// external activity, pass-through intent
			super.startActivityFromFragment(fragment, intent, requestCode);
		} else {
			String className = intent.getComponent().getClassName();
			boolean backstack = intent.getBooleanExtra(ClientAppFragment.EXTRA_KEY_BACKSTACK, true);
			ApplicationInfo appInfo = ((ClientAppFragment) fragment).getApplicationInfo();
			ClientAppFragment newFragment = createClientAppFragment(currentCl, className, intent, appInfo);
			Logger.d("Activating ClientAppFragment: " + className);
			activateClientAppFragment(newFragment, backstack);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	
	
}
