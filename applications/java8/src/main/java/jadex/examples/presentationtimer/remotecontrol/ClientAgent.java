package jadex.examples.presentationtimer.remotecontrol;

import javax.swing.SwingUtilities;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.examples.presentationtimer.common.ICountdownService;
import jadex.examples.presentationtimer.common.ICountdownService.ICountdownListener;
import jadex.examples.presentationtimer.common.State;
import jadex.examples.presentationtimer.remotecontrol.ui.CDListItem;
import jadex.examples.presentationtimer.remotecontrol.ui.CDListModel;
import jadex.examples.presentationtimer.remotecontrol.ui.ClientFrame;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


@Agent
@RequiredServices({@RequiredService(type = ICountdownService.class, name = "cds", binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL))})
public class ClientAgent
{

	private ICountdownService	cds;

	@Agent
	private IInternalAccess		access;

	@AgentCreated
	public void onCreate()
	{
		System.out.println("Client Agent created");
	}

//	@AgentService(name = "cds")
	public void injectService(ICountdownService cds)
	{
		System.out.println("Service injected: " + cds);
	}

	@AgentBody
	public void body()
	{
		ClientFrame clientFrame = new ClientFrame();
		clientFrame.setVisible(true);
		CDListModel listmodel = clientFrame.getListmodel();
		
		searchCdServices().addIntermediateResultListener(cdService -> {
			System.out.println("Service found: " + cdService + " of class: " + cdService.getClass().getName());
			CDListItem item = new CDListItem(cdService);
			item.setStatus(cdService.getState().get());
			item.setTime(cdService.getTime().get());
			SwingUtilities.invokeLater(()-> listmodel.addElement(item));
		});
		
	}
	SubscriptionIntermediateFuture<ICountdownService> subscription = new SubscriptionIntermediateFuture<ICountdownService>();

	private ISubscriptionIntermediateFuture<ICountdownService> searchCdServices()
	{
		IIntermediateFuture<ICountdownService> searchServices = access.getComponentFeature(IRequiredServicesFeature.class).searchServices(ICountdownService.class, RequiredServiceInfo.SCOPE_GLOBAL);
		searchServices.addIntermediateResultListener(cdService -> {
			subscription.addIntermediateResult(cdService);
		}, () -> {
			System.out.println("Search finished. Re-scheduling search.");

			access.getComponentFeature(IExecutionFeature.class).waitForDelay(10000, new IComponentStep<Void>()
			{

				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					searchCdServices();
					return Future.DONE;
				}
			});
		});
//		searchServices.addResultListener(new IntermediateDefaultResultListener<ICountdownService>()
//		{
//
//			@Override
//			public void intermediateResultAvailable(ICountdownService result)
//			{
//				super.intermediateResultAvailable(result);
//				subscription.addIntermediateResult(result);
//			}
//
//			@Override
//			public void finished()
//			{
//				super.finished();
//				System.out.println("Search finished. Re-scheduling search.");
//
//				access.getExternalAccess().scheduleStep(new IComponentStep<Void>()
//				{
//
//					@Override
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						searchCdServices();
//						return Future.DONE;
//					}
//				}, 10000);
//			}
//
//		});
		return subscription;

	}

}


// @Reference
class MyListener implements ICountdownListener
{


	@Override
	public void timeChanged(String timeString)
	{
		System.out.println("receveid: Time changed: " + timeString);
	}

	@Override
	public void stateChanged(State state)
	{
		System.out.println("receveid: state: " + state);
	}

}
