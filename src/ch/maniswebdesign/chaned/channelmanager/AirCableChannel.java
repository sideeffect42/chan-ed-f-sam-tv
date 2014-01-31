/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.42
  
	Copyright 2009 by Timo Dobbrick
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AirCableChannel extends Channel {
	
	public static final byte QAM64 = 0x1; //RB changed from 0x0 to 0x1
	public static final byte QAM256 = 0x2; //RB changed from 0x1 to 0x2
	public static final byte QAMauto = 0x8; //DVB-T
	
	public static final Map<String, Class<? extends Object>> SPECIAL_PROPERTIES = 
			Collections.unmodifiableMap(
					new HashMap<String, Class<? extends Object>>() {
						private static final long serialVersionUID = 1L;
						{
							put("recordLen", Integer.class);
							
							put("iChanNo", Integer.class);
							put("iChanVpid", Integer.class);
							put("iChanMpid", Integer.class);
							put("iChanSid", Integer.class);
							put("iChanStatus", Integer.class);
							put("iChanQam", Integer.class);
							put("iChanSType", Integer.class);
							put("iChanFav", Integer.class);
							put("iChanEnc", Integer.class);
							put("iChanSymbR", Integer.class);
							put("iChanLock", Integer.class);
							put("iChanONid", Integer.class);
							put("iChanNid", Integer.class);
							put("iChanBouqet", Integer.class);
							put("iChanProvId", Integer.class);
							put("iChanFreq", Integer.class);
							put("iChanLcn", Integer.class);
							put("iChanTSid", Integer.class);
							put("iChanName", Integer.class);
							put("lChanName", Integer.class);
							put("iChanSName", Integer.class);
							put("lChanSName", Integer.class);
							put("iChanVFmt", Integer.class);
							put("iChanFav79", Integer.class);
							put("iChanCRC", Integer.class);
							
							put("qam", Byte.class);
							
							put("nid", Integer.class);
							put("freq", Integer.class);
							put("symbr", Integer.class);
							
							put("lcn", Integer.class);
						}
					}
			);
	
	byte[] rawData = new byte[320]; //initialize with biggest value of various versions

	public AirCableChannel() {
		super();
		
		// Set default special properties
		setProperty("iChanNo", 0);				// Displayed Channel Number
		setProperty("iChanVpid", 2);			// Video Stream PID (or -1)
		setProperty("iChanMpid", 4);			// Program Clock Recovery PID
		setProperty("iChanSid", 6);			// SVB Service Identifier
		setProperty("iChanStatus", 8);
		setProperty("iChanQam", 12);			// Modulation type (QAM64 | QAM256 | QAM_Auto)
		setProperty("iChanSType", 15);			// Service Type (0x01 = TV; 0x02 = Radio; 0x0c = Data; 0x19 = HD)
		setProperty("iChanFav", 16);			// Video Codec (0=MPEG2, 1=MPEG4)
		setProperty("iChanEnc", 24);			// Scrambled service (0=FTA, 1=CSA)
//		public int iChan???	= 25; 					// Frame Rate
		setProperty("iChanSymbR", 28);			// Symbol Rate
		setProperty("iChanLock", 31);			// Lock State (0=open, 1=locked)
		setProperty("iChanONid", 32);			// Original DVB Network ID 
		setProperty("iChanNid", 34);			// DVB Network ID displayed ???
		setProperty("iChanBouqet", 36);		// ??? always 0
		setProperty("iChanProvId", 38);		// Service Provider ID (or -1)
		setProperty("iChanFreq", 42);			// Cable channel
		
		setProperty("iChanLcn", 44);			// Logical Channel Number or -1 ???
		setProperty("iChanTSid", 48);			// Transport Stream Identifier
	 	setProperty("iChanName", 64);			// Big-endian Unicode characters
	 	setProperty("lChanName", 100);
	 	setProperty("iChanSName", 164);		// Big-endian Unicode characters
	 	setProperty("lChanSName", 9);
	 	setProperty("iChanVFmt", 182);			// Video Format (5=1080i25, 7=720p50, 12=576i25, 13=576i25w, 20=custom)
	 	setProperty("iChanFav79", 290);		// bit-field (0x1=Fav1, 0x2=Fav2, 0x4=Fav3, 0x8=Fav4)
	 	setProperty("iChanCRC", 291);			// Simple char sum of all previous bytes

	 	setProperty("qam", QAM64);
		
	 	setProperty("nid", -1);
	 	setProperty("freq", -1);
	 	setProperty("symbr", -1);

	 	setProperty("lcn", 0);
	}
	
	/** 
	 * reads the record number "row" out of "inData"
	 * 
	 * @param row - number of the channel record
	 * @param inData - raw / binary date to parse
	 */
	public int parse(int row, byte[] inData) {
		/* read inData into the chan.rawData
		 * attention, byte data type is not unsigned, conversion must
		 * be applied to negative values */
		int recordLen = ((Integer) this.getProperty("recordLen")).intValue();
		
		int size = inData.length / recordLen;
		if (row > size) return 0;
		for (int i = row; i < size; i++) {			/* Search next valid line and return the values */ 
			/* empty line or inactive channel, skip to next */
			int offset = i * recordLen;

			if(inData[offset] == (byte) 00)			//RB || (inData[offset+8]&Channel.FLAG_ACTIVE)==0) continue;
				continue;
			//RB looks like iChanStatus must be checked on both bytes!
			//if((inData[offset] == (byte)00 && inData[offset+1] == (byte)00) || (inData[offset+iChanStatus] & Channel.FLAG_ACTIVE)==0) continue;

			byte chsum = 0;
			for(int j = 0; j < recordLen; j++) {
				rawData[j] = inData[offset+j];
				chsum += inData[offset+j];
			}
			this.setProperty("num",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanNo"))],
									 inData[offset + ((Integer) this.getProperty("iChanNo")) + 1]));			// Displayed Channel Number
			this.setProperty("vpid",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanVpid"))],
									 inData[offset + ((Integer) this.getProperty("iChanVpid")) + 1]));			// Video Stream PID (or -1)
			this.setProperty("mpid",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanMpid"))],
									 inData[offset + ((Integer) this.getProperty("iChanMpid")) + 1]));			// Program Clock Recovery PID
			this.setProperty("sid",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanSid"))],
									 inData[offset + ((Integer) this.getProperty("iChanSid")) + 1]));			// SVB Service Identifier
			
			this.setProperty("fav", inData[offset + ((Integer) this.getProperty("iChanFav"))]);
			this.setProperty("status", inData[offset + ((Integer) this.getProperty("iChanStatus"))]);
			this.setProperty("stype", inData[offset + ((Integer) this.getProperty("iChanSType"))]);
			this.setProperty("qam", inData[offset + ((Integer) this.getProperty("iChanQam"))]);					// Modulation
			this.setProperty("enc", inData[offset + ((Integer) this.getProperty("iChanEnc"))]);
			this.setProperty("freq",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanFreq"))],
									 inData[offset + ((Integer) this.getProperty("iChanFreq")) + 1]));			// Frame Rate
			this.setProperty("symbr",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanSymbR"))],
									 inData[offset + ((Integer) this.getProperty("iChanSymbR")) + 1]));			// Symbol Rate
			
			this.setProperty("lock", inData[offset + ((Integer) this.getProperty("iChanLock"))]);				// RB locked 0|1
			this.setProperty("onid",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanONid"))],
									 inData[offset + ((Integer) this.getProperty("iChanONid")) + 1]));			// Original DVB NID
			this.setProperty("bouqet",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanBouqet"))],
									 inData[offset + ((Integer) this.getProperty("iChanBouqet")) + 1]));
			this.setProperty("nid",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanNid"))],
									 inData[offset + ((Integer) this.getProperty("iChanNid")) + 1]));			// DVB NID displayed
			this.setProperty("lcn",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanLcn"))],
									 inData[offset + ((Integer) this.getProperty("iChanLcn")) + 1]));			// Logical Channel Number or -1
			this.setProperty("tsid",
							 convertEndianess(
									 inData[offset + ((Integer) this.getProperty("iChanTSid"))],
									 inData[offset + ((Integer) this.getProperty("iChanTSid")) + 1]));			// Transport Stream Identifier
			this.setProperty("fav79", inData[offset + ((Integer) this.getProperty("iChanFav79"))]);				// Test same as fav

