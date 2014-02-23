package edu.uncc.aside.phpcodeannotate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import edu.uncc.aside.phpcodeannotate.models.AnnotationRecord;
import edu.uncc.aside.phpcodeannotate.models.MarkerRecord;
import edu.uncc.aside.phpcodeannotate.models.NodePositionInfo;
import edu.uncc.aside.phpcodeannotate.util.Utils;

public class PreRunPluginConfig {

	public static void config() {
		// check whether it is the first time
		// prepareConfigFile();
		// if(Plugin.FIRST_TIME_RUN == false){
		// if this is not the first time running it, we need to read
		// the markers and annotation recordsin the file
	//	Plugin.allMarkerRecords = readMarkerRecordFile();
		// Plugin.allAnnotationRecords = readAnnotationRecordFile();
		// }
	}

	public static void prepareConfigFile() {
		IPath stateLocation = Plugin.getDefault().getStateLocation();
		String fileName = stateLocation + "/" + Plugin.getConfigFile(); // might
																		// have
																		// to be
																		// updated
																		// about
																		// "/"
		File configFile = new File(fileName);
		if (configFile.exists()) {
			try {
				FileReader fr = new FileReader(configFile);
				BufferedReader br = new BufferedReader(fr);
				String str = br.readLine();
				if (str.equals("alreadyRun"))
					Plugin.FIRST_TIME_RUN = false;
				br.close();
				fr.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Plugin.FIRST_TIME_RUN = true;
			boolean created = false;
			String str = "alreadyRun";
			try {
				created = configFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (created == true) {
				FileWriter fw = null;
				try {
					fw = new FileWriter(configFile);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(str);
					bw.close();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.err.println("Config file is not created properly!");
			}
		}
	}

	public static HashSet<MarkerRecord> readMarkerRecordFile(IProject project) {
		// /read file and create the set of MarkerRecord, as well as initilize
		// all the marker with the corresponding resources;
		String basePath = Utils.getPlugingBasePath();
		
		String markerRecordFileName = basePath + Plugin.MARKER_RECORD_FILE;

		int NumOfItems = Plugin.MARKER_FILE_NUM_OF_ITEMS;
		String regex = Plugin.COMMA;
		HashSet<MarkerRecord> allMarkerRecords = new HashSet<MarkerRecord>();

		File markerRecordFile = new File(markerRecordFileName);
		FileReader fr = null;
		try {
			fr = new FileReader(markerRecordFile);
			BufferedReader br = new BufferedReader(fr);
			String str = null;
System.out.println("markers:");
			while ((str = br.readLine()) != null) {
				//System.out.println("str = " + str);
				String[] strs = str.split(regex);
				if (strs.length != NumOfItems && strs.length != NumOfItems - 1) {
					System.out.println("strs.length = " + strs.length);
					System.err
							.println("ERROR: strs length not correct when reading the Markers file");
					return null;
				}
				String fileDir = strs[1];
				int nodeStart = Integer.valueOf(strs[2]);
				int nodeLength = Integer.valueOf(strs[3]);
				String markerType = strs[4];
				boolean isAnnotated = Boolean.valueOf(strs[5]); // indicate
																// which marker
																// to use, red
																// or yellow, if
																// true, use
																// yellow
				NodePositionInfo nodePositionInfo = new NodePositionInfo(
						fileDir, nodeStart, nodeLength);
				MarkerRecord markerRecord = new MarkerRecord(nodePositionInfo,
						markerType, isAnnotated);
				// if there is already annotations in the annotation record
				// file, then we need to read and insert to annotation set of
				// the marker
				if (strs.length == NumOfItems) {// has annotation information
					// to be added~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				} else {// no annotations made yet, do nothing here.

				}
				if (!allMarkerRecords.contains(markerRecord)) {
					if (!Utils.isOnFilterList(project, markerRecord
							.getNodePositionInfo().getFileDir(), markerRecord
							.getNodePositionInfo().getStartPosition(),
							markerRecord.getNodePositionInfo().getLength())){ // newly
																				// added
																				// to
																				// filter
																				// out
																				// some
																				// hugely
																				// repeated
																				// functions

						allMarkerRecords.add(markerRecord);
					System.out.println(markerRecord.getNodePositionInfo().getFileDir());
					}
				}
			}
			System.out.println("in pre run config, marker records size = "
					+ allMarkerRecords.size());

			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allMarkerRecords;
	}

	private static HashSet<AnnotationRecord> readAnnotationRecordFile() {
		String annotationRecordFileName = Plugin.STATE_LOCATION + "/"
				+ Plugin.ANNOTATION_RECORD_FILE;
		int NumOfItems = Plugin.ANNOTATON_FILE_NUM_OF_ITEMS;
		String regex = Plugin.COMMA;
		HashSet<AnnotationRecord> allAnnotationRecords = new HashSet<AnnotationRecord>();

		File annotationRecordFile = new File(annotationRecordFileName);
		FileReader fr = null;
		try {
			fr = new FileReader(annotationRecordFile);
			BufferedReader br = new BufferedReader(fr);
			String str = null;

			while ((str = br.readLine()) != null) {
				String[] strs = str.split(regex);
				if (strs.length != NumOfItems) {
					System.err
							.println("ERROR: strs length not correct when reading the Markers file");
					return null;
				}
				String fileDir = strs[1];
				int nodeStart = Integer.valueOf(strs[2]);
				int nodeLength = Integer.valueOf(strs[3]);
				String markerType = strs[4];

				NodePositionInfo nodePositionInfo = new NodePositionInfo(
						fileDir, nodeStart, nodeLength);
				AnnotationRecord annotationRecord = new AnnotationRecord(
						nodePositionInfo, markerType, null);
				allAnnotationRecords.add(annotationRecord);
			}
			System.out.println("in pre run config, annotation records size = "
					+ allAnnotationRecords.size());

			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allAnnotationRecords;
	}

}
