package jadex.bdiv3.tutorial.stamp;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

@Service
public interface IStampService {
	public IFuture<Void> stamp(IComponentIdentifier wp, String text);
}
