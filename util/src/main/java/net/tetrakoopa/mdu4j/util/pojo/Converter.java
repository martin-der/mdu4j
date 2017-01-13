package net.tetrakoopa.mdu4j.util.pojo;

public interface Converter<SRC, DEST> {

	public DEST convertNotNull(SRC src);
}
