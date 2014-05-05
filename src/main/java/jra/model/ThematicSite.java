package jra.model;

import java.net.URI;
import java.util.List;

import jra.JRA.PublicationMode;
import jra.JRAException;

import org.oLabDynamics.jra.rest.Resource;
import org.springframework.http.HttpHeaders;

/**
 * 
 * @author Benoit Charroux
 *
 */
public abstract class ThematicSite{
	
	protected URI uri;
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public abstract void addCompanionSite(CompanionSite companionSite);

	//@XmlElement(name = "companionSites", required = true)
	public abstract List<CompanionSite> getCompanionSites();
	
	public abstract void publishThematicSite();
	
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
		return "ThematicSite [uri=" + uri + "]";
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
		// if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass())
			return false;
		ThematicSite other = (ThematicSite) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	
}
