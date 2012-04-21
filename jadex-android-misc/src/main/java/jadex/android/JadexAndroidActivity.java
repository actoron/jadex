package jadex.android;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.annotations.Classname;

import java.util.HashMap;
import java.util.UUID;

import android.util.Log;

/**
 * This is an Android Activity Class which provides needed Functionality and
 * comfort Features for Jadex Android Activities. It MUST be extended by every
 * Jadex-Android Activity.
 * 
 * @author Julian Kalinowski
 */
public class JadexAndroidActivity extends ContextProvidingActivity {
	
	public static final String KERNEL_COMPONENT = "component";
	public static final String KERNEL_MICRO = "micro";
	public static final String KERNEL_BPMN = "bpmn";
	public static final String KERNEL_BDI = "bdi";
	
	public static final String[] DEFAULT_KERNELS = new String[]{KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN};
	
	private JadexAndroidContext jadexAndroidContext;
	
	public JadexAndroidActivity() {
		super();
		jadexAndroidContext = JadexAndroidContext.getInstance();
	}
	
	/**
	 * Returns the External Access Object of the Jadex Platform.
	 * @return {@link IExternalAccess}
	 */
	public IExternalAccess getExternalPlatformAccess() throws JadexAndroidPlatformNotStartedError {
		checkIfJadexIsRunning("getExternalPlatformAccess()");
		return JadexAndroidContext.getInstance().getExternalPlattformAccess();
	}
	
	private void checkIfJadexIsRunning(String caller) {
		if (!jadexAndroidContext.isJadexRunning()) {
			throw new JadexAndroidPlatformNotStartedError(caller);		
		}
	}
	
	protected boolean isJadexPlatformRunning() {
		return jadexAndroidContext.isJadexRunning();
	}

	protected IFuture<IExternalAccess> startJadexPlatform() {
		return startJadexPlatform(DEFAULT_KERNELS, createRandomPlatformID());
	}
	
	protected IFuture<IExternalAccess> startJadexPlatform(final String[] kernels) {
		return startJadexPlatform(kernels, createRandomPlatformID());
	}
	
	protected IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId) {
		return startJadexPlatform(kernels, platformId, "");
		
	}
	
	protected IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId, final String options) {
		if (jadexAndroidContext.isJadexRunning()){
			throw new JadexAndroidError("Platform was already started!");
		}
		
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		final StringBuffer kernelString = new StringBuffer("\"");
		String sep = "";
		for (int i = 0; i < kernels.length; i++) {
			kernelString.append(sep);
			kernelString.append(kernels[i]);
			sep = ",";
		}
		kernelString.append("\"");
		
		final String defOptions = "-logging_level java.util.logging.Level.INFO" +
		" -extensions null" +
		" -wspublish false" +
		" -android true" +
		" -kernels " + kernelString.toString() +
		" -binarymessages true" +
//		" -tcptransport false" +
//		" -niotcptransport false" +
//		" -relaytransport true" +
//		" -relayaddress \"http://134.100.11.200:8080/jadex-platform-relay-web/\"" +				
//		" -saveonexit false -gui false" +
		" -autoshutdown false" +
		" -platformname " + platformId +
		" -saveonexit true -gui false" +
		" ";
		
		new Thread(new Runnable() {
			public void run() {
				IFuture<IExternalAccess> future = Starter
						.createPlatform((defOptions + options).split("\\s+"));
				future.addResultListener(new IResultListener<IExternalAccess>() {

					@Override
					public void resultAvailable(IExternalAccess result) {
						jadexAndroidContext.setExternalPlattformAccess(result);
						ret.setResult(result);
					}

					@Override
					public void exceptionOccurred(Exception exception) {
						ret.setException(exception);
					}
				});
			}
		}).start();
		
		return ret;
	}
	
	protected void shutdownJadexPlatform() {
		Log.d("jadex-android","Starting platform shutdown");
		ThreadSuspendable	sus	= new ThreadSuspendable();
		long start	= System.currentTimeMillis();
		long timeout	= 4500;	// Android issues hard kill (ANR) after 5 secs!
		//IExternalAccess	ea = platform.get(sus, timeout);
		getExternalPlatformAccess().killComponent().get(sus, start+timeout-System.currentTimeMillis());
		jadexAndroidContext.setExternalPlattformAccess(null);
		Log.d("jadex-android", "Platform shutdown completed");
	}

	protected IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz) {
		checkIfJadexIsRunning("startMicroAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>() {

					public void resultAvailable(IComponentManagementService cms) {
						HashMap<String, Object> args = new HashMap<String, Object>();

						cms.createComponent(
								name,
								clazz.getName().replaceAll("\\.",
										"/")
										+ ".class", new CreationInfo(args),
								null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
					}
				});
		
		return ret;
	}
	
	protected IFuture<IComponentIdentifier> startBPMNAgent(final String name, final String modelPath) {
		checkIfJadexIsRunning("startBPMNAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>() {

					public void resultAvailable(IComponentManagementService cms) {
						HashMap<String, Object> args = new HashMap<String, Object>();

						args.put("androidContext",
								JadexAndroidActivity.this);
						cms.createComponent(
								name,
								modelPath,
								new CreationInfo(args), null)
								.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
					}
				});
		
		return ret;
	}
	
	private IFuture<IComponentManagementService> getCMS() {
		return jadexAndroidContext.getExternalPlattformAccess().scheduleStep(new IComponentStep<IComponentManagementService>() {
			@Classname("create-component")
			public IFuture<IComponentManagementService> execute(
					IInternalAccess ia) {
				Future<IComponentManagementService> ret = new Future<IComponentManagementService>();
				SServiceProvider
						.getService(ia.getServiceContainer(),
								IComponentManagementService.class,
								RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(
								ia.createResultListener(new DelegationResultListener<IComponentManagementService>(
										ret)));

				return ret;
			}
		});
	}
	
	protected void registerEventReceiver(String eventName, EventReceiver<?> rec) {
		jadexAndroidContext.registerEventListener(eventName, rec);
	}
	
	protected void unregisterEventReceiver(String eventName, EventReceiver<?> rec) {
		jadexAndroidContext.unregisterEventListener(eventName, rec);
	}
	
	protected String createRandomPlatformID() {
		UUID randomUUID = UUID.randomUUID();
		return "and-" + randomUUID.toString().substring(0, 5);
	}
}
