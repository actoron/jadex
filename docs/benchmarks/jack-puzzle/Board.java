/*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
  This code is generated by JAC version 5.6 by
  Agent Oriented Software. http://www.agent-software.com.au

    DO NOT ALTER THIS CODE AND DO NOT REMOVE THIS COMMENT
 *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*/
import aos.jack.jak.beliefset.BeliefSetException;
import aos.jack.jak.cursor.Cursor;
import aos.jack.jak.cursor.BinaryBoolOp;
import aos.jack.plugin.view.rt.CleanupCursor;
import aos.jack.util.cursor.EnumerationCursor;
import java.util.Vector;
import aos.jack.jak.core.Jak;

class Board implements aos.jack.plugin.view.rt.ViewMarker {
    static int[][] move_check_table = {
            {
                1,
                0,
                -1},
            {
                0,
                1,
                -1},
            {
                -1,
                0,
                1},
            {
                0,
                -1,
                1},
            {
                2,
                0,
                -1,
                1,
                0,
                1},
            {
                0,
                2,
                -1,
                0,
                1,
                1},
            {
                -2,
                0,
                1,
                -1,
                0,
                -1},
            {
                0,
                -2,
                1,
                0,
                -1,
                -1}};
    int[][] board = {
            {
                1,
                1,
                1,
                4,
                4},
            {
                1,
                1,
                1,
                4,
                4},
            {
                1,
                1,
                0,
                -1,
                -1},
            {
                4,
                4,
                -1,
                -1,
                -1},
            {
                4,
                4,
                -1,
                -1,
                -1}};
    int last = 4;
    SquareIndex the_hole = new SquareIndex(2,2);
    protected java.util.List moves = new java.util.ArrayList();
    /**
     * The moves() method computes possible moves to <x:y>,
     * represented by the SquareIndexes of pieces to move.
     */

    java.util.Vector moves(int x, int y)
    {
        java.util.Vector v = new java.util.Vector();
        for (int i = 0; i < move_check_table.length; i++ )
            check(v,move_check_table[i],x,y);
        return v;
    }
    
    /**
     * The check() method processes a move_check_table entry, and adds
     * a SquareIndex to the Vector when the entry applies.
     */

    void check(java.util.Vector v, int[] m, int x, int y)
    {
        int x1 = x + m[0];
        int y1 = y + m[1];
        if (get(x1,y1) == m[2]) {
            if (m.length == 3 || get(x + m[3],y + m[4]) == m[5]) {
                SquareIndex s = new SquareIndex(x1,y1);
                v.add(s);
            }
        }
    }
    
    int get(int x, int y)
    {
        if (x < 0 || x >= 5 || y < 0 || y >= 5) 
            return 4;
        return board[x][y];
    }
    
    int get(SquareIndex s)
    {
        return get(s.x,s.y);
    }
    
    void set(int v, int x, int y)
    {
        board[x][y] = v;
    }
    
    void set(int v, SquareIndex s)
    {
        set(v,s.x,s.y);
    }
    
    boolean solution()
    {
        if (board[2][2] != 0) 
            return false;
        for (int i = 0; i < 3; i++ ) {
            for (int j = 0; j < 3; j++ ) {
                if (board[i][j] > 0) 
                    return false;
            }
        }
        return true;
    }
    
    java.util.Vector moves(SquareIndex hole)
    {
        return moves(hole.x,hole.y);
    }
    
    boolean isJumpMove(int x, int y)
    {
        int dx = java.lang.Math.abs(the_hole.x - x);
        int dy = java.lang.Math.abs(the_hole.y - y);
        return dx == 2 || dy == 2;
    }
    
    boolean isJumpMove(SquareIndex s)
    {
        return isJumpMove(s.x,s.y);
    }
    
    java.util.Vector moves(aos.jack.jak.logic.ObjectVariable $hole)
        throws aos.jack.jak.beliefset.BeliefSetException
    {
        Board.__complex_1 __c = new Board.__complex_1($hole);
        try {
            return __c.__complex_1_moves();
        }
        catch (java.lang.Exception __e) {
            throw new aos.jack.jak.beliefset.BeliefSetException("moves got exception " + __e);
        }
    }
    
    boolean move(aos.jack.jak.logic.ObjectVariable $from, aos.jack.jak.logic.ObjectVariable $to, boolean back)
        throws aos.jack.jak.beliefset.BeliefSetException
    {
        Board.__complex_2 __c = new Board.__complex_2($from,$to,back);
        try {
            return __c.__complex_2_move();
        }
        catch (java.lang.Exception __e) {
            throw new aos.jack.jak.beliefset.BeliefSetException("move got exception " + __e);
        }
    }
    
    boolean hole(aos.jack.jak.logic.ObjectVariable $s)
        throws aos.jack.jak.beliefset.BeliefSetException
    {
        Board.__complex_3 __c = new Board.__complex_3($s);
        try {
            return __c.__complex_3_hole();
        }
        catch (java.lang.Exception __e) {
            throw new aos.jack.jak.beliefset.BeliefSetException("hole got exception " + __e);
        }
    }
    
    public aos.jack.jak.cursor.Cursor possibleMove(aos.jack.jak.logic.ObjectVariable $s, aos.jack.jak.logic.ObjectVariable $t)
        throws aos.jack.jak.beliefset.BeliefSetException
    {
        Board.__complex_4 __c = new Board.__complex_4($s,$t);
        try {
            return __c.setCursor(__c.__complex_4_possibleMove());
        }
        catch (java.lang.Exception __e) {
            throw new aos.jack.jak.beliefset.BeliefSetException("possibleMove got exception " + __e);
        }
    }
    
