package jra;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Class to query the Exec & Share data model.
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

	private String dataType;

	/**
	 * Create a query to retrieve a data form the {@link org.oLabDynamics.jra.model Exec & Share data model} (an author, a companion site, a publication...).
	 * 
	 * @param dataType what to find. Must be one of the Exec & Share data model type like author, publication (see {@link org.oLabDynamics.jra.model Exec & Share data model})
	 */
	public Query(String dataType) {
		this.dataType = dataType;
	}

	public String getDataType() {
		return dataType;
	}

	/**
	 * 
	 * Add filters to a query to limit the number of responses.
	 * 
	 * @param attribut
	 * @param filterOperator
	 * @param value
	 */
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
