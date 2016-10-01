package jadex.examples.docs;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class GettingStarted {

//    private void startingApps() {
//        PlatformConfiguration   config  = PlatformConfiguration.getDefault();
//        config.addComponent(Components.MyAgent.class);
//        Starter.createPlatform(config).get();
//    }

    public static void main(String[] args) {
        PlatformConfiguration   config  = PlatformConfiguration.getDefault();
        config.addComponent(Components.MyAgent.class);
        Starter.createPlatform(config).get();
    }
}
