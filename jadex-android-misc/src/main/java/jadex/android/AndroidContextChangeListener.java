package jadex.android;

import android.content.Context;

public interface AndroidContextChangeListener {
	void onContextDestroy(Context ctx);
	
	void onContextCreate(Context ctx);
}
