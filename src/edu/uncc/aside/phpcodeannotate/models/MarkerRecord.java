package edu.uncc.aside.phpcodeannotate.models;

import java.util.HashSet;
import java.util.Set;

public class MarkerRecord {
	private NodePositionInfo nodePositionInfo;
	private String markerType;
	private int numOfAnnotations;
	private boolean annotated;
	private Set<AnnotationRecord> annotationRecords;
	
	public MarkerRecord(NodePositionInfo nodePositionInfo, String markerType, boolean annotated){
		this.nodePositionInfo = nodePositionInfo;
		this.markerType = markerType;
		this.numOfAnnotations = 0;
		this.annotated = annotated;
		this.annotationRecords = new HashSet<AnnotationRecord>();
	}
	
	public void addAnnotation(AnnotationRecord annotationRecord){
		this.getAnnotationRecords().add(annotationRecord);
		this.setNumOfAnnotations(this.getAnnotationRecords().size());
		this.setAnnotated(true);
	}
	
	public void removeAnnotation(AnnotationRecord annotationRecord){
		if(this.getAnnotationRecords().contains(annotationRecord)){
		    this.getAnnotationRecords().remove(annotationRecord);
		    this.setNumOfAnnotations(this.getAnnotationRecords().size());
		    if(this.getAnnotationRecords().size() == 0)
		    	this.setAnnotated(false);
		}
		else
			System.err.println("annotation records doesn't contain this record.");
	}

	public NodePositionInfo getNodePositionInfo() {
		return nodePositionInfo;
	}

	public void setNodePositionInfo(NodePositionInfo nodePositionInfo) {
		this.nodePositionInfo = nodePositionInfo;
	}

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	public int getNumOfAnnotations() {
		return numOfAnnotations;
	}

	public void setNumOfAnnotations(int numOfAnnotations) {
		this.numOfAnnotations = numOfAnnotations;
	}

	public boolean isAnnotated() {
		return annotated;
	}

	public void setAnnotated(boolean annotated) {
		this.annotated = annotated;
	}

	public Set<AnnotationRecord> getAnnotationRecords() {
		return annotationRecords;
	}

	public void setAnnotationRecords(Set<AnnotationRecord> annotationRecords) {
		this.annotationRecords = annotationRecords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (annotated ? 1231 : 1237);
		result = prime * result
				+ ((markerType == null) ? 0 : markerType.hashCode());
		result = prime
				* result
				+ ((nodePositionInfo == null) ? 0 : nodePositionInfo.hashCode());
		result = prime * result + numOfAnnotations;
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
		MarkerRecord other = (MarkerRecord) obj;
		if (annotated != other.annotated)
			return false;
		if (markerType == null) {
			if (other.markerType != null)
				return false;
		} else if (!markerType.equals(other.markerType))
			return false;
		if (nodePositionInfo == null) {
			if (other.nodePositionInfo != null)
				return false;
		} else if (!nodePositionInfo.equals(other.nodePositionInfo))
			return false;
		if (numOfAnnotations != other.numOfAnnotations)
			return false;
		return true;
	}



}