    public Board()
    {
    }
    
    class Places extends aos.jack.util.cursor.EnumerationCursor {
        aos.jack.jak.logic.ObjectVariable $s;
        Places(java.util.Vector v, aos.jack.jak.logic.ObjectVariable $s)
        {
            super(v.elements(),$s);
            this.$s = $s;
        }
        
        public boolean bindValues(java.lang.Object o)
            throws java.lang.Exception
        {
            return $s.unify(o);
        }
        
    }
    class __complex_1 extends aos.jack.plugin.view.rt.CleanupCursor {
        aos.jack.jak.logic.ObjectVariable $hole;
        public java.lang.Object genObject(int __index)
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Object Construction");
            return null;
        }
        
        public aos.jack.jak.cursor.Cursor genCursor(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Cursor Construction");
            return null;
        }
        
        public aos.jack.jak.fsm.FSM genFSM(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal FSM Construction");
            return null;
        }
        
        public boolean testCondition(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal test Construction");
            return false;
        }
        
        public __complex_1(aos.jack.jak.logic.ObjectVariable $hole)
        {
            super($hole);
            this.$hole = $hole;
        }
        
        java.util.Vector __complex_1_moves()
            throws java.lang.Exception
        {
            return moves((SquareIndex) $hole.getValue());
        }
        
    }
    class __complex_2 extends aos.jack.plugin.view.rt.CleanupCursor {
        aos.jack.jak.logic.ObjectVariable $from;
        aos.jack.jak.logic.ObjectVariable $to;
        boolean back;
        SquareIndex __local_2;
        SquareIndex __local_3;
        int __local_4;
        java.lang.Integer __local_5;
        public java.lang.Object genObject(int __index)
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Object Construction");
            return null;
        }
        
        public aos.jack.jak.cursor.Cursor genCursor(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Cursor Construction");
            return null;
        }
        
        public aos.jack.jak.fsm.FSM genFSM(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal FSM Construction");
            return null;
        }
        
        public boolean testCondition(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal test Construction");
            return false;
        }
        
        public __complex_2(aos.jack.jak.logic.ObjectVariable $from, aos.jack.jak.logic.ObjectVariable $to, boolean back)
        {
            super($from);
            this.$from = $from;
            this.$to = $to;
            this.back = back;
        }
        
        boolean __complex_2_move()
            throws java.lang.Exception
        {
            __local_2 = (SquareIndex) $from.getValue();
            __local_3 = (SquareIndex) $to.getValue();
            if (get(__local_3.x,__local_3.y) != 0) 
                return false;
            __local_4 = get(__local_2.x,__local_2.y);
            if (__local_4 != 1 && __local_4 != -1) 
                return false;
            if (!back) {
                moves.add(new java.lang.Integer(__local_4));
                last = __local_4;
            }
             else {
                moves.remove(moves.size() - 1);
                if (moves.size() > 0) {
                    __local_5 = (java.lang.Integer) moves.get(moves.size() - 1);
                    last = __local_5.intValue();
                }
                 else {
                    last = 4;
                }
            }
            the_hole = __local_2;
            set(__local_4,__local_3);
            set(0,__local_2);
            return true;
        }
        
    }
    class __complex_3 extends aos.jack.plugin.view.rt.CleanupCursor {
        aos.jack.jak.logic.ObjectVariable $s;
        public java.lang.Object genObject(int __index)
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Object Construction");
            return null;
        }
        
        public aos.jack.jak.cursor.Cursor genCursor(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Cursor Construction");
            return null;
        }
        
        public aos.jack.jak.fsm.FSM genFSM(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal FSM Construction");
            return null;
        }
        
        public boolean testCondition(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal test Construction");
            return false;
        }
        
        public __complex_3(aos.jack.jak.logic.ObjectVariable $s)
        {
            super($s);
            this.$s = $s;
        }
        
        boolean __complex_3_hole()
            throws java.lang.Exception
        {
            return $s.unify(the_hole);
        }
        
    }
    class __complex_4 extends aos.jack.plugin.view.rt.CleanupCursor {
        aos.jack.jak.logic.ObjectVariable $s;
        aos.jack.jak.logic.ObjectVariable $t;
        public java.lang.Object genObject(int __index)
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal Object Construction");
            return null;
        }
        
        public aos.jack.jak.cursor.Cursor genCursor(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
                case 0: 
                {
                    return (new Board.Places(moves($t),$s));
                }
                case 1: 
                {
                    return (new aos.jack.jak.cursor.BinaryBoolOp(this,__logic,aos.jack.jak.cursor.BinaryBoolOp.AND,(short) 0,false,(short) 0,true));
                }
            }
            aos.jack.jak.core.Jak.error("illegal Cursor Construction");
            return null;
        }
        
        public aos.jack.jak.fsm.FSM genFSM(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
            }
            aos.jack.jak.core.Jak.error("illegal FSM Construction");
            return null;
        }
        
        public boolean testCondition(int __index)
            throws java.lang.Exception
        {
            switch (__index) {
                case 0: 
                {
                    return (hole($t));
                }
            }
            aos.jack.jak.core.Jak.error("illegal test Construction");
            return false;
        }
        
        public __complex_4(aos.jack.jak.logic.ObjectVariable $s, aos.jack.jak.logic.ObjectVariable $t)
        {
            super($s);
            this.$s = $s;
            this.$t = $t;
        }
        
        aos.jack.jak.cursor.Cursor __complex_4_possibleMove()
            throws java.lang.Exception
        {
            return genCursor(1);
        }
        
    }
}
