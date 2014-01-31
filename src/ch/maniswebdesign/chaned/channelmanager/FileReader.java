package ch.maniswebdesign.chaned.channelmanager;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileReader {

	private ChannelList channelList;
	private File file;

	private File scmExtracedTo;
	private File scmFile;
	private boolean isScmFile = false;
	private File[] filesInScm;
	
	private byte mapType;
	
	public FileReader() {
		file = null;
		channelList = null;
	}

	/**
	 * File Dialog to select a file for reading
	 * 
	 * @param message
	 *            displayed for selection
	 * @param filter
	 *            file selection filters
	 * @param baseDir
	 *            directory to look into
	 * @return String - the filename selected or <dd>null - if unsuccessful</dd>
	 * @throws URISyntaxException
	 */
	private File showFileDialog(String message, String defaultButtonCaption,
			String baseDir) throws URISyntaxException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(baseDir));
		/*fileChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "SCM Files";
			}

			@Override
			public boolean accept(File f) {
				return (f.getName().endsWith(".scm") ? true : false);
			}
		});*/
		fileChooser.setDialogTitle(message);

		// Show Open Dialog
		int returnVal = fileChooser.showDialog(null, defaultButtonCaption);

		File chosenFile;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			chosenFile = fileChooser.getSelectedFile();
		} else {
			return null;
		}

		return chosenFile;
	}

	public boolean isScmFile() {
		return this.isScmFile;
	}
	
	public File scmExtractedTo() {
		return this.scmExtracedTo;
	}
	
	public void openFile() {
		openFile(null);
	}
	
	public void openFile(File fileToOpen) {
		if (this.file != null) return;

		String userHome = System.getProperty("user.home") + File.separator;
		File selectedFile;

		if (fileToOpen == null) {
			try {
				selectedFile = showFileDialog("Open File", "Open", userHome);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return;
			}
		} else {
			selectedFile = fileToOpen;
		}
		
		if (selectedFile != null && selectedFile.exists()) {
			if (selectedFile.getName().endsWith(".scm")) {
				this.scmFile = selectedFile;
				this.isScmFile = true;
				
				// Generate folder to extract
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddkkmmss");
				String dateString = formatter.format(new Date());

				String baseTempDir = userHome + (!userHome.endsWith(File.separator) ? File.separator : "") + "ChanEditTemp" + File.separator;
				ensureDirectoryExists(baseTempDir, true);
				String extractedFolder = baseTempDir + dateString + File.separator;

				// Extract SCM File
				int numExtractedFiles = extractScmFile(selectedFile, extractedFolder);

				this.file = null;
				if (numExtractedFiles > 0) this.scmExtracedTo= new File(extractedFolder);
			} else {
				this.file = selectedFile;
			}
		} else {
			System.out.println("Open: No File selected!");
			return;
		}
	}

	/**
	 * extracts an SCM file into a tempDir that it selects and creates itself
	 * 
	 * @param path
	 *            String variable that contains the path to the SCM file
	 * @return number of files extracted or <dd>-1 if SCM file format error</dd>
	 */
	public int extractScmFile(File scmFile, String destination) {
		if (!destination.endsWith(File.separator))
			destination.concat(File.separator);

		ensureDirectoryExists(destination, true);

		if (ZipHandler.decompress(scmFile, destination) <= 0) {
			System.out.println("File empty or not in SCM Format!");
			return -1;
		}

		// extract TV Model from file SCM filename, e.g.:
		// channel_list_UE40D8000_1101.scm
		char scmVersion = MapParser.getFileVersion(scmFile);
		System.out.println("Guess TV Version is: " + scmVersion);
		
		File d = new File(destination);
		this.scmExtracedTo = d;
		File extractedFiles[] = d.listFiles();
		this.filesInScm = extractedFiles;
		
		// assure that we do not leave the files lying around
		int i = 0;
		for (; i < extractedFiles.length; i++)
			extractedFiles[i].deleteOnExit();
		File destinationFile = new File(destination);
		destinationFile.deleteOnExit();
		
		File cloneFile = new File(destinationFile.getAbsoluteFile() + (!destinationFile.getAbsolutePath().endsWith(File.separator) ? File.separator : "") + "CloneInfo");
		
		char version = MapParser.getFileVersion(scmFile);
		
		if (version != Character.MIN_VALUE) {
			System.out.println("Main: Read TV Version: " + version
					+ " from file " + cloneFile);
			scmVersion = version;
		} else {
			System.out.println("Main: No TV Version info found in file "
					+ cloneFile);
		}
		
		return i;
	}

	public void ensureDirectoryExists(String directoryPath, boolean temp) {
		File destinationFile = new File(directoryPath);
		String filename = destinationFile.getPath();

		int number = 0;
		while (destinationFile.isFile()) {
			// try to Find a valid tempDir name
			destinationFile = new File(filename + number++);
		}

		if (destinationFile.isDirectory()) {
			// if the directory does exist, then empty it
			File fileList[] = destinationFile.listFiles();
			for (int i = 0; i < fileList.length; i++)
				fileList[i].delete();
		} else {
			// Create directory
			destinationFile.mkdir();
			if (!destinationFile.isDirectory()) {
				// if the directory does NOT exist, ERROR
				System.out.println("Cannot create temporary Directory: "
						+ destinationFile.getPath());
				return;
			}

			if (temp)
				destinationFile.deleteOnExit(); // only delete the directory on exist, if we have created it
		}
	}

	public ChannelList getChannelList() {
		return this.channelList;
	}
	
	public ChannelList parseFile() {
		if(this.file == null) return null;
		
		this.mapType = detectMapType();
		ChannelList createdList = null;
		
		if (this.mapType == Channel.TYPE_AIR || this.mapType == Channel.TYPE_CABLE) {
			createdList = MapParser.parseAirCable(this.file);
		} else if (this.mapType == Channel.TYPE_SAT) {
			createdList = MapParser.parseSat(this.file);
		} else if (this.mapType == Channel.TYPE_CLONE) {
			/* clone.bin */
			createdList = MapParser.parseClone(this.file);
		} else {
			System.out.println("Function not implemented for files with name \""+ this.file.getAbsolutePath()+"\" and type "+this.mapType);
		}
		
		this.channelList = createdList;
		return createdList;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public File[] getScmContents() {
		return this.filesInScm;
	}
	
	public void selectFileFromScm(String name) throws NullPointerException {
		if (!this.isScmFile()) return;
		
		File[] availableFiles = getScmContents();
		
		for (int i = 0; i < availableFiles.length; i++) {
			if (availableFiles[i].getName().equals(name)) {
				this.file = availableFiles[i];
				
				this.parseFile();
				
				return;
			}
		}
		
		throw new NullPointerException("Could not find file in SCM bundle");
	}
	
	public void repaceFileWithChannelList(String fileName, ChannelList channelList) {
		if ((!this.isScmFile()) && !fileName.equals(this.file.getName())) return;
		
		File[] availableFiles = (isScmFile() ? getScmContents() : new File[]{this.getFile()});
		for (int i = 0; i < availableFiles.length; i++) {
			if (availableFiles[i].getName().equals(fileName)) {
				MapParser.write(channelList, availableFiles[i]);
			}
		}
		
	}
	
	public void replaceSelectedFile(ChannelList channelList) {
		if (this.getFile() == null) return;
		repaceFileWithChannelList(this.getFile().getName(), channelList);
	}
	
	public byte detectMapType() {
		if(this.file == null) return new Byte(null).byteValue();
		
		String fileName = this.file.getName();
		ArrayList<Byte> possiblities = new ArrayList<Byte>();
		
		if (fileName.indexOf("map-AirD") > -1) possiblities.add(Channel.TYPE_AIR);
		if (fileName.indexOf("map-CableD") > -1) possiblities.add(Channel.TYPE_CABLE);
		if (fileName.indexOf("map-SateD") > -1) possiblities.add(Channel.TYPE_SAT);
		if (fileName.endsWith("clone.bin")) possiblities.add(Channel.TYPE_CLONE);
		
		if (possiblities.size() == 1) return possiblities.get(0).byteValue();
		if (possiblities.size() == 0) {
			possiblities.add(Channel.TYPE_AIR);
			possiblities.add(Channel.TYPE_CABLE);
			possiblities.add(Channel.TYPE_SAT);
			possiblities.add(Channel.TYPE_CLONE);
		}
		
		HashMap<Byte, String> captions = new HashMap<Byte, String>(4);
		captions.put(Channel.TYPE_AIR, "Air");
		captions.put(Channel.TYPE_CABLE, "Cable");
		captions.put(Channel.TYPE_SAT, "Satellite");
		captions.put(Channel.TYPE_CLONE, "Clone File");
		
		String[] choices = new String[possiblities.size()];
		for(byte b : possiblities) {
			choices[possiblities.indexOf(b)] = captions.get(b);
		}
		
		String chosenType = (String) JOptionPane.showInputDialog(null,
				"Could not detect correct file type. Please choose the right one:",
				"File Type",
				JOptionPane.WARNING_MESSAGE,
				null,
				choices,
				choices[0]);

		//If a string was returned, say so.
		if (chosenType != null && chosenType.length() > 0) {
			for (Map.Entry<Byte, String> entry : captions.entrySet()) {
				if(entry.getValue().equals(chosenType)) return ((Byte) entry.getKey()).byteValue();
			}
		}
		
		return new Byte(null).byteValue();
	}
	
	public File getScmFile() {
		if(this.isScmFile())
			return this.scmFile;
		
		return null;
	}
}
