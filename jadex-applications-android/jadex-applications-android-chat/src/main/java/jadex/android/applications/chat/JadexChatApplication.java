package jadex.android.applications.chat;

import android.content.Intent;
import android.view.Window;
import jadex.android.standalone.JadexClientApplication;

public class JadexChatApplication extends JadexClientApplication
{

	@Override
	protected String getInitialFragmentClassName()
	{
		String result;
		Intent intent = getIntent();
		if (Intent.ACTION_SEND.equals(intent.getAction()) || (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())))
		{
			result = "jadex.android.applications.chat.filetransfer.SendFileActivity";
		}
		else
		{
			result = "jadex.android.applications.chat.JadexAndroidChatActivity";
		}
		return result;
	}

	@Override
	protected int[] getWindowFeatures()
	{
		return new int[]{Window.FEATURE_INDETERMINATE_PROGRESS};
	}
}
