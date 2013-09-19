package storageService;

import jadex.bridge.service.annotation.GuiClass;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.List;

/**
 * Storage client agent for interactive testing.
 */
@Description("This agent provides a basic chat service.")
@Agent
@RequiredServices({
	@RequiredService(name="storageKVservice", type=IStorageClientService.class, 
		binding=@Binding(scope=Binding.SCOPE_PLATFORM)),

})
@GuiClass(ClientKVGui.class)
public class ClientKVAgent {
	
	/** The underlying micro agent. */
	@Agent
	protected MicroAgent agent;
	
	public void storeKV(final String key, final String value) {
		IFuture<IStorageClientService> fut = agent.getServiceContainer()
				.getRequiredService("storageKVservice");
		fut.addResultListener(new DefaultResultListener<IStorageClientService>() {

			public void resultAvailable(IStorageClientService result) {
				result.writeAny(key, value);
			}
		});
	}
	
	/**
	 * Get a list of DBEntry that contain the whole database-content
	 * @return
	 */
	public IFuture<List<DBEntry>> getAllFut() {
		final Future<List<DBEntry>> ret = new Future<List<DBEntry>>();
		IFuture<IStorageClientService> fut = agent.getServiceContainer()
				.getRequiredService("storageKVservice");
		fut.addResultListener(new DefaultResultListener<IStorageClientService>() {

			public void resultAvailable(IStorageClientService result) {
				IFuture<List<DBEntry>> dbFut = result.getDB();
				dbFut.addResultListener(new DefaultResultListener<List<DBEntry>>() {

					public void resultAvailable(List<DBEntry> result) {
						ret.setResult(result);
					}
				});
				
			}
		});
		
		return ret;
	}


}
