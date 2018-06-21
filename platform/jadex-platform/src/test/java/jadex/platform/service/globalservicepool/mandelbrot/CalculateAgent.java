package jadex.platform.service.globalservicepool.mandelbrot;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Calculate agent allows calculating the colors of an area using a calculate service.
 */
@Description("Agent offering a calculate service.")
@ProvidedServices(@ProvidedService(type=ICalculateService.class))
@Arguments({
	@Argument(name="diedelay", description="Agent kills itself when no job arrives in the delay interval.", clazz=Long.class, defaultvalue="new Long(1000)"),
	@Argument(name="drawdelay", description="Draw delay.", clazz=Long.class, defaultvalue="new Long(10)")
})
@Configurations({
	@Configuration(name="default"),
	@Configuration(name="long lived", arguments={@NameValue(name="delay", value="-1")})
})
@Agent(synchronous=Boolean3.FALSE)
public class CalculateAgent implements ICalculateService
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	/** Flag indicating that the agent had a job. */
	protected boolean hadjob;
	
	@AgentArgument
	protected long drawdelay;
	
	@AgentArgument
	protected long diedelay;
	
	//-------- methods --------
	
	/**
	 *  Execute the body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!isHadJob())
				{
//					System.out.println("killComponent: "+getComponentIdentifier());
//					killComponent();
					ret.setResult(null);
				}
				setHadJob(false);
				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(diedelay, this);
				return IFuture.DONE;
			}
		};
		if(diedelay>0)
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(diedelay, step);
		
		return ret;
	}
	
	/**
	 *  Set the hadjob.
	 *  @param hadjob The hadjob to set.
	 */
	public void setHadJob(boolean hadjob)
	{
		this.hadjob = hadjob;
	}
	
	/**
	 *  Get the hadjob.
	 *  @return The hadjob.
	 */
	public boolean isHadJob()
	{
		return hadjob;
	}
	
	//-------- methods --------
	
	/**
	 *  Calculate colors for an area of points.
	 *  @param data	The area to be calculated.
	 *  @return	A future containing the calculated area.
	 */
	public ISubscriptionIntermediateFuture<CalculateEvent> calculateArea(final AreaData data)
	{
		final SubscriptionIntermediateFuture<CalculateEvent> ret = (SubscriptionIntermediateFuture)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
//			System.out.println("calc: "+data.getId()+" "+agent.getComponentIdentifier());
		
		setHadJob(true);
		
		// This code iterates over the area in a bounding boxes
		// If a complete bounding box has is in the set the rest
		// is just set to -1 without calculation. This is more
		// efficient for areas with 'much black'.
		
//			Future<AreaData> ret = new Future<AreaData>();
		
		agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
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
				int last = -1;
				
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
						//agent.setProgress(cnt*100/size);
					}
//						ret.addIntermediateResult(new CalculateEvent(cnt*100/size));
					last = reportProgress(cnt, size, last, ret);
					
					ystart++;
					if(ystart>yend)
						break;
					for(int yi=ystart; yi<=yend; yi++)
					{
						res[xend][yi] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xend*stepx, data.getYStart()+yi*stepy, data.getMax());
						if(allin && res[xend][yi]!=fillcol)
							allin = false;
						cnt++;
//							agent.setProgress(cnt*100/size);
					}
//						ret.addIntermediateResult(new CalculateEvent(cnt*100/size));
					last = reportProgress(cnt, size, last, ret);
					
					xend--;
					if(xstart>xend)
						break;
					for(int xi=xend; xi>=xstart; xi--)
					{
						res[xi][yend] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xi*stepx, data.getYStart()+yend*stepy, data.getMax());
						if(allin && res[xi][yend]!=fillcol)
							allin = false;
						cnt++;
//							agent.setProgress(cnt*100/size);
					}
//						ret.addIntermediateResult(new CalculateEvent(cnt*100/size));
					last = reportProgress(cnt, size, last, ret);
					
					yend--;
					if(ystart>yend)
						break;
					for(int yi=yend; yi>=ystart; yi--)
					{
						res[xstart][yi] = justfill? fillcol: data.getAlgorithm().determineColor(data.getXStart()+xstart*stepx, data.getYStart()+yi*stepy, data.getMax());
						if(allin && res[xstart][yi]!=fillcol)
							allin = false;
						cnt++;
//							agent.setProgress(cnt*100/size);
					}
//						ret.addIntermediateResult(new CalculateEvent(cnt*100/size));
					last = reportProgress(cnt, size, last, ret);
					xstart++;
					
					if(drawdelay>0)
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(drawdelay).get();
					
					if(allin && usejustfill)
					{
						justfill = true;
//							System.out.println("justfill"+fillcol);
						allin = false;
					}
					else if(!justfill)
					{
						allin = true;
					}
				}
				
				data.setData(res);
				
				ret.addIntermediateResult(new CalculateEvent(100, agent.getComponentIdentifier()));
				ret.addIntermediateResult(new CalculateEvent(data, agent.getComponentIdentifier()));
				ret.setFinished();
				
				return IFuture.DONE;
			}
		});
		
//			ret.addIntermediateResult(new CalculateEvent(100));
		
//			System.out.println("calc finished: "+data.getId()+" "+agent.getComponentIdentifier());
		return ret;
	}
	
	/**
	 * 
	 */
	protected int reportProgress(int cnt, int size, int last, IntermediateFuture<CalculateEvent> ret)
	{
		int cur = cnt*100/size;
		int tst = (cur/10)%10;
		if(tst>last)
		{
//				System.out.println("sending ires: "+cur);
			ret.addIntermediateResult(new CalculateEvent(cur, agent.getComponentIdentifier()));
			last=tst;
		}
		return last;
	}
	
//		/**
//		 *  Calculate colors for an area of points.
//		 */
//		public IFuture calculateArea(AreaData data)
//		{
////			System.out.println("calc: "+data);
//			
//			Future ret = new Future();
//			
//			double stepx = (data.getXEnd()-data.getXStart())/data.getSizeX();
//			double stepy = (data.getYEnd()-data.getYStart())/data.getSizeY();
//			
//			int[][] res = new int[data.getSizeX()][data.getSizeY()];
//			
//			for(int yi=0; yi<data.getSizeY(); yi++)
//			{
//				for(int xi=0; xi<data.getSizeX(); xi++)
//				{
//					res[xi][yi] = determineColor(data.getXStart()+xi*stepx, data.getYStart()+yi*stepy, data.getMax());
//				}
//			}
//			
//			data.setData(res);
//			ret.setResult(data);
//			
//			return ret;
//		}

//		/**
//		 * 
//		 */
//		public static void main(String[] args)
//		{
//			AreaData ad = new AreaData(0, 1, 0, 1, 5, 5, 3, 1, 1, null, null);
//			calculateArea(ad);
//		}
}
