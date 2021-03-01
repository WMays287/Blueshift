package blueshift.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Deflate {
	
	public static byte[] compress(byte[] data) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        DeflaterOutputStream dos = new DeflaterOutputStream(baos);
	        dos.write(data);
	        dos.flush();
	        dos.close();
	        return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] decompress(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
	        InflaterInputStream iis = new InflaterInputStream(bais);
	        return iis.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
