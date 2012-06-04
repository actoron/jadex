package jadex.android.controlcenter.settings;

import jadex.bridge.service.types.awareness.DiscoveryInfo;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscoveryPreference extends DialogPreference {

	private DiscoveryInfo info;
	private DiscoveryDialog dialog;

	public DiscoveryPreference(Context context, DiscoveryInfo info) {
		super(context, null);
		this.info = info;
		setTitle(info.getComponentIdentifier().getName());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		StringBuilder summaryString = new StringBuilder("Last info: ");
		summaryString.append(dateFormat.format(info.getTime()));
		if (info.proxy != null && info.proxy.isDone() && info.proxy.getException() == null) {
			summaryString.append("\nHas Proxy");
		}
		if (info.remoteexcluded) {
			summaryString.append("\nRemote excluded");
		}
		setSummary(summaryString);
	}
	
	@Override
	protected View onCreateDialogView() {
		dialog = new DiscoveryDialog(getContext());
		return dialog;
	}
	
	@Override
	protected void onBindDialogView(View view) {
		dialog.setProxy(info.proxy != null && info.proxy.isDone() && info.proxy.getException() == null);
		dialog.setExclude(info.isRemoteExcluded());
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			callChangeListener(dialog.hasProxy());
		}
	}
	
	static class DiscoveryDialog extends LinearLayout {

		private CheckBox proxyCheckBox;
		private CheckBox excludeCheckBox;

		public DiscoveryDialog(Context context) {
			super(context);
			initLayout();
		}

		private void initLayout() {
			this.setOrientation(LinearLayout.VERTICAL);
			LinearLayout proxyLayout = new LinearLayout(getContext()); 
			proxyLayout.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout excludeLayout = new LinearLayout(getContext());
			excludeLayout.setOrientation(LinearLayout.HORIZONTAL);
			
//			TextView proxyText = new TextView(getContext());
//			proxyText.setText("Has proxy");
//			TextView excludeText = new TextView(getContext());
//			excludeText.setText("Remote excluded");
			
			
			proxyCheckBox = new CheckBox(getContext());
			excludeCheckBox = new CheckBox(getContext());
			excludeCheckBox.setEnabled(false);
			
			proxyCheckBox.setText("Has proxy");
			excludeCheckBox.setText("Remote excluded");

//			proxyLayout.addView(proxyText);
			proxyLayout.addView(proxyCheckBox);
			
//			excludeLayout.addView(excludeText);
			excludeLayout.addView(excludeCheckBox);
			
			this.addView(proxyLayout);
			this.addView(excludeLayout);
		}
		
		public void setProxy(boolean b) {
			proxyCheckBox.setChecked(b);
		}
		
		public boolean hasProxy() {
			return proxyCheckBox.isChecked();
		}
		
		public void setExclude(boolean b) {
			excludeCheckBox.setChecked(b);
		}
	}
}
