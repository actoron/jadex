package jadex.examples.docs;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;

public class GettingStarted {

//    private void startingApps() {
//        PlatformConfiguration   config  = PlatformConfiguration.getDefault();
//        config.addComponent(Components.MyAgent.class);
//        Starter.createPlatform(config).get();
//    }

    public static void main(String[] args) {
        IPlatformConfiguration   config  = PlatformConfigurationHandler.getDefault();
        config.addComponent(Components.MyAgent.class);
        Starter.createPlatform(config).get();
    }
}
