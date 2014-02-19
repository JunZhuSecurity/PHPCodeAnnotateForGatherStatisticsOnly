package edu.uncc.aside.phpcodeannotate.models;

import java.util.HashSet;
import java.util.Set;

public class AnnotationRecord {
	//search annotation by sensitive accessor, so each accessor could have one or more annotations
	//and it is also possible that one annotation is shared by multiple accessor
	//so how to avoid, we need to have this connection so that we can track which annotation
	//is shared among which accessors, the bridge is the ASTNode 
	//so maybe, a map? each path has a collection of annotations.
	//so there are multiple nodes and status to keep track of
	
	public NodePositionInfo nodePositionInfo;
	public String annotationType;
	public int numOfCorrespondingMarkers;
	public Set<MarkerRecord> correspondingMarkerRecords; 
	
	public AnnotationRecord(NodePositionInfo nodePositionInfo, String annotationType, MarkerRecord markerRecord){
		
		/*if(markerRecord == null){
			System.err.println("ERROR: markerRecord == null in creating AnnotationRecord");
			return;
		}*/
		this.nodePositionInfo = nodePositionInfo;
		this.annotationType = annotationType;
		
		if(this.getCorrespondingMarkerRecords() == null){
			this.setCorrespondingMarkerRecords(new HashSet<MarkerRecord>());
		}
			this.getCorrespondingMarkerRecords().add(markerRecord);
			this.setNumOfCorrespondingMarkers(this.getCorrespondingMarkerRecords().size());
		
	}
	
	public void addMarker(MarkerRecord markerRecord){
		if(this.getCorrespondingMarkerRecords() == null){
			this.setCorrespondingMarkerRecords(new HashSet<MarkerRecord>());
		}
			this.getCorrespondingMarkerRecords().add(markerRecord);
			this.setNumOfCorrespondingMarkers(this.getCorrespondingMarkerRecords().size());
	}
	
	
	public NodePositionInfo getNodePositionInfo() {
		return nodePositionInfo;
	}



	public void setNodePositionInfo(NodePositionInfo nodePositionInfo) {
		this.nodePositionInfo = nodePositionInfo;
	}



	public String getAnnotationType() {
		return annotationType;
	}



	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}



	public int getNumOfCorrespondingMarkers() {
		return numOfCorrespondingMarkers;
	}



	public void setNumOfCorrespondingMarkers(int numOfCorrespondingMarkers) {
		this.numOfCorrespondingMarkers = numOfCorrespondingMarkers;
	}



	public Set<MarkerRecord> getCorrespondingMarkerRecords() {
		return correspondingMarkerRecords;
	}



	public void setCorrespondingMarkerRecords(
			Set<MarkerRecord> correspondingMarkerRecords) {
		this.correspondingMarkerRecords = correspondingMarkerRecords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotationType == null) ? 0 : annotationType.hashCode());
		result = prime
				* result
				+ ((nodePositionInfo == null) ? 0 : nodePositionInfo.hashCode());
		result = prime * result + numOfCorrespondingMarkers;
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
		AnnotationRecord other = (AnnotationRecord) obj;
		if (annotationType == null) {
			if (other.annotationType != null)
				return false;
		} else if (!annotationType.equals(other.annotationType))
			return false;
		if (nodePositionInfo == null) {
			if (other.nodePositionInfo != null)
				return false;
		} else if (!nodePositionInfo.equals(other.nodePositionInfo))
			return false;
		if (numOfCorrespondingMarkers != other.numOfCorrespondingMarkers)
			return false;
		return true;
	}

	
}

