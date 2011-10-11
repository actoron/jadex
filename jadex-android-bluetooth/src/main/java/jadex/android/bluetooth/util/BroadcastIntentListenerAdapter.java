package jadex.android.bluetooth.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BroadcastIntentListenerAdapter {

	private boolean oneTime = true;
	private Context ctx;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			BroadcastIntentListenerAdapter.this.onReceive(context, intent);
			if (oneTime) {
				unregister();
			}
		}
	};

	public BroadcastIntentListenerAdapter(Context ctx, IntentFilter filter) {
		this(ctx, filter, true);
	}

	public BroadcastIntentListenerAdapter(Context ctx, IntentFilter filter,
			boolean oneTime) {
		this.ctx = ctx;
		this.oneTime = oneTime;
		ctx.registerReceiver(receiver, filter);
	}

	public void unregister() {
		if (ctx != null && receiver != null) {
			ctx.unregisterReceiver(receiver);
			ctx = null;
			receiver = null;
		}
	}

	protected abstract void onReceive(Context context, Intent intent);
}
