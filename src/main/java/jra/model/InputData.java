package jra.model;

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

//@XmlRootElement(name = "InputData")
//@XmlType(name = "", propOrder = {"nameInCode","nickName","description","kind","type","mandatory","multipleValues","data"})
@XmlType(propOrder = {"nameInCode","nickName","description","kind","type","mandatory","multipleValues","data"})
public abstract class InputData implements Cloneable {
	
	protected String description;
	
	public enum Kind{
		SCALAR,
		VECTOR,
		MATRIX,
		//CHOICE_LIST,
		CHOICE,
		RAW,
		//TABLE,		// ????	=> remplace par List<InputDat>
		//COLUMN,		// ????	=> remplace par List<InputDat>
		TEXT
		
		// IMAGE,	= FILE
		// VIDEO,	= FILE
		// AUDIO,	= FILE
		// OTHER			// ??? usefull ???
		
	}
	
	public enum Type{
		INTEGER,
		REAL,
		//COMPLEX,
		STRING,
		UNDEFINED
	}

	protected String nameInCode;
	protected Kind kind;
	protected String nickName;
	protected boolean mandatory;
	protected Type type;

	protected boolean multipleValues;
	
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
	
	@XmlElement(name = "nameInCode", required = true)
	public String getNameInCode() {
		return nameInCode;
	}

	public void setNameInCode(String nameInCode) {
		this.nameInCode = nameInCode;
	}

	@XmlElement(name = "kind", required = true)
	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	@XmlElement(name = "nickName", required = true)
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@XmlElement(name = "mandatory", required = true)
	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@XmlElement(name = "type", required = true)
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@XmlElement(name = "data", required = true)
	public abstract Object getData() throws JRAException;
	
	public abstract void setData(Object data);
	
	@XmlElement(name = "multipleValues", required = false)
	public boolean isMultipleValues() {
		return multipleValues;
	}

	public void setMultipleValues(boolean multipleValues) {
		this.multipleValues = multipleValues;
	}

	/**
	 * Restricted access: can not be used without special authorization.
	 * Use {@link org.oLabDynamics.client.JRA#publish(CompanionSite, PUBLICATION_MODE) publish} instead.
	 * @param publicationMode
	 * @throws JRAException
	 */
	public abstract void publish(PublicationMode publicationMode) throws JRAException;
	

	@Override
    public InputData clone() throws CloneNotSupportedException {
		InputData clone = (InputData) super.clone();
        return clone;
    }

	@Override
	public String toString() {
		return "InputData [description=" + description + ", nameInCode="
				+ nameInCode + ", kind=" + kind + ", nickName=" + nickName
				+ ", mandatory=" + mandatory + ", type=" + type
				+ ", multipleValues=" + multipleValues + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + (mandatory ? 1231 : 1237);
		result = prime * result + (multipleValues ? 1231 : 1237);
		result = prime * result
				+ ((nameInCode == null) ? 0 : nameInCode.hashCode());
		result = prime * result
				+ ((nickName == null) ? 0 : nickName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		// if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass())
			return false;
		InputData other = (InputData) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (kind != other.kind)
			return false;
		if (mandatory != other.mandatory)
			return false;
		if (multipleValues != other.multipleValues)
			return false;
		if (nameInCode == null) {
			if (other.nameInCode != null)
				return false;
		} else if (!nameInCode.equals(other.nameInCode))
			return false;
		if (nickName == null) {
			if (other.nickName != null)
				return false;
		} else if (!nickName.equals(other.nickName))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	
	
}
