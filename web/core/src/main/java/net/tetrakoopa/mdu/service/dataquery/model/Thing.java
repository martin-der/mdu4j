package net.tetrakoopa.mdu.service.dataquery.model;

import com.google.gson.annotations.SerializedName;

public class Thing {

	private String caption;

	@SerializedName("UIC")
	private boolean uic;
	@SerializedName("UIS")
	private boolean uis;
	@SerializedName("UIR")
	private boolean uir;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public boolean isUic() {
		return uic;
	}

	public void setUic(boolean uic) {
		this.uic = uic;
	}

	public boolean isUis() {
		return uis;
	}

	public void setUis(boolean uis) {
		this.uis = uis;
	}

	public boolean isUir() {
		return uir;
	}

	public void setUir(boolean uir) {
		this.uir = uir;
	}

}
