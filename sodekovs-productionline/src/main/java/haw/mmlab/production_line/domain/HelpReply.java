package haw.mmlab.production_line.domain;

import haw.mmlab.production_line.configuration.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * HelpReply class containing the old roles of the original request
 * replaceAgentId which is taken by the help reply replaceAgentId, the role of
 * the help reply replaceAgentId which is now vacant and the reply defectAgentId
 * and replaceAgentId id.
 * 
 * @author thomas
 */
public class HelpReply implements MediumMessage {

	/**
	 * Id for Serialization.
	 */
	private static final long serialVersionUID = -276488014242362627L;

	/**
	 * The defectAgentId, the original request replaceAgentId.
	 */
	private String defectAgentId = null;

	/**
	 * The replaceAgentId.
	 */
	private String replaceAgentId = null;

	/**
	 * The old roles of the request replaceAgentId which has been taken from the
	 * reply replaceAgentId.
	 */
	private List<Role> takenRoles = null;

	/**
	 * The new roles of the request replaceAgentId.
	 */
	private List<Role> vacantRoles = null;

	private Set<String> receiverIds = null;

	/**
	 * Default constructor.
	 */
	public HelpReply() {
		super();
		this.receiverIds = new HashSet<String>();
		this.vacantRoles = new ArrayList<Role>();
		this.takenRoles = new ArrayList<Role>();
	}

	/**
	 * @return the receiverIds
	 */
	public Set<String> getReceiverIds() {
		return receiverIds;
	}

	/**
	 * @param receiverIds
	 *            the receiverIds to set
	 */
	public void setReceiverIds(Set<String> receiverIds) {
		this.receiverIds = receiverIds;
	}

	/**
	 * Adds the given receiverId to the {@link Set} of receivers.
	 * 
	 * @param receiverId
	 *            the given receiverId
	 */
	public void addReceiver(String receiverId) {
		this.receiverIds.add(receiverId);
	}

	/**
	 * @return the takenRoles
	 */
	public List<Role> getTakenRoles() {
		return takenRoles;
	}

	/**
	 * @param takenRoles
	 *            the takenRoles to set
	 */
	public void setTakenRoles(List<Role> takenRoles) {
		this.takenRoles = takenRoles;
	}

	/**
	 * @return the vacantRole
	 */
	public List<Role> getVacantRoles() {
		return vacantRoles;
	}

	/**
	 * @param vacantRoles
	 *            the vacantRoles to set
	 */
	public void setVacantRoles(List<Role> vacantRoles) {
		this.vacantRoles = vacantRoles;
	}

	/**
	 * @return the defectAgentId
	 */
	public String getDefectAgentId() {
		return defectAgentId;
	}

	/**
	 * @param defectAgentId
	 *            the defectAgentId to set
	 */
	public void setDefectAgentId(String defectAgentId) {
		this.defectAgentId = defectAgentId;
	}

	/**
	 * @return the replaceAgentId
	 */
	public String getReplaceAgentId() {
		return replaceAgentId;
	}

	/**
	 * @param replaceAgentId
	 *            the replaceAgentId to set
	 */
	public void setReplaceAgentId(String replaceAgentId) {
		this.replaceAgentId = replaceAgentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HelpReply [" + (!vacantRoles.isEmpty() ? "vacantRoles=" + vacantRoles + ", " : "")
				+ (!takenRoles.isEmpty() ? "takenRoles=" + takenRoles + ", " : "")
				+ (defectAgentId != null ? "defectAgentId=" + defectAgentId + ", " : "")
				+ (replaceAgentId != null ? "replaceAgentId=" + replaceAgentId : "") + "]";
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
		result = prime * result + ((defectAgentId == null) ? 0 : defectAgentId.hashCode());
		result = prime * result + ((receiverIds == null) ? 0 : receiverIds.hashCode());
		result = prime * result + ((replaceAgentId == null) ? 0 : replaceAgentId.hashCode());
		result = prime * result + ((takenRoles == null) ? 0 : takenRoles.hashCode());
		result = prime * result + ((vacantRoles == null) ? 0 : vacantRoles.hashCode());
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
		HelpReply other = (HelpReply) obj;
		if (defectAgentId == null) {
			if (other.defectAgentId != null)
				return false;
		} else if (!defectAgentId.equals(other.defectAgentId))
			return false;
		if (receiverIds == null) {
			if (other.receiverIds != null)
				return false;
		} else if (!receiverIds.equals(other.receiverIds))
			return false;
		if (replaceAgentId == null) {
			if (other.replaceAgentId != null)
				return false;
		} else if (!replaceAgentId.equals(other.replaceAgentId))
			return false;
		if (takenRoles == null) {
			if (other.takenRoles != null)
				return false;
		} else if (!takenRoles.equals(other.takenRoles))
			return false;
		if (vacantRoles == null) {
			if (other.vacantRoles != null)
				return false;
		} else if (!vacantRoles.equals(other.vacantRoles))
			return false;
		return true;
	}
}