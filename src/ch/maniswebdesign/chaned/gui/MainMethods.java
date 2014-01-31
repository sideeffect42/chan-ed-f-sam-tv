package ch.maniswebdesign.chaned.gui;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ch.maniswebdesign.chaned.channelmanager.Channel;
import ch.maniswebdesign.chaned.channelmanager.ChannelList;
import ch.maniswebdesign.chaned.channelmanager.FileReader;
import ch.maniswebdesign.chaned.channelmanager.MapParser;
import ch.maniswebdesign.chaned.channelmanager.ZipHandler;
/**
 * 
 */

/**
 * @author Takashi Yoshi
 *
 */
public class MainMethods extends Main {
	
	public static FileReader openFile;
	
	public static void fileNew() {
		MainMethods.openFile = null;
		
		// Clear table
		JTable channelTable = Main.table;
		DefaultTableModel tableModel = (DefaultTableModel) channelTable.getModel();
		
		channelTable.clearSelection();
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();
		
		// Clear JTree
		Main.contentTree.setModel(null);
	}
	
	public static void fileOpen() {
		final FileReader fr = new FileReader();
		fr.openFile();
		
		if (fr.getFile() == null && !fr.isScmFile()) return;
		
		MainMethods.updateTreeView(fr);
		
		if (fr.getFile() != null) {
			Thread fileParser = new Thread(new Runnable() {
				public void run() {
					ChannelList channelList = fr.parseFile();
					if(channelList != null) {
						MainMethods.updateChannelTable(channelList);
					}else
						System.out.println("Chlist is null");
				}
				
			});
			fileParser.start();
		}
			
		MainMethods.openFile = fr;
	}
	
	public static void fileSave() {
		if (MainMethods.openFile == null) { updateStatusBar("Nothing to save ... please open a file first!", 2); return; }
		
		if (openFile.isScmFile()) {
			fileSaveAsSCM(openFile.getScmFile());
		} else {
			fileSaveAsFile(openFile.getFile());
		}
	}
	
	public static void fileSaveAsAuto() {
		if (MainMethods.openFile == null) { updateStatusBar("Nothing to save ... please open a file first!", 2); return; }
		
		if (openFile.isScmFile()) {
			fileSaveAsSCM();
		} else {
			fileSaveAsFile();
		}
	}
	
	public static void fileSaveAsSCM(File target) {
		if (MainMethods.openFile == null) { updateStatusBar("Nothing to save ... please open a file first!", 2); return; }
		if (!MainMethods.openFile.isScmFile()) { updateStatusBar("You must open a SCM File to save as an SCM file", 2); return; }
		
		File outputFile = target;
		if(outputFile == null) 
			outputFile = MainMethods.getFileToSaveIn();
			//outputFile = new File(System.getProperty("user.home")+File.separator+"export.scm");
		
		MapParser.write(MainMethods.openFile.getChannelList(), MainMethods.openFile.getFile());
		
		int compressedFiles = ZipHandler.compress(outputFile.getAbsolutePath(), MainMethods.openFile.scmExtractedTo().getAbsolutePath());
		
		if (compressedFiles < 0) { MainMethods.updateStatusBar("Packaging SCM file failed!", 2); return; }
		
		updateStatusBar("Saved SCM file as \""+outputFile.getAbsolutePath()+"\"", 2);
	}
	public static void fileSaveAsSCM() {
		fileSaveAsSCM(null);
	}
	
	public static void fileSaveAsFile(File target) {
		if ((MainMethods.openFile == null)) { updateStatusBar("Nothing to save ... please open a file first!", 2); return; }
		
		File outputFile = target;
		if(outputFile == null)
			outputFile = MainMethods.getFileToSaveIn();
			//outputFile = new File(System.getProperty("user.home") + File.separator + MainMethods.openFile.getFile().getName()+"_export");
		
		MapParser.write(MainMethods.openFile.getChannelList(), outputFile);
		updateStatusBar("Saved channel file as \""+outputFile.getAbsolutePath()+"\"", 2);
	}
	public static void fileSaveAsFile() {
		fileSaveAsFile(null);
	}
	
