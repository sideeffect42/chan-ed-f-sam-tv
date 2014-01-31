package ch.maniswebdesign.chaned.channelmanager;

public class ListMerger {
	
	private ChannelList oldList;
	private ChannelList newList;
	private ChannelList mergedList;
	
	private ChannelList missingChannels;
	private ChannelList addedChannels;
	
	public ListMerger(ChannelList base, ChannelList mergeWith) {
		oldList = base;
		newList = mergeWith;
		
		missingChannels = new ChannelList();
		addedChannels = new ChannelList();
	}
	
	private void detectMissingChannels() {
		missingChannels.clear();
		
		for (int i = 0; i < oldList.lastChannelNum(); i++) {
			Channel c = oldList.get(i);
			if (c == null) continue;
			
			String cName = (String) c.getProperty("name");
			boolean foundInNew = false;
			
			for (int j = 0; j < newList.lastChannelNum(); j++) {
				Channel cNew = newList.get(i);
				if (cNew == null) continue;
				
				if (((String) cNew.getProperty("name")).equals(cName))
					foundInNew = true;
			}
			if(!foundInNew) missingChannels.append(c);
		}
	}
	
	private void detectNewChannels() {
		addedChannels.clear();
		
		for (int i = 0; i < newList.lastChannelNum(); i++) {
			Channel c = newList.get(i);
			if (c == null) continue;
			
			String cName = (String) c.getProperty("name");
			Boolean foundInOld = false;
			
			for(int j = 0; j < oldList.lastChannelNum(); j++) {
				Channel cOld = oldList.get(j);
				if (cOld == null) continue;
				
				if(cOld.getProperty("name").equals(cName)) foundInOld = true;
			}
			if(!foundInOld) addedChannels.append(c);
		}
	}
	
	public ChannelList mergeLists() {
		return mergeLists(true, true, true);
	}
	
	public ChannelList mergeLists(boolean updateProperties, boolean removeMissing, boolean appendAdded) {
		detectMissingChannels();
		System.out.println("Missing channels: (" + missingChannels.channelCount() + ")");
		for(String cName : missingChannels.getChannelNames()) { System.out.println("- " + cName); }
		
		detectNewChannels();
		System.out.println("Added channels: (" + addedChannels.channelCount() + ")");
		for(String cName : addedChannels.getChannelNames()) { System.out.println("- " + cName); }
		
		mergedList = oldList;
		
		// Map New List to Old List
		if (updateProperties) {
			for(int i = 0; i < mergedList.lastChannelNum(); i++) {
				Channel c = mergedList.get(i);
				if (c == null) continue;
				
				Channel foundMatch = newList.findFirst((String) c.getProperty("name"));
				if(foundMatch == null && !((String) c.getProperty("name")).endsWith("HD")) {
					// Try searching for the same channel with appendix "HD"
					String channelNameHD = ((String) c.getProperty("name")) + " HD";
					foundMatch = newList.findFirst(channelNameHD);
					if(foundMatch != null) {
						// Remove from addedChannels if channel exists with "HD" appendix
						addedChannels.removeAll(channelNameHD);
					}
				}
				
				if(foundMatch != null) {
					System.out.println("Replaced "+c.getProperty("name")+" ("+c.getProperty("num")+") with "+foundMatch.getProperty("name")+" ("+foundMatch.getProperty("num")+")");
	
					mergedList.put(i, foundMatch);
					
					// Remove from missingChannels because we found a match in the new list.
					missingChannels.removeAll((String) c.getProperty("name"));
				}
			}
		}
		
		if (removeMissing) {
			// Empty missing channels
			for (int i = 0; i < missingChannels.lastChannelNum(); i++) {
				Channel c = missingChannels.get(i);
				if (c == null) continue;
				
				mergedList.removeAll((String) c.getProperty("name"));
				System.out.println("Removed channel #" + c.getProperty("num") + " \"" + c.getProperty("name") + "\"");
			}
		}
		
		if (appendAdded) {
			// Fill up with added channels
			for (int i = 0; i < addedChannels.lastChannelNum(); i++) {
				Channel c = addedChannels.get(i);
				if (c == null) continue;
				
				int lastKey = mergedList.lastChannelNum();
				int  channelNum = (lastKey == 0 ? 0 : lastKey + 1);
				c.setProperty("num", channelNum);
				mergedList.put(channelNum, c);
				System.out.println("Appended channel #" + c.getProperty("num") + " \"" + c.getProperty("name") + "\"");
			}
		}
		
		return mergedList;
	}
	
	public ChannelList getMergedList() {
		return mergedList;
	}
}
