package storageService;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

@Description("This agent uses the storage service.")
@Agent
@RequiredServices(@RequiredService(
		name = "storageService",
		type = IStorageClientService.class,
		binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class ClientPerfTestReadAgent {

	/** The underlying micro agent. */
	@Agent
	protected MicroAgent agent;
	
	private int numberOfTests = 600;

	/**
	 * Execute the functional body of the agent. Is only called once.
	 */
	@AgentBody
	public void executeBody() {
		IFuture<IStorageClientService> futSC = agent.getServiceContainer()
				.getRequiredService("storageService");
		futSC.addResultListener(new DefaultResultListener<IStorageClientService>() {

			public void resultAvailable(
					IStorageClientService storageClientService) {

				read(storageClientService);
				
			}

		});

	}

	

	/**
	 * test read performance.
	 * @param storageClientService
	 */
	private void read(IStorageClientService storageClientService) {

		final long start = System.currentTimeMillis();
		Timestamp ts = new Timestamp(start);
		System.out.println("Start: " + ts.toString());

		for (int i = 1; i < numberOfTests; i++) {
			final String key = "testNeu" + String.valueOf(i);
			IFuture<List<VersionValuePair>> futRead = storageClientService
					.read(key);
			futRead.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

				@Override
				public void resultAvailable(List<VersionValuePair> result) {
					if (result.size() == 0) {
						System.out.println(key + "=Empty list");
					} else {
						Iterator<VersionValuePair> it = result.iterator();
						while (it.hasNext()) {
							VersionValuePair vvp = it.next();
							System.out.println(key + "=" + vvp.toString());
						}
					}
				}

			});
		}
		final String key = "testlast";
		IFuture<List<VersionValuePair>> futRead = storageClientService
				.read(key);
		futRead.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

			@Override
			public void resultAvailable(List<VersionValuePair> result) {
				if (result.size() == 0) {
					System.out.println(key + "=Empty list");
				} else {
					Iterator<VersionValuePair> it = result.iterator();
					while (it.hasNext()) {
						VersionValuePair vvp = it.next();
						long end = System.currentTimeMillis();
						;
						Timestamp ts2 = new Timestamp(end);
						System.out.println(key + "=" + vvp.toString());
						System.out.println("Read test finished: "
								+ ts2.toString());
						System.out.println("Read test with " + numberOfTests
								+ ". Total time: " + (end - start));
					}
				}
			}

		});

	}


}