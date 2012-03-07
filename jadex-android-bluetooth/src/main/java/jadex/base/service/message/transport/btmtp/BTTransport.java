package jadex.base.service.message.transport.btmtp;

import jadex.android.JadexAndroidActivity.AndroidContextChangeListener;
import jadex.android.bluetooth.JadexBluetoothActivity;
import jadex.android.bluetooth.exceptions.ActivityIsNotJadexBluetoothActivityException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.service.IBTP2PMessageCallback;
import jadex.android.bluetooth.service.IConnectionServiceConnection;
import jadex.android.bluetooth.util.Helper;
import jadex.base.service.message.ISendTask;
import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.IResultCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * The Bluetooth transport for sending messages over Bluetooth.
 * Uses the Bluetooth Connection Service to send and receive Messages.
 * 
 * It will start the Service if its not already running.
 */
public class BTTransport implements ITransport, AndroidContextChangeListener {
	// -------- constants --------

	/** The schema names. */
	public final static String[] SCHEMAS = new String[]{"bt-mtp://"};

	/** Constant for asynchronous setting. */
	public final static String ASYNCHRONOUS = "asynchronous";

	/** How long to keep output connections alive (5 min). */
	protected final static int MAX_KEEPALIVE = 300000;

	/** The prolog size. */
	protected final static int PROLOG_SIZE = 4;
	
	/** The length of a valid address, without schema name */
	protected final static int ADDRESS_LENGTH = 17; 
	
	// -------- attributes --------

	/** The platform. */
	protected IServiceProvider container;

	/** The addresses. */
	protected String[] addresses;

	/** The library service. */
	protected ILibraryService libservice;

	protected IConnectionServiceConnection binder;

	public IBTP2PMessageCallback msgCallback = new IBTP2PMessageCallback.Stub() {

		@Override
		public void messageReceived(final byte[] data) throws RemoteException {
			receiveMessage(data);
		}
	};

	private BTServiceConnection sc;

	protected ClassLoader classLoader;

	private Context context;

	private boolean started;

	// -------- constructors --------

	/**
	 * Init the transport.
	 */
	public BTTransport(final IServiceProvider container) {
		this.container = container;
		JadexBluetoothActivity.addContextChangeListener(this);
	}

	/**
	 * Start the transport.
	 */
	public IFuture<Void> start() {
		started = true;
		final Future<Void> ret = new Future<Void>();
		try {
			if (context != null && binder == null) {
				Intent intent = new Intent(context, ConnectionService.class);
				Log.d(Helper.LOG_TAG, "(BTTransport) Trying to bind BT Service...");
				sc = new BTServiceConnection(ret);
				context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
			} else {
				throw new ActivityIsNotJadexBluetoothActivityException();
			}

		} catch (Exception e) {
			// e.printStackTrace();
			ret.setException(new RuntimeException(
					"(BTTransport) Transport initialization error: " + e.getMessage()));
			// throw new
			// RuntimeException("Transport initialization error: "+e.getMessage());
		}
		return ret;
	}

	/**
	 * Perform cleanup operations (if any).
	 */
	public IFuture shutdown() {
		started = false;
		if (binder != null && context != null) {
			try {
				Log.d(Helper.LOG_TAG, "(BTTransport) Stopping autoconnect...");
				binder.stopAutoConnect();
				binder.stopBTServer();
			} catch (RemoteException e) {
				e.printStackTrace();
			} finally {
				binder = null;
				Log.d(Helper.LOG_TAG, "(BTTransport) Unbinding Service...");
				context.unbindService(sc);
			}
		}
		return new Future(null);
	}
	
	@Override
	public void onContextCreate(Context ctx) {
		context = ctx;
		if (started) {
			Intent intent = new Intent(context, ConnectionService.class);
			Log.d(Helper.LOG_TAG, "(BTTransport) Trying to bind BT Service...");
			context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
		}
	}
	
	@Override
	public void onContextDestroy(Context ctx) {
		if (started && context == ctx && binder != null) {
			context.unbindService(sc);
			context = null;
		}
	}

	// -------- methods --------