	private static File getFileToSaveIn() {
		String userHome = System.getProperty("user.home") + File.separator;
		File selectedFile;

		try {
			selectedFile = MainMethods.showFileDialog("Save File", "Save", userHome);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		
		if (selectedFile != null && !selectedFile.exists()) {
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

	
	public static void fileExit() {
		MainMethods.updateStatusBar("Bye.");
		System.exit(0);
	}
	
	public static void listReparse() {
		MainMethods.openFile.parseFile();
		MainMethods.updateChannelTable(openFile.getChannelList());
	}
	
	public static void listMerge() {
		ListMerge merger = new ListMerge();
		merger.setVisible(true);
	}
	
	public static void updateTreeView(FileReader fr) {
		File file;
		boolean isScmFile;
		final File[] parentContents;
		
		synchronized (fr) {
			isScmFile = fr.isScmFile();
			file = (isScmFile ? fr.getScmFile() : fr.getFile());
			
			if (isScmFile) {
				// Show contents of the directory the SCM was extracted to
				parentContents = fr.getScmContents();
			} else {
				// Show contents of the directory the channel file is in
				parentContents = file.getParentFile().listFiles();
			}
		}
		
		Main.contentTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode( (isScmFile ? file.getName() : file.getParentFile().getName()) ) {
				private static final long serialVersionUID = 1L;
				{
					for (int i = 0; i < parentContents.length; i++) {
						add(new DefaultMutableTreeNode(parentContents[i].getName()));
					}
				}
			}
		));
	}
	
	public static void updateChannelTable(ChannelList channelList) {
		updateChannelTable(channelList, true);
	}
	
	public static void updateChannelTable(ChannelList channelList, boolean printEmptyRows) {
		MainMethods.updateStatusBar("Updating channel table...");
		
		JTable channelTable = Main.table;
		DefaultTableModel tableModel = (DefaultTableModel) channelTable.getModel();
		
		channelTable.clearSelection();
		tableModel.getDataVector().removeAllElements();
		tableModel.fireTableDataChanged();
		
		if(channelList == null) return;
		
		for(int i = 0; i < channelList.size(); i++) {
			Channel c = channelList.get(i);
			if (c != null) {
				tableModel.addRow(new Object[] {
					c.getProperty("num"), 
					c.getProperty("name")
				});
			} else if (printEmptyRows) {
				tableModel.addRow(new Object[] {
					i,
					""
				});
			}
		}
		
		MainMethods.resetStatusBar();
	}
	
	public static void selectNewFile() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) MainMethods.contentTree.getLastSelectedPathComponent();
		
