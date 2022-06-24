export class RingBuffer 
{
	constructor(n) 
	{
    	this.values = new Array(n);
		this.pos = 0;
	}
	
	length() 
	{
    	return this.pos>this.values.length? this.values.length: this.pos;
    }

	
	add(v)
	{
		this.values[this.pos++%this.values.length] = v;
	}
	
	get(i) 
	{
    	if(i<0 || i<this.length-this.values.length)
    	    return undefined;
    	return this.values[i%this.values.length];
	}
	
	toArray()
	{
		var ret = new Array(this.length());
		for(var i=0; i<ret.length; i++)
		{
			if(this.length()<this.values.length)
			{
				// buffer not full
				ret[i] = this.values[i%this.values.length];	
			}
			else
			{
				// buffer overfull
				ret[i] = this.values[(this.pos+i)%this.values.length];
			}
		}
		return ret;
	}
	
	toString() 
	{
    	return '[object RingBuffer('+this.values.length+') length '+this.length()+']';
	}
}

