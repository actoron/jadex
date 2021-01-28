package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

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
	
	@ServiceComponent
	protected IInternalAccess ag;
	
	//-------- methods --------
	
	/**
	 *  Calculate colors for an area of points.
	 *  @param data	The area to be calculated.
	 *  @return	A future containing the calculated area.
	 */
	@Timeout(30000)
	public IIntermediateFuture<PartDataChunk> calculateArea(AreaData data)
	{
		IntermediateFuture<PartDataChunk> ret = new IntermediateFuture<>();
		
		//long start = System.currentTimeMillis();
		System.out.println("calc start: "+data.getId()+" "+ag.getId());
		
		agent.setTaskId(data.getId());
		ret.addIntermediateResult(new PartDataChunk(0, null, 0, 0, ag.getId()));
		
		// This code iterates over the area in a bounding boxes
		// If a complete bounding box has is in the set the rest
		// is just set to -1 without calculation. This is more
		// efficient for areas with 'much black'.
		
		double stepx = (data.getXEnd()-data.getXStart())/data.getSizeX();
		double stepy = (data.getYEnd()-data.getYStart())/data.getSizeY();
		
		int xstart = 0;
		int xend = data.getSizeX()-1;
		int ystart = 0;
		int yend = data.getSizeY()-1;
		boolean justfill = false;
		short fillcol = -2;
		
		int numchunks = data.getChunkCount();
		//System.out.println("Chunk count: "+numchunks);

		int	size = data.getSizeX()*data.getSizeY();
		
		//int amount = (yend-ystart+1)*(xend-xstart+1);
		int perchunk = size/numchunks;
		int lastchunk = perchunk+size-(perchunk*numchunks);
		
		int cnt = 0;
		int pointcnt = 0;
		int chunkcnt = 0;
		short[] chunk = new short[perchunk];
		int xcs = xstart;
		int ycs = ystart;
		for(int yi=ystart; yi<=yend; yi++)
		{
			//short[] res = new short[data.getSizeX()];
			for(int xi=xstart; xi<=xend; xi++)
			{
				//res[xi] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xi*stepx, data.getYStart()+yi*stepy, data.getMax());
				chunk[pointcnt] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xi*stepx, data.getYStart()+yi*stepy, data.getMax());
				if(!justfill && xi==xstart)
					fillcol = chunk[pointcnt];
				pointcnt++;
				cnt++;
				
				if((chunkcnt==numchunks-1 && lastchunk==pointcnt) || (chunkcnt!=numchunks-1 && pointcnt==perchunk))
				{
					int psize = (yend-ystart+1)*(xend-xstart+1);
					System.out.println("cnt: "+cnt+" "+size+" "+psize);
					ret.addIntermediateResult(new PartDataChunk(cnt*100/size, chunk, xcs, ycs, ag.getId()));
					ag.waitForDelay(100).get(); // yield
					pointcnt = 0;
					chunkcnt++;
					chunk = new short[chunkcnt==numchunks-1? lastchunk: perchunk];
					xcs = xi+1;
					ycs = yi;
					if(xcs>xend)
					{
						xcs = xstart;
						ycs++;
					}
				}	
				//System.out.print(res[xi]+"-");
			}
			//System.out.println();
		}
		
		agent.setTaskId(null);
		//data.setData(res);
		//ret.setResult(data);
		
		//Long dur = (System.currentTimeMillis()-start)/1000;
		//System.out.println("calc end: "+data.getId()+" "+dur);
		
		ret.setFinished();
		
		System.out.println("calc finished: "+data.getId()+" "+ag.getId());
		
		return ret;
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
