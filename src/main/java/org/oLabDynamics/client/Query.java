package org.oLabDynamics.client;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 
 * @author Benoit Charroux
 *
 */
public class Query {
	
	class Filter{
		FilterOperator filterOperator;
		String value;
	}
	
	Hashtable<String,Filter> filters = new Hashtable<String,Filter>();

	public enum FilterOperator{
		EQUAL,
		LESS_THAN,
		GREATER_THAN, 
		CONTAIN
	}

	private String rel;

	public Query(String rel) {
		this.rel = rel;
	}

	public String getRel() {
		return rel;
	}

	public void addFilter(String attribut, FilterOperator filterOperator, String value) {
		Filter filter = new Filter();
		filter.filterOperator = filterOperator;
		filter.value = value;
		filters.put(attribut, filter);
	}

	public String format() {
		String formatedFilters = "";
		String attribut;
		Filter filter;
		Enumeration<String> attributs = filters.keys();
		while(attributs.hasMoreElements()){
			attribut = attributs.nextElement();
			formatedFilters += attribut;
			filter = filters.get(attribut);
			switch(filter.filterOperator){
			case EQUAL:
				formatedFilters += "==" + filter.value;
				break;
			case LESS_THAN:
				formatedFilters += "<" + filter.value;
				break;
			case GREATER_THAN:
				formatedFilters += ">" + filter.value;
				break;
			case CONTAIN:
				formatedFilters += "<>" + filter.value;
				break;
			}
			if(attributs.hasMoreElements()){
				formatedFilters += ",";
			}
		}
		return formatedFilters;
	}

}
