/**
 * 
 */
package nuggets;

import java.util.Arrays;
import java.util.Random;


/**
 * @author Andrzej
 */
public class PerfectHash
{
	private static final int	DEFAULT_PERFECT_SIZE	= 23;

	/** keys */
	protected String[]			keys;

	/** the hash function distributes among [0..n-1] */
	protected int				n;

	/** rnd_tab */
	protected int[]				rnd_tab;

	/** hash_cols */
	protected int[]				hash_cols;

	/** minlen */
	protected int				minlen;

	/** maxlen */
	protected int				maxlen;

	private int					perfect_size			= DEFAULT_PERFECT_SIZE;

	/** Default */
	public PerfectHash()
	{ /* NOP */
	}

	/**
	 * Keys must be unique
	 * 
	 * @param keys
	 */
	public PerfectHash(String[] keys)
	{
		setKeys(keys);
	}

	/**
	 * Constructor for PerfectHash.
	 * 
	 * @param keys
	 * @param perfect_size the maximum keys for perfect hash
	 */
	public PerfectHash(String[] keys, int perfect_size)
	{
		this.perfect_size = perfect_size;
		setKeys(keys);
	}

	/**
	 * @param keys
	 */
	public void setKeys(String[] keys)
	{
		this.keys = keys;
		maxlen = 0;
		minlen = Integer.MAX_VALUE;
		final int keys_length = keys.length;

		int k = keys_length;
		while(k > 00)
		{
			int l = keys[--k].length();
			if(l > maxlen) maxlen = l;
			if(l < minlen) minlen = l;
		}

		// determine all columns with difference
		char[][] k_tab = new char[keys_length][]; // represent as char array
		k = keys_length;
		while(k > 00)
		{
			k_tab[--k] = new char[maxlen];
			System.arraycopy(keys[k].toCharArray(), 0, k_tab[k], 0, keys[k].length());
		}

		int[] sort_cols = new int[maxlen];
		int sort_coli = 0;

		// loop - O(c*k*k*k) - as long as there are 2 or more lines that need a
		// difference column
		int keys_left = keys_length;
		while(keys_left > 1)
		{
			// determine a column with minimum duplicates
			int col = maxlen;
			int min_c = -1;
			int min_dupl = Integer.MAX_VALUE;
			col_loop: while(col > 0)
			{
				col--;
				for(int tmp = 0; tmp < sort_coli; tmp++)
				{
					if(col == sort_cols[tmp]) continue col_loop;
				}
				k = keys_left;
				int same_ch = 0;
				while(k > 0)
				{
					char ch = k_tab[--k][col];
					int k2 = k;
					while(k2 > 0)
						if(ch == k_tab[--k2][col])
						{
							same_ch++;
							break;
						}
					if(same_ch > min_dupl) continue col_loop;
				}
				min_c = col;
				min_dupl = same_ch;
			}
			// save the column as next best
			sort_cols[sort_coli++] = min_c;


			k = keys_left;
			key_loop: while(k > 0)
			{
				--k;
				int k2 = keys_left;
				while(k2 > 0)
				{ // lookup keys that have the same chars
					if(--k2 != k)
					{
						boolean same = true;
						for(int tmp = 0; tmp < sort_coli; tmp++)
						{ // test for difference
							int sc = sort_cols[tmp];
							if(k_tab[k][sc] != k_tab[k2][sc])
							{
								same = false;
								break;
							}
						}
						if(same) continue key_loop;
					}
				}
				// delete the unique line
				k_tab[k] = k_tab[--keys_left];
				k_tab[keys_left] = null;
			}
		}

		hash_cols = new int[sort_coli];
		System.arraycopy(sort_cols, 0, hash_cols, 0, sort_coli);
		Arrays.sort(hash_cols); // sort the cols

		rnd_tab = new int[sort_coli];

		n = keys_length;
		if(n >= perfect_size) n = (int)(n * Math.log(n - perfect_size + 3));

		final String[] inv = new String[n];

		search_loop: while(true)
		{ // TODO: a well formed failure exception
			int j = rnd_tab.length;
			while(j > 00)
				rnd_tab[--j] = 0x7fffffff & rnd.nextInt();

			Arrays.fill(inv, null);

			k = keys.length;
			while(k > 00)
			{
				int h = hash(keys[--k]);
				if(inv[h] != null) continue search_loop; // goto
				inv[h] = keys[k];
			}
			break;
		}
		this.keys = inv; // set the inverse table
	}

	/**
	 * @param name
	 * @return the has b based on the table
	 */
	public int hash(String name)
	{
		int h = 0;
		int c = name.length();
		int i = hash_cols.length;
		while(i > 00)
			if(hash_cols[--i] < c) h += rnd_tab[i] * name.charAt(hash_cols[i]);
		return (h >>> 15) % n;
	}

	/**
	 * @return a string of the hash method
	 */
	public String getHashMethodString()
	{
		String method = "\nprivate static final int hash(String name) {\n";
		if(hash_cols.length == 0) return method + "  return 0;\n}\n";
		if(hash_cols[hash_cols.length - 1] < minlen)
		{
			method += shortHashMethod();
		}
		else
		{
			method += longHashMethod();
		}

		return method + "}\n";
	}

	/**
	 * @return a long representation of the method
	 */
	protected String longHashMethod()
	{
		String s = "  int h=0;\n  int c=name.length();\n";
		for(int i = 0; i < hash_cols.length; i++)
		{
			int col = hash_cols[i];
			if(col >= minlen) if(i > 0)
			{
				s += "   if (c<=" + col + ") return (h>>>15)%" + n + ";\n";
			}
			else
			{
				s += "   if (c<=" + col + ") return 0;\n";
			}
			s += "   h += " + rnd_tab[i] + "*" + "name.charAt(" + col + ");\n";
		}
		return s + "   return (h>>>15)%" + n + ";\n";
	}

	/**
	 * @return short representation
	 */
	protected String shortHashMethod()
	{
		String s = "   return ((";
		int i = hash_cols.length;
		while(i > 1)
			s += rnd_tab[--i] + "*" + "name.charAt(" + hash_cols[i] + ")+";
		return s + rnd_tab[0] + "*" + "name.charAt(" + hash_cols[0] + "))>>>15)%" + n + ";\n";
	}

	static final private Random	rnd	= new Random(System.currentTimeMillis());

	/**
	 * @return the keys - in lookup order
	 */
	public String[] getKeys()
	{
		return keys;
	}

	/**
	 * This is the same as the length of the inverse keys table
	 * 
	 * @return the range of the hash
	 */
	public int getHashRange()
	{
		return keys.length;
	}


}
