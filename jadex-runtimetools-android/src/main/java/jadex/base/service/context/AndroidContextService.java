package jadex.base.service.context;

import jadex.android.JadexAndroidContext;
import jadex.android.JadexAndroidContext.AndroidContextChangeListener;
import jadex.android.exception.JadexAndroidContextNotFoundError;
import jadex.android.exception.WrongEventClassException;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.context.IPreferences;
import jadex.commons.android.Logger;
import jadex.commons.future.IFuture;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * Android Implementation of {@link IContextService}.
 * Provides Access to the Android Application Context and Android Resources such
 * as Files and Properties
 */
public class AndroidContextService extends BasicService implements AndroidContextChangeListener, IContextService
{

	// -------- attributes ----------

	/** The Android Application Context */
	private Context context;

	/** The {@link JadexAndroidContext} */
	private JadexAndroidContext jadexAndroidContext;

	/** Cache the Wifi permission check */
	private Boolean hasWifiPermission;

	// -------- constructors ----------

	/**
	 * Constructor
	 * 
	 * @param provider
	 */
	public AndroidContextService(IServiceProvider provider)
	{
		super(provider.getId(), IContextService.class, null);
		jadexAndroidContext = JadexAndroidContext.getInstance();
		jadexAndroidContext.addContextChangeListener(this);
	}

	// -------- methods ----------

	public IFuture<Void> startService()
	{
		return super.startService();
	}

	public IFuture<Void> shutdownService()
	{
		JadexAndroidContext.getInstance().removeContextChangeListener(this);
		return super.shutdownService();
	}

	public void onContextDestroy(Context ctx)
	{
		this.context = null;
	}

	public void onContextCreate(Context ctx)
	{
		this.context = ctx;

		if (hasWifiPermission == null)
		{
			int perm = context.checkCallingOrSelfPermission(permission.ACCESS_WIFI_STATE);
			hasWifiPermission = perm == PackageManager.PERMISSION_GRANTED;
			if (!hasWifiPermission)
			{
				Logger.e("For full functionality (checking Netmask and IP Address), this Application needs PERMISSION.ACCESS_WIFI_STATE");
			}
		}
	}

	private void checkContext() throws JadexAndroidContextNotFoundError
	{
		if (context == null)
		{
			throw new JadexAndroidContextNotFoundError();
		}
	}

	/**
	 * Returns a File
	 * 
	 * @param name
	 *            File name
	 * @return {@link File}
	 */
	public File getFile(String name)
	{
		checkContext();
		return context.getFileStreamPath(name);
	}

	/**
	 * Gets an Android Shared Preference Container. Returns null on Desktop
	 * Systems.
	 * 
	 * @param preferenceFileName
	 */
	public IPreferences getSharedPreferences(String name)
	{
		checkContext();
		return AndroidSharedPreferencesWrapper.wrap(context.getSharedPreferences(name, Context.MODE_PRIVATE));
	}

	/**
	 * Dispatches an Event to the Android UI / Activity. Does nothing on Desktop
	 * Systems.
	 * 
	 * @param event
	 *            {@link IJadexAndroidEvent}
	 * @return true, if at least one receiver was registered for this event and
	 *         delivery was successful, else false.
	 */
	public boolean dispatchUiEvent(IJadexAndroidEvent event)
	{
		try
		{
			return jadexAndroidContext.dispatchEvent(event);
		}
		catch (WrongEventClassException e)
		{
			Log.e("AndroidContextService", e.getMessage());
			return false;
		}
	}

	/**
	 * Queries the Android Wifimanager for DHCP Infos and returns the Netmask,
	 * if any.
	 * 
	 * @return dhcp netmask or -1, if none
	 */
	private int getDhcpNetmask()
	{
		if (hasWifiPermission)
		{
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wifi.getDhcpInfo();
			return dhcpInfo != null ? dhcpInfo.netmask : -1;
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Queries the Android Wifimanager for DHCP Infos and returns the IP
	 * Address, if any.
	 * 
	 * @return ip address or -1, if none
	 */
	private int getDhcpInetAddress()
	{
		if (hasWifiPermission)
		{
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wifi.getDhcpInfo();
			return dhcpInfo != null ? dhcpInfo.ipAddress : -1;
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Queries the Android Wifimanager for DHCP Infos and returns the assigned
	 * IP Address, if any.
	 * 
	 * @return IP Address or -1, if none
	 */
	public List<InetAddress> getNetworkIps()
	{
		checkContext();
		List<InetAddress> ret = new ArrayList<InetAddress>();
		int dhcpNetmask = getDhcpNetmask();
		int dhcpInetAdress = getDhcpInetAddress();

		if (dhcpInetAdress != -1 && dhcpNetmask != -1)
		{
			int network = (dhcpNetmask & dhcpInetAdress);
			byte[] quads = new byte[4];
			for (int k = 0; k < 4; k++)
			{
				quads[k] = (byte) ((network >> k * 8) & 0xFF);
			}
			try
			{
				ret.add(InetAddress.getByAddress(quads));
			}
			catch (UnknownHostException e)
			{
			}
		}
		return ret;
	}

	/**
	 * Starts an intent to open the given file.
	 * @param path
	 * @throws IOException 
	 */
	public void openFile(String path) throws IOException
	{
          Intent intent = new Intent();
          intent.setAction(android.content.Intent.ACTION_VIEW);
          File file = new File(path);
        
          MimeTypeMap mime = MimeTypeMap.getSingleton();
          String ext=file.getName().substring(file.getName().indexOf(".")+1).toLowerCase();
          String type = mime.getMimeTypeFromExtension(ext);
       
          intent.setDataAndType(Uri.fromFile(file),type);
          jadexAndroidContext.getAndroidContext().startActivity(intent); 
	}

}