package jadex.micro.examples.lottery;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

@Service
public interface ILotteryService
{
	public ISubscriptionIntermediateFuture<String> subscribeToLottery();
	
	public IFuture<Boolean> claimItem(String item);
}
