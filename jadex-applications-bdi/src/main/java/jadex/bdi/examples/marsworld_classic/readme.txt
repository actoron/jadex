Marsworld
--------------

To start the example application start
the manager agent.

Overall Task:
Several interacting agents have the task to
explore the environment for ore resources and
bring as much ore as possible to the agents
homebase. When the mission time has expired
the agents have to abort their current actions
and return to the homebase.

The different agent types:
Sentry Agent
The sentry agent has the task to find ore
resources inspect them if they can be exploited.
Therefore the sentry agent has the greatest
vision of all agent types. To find the ore
resources more quickly all other agents report
to the sentry about resources they explored.

Production Agent:
The production agent is called to a target
from a sentry to produce as much ore as the
capacity of the resource permits. When finished
the agents calls for carry agents to bring
the ore to the homebase.

Carry Agent:
The carry agent has the task to bring ore from
targets to the homebase. It is called by the
production agent.
