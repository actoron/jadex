package jadex.android.controlcenter;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This Activity is a PreferenceActivity that allows adding of PreferenceScreens
 * that support Option Menus (Android does not allow that by default, see
 * http:// stackoverflow.com/questions/5032141/adding-menus-to-child-preference-
 * screens
 * 
 * How it works: PreferenceScreens do not longer extend PreferenceScreen but
 * must implement {@link IChildPreferenceScreen}. They must be created using
 * createSubPreferenceScreen.
 * 
 */
public class OptionsMenuDelegatingPreferenceActivity extends PreferenceActivity
{
	public static final String EXTRA_SHOWCHILDPREFSCREEN = "showChildPrefScreen";
	public static final String EXTRA_SETTINGSKEY = "settingsKey";

	private IChildPreferenceScreen displayedChildPreferenceScreen;

	static private Map<String, IChildPreferenceScreen> childScreens;

	static
	{
		childScreens = new HashMap<String, IChildPreferenceScreen>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		updateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = false;
		if (displayedChildPreferenceScreen != null)
		{
			// child functionality
			result = displayedChildPreferenceScreen.onCreateOptionsMenu(menu);
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = false;
		if (displayedChildPreferenceScreen != null)
		{
			// child functionality
			result = displayedChildPreferenceScreen.onOptionsItemSelected(item);
		}
		return result;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		super.onOptionsMenuClosed(menu);
		if (displayedChildPreferenceScreen != null)
		{
			// child functionality
			displayedChildPreferenceScreen.onOptionsMenuClosed(menu);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (displayedChildPreferenceScreen != null)
		{
			displayedChildPreferenceScreen.onDestroy();
		}
	}

	protected boolean updateView()
	{
		boolean result = false;
		if (getIntent().getBooleanExtra(EXTRA_SHOWCHILDPREFSCREEN, false))
		{
			String settingsKey = getIntent().getStringExtra(EXTRA_SETTINGSKEY);
			displayedChildPreferenceScreen = childScreens.get(settingsKey);
			if (displayedChildPreferenceScreen != null)
			{
				// display child preferences, enables us to control the options
				// menu
				PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
				setPreferenceScreen(root);
				displayedChildPreferenceScreen.setPreferenceScreen(root);
				this.setTitle(settingsKey);
				result = true;
			}
			else
			{
				// display error
			}
		}
		else
		{
			displayedChildPreferenceScreen = null;
		}
		return result;
	}

	protected void resetChildScreens()
	{
		childScreens.clear();
	}

	protected boolean isDisplayingChildScreen()
	{
		return displayedChildPreferenceScreen != null;
	}

	protected PreferenceScreen createSubPreferenceScreen(IChildPreferenceScreen child)
	{
		PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(this);
		Intent i = new Intent(this, this.getClass());
		// i.putExtra(EXTRA_PLATFORMID, (Serializable) platformId);
		i.putExtra(EXTRA_SHOWCHILDPREFSCREEN, true);
		i.putExtra(EXTRA_SETTINGSKEY, child.getTitle());
		childScreens.put(child.getTitle(), child);
		screen.setIntent(i);
		screen.setKey(child.getTitle());
		screen.setTitle(child.getTitle());
		return screen;
	}

}
