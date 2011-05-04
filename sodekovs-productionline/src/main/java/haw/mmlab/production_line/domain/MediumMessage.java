package haw.mmlab.production_line.domain;

import java.io.Serializable;

/**
 * Tag Interface for Message Objects which are sent over the Medium.
 * 
 * @author thomas
 */
public interface MediumMessage extends Serializable {

	public String toString();

	public boolean equals(Object o);
}