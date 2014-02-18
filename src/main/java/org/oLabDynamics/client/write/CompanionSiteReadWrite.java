package org.oLabDynamics.client.write;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.oLabDynamics.client.Code;
import org.oLabDynamics.client.CompanionSite;
import org.oLabDynamics.client.Publication;
import org.oLabDynamics.client.ThematicSite;
import org.oLabDynamics.rest.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

/**
 * Caches data from the server: get data from the server only at the first call of a getter method.
 * @author charroux
 *
 */
public class CompanionSiteReadWrite extends CompanionSite{
	
	@JsonIgnore
	Publication publication = null;
	
	Code referenceImplementation = null;
	
	@JsonIgnore
	List<ThematicSite> thematicSites = null;
	
	public CompanionSiteReadWrite(){
		super();
	}

	@Override
	public Publication getPublication(){
		if(publication == null){	// la liste des publications n'a pas �t� r�initialis�e, on peut prendre celle du serveur
			publication = super.getPublication();
		}
		return publication;
	}

	void setPublication(PublicationReadWrite publication) {
		this.publication = publication;
	}
	
	@Override
	public List<ThematicSite> getThematicSites() {
		if(thematicSites == null){	// la liste des thematicSites n'a pas �t� r�initialis�e, on peut prendre celle du serveur
			List<ThematicSite> thematicSitesFromTheServer = super.getThematicSites();
			if(thematicSitesFromTheServer == null){
				return thematicSites = new ArrayList<ThematicSite>();
			} else {
				return thematicSites = thematicSitesFromTheServer;
			}
		}
		return thematicSites;
	}
	
	void addThematicSite(ThematicSite thematicSite) {
		if(thematicSites == null){	// la liste des thematicSites n'a pas �t� r�initialis�e, on peut prendre celle du serveur
			thematicSites = this.getThematicSites();
		}
		this.thematicSites.add(thematicSite);
	}
	
	@Override
	public Code getReferenceImplementation() {
		if(referenceImplementation == null){
			referenceImplementation = super.getReferenceImplementation();
		}
		return referenceImplementation;
	}

	public void setReferenceImplementation(CodeReadWrite referenceImplementation) {
		this.referenceImplementation = referenceImplementation;
		referenceImplementation.setCompanionSite(this);
	}



	@Override
	public String toString() {
		return "CompanionSiteReadWrite [referenceImplementation="
				+ referenceImplementation + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((publication == null) ? 0 : publication.hashCode());
		result = prime
				* result
				+ ((referenceImplementation == null) ? 0
						: referenceImplementation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompanionSiteReadWrite other = (CompanionSiteReadWrite) obj;
		if (publication == null) {
			if (other.publication != null)
				return false;
		} else if (!publication.equals(other.publication))
			return false;
		if (referenceImplementation == null) {
			if (other.referenceImplementation != null)
				return false;
		} else if (!referenceImplementation
				.equals(other.referenceImplementation))
			return false;
		return true;
	}

	

}