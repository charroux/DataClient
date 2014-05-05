package jra.model;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import jra.JRA.PublicationMode;
import jra.JRAException;

import org.oLabDynamics.jra.rest.Resource;

/**
 * 
 * @author Benoit Charroux
 *
 */

//@XmlRootElement(name = "CompanionSite")
//@XmlType(name = "", propOrder = {"uri","publication","code"})
//@XmlType(propOrder = {"publication","uri","code"})
public abstract class CompanionSite{
	
	protected URI uri;
	
	@XmlElement(name = "publication", required = true)
	public abstract Publication getPublication();

	public abstract void setPublication(Publication publication);
	
	public abstract List<ThematicSite> getThematicSites();
	
	public abstract boolean addThematicSite(ThematicSite thematicSite);

	@XmlElement(name = "code", required = true)
	public abstract Code getCode();
	
	public abstract void setCode(Code code);
	
/*	void setCode(Code code) {
		this.code = code;
	}
*/	
	public abstract void publish(PublicationMode publicationMode) throws JRAException;
	
	/**
	 * Get the uri of this companionSite: a companion site can come from any Libraries for Reproducibility,
	 * the uri indentify the origin of the companionSite (which library it comes from), 
	 * and give the unique identifier it has.
	 * @return 
	 */
	@XmlElement(name = "uri", required = true)
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "CompanionSite [uri=" + uri + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		//if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass())
			return false;
		CompanionSite other = (CompanionSite) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	
	
}
