package jadex.android.controlcenter.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class JadexStringPreference extends EditTextPreference implements OnClickListener {

	private int mWhichButtonClicked;
	private AlertDialog.Builder mBuilder;
	private View view;
	private ViewGroup parent;

	public JadexStringPreference(Context context) {
		super(context);
		setEnabled(true);
		setSelectable(true);
		setPersistent(false);
	}

	public void showDialog() {
		Context context = getContext();
		mWhichButtonClicked = DialogInterface.BUTTON_POSITIVE;

		mBuilder = new AlertDialog.Builder(context)
				.setTitle(super.getDialogTitle())
				.setIcon(super.getDialogIcon())
				.setPositiveButton(super.getPositiveButtonText(), this)
				.setNegativeButton(super.getNegativeButtonText(), this);

		View contentView = onCreateDialogView();
		
		if (contentView != null) {
			onBindDialogView(contentView);
			mBuilder.setView(contentView);
		} else {
			mBuilder.setMessage(super.getDialogMessage());
		}

		onPrepareDialogBuilder(mBuilder);

		// Create the dialog
		final Dialog dialog = mBuilder.create();
		// if (needInputMethod()) {
		// requestInputMethod(dialog);
		// }
		dialog.setOnDismissListener(this);
		dialog.show();
	}

	@Override
	public View getView(View convertView, ViewGroup parent) {
		view = super.getView(convertView, parent);
		this.parent = parent;
		view.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		setSummary("Current value: "
				+ text);
	}
	
	public void setValue(Object value) {
		String text = (String) value;
		setText(text);
	}
	
	public void onClick(View v) {
		showDialog();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		boolean result = mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE;
		if (result) {
		}
		onDialogClosed(result);
		view = getView(view, parent);
	}

}
