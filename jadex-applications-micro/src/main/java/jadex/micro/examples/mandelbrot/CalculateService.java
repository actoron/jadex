package jadex.micro.examples.mandelbrot;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

/**
 *  Calculate service implementation.
 */
public class CalculateService extends BasicService implements ICalculateService
{
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public CalculateService(IServiceProvider provider)
	{
		super(provider.getId(), ICalculateService.class, null);
	}
	
	//-------- methods --------
	
	/**
	 *  Calculate colors for an area of points.
	 */
	public IFuture calculateArea(double x1, double y1, double x2, double y2, double stepx, double stepy)
	{
		Future ret = new Future();
		
		int nx = (int)((x2-x1)/stepx);
		int ny = (int)((y2-y1)/stepy);
		
		int[][] res = new int[nx][ny];
		
		for(int yi=0; yi<ny; yi++)
		{
			for(int xi=0; xi<nx; xi++)
			{
				res[xi][yi] = determineColor(x1+xi*stepx, y1+yi*stepy);
			}
		}
		
		ret.setResult(res);
		
		return ret;
	}

	/**
	 *  Determine the color of a point.
	 */
	protected int determineColor(double xn, double yn)
	{
		int max = 20;
		double x0 = xn;
		double y0 = yn;
		int i = 0;
		double c = 0;
		
		for(i=0; c<2 && i<20; i++)
		{
			double xn1 = xn*xn - yn*yn + x0;
			double yn1 = 2*xn*yn + y0;
			xn = xn1;
			yn = yn1;
			c =  Math.sqrt(xn*xn + yn*yn);
		}
		
		return i==max? -1: i;
	}
}
