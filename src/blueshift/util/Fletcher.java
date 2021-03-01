package blueshift.util;

public class Fletcher {
	
	public static int fletcher16(byte[] data) {
		int checkLow = 0;
		int checkHigh = 0;
		for (byte foo : data) {
			checkLow = (checkLow + foo) % 255;
			checkHigh = (checkHigh + checkLow) % 255;
		}
		return checkHigh << 8 | checkLow;
	}

}
