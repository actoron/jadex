package jadex.commons;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

//import jadex.commons.future.Future;

/**
 *  Helper class for methods used from non-android code.
 */
public class SNonAndroid
{
	/**
	 *  Get the network prefix length for IPV4 address
	 *  24=C, 16=B, 8=A classes. 
	 *  Returns -1 in case of V6 address.
	 *  @param iadr The address.
	 *  @return The length of the prefix.
	 */
	public static short getNetworkPrefixLength(InetAddress iadr)
	{
		short ret = -1;
		try
		{
			NetworkInterface ni = NetworkInterface.getByInetAddress(iadr);
			List<InterfaceAddress> iads = ni.getInterfaceAddresses();
			if(iads!=null)
			{
				for(int i=0; i<iads.size() && ret==-1; i++)
				{
					InterfaceAddress ia = iads.get(i);
					if(ia.getAddress() instanceof Inet4Address)
						ret = ia.getNetworkPrefixLength();
				}
			}
			
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		return ret;
	}

	/**
	 *  Check if a file represents a floppy.
	 */
	public static boolean isFloppyDrive(File file)
	{
		return FileSystemView.getFileSystemView().isFloppyDrive(file);
	}

	/**
	 *  Get the display name (e.g. of a system drive).
	 */
	public static String getDisplayName(File file)
	{
		return FileSystemView.getFileSystemView().getSystemDisplayName(file);
	}

	/**
	 * Try to find a {@link ResourceBundle} by trying Classloaders 
	 * from all calling Classes.
	 * @param name Name of the ResourceBundle to find
	 * @param currentLocale Name of the locale
	 * @param cl the default classloader
	 * @return The found {@link ResourceBundle} or <code>null</code>.
	 */
	public static ResourceBundle findResourceBundle(String name, Locale currentLocale, ClassLoader cl)
	{
		// Fall back to searching up the call stack and trying each
		// calling ClassLoader.
		for(int ix = 0;; ix++)
		{
			Class<?> clz = null;
			try
			{
				Class<?> ref = Class.forName("sun.reflect.Reflection");
				Method m = ref.getMethod("getCallerClass", new Class[]{int.class});
				clz = (Class<?>)m.invoke(null, new Object[]{Integer.valueOf(ix)});
			}
			catch(Exception e)
			{
			}
//			Class<?> clz = sun.reflect.Reflection.getCallerClass(ix);
			
			if(clz == null)
			{
				break;
			}
			ClassLoader cl2 = clz.getClassLoader();
			if(cl2 == null)
			{
				cl2 = ClassLoader.getSystemClassLoader();
			}
			if(cl == cl2)
			{
				// We've already checked this classloader.
				continue;
			}
			cl = cl2;
			try
			{
				return ResourceBundle.getBundle(name, currentLocale, cl);
			}
			catch(MissingResourceException ex)
			{
				// Ok, this one didn't work either.
				// Drop through, and try the next one.
			}
		}
		
		return null;
	}

	/**
	 * Get the network ips.
	 */
	public static List<InetAddress> getNetworkIps()
	{
		List<InetAddress> ret = new ArrayList<InetAddress>();
		try
		{
			// Generate network identifiers
			for(NetworkInterface ni : SUtil.getNetworkInterfaces())
			{
				for(InterfaceAddress ifa : ni.getInterfaceAddresses())
				{
					if(ifa != null) // Yes, there may be a null in the list. grrr.
					{
						InetAddress addr = ifa.getAddress();
						// System.out.println("addr: "+addr+" "+addr.isAnyLocalAddress()+" "+addr.isLinkLocalAddress()+" "+addr.isLoopbackAddress()+" "+addr.isSiteLocalAddress()+", "+ni.getDisplayName());

						if(addr.isLoopbackAddress())
						{
							// ignore
						}
						else if(addr.isLinkLocalAddress())
						{
							// ignore
						}
						else
						// if(addr.isSiteLocalAddress()) or other
						{
							// Hack!!! Use sensible default prefix when -1 or 128
							// due to JDK bug on windows
							// http://bugs.sun.com/view_bug.do?bug_id=6707289
							short prefix = ifa.getNetworkPrefixLength();
							if(prefix==-1 || prefix==128 && addr instanceof Inet4Address)
							{
								prefix	= 24;
							}
							InetAddress ad = SUtil.getNetworkIp(addr, prefix);
							ret.add(ad);
						}
					}
				}
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		return ret;
	}

	/**
	 * Opens a File with the default application.
	 * @param path
	 * @throws IOException 
	 */
	public static void openFile(String path) throws IOException
	{
		Desktop.getDesktop().open(new File(path));
	}


	/**
	 *  Test if a call is running on the swing thread.
	 */
	public static boolean isGuiThread()
	{
		try
		{
			return SReflect.HAS_GUI
					// pre-check because isEventDispatchThread is slow
					&& Thread.currentThread().getName().startsWith("AWT-EventQueue")
					&& SwingUtilities.isEventDispatchThread();
		}
		catch(Exception e)
		{
			// null pointer exception thrown by swing: http://bugs.java.com/view_bug.do?bug_id=8143287
			return false;
		}
	}
	
	/**
	 *  Test if there is a gui available.
	 */
	public static boolean hasGui()
	{
		boolean hasgui;
		try
		{
			hasgui = !(GraphicsEnvironment.isHeadless() ||
					 	  GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length < 1);
		}
		catch(Error e)
		{
			// On system misconfigurations, Java throws an Error (grr).
			hasgui = false;
		}
		return hasgui;
	}

	/**
	 *  Get the home directory.
	 */
	public static File	getHomeDirectory()
	{
		return FileSystemView.getFileSystemView().getHomeDirectory();
	}
	
	/**
	 *  Get the default directory.
	 */
	public static File	getDefaultDirectory()
	{
		return FileSystemView.getFileSystemView().getDefaultDirectory();
	}

	/**
	 *  Get the parent directory.
	 */
	public static File	getParentDirectory(File file)
	{
		return FileSystemView.getFileSystemView().getParentDirectory(file);
	}

	/**
	 *  Get the files of a directory.
	 */
	public static File[]	getFiles(File file, boolean hiding)
	{
		return FileSystemView.getFileSystemView().getFiles(file, hiding);
	}
	
	/**
	 *  Get the mac address.
	 *  @return The mac address.
	 */
	public static String[] getMacAddresses()
	{
		TreeSet<String> res = new TreeSet<String>(new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o1.compareTo(o2);
			}
		});
		
		try
		{
			List<NetworkInterface> nis = SUtil.getNetworkInterfaces();
			for(NetworkInterface ni: nis)
			{
				byte[] hwa = ni.getHardwareAddress();
				if(hwa!=null && hwa.length>0)
				{
					String mac = Arrays.toString(hwa);
					if(!res.contains(mac))
					{
						res.add(mac);
					}
				}
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
			
		return res.isEmpty()? new String[0]: (String[])res.toArray(new String[res.size()]);
	}

	/**
	 *  Workaround for AWT/Swing memory leaks.
	 */
	public static void	clearAWT()
	{
		// Java Bug not releasing the last focused window, see:
		// http://www.lucamasini.net/Home/java-in-general-/the-weakness-of-swing-s-memory-model
		// http://bugs.sun.com/view_bug.do?bug_id=4726458
		
//		final Future<Void>	disposed	= new Future<Void>();
		final Semaphore sem = new Semaphore(0);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				javax.swing.Timer	t	= new javax.swing.Timer(100, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final JFrame f	= new JFrame("dummy");
						f.getContentPane().add(new JButton("Dummy"), BorderLayout.CENTER);
						f.pack();
						f.setVisible(true);
						
						javax.swing.Timer	t	= new javax.swing.Timer(100, new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								f.dispose();
								javax.swing.Timer	t	= new javax.swing.Timer(100, new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
//										System.out.println("cleanup dispose");
										KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
										sem.release();
//										disposed.setResult(null);
									}
								});
								t.setRepeats(false);
								t.start();

							}
						});
						t.setRepeats(false);
						t.start();
					}
				});
				t.setRepeats(false);
				t.start();
			}
		});
		
//		disposed.get(new ThreadSuspendable(), BasicService.getDefaultTimeout());
//		disposed.get(new ThreadSuspendable(), 30000);
//		disposed.get(30000);
		try
		{
			sem.tryAcquire(30000, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
		}
		
//		// Another bug not releasing the last drawn window.
//		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6857676
//		
//		try
//		{
//			Class<?> clazz	= Class.forName("sun.java2d.pipe.BufferedContext");
//			Field	field	= clazz.getDeclaredField("currentContext");
//			field.setAccessible(true);
//			field.set(null, null);
//		}
//		catch(Throwable e)
//		{
//			e.printStackTrace();
//		}

	}
}
