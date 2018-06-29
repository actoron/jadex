package jadex.platform.service.message.transport.btmtp;


import jadex.bridge.IComponentIdentifier;

public class BTChannel {
    private final String address;
    private IComponentIdentifier target;

    public BTChannel(String address, IComponentIdentifier target) {
        this.address = address;
        this.target = target;
    }

    public IComponentIdentifier getReceiver() {
        return target;
    }

    public String getAddress() {
        return address;
    }
}
