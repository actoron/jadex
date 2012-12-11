package jadex.android.controlcenter.preference;

import jadex.bridge.service.types.awareness.DiscoveryInfo;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.preference.DialogPreference;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class DiscoveryPreference extends LongClickablePreference {

	private DiscoveryInfo info;

	public DiscoveryPreference(Context context, DiscoveryInfo info) {
		super(context);
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
	
	public DiscoveryInfo getDiscoveryInfo() {
		return info;
	}
	
//	@Override
//	protected View onCreateDialogView() {
//		dialog = new DiscoveryDialog(getContext());
//		return dialog;
//	}
//	
//	@Override
//	protected void onBindDialogView(View view) {
//		dialog.setProxy(info.proxy != null && info.proxy.isDone() && info.proxy.getException() == null);
//		dialog.setExclude(info.isRemoteExcluded());
//	}
//	
//	@Override
//	protected void onDialogClosed(boolean positiveResult) {
//		if (positiveResult) {
//			callChangeListener(dialog.hasProxy());
//		}
//	}
	
}
