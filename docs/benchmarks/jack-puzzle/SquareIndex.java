class SquareIndex {

    int x;
    int y;

    SquareIndex(int x,int y)
    {
	this.x = x;
	this.y = y;
    }

    public String toString()
    {
	return "<" + x + ":" + y + ">" ;
    }

    public int hashCode()
    {
	return x * 100 + y ;
    }

    public boolean equals(Object s)
    {
	if ( s instanceof SquareIndex )
	    return equals( (SquareIndex)s );
	return false;
    }

    public boolean equals(SquareIndex s)
    {
	return x == s.x && y == s.y ;
    }

}
