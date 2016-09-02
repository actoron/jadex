package jadex.examples.docs;

import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 * Created by kalinowski on 01.09.16.
 */
public class Components {

    @Agent
    @Arguments(@Argument(name="myName", description = "Name of this agent", clazz=String.class, defaultvalue = "Hugo"))
    public class MyAgent
    {
        @AgentArgument
        protected String myName;

        MyAgent() {
            super();
            CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"myName"}, new Object[]{"Harald"}));
        }
    }
}
