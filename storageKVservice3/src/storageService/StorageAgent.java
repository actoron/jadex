package storageService;

import jadex.bridge.service.IService;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.CannotProceedException;

import storageService.exceptions.NoSuchVersionException;
import storageService.exceptions.ObsoleteVersionException;

/**
 *  This agent provides a key value storage service.
 */
@Description("This agent provides a key value storage service.")
@Agent
@ProvidedServices({
	@ProvidedService(type=IStorageClientService.class,
		implementation=@Implementation(value=StorageClientService.class)),
	@ProvidedService(type=IStorageNodeService.class,
		implementation=@Implementation(value=StorageNodeService.class))
})
@RequiredServices({
	@RequiredService(name="nodeServices", type=IStorageNodeService.class, multiple=true,
		binding=@Binding(dynamic=false, scope=Binding.SCOPE_GLOBAL))
})
public class StorageAgent {

	/** The agent. */
	@Agent
	protected MicroAgent agent;

	private DB_DerbyConnect db;
	private String dbPath = System.getProperty("user.home") + "/jadexStorage/";
	private String dbName = "derby";
	private String nodeID;
	private boolean localOnly = false;

	/**
	 * Execute the functional body of the agent. Is only called once.
	 * 
	 * Connect to and initialize local database.
	 */
	@AgentBody
	public void executeBody() {
		nodeID = agent.getComponentIdentifier().getPlatformName();

		// Assign DB connector and create standard table if it doesn't exist
		try {
			db = new DB_DerbyConnect(dbPath, dbName, nodeID);
		} catch (CannotProceedException e) {
			// This happens when multiple platforms from the same
			// machine try to access the same DB. Retry with different db.
			System.out
					.println("Probably trying to connect to a db that is already connected: "
							+ e.getMessage());
			try {
				db = new DB_DerbyConnect(dbPath, dbName + "2", nodeID);
			} catch (CannotProceedException e1) {
				System.err.println("DB really can't be started. Retry failed.");
				e1.printStackTrace();
			}
		}
		synchronizeData();
	}

	protected void setUpForUnitTest() throws CannotProceedException {
		nodeID = "node42";
		db = new DB_DerbyConnect(dbPath, dbName, nodeID);
		localOnly = true;
	}

	/**
	 * Write local and iff successful, send update to other nodes. Will
	 * overwrite all previous local versions
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public IFuture<Boolean> write(final String key, final Object value) {
		IFuture<Version> fut = db.writeAndReplace(key, value);
		fut.addResultListener(new DefaultResultListener<Version>() {

			@Override
			public void resultAvailable(Version version) {
				sendUpdateToAll(key, version, value);
			}
		});

		return new Future<Boolean>(true);
	}

	/**
	 * Write on all nodes. Return true guarantees successful completion. Return
	 * false signifies unknown state, i.e. write may have succeeded on some
	 * nodes and failed on others.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public IFuture<Boolean> writeAll(final String key, final Object value) {
		final Future<Boolean> ret = new Future<Boolean>();
		IFuture<Version> fut = db.writeAndReplace(key, value);
		fut.addResultListener(new DefaultResultListener<Version>() {

			@Override
			public void resultAvailable(final Version version) {
				IIntermediateFuture<IStorageNodeService> fut2 = agent
						.getServiceContainer().getRequiredServices(
								"nodeServices");
				fut2.addResultListener(new DefaultResultListener<Collection<IStorageNodeService>>() {

					@Override
					public void resultAvailable(
							Collection<IStorageNodeService> nodes) {
						// Define CollectionResultListener to integrate the results.
						CollectionResultListener<Boolean> lis = new CollectionResultListener<Boolean>(
								nodes.size() - 1,
								false,
								new DefaultResultListener<Collection<Boolean>>() {
									public void resultAvailable(
											Collection<Boolean> result) {
										Boolean success = true;
										Iterator<Boolean> it = result
												.iterator();
										while (it.hasNext()) {
											Boolean b = it.next();
											if (!b) { 
												// Return false if at least one node returns false
												success = false;
												break;
											}
										}
										ret.setResult(success);
									}
								});
						// add CollectionResultListener
						for(IStorageNodeService node : nodes) {
							if (!isLocalService(node)) {
								IFuture<Boolean> fut = node.receiveUpdate(key,
										version, value);
								fut.addResultListener(lis);
							}
						}

					}
				});
			}
		});
		return ret;
	}

	/**
	 * update a specific version
	 * 
	 * @param key
	 * @param newValue
	 * @param versionToUpdate
	 * @return IFuture<Boolean>(true) iff successful
	 */
	public IFuture<Boolean> update(final String key, final Object newValue,
			final Version versionToUpdate) {
		try {
			IFuture<Version> fut = db.update(key, newValue, versionToUpdate);
			fut.addResultListener(new DefaultResultListener<Version>() {

				@Override
				public void resultAvailable(Version version) {
					sendUpdateToAll(key, version, newValue);
				}
			});
		} catch (NoSuchVersionException e) {
			return new Future<Boolean>(false);
		}
		return new Future<Boolean>(true);
	}

