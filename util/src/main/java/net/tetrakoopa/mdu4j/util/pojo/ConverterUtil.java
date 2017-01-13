package net.tetrakoopa.mdu4j.util.pojo;

public class ConverterUtil {

	public static <SRC, DEST> DEST convert(SRC src) {
		return null;
	}

	public static <SRC extends Enum<SRC>, DEST extends Enum<DEST>> DEST convertEnum(SRC src, Class<DEST> destClass) {
		if (src == null)
			return null;

		final String srcName = src.name();

		for (DEST dest : destClass.getEnumConstants()) {
			if (srcName.equals(dest.name()))
				return dest;
		}
		throw new IllegalArgumentException("Cannot convert from ENUM<" + src.getClass().getName() + ">." + srcName + " to ENUM<" + destClass.getName() + ">");
	}

	//
	// public DEST convert(SRC src) {
	// if (src == null) {
	// return null;
	// }
	// return convert(src);
	// }
	//
	// public abstract DEST convert(SRC src, DEST dest);
}
