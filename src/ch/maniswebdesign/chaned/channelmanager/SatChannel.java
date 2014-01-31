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

public abstract class SatChannel extends Channel {
	public static final int initLen = 200; // max bytes we need for any sub class!

	public static final Map<String, Class<? extends Object>> SPECIAL_PROPERTIES = 
			Collections.unmodifiableMap(
					new HashMap<String, Class<? extends Object>>() {
						private static final long serialVersionUID = 1L;
						{
							put("recordLen", Integer.class);
							
							put("tpid", Integer.class);
							put("sat", Integer.class);
							put("lChanName", Integer.class);
							
							put("iChanNo", Integer.class);
							put("iChanVpid", Integer.class);
							put("iChanMpid", Integer.class);
							put("iChanSid", Integer.class);
							put("iChanVType", Integer.class);
							put("iChanSType", Integer.class);
							put("iChanLock", Integer.class);
							put("iChanONid", Integer.class);
							put("iChanBouqet", Integer.class);
							put("iChanTSid", Integer.class);
							put("iChanName", Integer.class);
							put("iChanFav79", Integer.class);
							put("iChanCRC", Integer.class);
						}
					}
			);

	
	byte[] rawData = new byte[initLen];

	public SatChannel() {
		super();
		
		this.setProperty("tpid", -1);
		this.setProperty("sat", -1);
		this.setProperty("recordLen", 0);			// C-Series: 144; D-Series: 172
		this.setProperty("lChanName", 50);			// Max length of the name string in Unicode (2 byte per character)

		this.setProperty("iChanNo", 0);				// Displayed Channel Number
		this.setProperty("iChanVpid", 2);			// Video Stream PID (or -1)
		this.setProperty("iChanMpid", 4);			// Program Clock Recovery PID
		this.setProperty("iChanVType", 6);			// Virtual Service Type
		this.setProperty("iChanSType", 14);			// Service Type (0x01 = TV; 0x02 = Radio; 0x0c = Data; 0x19 = HD)
		this.setProperty("iChanSid", 16);			// SVB Service Identifier
		this.setProperty("iChanTpid", 18);
		this.setProperty("iChanSat", 20);
		this.setProperty("iChanTSid", 24);			// Transport Stream Identifier
		this.setProperty("iChanONid", 28);			// Original DVB Network ID
		this.setProperty("iChanName", 36);			// Big-endian Unicode characters
		this.setProperty("iChanBouqet", 138);		// ??? always 0
		this.setProperty("iChanLock", 141);			// Locked (0=open, 1=locked)
		this.setProperty("iChanFav79", 142);		// bit-field (0x1=Fav1, 0x2=Fav2, 0x4=Fav3, 0x8=Fav4)
		this.setProperty("iChanCRC", -1);			// Simple char sum of all previous bytes
	}
	
