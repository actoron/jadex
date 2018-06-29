Puzzle
--------------

This example is adapted from the commercial agent
platform JACK(TM) from Agent Oriented Software.
The Jadex implementation is very similar to
allow performance measurements between both
platform (see below for details).

Rules:
This is a puzzle game played by one agent.
It consists of a board with white and red
pieces. Objective is to switch to positions
of both pieces whereby the following rules for
making a move exist.

- white pieces move right or down to an adjacent
free field.
- white pieces jump right or down over a red
piece to a free field.
- red pieces can only move up or left with the
same restrictions as white pieces.
- the color of a piece to move is not specified.

Solution strategy:
The agent uses meta-level reasoning to solve the
puzzle. It creates a goal to make a move and find
the solution. For this goal a plan for each
possible move is created. To decide which move
plan to test first a meta-level goal is created.
The choose move plan handles the meta goal and
decides according to the specified strategy:
- default: use order of movelist from board
- long: prefer long moves
- long_same: prefer jump moves of the same color
- long_alter: prefer jump moves of alternate color


Jadex - JACK performance comparison
--------------------------------------------

Policy(JACK/Jadex)    no. tries    no. moves
------------------    ---------    ---------
none/none              1156         52
all/long               657          49
same/same_long         107          53
(alter                 1147         51)      broken JACK implementation
alter/alter_long       5022         53       fixed implementation


Performance [millis] (Benchmark w/o GUI)
--------------------------------------------

Machine A (1.8 GHz Pentium 4, 512MB)
                 JACK      Jadex    Factor
none             5147      7341     1.42
all              3795      4136     1.08
same             2594      821      0.31
alter            15300     36442    2.38

Machine B (3 GHz Pentium 4, 512MB)
                 JACK      Jadex    Factor
none             1844      2265     1.23
all              1218      1250     1.03
same             687 (?)   265      0.39
alter            7266      12750    1.75

Machine C (1.5 GHz Pentium 4M Centrino, 512MB)
                 JACK      Jadex    Factor
none             1091      1673     1.53
all              691       971      1.41
same             210       271      1.29
alter            5077      9003     1.77

non-threaded
none			 1091	   1442		1.32
all				 691	   831		1.20
same			 210	   210		1.0
alter			 5077	   8092		1.59



Release 0.93
--------------------------------------------

Machine A (1.8 GHz Pentium 4, 512MB)
                 JACK      Jadex    Factor
none             5147      7341     1.42
all              3795      4136     1.08
same             2594      821      0.31
alter            15300     36442    2.38

Machine B (3 GHz Pentium 4, 512MB)
                 JACK      Jadex    Factor
none             1844      3609     1.95
all              1218      1953     1.60
same             687       422      0.61
alter            7266      19625    2.7

Machine C (1.5 GHz Pentium 4M Centrino, 512MB)
                 JACK      Jadex    Factor
none             1091      2363     2.17
all              691       1322     1.91
same             210       290      1.38
alter            5077      13269    2.61
