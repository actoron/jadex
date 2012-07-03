package jadex.commons;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileSystemView;

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
			Class clz = sun.reflect.Reflection.getCallerClass(ix);
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
			for (NetworkInterface ni : SUtil.getNetworkInterfaces())
			{
				for (InterfaceAddress ifa : ni.getInterfaceAddresses())
				{
					if (ifa != null) // Yes, there may be a null in the
										// list. grrr.
					{
						InetAddress addr = ifa.getAddress();
						// System.out.println("addr: "+addr+" "+addr.isAnyLocalAddress()+" "+addr.isLinkLocalAddress()+" "+addr.isLoopbackAddress()+" "+addr.isSiteLocalAddress()+", "+ni.getDisplayName());

						if (addr.isLoopbackAddress())
						{
							// ignore
						}
						else if (addr.isLinkLocalAddress())
						{
							// ignore
						}
						else
						// if(addr.isSiteLocalAddress()) or other
						{
							// Hack!!! Use sensible default prefix when -1
							// due to jdk on windows bug
							// http://bugs.sun.com/view_bug.do?bug_id=6707289
							short prefix = ifa.getNetworkPrefixLength();
							InetAddress ad = SUtil.getNetworkIp(ifa.getAddress(), prefix != -1 ? prefix : 24);
							ret.add(ad);
						}
					}
				}
			}
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
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
}
