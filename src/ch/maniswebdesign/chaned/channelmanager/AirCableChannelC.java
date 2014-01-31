/**
 * @author rayzyt
 * @version 0.49cd
 */

package ch.maniswebdesign.chaned.channelmanager;

public class AirCableChannelC extends AirCableChannel {
	
	public static int lChan = 292;
	public static char implementsFileVersion = 'C';
	
	public AirCableChannelC() {
		super();
		
		super.setProperty("recordLen", lChan);
		super.setProperty("iChanCRC", lChan - 1);
	}
}