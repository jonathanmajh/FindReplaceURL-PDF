package com.jonathanmajh.urlReplace;

import java.io.FileOutputStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;

public class ReplaceLinks {
	public static int main(String fileNamePDF, String filePathFolder, String findStr, String replaceStr, String outputFolder) throws Exception { 
		//this essentially declares this part of the code as executable, and to not exit if errors are made
//		System.out.println(fileNamePDF);
//		System.out.println(filePathFolder);
//		System.out.println(findStr);
//		System.out.println(replaceStr);
			
		String base = filePathFolder.concat(fileNamePDF);
		String dest = outputFolder.concat(fileNamePDF);
//		System.out.println(base);
//		System.out.println(dest);
		int replaceNum = 0;
		String newURL = "";
	    PdfReader 	reader	=	new PdfReader(base); 
	    PdfStamper	stamper	=	new PdfStamper(reader, new FileOutputStream(dest));
	    

	    int n = reader.getNumberOfPages(); 
//	    System.out.println("Starting page loop");
	    for (int i=1; i <= n; i++) {
	    	
	        if (Thread.interrupted()) {
	            // Oh Shit! We've been interrupted...GTFO
	            return -1;
	        }
	
	    	PdfDictionary pageDic = reader.getPageN(i); 
	    	PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);   
			if (annots == null) {
				continue;
			}
			for (PdfObject annot : annots.getElements()) {
				Boolean foundLink = false;
	 			PdfDictionary annotDict = (PdfDictionary)PdfReader.getPdfObject(annot); 
	 			
		        if (Thread.interrupted()) {
		            // Oh Shit! We've been interrupted...GTFO
		            return -1;
		        }
	 			
	 			if (annotDict.get(PdfName.SUBTYPE).equals(PdfName.LINK)) {
//	 				System.out.println("a link"); 
	// 				System.out.println(annotDict.get(PdfName.SUBTYPE));
	 				foundLink = true;
	 			}
	 			
	 			if (annotDict.get(PdfName.SUBTYPE).equals(PdfName.WIDGET)) {
//	 				System.out.println("a widget"); 
	 				foundLink = true;
	 			}
	 			
	 			if (annotDict.get(PdfName.A) == null) {
//	 				System.out.println("nullthing");
	 				continue;
	 			}
	 			if (foundLink) {
	 				PdfDictionary annotAction = (PdfDictionary)PdfReader.getPdfObject(annotDict.get(PdfName.A));
	 				if (annotAction.get(PdfName.S).equals(PdfName.URI)) {
	 					
	 	 				String oldURL = annotAction.getAsString(PdfName.URI).toString();
//	 	 				System.out.println(oldURL);
	 	 				newURL = oldURL.replace(findStr, replaceStr);
	 	 				
	 	 				if (newURL != oldURL) {
	 	 					annotAction.put(PdfName.URI, new PdfString(newURL));
	 	 					replaceNum++;
	 	 				}
	// 	 				"?event=loadapp", "?mobile=false&event=loadapp"
	 	 				
	 				}
	 			}
	 		}
	    }
	    stamper.close();
	    reader.close(); 
	    System.out.println("The Pdf is Created..");
	    return replaceNum;
	}
}
	


 
