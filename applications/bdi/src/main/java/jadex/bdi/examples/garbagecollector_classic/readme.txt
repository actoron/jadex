Garbagecollector
--------------

Starting requires:
- Starting one environment agent.
- Starting one burner agent.
- Starting one or more collector agents.

This example is inspired by the "robot" example
of the Jason BDI agent engine.

Garbage collector agents:
- Run on the grind in a predefined way.
- Whenever they step on a piece of garbage they try to
  pick it up and bring it to a burner agent.
- Thereafter they go back to their original position and
  continue the search for garbage.

Burner agents:
- Do not move.
- Sit and wait for garbage.
- Whenever garbage occurs on their position they
  pick it up and burn it.





