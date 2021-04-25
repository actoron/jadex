package jadex.micro.examples.lottery;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

@ComponentTypes({
	@ComponentType(name="Lottery", clazz=LotteryAgent.class),
	@ComponentType(name="Player", clazz=PlayerAgent.class),
	@ComponentType(name="Human", clazz=HumanPlayerAgent.class)
	
})
@Configurations({
	
	@Configuration(name="default", components={
		@Component(type="Lottery"),
		@Component(type="Player"),
		@Component(type="Human")
	}),
})
@Agent
public class AppAgent
{
}

