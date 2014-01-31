/**
 * @author polskafan <polska at polskafan.de>
 * @author rayzyt <rayzyt at mail-buero.de>
 * @version 0.47c2
  
	Copyright 2009 by Timo Dobbrick
	adjustments for C-Series made by rayzyt
	For more information see http://www.polskafan.de/samsung
 
    This file is part of SamyGO ChanEdit.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package ch.maniswebdesign.chaned.channelmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

import ch.maniswebdesign.chaned.gui.MainMethods;

public class MapParser {

	
	private static ChannelList parseFile(File file, byte fileType) {
		// Determine class base name
		String classBaseName;
		String caption;
		if (fileType == Channel.TYPE_CABLE) {
			classBaseName = "AirCableChannel";
			caption = "cable channel list";
		} else if (fileType == Channel.TYPE_AIR) {
			classBaseName = "AirCableChannel";
			caption = "air channel list";
		} else if (fileType == Channel.TYPE_SAT) {
			classBaseName = "SatChannel";
			caption = "satellite channel list";
		} else {
			return null;
		}
		
		final String statusMessage = "Parsing "+caption+"...";
		MainMethods.updateStatusBar(statusMessage);
		MainMethods.resetStatusProgress();
		
		// Set variables
		final Class<? extends Channel> chListClass = guessChannelClass(classBaseName, file);
		final ChannelList channelList = new ChannelList(fileType, chListClass);
		
		// Read file
		byte[] raw;
		try {
			raw = getFileContentsAsBytes(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		final byte[] rawData = raw;
		
		// Guess number of channels
		final int numChannels = guessNumChannelsInFile(rawData, chListClass);
		
		// Setup Multi-Threading
		final int numThreads = 8;
		final int step = numThreads;
		
		Thread threadHandler = new Thread(new Runnable() {
			private int parsedChannels;
			
			private void increaseParsedChannels() {
				parsedChannels++;
				MainMethods.updateStatusProgress((int) (parsedChannels / (float) numChannels * 100));
			}
			
			public void run() {
				long startTime = System.currentTimeMillis();
				
				// Set up parsing threads
				final Thread[] allThreads = new Thread[numThreads];
				
				for (int t = 0; t < numThreads; t++) {
					final int startRow = t - step;
					Thread runner = new Thread(new Runnable() {
						public void run() {
							Channel channel;
							int row = startRow;
							do {
								row += step;
								try {
									channel = chListClass.newInstance();
									
									if (channel.parse(row, rawData) <= 0) break;
									channelList.put((Integer) channel.getProperty("num"), channel);
									
									increaseParsedChannels();
								} catch (Exception e) {
									System.out.println("Could not parse channel row #"+row);
								}
							} while (row >= 0);
						}
					}); // Thread end
					allThreads[t] = runner;
				}
				
				// Start all threads
				for (int i = 0; i < allThreads.length; i++) allThreads[i].start();
				
				// Wait for all threads to die
				for (int i = 0; i < allThreads.length; i++) {
					try {
						allThreads[i].join();
					} catch (InterruptedException e) {
						System.out.println("Interrupted Thread #"+i);
						e.printStackTrace();
					}
				}
				
				// Finish
				long stopTime = System.currentTimeMillis();
				long parsingTime = (stopTime - startTime)/1000;
				MainMethods.updateStatusBar("Parsed "+parsedChannels+" channels. (in "+parsingTime+" secs)", 2);
				MainMethods.resetStatusProgress();
			}
		});
		
		// Start parsing
		threadHandler.start();
		
		try {
			threadHandler.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted handler thread");
			e.printStackTrace();
		}

		return channelList;
	}
	
	
	public static ChannelList parseAirCable(File file) {
		return parseFile(file, Channel.TYPE_CABLE);
	}

	public static ChannelList parseSat(File file) {
		return parseFile(file, Channel.TYPE_SAT);
	}

	public static ChannelList parseClone(File cloneFile) {
		MainMethods.updateStatusBar("Parsing clone file...");

		ChannelList channelList = new ChannelList(Channel.TYPE_CLONE, CloneChannel.class);
		
		byte[] rawData;
		try {
			rawData = getFileContentsAsBytes(cloneFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		/* only read as many lines, as tv says are valid */
		int size = convertEndianess(rawData[0x169f2], rawData[0x169f1]);

		for (int i = 0; i < size; i++) {
			MainMethods.updateStatusProgress((int)(i / (float) size * 100));
			
			int offset = 0x1342 + i * 81;
			
			/* empty line or inactive, skip to next */
			if ((rawData[offset + 73] & CloneChannel.FLAG_INACTIVE) == CloneChannel.FLAG_INACTIVE)
				continue;

			CloneChannel cloneChannel = new CloneChannel();
			for (int j = 0; j < 81; j++)
				cloneChannel.rawData[j] = rawData[offset + j];

			/*
			 * read channel name (max. 50 chars)
			 * 
			 * only reads a byte, has to be rewritten if the channel name is
			 * actually unicode utf8
			 */
			String channelName = "";
			for (int j = 0; j < 50; j++) {
				int c = rawData[offset + j];
				if (c == 0x00) break;
				if (c < 0)c += 256;
				channelName += (char) c;
			}
			cloneChannel.setProperty("name", channelName);

			cloneChannel.setProperty("num", convertEndianess(
									rawData[offset + 51],
									rawData[offset + 50]));
			cloneChannel.setProperty("vpid", convertEndianess(
									rawData[offset + 53],
									rawData[offset + 52]));
			cloneChannel.setProperty("mpid", convertEndianess(
									rawData[offset + 55],
									rawData[offset + 54]));
			
			cloneChannel.setProperty("freq", rawData[offset + 56]);
			cloneChannel.setProperty("fav", rawData[offset + 57]);
			
			cloneChannel.setProperty("nid", convertEndianess(
									rawData[offset + 60],
									rawData[offset + 59]));
			cloneChannel.setProperty("tsid", convertEndianess(
									rawData[offset + 62],
									rawData[offset + 61]));
			cloneChannel.setProperty("onid", convertEndianess(
									rawData[offset + 64],
									rawData[offset + 63]));
			cloneChannel.setProperty("sid", convertEndianess(
									rawData[offset + 66],
									rawData[offset + 65]));
			
			cloneChannel.setProperty("stype", rawData[offset + 71]);
			cloneChannel.setProperty("enc", rawData[offset + 73]);

			/* store channel in TreeMap */
			channelList.put((Integer) cloneChannel.getProperty("num"), cloneChannel);
		}
		
		MainMethods.updateStatusBar("Parsed "+size+" channels in clone file.", 2);
		MainMethods.resetStatusProgress();
		
		return channelList;
	}

	
	
	
	
	
	public static void write(ChannelList channelList, File outputFile) {
		byte mapType = channelList.getListType();
		switch (mapType) {
			case Channel.TYPE_AIR:
			case Channel.TYPE_CABLE:
				writeAirCable(channelList, outputFile);
				break;
			case Channel.TYPE_SAT:
				writeSat(channelList, outputFile);
				break;
			case Channel.TYPE_CLONE:
				writeClone(channelList, outputFile);
				break;
		}
	}

	public static void writeClone(ChannelList channelList, File outputFile) {
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write to file:\n" + e.getMessage());
			return;
		}

		/* write bytes 0 - 0x1341 out, nothing has changed there */
		try {
			outStream.write(channelList.rawData, 0, 0x1342);
		} catch (IOException e) {
			e.printStackTrace();
			try { outStream.close(); } catch (IOException e1) {}
			return;
		}

		/* build byte array to write out, stop at 999 channels */
		int entries = 0;
		for(int i = 0; i < channelList.size() && i < 999; i++) {
			CloneChannel channel = (CloneChannel) channelList.get(i);
			byte[] rawData = channel.rawData;

			char[] name = ((String) channel.getProperty("name")).toCharArray();
			int n = 0;
			for (; n < name.length && n < 50; n++) {
				rawData[n] = (byte) name[n];
			}
			rawData[75] = (byte) n;
			for (; n < 50; n++) {
				rawData[n] = (byte) 0x00;
			}

			revertClone(rawData, 50, (Integer) channel.getProperty("num"));
			revertClone(rawData, 52, (Integer) channel.getProperty("vpid"));
			revertClone(rawData, 54, (Integer) channel.getProperty("mpid"));
			rawData[56] = ((Integer) channel.getProperty("freq")).byteValue();
			rawData[57] = ((Byte) channel.getProperty("fav")).byteValue();
			revertClone(rawData, 59, (Integer) channel.getProperty("nid"));
			revertClone(rawData, 61, (Integer) channel.getProperty("tsid"));
			revertClone(rawData, 63, (Integer) channel.getProperty("onid"));
			revertClone(rawData, 65, (Integer) channel.getProperty("sid"));
			rawData[71] = ((Byte) channel.getProperty("stype")).byteValue();
			rawData[73] = ((Byte) channel.getProperty("enc")).byteValue();

			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				try { outStream.close(); } catch (IOException e1) {}
				return;
			}
			entries++;
		}
		System.out.println(entries);
		revertClone(channelList.rawData, 0x169ee, entries);
		revertClone(channelList.rawData, 0x169f1, entries);

		/* fill with 0xFF until we reach 999 channels */
		byte[] rawData = new byte[81];
		for (int i = 0; i < 81; i++)
			rawData[i] = (byte) 0xFF;
		while (entries < 999) {
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				try { outStream.close(); } catch (IOException e1) {}
				return;
			}
			entries++;
		}

		/* write bytes 0x14F59 - 0x1C390 out, nothing has changed there */
		try {
			outStream.write(channelList.rawData, 0x14F59, 0x74A7);
		} catch (IOException e) {
			e.printStackTrace();
			try { outStream.close(); } catch (IOException e1) {}
			return;
		}
		
		try { outStream.close(); } catch (IOException e) {}
		
		/* write the file out */
		System.out.println("Channel list written to file: " + outputFile);
		return;
	}

	public static void writeAirCable(ChannelList channelList, File outputFile) {
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write to file:\n" + e.getMessage());
			return;
		}

		/* build byte array to write out */
		int entries = 0;
		int recordLen = 0;
		char fileVersion = channelList.getListVersion();
		switch (fileVersion) {
			case 'C':
				recordLen = AirCableChannelC.lChan;
				break;
			case 'D':
				recordLen = AirCableChannelD.lChan;
				break;
			default: // Error
				System.out.println("Function not implemented for " + fileVersion
						+ "-Series TV");
				try { outStream.close(); } catch(IOException e) {}
				return;
		}
		AirCableChannel channel;
		for (int i = 0; i < channelList.size(); i++) {
			channel = (AirCableChannel) channelList.get(i);
			if(channel == null) continue;
			
			recordLen = (Integer) channel.getProperty("recordLen");
			
			byte[] rawData = channel.writeByteArray();

			try {
				outStream.write(rawData, 0, recordLen); // write data into the file
			} catch (IOException e) {
				e.printStackTrace();
				try { outStream.close(); } catch(IOException e1) {}
				return;
			}
			entries++;
		}
		/* TODO test if fill up to 1000 records is still needed */
		/* fill with null bytes until we reach a multiple of 1000 entries */
		byte[] rawData = new byte[recordLen];
		while (entries % 1000 != 0) {
			try {
				outStream.write(rawData, 0, recordLen); // write data into the file
			} catch (IOException e) {
				e.printStackTrace();
				try { outStream.close(); } catch(IOException e1) {}
				return;
			}
			entries++;
		}
		
		try { outStream.close(); } catch (IOException e) {}
		
		System.out.println("Channel list written to file: " + outputFile.getAbsolutePath());
		return;
	}

	public static void writeSat(ChannelList channelList, File outputFile) {
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write to file:\n" + e.getMessage());
			return;
		}

		/* build byte array to write out */
		int entries = 0;
		int recordLen = 0;
		Channel satChannel;
		char fileVersion = channelList.getListVersion();
		switch (fileVersion) {
			case 'B': {
				recordLen = SatChannelB.lChan;
				break;
			}
			case 'C': {
				recordLen = SatChannelC.lChan;
				break;
			}
			case 'D': {
				recordLen = SatChannelD.lChan;
				break;
			}
			default: // Error
				System.out.println("Function not implemented for " + fileVersion + "-Series TV");
				try { outStream.close(); } catch(IOException e) {}
				return;
		}
		for(int i = 0; i < channelList.size(); i++) {
			satChannel = (SatChannel) channelList.get(i);

			byte[] rawData = satChannel.writeByteArray();

			try {
				outStream.write(rawData, 0, recordLen);
			} catch (IOException e) {
				e.printStackTrace();
				try { outStream.close(); } catch(IOException e1) {}
				return;
			}
			entries++;
		}
		
		try { outStream.close(); } catch (IOException e) {}

		/* fill with null bytes until we reach a multiple of 1000 entries */
		/*
		 * it is not necessary anymore to fill up to 1000 entries!
		 * 
		 * while(entries % 1000 != 0) { chan = new SatChannelC(); byte[] rawData
		 * = new byte[SatChannel.initLen]; try { // outStream.write(rawData);
		 * outStream.write(rawData, 0, chan.recordLen); } catch (IOException e)
		 * { e.printStackTrace(); return; } entries++; }
		 */
		/* write the file out */
		System.out.println("Channel list written to file: " + outputFile.getAbsolutePath());
		return;
	}

	/*
	 * Endianess must be converted as Samsung and Java VM don't share the same
	 * endianess
	 */
	private static int convertEndianess(byte b, byte c) {
		int lower = b;
		int upper = c;
		if (b < 0)
			lower += 256;
		if (c < 0)
			upper += 256;
		return lower + (upper << 8);
	}

	private static void revertClone(byte[] b, int offset, int data) {
		b[offset + 1] = (byte) data;
		b[offset] = (byte) (data >> 8);
		return;
	}

	public static byte[] getFileContentsAsBytes(File file) throws IOException {
		byte[] data = new byte[(int) file.length()];
		InputStream inStream = new FileInputStream(file);
		inStream.read(data);
		inStream.close();
		return data;
	}
	
	private static FilenameFilter cloneFilter = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			if (name.equals("CloneInfo")) return true;
			return false;
		}
	};
	
	public static char getFileVersion(File file) {
		File cloneFile = null;
		char fileVersion = Character.MIN_VALUE;
		
		if (file.isDirectory()) {
			File[] dirListing = file.listFiles(MapParser.cloneFilter);
			if(dirListing.length == 1) cloneFile = dirListing[0];
		} else {
			File[] dirListing = file.getParentFile().listFiles(MapParser.cloneFilter);
			if(dirListing.length == 1) cloneFile = dirListing[0];
		}
		
		if (cloneFile != null && cloneFile.exists()) {
			// Determine version by Clone File
			byte data[];
			try {
				data = MapParser.getFileContentsAsBytes(cloneFile);
			} catch (IOException e) {
				e.printStackTrace();
				return fileVersion;
			}
			
			String line = "";
			for (int j = 0; j < data.length; j++) {
				int c = (int) data[j];
				if (c == 0x00)
					c = 0x20; // build a long string over borders / 0x00 is the end delimiter
				if (c < 0)
					c += 256;
				line += (char) c;
			}

			if (line.matches(".*[A-Za-z]+ +[A-Za-z]+[0-9][0-9]+B[0-9]+.*")) { fileVersion = 'B'; }
			else if (line.matches(".*[A-Za-z]+ +[A-Za-z]+[0-9][0-9]+C[0-9]+.*")) { fileVersion = 'C'; }
			else if (line.matches(".*[A-Za-z]+ +[A-Za-z]+[0-9][0-9]+D[0-9]+.*")) { fileVersion = 'D'; }
			
			if (fileVersion != Character.MIN_VALUE) { 
				System.out.println("Read TV Version: " + fileVersion + " from file " + cloneFile.getAbsolutePath());
			} else {
				System.out.println("No TV Version info found in cloneinfo file " + cloneFile.getAbsolutePath());
			}

		} else if(!file.isDirectory() && file.getName().endsWith(".scm")) {
			// Determine version by file name
			String fileName = file.getName();
			if (fileName.matches(".*[A-Za-z][A-Za-z][0-9][0-9]+B[0-9][0-9][0-9][0-9].*.scm")) {
				fileVersion = 'B';
			} else if (fileName.matches(".*[A-Za-z][A-Za-z][0-9][0-9]+C[0-9][0-9][0-9][0-9].*.scm")) {
				fileVersion = 'C';
			} else if (fileName.matches(".*[A-Za-z][A-Za-z][0-9][0-9]+D[0-9][0-9][0-9][0-9].*.scm")) {
				fileVersion = 'D';
			}
		} else {
			
			HashMap<Character, String> captions = new HashMap<Character, String>(3);
			captions.put('B', "B-Series (2009)");
			captions.put('C', "C-Series (2010)");
			captions.put('D', "D-Series (2011)");
			
			String[] choices = new String[captions.size()];
			int i = 0;
			for(String caption : captions.values()) {
				choices[i] = caption;
				i++;
			}
			
			String chosenVersion = (String) JOptionPane.showInputDialog(null,
					"Could not detect correct file version. Please choose the right one:",
					"File Version",
					JOptionPane.WARNING_MESSAGE,
					null,
					choices,
					choices[0]);

			//If a string was returned, say so.
			if (chosenVersion != null && chosenVersion.length() > 0) {
				for (Map.Entry<Character, String> entry : captions.entrySet()) {
					if (entry.getValue().equals(chosenVersion)) fileVersion = ((Character) entry.getKey()).charValue();
				}
			}
		}
		
		return fileVersion;
	}
	
	private static int guessNumChannelsInFile(byte[] rawData, Class<? extends Channel> targetClass) {
		int numChannels = new ChannelList().MAX_CHANNELS;
		try {
			int byteLength = rawData.length;
			
			for(int i = (rawData.length - 1); i >= 0; i--) {
				if(rawData[i] != '\0') { byteLength = i; break; }
			}
			
			numChannels = byteLength / ((Integer) targetClass.newInstance().getProperty("recordLen")).intValue();
		} catch (Exception e) {}
		
		return numChannels;
	}
	
	private static Class<? extends Channel> guessChannelClass(String classBaseName, File fileToParse) {
		Class<? extends Channel> guessedClass = Channel.class;
		
		try {
			char fileVersion = getFileVersion(fileToParse);
			System.out.println(MapParser.class.getPackage().getName() + "." + classBaseName + fileVersion);
			
			Class<? extends Channel> classForVersion = (Class<? extends Channel>) Class.forName(MapParser.class.getPackage().getName() + "." + classBaseName + fileVersion); 
			classForVersion.newInstance();
			
			guessedClass = classForVersion;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return guessedClass;
	}
}