//			if (i == 0) {
//				/* first line, try to detect channel type */
//				if ((Integer) this.getProperty("symbr") != 0) Main.mapType = Channel.TYPE_CABLE;
//				else Main.mapType = Channel.TYPE_AIR;
//			}

			/* read channel name (max. 100 chars) 
			 * 
			 * only reads a byte, has to be rewritten if
			 * the channel name is actually unicode utf8
			 */
			String channelName = "";
			for(int j = 0; j < ((Integer) this.getProperty("lChanName")); j++) {
				int c = inData[offset + ((Integer) this.getProperty("iChanName")) + 1 + j * 2];
				if(c == 0x00) break;
				if(c < 0) c+=256;
				channelName += (char) c;
			}
			this.setProperty("name", channelName);
			
			return (Integer) this.getProperty("num");
		}
		return 0;
	}

	/** 
	 * provides the Channel data a binary data for saving into a MapChan file
	 */
	public byte[] writeByteArray() {
		
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanNo"),
						(Integer) this.getProperty("num"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanVpid"),
						(Integer) this.getProperty("vpid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanMpid"),
						(Integer) this.getProperty("vpid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanSid"),
						(Integer) this.getProperty("vpid"));
		
		rawData[(Integer) this.getProperty("iChanFav")] = ((Byte) this.getProperty("fav")).byteValue();
		rawData[(Integer) this.getProperty("iChanQam")] = ((Byte) this.getProperty("qam")).byteValue();
		rawData[(Integer) this.getProperty("iChanStatus")] = ((Byte) this.getProperty("status")).byteValue();
		rawData[(Integer) this.getProperty("iChanSType")] = ((Byte) this.getProperty("stype")).byteValue();
		
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanONid"),
						(Integer) this.getProperty("onid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanNid"),
						(Integer) this.getProperty("nid"));
		
		rawData[(Integer) this.getProperty("iChanEnc")] = ((Byte) this.getProperty("enc")).byteValue();
		
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanFreq"),
						(Integer) this.getProperty("freq"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanLcn"),
						(Integer) this.getProperty("lcn"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanSymbR"),
						(Integer) this.getProperty("symbr"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanBouqet"),
						(Integer) this.getProperty("bouqet"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanTSid"),
						(Integer) this.getProperty("tsid"));
		
		char[] name = ((String) this.getProperty("name")).toCharArray();
		int n = 0;
		for(; n < name.length; n++) {
			rawData[((Integer) this.getProperty("iChanName")) +1+2*n] = (byte) name[n];
		}
		for(; n < ((Integer) this.getProperty("lChanName")); n++) {
			rawData[((Integer) this.getProperty("iChanName")) + 1 + 2 * n] = (byte) 0x00;
		}
		
		rawData[(Integer) this.getProperty("iChanLock")] = ((Byte) this.getProperty("lock")).byteValue();
		rawData[(Integer) this.getProperty("iChanFav79")] = ((Byte) this.getProperty("fav79")).byteValue();
		rawData[(Integer) this.getProperty("iChanCRC")] = 0;								// CRC Checksum
		for(int i = 0; i < ((Integer) this.getProperty("iChanCRC")); i++) {					//calculate the checksum
			rawData[(Integer) this.getProperty("iChanCRC")] += rawData[i];
		}
		
		return rawData;
	}
	
	/** converts the Channel/Frequency ID into a text String
	 * 
	 * @return String filled of Channel/Frequency ID in human readable form of {K|S|!}{number}
	 * @author rayzyt
	 */
	public String getFrequency() 
	{
		int freq = (Integer) this.getProperty("freq");
		if(freq < 7) return "S" + new Integer(freq + 4).toString();
		if(freq < 15) return "K" + new Integer(freq - 2).toString();
		if(freq < 46) return "S" + new Integer(freq - 4).toString();
		if(freq < 95) return "K" + new Integer(freq - 25).toString();
		if(freq < 194) return "!" + new Integer(freq).toString();
		if(freq < 196) return "S" + new Integer(freq - 192).toString();
		return "?" + new Integer(freq).toString();
	}
	/** converts a String back to the channel/Frequency ID
	 * 
	 * @param  String S - ASCII Text containing the channel/Frequency ID
	 * @return integer Frequency ID 
	*/
	public int setFrequency(String frequencyString) {
		
		int f = Integer.parseInt(frequencyString.substring(1));
		
		if(frequencyString.startsWith("S") && f < 4)
			this.setProperty("freq", f + 192);
		
		else if(frequencyString.startsWith("S") && f < 11)
			this.setProperty("freq", f - 4);
		
		else if(frequencyString.startsWith("S") && f < 42)
			this.setProperty("freq", f + 4);
		
		else if(frequencyString.startsWith("K") && f < 13)
			this.setProperty("freq", f + 2);
		
		else if(frequencyString.startsWith("K") && f < 70)
			this.setProperty("frreq", f + 25);
		
		else if(frequencyString.startsWith("!") && f < 194)
			this.setProperty("freq", f);
		
		else
			this.setProperty("freq", Integer.parseInt(frequencyString));
		
		int returnFreq = (Integer) this.getProperty("freq");
		return returnFreq;
	}
	
	public void setProperty(String prop, Object value) {
		super.setProperty(prop, value);
	}
}
