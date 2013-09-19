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
import java.util.Iterator;
import java.util.List;

@Description("This agent uses the storage service.")
@Agent
@RequiredServices(@RequiredService(name = "storageService", type = IStorageClientService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
public class ClientTest1Agent {

	/** The underlying micro agent. */
	@Agent
	protected MicroAgent agent;

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
				// tests
				// update(storageClientService);
				// writeAny(storageClientService);
//				writeAll(storageClientService);
				// read(storageClientService);
				 readAll(storageClientService);

			}

		});

	}

	/**
	 * test update
	 * 
	 * @param storageClientService
	 */
	void update(final IStorageClientService storageClientService) {
		final String key = "test";
		final Serializable value = key + "value";
		IFuture<Boolean> futWrite = storageClientService.writeAny(key, value);
		futWrite.addResultListener(new DefaultResultListener<Boolean>() {

			@Override
			public void resultAvailable(Boolean result) {
				if (!result) {
					System.err.println("test update write unsuccessful");
					return;
				}
				System.out.println("test update write successful");
				IFuture<List<VersionValuePair>> futRead = storageClientService
						.read(key);
				futRead.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

					@Override
					public void resultAvailable(List<VersionValuePair> result) {
						if (result.size() != 1) {
							System.err.println("test update read unsuccessful");
							return;
						}
						VersionValuePair vvp = result.get(0);
						System.out.println("Key=" + key + ", Version="
								+ vvp.getVersion().toString() + ", Value="
								+ vvp.getValue());
						IFuture<Boolean> futUpdate = storageClientService
								.updateAny(key, "updateValue", vvp.getVersion());
						futUpdate
								.addResultListener(new DefaultResultListener<Boolean>() {

									@Override
									public void resultAvailable(Boolean result) {
										if (!result) {
											System.err
													.println("test update unsuccessful");
											return;
										}
										System.out
												.println("test update successful");
										IFuture<List<VersionValuePair>> futRead = storageClientService
												.read(key);
										futRead.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

											@Override
											public void resultAvailable(
													List<VersionValuePair> result) {
												if (result.size() != 1) {
													System.err
															.println("test update read unsuccessful");
													return;
												}
												VersionValuePair vvp = result
														.get(0);
												System.out.println("Key="
														+ key
														+ ", Version="
														+ vvp.getVersion()
																.toString()
														+ ", Value="
														+ vvp.getValue());
											}
										});
									}
								});
					}
				});

			}
		});

	}

	/**
	 * test writeAll
	 * 
	 * @param storageClientService
	 */
	void writeAll(IStorageClientService storageClientService) {
		final long start = System.currentTimeMillis();
		Timestamp ts = new Timestamp(start);
		System.out.println("Start: " + ts.toString());

		for (int i = 1; i < 150; i++) {
			final String key = "testNeu" + String.valueOf(i);
			Serializable value = key + "value";

			// // no result on console
			// storageClientService.writeAll(key, value);

			// result on console
			IFuture<Boolean> fut = storageClientService.writeAll(key, value);
			fut.addResultListener(new DefaultResultListener<Boolean>() {

				@Override
				public void resultAvailable(Boolean result) {
					System.out.println("writeAll: " + key + ": " + result);
				}
			});

		}
		String key = "testLast";
		Serializable value = key + "value";
		IFuture<Boolean> futWrite = storageClientService.writeAll(key, value);
		futWrite.addResultListener(new DefaultResultListener<Boolean>() {

			public void resultAvailable(Boolean result) {
				long end = System.currentTimeMillis();
				Timestamp ts = new Timestamp(end);
				System.out.println("--------------------------------------");
				System.out.println("Done: " + result + ", " + ts.toString());
				System.out.println("writeAll Total time= " + (end - start));
			}
		});
	}

	/**
	 * tese writeAny
	 * 
	 * @param storageClientService
	 */
	void writeAny(IStorageClientService storageClientService) {
		final long start = System.currentTimeMillis();
		Timestamp ts = new Timestamp(start);
		System.out.println("Start: " + ts.toString());

		for (int i = 0; i < 5; i++) {
			String key = "testNeu" + String.valueOf(i);
			Serializable value = key + "value";
			storageClientService.writeAny(key, value);
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

	void read(IStorageClientService storageClientService) {
		IFuture<List<VersionValuePair>> futRead = storageClientService
				.read("testlast");
		System.out.println("Read testlast");
		futRead.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

			@Override
			public void resultAvailable(List<VersionValuePair> result) {
				if (result.size() == 0) {
					System.out.println("Empty list");
				} else {
					Iterator<VersionValuePair> it = result.iterator();
					while (it.hasNext()) {
						VersionValuePair vvp = it.next();
						System.out.println(vvp.toString());
					}
				}
			}

		});
	}

	void readAll(IStorageClientService storageClientService) {
		for (int i = 1; i < 150; i++) {
			final String key = "testNeu" + String.valueOf(i);
			IFuture<List<VersionValuePair>> fut = storageClientService
					.readAll(key);
			fut.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

				@Override
				public void resultAvailable(List<VersionValuePair> result) {
					System.out.println("ReadAll resultAvailable: Key=" + key);
					if (result.size() == 0) {
						System.out.println("Empty list");
					} else {
						Iterator<VersionValuePair> it = result.iterator();
						while (it.hasNext()) {
							VersionValuePair vvp = it.next();
							System.out.println(vvp.toString());
						}
					}
				}

			});
		}
		IFuture<List<VersionValuePair>> fut = storageClientService
				.readAll("testlast");
//		System.out.println("ReadAll testlast");
		fut.addResultListener(new DefaultResultListener<List<VersionValuePair>>() {

			@Override
			public void resultAvailable(List<VersionValuePair> result) {
				System.out.println("ReadAll resultAvailable: Key=testlast");
				if (result.size() == 0) {
					System.out.println("Empty list");
				} else {
					Iterator<VersionValuePair> it = result.iterator();
					while (it.hasNext()) {
						VersionValuePair vvp = it.next();
						System.out.println(vvp.toString());
					}
				}
			}

		});
	}

}