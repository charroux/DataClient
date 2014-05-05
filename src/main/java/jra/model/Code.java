package jra.model;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import jra.JRA.PublicationMode;
import jra.JRAException;

import org.oLabDynamics.jra.rest.Resource;
/**
 * 
 * @author Benoit Charroux
 *
 */
//@XmlRootElement(name = "Code")
@XmlType(name = "", propOrder = {"description","inputs","outputs"})
//@XmlType(propOrder = {"description"})
public abstract class Code {
	
	protected String description;
	
	public abstract boolean addInput(InputData input);
	
	public abstract boolean addOutput(OutputData output);
	
	@XmlTransient
	public abstract List<CompanionSite> getCompanionSites();
	
	public abstract boolean addCompanionSite(CompanionSite companionSite);
	
	@XmlAnyElement
	public abstract List<InputData> getInputs();
	
	@XmlAnyElement
	public abstract List<OutputData> getOutputs();
	
	@XmlElement(name = "description", required = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract void export(File file);
		
	/**
	 * Restricted access: can not be used without special authorization.
	 * Use {@link org.oLabDynamics.client.JRA#publish(CompanionSite, PUBLICATION_MODE) publish} instead.
	 * @param publicationMode
	 * @throws JRAException
	 */
	public abstract void publish(PublicationMode publicationMode) throws JRAException;

	@Override
	public String toString() {
		return "Code [description=" + description + "]";
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
		Code other = (Code) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	
}
