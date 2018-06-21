package jadex.android.controlcenter;

import jadex.bridge.service.IService;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class ViewableFilter implements IAsyncFilter {

	public static final String COMPONENTVIEWER_VIEWERCLASS = "componentviewer.viewerclass";

	/** Static proxy filter instance. */
	public static IAsyncFilter VIEWABLE_FILTER = new ViewableFilter();

	public IFuture<Boolean> filter(Object obj) {
		Future<Boolean> ret = new Future<Boolean>();
		if (obj instanceof IService) {
			IService service = (IService) obj;
			Object clid = service.getPropertyMap().get(COMPONENTVIEWER_VIEWERCLASS);
			if (clid != null) {
				ret.setResult(true);
			} else {
				ret.setResult(false);
			}
		} else {
			ret.setResult(false);
		}
		return ret;
	}

}
