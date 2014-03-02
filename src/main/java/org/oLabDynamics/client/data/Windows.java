package org.oLabDynamics.client.data;


/**
 * 
 * @author Benoit Charroux
 *
 */
public class Windows extends OperatingSystem{

	String servicePackName;

	public String getServicePackName() {
		return servicePackName;
	}

	public void setServicePackName(String servicePackName) {
		this.servicePackName = servicePackName;
	}

	@Override
	public String toString() {
		return "Windows [servicePackName=" + servicePackName + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((servicePackName == null) ? 0 : servicePackName.hashCode());
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
		Windows other = (Windows) obj;
		if (servicePackName == null) {
			if (other.servicePackName != null)
				return false;
		} else if (!servicePackName.equals(other.servicePackName))
			return false;
		return true;
	}
	
	
	
}
