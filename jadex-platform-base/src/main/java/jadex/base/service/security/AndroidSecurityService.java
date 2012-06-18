package jadex.base.service.security;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.android.IAndroidContextService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


@Service
public class AndroidSecurityService extends SecurityService
{
	protected IAndroidContextService	contextService;

	public AndroidSecurityService(Boolean usepass, Boolean printpass, Boolean trustedlan)
	{
		super(usepass, printpass, trustedlan);
	}

	@Override
	@ServiceStart
	public IFuture<Void> start()
	{
		IFuture<IAndroidContextService> service = SServiceProvider.getService(
				component.getServiceContainer(), IAndroidContextService.class);
		service.addResultListener(new DefaultResultListener<IAndroidContextService>()
		{
			@Override
			public void resultAvailable(IAndroidContextService result)
			{
				contextService = result;
			}
		});
		return super.start();
	}

	/* if[android8] */
	@Override
	protected List<InetAddress> getNetworkIps()
	{
		List<InetAddress> ret = new ArrayList<InetAddress>();
		int dhcpNetmask = contextService.getDhcpNetmask();
		int dhcpInetAdress = contextService.getDhcpInetAdress();

		if(dhcpInetAdress != -1 && dhcpNetmask != -1)
		{
			int network = (dhcpNetmask & dhcpInetAdress);
			byte[] quads = new byte[4];
			for(int k = 0; k < 4; k++)
			{
				quads[k] = (byte)((network >> k * 8) & 0xFF);
			}
			try
			{
				ret.add(InetAddress.getByAddress(quads));
			}
			catch(UnknownHostException e)
			{
			}
		}
		return ret;
	}
	/* end[android8] */

}
