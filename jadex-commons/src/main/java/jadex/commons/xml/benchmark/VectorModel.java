package jadex.commons.xml.benchmark;
import java.util.Vector;

public class VectorModel
	{
		private Vector v1;
		private Vector v2;
		
		public VectorModel()
		{
			v1 = new Vector();
			v2 = new Vector();
		}
		
		public Vector getV1()
		{
			return v1;
		}

		public void setV1(Vector v1)
		{
			this.v1 = v1;
		}

		public Vector getV2()
		{
			return v2;
		}

		public void setV2(Vector v2)
		{
			this.v2 = v2;
		}

		public void addToV1(Object o)
		{
			v1.add(o);
		}
		
		public String toString()
		{
			return "v1-size: " + v1.size() + " v2-size: " + v2.size();
		}
	}
