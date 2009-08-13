package nuggets.benchmark;

import java.util.ArrayList;
import java.util.List;


public class A
{
	protected int i;
	
	protected String s;
	
	protected B b;
	
	protected List bs;
	
	public A()
	{
	}
	
	public A(int i, String s, B b, B[] bs)
	{
		this.i = i;
		this.s = s;
		this.b = b;
		if(bs!=null)
		{
			this.bs = new ArrayList();
			for(int j=0; j<bs.length; j++)
			{
				this.bs.add(bs[j]);
			}
		}
	}

	public int getI()
	{
		return this.i;
	}

	public void setI(int i)
	{
		this.i = i;
	}

	public String getS()
	{
		return this.s;
	}

	public void setS(String s)
	{
		this.s = s;
	}

	public B getB()
	{
		return this.b;
	}

	public void setB(B b)
	{
		this.b = b;
	}

	public B[] getBs()
	{
		return (B[])(this.bs==null? new B[0]: this.bs.toArray(new B[0]));
	}

	public void setBs(B[] bs)
	{
		this.bs = new ArrayList();
		for(int j=0; j<bs.length; j++)
		{
			this.bs.add(bs[j]);
		}
	}
	
	public void addB(B b)
	{
		if(bs==null)
			bs = new ArrayList();
		bs.add(b);
	}
	
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.b == null) ? 0 : this.b.hashCode());
		result = prime * result + ((this.bs == null) ? 0 : this.bs.hashCode());
		result = prime * result + this.i;
		result = prime * result + ((this.s == null) ? 0 : this.s.hashCode());
		return result;
	}

	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		A other = (A)obj;
		if(this.b == null)
		{
			if(other.b != null)
				return false;
		}
		else if(!this.b.equals(other.b))
			return false;
		if(this.bs == null)
		{
			if(other.bs != null)
				return false;
		}
		else if(!this.bs.equals(other.bs))
			return false;
		if(this.i != other.i)
			return false;
		if(this.s == null)
		{
			if(other.s != null)
				return false;
		}
		else if(!this.s.equals(other.s))
			return false;
		return true;
	}

	public String toString()
	{
		return "A [b=" + this.b + ", bs=" + bs + ", i="
				+ this.i + ", s=" + this.s + "]";
	}
}