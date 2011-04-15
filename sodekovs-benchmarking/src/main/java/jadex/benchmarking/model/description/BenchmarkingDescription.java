package jadex.benchmarking.model.description;

import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.bridge.IComponentIdentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An benchmark description.
 */
public class BenchmarkingDescription implements IBenchmarkingDescription, Serializable {
	// -------- attributes ----------

	// /** Attribute for slot languages. */
	// protected List languages;

	/** Attribute for slot componentidentifier. */
	protected IComponentIdentifier suTIdentifiertType;

	/** Attribute for slot name. */
	protected String name;

	/** Attribute for slot type. */
	protected String type;
	
	/** Attribute for slot status. */
	protected String status;

	// /** Attribute for slot ontologies. */
	// protected List ontologies;
	//
	// /** Attribute for slot services. */
	// protected List services;
	//
	// /** Attribute for slot lease-time. */
	// protected java.util.Date leasetime;
	//
	// /** Attribute for slot protocols. */
	// protected List protocols;

	// -------- constructor --------

	/**
	 * Create a new benchmark description.
	 */
	public BenchmarkingDescription() {
		this(null);
	}

	/**
	 * Create a new benchmark description.
	 * 
	 * @param name
	 *            The name.
	 */
	public BenchmarkingDescription(IComponentIdentifier suTIdentifiertType) {
		this(suTIdentifiertType, null, null, null);
	}

	/**
	 * Create a new benchmark description.
	 * 
	 * @param IComponentIdentifier
	 *            id.
	 * @param name
	 *            The name.
	 * @param services
	 *            The type.
	 */
	public BenchmarkingDescription(IComponentIdentifier suTIdentifiertType, String name, String type, String status) {
		this.suTIdentifiertType = suTIdentifiertType;
		this.name = name;
		this.type = type;
		this.status = status;
	}

	// -------- accessor methods --------

	
	/**
	 * Clone a benchmark description.
	 */
	public Object clone() {
		try {
			BenchmarkingDescription ret = (BenchmarkingDescription) super.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cannot clone: " + this);
		}
	}

	/**
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Id of the SuT
	 */
	public IComponentIdentifier getSuTIdentifiertType() {
		return this.suTIdentifiertType;
	}

	public void setSuTIdentifiertType(IComponentIdentifier suTIdentifiertType) {
		this.suTIdentifiertType = suTIdentifiertType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Get a string representation of this benchmark description.
	 * 
	 * @return The string representation.
	 */
	public String toString() {
		return "BenchmarkDescription: " + getName() + " - " + getType() + " - " + getSuTIdentifiertType();
	}
}
