package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Calculate service implementation.
 */
@Service
public class CalculateService implements ICalculateService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected CalculateAgent agent;
	
	//-------- methods --------
	
	/**
	 *  Calculate colors for an area of points.
	 *  @param data	The area to be calculated.
	 *  @return	A future containing the calculated area.
	 */
	public IFuture<AreaData> calculateArea(AreaData data)
	{
//		System.out.println("calc: "+data.getId()+" "+agent.getComponentIdentifier());
		
		agent.setHadJob(true);
		agent.setTaskId(data.getId());
		
		// This code iterates over the area in a bounding boxes
		// If a complete bounding box has is in the set the rest
		// is just set to -1 without calculation. This is more
		// efficient for areas with 'much black'.
		
		Future<AreaData> ret = new Future<AreaData>();
		
		double stepx = (data.getXEnd()-data.getXStart())/data.getSizeX();
		double stepy = (data.getYEnd()-data.getYStart())/data.getSizeY();
		
		short[][] res = new short[data.getSizeX()][data.getSizeY()];
		
		int xstart = 0;
		int xend = data.getSizeX()-1;
		int ystart = 0;
		int yend = data.getSizeY()-1;
		boolean allin = true;
		boolean justfill = false;
		short fillcol = -2;
		boolean	usejustfill	= data.getAlgorithm().isOptimizationAllowed()
			&& (data.getXStart()<2 && data.getXStart()>-2
			|| data.getYStart()<2 && data.getYStart()>-2
			|| data.getXEnd()<2 && data.getXEnd()>-2
			|| data.getYEnd()<2 && data.getYEnd()>-2);
			
		int	size	= data.getSizeX()*data.getSizeY();
		int	cnt	= 0;
		
		while(true)
		{
			if(xstart>xend)
				break;
			for(int xi=xstart; xi<=xend; xi++)
			{
				res[xi][ystart] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xi*stepx, data.getYStart()+ystart*stepy, data.getMax());
				if(!justfill && xi==xstart)
					fillcol = res[xi][ystart];
				if(allin && res[xi][ystart]!=fillcol)
					allin = false;
				cnt++;
				agent.setProgress(cnt*100/size);
			}
			ystart++;
			if(ystart>yend)
				break;
			for(int yi=ystart; yi<=yend; yi++)
			{
				res[xend][yi] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xend*stepx, data.getYStart()+yi*stepy, data.getMax());
				if(allin && res[xend][yi]!=fillcol)
					allin = false;
				cnt++;
				agent.setProgress(cnt*100/size);
			}
			xend--;
			if(xstart>xend)
				break;
			for(int xi=xend; xi>=xstart; xi--)
			{
				res[xi][yend] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xi*stepx, data.getYStart()+yend*stepy, data.getMax());
				if(allin && res[xi][yend]!=fillcol)
					allin = false;
				cnt++;
				agent.setProgress(cnt*100/size);
			}
			yend--;
			if(ystart>yend)
				break;
			for(int yi=yend; yi>=ystart; yi--)
			{
				res[xstart][yi] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xstart*stepx, data.getYStart()+yi*stepy, data.getMax());
				if(allin && res[xstart][yi]!=fillcol)
					allin = false;
				cnt++;
				agent.setProgress(cnt*100/size);
			}
			xstart++;
			
			if(allin && usejustfill)
			{
				justfill = true;
//				System.out.println("justfill"+fillcol);
				allin = false;
			}
			else if(!justfill)
			{
				allin = true;
			}
		}
		
		agent.setTaskId(null);
		agent.setProgress(0);
		data.setData(res);
		ret.setResult(data);
		
//		System.out.println("calc finished: "+data.getId()+" "+agent.getComponentIdentifier());
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

//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		AreaData ad = new AreaData(0, 1, 0, 1, 5, 5, 3, 1, 1, null, null);
//		calculateArea(ad);
//	}
}
