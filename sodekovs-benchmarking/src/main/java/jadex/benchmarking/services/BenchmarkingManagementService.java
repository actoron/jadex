package jadex.benchmarking.services;

import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import sodekovs.util.model.benchmarking.description.BenchmarkingDescription;
import sodekovs.util.model.benchmarking.description.HistoricDataDescription;
import sodekovs.util.model.benchmarking.description.IBenchmarkingDescription;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

/**
 * Implementation of the related interface.
 */
public class BenchmarkingManagementService extends BasicService implements IBenchmarkingManagementService {

	// -------- attributes --------

	/** The platform. */
	protected IServiceProvider provider;

	/** The cached component management service. */
	protected IComponentManagementService cms;

	/** The cached clock service. */
	protected IClockService clockservice;

	/** The registered components. */
	protected IndexMap components;

	// -------- constructors --------

	/**
	 * Create a standalone BenchmarkingManagementService.
	 */
	public BenchmarkingManagementService(IServiceProvider provider) {
		this(provider, null);
	}

	/**
	 * Create a standalone BenchmarkingManagementService.
	 */
	public BenchmarkingManagementService(IServiceProvider provider, Map properties) {
		super(provider.getId(), IBenchmarkingManagementService.class, properties);

		this.provider = provider;
		this.components = new IndexMap();
	}

	/**
	 * Get information about currently running benchmarks
	 */
	public IFuture getStatusOfRunningBenchmarkExperiments() {
		final Future fut = new Future();
		final ArrayList<IBenchmarkingDescription> benchmarks = new ArrayList<IBenchmarkingDescription>();

		SServiceProvider.getServices(provider, IBenchmarkingExecutionService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(new IResultListener() {
			public void resultAvailable(Object result) {
				Collection coll = (Collection) result;
				System.out.println("Coll length: " + coll.size());
				// Ignore search failures of remote dfs
				CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener() {
					public void resultAvailable(Object res) {
						// System.out.println("Part 2 length: "+ ((Collection)result).size());
						// Add all services of all remote dfs
						for (Iterator it = ((Collection) res).iterator(); it.hasNext();) {
							IBenchmarkingDescription benchDesc = (IBenchmarkingDescription) it.next();
							if (benchDesc != null) {
								benchmarks.add(benchDesc);
							}
						}
						// open.remove(fut);
						// System.out.println("Federated search: "+ret);//+" "+open);
						fut.setResult(benchmarks.toArray(new BenchmarkingDescription[benchmarks.size()]));
					}

					public void exceptionOccurred(Exception exception) {
						// open.remove(fut);
						fut.setException(exception);
						// fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
					}
				});
				for (Iterator it = coll.iterator(); it.hasNext();) {
					IBenchmarkingExecutionService benchServ = (IBenchmarkingExecutionService) it.next();
					// if(remotedf!=DirectoryFacilitatorService.this)
					// {
					benchServ.getBenchmarkStatus().addResultListener(lis);
					// }
					// else
					// {
					// lis.resultAvailable(null);
					// }
				}
			}

			public void exceptionOccurred(Exception exception) {
				// open.remove(fut);
				fut.setResult(benchmarks.toArray(new BenchmarkingExecutionService[benchmarks.size()]));
			}
		});
		// public void resultAvailable(Object result) {
		// Collection coll = (Collection) result;
		// System.out.println("dfs: "+coll.size());
		// ret.setResult(benchmarks);
		// Ignore search failures of remote dfs
		// CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener() {
		// public void resultAvailable(Object result) {
		// // Add all services of all remote dfs
		// for (Iterator it = ((Collection) result).iterator(); it.hasNext();) {
		// benchmarks.add((String) it.next());
		// }
		// ret.setResult(benchmarks);
		// }
		//
		// public void exceptionOccurred(Exception exception) {
		// // open.remove(fut);
		// ret.setException(exception);
		// // fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
		// }
		// });
		// }
		//
		// public void exceptionOccurred(Exception exception) {
		// // open.remove(fut);
		// ret.setResult(exception);
		// }
		// });

		return fut;
	}

	/**
	 * Retrieve results from database about results of performed benchmarks
	 */
	public IFuture getHistoryOfBenchmarkExperiments() {
		
			//Hack: using search of this service class
//			Future ret = new Future();
//			ConnectionManager conMgr = new ConnectionManager();
//			ret.setResult(conMgr.getLog());
//			
//			return ret;
					
			
//		Using local service of agents				
		final Future fut = new Future();
		final ArrayList<IHistoricDataDescription> historicData = new ArrayList<IHistoricDataDescription>();

		SServiceProvider.getServices(provider, IBenchmarkingExecutionService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(new IResultListener() {
			public void resultAvailable(Object result) {
				Collection coll = (Collection) result;
				System.out.println("Coll length: " + coll.size());
				// Ignore search failures of remote dfs
				CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener() {
					public void resultAvailable(Object res) {
						// System.out.println("Part 2 length: "+ ((Collection)result).size());
						// Add all services of all remote dfs
						for (Iterator it = ((Collection) res).iterator(); it.hasNext();) {
							IHistoricDataDescription[] histDataDesc = (IHistoricDataDescription[]) it.next();
							if (histDataDesc != null) {
								for (IHistoricDataDescription desc : histDataDesc) {
									historicData.add(desc);
								}
							}
						}
						// open.remove(fut);
						// System.out.println("Federated search: "+ret);//+" "+open);
						fut.setResult(historicData.toArray(new HistoricDataDescription[historicData.size()]));
					}

					public void exceptionOccurred(Exception exception) {
						// open.remove(fut);
						fut.setException(exception);
						// fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
					}
				});
				for (Iterator it = coll.iterator(); it.hasNext();) {
					IBenchmarkingExecutionService benchServ = (IBenchmarkingExecutionService) it.next();
					// if(remotedf!=DirectoryFacilitatorService.this)
					// {
					benchServ.getResultsFromDB().addResultListener(lis);
					// }
					// else
					// {
					// lis.resultAvailable(null);
					// }
				}
			}

			public void exceptionOccurred(Exception exception) {
				// open.remove(fut);
				fut.setResult(historicData.toArray(new BenchmarkingExecutionService[historicData.size()]));
			}
		});

		return fut;
	}
}
