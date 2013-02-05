This application executes a search to find a solution to a simple
puzzle. It is an example of how meta-level agent programs can be
written.

The puzzle consists of a board with a set of holes layed out in
two side-by-side diamond patterns with the two diamond sharing
one hole. There are 8 yellow pegs on the left and 8 red pegs on
the right and the shared hole is empty.

         o       o                   Y       R
       o   o   o   o               Y   Y   R   R 
     o   o   o   o   o           Y   Y   o   R   R
       o   o   o   o               Y   Y   R   R
         o       o                   Y       R

        (the board)            (the board with pegs)

The aim of the puzzle is to swap all the red and yellow pegs.
Only one peg may be moved at the time and the moves must
obey the following:

    * Yellow pegs can only move towards the right (up or down);
    * Red pegs can only move towards the left (up or down);
    * A peg can move to an open hole which is adjacent to it
      or it can jump over a peg of the opposite color to land in
      an empty hole (still only moving to the left or the right).

The search for a solution is displayed by a graphical back-end
while the plans are executing. You will see moves that look illegal
but they are actually moves that were discarded by backtracking and
are being reversed.

A time-delay has been built in to provide smooth animation of the
moves. You might try taking out the graphical animation to see how
fast it can really go.

Compiling
---------

To compile the example, run the mkit script. You will need
to have the jack.jar file in your CLASSPATH and "." too.

Running
-------

The example is not trying to solve this puzzle in a particularly
quick or clever way. It is written to demonstrate some meta level
reasoning to apply a choice policy whenever there are alternative
moves. The policies are:

default:
    Prefer short moves, and red before yellow. This policy arises 
    'accidentally' from the representation and the way the move options
    are produced. To use this policy, you run the program by:

    java Sokrates

all:
    Prefer long moves, and red before yellow. This policy arises from
    the meta-level reasoning showing preference for long moves, but not
    worrying about peg colour. Therefore the red-before-yellow
    preference that is intrinsic to the representation shows through.
    To use this policy, you run the program by:

    java -Dchoice=all Sokrates

same:
    Prefer long moves, and the same colour as last move. This policy
    arises from the meta-level reasoning showing preference for long
    moves and with attention to the peg colour. To use this policy, you
    run the program by:

    java -Dchoice=same Sokrates

alter:
    Prefer long moves, and alternate colours for moves. This is the
    same as policy "same", but the opposite constraint applied to
    the peg colour. To use this policy, you run the program by:

    java -Dchoice=alter Sokrates

It is interesting to note that policy "same" outperforms the others
in time, but results in the most final moves. Policy "all" ponders
about 6 times longer, and results in the least number of final moves.
Policies "default" and "alter" take a magnitude longer than policy
"same", and finds solutions in between "same" and "all".

The final table is:

    Policy    no. tries    no. moves
    -------   ---------    ---------
    default     1156           52
     all         657           49
     same        107           53
     alter      1147           51

