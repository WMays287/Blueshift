package blueshift.util;

import java.util.ArrayList;
import java.util.List;

public class DiskStreamWriter {
	
	private List<Byte> data;
	
	public DiskStreamWriter() {
		data = new ArrayList<Byte>();
	}
	
	public void writeByte(byte foo) {
		data.add(foo);
	}
	
	public void writeBoolean(boolean foo) {
		writeByte((byte) (foo ? 0x01 : 0x00));
	}
	
	public void writeInt(int foo) {
		writeByte((byte) ((foo >> 24) & 0xFF));
		writeByte((byte) ((foo >> 16) & 0xFF));
		writeByte((byte) ((foo >> 8 ) & 0xFF));
		writeByte((byte) ( foo        & 0xFF));
	}
	
	public void writeString(String foo) {
		writeInt(foo.length());
		for (char c : foo.toCharArray()) {
			writeByte((byte) c);
		}
	}
	
	public List<Byte> toList() {
		return data;
	}
	
	public byte[] toArray() {
		// This is apparently the best way to convert List<Byte> to Byte[] ?
		byte[] foo = new byte[data.size()];
		for (int i = 0; i < foo.length; i++) {
			foo[i] = data.get(i);
		}
		return foo;
	}

}
