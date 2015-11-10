package jadex.android.applications.chat.fragments;

import jadex.android.applications.chat.R;
import jadex.android.applications.chat.model.ITypedObserver;
import jadex.android.applications.chat.model.TypedObservable;
import jadex.android.applications.chat.service.AndroidChatService;
import jadex.android.applications.chat.service.IAndroidChatService;
import jadex.bridge.IComponentIdentifier;
import jadex.platform.service.chat.ChatService;

import java.util.Observer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class MainActivity extends FragmentActivity implements ServiceConnection, ChatServiceProvider {
	
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;
	
	private IAndroidChatService service;
	
	private boolean connected;
	private TypedObservable<Boolean> connectedObservable = new TypedObservable<Boolean>();
	
	/**
	 * Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		startService(new Intent(this, AndroidChatService.class));
		setTitle("Jadex Chat");

		setContentView(R.layout.main);

		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		ClassLoader classLoader = this.getClass().getClassLoader();
		System.out.println(classLoader);
		try {
			String name = DemoCollectionPagerAdapter.class.getName();
			Class<?> loadClass = classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDemoCollectionPagerAdapter =
				new DemoCollectionPagerAdapter(
						getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	};
	
	@Override
	public void onResume() {
		super.onResume();
		setProgressBarIndeterminateVisibility(true);
		bindService(new Intent(this, AndroidChatService.class), this, BIND_AUTO_CREATE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (service != null) {
			service.setStatus(ChatService.STATE_AWAY, null, null);
			unbindService(this);
		}
	}
	
	public void onServiceConnected(ComponentName comp, IBinder binder)
	{
		System.out.println("service connected: " + IComponentIdentifier.LOCAL.get());
		this.service = (IAndroidChatService) binder;
		setConnected(true);
	}
	
	public void onServiceDisconnected(ComponentName name)
	{
		this.service = null;
		setConnected(false);
	}
	
	private void setConnected(final boolean b)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				setProgressBarIndeterminateVisibility(false);
			}
		});
		connectedObservable.setChanged();
		connectedObservable.notifyObservers(b);
		this.connected = b;
	}
	

	public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
	    public DemoCollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int i) {
	    	Fragment fragment = null;
	    	switch (i) {
			case 0:
				fragment = new ChatFragment();
				break;
			case 1:
				fragment = new UsersFragment();
				break;
			default:
				fragment = new ChatFragment();
				break;
			}
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
//	        args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
	        fragment.setArguments(args);
	        return fragment;
	    }

	    @Override
	    public int getCount() {
	        return 2;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	    	switch (position) {
			case 0:
				return "Chat";
			case 1:
				return "Users";
			default:
				return "";
			}
	    }
	}

	@Override
	public boolean hasChanged() {
		return this.connectedObservable.hasChanged();
	}

	@Override
	public void deleteObservers() {
		this.connectedObservable.deleteObservers();;
	}

	@Override
	public void deleteObserver(Observer o) {
		this.connectedObservable.deleteObserver(o);
	}

	@Override
	public void addObserver(ITypedObserver<Boolean> o) {
		this.connectedObservable.addObserver(o);
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public IAndroidChatService getChatService() {
		return service;
	}

}
