package de.unihamburg.vsis.jadexAndroid_test.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import de.unihamburg.vsis.jadexAndroid_test.BaseActivity;
import de.unihamburg.vsis.jadexAndroid_test.Helper;
import de.unihamburg.vsis.jadexAndroid_test.R;
import de.unihamburg.vsis.jadexAndroid_test.Startup;

public class ChatActivity extends BaseActivity {
	private ListView chatListView;
	private Button sendButton;
	private EditText editText;
	protected IExternalAccess extAcc;
	
	public static ChatService chatService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		chatListView = findListViewById(R.id.chat_activity_chatListView);
		sendButton = findButtonById(R.id.chat_activity_sendButton);
		editText = findEditTextById(R.id.chat_activity_inputField);
		
		IFuture<IExternalAccess> future = Startup.startBluetoothPlatform("Platform-"
				+ createRandomPlattformID());
		future.addResultListener(platformResultListener);
	}

	private IResultListener<IExternalAccess> platformResultListener = new DefaultResultListener<IExternalAccess>() {
		@Override
		public void resultAvailable(IExternalAccess result) {
			extAcc = result;
			startComponent(ChatService.class, "ChatService", extAcc).addResultListener(new DefaultResultListener<IComponentIdentifier>() {
				@Override
				public void resultAvailable(IComponentIdentifier result) {
					while (chatService == null) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					Log.i(Helper.LOG_TAG, "Chatservice set!");
				}
			});
		}
	};
}
