package jadex.micro.examples.mandelbrot;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

/**
 *  Calculate service implementation.
 */
public class CalculateService extends BasicService implements ICalculateService
{
	//-------- attributes --------
	
	/** The agent. */
	protected CalculateAgent agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public CalculateService(CalculateAgent agent)
	{
		super(agent.getServiceProvider().getId(), ICalculateService.class, null);
		this.agent = agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Calculate colors for an area of points.
	 */
	public IFuture calculateArea(AreaData data)
	{
//		System.out.println("calc: "+data);
		
		agent.setHadJob(true);
		
		// This code iterates over the area in a bounding boxes
		// If a complete bounding box has is in the set the rest
		// is just set to -1 without calculation. This is more
		// efficient for areas with 'much black'.
		
		Future ret = new Future();
		
		double stepx = (data.getXEnd()-data.getXStart())/data.getSizeX();
		double stepy = (data.getYEnd()-data.getYStart())/data.getSizeY();
		
		int[][] res = new int[data.getSizeX()][data.getSizeY()];
		
		int xstart = 0;
		int xend = data.getSizeX()-1;
		int ystart = 0;
		int yend = data.getSizeY()-1;
		boolean allin = true;
		boolean justfill = false;
		
		while(true)
		{
			if(xstart>xend)
				break;
			for(int xi=xstart; xi<=xend; xi++)
			{
				res[xi][ystart] = justfill? -1: determineColor(data.getXStart()+xi*stepx, data.getYStart()+ystart*stepy, data.getMax());
				if(allin && res[xi][ystart]!=-1)
					allin = false;
			}
			ystart++;
			if(ystart>yend)
				break;
			for(int yi=ystart; yi<=yend; yi++)
			{
				res[xend][yi] = justfill? -1: determineColor(data.getXStart()+xend*stepx, data.getYStart()+yi*stepy, data.getMax());
				if(allin && res[xend][yi]!=-1)
					allin = false;
			}
			xend--;
			if(xstart>xend)
				break;
			for(int xi=xend; xi>=xstart; xi--)
			{
				res[xi][yend] = justfill? -1: determineColor(data.getXStart()+xi*stepx, data.getYStart()+yend*stepy, data.getMax());
				if(allin && res[xi][yend]!=-1)
					allin = false;
			}
			yend--;
			if(ystart>yend)
				break;
			for(int yi=yend; yi>=ystart; yi--)
			{
				res[xstart][yi] = justfill? -1: determineColor(data.getXStart()+xstart*stepx, data.getYStart()+yi*stepy, data.getMax());
				if(allin && res[xstart][yi]!=-1)
					allin = false;
			}
			xstart++;
			
			if(allin)
			{
				justfill = true;
//				System.out.println("justfill"+SUtil.arrayToString(res));
				allin = false;
			}
			else if(!justfill)
			{
				allin = true;
			}
		}
		
		data.setData(res);
		ret.setResult(data);
		
		return ret;
	}
	
//	/**
//	 *  Calculate colors for an area of points.
//	 */
//	public IFuture calculateArea(AreaData data)
//	{
////		System.out.println("calc: "+data);
//		
//		Future ret = new Future();
//		
//		double stepx = (data.getXEnd()-data.getXStart())/data.getSizeX();
//		double stepy = (data.getYEnd()-data.getYStart())/data.getSizeY();
//		
//		int[][] res = new int[data.getSizeX()][data.getSizeY()];
//		
//		for(int yi=0; yi<data.getSizeY(); yi++)
//		{
//			for(int xi=0; xi<data.getSizeX(); xi++)
//			{
//				res[xi][yi] = determineColor(data.getXStart()+xi*stepx, data.getYStart()+yi*stepy, data.getMax());
//			}
//		}
//		
//		data.setData(res);
//		ret.setResult(data);
//		
//		return ret;
//	}

	/**
	 *  Determine the color of a point.
	 */
	protected int determineColor(double xn, double yn, int max)
	{
		double x0 = xn;
		double y0 = yn;
		int i = 0;
		double c =  Math.sqrt(xn*xn + yn*yn);
		
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
	
//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		AreaData ad = new AreaData(0, 1, 0, 1, 5, 5, 3, 1, 1, null, null);
//		calculateArea(ad);
//	}
}
