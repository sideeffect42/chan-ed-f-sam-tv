/**
 * 
 */
package ch.maniswebdesign.chaned.gui;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.maniswebdesign.chaned.channelmanager.ChannelList;
import ch.maniswebdesign.chaned.channelmanager.FileReader;
import ch.maniswebdesign.chaned.channelmanager.ListMerger;

/**
 * @author Takashi Yoshi
 *
 */
public abstract class ListMergeMethods extends JFrame {
	
	private static final long serialVersionUID = 1L;
	protected FileReader oldList;
	protected File fileToMerge;
	protected ChannelList mergedList;
	protected FileReader newReader;
	
	protected abstract void setTfFileToMerge(String text);
	protected abstract void setStep0Text(String text);
	protected abstract void setEnabledStep1(boolean value);
	protected abstract void setEnabledStep2(boolean value);
	protected abstract void setEnabledStep3(boolean value);
	protected abstract void setMergeStatus(String text);
	protected abstract void enableSaveButtons(boolean value);
	protected abstract void closeWindow();
	
	protected void fillInBaseData() {
		this.oldList = MainMethods.openFile;
		File oldFile = (this.oldList != null ? this.oldList.getFile() : null);
		
		if(oldList == null || oldFile == null || !oldFile.exists()) setStep0Text("Open a file first!");
		else { 
			String oldPath = oldFile.getAbsolutePath();
			setStep0Text(oldPath);
			if(oldPath != null && oldPath.length() > 0)
				setEnabledStep1(true);
		}
	}
	
	protected void chooseFileToMerge() {
		File toMerge = getFileToMergeWith();
		
		// Get new list
		setMergeStatus("Reading file...");
		newReader = new FileReader();
		newReader.openFile(toMerge);
		
		if (newReader.isScmFile()) {
			setMergeStatus("File is an SCM bundle. Processing...");
			File[] channelFiles = newReader.getScmContents();
			
			File channelFile = (File) JOptionPane.showInputDialog(
							null,
							"Choose the channel file to merge with:",
							"Channel File",
							JOptionPane.QUESTION_MESSAGE,
							null,
							channelFiles,
							channelFiles[0].getName());
			
			if (channelFile != null && channelFile.exists()) {
				setMergeStatus("Processing file...");
				newReader.selectFileFromScm(channelFile.getName());
			} else {
				setFileToMerge(null);
				setMergeStatus("Invalid file.");
				JOptionPane.showMessageDialog(getParent(), "You must select a channel file!", "Channel File", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			setMergeStatus("Processing file...");
			newReader.parseFile();
		}
		
		setFileToMerge(this.newReader.getFile());
		setMergeStatus("");
	}
	
	protected void setFileToMerge(File file) {
		this.fileToMerge = file;
		String filePath = (file == null ? "" : file.getAbsolutePath());
		setTfFileToMerge(filePath);
		if (filePath != null && filePath.length() > 0) {
			setEnabledStep2(true);
			setEnabledStep3(true);
		}
	}
	
	protected static File getFileToMergeWith() {
		String userHome = System.getProperty("user.home") + File.separator;
		File selectedFile;

		try {
			selectedFile = ListMergeMethods.showFileDialog("Choose File to Merge with", "Choose", userHome);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		
		if (selectedFile != null && selectedFile.exists()) {
			return selectedFile;
		}
		return null;
	}
	
	private static File showFileDialog(String message, String defaultButtonCaption, String baseDir) throws URISyntaxException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(baseDir));
		fileChooser.setDialogTitle(message);

		// Show Open Dialog
		int returnVal = fileChooser.showDialog(null, defaultButtonCaption);

		// Return chosen file
		File chosenFile = null;;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			chosenFile = fileChooser.getSelectedFile();
		}
		return chosenFile;
	}
	
	protected void mergeLists(boolean updateProperties, boolean removeMissing, boolean appendAdded) {
		ChannelList oldList = MainMethods.openFile.getChannelList();
		ChannelList newList = (this.newReader == null ? null : this.newReader.getChannelList());
		if (oldList == null || newList == null) { setMergeStatus("Error: Either old or new list is invalid."); return; }
		
		// Start merging
		ListMerger merger = new ListMerger(oldList, newList);
		
		setMergeStatus("Merging lists...");
		merger.mergeLists(updateProperties, removeMissing, appendAdded);
		
		setMergeStatus("Merged lists...");
		this.mergedList = merger.getMergedList();
		
		setMergeStatus("Finished merging.");
		enableSaveButtons(true);
	}
	
	protected void saveList() {
		if (this.mergedList == null) return;
		oldList.replaceSelectedFile(mergedList);
		MainMethods.updateChannelTable(mergedList);
		closeWindow();
	}
	
	protected void saveToNew() {
		if (this.mergedList == null) return;
		
	}
}
