package com.iko.urlReplace;
import java.awt.*;       // Using AWT layouts
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class MainUI extends JFrame {
	
	private JTextField searchStr;
	private JTextField replaceStr;
	private JLabel statusLabel;
	private JButton folderPick;
	private JButton multiPick;
	private JButton mapButton;
	private JButton unmapButton;
	private JButton startButton;
	private JButton stopButton;
	private static JProgressBar progressBar;
	private static JTextArea taskOutput;
	private TaskForThing taskWorker;
	private File pickedFolder;
	private File[] multiFiles;
	private JComboBox<String> siteDrop;
	private static int fileCount; 
	private int isFolder;

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
			//System.out.println("running tasks");
			taskOutput.setText(""); //clear the text box
			progressBar.setIndeterminate(false);
			statusLabel.setText("Updating URLs...");
			folderPick.setEnabled(false);
			multiPick.setEnabled(false);
			startButton.setEnabled(false);
			stopButton.setEnabled(true);

			if (isFolder == 0) {
				FolderPicked.main(pickedFolder.getPath(), searchStr.getText(), replaceStr.getText(), "");
			}else if (isFolder == 1){
				//System.out.println("trying individual files");
				fileCount = multiFiles.length;
				//System.out.println(fileCount);
				if (fileCount > 0) {
					FilesPicked.main(multiFiles, searchStr.getText(), replaceStr.getText());
				}else {
				//System.out.println("no file selected");
			}
				
			}else if (isFolder == 2) {
				MapSharepoint.map(siteDrop.getSelectedIndex());
				taskOutput.append("Mapping Site Sharepoint to B:\\ \n");
			    String property = "java.io.tmpdir";
			    String tempDir = System.getProperty(property);
			    tempDir += "push\\";
			    taskOutput.append("Using temporary directory " + tempDir + "\n");
			    FolderPicked.main("B:\\", searchStr.getText(), replaceStr.getText(), tempDir);
			    
			    statusLabel.setText("Copying File to Sharepoint...");
			    
			    File newFolder = new File(tempDir);
			    if (newFolder.exists()) {
			    	File[] fileList = newFolder.listFiles(new FilenameFilter() {
			    		@Override
							public boolean accept(File newFolder, String name) {
								return name.toLowerCase().endsWith(".pdf");
							}
			    	});
//			    	File pushFolder = new File("B:\\");
			    	int newFileCount = fileList.length;
			    	File destFile;
			    	for (int i = 0; i < newFileCount; i++) {
			    		destFile = new File("B:\\".concat(fileList[i].getName()));
			    		progressBar.setValue((i + 1)*100/newFileCount);
			    		taskOutput.append("File: " + fileList[i].getName() + "\n");
			    		Files.copy(fileList[i].toPath(), destFile.toPath(), REPLACE_EXISTING);
			    		Files.delete(fileList[i].toPath());
			    	}
			    }
			    
				MapSharepoint.unmap();
				taskOutput.append("unmapping Sharepoint from B:\\ \n");

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
			startButton.setEnabled(true);
			progressBar.setIndeterminate(true);
			stopButton.setEnabled(false);
			progressBar.setValue(0);
			statusLabel.setText("Waiting for User Input...");
			taskOutput.append("Done!\n");
		}
	}
	// Constructor to setup the GUI components and event handlers
	public MainUI() throws Exception {

		//=============Begin UI Code=======================
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JPanel cp = new JPanel();
		cp.setLayout(new GridBagLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel manOps = new JPanel(); //manual options, totally not sexist
		JPanel commonOps = new JPanel();
		JPanel autoOps = new JPanel();
		
		manOps.setLayout(new GridBagLayout());
		autoOps.setLayout(new GridBagLayout());
		commonOps.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();

		//some constants used for most / all elements
		c.fill = GridBagConstraints.HORIZONTAL; //fill all horizontal space
		c.insets = new Insets(5, 20, 5, 20);  //padding
		c.weightx = 0.5; //share the space

		JLabel newLabel;
		
		newLabel = new JLabel("Pick Site to Modify");
		c.gridx = 0;
		c.gridy = 0;
		commonOps.add(newLabel, c);
		
		String[] siteList = {
				"BA: Calgary",
				"CA: Kankakee",
				"GA: Wilmington",
				"GC: Sumas",
				"GE: Ashcroft",
				"GH: Hawkesbury",
				"GI: Madoc",
				"GV: Texas/SouthWest",
				"PBM: Senica/Sloviakia",
				"GK: IG Brampton",
				"GS: Sylacauga",
				"GR: BramCal",
				"Test Site",
		};
		
		siteDrop = new JComboBox<String>(siteList);
		siteDrop.setEditable(false);
		c.gridx = 1;
//		manOps.add(siteDrop, c);
		commonOps.add(siteDrop, c); 
		//some components will be the same so just add them again
		
		mapButton = new JButton("Map");
		c.gridx = 0;
		c.gridy++;
		manOps.add(mapButton,c);
		
		unmapButton = new JButton("Unmap");
		c.gridx = 1;
		manOps.add(unmapButton,c);

		newLabel = new JLabel("Enter String to find:");
		c.gridx = 0;
		c.gridy++;
//		manOps.add(newLabel, c);
		commonOps.add(newLabel, c);
		
		searchStr = new JTextField();
		searchStr.setEditable(true);
		c.gridx = 1;
//		c.gridy = 2;
//		manOps.add(searchStr, c);
		commonOps.add(searchStr, c);

		newLabel = new JLabel("Enter String to replace with:");
		c.gridx = 0;
		c.gridy++;
//		manOps.add(newLabel, c);
		commonOps.add(newLabel, c);

		replaceStr = new JTextField();
		replaceStr.setEditable(true);
		c.gridx = 1;
//		c.gridy = 3;
//		manOps.add(replaceStr, c);
		commonOps.add(replaceStr, c);

		folderPick = new JButton("Pick Folder");
		c.gridx = 0;
		c.gridy++;
		manOps.add(folderPick,c);

		multiPick = new JButton("Select Multiple Files");
		c.gridx = 1;
//		c.gridy = 4;
		manOps.add(multiPick,c);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
//		manOps.add(progressBar,c);
		commonOps.add(progressBar,c);
		
		statusLabel = new JLabel("Waiting for User Input...");
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		commonOps.add(statusLabel, c);

		taskOutput = new JTextArea();
		DefaultCaret caret = (DefaultCaret)taskOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		taskOutput.setEditable(false);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.ipady = 40;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,20,20,20);
//		manOps.add(new JScrollPane(taskOutput), c);
		commonOps.add(new JScrollPane(taskOutput), c);
		
		GridBagConstraints c2 = new GridBagConstraints();
		
		c2.fill = GridBagConstraints.BOTH; //fill all horizontal space
		
		startButton = new JButton("Start");
		c2.gridx = 0;
		c2.gridy = 0;
		c2.weightx = 0.5;
		c2.weighty = 1.0;
		c2.insets = new Insets(0, 0, 0, 0);
		autoOps.add(startButton, c2);
		
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		c2.gridx = 2;
		c2.gridy = 0;
		autoOps.add(stopButton, c2);
		
		tabbedPane.addTab("Automatic", null, autoOps, "Pick a Site, then Specific the Strings");
		tabbedPane.addTab("Manual", null, manOps, "Options for manually Updating PDFs");
		
		c2.insets = new Insets(5, 20, 5, 20);  //padding
		c2.weightx = 0.5;
		c2.weighty = 0.0;
		c2.gridx = 0;
		c2.gridy = 0;
		cp.add(tabbedPane, c2);
		
		c2.weighty = 1.0;
		c2.gridy = 1;
		c2.insets = new Insets(0, 0, 0, 0);
		cp.add(commonOps, c2);
		
		add(cp);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close-window button clicked
		setTitle("Hyperlink Updater"); // "super" JFrame sets title
		setSize(500, 600);         // "super" JFrame sets initial size
		setVisible(true);          // "super" JFrame shows
		//=============End UI Code=======================

		// Allocate an anonymous instance of an anonymous inner class that
		//  implements ActionListener as ActionEvent listener
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				isFolder = 2;
				taskWorker = new TaskForThing();
				taskWorker.execute();
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				folderPick.setEnabled(true);
				multiPick.setEnabled(true);
				startButton.setEnabled(true);
				progressBar.setIndeterminate(true);
				stopButton.setEnabled(false);
				taskWorker.cancel(true);
				taskWorker = null;
			}
		});
		
		mapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					MapSharepoint.map(siteDrop.getSelectedIndex());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		unmapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					MapSharepoint.unmap();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		folderPick.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFileChooser folderChooser = new JFileChooser();
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				Component frame = null;
				folderChooser.showOpenDialog(frame);
				pickedFolder = folderChooser.getSelectedFile();
				//System.out.println(pickedFolder.getName());
				isFolder = 0;
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
				isFolder = 1;
				taskWorker = new TaskForThing();
				taskWorker.execute();
			}
		});

	}

	// The entry main() method
	public static void main(String[] args) {
		// Run the GUI construction in the Event-Dispatching thread for thread-safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new MainUI();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Let the constructor do the job
			}
		});
	}

	public static void addText(String textInput) {
		taskOutput.append(textInput.concat("haha\n"));
		System.out.println("added: ".concat(textInput));
	}

	public static void remoteSetProgress(FileProgressInfo pairs) {
//			FileProgressInfo pair = pairs.get(pairs.size() - 1);
			progressBar.setValue(pairs.files*100/fileCount);
//			System.out.println(pairs.fileName);
			taskOutput.append(pairs.fileName);
			taskOutput.append(" ".concat(Long.toString(pairs.linksThisFile)));
			taskOutput.append(" Links Replaced.\n");
	}
	
	public static void setFileCount(int fileCountRemote) {
		fileCount = fileCountRemote;
	}

}