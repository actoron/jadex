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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import de.unihamburg.vsis.jadexAndroid_test.BaseActivity;
import de.unihamburg.vsis.jadexAndroid_test.Helper;
import de.unihamburg.vsis.jadexAndroid_test.R;
import de.unihamburg.vsis.jadexAndroid_test.Startup;

public class MeasureActivity extends BaseActivity {
	private ListView agentsListView;
	private Button sendButton;
	protected IExternalAccess extAcc;
	private Button refreshButton;
	private HashMap<String, IChatService> receiverMap;

	private PerformanceResult perfRes;

	public static IExternalAccess chatAgent;

	private ArrayAdapter<IChatService> arrayAdapter;
	private TextView statusTextView;
	protected boolean measureComplete;
	protected long pingSentTime;
	protected IChatService measureTarget;
	protected String measureString;
	private TextView ownNameTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_activity);
		receiverMap = new HashMap<String, IChatService>();
		//perfRes = new PerformanceResult();

		ownNameTextView = findTextViewById(R.id.measure_activity_ownName);

		agentsListView = findListViewById(R.id.measure_activity_agentsListView);
		sendButton = findButtonById(R.id.measure_activity_sendButton);
		refreshButton = findButtonById(R.id.measure_activity_refreshButton);
		measureString = "";

		statusTextView = findTextViewById(R.id.measure_activity_statusTextView);

		arrayAdapter = new IChatServiceArrayAdapter(this);

		IChatService[] chatList = getRetainConfigObject().chatList;
		if (chatList != null) {
			for (int i = 0; i < chatList.length; i++) {
				arrayAdapter.add(chatList[i]);
			}
		}

		agentsListView.setStackFromBottom(false);
		agentsListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		agentsListView.setDividerHeight(0);
		agentsListView.setAdapter(arrayAdapter);

		extAcc = getRetainConfigObject().extAcc;

		if (extAcc == null) {
			IFuture<IExternalAccess> future = Startup
					.startBluetoothPlatform("Platform-"
							+ createRandomPlattformID());
			future.addResultListener(platformResultListener);
		}

		refreshButton.setOnClickListener(sendClickListener);
		measureComplete = true;
		agentsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (measureComplete) {
					perfRes = new PerformanceResult(PerformanceResult.byteLengths);
					int byteLen = perfRes.getNextByteLength();
					perfRes.newPingRun(byteLen);
					
					StringBuilder sb = new StringBuilder();
					Random random = new Random();
					for (int i = 0; i < byteLen; i++) {
						sb.append((char) (random.nextInt(78) + '0'));
					}
					
					measureString = sb.toString();
					measureComplete = false;
					IChatService item = (IChatService) agentsListView
							.getItemAtPosition(position);
					perfRes.fromDevice = chatAgent.getComponentIdentifier().getName();
					perfRes.toDevice = item.getIdentification();
					measureTarget = item;
					pingSentTime = System.nanoTime();
					item.hear(chatAgent.getComponentIdentifier().getName(),
							measureString);
				}
			}

		});

		registerForContextMenu(agentsListView);
		
		statusTextView.setOnClickListener(sendClickListener);
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
		if (chatAgent != null) {
			setUiListeners();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		retainNonConfigurationInstance o = new retainNonConfigurationInstance();
		o.extAcc = extAcc;
		o.chatList = new IChatService[arrayAdapter.getCount()];
		for (int i = 0; i < arrayAdapter.getCount(); i++) {
			o.chatList[i] = arrayAdapter.getItem(i);
		}

		return o;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.measure_activity_agentsListView) {
			// measureTarget = (IChatService) agentsListView.getSelectedItem();
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Choose Paket Size");
			String[] menuItems = new String[] { "32b", "512b", "1024b", "2048b", "10KiB", "20KiB" };
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int index = item.getItemId();
		int bytelen = 0;
		switch (index) {
		case 0:
			bytelen = 32;
			break;
		case 1:
			bytelen = 512;
			break;
		case 2:
			bytelen = 1024;
			break;
		case 3:
			bytelen = 2048;
			break;
		case 4:
			bytelen = 10*1024;
			break;
		case 5:
			bytelen = 20*1024;
			break;
		default:
			break;
		}

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < bytelen; i++) {
			sb.append((char) (random.nextInt(78) + '0'));
		}
		measureString = sb.toString();

		// pingSentTime = System.nanoTime();
		// measureTarget.hear(chatAgent.getComponentIdentifier().getName(),
		// measureString);
		return true;
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
									runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											ownNameTextView.setText("My Name is: "
													+ chatAgent
													.getComponentIdentifier()
													.getName());
											
										}
									});
									setUiListeners();
								}
							});
		}
	};

	private void setUiListeners() {
		sendButton.setOnClickListener(sendClickListener);
		IFuture<IChatService> step = chatAgent
				.scheduleStep(new IComponentStep<IChatService>() {
					@XMLClassname("addlistener")
					public IFuture<IChatService> execute(IInternalAccess ia) {
						Future<IChatService> ret = new Future<IChatService>();
						IService iService = ia.getServiceContainer()
								.getProvidedServices(IChatService.class)[0];
						ret.setResult((IChatService) iService);
						return ret;
					}
				});

		step.addResultListener(new DefaultResultListener<IChatService>() {
			public void resultAvailable(IChatService cs) {
				cs.addChangeListener(new IRemoteChangeListener() {
					public IFuture changeOccurred(final ChangeEvent event) {
						Future ret = new Future();
						// if(!isVisible())
						// {
						// ret.setException(new
						// RuntimeException("Gui closed."));
						// }
						// else
						// {
						// addMessage((String)event.getSource(),
						// (String)event.getValue());
						// ret.setResult(null);
						// }
						// addMessage((String)event.getSource(),
						// (String)event.getValue());

						receivedMessage((String) event.getSource(),
								(String) event.getValue());

						// tell(chatAgent.getComponentIdentifier().getName(),
						// "pong");
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
				// tell(chatAgent.getComponentIdentifier().getName(), "ping");
			} else if (v == refreshButton) {
				measureComplete = true;
				refreshAvailableChatServices();
			} else if (v == statusTextView) {
				if (perfRes != null) {
					final String result = perfRes.toString();
					Log.i(Helper.LOG_TAG, result);
					runOnUiThread(new Runnable() {
						public void run() {
							statusTextView.setText(result);
						}
					});
				}
			}
		}
	};

	private void refreshAvailableChatServices() {
		final ArrayList<IChatService> newChatUsers = new ArrayList<IChatService>();
		if (chatAgent == null) {
			return;
		}
		chatAgent.scheduleStep(new IComponentStep<Void>() {
			public IFuture<Void> execute(IInternalAccess ia) {
				IIntermediateFuture<IChatService> fut = ia
						.getServiceContainer().getRequiredServices(
								"chatservices");
				fut.addResultListener(new IIntermediateResultListener<IChatService>() {
					public void resultAvailable(
							final Collection<IChatService> result) {
						for (IChatService iChatService : result) {
							// availableChatAgents.add(iChatService.getChatName());
							newChatUsers.add(iChatService);
						}
					}

					public void exceptionOccurred(Exception exception) {
						exception.printStackTrace();
					}

					public void intermediateResultAvailable(
							final IChatService result) {
						// availableChatAgents.add(result.getChatName());
						// userListAdapter.add(new
						// ChatUser(result.getChatName(),
						// result.getPlattformName()));
						newChatUsers.add(result);
						;
					}

					public void finished() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								arrayAdapter.clear();
								for (IChatService agent : newChatUsers) {
									String identification = agent
											.getIdentification();
									if (!identification
											.equals(chatAgent
													.getComponentIdentifier()
													.getName())) {
										arrayAdapter.add(agent);
										receiverMap.put(identification, agent);
									} else {
										// arrayAdapter.add("I am: " +
										// chatAgent.getComponentIdentifier().getName());
									}
								}
							}
						});
					}
				});
				return IFuture.DONE;
			}
		});
	}

	private void receivedMessage(String source, String value) {
		Log.i(Helper.LOG_TAG, "received <" + value + "> from " + source);
		if (!value.equals("pong")) {
			Log.i(Helper.LOG_TAG, "answering with pong to " + source);
			tell(chatAgent.getComponentIdentifier().getName(), (String) source,
					"pong");
		} else {
			if (!measureComplete) {
				long arrivalTime = System.nanoTime();
				final int roundTripTime = (int) ((arrivalTime - pingSentTime) / 1000000);
				
				perfRes.addDelay(roundTripTime);

				final int av = perfRes.getCurrentPingRunAverage();
				final int count = perfRes.getCurrentPingRunCount();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						statusTextView.setText("Target: " + measureTarget.getIdentification() + " size: " + measureString.length());
						statusTextView.append("\nRoundtriptime is: "
								+ roundTripTime + "ms");
						statusTextView.append("\nAverage over " + count
								+ " is: " + av + "ms");
					}
				});

				if (count < 100) {
					pingSentTime = System.nanoTime();
					measureTarget.hear(chatAgent.getComponentIdentifier()
							.getName(), measureString);
				} else {
					if (!perfRes.isComplete()) {
						int nextByteLength = perfRes.getNextByteLength();
						perfRes.newPingRun(nextByteLength);
						StringBuilder sb = new StringBuilder();
						Random random = new Random();
						for (int i = 0; i < nextByteLength; i++) {
							sb.append((char) (random.nextInt(78) + '0'));
						}
						measureString = sb.toString();
						
						pingSentTime = System.nanoTime();
						measureTarget.hear(chatAgent.getComponentIdentifier()
								.getName(), measureString);
					} else {
						measureTarget = null;
						measureComplete = true;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								statusTextView.setText(perfRes.toString());
							}
						});
					}
				}
				
				
			}
		}
	}

	/**
	 * Tell something.
	 * 
	 * @param ownName
	 *            The name.
	 * @param text
	 *            The text.
	 */
	public void tell(final String ownName, final String receiverName,
			final String text) {

		IChatService cs = receiverMap.get(receiverName);
		if (cs != null) {
			// already know the receiver CS
			cs.hear(ownName, text);
			return;
		}

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
								String identification = cs.getIdentification();
								if (identification.equals(receiverName)) {
									cs.hear(ownName, text);
									receiverMap.put(identification, cs);
									Log.i(Helper.LOG_TAG, "sent <" + text
											+ "> to " + receiverName);
								}
							}
						}
					}

					public void exceptionOccurred(Exception exception) {
						// System.out.println("Chat service exception.");
						exception.printStackTrace();
					}

					public void intermediateResultAvailable(IChatService result) {
						// System.out.println("intermediate");
						result.hear(ownName, text);
						receiverMap.put(result.getIdentification(), result);
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
		public IChatService[] chatList;
	}

}
