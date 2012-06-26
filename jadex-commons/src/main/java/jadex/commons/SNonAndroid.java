package jadex.commons;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.List;

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
}
