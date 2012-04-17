package jadex.android.controlcenter;

import jadex.extension.agr.MPosition;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.View;

public class JadexStringPreference extends EditTextPreference {

	private int mWhichButtonClicked;
	private AlertDialog.Builder mBuilder;
	private AlertDialog mDialog;

	public JadexStringPreference(Context context) {
		super(context);
	}
	
	public void showDialog() {
		   Context context = getContext();
	        mWhichButtonClicked = DialogInterface.BUTTON2;
	        
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
	        
	        //getPreferenceManager().registerOnActivityDestroyListener(this);
	        
	        // Create the dialog
	        final Dialog dialog = mDialog = mBuilder.create();
//	        if (needInputMethod()) {
//	            requestInputMethod(dialog);
//	        }
	        dialog.setOnDismissListener(this);
	        dialog.show();
	}
	
}
