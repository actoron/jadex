package jadex.android.controlcenter.preference;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnLongClickListener;

/**
 * A simple Preference that delegates long clicks to a previously set listener.
 */
public class LongClickablePreference extends Preference implements OnLongClickListener
{
	/** the listener to delegate long clicks to */
	protected OnPreferenceClickListener listener;

	public LongClickablePreference(Context context)
	{
		super(context);
	}

	public boolean onLongClick(View v)
	{
		// delegate this call
		return (this.listener != null) ? listener.onPreferenceClick(this) : false;
	}

	/**
	 * Sets a new {@link OnLongClickListener} for this preference.
	 * 
	 * @param l
	 */
	public void setOnPreferenceLongClickListener(OnPreferenceClickListener l)
	{
		this.listener = l;
	}

}
