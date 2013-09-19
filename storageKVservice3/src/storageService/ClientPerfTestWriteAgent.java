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

import java.io.Serializable;
import java.sql.Timestamp;

@Description("This agent test the storage service.")
@Agent
@RequiredServices(@RequiredService(name = "storageService", type = IStorageClientService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class ClientPerfTestWriteAgent {

	/** The underlying micro agent. */
	@Agent
	protected MicroAgent agent;

	private int numberOfTests = 10000;

	/**
	 * Execute the functional body of the agent. Is only called once.
	 */
	@AgentBody
	public void executeBody() {
		IFuture<IStorageClientService> futSC = agent.getServiceContainer()
				.getRequiredService("storageService");
		futSC.addResultListener(new DefaultResultListener<IStorageClientService>() {

			@Override
			public void resultAvailable(
					IStorageClientService storageClientService) {

				writeAny(storageClientService);

				// writeAny2();
			}

		});

	}

	/**
	 * test writeAny
	 * @param storageClientService
	 */
	void writeAny(IStorageClientService storageClientService) {
		final long start = System.currentTimeMillis();
		Timestamp ts = new Timestamp(start);
		System.out.println("Start: " + ts.toString());

		for (int i = 1; i < numberOfTests; i++) {
			final String key = "testNeu" + String.valueOf(i);
			Serializable value = key + "value";
			
			// write without result on console
			storageClientService.writeAny(key, value);
			
//			// write with result on console
//			IFuture<Boolean> fut = storageClientService.writeAny(key, value);
//			fut.addResultListener(new DefaultResultListener<Boolean>() {
//
//				@Override
//				public void resultAvailable(Boolean result) {
//					System.out.println(key + ": " + result);
//				}
//			});
		}
		String key = "testlast";
		Serializable value = key + "value";
		IFuture<Boolean> futWrite = storageClientService.writeAny(key, value);
		futWrite.addResultListener(new DefaultResultListener<Boolean>() {

			public void resultAvailable(Boolean result) {
				long end = System.currentTimeMillis();
				Timestamp ts = new Timestamp(end);
				System.out.println("Done: " + result + ", " + ts.toString());
				System.out.println("writeAny Total time= " + (end - start));
			}
		});
	}
	
	/**
	 * Moving the servicesearch inside the loop.
	 * Doesn't seem to change performance significantly.
	 */
	void writeAny2() {
		final long start = System.currentTimeMillis();
		Timestamp ts = new Timestamp(start);
		System.out.println("Start: " + ts.toString());

		for (int i = 1; i < numberOfTests; i++) {
			IFuture<IStorageClientService> futSC = agent.getServiceContainer()
					.getRequiredService("storageService");
			futSC.addResultListener(new DefaultResultListener<IStorageClientService>() {

				@Override
				public void resultAvailable(
						IStorageClientService storageClientService) {

					String key = "testNeu" + String.valueOf(Math.random());
					Serializable value = key + "value";
					storageClientService.writeAny(key, value);

				}

			});	
		}
		final String key = "testlast";
		final Serializable value = key + "value";
		
		IFuture<IStorageClientService> futSC = agent.getServiceContainer()
				.getRequiredService("storageService");
		futSC.addResultListener(new DefaultResultListener<IStorageClientService>() {

			@Override
			public void resultAvailable(
					IStorageClientService storageClientService) {

				IFuture<Boolean> futWrite = storageClientService.writeAny(key, value);
				futWrite.addResultListener(new DefaultResultListener<Boolean>() {

					public void resultAvailable(Boolean result) {
						long end = System.currentTimeMillis();
						Timestamp ts = new Timestamp(end);
						System.out.println("Done: " + result + ", " + ts.toString());
						System.out.println("writeAny Total time= " + (end - start));
					}
				});

			}

		});	
		
		
	}


}