		if(node != null && node.isLeaf()) {
			Object[] path = node.getUserObjectPath();
			final String fileName = path[path.length - 1].toString();
			
			Thread selector = new Thread(new Runnable() {
				public void run() {
					if (MainMethods.openFile.isScmFile()) {
						MainMethods.openFile.selectFileFromScm(fileName);
						MainMethods.updateChannelTable(openFile.getChannelList());
					} else {
						String baseFolder = MainMethods.openFile.getFile().getParentFile().getAbsolutePath();
						if (!baseFolder.endsWith(File.separator)) baseFolder += File.separator;
						
						FileReader fr = new FileReader();
						fr.openFile(new File(baseFolder + fileName));
						
						MainMethods.updateTreeView(fr);
						MainMethods.openFile = fr;
						fr.parseFile();
						MainMethods.updateChannelTable(fr.getChannelList());
						
					}
				}
			});
			selector.start();
		}
	}
	
	public static void getChannelInfo() {
		int selectedRow = table.getSelectedRow();
		int channelNum = (Integer) table.getModel().getValueAt(selectedRow, 0);
		
		new GetInfo(MainMethods.openFile.getChannelList().get(channelNum));
		
	}
	
	public static void moveChannel() {
		int[] selectedRows = table.getSelectedRows();
		
		String respose = JOptionPane.showInputDialog(frmChannelEditor, "To which position should we move?");
		int destinationNumber = -1;
		try { destinationNumber = new Integer(respose).intValue(); } catch(Exception e) {}
		
		if(destinationNumber >= 0) {
			ChannelList channelList = MainMethods.openFile.getChannelList();
			for (int i = 0; i < selectedRows.length; i++) {
				int channelNum = (Integer) table.getModel().getValueAt(selectedRows[i], 0);
				int numDifference = (destinationNumber + i) - channelNum;
				int moveTo = channelNum + numDifference;
				
				if (numDifference == 0) { continue;
				}else{
					if (channelList.get(moveTo) == null) {
						channelList.put(moveTo, channelList.get(channelNum));
						channelList.remove(channelNum);
					} else {
						// Channel to move to not null. Move around other channels.
						int startIndex = -1;
						int endIndex = -1;
						if (numDifference < 0) {
							startIndex = moveTo;
							endIndex = channelNum;
						} else if (numDifference > 0) {
							startIndex = moveTo;
							endIndex = channelList.MAX_CHANNELS;
						}
						if (startIndex < 0 || endIndex < 0) return;
						
						Channel channelToMove = channelList.get(channelNum);
						Channel temp1 = channelList.get(startIndex);
						Channel temp2 = channelList.get(startIndex + 1);
						
						for(int c = startIndex+1; (temp1 != null && c <= endIndex); c++) {
							channelList.put(c, temp1);
							temp1 = temp2;
							temp2 = channelList.get(c + 1);
						}
						
						// Finally move the right channel
						channelList.put(moveTo, channelToMove);
						if (numDifference > 0) channelList.remove(channelNum);
					}
				}
			}
			
			// Update Channel Table
			updateChannelTable(openFile.getChannelList());
			
		} else {
			JOptionPane.showMessageDialog(frmChannelEditor, "I don't understand your input. Try filling in an integer please.", "What?!", JOptionPane.ERROR_MESSAGE, null);
		}
	}
	
	public static void removeChannel() {
		int[] selectedRows = table.getSelectedRows();
		
		String[] buttons = {"Remove", "Don't Remove"};
		int okButton = 1;
		
		String question = "Are you sure you want to remove this channel?";
		if (selectedRows.length > 1) question = "Are you sure you want to remove these channels?";
		
		int answer = JOptionPane.showOptionDialog(frmChannelEditor, question, "Remove Channel(s)", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
		
		if (answer == okButton) {
			ChannelList channelList = MainMethods.openFile.getChannelList();
			
			for (int i = 0; i < selectedRows.length; i++) {
				channelList.remove((Integer) table.getModel().getValueAt(selectedRows[i], 0));
			}
			
			// Update Channel Table
			updateChannelTable(openFile.getChannelList());
			
		}
	}
	
	public static void updateStatusBar(final String message, final int timeoutSecs) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.out.println(message);
				Main.statusBar.setText(message);
				
				if(timeoutSecs > 0) {
					new Thread(new Runnable() {
						public void run() {
							String previousMessage = Main.statusBar.getText();
							try {
								Thread.sleep(timeoutSecs * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(previousMessage.equals(Main.statusBar.getText()))
								resetStatusBar();
						}
					}).start();
				}
			}
		});
	}
	public static void updateStatusBar(final String message) {
		updateStatusBar(message, 0);
	}
	
	public static void resetStatusBar() {
		updateStatusBar("Ready.");
	}
	public static void updateStatusProgress(final int percent) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main.progressBar.setValue(percent);
			}
		});
	}
	public static void resetStatusProgress() {
		updateStatusProgress(0);
	}
}