	/**
	 * Send a message.
	 * 
	 * 	@param address The address to send to.
	 *  @param task A task representing the message to send.
	 *  
	 */
	public void sendMessage(String address, final ISendTask task) {

		IResultCommand<IFuture<Void>, Void> send = new IResultCommand<IFuture<Void>, Void>() {

			@Override
			public IFuture<Void> execute(Void args) {
				IFuture<Void>	ret	= null;
				
				IComponentIdentifier[] receivers = task.getReceivers();

				// Fetch all addresses
				Set<String> addresses = new LinkedHashSet<String>();
				for (int i = 0; i < receivers.length; i++) {
					String[] raddrs = receivers[i].getAddresses();
					for (int j = 0; j < raddrs.length; j++) {
						if (isApplicable(raddrs[j])) {
							addresses.add(raddrs[j]);
						}
					}
				}

				// Iterate over all different addresses and try to send
				// to missing and appropriate receivers
				String[] addrs = addresses.toArray(new String[addresses.size()]);

				boolean delivered = false;
				BluetoothMessage bluetoothMessage;
				ByteArrayOutputStream stream = new ByteArrayOutputStream(task.getProlog().length
						+ task.getData().length);

				try {
					stream.write(task.getProlog());
					stream.write(task.getData());
				} catch (IOException e1) {
					Log.e(Helper.LOG_TAG, "Could not encode Message: " + e1.toString());
				}
				byte[] msgData = stream.toByteArray();
				for (int i = 0; !delivered && i < addrs.length; i++) {

					bluetoothMessage = new BluetoothMessage(addrs[i], msgData, DataPacket.TYPE_DATA);

					try {
						binder.sendMessage(bluetoothMessage);
						ret	= IFuture.DONE;
					} catch (RemoteException e) {
						ret	= new Future<Void>(new RuntimeException("Send failed: "+bluetoothMessage));
						e.printStackTrace();
					}
				}
				
				if(ret==null)
				{
					ret	= new Future<Void>(new RuntimeException("No working connection."));			
				}
				
				return ret;
			}
		};
		
		task.ready(send);

	}
	
	protected void receiveMessage(final byte[] data) {
		final Future<IThreadPoolService> fut = new Future<IThreadPoolService>();
		SServiceProvider.getService(container, IThreadPoolService.class,
				RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
				new DelegationResultListener<IThreadPoolService>(fut) {
					public void customResultAvailable(IThreadPoolService result) {
						fut.setResult(null);
						final IThreadPoolService tp = result;
						tp.execute(new Runnable() {
							public void run() {
								BTTransport.this
										.deliverMessage(data);
							}
						});
					}
				});
	}

	/**
	 * Returns the prefix of this transport
	 * 
	 * @return Transport prefix.
	 */
	@Override
	public String[] getServiceSchemas() {
		return SCHEMAS;
	}

	/**
	 * Get the adresses of this transport.
	 * 
	 * @return An array of strings representing the addresses of this message
	 *         transport mechanism.
	 */
	public String[] getAddresses() {
		return addresses;
	}

	// -------- helper methods --------

	/**
	 * Get the address of this transport.
	 * 
	 * @param hostname
	 *            The hostname.
	 * @return <scheme><hostname>
	 */
	public String getAddress(String hostname) {
		return getServiceSchemas() + hostname;
	}

//	protected MessageEnvelope decodeMessage(byte[] data) {
//		
//		Log.i(Helper.LOG_TAG, "Received message byte array is: " + data.length);
//		MessageEnvelope ret = null;
//
//		// Calculate message size by reading the first 4 bytes
//		// Read here is always a blocking call.
//		int msg_size;
//		int pos = 0;
//		byte[] codec_ids = new byte[(int) data[0] & 0xFF];
//
//		pos++;
//		
//		for (int i = 0; i < codec_ids.length; i++) {
//			codec_ids[i] = data[pos];
//			pos++;
//		}
//
//		msg_size = SUtil.bytesToInt(new byte[] { data[pos], data[pos + 1],
//				data[pos + 2], data[pos + 3] });
//
//		pos += 4;
//
//		// readByte() << 24 | readByte() << 16 | readByte() << 8 | readByte();
//		// System.out.println("reclen: "+msg_size);
//		msg_size = msg_size - BTTransport.PROLOG_SIZE - codec_ids.length - 1; // Remove
//																				// prolog.
//		if (msg_size > 0) {
////			byte[] rawMsg = Arrays.copyOfRange(data, pos, data.length - 1);
//			byte[] rawMsg = new byte[data.length - pos];
////			System.out.println("rawMsglength: " + rawMsg.length);
//			for (int i = 0; i < msg_size; pos++, i++ ) {
//				rawMsg[i] = data[pos];
//			}
//			
//			Object tmp = rawMsg;
//			for (int i = codec_ids.length - 1; i > -1; i--) {
//				ICodec dec = codecfac.getCodec(codec_ids[i]);
//				tmp = dec.decode((byte[]) tmp, classLoader);
//			}
//			ret = (MessageEnvelope) tmp;
//		}
//
//		return ret;
//	}

