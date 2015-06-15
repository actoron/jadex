package jadex.platform.service.dht;

import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class OverlayIdFilter implements IAsyncFilter<IRingNodeService>
{
	private String	overlayId;
	
	public OverlayIdFilter()
	{
	}
	
	public OverlayIdFilter(String overlayId)
	{
		this.overlayId = overlayId;
	}
	@Override
	public IFuture<Boolean> filter(IRingNodeService obj)
	{
		return new Future<Boolean>(overlayId.equals(obj.getOverlayId()));
	}

	public String getOverlayId()
	{
		return overlayId;
	}

	public void setOverlayId(String overlayId)
	{
		this.overlayId = overlayId;
	}
}