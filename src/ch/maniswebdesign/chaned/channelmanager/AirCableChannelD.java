package ch.maniswebdesign.chaned.channelmanager;

public class AirCableChannelD extends AirCableChannel {
	
	public static int lChan = 320;
	public static char implementsFileVersion = 'D';
	
	public AirCableChannelD() {
		super();
		
		this.setProperty("recordLen", lChan);
		this.setProperty("iChanCRC", ((Integer) this.getProperty("recordLen")) - 1);
	}
}