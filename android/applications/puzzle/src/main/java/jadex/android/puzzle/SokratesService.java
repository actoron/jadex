package jadex.android.puzzle;

import jadex.android.commons.JadexPlatformOptions;
import jadex.android.service.JadexPlatformService;
import jadex.base.IPlatformConfiguration;
import jadex.bdi.examples.puzzle.Board;
import jadex.bdiv3.examples.puzzle.IBoard;
import jadex.bdiv3.examples.puzzle.SokratesBDI;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.cms.CreationInfo;
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

@Reference	// Hack??? Is transferred in creation info.
public class SokratesService extends JadexPlatformService
{
	private PlatformListener listener;
	private IComponentIdentifier platformId;
	private Handler handler;
	public SokratesListener soListener;
	protected IComponentIdentifier sokratesComponent;
	private boolean sokratesRunning;

	public interface PlatformListener
	{
		public void platformStarted();
		public void platformStarting();
	}

	@Reference	// Hack??? Is transferred in creation info.
	public interface SokratesListener
	{
		public void handleEvent(PropertyChangeEvent event);

		public void setBoard(IBoard board);

		public void showMessage(String text);
	}

	public SokratesService()
	{
		setPlatformAutostart(false);
		setPlatformName("Sokrates");
		IPlatformConfiguration config = getPlatformConfiguration();
		config.setValue("kernel_component", "true");
		config.setValue("kernel_bdiv3", "true");
		setSharedPlatform(false);
		handler = new Handler();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new PlatformBinder();
	}

	public class PlatformBinder extends Binder
	{

		public IFuture<IExternalAccess> startPlatform()
		{
			IFuture<IExternalAccess> result;
			if (platformId != null)
			{
				listener.platformStarting();
				listener.platformStarted();
				result = new Future<IExternalAccess>(getPlatformAccess(platformId));
			}
			else
			{
				result = SokratesService.this.startPlatform();
			}
			return result;
		}

		public IFuture<Void> startSokrates()
		{
			return createSokratesGame(false);
		}
		
		public IFuture<Void> startSokratesBench()
		{
			return createSokratesGame(true);
		}
		
		public IFuture<Void> startSokratesV3()
		{
			return createSokratesGameV3(false);
		}
		
		public IFuture<Void> startSokratesV3Bench()
		{
			return createSokratesGameV3(true);
		}

		public void setPlatformListener(PlatformListener l)
		{
			listener = l;
		}

		public void setSokratesListener(SokratesListener l)
		{
			soListener = l;
		}

		public boolean isSokratesRunning()
		{
			return sokratesRunning;
		}

		public synchronized IFuture<Void> stopSokrates()
		{
			final Future<Void> result = new Future<Void>();
			if (platformId != null && sokratesComponent != null)
			{
				new Thread() {
					@Override
					public void run() {
						getPlatformAccess().killComponent(sokratesComponent).addResultListener(new DefaultResultListener<Map<String, Object>>()
						{

							@Override
							public void resultAvailable(Map<String, Object> cmsResult)
							{
								result.setResult(null);
							}

							@Override
							public void exceptionOccurred(Exception exception) {
								result.setResult(null);
							}
						});
					}
				}.start();
			} else {
				result.setResult(null);
			}
			sokratesRunning = false;
			return result;
		}
	}

	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		super.onPlatformStarted(platform);
		this.platformId = platform.getId();
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

	private synchronized IFuture<Void> createSokratesGame(final boolean benchmark)
	{
		if (!sokratesRunning)
		{
			final CreationInfo ci = new CreationInfo();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("gui_listener", soListener);
			long delay = 500;
			if (benchmark) {
				delay = 0;
			}
			args.put("delay", delay);
			ci.setArguments(args);

			new Thread() {
				@Override
				public void run() {
					String agentXml;
					if (benchmark) {
						agentXml = "jadex/bdi/examples/puzzle/Benchmark.agent.xml";
					} else {
						agentXml = "jadex/bdi/examples/puzzle/Sokrates.agent.xml";
					}
					IFuture<IComponentIdentifier> future = SokratesService.this.startComponent(platformId, "Sokrates",
							agentXml, ci, new DefaultResultListener<Map<String,Object>>() {

								@Override
								public void resultAvailable(Map<String, Object> result) {
									sokratesComponent = null;
									sokratesRunning = false;
								}
							});

					future.addResultListener(new DefaultResultListener<IComponentIdentifier>() {

						@Override
						public void resultAvailable(IComponentIdentifier result) {
							sokratesComponent = result;
						}

						@Override
						public void exceptionOccurred(Exception exception) {
							exception.printStackTrace();
						}
					});

					sokratesRunning = true;
				}
			}.start();



		}
		return IFuture.DONE;

	}
	
	private synchronized IFuture<Void> createSokratesGameV3(boolean benchmark)
	{
		if (!sokratesRunning)
		{
			final CreationInfo ci = new CreationInfo();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("gui_listener", soListener);
			long delay = 500;
			if (benchmark) {
				delay = 0;
			}
			args.put("delay", delay);
			ci.setArguments(args);
			new Thread() {
				@Override
				public void run() {
					IFuture<IComponentIdentifier> future = SokratesService.this.startComponent(platformId, "Sokrates",
							SokratesBDI.class, ci);

					future.addResultListener(new DefaultResultListener<IComponentIdentifier>() {

						@Override
						public void resultAvailable(IComponentIdentifier result) {
							sokratesComponent = result;
						}
					});

					sokratesRunning = true;

				}
			}.start();
		}
		return IFuture.DONE;

	}

}
