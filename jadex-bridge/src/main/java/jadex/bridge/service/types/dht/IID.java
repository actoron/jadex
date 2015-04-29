package jadex.bridge.service.types.dht;



public interface IID extends Comparable<IID>
{

	boolean isInInterval(IID id, IID key);
	boolean isInInterval(IID id, IID iid, boolean b, boolean c);
	int getLength();
	byte[] getBytes();
	IID addPowerOfTwo(int i);
	
	IID createNew();
	
}
