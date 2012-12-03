package deco4mas.distributed.mechanism.convergence;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * This class holds all details which can be adapted by the convergence medium.
 * 
 * @author Thomas Preisler
 */
public class Adaption implements Serializable {

	/**
	 * Serializable Id
	 */
	private static final long serialVersionUID = 8884821429822430817L;

	/** The Id of the realization link which should be adapted */
	private String realizationId = null;

	/** Should the realization link be activated or not? <code>null</code> if a mechanism parameter should be adapted */
	private Boolean active = null;

	/** The key of the parameter that should be adapted */
	private String parameterKey = null;

	/** The value to which the parameter should be adapted */
	private Object parameterValue = null;

	/**
	 * @return the realizationId
	 */
	public String getRealizationId() {
		return realizationId;
	}

	/**
	 * @param realizationId
	 *            the realizationId to set
	 */
	public void setRealizationId(String realizationId) {
		this.realizationId = realizationId;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the parameterKey
	 */
	public String getParameterKey() {
		return parameterKey;
	}

	/**
	 * @param parameterKey
	 *            the parameterKey to set
	 */
	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}

	/**
	 * @return the parameterValue
	 */
	public Object getParameterValue() {
		return parameterValue;
	}

	/**
	 * @param parameterValue
	 *            the parameterValue to set
	 */
	public void setParameterValue(Object parameterValue) {
		this.parameterValue = parameterValue;
	}

	/**
	 * Default constructor.
	 */
	public Adaption() {
		super();
	}

	public static Adaption parseAdaption(String description) throws AdaptionParseException {
		Adaption adaption = new Adaption();

		StringTokenizer tok = new StringTokenizer(description, ":");
		if (!(tok.countTokens() == 2 || tok.countTokens() == 4)) {
			throw new AdaptionParseException("Could not parse " + description);
		}
		String realizationId = tok.nextToken();
		adaption.setRealizationId(realizationId);

		String activeStr = tok.nextToken();
		Boolean active = null;
		if (activeStr.equals("true") || activeStr.equals("false")) {
			active = Boolean.valueOf(activeStr);
		}
		adaption.setActive(active);

		if (tok.hasMoreElements()) {
			String parameterKey = tok.nextToken();
			String parameterValue = tok.nextToken();

			adaption.setParameterKey(parameterKey);
			adaption.setParameterValue(parameterValue);
		}

		return adaption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Adaption [realizationId=" + realizationId + ", active=" + active + ", parameterKey=" + parameterKey + ", parameterValue=" + parameterValue + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((parameterKey == null) ? 0 : parameterKey.hashCode());
		result = prime * result + ((parameterValue == null) ? 0 : parameterValue.hashCode());
		result = prime * result + ((realizationId == null) ? 0 : realizationId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Adaption other = (Adaption) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (parameterKey == null) {
			if (other.parameterKey != null)
				return false;
		} else if (!parameterKey.equals(other.parameterKey))
			return false;
		if (parameterValue == null) {
			if (other.parameterValue != null)
				return false;
		} else if (!parameterValue.equals(other.parameterValue))
			return false;
		if (realizationId == null) {
			if (other.realizationId != null)
				return false;
		} else if (!realizationId.equals(other.realizationId))
			return false;
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Adaption adaption = parseAdaption("realizationId:null:key:value");
			Adaption adaption2 = parseAdaption("realizationId:true");
			System.out.println(adaption);
			System.out.println(adaption2);
		} catch (AdaptionParseException e) {
			e.printStackTrace();
		}
	}
}