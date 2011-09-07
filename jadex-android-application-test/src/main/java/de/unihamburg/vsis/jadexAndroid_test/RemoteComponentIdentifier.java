package de.unihamburg.vsis.jadexAndroid_test;

import jadex.bridge.IComponentIdentifier;

import java.io.Serializable;
import java.rmi.server.RemoteObject;
import java.util.Arrays;

public class RemoteComponentIdentifier implements IComponentIdentifier, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String[] addresses;
	private String localName;
	private RemoteComponentIdentifier parent;
	private String platformName;
	private RemoteComponentIdentifier root;
	
	public RemoteComponentIdentifier(IComponentIdentifier id) {
		this.name = id.getName();
		this.localName = id.getLocalName();
		this.addresses = id.getAddresses();
		IComponentIdentifier parent = id.getParent();
		if (parent != null) {
			this.parent = new RemoteComponentIdentifier(parent);
		}
		this.platformName = id.getPlatformName();
		IComponentIdentifier root = id.getRoot();
		if (root != null) {
			if (Arrays.equals(root.getAddresses(), id.getAddresses())) {
				this.root = this;
			} else {
				this.root = new RemoteComponentIdentifier(root);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public String getPlatformName() {
		return platformName;
	}

	@Override
	public String[] getAddresses() {
		return addresses;
	}

	@Override
	public IComponentIdentifier getParent() {
		return parent;
	}

	@Override
	public IComponentIdentifier getRoot() {
		return root;
	}
	
	@Override
	public String toString() {
		return name + " auf " + root.platformName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RemoteComponentIdentifier) {
			RemoteComponentIdentifier other = (RemoteComponentIdentifier) obj;
			return (name.equals(other.name));
		} 
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
