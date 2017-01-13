package net.tetrakoopa.mdu.service.dataquery.message;


public class QueryRequestMessage {

	private String queryJson;

	private String optionsJson;

	public String getQueryJson() {
		return queryJson;
	}

	public void setQueryJson(String queryJson) {
		this.queryJson = queryJson;
	}

	public String getOptionsJson() {
		return optionsJson;
	}

	public void setOptionsJson(String optionsJson) {
		this.optionsJson = optionsJson;
	}

}
