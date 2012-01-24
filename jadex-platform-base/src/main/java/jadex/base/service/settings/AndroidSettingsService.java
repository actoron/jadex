package jadex.base.service.settings;

import jadex.base.service.android.IAndroidContextService;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.io.File;

/**
 *  Android settings service implementation.
 */
public class AndroidSettingsService extends SettingsService {

	protected IAndroidContextService contextService;

	/**
	 * Creates an Android Settings Service
	 * @param prefix
	 * @param access
	 * @param saveonexit
	 */
	public AndroidSettingsService(String prefix, IInternalAccess access,
			boolean saveonexit) {
		super(prefix, access, saveonexit);
	}

	@Override
	public IFuture<Void> startService() {
		IFuture<IAndroidContextService> service = SServiceProvider.getService(access.getServiceContainer(), IAndroidContextService.class);
		service.addResultListener(new DefaultResultListener<IAndroidContextService>() {
			@Override
			public void resultAvailable(IAndroidContextService result) {
				contextService = result;
			}
		});
		return super.startService();
	}

	@Override
	protected File getFile(String path) {
		return contextService.getFile(path);
	}
}