	public int parse(int row, byte[] inData) {
		/*
		 * read rawData attention, byte data type is not unsigned, conversion
		 * must be applied to negative values
		 */
		int recordLen = ((Integer) this.getProperty("recordLen")).intValue();
		
		int size = inData.length / recordLen;
		if (row > size)
			return 0;
		for (int i = row; i < size; i++) { // Search next valid line and return the values
			// empty line, skip to next // RB: skip only if both bytes of chan.num == 0
			int offset = i * recordLen;
			if ((inData[offset] | inData[offset + 1]) == (byte) 00)
				continue;

			byte chsum = 0;
			for (int j = 0; j < recordLen; j++) {
				rawData[j] = inData[offset + j];
				chsum += inData[offset + j];
			}
			
			this.setProperty("num", convertEndianess(												// Displayed Channel Number
										rawData[(Integer) this.getProperty("iChanNo")],
										rawData[((Integer) this.getProperty("iChanNo")) + 1]));
			this.setProperty("vpid", convertEndianess(												// Video Stream PID (or -1)
										rawData[(Integer) this.getProperty("iChanVpid")],
										rawData[((Integer) this.getProperty("iChanVpid")) + 1]));
			this.setProperty("mpid", convertEndianess(												// Program Clock Recovery PID
										rawData[(Integer) this.getProperty("iChanMpid")],
										rawData[((Integer) this.getProperty("iChanMpid")) + 1]));
			
			this.setProperty("vtype", rawData[(Integer) this.getProperty("iChanVType")]);
			this.setProperty("stype", rawData[(Integer) this.getProperty("iChanSType")]);
			
			this.setProperty("sid", convertEndianess(												// SVB Service Identifier
										rawData[(Integer) this.getProperty("iChanSid")],
										rawData[((Integer) this.getProperty("iChanSid")) + 1]));
			this.setProperty("tpid", convertEndianess(												// TPID
										rawData[(Integer) this.getProperty("iChanTpid")],
										rawData[((Integer) this.getProperty("iChanTpid")) + 1]));
			this.setProperty("sat", convertEndianess(												// SAT ID
										rawData[(Integer) this.getProperty("iCanSat")],
										rawData[((Integer) this.getProperty("iChanSat")) + 1]));
			this.setProperty("tsid", convertEndianess(												// Transport Stream Identifier
										rawData[(Integer) this.getProperty("iChanTSid")],
										rawData[((Integer) this.getProperty("iChanTSid")) + 1]));
			this.setProperty("onid", convertEndianess(												// Original DVB  NID
										rawData[(Integer) this.getProperty("iChanONid")],
										rawData[((Integer) this.getProperty("iChanONid")) + 1]));
			this.setProperty("bouqet", convertEndianess(
										rawData[(Integer) this.getProperty("iChanBouqet")],
										rawData[((Integer) this.getProperty("iChanBouqet")) + 1]));
			
			this.setProperty("lock", rawData[(Integer) this.getProperty("iChanLock")]);				// RB locked 0|1
			this.setProperty("fav79", rawData[(Integer) this.getProperty("iChanFav79")]);			// Test same as chan.fav

			// fav = rawData[iChanFav];
			// status = rawData[iChanStatus];
			// qam = rawData[iChanQam]; //modulation
			// enc = rawData[iChanEnc];
			// freq = convertEndianess(rawData[iChanFreq ], rawData[iChanFreq +
			// 1]); //Frame Rate
			// symbr = convertEndianess(rawData[iChanSymbR ], rawData[iChanSymbR
			// + 1]); //Symbol Rate
			// nid = convertEndianess(rawData[iChanNid ], rawData[iChanNid +
			// 1]); //DVB NID displayed
			// lcn = convertEndianess(rawData[iChanLcn ], rawData[iChanLcn +
			// 1]); //Logical Channel Number or -1

			/*
			 * read channel name
			 * 
			 * only reads a byte, has to be rewritten if the channel name is
			 * actually unicode utf8
			 */
			String name = "";
			for (int j = 0; j < ((Integer) this.getProperty("lChanName")); j++) {
				int c = rawData[((Integer) this.getProperty("iChanName")) + 1 + j * 2];
				if (c == 0x00) break; // 0x00 is the end delimiter
				if (c < 0) c += 256;
				name += (char) c;
			}
			this.setProperty("name", name);
			return (Integer) this.getProperty("num");
		}
		return 0;
	}

	public byte[] writeByteArray() {
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanNo"),
						(Integer) this.getProperty("num"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanVpid"),
						(Integer) this.getProperty("vpid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanMpid"),
						(Integer) this.getProperty("mpid"));
		
		rawData[(Integer) this.getProperty("iChanVType")] = ((Byte) this.getProperty("vtype")).byteValue();
		rawData[(Integer) this.getProperty("iChanSType")] = ((Byte) this.getProperty("stype")).byteValue();
		
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanSid"),
						(Integer) this.getProperty("sid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanTpid"),
						(Integer) this.getProperty("tpid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanSat"),
						(Integer) this.getProperty("sat"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanTSid"),
						(Integer) this.getProperty("tsid"));
		revertEndianess(rawData,
						(Integer) this.getProperty("iChanONid"),
						(Integer) this.getProperty("onid"));

		int iChanName = (Integer) this.getProperty("iChanName");
		char[] name = ((String) this.getProperty("name")).toCharArray();
		int n = 0;
		for (; n < name.length; n++) {
			rawData[iChanName + 1 + 2 * n] = (byte) name[n];
		}
		int lChanName = (Integer) this.getProperty("lChanName");
		for (; n < lChanName; n++) {
			rawData[iChanName + 1 + 2 * n] = 0x00;
		}

		revertEndianess(rawData,
						(Integer) this.getProperty("iChanBouqet"),
						(Integer) this.getProperty("bouqet"));
		
		rawData[(Integer) this.getProperty("iChanLock")] = ((Byte) this.getProperty("lock")).byteValue();
		rawData[(Integer) this.getProperty("iChanFav79")] = ((Byte) this.getProperty("fav79")).byteValue();

		int iChanCRC = (Integer) this.getProperty("iChanCRC");
		rawData[iChanCRC] = 0;
		/* calculate checksum */
		for (int i = 0; i < iChanCRC; i++) {
			rawData[iChanCRC] += rawData[i];
		}
		return rawData;
	}
	
	public void setProperty(String prop, Object value) {
		super.setProperty(prop, value);
	}
}
