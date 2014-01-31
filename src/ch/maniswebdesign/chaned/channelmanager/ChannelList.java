package ch.maniswebdesign.chaned.channelmanager;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ch.maniswebdesign.chaned.channelmanager.Channel;

/**
 * @author Takashi Yoshi
 *
 */
public class ChannelList {
	
	private Channel[] channelArray;
	public final int MAX_CHANNELS = 1000;
	private byte listType;
	public byte[] rawData;
	private boolean strictTyped;
	private Class<? extends Channel> allowedClass;
	
	public ChannelList() {
		this.channelArray = new Channel[MAX_CHANNELS];
		this.listType = 0;
		this.strictTyped = false;
		this.allowedClass = Channel.class;
	}
	
	public ChannelList(byte listType, Class<? extends Channel> allowedClass) {
		this.channelArray = new Channel[MAX_CHANNELS];
		this.listType = listType;
		
		this.strictTyped = true;
		this.allowedClass = allowedClass;
	}
	
	
	
	public byte getListType() {
		return listType;
	}
	
	public int size() {
		return this.channelArray.length;
	}
	
	public int channelCount() {
		int channelCount = 0;
		
		for(Channel c : this.channelArray) {
			if(c != null && ((String) c.getProperty("name")).length() > 0)
				channelCount++;
		}
		
		return channelCount;
	}
	
	public void clear() {
		for(int i = 0; i < channelArray.length; i++)
			channelArray[i] = null;
	}
	
	public int lastChannelNum() {
		int highestNum = 0;
		
		for (int i = this.channelArray.length - 1; i >= 0; i--) {
			if(this.channelArray[i] != null) { highestNum = i; break; }
		}
		
		return highestNum;
	}
	
	public char getListVersion() {
		char fileVersion = Character.MIN_VALUE;
		
		Field versionField = null;
		try {
			versionField = allowedClass.getField("implementsFileVersion");
		} catch (SecurityException e) {} catch (NoSuchFieldException e) {}
		
		if(versionField != null) {
			try {
				fileVersion = versionField.getChar(versionField);
			} catch (IllegalArgumentException e) {} catch (IllegalAccessException e) {}
		}
		
		return fileVersion;
	}
	
	public String[] getChannelNames() {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i = 0; i < this.channelArray.length; i++) {
			if(this.channelArray[i] != null) {
				String channelName = (String) this.channelArray[i].getProperty("name");
				if(channelName.length() > 0) names.add(channelName);
			}
		}
		
		return names.toArray(new String[]{});
	}
	
	public Channel get(int index) {
		if (index < 0) throw new IllegalAccessError("index must not be negative");
		if (channelArray[index] == null) return null;
		return channelArray[index].clone();
	}
	
	public void put(int index, Channel channel) {
		Channel toPut = channel.clone();
		
		if(index < 0) throw new IllegalArgumentException("index must not be negative");
		if(index > MAX_CHANNELS) throw new IllegalArgumentException("index must be in range. (maxValue = "+MAX_CHANNELS+")");
		if(this.strictTyped && !(allowedClass.isAssignableFrom(channel.getClass())))
			throw new IllegalArgumentException("This list is strict typed and your channel does not conform to this!");
		
		toPut.setProperty("num", index);
		channelArray[index] = toPut;
	}
	
	public void append(Channel channel) {
		int highestChNum = lastChannelNum();
		if (highestChNum > 0)
			this.put(highestChNum + 1, channel);
		else
			this.put(0, channel);
	}
	
	public void removeFirst(String channelName, int offset) {
		for (int i = offset; i < channelArray.length; i++) {
			if (((String) channelArray[i].getProperty("name")).equals(channelName)) { channelArray[i] = null; return; }
		}
	}
	
	public void remove(int channelNum) {
		if (channelNum > channelArray.length || channelNum < 0) throw new ArrayIndexOutOfBoundsException();
		if (channelArray[channelNum] != null)
			channelArray[channelNum] = null;
		
	}
	
	public void removeFirst(String channelName) {
		removeFirst(channelName, 0);
	}
	
	public void removeAll(String channelName) {
		for (int i = 0; i < channelArray.length; i++) {
			if (channelArray[i] == null) continue;
			
			if (((String) channelArray[i].getProperty("name")).equals(channelName))
				channelArray[i] = null;
		}	
	}
	
	public Channel findFirst(String channelName, int offset) {
		for(int i = offset; i < channelArray.length; i++) {
			if (channelArray[i] == null) continue;
			if (((String) channelArray[i].getProperty("name")).equals(channelName)) return channelArray[i];
		}
		
		return null;
	}
	
	public Channel findFirst(String channelName) {
		return findFirst(channelName, 0);
	}
	
	public Channel[] findAll(String channelName) {
		ArrayList<Channel> channelsFound = new ArrayList<Channel>();
		
		for (int i = 0; i < channelArray.length; i++) {
			if (channelArray[i] == null) continue;
			if (((String) channelArray[i].getProperty("name")).equals(channelName)) channelsFound.add(channelArray[i]);
		}
		
		return (Channel[]) channelsFound.toArray();
	}
	
	public int indexOf(String channelName, int offset) {
		for (int i = offset; i < channelArray.length; i++) {
			if (channelArray[i] == null) continue;
			if (((String) channelArray[i].getProperty("name")).equals(channelName)) return i; 
		}
		
		return -1;
	}
	
	public int indexOf(String channelName) {
		return indexOf(channelName, 0);
	}
	
	public Integer[] indexOfAll(String channelName) {
		ArrayList<Integer> indices  = new ArrayList<Integer>();
		
		for (int i = 0; i < channelArray.length; i++) {
			if (channelArray[i] == null) continue;
			if (((String) channelArray[i].getProperty("name")).equals(channelName)) indices.add(i); 
		}
		
		return (Integer[]) indices.toArray();
	}

	@Override
	public String toString() {
		String listString = "";
		
		for (int i = 0; i < this.lastChannelNum(); i++) {
			Channel c = channelArray[i];
			if (c == null) continue;
			
			listString += "["+c.getProperty("num")+", "+c.getProperty("name")+"]\n";
		}
		
		return listString;
	}
}
