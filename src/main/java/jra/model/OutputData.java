package jra.model;

import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.JRA.PublicationMode;
import jra.JRAException;

import org.oLabDynamics.jra.rest.Resource;
import org.springframework.http.HttpHeaders;

/**
 * 
 * @author Benoit Charroux
 *
 */

//@XmlRootElement(name = "OutputData")
//@XmlType(name = "", propOrder = {"description"})
@XmlType(propOrder = {"description"})
public abstract class OutputData {
	
	protected String description;
	
	@XmlTransient
	public abstract Code getCode();
	
	public abstract void setCode(Code code);
	
	@XmlElement(name = "description", required = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Restricted access: can not be used without special authorization.
	 * Use {@link org.oLabDynamics.client.JRA#publish(CompanionSite, PUBLICATION_MODE) publish} instead.
	 * @param publicationMode
	 * @throws JRAException
	 */
	public abstract void publish(PublicationMode publicationMode) throws JRAException;
	
	//public abstract void publish(PublicationMode publicationMode, HttpHeaders headers);

	@Override
	public String toString() {
		return "OutputData [description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		// if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass())
			return false;
		OutputData other = (OutputData) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}
	
}
