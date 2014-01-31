/**
 * @author rayzyt <rayzyt at mail-buero.de>
 * @version 0.5
  
	Copyright 2009 by Timo Dobbrick
	For more information see http://www.polskafan.de/samsung
 
	This file is has been developt to support SamyGO ChanEdit.

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

import java.io.*;
import java.util.zip.*;

/**
 * defines basic zip archive functions: <dd>compress: create a zip file and
 * store all files of a given directory into it</dd> <dd>decompress: extract all
 * files found in an zip file into a given directory</dd>
 * 
 * @author rayzyt <rayzyt at mail-buero.de>
 */

public class ZipHandler {

	static final int BUFFER_SIZE = 2048;

	/**
	 * stores all file found in targetDir into the archive specified
	 * 
	 * @param zipFile
	 *            file to be created
	 * @param directoryToZip
	 *            directory of which all files will be compressed into the
	 *            zipfile
	 * @return number of files stored in the archive
	 */
	public static int compress(String zipFile, String directoryToZip) {
		if (!directoryToZip.endsWith(File.separator))
			directoryToZip += File.separator;

		
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFile);
			CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER_SIZE];

			// get a list of files from current directory
			File dir = new File(directoryToZip);
			String files[] = dir.list();
			int i = 0;
			for (; i < files.length; i++) {
				System.out.println("Adding: " + files[i]);
				FileInputStream fi = new FileInputStream(directoryToZip + files[i]);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
			System.out.println("Checksum: " + checksum.getChecksum().getValue());
			return i; // successful: return number of files stored into archive
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0; // unsuccessful
	}

	/**
	 * extracts all file found in archive into the targetDir
	 * 
	 * @param archive
	 *            file to be extracted
	 * @param targetDir
	 *            directory directory of which all files will be compressed into
	 *            the zipfile
	 * @return number of files extracted off the archive
	 */
	public static int decompress(File archive, String targetDir) {
		if (!targetDir.endsWith(File.separator))
			targetDir.concat(File.separator);

		try {
			BufferedOutputStream destination = null;
			FileInputStream inputFile = new FileInputStream(archive);
			CheckedInputStream checksum = new CheckedInputStream(inputFile, new Adler32());
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
			ZipEntry entry;
			
			int files = 0;
			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				byte data[] = new byte[BUFFER_SIZE];
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream(targetDir
						+ entry.getName());
				destination = new BufferedOutputStream(fos, BUFFER_SIZE);
				int count;
				while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
					destination.write(data, 0, count);
				}
				destination.flush();
				destination.close();
				files++;
			}
			zis.close();
			System.out.println("Checksum: " + checksum.getChecksum().getValue());
			return files; // successful: return number of files extracted
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // unsuccessful
	}
}