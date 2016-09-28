package jadex.examples.docs;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;


public class Faq {

    private void transportAdresses() {
        IInternalAccess access = null;
        IComponentIdentifier cid = null;

        ITransportAddressService tas = SServiceProvider.getLocalService(access, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM);
        String[] addrs = tas.getPlatformAddresses(cid).get();
    }
}
