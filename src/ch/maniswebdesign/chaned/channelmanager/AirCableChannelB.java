/**
 * 
 */
package ch.maniswebdesign.chaned.channelmanager;

/**
 * @author Takashi Yoshi
 *
 */
public class AirCableChannelB extends AirCableChannel {
	
	public static int lChan = 292;
	public static char implementsFileVersion = 'B';
	
	public AirCableChannelB() {
		super();
		
		this.setProperty("recordLen", lChan);
	}
}
