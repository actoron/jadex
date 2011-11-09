package de.unihamburg.vsis.jadexAndroid_test.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import de.unihamburg.vsis.jadexAndroid_test.BaseActivity;
import de.unihamburg.vsis.jadexAndroid_test.Helper;
import de.unihamburg.vsis.jadexAndroid_test.R;
import de.unihamburg.vsis.jadexAndroid_test.Startup;

public class ChatActivity extends BaseActivity {
	private static final String KEY_TEXTINPUT = "KEY_TEXTINPUT";
	private ListView chatListView;
	private Button sendButton;
	private EditText editText;
	protected IExternalAccess extAcc;

	public static IExternalAccess chatAgent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		chatListView = findListViewById(R.id.chat_activity_chatListView);
		sendButton = findButtonById(R.id.chat_activity_sendButton);
		editText = findEditTextById(R.id.chat_activity_inputField);

		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		String[] chatList = getRetainConfigObject().chatList;
		if (chatList != null) {
			for (int i = 0; i < chatList.length; i++) {
				arrayAdapter.add(chatList[i]);
			}
		}
		
		chatListView.setStackFromBottom(true);
		chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		chatListView.setDividerHeight(0);		
		chatListView.setAdapter(arrayAdapter);
		
		extAcc = getRetainConfigObject().extAcc;
		
		if (extAcc == null) {
			IFuture<IExternalAccess> future = Startup
					.startBluetoothPlatform("Platform-" + createRandomPlattformID());
			future.addResultListener(platformResultListener);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isFinishing() && extAcc != null) {
			extAcc.killComponent();
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		editText.setText(savedInstanceState.getString(KEY_TEXTINPUT));
		if (chatAgent != null) {
			setUiListeners();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_TEXTINPUT, editText.getText().toString());
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		retainNonConfigurationInstance o = new retainNonConfigurationInstance();
		o.extAcc = extAcc;
		o.chatList = new String[arrayAdapter.getCount()];
		for (int i = 0; i < arrayAdapter.getCount(); i++) {
			o.chatList[i] = arrayAdapter.getItem(i);
		}
		
		return o;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	private IResultListener<IExternalAccess> platformResultListener = new DefaultResultListener<IExternalAccess>() {
		@Override
		public void resultAvailable(IExternalAccess result) {
			extAcc = result;
			startComponent(ChatAgent.class, "ChatAgent", extAcc)
					.addResultListener(
							new DefaultResultListener<IComponentIdentifier>() {
								@Override
								public void resultAvailable(
										IComponentIdentifier result) {

									while (chatAgent == null) {
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									Log.i(Helper.LOG_TAG, "Chatservice set!");
									setUiListeners();
								}
							});
		}
	};

	private void setUiListeners() {
		sendButton.setOnClickListener(sendClickListener);
		IFuture<IChatService> step = chatAgent.scheduleStep(new IComponentStep<IChatService>()
				{
					@XMLClassname("addlistener")
					public IFuture<IChatService> execute(IInternalAccess ia)
					{
						Future<IChatService> ret = new Future<IChatService>();
						IService iService = ia.getServiceContainer().getProvidedServices(IChatService.class)[0];
						ret.setResult((IChatService) iService);
						return ret;
					}
				});
				
				
				
				step.addResultListener(new DefaultResultListener<IChatService>()
				{
					public void resultAvailable(IChatService cs)
					{
						cs.addChangeListener(new IRemoteChangeListener()
						{
							public IFuture changeOccurred(final ChangeEvent event)
							{
								Future ret = new Future();
//								if(!isVisible())
//								{
//									ret.setException(new RuntimeException("Gui closed."));
//								}
//								else
//								{
//									addMessage((String)event.getSource(), (String)event.getValue());
//									ret.setResult(null);
//								}
								addMessage((String)event.getSource(), (String)event.getValue());
								return ret;
							}

							
						});
					}
				});
	}

	private OnClickListener sendClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == sendButton) {
				String text = editText.getText().toString();
				if (text != null && text != "") {
					tell(chatAgent.getComponentIdentifier().getName(), text);
				}
				editText.setText("");
			}
		}
	};
	private ArrayAdapter<String> arrayAdapter;
	
	private void addMessage(final String source, final String value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				arrayAdapter.add(source + ": " + value);
			}
		});
	}

	/**
	 * Tell something.
	 * 
	 * @param name
	 *            The name.
	 * @param text
	 *            The text.
	 */
	public void tell(final String name, final String text) {
		chatAgent.scheduleStep(new IComponentStep<Void>() {
			public IFuture<Void> execute(IInternalAccess ia) {
				IIntermediateFuture<IChatService> fut = ia
						.getServiceContainer().getRequiredServices(
								"chatservices");
				fut.addResultListener(new IIntermediateResultListener<IChatService>() {
					public void resultAvailable(Collection<IChatService> result) {
						// System.out.println("bulk");
						if (result != null) {
							for (Iterator it = ((Collection) result).iterator(); it
									.hasNext();) {
								IChatService cs = (IChatService) it.next();
								cs.hear(name, text);
							}
						}
					}

					public void exceptionOccurred(Exception exception) {
						// System.out.println("Chat service exception.");
						exception.printStackTrace();
					}

					public void intermediateResultAvailable(IChatService result) {
						// System.out.println("intermediate");
						result.hear(name, text);
					}

					public void finished() {
						// System.out.println("end");
					}
				});
				return IFuture.DONE;
			}
		});
	}
	
	
	private retainNonConfigurationInstance getRetainConfigObject() {
		Object object = getLastNonConfigurationInstance();
		if (object != null && object instanceof retainNonConfigurationInstance) {
			return (retainNonConfigurationInstance) object;
		} else {
			return new retainNonConfigurationInstance();
		}
	}
	
	private static class retainNonConfigurationInstance {
		public IExternalAccess extAcc;
		public String[] chatList;
	}
	
}
