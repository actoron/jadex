package jadex.platform.service.message.transport.btmtp;


import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMsgHeader;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.platform.service.transport.AbstractTransportAgent2;
import jadex.platform.service.transport.ITransport;

public class BluetoothTransportAgent extends AbstractTransportAgent2<BTChannel> {

    @Agent
    private IInternalAccess access;

    @Override
    public ITransport<BTChannel> createTransportImpl() {
        return new BTTransport(access);
    }

//    @Override
//    protected IFuture<String[]> getAddresses(IMsgHeader header) {
//        // TODO overwrite
//        return super.getAddresses(header);
//    }
}
