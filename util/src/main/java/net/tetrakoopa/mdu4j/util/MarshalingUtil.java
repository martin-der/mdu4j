package net.tetrakoopa.mdu4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


public class MarshalingUtil {

	public static void freeze(Object object, OutputStream output) throws IOException {
		new ObjectOutputStream(output).writeObject(object);
	}

	@SuppressWarnings("unchecked")
	public static <T> T unfreeze(InputStream input) throws IOException {
		try {
			return (T) new ObjectInputStream(input).readObject();
		} catch (ClassNotFoundException cnfex) {
			throw new IOException(cnfex);
		}
	}
}
