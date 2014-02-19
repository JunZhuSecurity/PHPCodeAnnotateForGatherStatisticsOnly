package edu.uncc.aside.phpcodeannotate.models;

import edu.uncc.aside.phpcodeannotate.Constants;

public class SensitiveMethod {
	public String dispatcherType;
	public String functionName;
	
	public SensitiveMethod(String dispatcherType, String functionName){
		this.dispatcherType = dispatcherType;
		this.functionName = functionName;
	}
	
	public SensitiveMethod(String functionNam){
		this.dispatcherType = Constants.isPureFunction;
		this.functionName = functionName;
	}

	public String getDispatcherType() {
		return dispatcherType;
	}

	public void setDispatcherType(String dispatcherType) {
		this.dispatcherType = dispatcherType;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dispatcherType == null) ? 0 : dispatcherType.hashCode());
		result = prime * result
				+ ((functionName == null) ? 0 : functionName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensitiveMethod other = (SensitiveMethod) obj;
		if (dispatcherType == null) {
			if (other.dispatcherType != null)
				return false;
		} else if (!dispatcherType.equals(other.dispatcherType))
			return false;
		if (functionName == null) {
			if (other.functionName != null)
				return false;
		} else if (!functionName.equals(other.functionName))
			return false;
		return true;
	}
	
	
	
	

}
