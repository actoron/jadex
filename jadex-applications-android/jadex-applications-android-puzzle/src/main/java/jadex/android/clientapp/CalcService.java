package jadex.android.clientapp;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import jadex.android.standalone.clientapp.JadexClientAppService;

public class CalcService extends JadexClientAppService
{

	public class CalcResult {
		public int result;
	}
	
	public class CalcBinder extends Binder
	{
		public CalcResult add(int var1, int var2) {
			CalcResult result = new CalcResult();
			result.result = var1 + var2;
			return result;
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new CalcBinder();
	}
	

}