	/**
	 * Write on this node. Send no updates to other nodes.
	 * 
	 * @param key
	 * @param version
	 * @param value
	 * @return
	 */
	public IFuture<Boolean> writeLocal(String key, Version version, Object value) {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		try {
			return db.writeVersioned(key, version, value);
		} catch (ObsoleteVersionException e) {
			return new Future<Boolean>(false);
		}
	}

	public IFuture<List<VersionValuePair>> read(String key) {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.read(key);
	}

	public IFuture<Object> read(String key, Version version) {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.read(key, version);
	}

	/**
	 * readAll
	 * 
	 * @param key
	 * @return
	 */
	public IFuture<List<VersionValuePair>> readAll(final String key) {
		final Future<List<VersionValuePair>> ret = new Future<List<VersionValuePair>>();
		IIntermediateFuture<IStorageNodeService> futNodes = agent
				.getServiceContainer().getRequiredServices("nodeServices");
		futNodes.addResultListener(new DefaultResultListener<Collection<IStorageNodeService>>() {

			@Override
			public void resultAvailable(Collection<IStorageNodeService> nodes) {
				// Define CollectionResultListener to integrate the results.
				CollectionResultListener<List<VersionValuePair>> lis = new CollectionResultListener<List<VersionValuePair>>(
						nodes.size(),
						true,
						new DefaultResultListener<Collection<List<VersionValuePair>>>() {
							public void resultAvailable(
									Collection<List<VersionValuePair>> result) {
								// return only values that come from all nodes!
								ret.setResult(getElementsPresentInAll(result));
							}
						});
				// add CollectionResultListener
				for(IStorageNodeService node : nodes) {
					if (isLocalService(node)) {
						IFuture<List<VersionValuePair>> value = db.read(key);
						value.addResultListener(lis);
					} else {
						IFuture<List<VersionValuePair>> value = node.get(key);
						value.addResultListener(lis);
					}
				}

			}
		});
		return ret;
	}
	
	/**
	 * delete database entry by placing a tombstone and propagate to other nodes
	 * @param key
	 * @return
	 */
	public IFuture<Boolean> delete(final String key) {
		final Tombstone tombstone = new Tombstone();
		IFuture<Version> fut = db.writeAndReplace(key, tombstone);
		fut.addResultListener(new DefaultResultListener<Version>() {

			@Override
			public void resultAvailable(Version version) {
				sendUpdateToAll(key, version, tombstone);
			}
		});
		return new Future<Boolean>(true);
	}

	
	
	public IFuture<List<DBEntry>> getDB() {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.getDB();
	}

	public IFuture<List<KeyVersionPair>> getKeyVersionPairs() {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.getKeyVersionPairs();
	}

	public List<KeyVersionPair> getKeyVersionPairsNonFut() {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.getKeyVersionPairsNonFut();
	}

	public IFuture<Map<String, Version>> getKeyVersionMap() {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.getKeyVersionMap();
	}

	public Map<String, List<Version>> getKeyVersionsMapNonFut() {
		if (db == null) {
			throw new RuntimeException("Database not initialized");
		}
		return db.getKeyVersionsMapNonFut();
	}

	private boolean isLocalService(IStorageNodeService service) {
		if ((((IService) service).getServiceIdentifier().getProviderId())
				.equals(agent.getComponentIdentifier())) {
			return true;
		} else {
			return false;
		}
	}

	protected List<VersionValuePair> getElementsPresentInAll(
			Collection<List<VersionValuePair>> col) {
		if (col.size() == 0) {
			return new ArrayList<VersionValuePair>();
		}
		Iterator<List<VersionValuePair>> itCol = col.iterator();
		List<VersionValuePair> retList = new ArrayList<VersionValuePair>(itCol.next());
		while (itCol.hasNext()) {
			List<VersionValuePair> list = itCol.next();
			retList.retainAll(list);
		}
		return retList;
	}

