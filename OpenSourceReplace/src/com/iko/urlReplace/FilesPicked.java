package com.iko.urlReplace;

import java.io.File;

public class FilesPicked {

	public static void main(File[] multiFiles, String findStr, String replaceStr) throws Exception {
		String theFolder = multiFiles[0].getParent().concat("\\");
		File pushFolder = new File(theFolder.concat("push\\"));
		if (!pushFolder.exists()) {
			pushFolder.mkdir();
//			System.out.println("Making Dir");
		}
		int filesDone = 0;
		for (File thisFile : multiFiles) {
			filesDone++;
			int linksReplaced = ReplaceLinks.main(thisFile.getName(), theFolder, findStr, replaceStr, theFolder.concat("\\push"));
			if (linksReplaced > 0) {
//				System.out.println("success");
			}else {
//				System.out.println("Failure");
			}
			MainUI.remoteSetProgress(new MainUI.FileProgressInfo(filesDone, linksReplaced, thisFile.getName()));
//			publish(new MainUI.filesDonePair(filesDone, linksReplaced));
		}
	}
}
