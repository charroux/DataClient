package jra.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.oLabDynamics.jra.rest.Resource;

import jra.JRA.PublicationMode;
import jra.JRAException;


/**
 * 
 * @author Benoit Charroux
 *
 */

//@XmlRootElement(name = "Publication")
//@XmlType(namespace="jra", propOrder = {"title","publicationType","authors"})
//@XmlType(namespace="jra")
//@XmlType(name = "publication", namespace = Publication.ATOM_NAMESPACE)
//@XmlType(propOrder = {"publicationType","authors","title"})
//@XmlType(namespace = Publication.ATOM_NAMESPACE)
public abstract class Publication{
	
	public static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
	
	public enum PublicationType{
		ARTICLE,
		WORKING_PAPER
	}
	
	protected PublicationType publicationType;
	protected String title;
	
	public void setTitle(String title) {
		this.title = title;
	}

	//@XmlElement(name = "title", required = true)
	@XmlElement(name = "title", namespace = Publication.ATOM_NAMESPACE)
	public String getTitle() {
		return title;
	}
	
	public abstract boolean addAuthor(Author author, int authorOrdering);

	/**
	 * 
	 * @return all authors: those present at the server (if they exist and if they do not have been removed by {@link #removeAuthor removeAuthor}) 
	 * plus all added authors with {@link #addAuthor addAuthor}.
	 * Notice that {@link #setAuthors setAuthors} resets the entire list so that the list is no more gotten from the server.
	 */
	//@XmlElement(name = "authors", required = true)
	@XmlAnyElement
	public abstract List<Author> getAuthors();
	
	public abstract void setAuthors(List<Author> authors);
	
	@XmlTransient
	public abstract CompanionSite getCompanionSite();

	@XmlElement(name = "publicationType", required = true)
	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(PublicationType publicationType) {
		this.publicationType = publicationType;
	}

	/**
	 * Restricted access: can not be used without special authorization.
	 * Use {@link org.oLabDynamics.client.JRA#publish(CompanionSite, PUBLICATION_MODE) publish} instead.
	 * @param publicationMode
	 * @throws JRAException
	 */
	public abstract void publish(PublicationMode publicationMode) throws JRAException;

	@Override
	public String toString() {
		return "Publication [publicationType=" + publicationType + ", title="
				+ title + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((publicationType == null) ? 0 : publicationType.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		//if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass())
			return false;
		Publication other = (Publication) obj;
		if (publicationType != other.publicationType)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
		
}
