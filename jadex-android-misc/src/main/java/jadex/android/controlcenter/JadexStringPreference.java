package jadex.android.controlcenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class JadexStringPreference extends EditTextPreference implements
		OnClickListener {

	private int mWhichButtonClicked;
	private AlertDialog.Builder mBuilder;
	private AlertDialog mDialog;
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
		final Dialog dialog = mDialog = mBuilder.create();
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
	public void setKey(String key) {
		super.setKey(key);
	}

	public void setValue(String value) {
		setDefaultValue(value);
		setText(value);
	}

	@Override
	public void onClick(View v) {
		showDialog();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		mDialog = null;
		boolean result = mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE;
		onDialogClosed(result);
		getView(view, parent);
	}

}
