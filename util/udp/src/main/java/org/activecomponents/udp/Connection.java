package org.activecomponents.udp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.activecomponents.udp.asymciphers.KeyExchangeGenerator;
import org.spongycastle.crypto.digests.SHA512Digest;

public class Connection
{
	/** Debug flag. */
	protected static final boolean DEBUG = false;
	
	// LEVEL 0/outer packet types 
	/** Packet type for initiating a connection. */
	protected static final byte SYN = 0;
	
	/** Response to a SYN, finalizing the key exchange. */
	protected static final byte SYNACK = 1;
	
	/** Response to a SYNACK, confirming readiness by the initiator. */
	protected static final byte RDY = 2;
	
	/** Data package after connection handshake. */
	public static final byte DATA = 16;
	
	// LEVEL 1/inner packet types
	
	/** Keepalive packet. */
	protected static final byte KEEPALIVE = 0;
	
	/** Packet type for closing a connection. */
	protected static final byte FIN = 1;
	
	/** Packet type for confirming a closed connection. */
	protected static final byte FINACK = 4;
	
	/** Acknowledgement of a message part. */
	protected static final byte ACK = 16;
	
	/** Packet with data requiring no confirmation. */
	protected static final byte PACKET = 17;
	
	/** Part of a message. */
	protected static final byte MESSAGE_PART = 18;
	
	/** Part of an internal message. */
	protected static final byte INT_MESSAGE_PART = 19;
	
	// Level 3/internal message types
	
	/** Message type used to rekey the connection. */
	protected static final byte REKEY = 0;
	
	/** The socket. */
	protected DatagramSocket socket;
	
	/** Listeners for new connections. */
	protected List<IConnectionListener> connectionlisteners;
	
	/** List of externally-provided key verifiers. */
	protected List<IKeyVerifier> keyverifiers;
	
	/** The thread executor. */
	protected IThreadExecutor texec;
	
	/** The remote peer this connection targets. */
	protected SocketAddress remoteaddr;
	
	/** Queue of scheduled messages. */
	protected Queue<OutgoingMessage> messagequeue = new ConcurrentLinkedQueue<OutgoingMessage>();
	
	/** Parts of messages that have been queued and awaiting transmission. */
	protected LinkedList<MessagePart> queuedmessageparts = new LinkedList<MessagePart>();
	
	/** Message parts that have been sent and await confirmation. */
	protected Map<Long, MessagePart> sentwindow = Collections.synchronizedMap(new HashMap<Long, MessagePart>());
	
	/** Incoming messages */
	protected Map<Long, IncomingMessage> messages = new HashMap<Long, IncomingMessage>();
	
	/** Current window size. */
	protected volatile int windowsize = 1;
	
	/** Window size threshold. */
	protected volatile int windowthres = 1000;
	
	/** Current latency. */
	protected volatile long latency = STunables.INITIAL_LATENCY;
	
	/** Insecure PRNG. */
	protected Random insecrandom = new Random();
	
	/** Arrived packets that are used to manage the connection */
	protected LinkedBlockingQueue<byte[]> internalpackets = new LinkedBlockingQueue<byte[]>();
	
	/** User packets with a payload. */
	protected LinkedBlockingQueue<byte[]> userpackets;
	
	/** Listener for incoming messages and packets. */
	protected IIncomingListener inclistener;
	
	/** Key package built during connection establishment. */
	protected volatile byte[] keypackage;
	
	/** Management object for symmetric ciphers. */
	protected SymCipherManagement ciphermgmt;
	
	protected IUdpCallback<Boolean> rekeycallback;
	
	/** Flag that defines if the connection is currently active. */
	protected volatile boolean connected;
	
	/** Timestamp of the last received keepalive packet. */
	protected volatile long keepalivereceived;
	
	/** Timestamp of the last sent keepalive packet. */
	protected volatile long keepalivesent;
	
	public Connection(DatagramSocket socket, List<IConnectionListener> connectionlisteners, SocketAddress remoteaddr, IThreadExecutor texec, KeyExchangeGenerator keyexgen, int symcipherclassid, List<IKeyVerifier> keyverifiers, IIncomingListener inclistener)
	{
		this.rekeycallback = new IUdpCallback<Boolean>()
		{
			public void resultAvailable(Boolean result)
			{
				synchronized (ciphermgmt)
				{
					if (ciphermgmt.getKx() != null)
					{
						byte cipherid = ciphermgmt.getActiveSymCipherId();
						++cipherid;
						ciphermgmt.setActiveSymCipherId(cipherid);
						cipherid -= (STunables.MAX_KEY_RETENTION + 1);
						ciphermgmt.destroySymCipherSuite(cipherid);
						ciphermgmt.refreshRemainingBytes();
						ciphermgmt.destroyKx();
						//System.out.println("Rekey confirmed: " + ciphermgmt.getActiveSymCipherId());
						//System.out.println("Keys: " + Arrays.toString(symciphers));
					}
				}
			}
		};
		this.socket = socket;
		this.connectionlisteners = connectionlisteners;
		this.remoteaddr = remoteaddr;
		this.texec = texec;
		this.keyverifiers = keyverifiers;
		this.connected = false;
		this.ciphermgmt = new SymCipherManagement(symcipherclassid, keyexgen);
		if (inclistener != null)
		{
			this.inclistener = inclistener;
		}
		else
		{
			this.userpackets = new LinkedBlockingQueue<byte[]>();
		}
	}
	
