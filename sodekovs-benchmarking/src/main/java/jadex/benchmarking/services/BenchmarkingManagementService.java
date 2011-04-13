package jadex.benchmarking.services;

import jadex.base.fipa.DFComponentDescription;
import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
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
		final Future ret = new Future();
		final ArrayList<String> benchmarks = new ArrayList<String>();

		SServiceProvider.getServices(provider, IBenchmarkingManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(new IResultListener() {
			public void resultAvailable(Object result) {
				Collection coll = (Collection) result;
				 System.out.println("dfs: "+coll.size());
				 ret.setResult(benchmarks);
				// Ignore search failures of remote dfs
				CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener() {
					public void resultAvailable(Object result) {
						// Add all services of all remote dfs
						for (Iterator it = ((Collection) result).iterator(); it.hasNext();) {
							benchmarks.add((String) it.next());
						}
						ret.setResult(benchmarks);
					}

					public void exceptionOccurred(Exception exception) {
						// open.remove(fut);
						ret.setException(exception);
						// fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
					}
				});				
			}

			public void exceptionOccurred(Exception exception) {
				// open.remove(fut);
				ret.setResult(exception);
			}
		});

		return ret;
	}
}
