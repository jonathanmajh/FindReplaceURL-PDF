package com.jonathanmajh.urlReplace;

import java.io.*;
import java.net.URI;
import java.net.URL;


public class MapSharepoint {
	
	static String[][] siteURL ={
			{"BA",	"http://operations.connect.na.local/support/Reliability/IKOCalgary/CalgaryAssetDocuments"},
			{"CA",  "http://operations.connect.na.local/support/Reliability/IKOKankakee/KankakeeAssetDocuments"},
			{"GA",  "http://operations.connect.na.local/support/Reliability/IKOWilmington/PlantAssetDocs"},
			{"GC",  "http://operations.connect.na.local/support/Reliability/IKOSumas/Sumas Asset Documents"},
			{"GE",  "http://operations.connect.na.local/support/Reliability/IKOAshcroft/PlantAssetDocs"},
			{"GH",  "http://operations.connect.na.local/support/Reliability/IKOHawkesbury/HawkesburyAssetDocuments"},
			{"GI",  "http://operations.connect.na.local/support/Reliability/IKOMadoc/PlantAssetDocs"},
			{"GV",  "http://operations.connect.na.local/support/Reliability/IKOSouthwest/PlantAssetDocs"},
			{"PBM", "http://operations.connect.na.local/support/Reliability/IKOSlovakia/PlantAssetDocs"},
			{"GK",  "http://operations.connect.na.local/support/Reliability/IGBrampton/IGBramptonAssetDocuments"},
			{"GS",  "http://operations.connect.na.local/support/Reliability/IKOSylacauga/SylacaugaAssetDocuments"},
			{"GR",  "http://operations.connect.na.local/support/Reliability/Bramcal/Plant Asset Documents"},
			{"Test","http://operations.connect.na.local/support/Reliability/ReliabilityShared/Pages/TestFiles"}
			//this array needs to have the same order of sites as the drop down.
			//which lets us avoid a for loop
	};
	
	public static URI getSharpointURL(int siteIndex) throws Exception{
		URL sharepointURL = new URL(siteURL[siteIndex][1]);
		return sharepointURL.toURI();
		
	}
	
	public static void map(int siteIndex) throws Exception {
//		siteID = "GH";
		String command = "";

		command = "net use B: \"".concat(siteURL[siteIndex][1].concat("\""));

		ProcessBuilder pb=new ProcessBuilder("cmd.exe", "/c", command);
		pb.redirectErrorStream(true);
		Process process=pb.start();
		BufferedReader inStreamReader = new BufferedReader(
		    new InputStreamReader(process.getInputStream())); 
		String cmdOut;
		while((cmdOut = inStreamReader.readLine()) != null){
//		    System.out.println(cmdOut);
			if (cmdOut.length() > 0) {
				MainUI.addText(cmdOut);
			}
		}
	}

	public static void unmap() throws Exception {
		String command = "net use B: /delete";
		ProcessBuilder pb=new ProcessBuilder("cmd.exe", "/c", command);
		pb.redirectErrorStream(true);
		Process process=pb.start();
		BufferedReader inStreamReader = new BufferedReader(
		    new InputStreamReader(process.getInputStream())); 
		String cmdOut;
		while((cmdOut = inStreamReader.readLine()) != null){
//		    System.out.println(cmdOut);
//			System.out.println(cmdOut.length());
			if (cmdOut.length() > 0) {
				MainUI.addText(cmdOut);
//				MainUI.remoteSetProgress(50);
			}
		}
	}
}