	/**
	 *  Checks if the connection is working.
	 *  @return True, if data can be send.
	 */
	public boolean isConnected()
	{
		return connected;
	}
	
	/**
	 *  Sends a packet. Maximum size is at least 60000 bytes, delivery is not guaranteed.
	 *  @param data The data being send.
	 */
	public void sendPacket(byte[] data)
	{
		if (!connected)
		{
			throw new IllegalStateException("Connection not established.");
		}
		
		byte[] packet = new byte[data.length + 9];
		packet[0] = PACKET;
		System.arraycopy(data, 0, packet, 1, data.length);
		synchronized (ciphermgmt)
		{
			SUdpUtil.longIntoByteArray(packet, 1, ciphermgmt.getActiveSymCipherSuite().getPacketidcounter().getAndIncrement());
			packet = createUserPacket(packet);
		}
		try
		{
			DatagramPacket dgp = new DatagramPacket(packet, packet.length, remoteaddr);
			socket.send(dgp);
		}
		catch (IOException e)
		{
			new RuntimeException(e);
		}
	}
	
	/**
	 *  Send a message. Size is not limited, delivery is guaranteed unless a failure occurs.
	 *  @param msg The message.
	 */
	public void sendMessage(final byte[] msg)
	{
		sendMessage(msg, null);
	}
	
	/**
	 *  Send a message. Size is not limited, delivery is guaranteed unless a failure occurs.
	 *  @param msg The message.
	 *  @param callback Callback invoked with result of the send operation,
	 *  				true if the message was received, false on failure.
	 */
	public void sendMessage(final byte[] msg, IUdpCallback<Boolean> callback)
	{
		OutgoingMessage outgoingmsg = new OutgoingMessage(msg, callback);
		synchronized(messagequeue)
		{
			messagequeue.offer(outgoingmsg);
			messagequeue.notifyAll();
		}
	}
	
