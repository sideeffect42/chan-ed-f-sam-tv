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

public class CloneChannel extends Channel {
	public static final byte FLAG_INACTIVE = (byte) 0x20;
	public static final byte FLAG_SCRAMBLED = (byte) 0x04;
	
	public static final Map<String, Class<? extends Object>> SPECIAL_PROPERTIES = 
			Collections.unmodifiableMap(
					new HashMap<String, Class<? extends Object>>() {
						private static final long serialVersionUID = 1L;
						{
							put("nid", Integer.class);
							put("freq", Integer.class);
							put("flags", Byte.class);
						}
					}
			);

	
	public byte[] rawData = new byte[81];
	
	public CloneChannel() {
		super();
		
		setProperty("nid", -1);
		setProperty("freq", -1);
		//setProperty("flags", 0x00);
	}
	
	public byte[] writeByteArray() {
		return null;
	}
	public int parse(int row, byte[] inData) {
		return 0;
	}
	
	public void setProperty(String prop, Object value) {
		super.setProperty(prop, value);
	}
}