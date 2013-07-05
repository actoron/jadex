package jadex.android.clientapp;

import jadex.android.commons.JadexPlatformOptions;
import jadex.android.service.JadexPlatformService;
import jadex.bdi.examples.puzzle.Board;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class MyPlatformService extends JadexPlatformService
{
	private PlatformListener listener;
	private IComponentIdentifier platformId;
	private Handler handler;
	public SokratesListener soListener;
	protected IComponentIdentifier sokratesComponent;
	
	public interface PlatformListener
	{
		public void platformStarted();
		public void platformStarting();
	}
	
	public interface SokratesListener
	{
		public void handleEvent(PropertyChangeEvent event);

		public void setBoard(Board board);
		
		public void showMessage(String text);
	}

	public MyPlatformService()
	{
		setPlatformAutostart(false);
		setPlatformKernels(JadexPlatformOptions.KERNEL_MICRO, JadexPlatformOptions.KERNEL_COMPONENT, JadexPlatformOptions.KERNEL_BDI);
		setPlatformName("Sokrates");
		handler = new Handler();
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return new PlatformBinder();
	}
	
	public class PlatformBinder extends Binder {
		

		public IFuture<IExternalAccess> startPlatform() {
			IFuture<IExternalAccess> result;
			if (platformId != null) {
				listener.platformStarting();
				listener.platformStarted();
				result = new Future<IExternalAccess>(getPlatformAccess(platformId));
			} else {
				result = MyPlatformService.this.startPlatform();
			}
			return result;
		}
		
		public IFuture<IComponentIdentifier> startAgent() {
			return MyPlatformService.this.startComponent(platformId, "Component", "jadex/android/clientapp/bditest/HelloWorld.agent.xml");
		}
		
		public IFuture<Void> startSokrates() {
			return createSokratesGame();
		}
		
		public void setPlatformListener (PlatformListener l) {
			listener = l;
		}
		
		public void setSokratesListener (SokratesListener l) {
			soListener = l;
		}

		public IFuture<Void> stopSokrates()
		{
			final Future<Void> result = new Future<Void>();
			if (platformId != null && sokratesComponent != null) {
				getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
				{

					@Override
					public void resultAvailable(IComponentManagementService cms)
					{
						cms.destroyComponent(sokratesComponent).addResultListener(new DefaultResultListener<Map<String,Object>>()
						{

							@Override
							public void resultAvailable(Map<String, Object> cmsResult)
							{
								result.setResult(null);
							}
						});
					}
				});
			}
			return result;
		}
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		super.onPlatformStarted(platform);
		this.platformId = platform.getComponentIdentifier();
		listener.platformStarted();
	}
	
	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		listener.platformStarting();
	}
	
	public void post(Runnable runnable)
	{
		handler.post(runnable);
	}
	
	private IFuture<Void> createSokratesGame() {
		CreationInfo ci = new CreationInfo();
		HashMap<String, Object> args = new HashMap<String,Object>();
		args.put("gui_listener", soListener);
		ci.setArguments(args);
		IFuture<IComponentIdentifier> future = MyPlatformService.this.startComponent(platformId, "Sokrates", "jadex/bdi/examples/puzzle/Sokrates.agent.xml", ci);
		
		future.addResultListener(new DefaultResultListener<IComponentIdentifier>()
		{

			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				sokratesComponent = result;
			}
		});
		
		return IFuture.DONE;
		
	}

}
