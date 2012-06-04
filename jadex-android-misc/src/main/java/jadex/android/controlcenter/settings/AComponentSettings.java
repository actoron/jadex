package jadex.android.controlcenter.settings;

import android.app.Activity;
import android.preference.PreferenceScreen;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import jadex.bridge.IExternalAccess;

public abstract class AComponentSettings implements ISettings {

	protected IExternalAccess extAcc;
	private String title;

	public AComponentSettings(IExternalAccess component) {
		this.extAcc = component;
		this.title = extAcc.getModel().getName();
	}

	protected abstract void createPreferenceHierarchy(PreferenceScreen screen);

	@Override
	public void setPreferenceScreen(PreferenceScreen screen) {
		screen.setTitle(title);
		createPreferenceHierarchy(screen);
	}

	@Override
	public String getTitle() {
		return title;
	}
}
