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
	public IFuture calculateArea(AreaData data)
	{
//		System.out.println("calc: "+data);
		
		Future ret = new Future();
		
		double stepx = (data.getXEnd()-data.getXStart())/data.getSizeX();
		double stepy = (data.getYEnd()-data.getYStart())/data.getSizeY();
		
		int[][] res = new int[data.getSizeX()][data.getSizeY()];
		
		for(int yi=0; yi<data.getSizeY(); yi++)
		{
			for(int xi=0; xi<data.getSizeX(); xi++)
			{
				res[xi][yi] = determineColor(data.getXStart()+xi*stepx, data.getYStart()+yi*stepy, data.getMax());
			}
		}
		
		data.setData(res);
		ret.setResult(data);
		
		return ret;
	}

	/**
	 *  Determine the color of a point.
	 */
	protected int determineColor(double xn, double yn, int max)
	{
		double x0 = xn;
		double y0 = yn;
		int i = 0;
		double c = 0;
		
		for(i=0; c<2 && i<max; i++)
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
