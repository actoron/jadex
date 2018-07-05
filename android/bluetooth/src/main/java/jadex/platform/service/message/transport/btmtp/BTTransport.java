package jadex.platform.service.message.transport.btmtp;

import jadex.android.AndroidContextManager;
import jadex.android.AndroidContextManager.AndroidContextChangeListener;
import jadex.android.bluetooth.exceptions.ActivityIsNotJadexBluetoothActivityException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.service.IBTP2PMessageCallback;
import jadex.android.bluetooth.service.IConnectionServiceConnection;
import jadex.android.bluetooth.util.Helper;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransport;
import jadex.platform.service.transport.ITransportHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class BTTransport implements ITransport<BTChannel>, AndroidContextChangeListener {
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
	protected IInternalAccess container;

	/** The addresses. */
//	protected String[] addresses;

	/** The library service. */
	protected ILibraryService libservice;

	protected IConnectionServiceConnection binder;

	public IBTP2PMessageCallback msgCallback = new IBTP2PMessageCallback.Stub() {

		@Override
		public void messageReceived(String remoteAddress, byte[] data) throws RemoteException {
			receiveMessage(remoteAddress, data);
		}

	};

	private BTServiceConnection sc;

	protected ClassLoader classLoader;

	private Context context;

	private boolean started;
	private ITransportHandler<BTChannel> handler;
	private Map<String, BTChannel> connections;

	// -------- constructors --------

	/**
	 * Init the transport.
	 */
	public BTTransport(IInternalAccess container) {
		this.container = container;
		this.connections = new HashMap<>();
		AndroidContextManager.getInstance().addContextChangeListener(this);
	}

	/**
	 * Start the transport.
	 */
	@Override
	public void init(ITransportHandler<BTChannel> handler) {
		this.handler = handler;

		started = true;

		try {
			if (context != null && binder == null) {
				Intent intent = new Intent(context, ConnectionService.class);
				Log.d(Helper.LOG_TAG, "(BTTransport) Trying to bind BT Service...");
				sc = new BTServiceConnection();
				sc.init();
				context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
			} else {
				throw new ActivityIsNotJadexBluetoothActivityException();
			}

		} catch (Exception e) {
			throw new RuntimeException(
					"(BTTransport) Transport initialization error: " + e.getMessage());
		}
	}

	/**
	 * Perform cleanup operations (if any).
	 */
	public void shutdown() {
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
	}

	@Override
	public String getProtocolName() {
		return "bt";
	}

	@Override
	public IFuture<Integer> openPort(int port) {
		return new Future(0); // TODO start listening here?
	}

	@Override
	public IFuture<BTChannel> createConnection(String address, IComponentIdentifier target) {
		Future<BTChannel> res = new Future<>();

		BTChannel btChannel = new BTChannel(address, target);

		this.connections.put(address, btChannel);
		System.out.println("Adding bt connection: " + address);
		res.setResult(btChannel);
		return res;
	}

	@Override
	public void closeConnection(BTChannel btChannel) {
		System.out.println("removing bt connection: " + btChannel.getAddress());
		this.connections.remove(btChannel.getAddress());
	}

	@Override
	public IFuture<Void> sendMessage(final BTChannel btChannel, final byte[] header, final byte[] body) {
		IFuture<Void>	ret	= null;

		IComponentIdentifier receiver = btChannel.getReceiver();

		boolean delivered = false;
		BluetoothMessage bluetoothMessage;
		ByteArrayOutputStream stream = new ByteArrayOutputStream(header.length
				+ body.length);

		try {
			stream.write(header);
			stream.write(body);
		} catch (IOException e1) {
			Log.e(Helper.LOG_TAG, "Could not encode Message: " + e1.toString());
		}
		byte[] msgData = stream.toByteArray();
//				for (int i = 0; !delivered && i < addrs.length; i++) {

		bluetoothMessage = new BluetoothMessage(btChannel.getAddress(), msgData, DataPacket.TYPE_DATA);

		try {
			binder.sendMessage(bluetoothMessage);
			ret	= IFuture.DONE;
		} catch (RemoteException e) {
			ret	= new Future<Void>(new RuntimeException("Send failed: "+bluetoothMessage));
			e.printStackTrace();
		}

//				}

		if(ret==null)
		{
			ret	= new Future<Void>(new RuntimeException("No working connection."));
		}

		return ret;
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


	protected void receiveMessage(final String remoteAddress, final byte[] data) {
		final Future<IThreadPoolService> fut = new Future<IThreadPoolService>();
		container.getFeature(IRequiredServicesFeature.class).getService(IThreadPoolService.class).addResultListener(
				new DelegationResultListener<IThreadPoolService>(fut) {
					public void customResultAvailable(IThreadPoolService result) {
						fut.setResult(null);
						final IThreadPoolService tp = result;
						tp.execute(new Runnable() {
							public void run() {
								List<byte[]> bytes = SUtil.splitData(data);
								handler.messageReceived(connections.get(remoteAddress), bytes.get(0), bytes.get(1));
							}
						});
					}
				});
	}



	// -------- helper methods --------


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
//	@Override
//	public boolean isApplicable(String address) {
//		boolean ret = false;
//		for (String schema : SCHEMAS) {
//			if (address.startsWith(schema)) {
//				int schemalen = address.length();
//				if (address.length() == schemalen + ADDRESS_LENGTH) {
//					ret = true;
//				}
//			}
//		}
//		return ret;
//	}

	class BTServiceConnection implements ServiceConnection {

		Future<Void> future;
		public BTServiceConnection() {
			future = new Future<>();
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

			container.getFeature(IRequiredServicesFeature.class).getService(ILibraryService.class).addResultListener(
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
//												addresses = new String[] { getAddress(binder
//														.getBTAddress()) };
												binder.registerMessageCallback(msgCallback);
												binder.startAutoConnect();
												future.setResult(null);
											} catch (RemoteException e) {
												e.printStackTrace();
												future.setException(e);
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

		public void init() {
			future.get();
		}
	}

}
