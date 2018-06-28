package jadex.web.examples.puzzle;

import java.util.Date;


/**
 *  Store a single highscore entry.
 */
public class HighscoreEntry	implements Comparable<HighscoreEntry>
{
	//-------- attributes --------
	
	/** The date. */
	protected Date	date;
	
	/** The name of the player. */
	protected String	name;
	
	/** The board size. */
	protected int	boardsize;
	
	/** The number of hints used. */
	protected int	hint_count;
	
	//-------- constructors --------
	
	/**
	 *  Create an empty highscore entry.
	 */
	public HighscoreEntry()
	{
		// Bean constructor, do not remove.
	}
	
	/**
	 *  Create a new highscore entry.
	 */
	public HighscoreEntry(String name, int boardsize, int hint_count)
	{
		this.date	= new Date();
		this.name	= name;
		this.boardsize	= boardsize;
		this.hint_count	= hint_count;
	}

	//-------- methods --------

	/**
	 *  Get the name of this HighscoreEntry.
	 *  @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name of this HighscoreEntry.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the boardsize of this HighscoreEntry.
	 *  @return Returns the boardsize.
	 */
	public int getBoardSize()
	{
		return boardsize;
	}

	/**
	 *  Set the boardsize of this HighscoreEntry.
	 *  @param boardsize The boardsize to set.
	 */
	public void setBoardSize(int boardsize)
	{
		this.boardsize = boardsize;
	}

	/**
	 *  Get the hint_count of this HighscoreEntry.
	 *  @return Returns the hint_count.
	 */
	public int getHintCount()
	{
		return hint_count;
	}

	/**
	 *  Set the hint_count of this HighscoreEntry.
	 *  @param hint_count The hint_count to set.
	 */
	public void setHintCount(int hint_count)
	{
		this.hint_count = hint_count;
	}

	/**
	 *  Get the date of this HighscoreEntry.
	 *  @return Returns the date.
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 *  Set the date of this HighscoreEntry.
	 *  @param date The date to set.
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	//-------- comparision methods --------
	
	/**
	 *  Compare two highscore entries.
	 */
	public int compareTo(HighscoreEntry entry)
	{
		if(this.getBoardSize()!=entry.getBoardSize())
			throw new UnsupportedOperationException("Cannot compare entries with different board sizes.");
		int	diff	= this.getHintCount() - entry.getHintCount();	// Smaller hint count -> better.
		diff	= diff!=0 ? diff : this.getDate().compareTo(entry.getDate()); // Earlier date -> better.
		return diff!=0 ? diff : this.getName().compareTo(entry.getName()); // Name compare only used for consistency with equals().
	}
	
	/**
	 *  Generate a hashcode for this entry. 
	 */
	public int hashCode()
	{
		return this.getDate().hashCode() ^ this.getBoardSize() ^ this.getHintCount()<<16;
	}
	
	/**
	 *  Test for equality.
	 */
	public boolean	equals(Object o)
	{
		boolean	ret	= false;
		if(o instanceof HighscoreEntry)
		{
			HighscoreEntry	entry	= (HighscoreEntry)o;
			ret	= this.getBoardSize() == entry.getBoardSize()
			&& this.getHintCount() == entry.getHintCount()
			&& this.getDate().equals(entry.getDate())
			&& this.getName().equals(entry.getName());
		}
		return ret;
	}
}
