/**
 *
 */
package polaris.okapi.options;


/**
 * @author lec50
 */
public enum WindowMode
{
	
	FULLSCREEN, BORDERLESS, WINDOWED;
	
	public static int valueOf(WindowMode mode) {
		return mode == WINDOWED ? 0 : mode == BORDERLESS ? 1 : 2;
	}
	
	public static WindowMode valueOf(int mode) {
		return mode == 0 ? WINDOWED : mode == 1 ? BORDERLESS : FULLSCREEN;
	}
	
}