	/**
	 *  Receives a message / packet (blocking).
	 * 
	 *  @return The message/packet.
	 */
	public byte[] receive()
	{
		try
		{
			return userpackets.take();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  Receives a message / packet (blocking).
	 * 
	 *  @return The message/packet.
	 */
	public byte[] receive(long timeout)
	{
		try
		{
			return userpackets.poll(timeout, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  Returns the remote address.
	 *  
	 *  @return Remote address.
	 */
	public SocketAddress getRemoteAddress()
	{
		return remoteaddr;
	}
	
	/**
	 *  Returns direct access to the message queue.
	 *  @return The message queue.
	 */
//	public Queue<byte[]> getMessageQueue()
//	{
//		return messagequeue;
//	}
	
	/**
	 *  Method for initiating the connection.
	 *  
	 *  @param handler The handler.
	 */
	protected void initiate(UdpConnectionHandler handler)
	{
		connected = false;
		internalpackets.clear();
		try
		{
			byte[] pubkey;
			int cipherclassid = Integer.MIN_VALUE;
			synchronized (ciphermgmt)
			{
				ciphermgmt.createKx();
				pubkey = ciphermgmt.getKx().getPublicKey();
				cipherclassid = ciphermgmt.getCipherClassId();
			}
			
			byte[] pubkeysig = keyverifiers.get(0).sign(pubkey);
			
			byte[] snd = new byte[pubkey.length + (pubkeysig == null ? 0 : pubkeysig.length) + 11];
			snd[0] = SYN;
			SUdpUtil.intIntoByteArray(snd, 1, STunables.PROTOCOL_VERSION);
			SUdpUtil.intIntoByteArray(snd, 5, cipherclassid);
			SUdpUtil.shortIntoByteArray(snd, 9, (short) pubkey.length);
			System.arraycopy(pubkey, 0, snd, 11, pubkey.length);
			if (pubkeysig != null && pubkeysig.length > 0)
				System.arraycopy(pubkeysig, 0, snd, 11 + pubkey.length, pubkeysig.length);
			DatagramPacket dgp = new DatagramPacket(snd, snd.length, remoteaddr);
			
			byte[] rcv = null;
			byte[] symkey = null;
			int count = STunables.SYN_RESEND_TRIES;
			byte[] remotepubkey = null;
			while (symkey == null && count > 0)
			{
				socket.send(dgp);
				rcv = internalpackets.poll(STunables.SYN_RESEND_DELAY, TimeUnit.MILLISECONDS);
//				System.out.println("SYNACK? " + rcv + " " + remoteaddr);
				if (rcv != null && rcv.length > 4 && rcv[0] == SYNACK)
				{
					remotepubkey = extractAndVerifyPubKey(pubkey, rcv, 1);
					if (remotepubkey == null)
					{
						return;
					}
					
					synchronized (ciphermgmt)
					{
						symkey = ciphermgmt.getKx().generateSymKey(remotepubkey);
						ciphermgmt.destroyKx();
					}
				}
				--count;
			}
			
			if (count == 0 || symkey == null)
			{
				//System.out.println("Failed: " + symkey + " " + count);
				return;
			}
			
			
			synchronized (ciphermgmt)
			{
				ciphermgmt.createSymCipherSuite((byte) 0, symkey);
				ciphermgmt.refreshRemainingBytes();
			}
			
			
			keypackage = buildSignedKeyPackage(pubkey, remotepubkey);
			snd = new byte[1 + keypackage.length];
			snd[0] = RDY;
			System.arraycopy(keypackage, 0, snd, 1, keypackage.length);
			dgp = new DatagramPacket(snd, snd.length, remoteaddr);
			
			connectionEstablished(handler, dgp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
//		System.out.println("DONE INITIATE: " + connected);
		synchronized (this)
		{
			this.notifyAll();
		}
		handler.fireConnectionEstablished(this);
//		sendLoop();
	}
	
	/**
	 *  Response actions for an attempt to initate a connection.
	 *  
	 *  @param initial Initiation packaet data received that caused the response activity.
	 *  @param handler The handler.
	 */
	protected void respond(byte[] initial, UdpConnectionHandler handler)
	{
		try
		{
			internalpackets.clear();
			int cipherclassid;
			byte[] pubkey;
			synchronized (ciphermgmt)
			{
				 cipherclassid = ciphermgmt.getCipherClassId();
				 ciphermgmt.createKx();
				 pubkey = ciphermgmt.getKx().getPublicKey();
			}
			
			int remoteversion = SUdpUtil.intFromByteArray(initial, 1);
			int remotecipher = SUdpUtil.intFromByteArray(initial, 5);
			if (remoteversion != STunables.PROTOCOL_VERSION ||
				remotecipher != cipherclassid)
			{
				System.err.println("Protocol or cipher mismatch, remote P:" + remoteversion + " C:" + remotecipher +
						", local P:" + STunables.PROTOCOL_VERSION + " C:" + cipherclassid);
				return;
			}
			int unverifiedremotepubkeysize = SUdpUtil.shortFromByteArray(initial, 9);
			byte[] unverifiedremotepubkey = new byte[unverifiedremotepubkeysize];
			System.arraycopy(initial, 11, unverifiedremotepubkey, 0, unverifiedremotepubkeysize);
			
			keypackage = buildSignedKeyPackage(pubkey, unverifiedremotepubkey);
			byte[] snd = new byte[keypackage.length + 1];
			snd[0] = SYNACK;
			System.arraycopy(keypackage, 0, snd, 1, keypackage.length);
			DatagramPacket dgp = new DatagramPacket(snd, snd.length, remoteaddr);
			
//			System.out.println("Waiting for RDY");
			boolean loop = true;
			byte count = 5;
			
			byte[] symkey = null;
			while (loop && count > 0)
			{
//				System.out.println("Sending SYNACK");
				socket.send(dgp);
				byte[] rcv = internalpackets.poll(5000, TimeUnit.MILLISECONDS);
//				System.out.println("RDY RCV: " + rcv);
//				if (rcv != null)
//				{
//					System.out.println(rcv[0]);
//				}
				if (rcv != null && rcv.length > 1 && rcv[0] == RDY)
				{
					byte[] remotepubkey = extractAndVerifyPubKey(pubkey, rcv, 1);
					if (remotepubkey != null)
					{
						synchronized (ciphermgmt)
						{
							symkey = ciphermgmt.getKx().generateSymKey(remotepubkey);
							ciphermgmt.createSymCipherSuite((byte) 0, symkey);
							ciphermgmt.refreshRemainingBytes();
							ciphermgmt.destroyKx();
						}
						loop = false;
					}
				}
				--count;
			}
			
			synchronized (ciphermgmt)
			{
				if (count == 0 || ciphermgmt.getActiveSymCipherSuite() == null)
				{
	//				System.out.println("RDY FAIL");
					return;
				}
			}
			
//			System.out.println("RDY, signaling...");
			connectionEstablished(handler, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
//		System.out.println("DONE RESPOND: " + connected);
		sendLoop();
	}
	
	/**
	 *  Called when the connection has been established.
	 *  
	 *  @param confirmation Confirmation packet.
	 */
	protected void connectionEstablished(UdpConnectionHandler handler, DatagramPacket confirmation)
	{
		if (DEBUG)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					int y = -1;
					
					JFrame dframe = new JFrame("Connection Debug Info " + socket.getLocalAddress().toString() + " " + socket.getLocalPort());
					dframe.getContentPane().setLayout(new GridBagLayout());
					
					dframe.getContentPane().add(new JLabel("Remote:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					dframe.getContentPane().add(new JLabel(remoteaddr.toString()), new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Internal Packet Queue:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel intpackq = new JLabel();
					dframe.getContentPane().add(intpackq, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("User Packet Queue:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel userpackq = new JLabel();
					dframe.getContentPane().add(userpackq, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Packet ID:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel partid = new JLabel();
					dframe.getContentPane().add(partid, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Message ID:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel msgid = new JLabel();
					dframe.getContentPane().add(msgid, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Latency:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel ltncy = new JLabel();
					dframe.getContentPane().add(ltncy, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Msg Queue:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel msgq = new JLabel();
					dframe.getContentPane().add(msgq, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Part Queue:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel partq = new JLabel();
					dframe.getContentPane().add(partq, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Window Thres:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel winthres = new JLabel();
					dframe.getContentPane().add(winthres, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Window Size:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel winsize = new JLabel();
					dframe.getContentPane().add(winsize, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JLabel("Sent Window:"), new GridBagConstraints(0, ++y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					final JLabel sent = new JLabel();
					dframe.getContentPane().add(sent, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.getContentPane().add(new JPanel(), new GridBagConstraints(0, ++y, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					
					dframe.setSize(300, 400);
					dframe.pack();
					dframe.setVisible(true);
					dframe.setLocationRelativeTo(null);
					
					texec.run(new Runnable()
					{
						public void run()
						{
							while (isConnected())
							{
								String tintpackqval = "0";
								try
								{
									tintpackqval = String.valueOf(internalpackets.size());
								}
								catch (Exception e)
								{
								}
								final String intpackqval = tintpackqval;
								
								String tuserpackqval = "0";
								try
								{
									tuserpackqval = String.valueOf(userpackets.size());
								}
								catch (Exception e)
								{
								}
								final String userpackqval = tuserpackqval;
								
//								final String partidval = String.valueOf(packetidcounter.get());
//								
//								final String msgidval = String.valueOf(messageidcounter.get());
								
								final String ltncyval = String.valueOf(latency);
								
								final String msgqval = String.valueOf(messagequeue.size());
								
								final String partqval = String.valueOf(queuedmessageparts.size());
								
								final String winthresval = String.valueOf(windowthres);
								
								final String winsizeval = String.valueOf(windowsize);
								
								final String sentval = String.valueOf(sentwindow.size());
								
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										intpackq.setText(intpackqval);
										userpackq.setText(userpackqval);
//										partid.setText(partidval);
//										msgid.setText(msgidval);
										ltncy.setText(ltncyval);
										msgq.setText(msgqval);
										partq.setText(partqval);
										winthres.setText(winthresval);
										winsize.setText(winsizeval);
										sent.setText(sentval);
									}
								});
								
								try
								{
									Thread.sleep(33);
								}
								catch (InterruptedException e)
								{
								}
							}
						}
					});
				}
			});
		}
		
		synchronized (internalpackets)
		{
			internalpackets.clear();
			if (userpackets != null)
			{
				userpackets.clear();
			}
			messages.clear();
			connected = true;
//			packetidcounter = new AtomicLong(Long.MIN_VALUE + 1);
//			messageidcounter = new AtomicLong(Long.MIN_VALUE);
//			packetidreplaytracker = new IdReplayTracker(packetidcounter.get(), STunables.MISSING_PACKET_TIMEOUT);
			
			if (confirmation != null)
			{
				try
				{
					socket.send(confirmation);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					connected = false;
				}
			}
			
			if (handler != null)
			{
				handler.fireConnectionEstablished(this);
			}
		}
	}
	
	/**
	 *  Notifies the remote host of a disconnect and terminates the connection.
	 */
	protected void disconnect()
	{
		connected = false;
		synchronized(messagequeue)
		{
			messagequeue.notifyAll();
			clearQueues();
		}
		internalpackets.clear();
		try
		{
			byte[] ranval = new byte[STunables.FIN_VERIFICATION_LENGTH];
			SUdpUtil.getSecRandom().nextBytes(ranval);
			SHA512Digest hash = new SHA512Digest();
			byte[] hashedranval = new byte[hash.getDigestSize()];
			hash.update(ranval, 0, ranval.length);
			hash.doFinal(hashedranval, 0);
			byte[] snd = new byte[ranval.length + hashedranval.length + 1];
			snd[0] = FIN;
			System.arraycopy(ranval, 0, snd, 1, ranval.length);
			System.arraycopy(hashedranval, 0, snd, ranval.length + 1, hashedranval.length);
			
			snd = createUserPacket(snd);
			
			DatagramPacket dgp = new DatagramPacket(snd, snd.length, remoteaddr);
			
			int count = STunables.FINACK_MAX_PACKETS;
			while (count > 0)
			{
				socket.send(dgp);
				byte[] rcv = internalpackets.poll(STunables.FINACK_RESEND_DELAY, TimeUnit.MILLISECONDS);
//				System.out.println("FINACK? " + rcv);
				if (rcv != null && rcv.length > 1 && rcv[0] == DATA)
				{
					synchronized (ciphermgmt)
					{
						rcv = ciphermgmt.getSymCipherSuite(rcv[1]).getSymCipher().decrypt(rcv, 2, -1);
					}
					if (rcv != null && rcv.length == 1 && rcv[0] == FINACK)
					{
						count = 0;
					}
				}
				--count;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		synchronized (connectionlisteners)
		{
			for (IConnectionListener listener : connectionlisteners)
			{
				listener.peerDisconnected(this);
			}
		}
	}
	
	/**
	 *  Called when a package is received.
	 *  @param data Raw package data.
	 */
	protected void rawPacketReceived(byte[] data)
	{
		try
		{
			if (connected)
			{
				if (data != null && data.length > 1)
				{
					if (data[0] == DATA && data.length >= 5)
					{
						try
						{
							SymCipherSuite ciphersuite = null;
							synchronized (ciphermgmt)
							{
								ciphersuite = ciphermgmt.getSymCipherSuite(data[1]);
								data = ciphersuite.getSymCipher().decrypt(data, 2 , -1);
							}
							
							userPacketReceived(ciphersuite, data);
						}
						catch (IOException e)
						{
							//TODO: Disconnect?
						}
						catch (Exception e)
						{
							// Disconnect?
							e.printStackTrace();
						}
					}
					else if (data[0] == SYNACK && keypackage != null)
					{
						byte[] snd = new byte[1 + keypackage.length];
						snd[0] = RDY;
						System.arraycopy(keypackage, 0, snd, 1, keypackage.length);
						DatagramPacket dgp = new DatagramPacket(snd, snd.length, remoteaddr);
						socket.send(dgp);
					}
				}
			}
			else
			{
				boolean done = false;
				synchronized (internalpackets)
				{
					if (!connected)
					{
						internalpackets.offer(data);
						done = true;
					}
				}
				if (!done)
				{
					rawPacketReceived(data);
				}
			}
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 *  Method for handling decrypted user-level packets.
	 *  
	 *  @param data Packet data.
	 *  @throws Exception Any exception that may occur and needs to be handled on a higher level.
	 */
	protected void userPacketReceived(SymCipherSuite ciphersuite, byte[] data) throws Exception
	{
		if (keypackage != null)
		{
			keypackage = null;
		}
		
		if (data.length > 0)
		{
			if (data[0] == PACKET)
			{
				long packetid = SUdpUtil.longFromByteArray(data, 1);
				
				if (ciphersuite.getPacketIdReplayTracker().isReplay(packetid))
				{
					return;
				}
				
				byte[] packet = new byte[data.length - 9];
				if (packet.length > 0)
				{
					System.arraycopy(data, 1, packet, 0, packet.length);
				}
				if (userpackets != null)
				{
					userpackets.offer(packet);
				}
				else
				{
					inclistener.receivePacket(getRemoteAddress(), packet);
				}
			}
			else if (data[0] == MESSAGE_PART || data[0] == INT_MESSAGE_PART)
			{
				long partid = SUdpUtil.longFromByteArray(data, 1);
				long msgid = SUdpUtil.longFromByteArray(data, 9);
				int partcount = SUdpUtil.intFromByteArray(data, 17);
				int parttotal = SUdpUtil.intFromByteArray(data, 21);
				
				byte[] reply = new byte[9];
				reply[0] = ACK;
				SUdpUtil.longIntoByteArray(reply, 1, partid);
				/*reply = getActiveSymCipher().encrypt(reply);
				byte[] dgcontent = new byte[reply.length + 1];
				dgcontent[0] = DATA;
				System.arraycopy(reply, 0, dgcontent, 1, reply.length);
				reply = dgcontent;*/
				reply = createUserPacket(reply);
				DatagramPacket dgp = new DatagramPacket(reply, reply.length, remoteaddr);
				socket.send(dgp);
				
				if (ciphersuite.getPacketIdReplayTracker().isReplay(partid))
				{
					return;
				}
				
				IncomingMessage message = messages.get(msgid);
				if (message == null)
				{
					message = new IncomingMessage(parttotal, data[0] == INT_MESSAGE_PART);
					messages.put(msgid, message);
				}
				
				message.addPart(partcount, data, 25);
				
				
//				System.out.println("PSTATE: " + partcount + " " + parttotal + " " + done);
				if(message.isDone())
				{
					IncomingMessage msg = messages.remove(msgid);
//					System.out.println("MSG Done " + " " + messages.size());
					
					if (msg.isInternal())
					{
						processInternalMessage(msg.getMessage());
					}
					else
					{
						if (userpackets != null)
						{
							userpackets.offer(msg.getMessage());
						}
						else
						{
							inclistener.receivePacket(getRemoteAddress(), msg.getMessage());
						}
					}
				}
			}
			else if (data[0] == ACK)
			{
//				System.out.println("ACK");
				try
				{
					long partid = SUdpUtil.longFromByteArray(data, 1);
					
					synchronized (messagequeue)
					{
						MessagePart part = sentwindow.remove(partid);
//						System.out.println("Removed: " + partid + " " + sentwindow.size());
						if (windowsize < windowthres)
						{
							windowsize = windowsize << 1;
						}
						else
						{
							++windowsize;
						}
						if (!part.isResend())
						{
							latency = Math.max(STunables.MIN_LATENCY, System.currentTimeMillis() - part.getSentTime());
//							System.out.println("WS: " + windowsize);
						}
						
						if (part.getMessage() != null && part.getMessage().getUnconfirmedParts() != null)
						{
							part.getMessage().getUnconfirmedParts().remove(partid);
							if (part.getMessage().getUnconfirmedParts().isEmpty())
							{
								final IUdpCallback<Boolean> cb = part.getMessage().getCallback();
								if (cb != null)
								{
									texec.run(new Runnable()
									{
										public void run()
										{
											cb.resultAvailable(Boolean.TRUE);
										}
									});
								}
							}
						}
						
						messagequeue.notifyAll();
					}
				}
				catch (Exception e)
				{
				}
			}
			else if (data[0] == KEEPALIVE)
			{
//				System.out.println("Rcv Keepalive");
				long packetid = SUdpUtil.longFromByteArray(data, 1);
				
				if (ciphersuite.getPacketIdReplayTracker().isReplay(packetid))
				{
					return;
				}
				
				keepalivereceived = System.currentTimeMillis();
			}
			else if (data[0] == FIN)
			{
//				System.out.println("FIN rcv");
//				byte[] confirm = symcipher.decrypt(data, 1, data.length - 1);
				byte[] ranval = new byte[STunables.FIN_VERIFICATION_LENGTH];
				System.arraycopy(data, 1, ranval, 0, STunables.FIN_VERIFICATION_LENGTH);
				byte[] hashedranval = new byte[data.length - STunables.FIN_VERIFICATION_LENGTH - 1];
				System.arraycopy(data, STunables.FIN_VERIFICATION_LENGTH + 1, hashedranval, 0, hashedranval.length);
				SHA512Digest hash = new SHA512Digest();
				byte[] tmphashedranval = new byte[hash.getDigestSize()];
				hash.update(ranval, 0, ranval.length);
				hash.doFinal(tmphashedranval, 0);
				if (Arrays.equals(hashedranval, tmphashedranval))
				{
//					System.out.println("FIN confirmed");
					connected = false;
					
					synchronized (connectionlisteners)
					{
						for (IConnectionListener listener : connectionlisteners)
						{
							listener.peerDisconnected(this);
						}
					}
					
					byte[] snd = new byte[1];
					snd[0] = FINACK;
					snd = createUserPacket(snd);
					DatagramPacket dgp = new DatagramPacket(snd, snd.length, remoteaddr);
					for (int i = 0; i < STunables.FINACK_MAX_PACKETS; ++i)
					{
						socket.send(dgp);
						Thread.sleep(STunables.FINACK_RESEND_DELAY);
					}
				}
			}
		}
	}
	
	/**
	 *  Loop for dispatching packets to the remote host.
	 */
	protected void sendLoop()
	{
		while (connected)
		{
			synchronized(messagequeue)
			{
				if (System.currentTimeMillis() > keepalivesent + STunables.KEEPALIVE_INTERVAL)
				{
	//					System.out.println("Send Keepalive");
	//					System.out.println("Part queue length: " + queuedmessageparts.size());
					byte[] snd = new byte[9];
					snd[0] = KEEPALIVE;
					synchronized (ciphermgmt)
					{
						SUdpUtil.longIntoByteArray(snd, 1, ciphermgmt.getActiveSymCipherSuite().getPacketidcounter().getAndIncrement());
						snd = createUserPacket(snd);
					}
					try
					{
						DatagramPacket dgp = new DatagramPacket(snd, snd.length, remoteaddr);
						socket.send(dgp);
					}
					catch (IOException e)
					{
						// Disconnect?
					}
					keepalivesent = System.currentTimeMillis();
				}
	//				else 
	//				{
	//					System.out.println("Not yet keepalive" + System.currentTimeMillis() + " " + keepalivesent + " " + STunables.KEEPALIVE_INTERVAL + " " + (keepalivesent + STunables.KEEPALIVE_INTERVAL));
	//				}
				
				boolean resend = false;
				for (Map.Entry<Long, MessagePart> entry : sentwindow.entrySet())
				{
					if (entry.getValue().getResendTime() < System.currentTimeMillis())
					{
						try
						{
							socket.send(entry.getValue().getPacket());
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						entry.getValue().setResentTime(System.currentTimeMillis() +
								((long) (latency * STunables.MIN_LATENCY_RESEND_DELAY_FACTOR)) +
								((long) (latency * STunables.RND_LATENCY_RESEND_DELAY_FACTOR * insecrandom.nextDouble())));
						
						if (!entry.getValue().isResend())
						{
							resend = true;
	//							windowthres = Math.max(1, windowsize / 2);;
	//							windowsize = 1;
						}
						
						entry.getValue().setResend(true);
						
	//						System.out.println("Resent: "+ entry.getValue() + " " + windowsize);
					}
				}
				
				if (resend)
				{
	//					windowsize = Math.max(windowthres, windowsize / 2);
	//					int prev = windowthres;
	//					windowthres = Math.max(1, windowsize / 2);
	//					windowthres = Math.max(1, sentwindow.size() / 2);
					windowthres = Math.max(1, windowsize / 2);
	//					System.out.print("WS: " + windowsize + " ");
	//					System.out.println("thres prev: " + prev + " new " + windowthres);
					windowsize = 1;
				}
			
			
				while (queuedmessageparts.size() > 0 && sentwindow.size() < windowsize)
				{
					windowsize = windowsize;
	//					System.out.println("Adding: " + sentwindow.size());
					MessagePart part = queuedmessageparts.removeFirst();
					
					try
					{
						socket.send(part.getPacket());
					}
					catch (IOException e)
					{
						//TODO:HANDLE
						e.printStackTrace();
					}
					
					part.setSentTime(System.currentTimeMillis());
					part.setResentTime(System.currentTimeMillis() +
							((long) (latency * STunables.MIN_LATENCY_RESEND_DELAY_FACTOR)) +
							((long) (latency * STunables.RND_LATENCY_RESEND_DELAY_FACTOR * insecrandom.nextDouble())));
					sentwindow.put(part.getId(), part);
				}
				
				synchronized (ciphermgmt)
				{
					if (ciphermgmt.getRemainingBytes() < 0 && ciphermgmt.getKx() == null)
					{
						ciphermgmt.createKx();
						byte[] pubkey = ciphermgmt.getKx().getPublicKey();
						byte[] msg = new byte[pubkey.length + 1];
						msg[0] = REKEY;
						System.arraycopy(pubkey, 0, msg, 1, pubkey.length);
						OutgoingMessage message = new OutgoingMessage(msg, null);
						scheduleMessage(message, true);
						//System.out.println("Starting rekey.");
					}
				}
			
				try
				{
	//					if (queuedmessageparts.isEmpty() && sentwindow.isEmpty())
	//					{
	//						System.out.println("Sleeping " );
	//						queuedmessageparts.wait();
	//					}
	//					else
	//					{
	//						System.out.println("Sleeping: " + (latency));
	//						queuedmessageparts.wait(Math.max(1, latency));
	//					}
					if (queuedmessageparts.isEmpty() && !messagequeue.isEmpty())
					{
						scheduleMessage(messagequeue.poll(), false);
					}
					else if (messagequeue.isEmpty() && queuedmessageparts.isEmpty() && sentwindow.isEmpty())
					{
						messagequeue.wait(STunables.KEEPALIVE_INTERVAL);
					}
					else
					{
						messagequeue.wait(Math.min(STunables.KEEPALIVE_INTERVAL, Math.max(10, latency << 1)));
					}
				}
				catch (InterruptedException e)
				{
				}
			}
			
			if (System.currentTimeMillis() > keepalivereceived + STunables.KEEPALIVE_TIMEOUT)
			{
				synchronized(messagequeue)
				{
					connected = false;
					clearQueues();
				}
				synchronized (connectionlisteners)
				{
					for (IConnectionListener listener : connectionlisteners)
					{
						listener.peerDisconnected(this);
					}
				}
//					System.out.println("Keepalive fail.");
			}
		}
	}
	
	/**
	 *  Creates a prepared packet as encrypted user packet.
	 *  @param packet The inner packet.
	 */
	protected byte[] createUserPacket(byte[] packet)
	{
		byte cipherid;
		synchronized (ciphermgmt)
		{
			ciphermgmt.subtractRemainingBytes(packet.length);
			packet = ciphermgmt.getActiveSymCipherSuite().getSymCipher().encrypt(packet);
			cipherid = ciphermgmt.getActiveSymCipherId();
		}
		byte[] buf = new byte[packet.length + 2];
		buf[0] = DATA;
		buf[1] = cipherid;
		System.arraycopy(packet, 0, buf, 2, packet.length);
		return buf;
	}
	
	/**
	 *  Builds a signed package of local and remote public key.
	 * 
	 *  @param localpubkey Local public key.
	 *  @param remotepubkey Remote public key.
	 *  @return Signed package.
	 */
	protected byte[] buildSignedKeyPackage(byte[] localpubkey, byte[] remotepubkey)
	{
		byte[] keypackage = new byte[2 + localpubkey.length + remotepubkey.length];
		SUdpUtil.shortIntoByteArray(keypackage, 0, (short) localpubkey.length);
		System.arraycopy(localpubkey, 0, keypackage, 2, localpubkey.length);
		System.arraycopy(remotepubkey, 0, keypackage, 2 + localpubkey.length, remotepubkey.length);
		
		byte[] sig = keyverifiers.get(0).sign(keypackage);
		byte[] signedkeypackage = new byte[2 + keypackage.length + sig.length];
		SUdpUtil.shortIntoByteArray(signedkeypackage, 0, (short) keypackage.length);
		System.arraycopy(keypackage, 0, signedkeypackage, 2, keypackage.length);
		if (sig != null && sig.length > 0)
			System.arraycopy(sig, 0, signedkeypackage, 2 + keypackage.length, sig.length);
		
		return signedkeypackage;
	}
	
	/**
	 *  Verifies a signed package of local and remote public key.
	 *  
	 *  @param localpubkey Local public key for comparison
	 *  @param buffer Buffer containing package.
	 *  @param offset Offset of the package.
	 *  @return Extracted remote public key if verified, null if verification failed.
	 */
	protected byte[] extractAndVerifyPubKey(byte[] localpubkey, byte[] buffer, int offset)
	{
		int keypackagesize = SUdpUtil.shortFromByteArray(buffer, offset);
		byte[] keypackage = new byte[keypackagesize];
		offset += 2;
		System.arraycopy(buffer, offset, keypackage, 0, keypackagesize);
		offset += keypackagesize;
		byte[] sig = null;
		if (buffer.length - offset > 0)
		{
			sig = new byte[buffer.length - offset];
			System.arraycopy(buffer, offset, sig, 0, sig.length);
		}
		
		boolean verified = false;
		for (IKeyVerifier verifier : keyverifiers)
		{
			if (verifier.verify(keypackage, sig))
			{
				verified = true;
				break;
			}
		}
		
		if (!verified)
		{
			return null;
		}
		
		offset = 0;
		int remotekeysize = SUdpUtil.shortFromByteArray(keypackage, offset);
		offset += 2;
		byte[] remotekey = new byte[remotekeysize];
		System.arraycopy(keypackage, offset, remotekey, 0, remotekeysize);
		offset += remotekeysize;
		
		byte[] returnedlocalkey = new byte[keypackage.length - offset];
		System.arraycopy(keypackage, offset, returnedlocalkey, 0, returnedlocalkey.length);
		
		if (!Arrays.equals(localpubkey, returnedlocalkey))
		{
			return null;
		}
		
		return remotekey;
	}
	
	/**
	 *  Processes an internal message.
	 *  @param incmsg The message.
	 */
	protected void processInternalMessage(byte[] incmsg)
	{
		if (incmsg[0] == REKEY)
		{
			synchronized (ciphermgmt)
			{
				if (ciphermgmt.getKx() == null)
				{
					// Received a kx request
					ciphermgmt.createKx();
					byte[] pubkey = ciphermgmt.getKx().getPublicKey();
					byte[] msg = new byte[pubkey.length + 1];
					msg[0] = REKEY;
					System.arraycopy(pubkey, 0, msg, 1, pubkey.length);
					
					byte[] remotekey = new byte[incmsg.length - 1];
					System.arraycopy(incmsg, 1, remotekey, 0, remotekey.length);
					byte[] newkey = ciphermgmt.getKx().generateSymKey(remotekey);
					ciphermgmt.createSymCipherSuite((byte)(ciphermgmt.getActiveSymCipherId() + 1), newkey);
					
					OutgoingMessage message = new OutgoingMessage(msg, rekeycallback);
					scheduleMessage(message, true);
				}
				else
				{
					// Answered a kx request
					byte[] remotekey = new byte[incmsg.length - 1];
					System.arraycopy(incmsg, 1, remotekey, 0, remotekey.length);
					byte[] newkey = ciphermgmt.getKx().generateSymKey(remotekey);
					ciphermgmt.createSymCipherSuite((byte)(ciphermgmt.getActiveSymCipherId() + 1), newkey);
					rekeycallback.resultAvailable(Boolean.TRUE);
				}
				
//				byte[] msg = new byte[1];
//				msg[0] = REKEY_ACK;
//				OutgoingMessage message = new OutgoingMessage(msg, null);
//				scheduleMessage(message, true);
				//System.out.println("Rekey success");
			}
		}
//		else if (incmsg[0] == REKEY_ACK)
//		{
//			synchronized (ciphermgmt)
//			{
//				byte cipherid = ciphermgmt.getActiveSymCipherId();
//				++cipherid;
//				ciphermgmt.setActiveSymCipherId(cipherid);
//				cipherid -= (STunables.MAX_KEY_RETENTION + 1);
//				ciphermgmt.destroySymCipher(cipherid);
//				ciphermgmt.refreshRemainingBytes();
//				ciphermgmt.destroyKx();
//				//System.out.println("Rekey confirmed: " + ciphermgmt.getActiveSymCipherId());
//				//System.out.println("Keys: " + Arrays.toString(symciphers));
//			}
//		}
	}
	
	protected void scheduleMessage(final OutgoingMessage msg, final boolean internal)
	{
//		int partnum = msg.length / STunables.MAX_MSG_PART_SIZE + 1;
//		
//		int offset = 0;
		
		byte cipherid = 0;
		SymCipherSuite actciphersuite = null;
		synchronized (ciphermgmt)
		{
			actciphersuite = ciphermgmt.getActiveSymCipherSuite();
			cipherid = ciphermgmt.getActiveSymCipherId();
			
			// Somewhat a hack, underestimates actual encryption size, but higher efficiency.
			ciphermgmt.subtractRemainingBytes(msg.getData().length);
		}
		
		final long msgid = actciphersuite.getMessageidcounter().getAndIncrement();
		
		int partnum = msg.getData().length / STunables.MAX_MSG_PART_SIZE + 1;
		
		int offset = 0;
		
		MessagePart[] parts = new MessagePart[partnum];
		Set<Long> unconfirmedpartids = new HashSet<Long>();
		
		for (int i = 0; i < partnum; ++i)
		{
			long partid = actciphersuite.getPacketidcounter().getAndIncrement();
			unconfirmedpartids.add(partid);
			int partsize = Math.min(STunables.MAX_MSG_PART_SIZE, msg.getData().length - offset);
			byte[] part = new byte[partsize + 25];
			part[0] = internal? INT_MESSAGE_PART : MESSAGE_PART;
			SUdpUtil.longIntoByteArray(part, 1, partid);
			SUdpUtil.longIntoByteArray(part, 9, msgid);
			SUdpUtil.intIntoByteArray(part, 17, i);
			SUdpUtil.intIntoByteArray(part, 21, partnum);
			System.arraycopy(msg.getData(), offset, part, 25, partsize);
			offset += partsize;
			part = actciphersuite.getSymCipher().encrypt(part);
			byte[] dgcontent = new byte[part.length + 2];
			dgcontent[0] = DATA;
			dgcontent[1] = cipherid;
			System.arraycopy(part, 0, dgcontent, 2, part.length);
			DatagramPacket dgp = null;
			try
			{
				dgp = new DatagramPacket(dgcontent, dgcontent.length, remoteaddr);
			}
			catch (Exception e)
			{
				//TODO: HANDLE?
				e.printStackTrace();
			}
			parts[i] = new MessagePart(partid, dgp, msg);
		}
		
		msg.setUnconfirmedParts(unconfirmedpartids);
		msg.clearData();
		
		for (MessagePart msgpart : parts)
		{
			queuedmessageparts.offer(msgpart);
		}
	}
	
	/** Clears internal buffers. */
	protected void clearQueues()
	{
		while (!messagequeue.isEmpty())
		{
			OutgoingMessage message = messagequeue.poll();
			if (message.getCallback() != null)
			{
				final IUdpCallback<Boolean> cb = message.getCallback();
				if (cb != null)
				{
					texec.run(new Runnable()
					{
						public void run()
						{
							cb.resultAvailable(Boolean.FALSE);
						}
					});
				}
			}
		}
		queuedmessageparts.clear();
		sentwindow.clear();;
	}
}
