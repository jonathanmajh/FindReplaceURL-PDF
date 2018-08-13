package com.iko.urlReplace;
import java.awt.*;       // Using AWT layouts
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class MainUI extends JFrame {
	private JTextField searchStr;
	private JTextField replaceStr;
	private JButton folderPick;
	private JButton multiPick;
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private TaskForThing taskWorker;
	private File pickedFolder;
	private File[] multiFiles;
	private int fileCount; 
	private boolean isFolder;

	static class FileProgressInfo { //this data type is used for passing progress information
		private final int files, linksThisFile;
		private final String fileName;
		FileProgressInfo(int files, int linksThisFile, String fileName) {
			this.files = files;
			this.linksThisFile = linksThisFile;
			this.fileName = fileName;
		}
	}

	class TaskForThing extends SwingWorker<Void, FileProgressInfo>{
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() throws Exception {
			////System.out.println("running tasks");
			taskOutput.setText(""); //clear the text box
			progressBar.setIndeterminate(false);
			folderPick.setEnabled(false);
			multiPick.setEnabled(false);

			if (isFolder) {
				////System.out.println("going through folder");
				String folderPdf = pickedFolder.getPath();
				String findStr = searchStr.getText();
				if (!folderPdf.endsWith("\\")) {
					folderPdf = folderPdf.concat("\\");
				}
				////System.out.println(folderPdf);
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
						////System.out.println("Making Dir");
					}

					fileCount = fileList.length;
					for (int i = 0; i < fileCount; i++) {
						////System.out.println(fileList[i]);
						String fileName = fileList[i].getName(); 
						int linksReplaced = ReplaceLinks.main(fileName, folderPdf, findStr, replaceStr.getText());
						publish(new MainUI.FileProgressInfo(i, linksReplaced, fileName));
						if (linksReplaced > 0) {
							//System.out.println("File Changed");
						}else {
							//System.out.println("File Didn't Change, Delete");
							File deleteFile = new File(folderPdf.concat("push\\").concat(fileName));
							if (!deleteFile.delete()) {
								//System.out.println("Problem Deleting File");
							}
						}
					}
				}
			}else{
				//System.out.println("trying individual files");
				fileCount = multiFiles.length;
				//System.out.println(fileCount);
				if (fileCount > 0) {
					String theFolder = multiFiles[0].getParent().concat("\\");
					File pushFolder = new File(theFolder.concat("push\\"));
					int filesDone = 0;

					if (!pushFolder.exists()) {
						pushFolder.mkdir();
						//System.out.println("Making Dir");
					}
					for (File thisFile : multiFiles) {
						filesDone++;
						int linksReplaced = ReplaceLinks.main(thisFile.getName(), theFolder, searchStr.getText(), replaceStr.getText());
						if (linksReplaced > 0) {
							//System.out.println("success");
						}else {
							//System.out.println("Failure");
						}
						publish(new MainUI.FileProgressInfo(filesDone, linksReplaced, thisFile.getName()));
					}
				}else {
					//System.out.println("no file selected");
				}
			}
			//System.out.println("complete");
			return null;
		}
		protected void process(List<FileProgressInfo> pairs) {
			FileProgressInfo pair = pairs.get(pairs.size() - 1);
			progressBar.setValue(pair.files*100/fileCount);
			//System.out.println(pair.files*100/multiFiles.length);
			taskOutput.append(pair.fileName);
			taskOutput.append(" ".concat(Long.toString(pair.linksThisFile)));
			taskOutput.append(" Links Replaced.\n");
		}
		/*
		 * Executed in event dispatch thread
		 */
		public void done() {
			//Toolkit.getDefaultToolkit().beep();
			folderPick.setEnabled(true);
			multiPick.setEnabled(true);
			progressBar.setIndeterminate(true);
			progressBar.setValue(0);
			taskOutput.append("Done!\n");
		}
	}
	// Constructor to setup the GUI components and event handlers
	public MainUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//for dat default windows look
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//=============Begin UI Code=======================
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		//some constants used for most / all elements
		c.fill = GridBagConstraints.HORIZONTAL; //fill all horizontal space
		c.insets = new Insets(5, 20, 5, 20);  //padding
		c.weightx = 0.5; //share the space

		JLabel newLabel;

		newLabel = new JLabel("Enter String to find:");
		c.gridx = 0;
		c.gridy = 0;
		cp.add(newLabel, c);

		searchStr = new JTextField();
		searchStr.setEditable(true);
		c.gridx = 1;
		c.gridy = 0;
		cp.add(searchStr, c);

		newLabel = new JLabel("Enter String to replace with:");
		c.gridx = 0;
		c.gridy = 1;
		cp.add(newLabel, c);

		replaceStr = new JTextField();
		replaceStr.setEditable(true);
		c.gridx = 1;
		c.gridy = 1;
		cp.add(replaceStr, c);

		folderPick = new JButton("Pick Folder");
		c.gridx = 0;
		c.gridy = 2;
		cp.add(folderPick,c);

		multiPick = new JButton("Select Multiple Files");
		c.gridx = 1;
		c.gridy = 2;
		cp.add(multiPick,c);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		cp.add(progressBar,c);


		taskOutput = new JTextArea();
		taskOutput.setEditable(false);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.ipady = 40;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,20,20,20);
		cp.add(new JScrollPane(taskOutput),c);

		//=============End UI Code=======================

		// Allocate an anonymous instance of an anonymous inner class that
		//  implements ActionListener as ActionEvent listener
		folderPick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFileChooser folderChooser = new JFileChooser();
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				Component frame = null;
				folderChooser.showOpenDialog(frame);
				pickedFolder = folderChooser.getSelectedFile();
				//System.out.println(pickedFolder.getName());
				isFolder = true;
				taskWorker = new TaskForThing();
				taskWorker.execute();
			}
		});

		multiPick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFileChooser multiChooser = new JFileChooser();
				multiChooser.setMultiSelectionEnabled(true);
				Component frame = null;
				multiChooser.showOpenDialog(frame);
				multiFiles = multiChooser.getSelectedFiles();
				//for (File file : multiFiles) {
				//System.out.println(file.getParent()); //folder path
				//System.out.println(file.getName()); //file name
				//}
				isFolder = false;
				taskWorker = new TaskForThing();
				taskWorker.execute();
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close-window button clicked
		setTitle("Hyperlink Updater"); // "super" JFrame sets title
		setSize(500, 500);         // "super" JFrame sets initial size
		setVisible(true);          // "super" JFrame shows
	}

	// The entry main() method
	public static void main(String[] args) {
		// Run the GUI construction in the Event-Dispatching thread for thread-safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainUI(); // Let the constructor do the job
			}
		});
	}
}