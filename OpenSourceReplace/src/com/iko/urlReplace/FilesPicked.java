package com.iko.urlReplace;

import java.io.File;

public class FilesPicked {

	public static void main(File[] multiFiles, String findStr, String replaceStr) throws Exception {
		String theFolder = multiFiles[0].getParent().concat("\\");
		File pushFolder = new File(theFolder.concat("push\\"));
		int filesDone = 0;
		if (!pushFolder.exists()) {
			pushFolder.mkdir();
			System.out.println("Making Dir");
		}
		for (File thisFile : multiFiles) {
			filesDone++;
			int linksReplaced = ReplaceLinks.main(thisFile.getName(), theFolder, findStr, replaceStr);
			if (linksReplaced > 0) {
				System.out.println("success");
			}else {
				System.out.println("Failure");
			}
//			publish(new MainUI.filesDonePair(filesDone, linksReplaced));
		}
	}
}