	/**
	 * Deliver messages to local message service for dispatching to the
	 * components.
	 * 
	 * @param con
	 *            The connection.
	 */
	protected IFuture deliverMessage(final byte[] rawmsg) {
		final Future ret = new Future();
		SServiceProvider.getService(container, IMessageService.class,
				RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
				new DefaultResultListener<IMessageService>() {
					public void resultAvailable(IMessageService ms) {
//						ms.deliverMessage(msg.getMessage(), msg.getTypeName(),
//								msg.getReceivers());
						ms.deliverMessage(rawmsg);
						ret.setResult(null);
					}
				});
		return ret;
	}
	
//	/**
//	 *  Get the address of this transport.
//	 *  @param hostname The hostname.
//	 *  @param port The port.
//	 *  @return <scheme>:<hostname>:<port>
//	 */
//	protected String getAddress(String hostname, int port)
//	{
//		return getServiceSchema()+hostname+":"+port;
//	}
	
	/**
	 *  Test if a transport is applicable for the target address.
	 *  
	 *  @return True, if the transport is applicable for the address.
	 */
	@Override
	public boolean isApplicable(String address) {
		boolean ret = false;
		for (String schema : SCHEMAS) {
			if (address.startsWith(schema)) {
				int schemalen = address.length();
				if (address.length() == schemalen + ADDRESS_LENGTH) {
					ret = true;
				}
			}
		}
		return ret;
	}

	class BTServiceConnection implements ServiceConnection {

		private Future fut;

		public BTServiceConnection(Future fut) {
			this.fut = fut;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			binder = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			binder = IConnectionServiceConnection.Stub.asInterface(arg1);

			Log.d(Helper.LOG_TAG,
					"(BTTransport) Service bound! Retrieving Classloader...");

			SServiceProvider.getService(container, ILibraryService.class,
					RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
					new DelegationResultListener<ILibraryService>(
							new Future<ILibraryService>()) {
						public void customResultAvailable(ILibraryService result) {
							libservice = (ILibraryService) result;
							libservice.getClassLoader(null).addResultListener(
									new DefaultResultListener<ClassLoader>() {

										@Override
										public void resultAvailable(
												ClassLoader result) {
											BTTransport.this.classLoader = result;

											Log.d(Helper.LOG_TAG,
													"(BTTransport) Classloader set. Starting Autoconnect...");

											try {
												addresses = new String[] { getAddress(binder
														.getBTAddress()) };
												binder.registerMessageCallback(msgCallback);
												binder.startAutoConnect();
												fut.setResult(true);
											} catch (RemoteException e) {
												e.printStackTrace();
											}
										}
									});
						}
					});

//			SServiceProvider.getService(container, IMessageService.class,
//					RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
//					new DelegationResultListener<IMessageService>(
//							new Future<IMessageService>()) {
//						@Override
//						public void customResultAvailable(IMessageService ms) {
//							BTTransport.this.codecfac = (CodecFactory) ms
//									.getCodecFactory();
//							Log.d(Helper.LOG_TAG,
//									"(BTTransport) CodecFactory set.");
//						}
//					});
		}
	}
}
