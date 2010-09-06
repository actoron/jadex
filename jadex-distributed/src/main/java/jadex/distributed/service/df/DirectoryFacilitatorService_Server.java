package jadex.distributed.service.df;

import jadex.base.DefaultResultListener;
import jadex.base.fipa.DFComponentDescription;
import jadex.base.fipa.DFServiceDescription;
import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.base.fipa.IProperty;
import jadex.base.fipa.SFipa;
import jadex.base.fipa.SearchConstraints;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ISearchConstraints;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.collection.IndexMap;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;
import jadex.standalone.service.DirectoryFacilitatorService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Directory facilitator implementation for standalone platform.
 */
public class DirectoryFacilitatorService_Server extends DirectoryFacilitatorService
{
	public DirectoryFacilitatorService_Server(IServiceContainer platform) {
		super(platform);
	}	
}