	protected void synchronizeData() {
		if(localOnly) {
			return;
		}
		System.out.println("Start synchronizeData: "
				+ new Timestamp(System.currentTimeMillis()));

		IFuture<IStorageNodeService> seed = getSeedAndPing();
		seed.addResultListener(new DefaultResultListener<IStorageNodeService>() {

			@Override
			public void resultAvailable(final IStorageNodeService node) {
				if (node == null) {
					System.out.println("synchronizeData: no seed found");
					System.out.println("End synchronizeData: "
							+ new Timestamp(System.currentTimeMillis()));
					return;
				}
				System.out.println("synchronizeData: seed: " + node.toString());

				final Map<String, List<Version>> localMap = getKeyVersionsMapNonFut();
				IFuture<Map<String, List<Version>>> fut = node
						.getKeyVersionsMap();
				fut.addResultListener(new DefaultResultListener<Map<String, List<Version>>>() {

					@Override
					public void resultAvailable(
							Map<String, List<Version>> seedMap) {
						Iterator<String> it = seedMap.keySet().iterator();
						while (it.hasNext()) {
							final String key = it.next();
							List<Version> seedVersions = seedMap.get(key);
							if (!localMap.containsKey(key)) {
								Iterator<Version> it2 = seedVersions.iterator();
								while (it2.hasNext()) {
									Version version = it2.next();
									requestUpdateAndWriteLocal(node, key,
											version);
								}
							} else {
								// compare versions for key present at seed and
								// local db
								List<Version> localVersions = localMap.get(key);
								// get from seed
								for (final Version v : seedVersions) {
									if (v.concurrentOrNewer(localVersions)) {
										requestUpdateAndWriteLocal(node, key, v);
									}
								}
								// send to all
								for (final Version v : localVersions) {
									if (v.concurrentOrNewer(seedVersions)) {
										IFuture<Object> readFut = db.read(key,
												v);
										readFut.addResultListener(new DefaultResultListener<Object>() {

											@Override
											public void resultAvailable(
													Object result) {
												sendUpdateToAll(key, v, result);
											}
										});

									}
								}
								localMap.remove(key);
							}
						}
						// send data that is still in the local map
						for (Entry<String, List<Version>> entry : localMap
								.entrySet()) {
							final String key = entry.getKey();
							final List<Version> versions = entry.getValue();
							for (final Version version : versions) {
								IFuture<Object> futRead = db.read(key, version);
								futRead.addResultListener(new DefaultResultListener<Object>() {

									@Override
									public void resultAvailable(Object value) {
										sendUpdateToAll(key, version, value);
									}
								});
							}
						}
					}

				});
				System.out.println("End synchronizeData: "
						+ new Timestamp(System.currentTimeMillis()));
			}
		});
	}

	/**
	 * 
	 */
	protected void emptyDatabase() {
		if (db == null) {
			throw new RuntimeException("Database not yet initialized");
		}
		db.emptyTable(null);
	}

	/**
	 * sendUpdateToAll
	 * 
	 * @return
	 */
	protected void sendUpdateToAll(final String key, final Version version,
			final Object value) {
		if (localOnly) {
			return;
		}
		IIntermediateFuture<IStorageNodeService> fut = agent
				.getServiceContainer().getRequiredServices("nodeServices");
		fut.addResultListener(new IIntermediateResultListener<IStorageNodeService>() {

			@Override
			public void exceptionOccurred(Exception exception) {
				System.err.println("Exception in sendUpdateToAll: "
						+ exception.toString());
			}

			@Override
			public void resultAvailable(Collection<IStorageNodeService> result) {
			}

			@Override
			public void intermediateResultAvailable(IStorageNodeService node) {
				if (!isLocalService(node)) {
					// send update only to other nodes
					node.receiveUpdate(key, version, value);
				}
			}

			@Override
			public void finished() {
			}

		});
	}

	/*
	 * getSeed get the fastest responding other node and send all nodes a ping.
	 */
	private IFuture<IStorageNodeService> getSeedAndPing() {
		final Future<IStorageNodeService> ret = new Future<IStorageNodeService>();
		IIntermediateFuture<IStorageNodeService> fut = agent
				.getServiceContainer().getRequiredServices("nodeServices");
		fut.addResultListener(new IIntermediateResultListener<IStorageNodeService>() {

			@Override
			public void exceptionOccurred(Exception exception) {
				throw new RuntimeException("Exception getSeedAndPing " + exception);
			}

			@Override
			public void resultAvailable(Collection<IStorageNodeService> result) {
			}

			@Override
			public void intermediateResultAvailable(IStorageNodeService node) {
				if (!isLocalService(node)) {
					node.ping();
					ret.setResultIfUndone(node);
				}
			}

			@Override
			public void finished() {
				ret.setResultIfUndone(null);
			}

		});

		return ret;
	}

	/*
	 * get update from another node and write it to local db
	 */
	private void requestUpdateAndWriteLocal(final IStorageNodeService node,
			final String key, final Version version) {
		IFuture<Object> valueFut = node.requestUpdate(new KeyVersionPair(key,
				version));
		valueFut.addResultListener(new DefaultResultListener<Object>() {

			@Override
			public void resultAvailable(Object value) {
				writeLocal(key, version, value);
			}
		});
	}

	/*
	 * rebindNodeServices searches for available nodes
	 */
	protected IFuture<Boolean> rebindNodeServices() {
		final Future<Boolean> ret = new Future<Boolean>();
		System.out.println(nodeID + " rebind");
		IIntermediateFuture<IStorageNodeService> futNodes = agent
				.getServiceContainer()
				.getRequiredServices("nodeServices", true);
		futNodes.addResultListener(new DefaultResultListener<Collection<IStorageNodeService>>() {

			@Override
			public void resultAvailable(Collection<IStorageNodeService> result) {
				ret.setResult(true);
			}
		});
		return ret;
	}

}
