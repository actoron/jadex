package haw.mmlab.production_line.dropout;

import haw.mmlab.production_line.common.AgentConstants;
import haw.mmlab.production_line.dropout.config.Action;
import haw.mmlab.production_line.dropout.config.AgentQuery;
import haw.mmlab.production_line.dropout.config.Configuration;
import haw.mmlab.production_line.dropout.config.DropoutConfig;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

/**
 * The dropout agent...
 * 
 * @author thomas
 */
public class DropoutAgent extends MicroAgent {

	public static final String CATEGORY_ROBOT = "ROBOT";
	public static final String CATEGORY_TRANSPORT = "TRANSPORT";
	public static final String CATEGORY_ALL = "ALL";
	public static final String CATEGORY_NONE = "NONE";

	public static final String ACTIONTYPE_DROP = "DROP";
	public static final String ACTIONTYPE_ADD = "ADD";

	public static final String ACTIONMODE_ACTIVE_ALL = "ACTIVE_ALL";
	public static final String ACTIONMODE_ACTIVE_RANDOM = "ACTIVE_RANDOM";
	public static final String ACTIONMODE_RANDOM = "RANDOM";
	public static final String ACTIONMODE_STATIC = "STATIC";

	private DropoutConfig config = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.micro.MicroAgent#agentCreated()
	 */
	@Override
	public IFuture agentCreated() {
		String confFile = (String) getArgument("configuration_model");

		try {
			// read from XML:
			config = (DropoutConfig) deco4mas.util.xml.XmlUtil.retrieveFromXML(DropoutConfig.class, confFile);
		} catch (FileNotFoundException e) {
			getLogger().severe(e.getMessage());
			killAgent();
		} catch (JAXBException e) {
			getLogger().severe(e.getMessage());
			killAgent();
		}

		return IFuture.DONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.micro.MicroAgent#executeBody()
	 */
	@Override
	public void executeBody() {
		for (Configuration conf : config.getConfigurations()) {
			waitFor(conf.getRate() * 1000, new QueryStep(conf));
		}
	}

	/**
	 * Returns the {@link MicroAgentMetaInfo}.
	 * 
	 * @return the {@link MicroAgentMetaInfo}
	 */
	public static MicroAgentMetaInfo getMetaInfo() {
		MicroAgentMetaInfo meta = new MicroAgentMetaInfo("Dropout agent", null, new IArgument[] { new Argument("configuration_model", "The dropout's configuration", "String") }, null);
		return meta;
	}

	private class QueryStep implements IComponentStep {

		private Configuration configuration = null;

		private int i = 0;

		private QueryStep(Configuration configuration) {
			this.configuration = configuration;
		}

		public Object execute(IInternalAccess ia) {
			int rate = configuration.getRate() * 1000;
			int count = configuration.getCount() != null ? configuration.getCount() : 0;

			if (rate < 0) {
				throw new IllegalStateException("rate must be a positive integer");
			}

			while (count <= 0 || i < count) {
				AgentQuery query = configuration.getQuery();
				Action action = configuration.getAction();
				List<IProcessWorkpieceService> receiver = getReceiver(query);
				executeAction(action, receiver);
				if (count > 0) {
					i++;
				}
				waitFor(rate, this);
			}

			return null;
		}

		private void executeAction(Action action, List<IProcessWorkpieceService> receiver) {
			for (IProcessWorkpieceService service : receiver) {
				service.executeDropoutAction(action);
			}

		}

		@SuppressWarnings("unchecked")
		private List<IProcessWorkpieceService> getReceiver(AgentQuery query) {
			List<IProcessWorkpieceService> receiver = new ArrayList<IProcessWorkpieceService>();
			IFuture future = SServiceProvider.getServices(getServiceProvider(), IProcessWorkpieceService.class);
			Collection<IProcessWorkpieceService> services = (Collection<IProcessWorkpieceService>) future.get(new ThreadSuspendable(this));

			if (query.getCategory().equals(CATEGORY_ALL)) {
				receiver.addAll(services);
			} else if (query.getCategory().equals(CATEGORY_ROBOT)) {
				for (IProcessWorkpieceService service : services) {
					if (service.getType().equals(AgentConstants.AGENT_TYPE_ROBOT)) {
						receiver.add(service);
					}
				}
			} else if (query.getCategory().equals(CATEGORY_TRANSPORT)) {
				for (IProcessWorkpieceService service : services) {
					if (service.getType().equals(AgentConstants.AGENT_TYPE_TRANSPORT)) {
						receiver.add(service);
					}
				}
			} else if (query.getCategory().equals(CATEGORY_NONE)) {
				for (IProcessWorkpieceService service : services) {
					if (query.getAgents().contains(service.getId())) {
						receiver.add(service);
					}
				}
			}

			List<IProcessWorkpieceService> result = new ArrayList<IProcessWorkpieceService>();
			if (query.getRandom() != null && query.getRandom() > 0) {
				for (int i = 0; i < query.getRandom(); i++) {
					Collections.shuffle(receiver);
					result.add(receiver.remove(0));
				}

				return result;
			} else {
				return receiver;
			}
		}
	}
}
