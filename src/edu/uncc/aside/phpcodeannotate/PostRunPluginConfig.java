package edu.uncc.aside.phpcodeannotate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import edu.uncc.aside.phpcodeannotate.models.AnnotationRecord;
import edu.uncc.aside.phpcodeannotate.models.MarkerRecord;
import edu.uncc.aside.phpcodeannotate.util.Utils;

public class PostRunPluginConfig {
	//write Markers info to persistent file
	public static void config(HashSet<MarkerRecord> allMarkerRecords, HashSet<AnnotationRecord> allAnnotationRecords){
	//if(Plugin.FIRST_TIME_RUN == true){  //if the plugin is run the first time, then the marker records need to be stored, or else, don't store marker records
		writeMarkerRecordIntoFile(allMarkerRecords);
	//	writeAnnotationRecordIntoFile(allAnnotationRecords);
	//}
	}
	
	private static void writeAnnotationRecordIntoFile(
			HashSet<AnnotationRecord> allAnnotationRecords) {
        String fileName = Plugin.STATE_LOCATION + "/" + Plugin.ANNOTATION_RECORD_FILE;
		
		System.out.println("size of annotations =========== " + Plugin.allAnnotationRecords.size());
		Iterator<AnnotationRecord> iter = allAnnotationRecords.iterator();
		AnnotationRecord tmpAnnotationRecord = null;
		String fileDir = null;
		int nodeStart, nodeLength;
		String markerType;
		boolean isAnnotated;
		String seperator = Plugin.COMMA;
		StringBuffer strBuf = new StringBuffer();
		
		
		while(iter.hasNext()){
			StringBuffer markerStrBuf = new StringBuffer();
			Iterator<MarkerRecord> markerIter = null;
			MarkerRecord markerRecord = null;
			int annotationHashCode;
			
			tmpAnnotationRecord = iter.next();
			annotationHashCode = tmpAnnotationRecord.hashCode();
			markerType = tmpAnnotationRecord.getAnnotationType();
			fileDir = tmpAnnotationRecord.getNodePositionInfo().getFileDir();
			nodeStart = tmpAnnotationRecord.getNodePositionInfo().getStartPosition();
			nodeLength = tmpAnnotationRecord.getNodePositionInfo().getLength();
			
			markerIter = tmpAnnotationRecord.getCorrespondingMarkerRecords().iterator();
			while(markerIter.hasNext()){
				markerRecord = markerIter.next();
				if(markerIter.hasNext())
				markerStrBuf.append(markerRecord.hashCode() + Plugin.ITEM_SEPERATOR);
				else
					markerStrBuf.append(markerRecord.hashCode());
			}
			
			//store the Marker information in the persistent file
			String str = annotationHashCode + seperator + fileDir + seperator + nodeStart + seperator + nodeLength + seperator + markerType + seperator + markerStrBuf.toString() + "\n";
			strBuf.append(str);
			System.out.println("fileDir=" + fileDir + ", nodeStart=" + nodeStart + ",nodeLength=" + nodeLength + ", markerType=" + markerType);
		}
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(strBuf.toString());
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void writeMarkerRecordIntoFile(HashSet<MarkerRecord> allMarkerRecords){
		
		String basePath = Utils.getPlugingBasePath();
		String fileName = basePath + Plugin.Cleaned_MARKER_RECORD_FILE;
		
		
		System.out.println("size of markers =========== " + Plugin.allMarkerRecords.size());
		Iterator<MarkerRecord> iter = allMarkerRecords.iterator();
		MarkerRecord tmpMarkerRecord = null;
		String fileDir = null;
		int nodeStart, nodeLength;
		String markerType;
		boolean isAnnotated;
		String seperator = Plugin.COMMA;
		StringBuffer strBuf = new StringBuffer();
		
		
		while(iter.hasNext()){
			StringBuffer annotationsStrBuf = new StringBuffer();
			Iterator<AnnotationRecord> iterAnnotation = null;
			AnnotationRecord annotationRecord = null;
			int markerHashCode;
			
			tmpMarkerRecord = iter.next();
			markerHashCode = tmpMarkerRecord.hashCode();
			markerType = tmpMarkerRecord.getMarkerType();
			isAnnotated = tmpMarkerRecord.isAnnotated();
			fileDir = tmpMarkerRecord.getNodePositionInfo().getFileDir();
			nodeStart = tmpMarkerRecord.getNodePositionInfo().getStartPosition();
			nodeLength = tmpMarkerRecord.getNodePositionInfo().getLength();
			
			iterAnnotation = tmpMarkerRecord.getAnnotationRecords().iterator();
			while(iterAnnotation.hasNext()){
				annotationRecord = iterAnnotation.next();
				if(iterAnnotation.hasNext())
				annotationsStrBuf.append(annotationRecord.hashCode() + Plugin.ITEM_SEPERATOR);
				else
					annotationsStrBuf.append(annotationRecord.hashCode());
			}
			//store the Marker information in the persistent file
			String str = markerHashCode + seperator + fileDir + seperator + nodeStart + seperator + nodeLength + seperator + markerType + seperator + isAnnotated + seperator + annotationsStrBuf.toString() + "\n";
			strBuf.append(str);
			System.out.println("fileDir=" + fileDir + ", nodeStart=" + nodeStart + ",nodeLength=" + nodeLength + ", markerType=" + markerType + ",isAnnotated=" + isAnnotated);
		}
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(strBuf.toString());
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
