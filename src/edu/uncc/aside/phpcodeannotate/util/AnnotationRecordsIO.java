package edu.uncc.aside.phpcodeannotate.util;
import java.util.Collection;
import java.util.Iterator;

import edu.uncc.aside.phpcodeannotate.models.AnnotationRecord;

public class AnnotationRecordsIO {
	private String filePath;
	
	public AnnotationRecordsIO(String filePath){
		if(filePath != null) //maybe add one additional check, the filePath should exist
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void writeSingleAnnotationRecord(){
		
	}
	
	public void writeAnnotationRecords(Collection<AnnotationRecord> annotationRecords){
		Iterator<AnnotationRecord> iter = annotationRecords.iterator();
		AnnotationRecord tmp = null;
		//file io;
		
		while(iter.hasNext()){
			tmp = iter.next();
			
		}
	}
	
	public Collection<AnnotationRecord> readAnnotationRecords(){
		Collection<AnnotationRecord> collection = null;
		
		return collection;
	}
	
	public void restoreAnnotationsToEclipse(){
		//first read all annotation records from storage
		
		//pop these annotations to the markers, (resource, markers, etc), 
		//should be called upon opening of Eclipse
		
	}
	public void transmitAnnotationsEclipseToStorage(){
		//gatherAllAnnotationsFromMarkers()
		//writeAnnotationRecords();
	}
	
	//this method might be needed or probably not, depends on whether 
	public void gatherAllAnnotationsFromMarkers(){
		
	}

	
}
