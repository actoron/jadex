package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Interface for calculating an area of points.
 */
@Service
@Security(roles=Security.UNRESTRICTED)
public interface ICalculateService
{
	/**
	 *  Calculate colors for an area of points.
	 *  @param data	The area to be calculated.
	 *  @return	A future containing the calculated area.
	 * /
	@Timeout(30000)
	public IFuture<AreaData> calculateArea(AreaData data);*/
	
	/**
	 *  Calculate colors for an area of points.
	 *  @param data	The area to be calculated.
	 *  @return	A future containing the calculated area.
	 */
	@Timeout(30000)
	public IIntermediateFuture<PartDataChunk> calculateArea(AreaData data);
}
