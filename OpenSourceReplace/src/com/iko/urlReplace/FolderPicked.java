package com.iko.urlReplace;

import java.io.File;
import java.io.FilenameFilter;


public class FolderPicked {
	public static void main(String folderPdf, String findStr, String replaceStr) throws Exception {
		if (!folderPdf.endsWith("\\")) {
			folderPdf = folderPdf.concat("\\");
		}
		System.out.println(folderPdf);
		File workingFolder = new File(folderPdf);
		if (workingFolder.exists()){
			File[] fileList = workingFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File workingFolder, String name) {
					return name.toLowerCase().endsWith(".pdf");
				}
			});
			File pushFolder = new File(folderPdf.concat("push\\"));
			if (!pushFolder.exists()) {
				pushFolder.mkdir();
				System.out.println("Making Dir");
			}
			int fileCount = fileList.length;
			for (int i = 0; i < fileCount; i++) {
				System.out.println(fileList[i]);
				String fileName = fileList[i].getName(); 
				int linksReplaced = ReplaceLinks.main(fileName, folderPdf, findStr, replaceStr);
				if (linksReplaced > 0) {
					System.out.println("File Changed");
				}else {
					System.out.println("File Didn't Change, Delete");
					File deleteFile = new File(folderPdf.concat("push\\").concat(fileName));
					if (!deleteFile.delete()) {
						System.out.println("Problem Deleting File");
					}
				}
			}
		}
		
		
//		return fileCount;
	}
}
