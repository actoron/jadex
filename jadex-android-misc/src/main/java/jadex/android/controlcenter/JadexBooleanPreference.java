package jadex.android.controlcenter;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class JadexBooleanPreference extends CheckBoxPreference implements
		OnClickListener {

	private View view;
	private ViewGroup parent;

	public JadexBooleanPreference(Context context) {
		super(context);
		setPersistent(false);
		setEnabled(true);
		setSelectable(true);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View view = super.onCreateView(parent);
		view.setOnClickListener(this);
		view.setEnabled(true);
		view.setClickable(true);
		return view;
	}

	@Override
	public View getView(View convertView, ViewGroup parent) {
		this.parent = parent;
		view = super.getView(convertView, parent);
		return view;
	}

	@Override
	public void onClick(View v) {
		super.onClick();
		getView(v, parent);
	}

}
