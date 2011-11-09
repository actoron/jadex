package jadex.android.bluetooth;

import android.content.Context;

public interface AndroidContextChangeListener {
	void onContextDestroy(Context ctx);
	
	void onContextCreate(Context ctx);
}
