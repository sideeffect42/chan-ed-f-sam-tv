package ch.maniswebdesign.chaned.channelmanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Takashi Yoshi
 *
 */
public abstract class Channel implements Cloneable {

	// TYPEs
	public static final byte TYPE_CABLE = (byte) 0x01;
	public static final byte TYPE_AIR = (byte) 0x02;
	public static final byte TYPE_SAT = (byte) 0x04;
	//public static final byte TYPE_SAT_D = (byte) 0x05;
	public static final byte TYPE_CLONE = (byte) 0x08;
	
	// STYPEs
	public static final byte STYPE_TV = (byte) 0x01;
	public static final byte STYPE_RADIO = (byte) 0x02;
	public static final byte STYPE_DATA = (byte) 0x0C;
	public static final byte STYPE_HD = (byte) 0x19;
	
	// VTYPEs
	public static final byte VTYPE_MPEG2 = (byte) 0x00;
	public static final byte VTYPE_MPEG4 = (byte) 0x01;
	
	// FLAGs
	public static final byte FLAG_ACTIVE = (byte) 0x80;
	public static final byte FLAG_SCRAMBLED = (byte) 0x01; //RB changed from 0x20 to 0x01
	public static final byte FLAG_LOCK = (byte) 0x01;
	
	public static final byte FLAG_FAV_1 = (byte) 0x01;
	public static final byte FLAG_FAV_2 = (byte) 0x02;
	public static final byte FLAG_FAV_3 = (byte) 0x04;
	public static final byte FLAG_FAV_4 = (byte) 0x08;
	
	
	public static char implementsFileVersion;
	private HashMap<String, Object> properties;
	protected static final Map<String, Class<? extends Object>> ALLOWED_PROPERTIES = 
			Collections.unmodifiableMap(
					new HashMap<String, Class<? extends Object>>() {
						private static final long serialVersionUID = 1L;
						{
							put("num", Integer.class);
							put("name", String.class);
							put("sid", Integer.class);
							put("vpid", Integer.class);
							put("mpid", Integer.class);
							
							put("bouqet", Integer.class);
							put("onid", Integer.class);
							put("tsid", Integer.class);
							
							put("stype", Byte.class);
							put("vtype", Byte.class);
							put("status", Byte.class);
							put("enc", Byte.class);
							put("fav", Byte.class);
							put("fav79", Byte.class);
							put("lock", Byte.class);
						}
					}
			);
	protected static Map<String, Class<? extends Object>> SPECIAL_PROPERTIES;
		
	public Channel() {	
		properties = new HashMap<String, Object>();
		
		// Set default values
		setProperty("stype", Channel.STYPE_TV);
		setProperty("vtype", Channel.VTYPE_MPEG2);
		setProperty("status", new Byte((byte) 0xE8));
		setProperty("enc", new Byte((byte) 0x00));
		setProperty("fav", new Byte((byte) 0x00));
		setProperty("fav79", new Byte((byte) 0x00));
		setProperty("lock", new Byte((byte) 0x00));
	}
	
	public void setProperty(String prop, Object value) {
		Map<String, Class<? extends Object>> specialProperties = null;
		try {
			java.lang.reflect.Field f = Class.forName(new Throwable().fillInStackTrace().getStackTrace()[1].getClassName()).getField("SPECIAL_PROPERTIES");
			if(f.getType().getName().equals("java.util.Map")) {
				specialProperties = (Map<String, Class<? extends Object>>) f.get(null);
			}
		} catch (Exception e) {}
		
		if ((Channel.ALLOWED_PROPERTIES.containsKey(prop) && value.getClass().equals(Channel.ALLOWED_PROPERTIES.get(prop))) ||
			(specialProperties != null && specialProperties.containsKey(prop) && value.getClass().equals(specialProperties.get(prop)))) {
			this.properties.put(prop, value);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public Object getProperty(String prop) {
		return this.properties.get(prop);
	}
	
	@Override
	public String toString() {
		String channelString = "cnum: " + this.getProperty("num") + 
							  " name: " + this.getProperty("name") + 
							  " sid: " + this.getProperty("sid") +
							  " mpid: " + this.getProperty("mpid") + 
							  " vpid: " + this.getProperty("vpid") + 
							  " bouqet: " + this.getProperty("bouqet") + 
							  " onid: " + this.getProperty("onid") + 
							  " tsid: " + this.getProperty("tsid");
		
					
		channelString += " type ";
		
		byte stype = (Byte) this.getProperty("stype");
		switch(stype) {
			case STYPE_TV:		channelString += "TV-SD"; break;
			case STYPE_RADIO:	channelString += "RADIO"; break;
			case STYPE_DATA:	channelString += "DATA"; break;
			case STYPE_HD:		channelString += "TV-HD"; break;
			default:			channelString += "unknown"; break;
		}
		
		channelString += " encryption: ";
		byte enc = (Byte) this.getProperty("enc");
		if((enc & FLAG_SCRAMBLED) != 0)
			channelString += "CSA"; //Content Secured on Air
		else
			channelString += "FTA"; //Free To Air
		
		return channelString;
	}
	
	@Override
	public Channel clone() {
		Channel clonedChannel = null;
		
		try {
			clonedChannel = (Channel) super.clone();
		} catch(CloneNotSupportedException e) {
			// do nothing
		}
		
		return clonedChannel;
	}

	/* Endianess must be converted as Samsung and Java VM don't share the same
	 * endianess
	 */
	protected static int convertEndianess(byte b, byte c) {
		int lower = b;
		int upper = c;
		if(b < 0) lower += 256;
		if(c < 0) upper += 256;
		return lower + (upper<<8);
	}
	
	protected static void revertEndianess(byte[] b, int offset, int data) {
		b[offset] = (byte) (data & 0x00ff); 
		b[offset+1] = (byte) (data>>8);
		return;
	}
	
	public abstract byte[] writeByteArray();
	public abstract int parse(int row, byte[] inData);
}