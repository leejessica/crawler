package mo.umac.parser;

public enum YahooXmlType {
	UNKNOWN("UNKNOWN"), VALID("VALID"), LIMIT_EXCEEDED("LIMIT_EXCEEDED"), OTHER_ERROR(
			"OTHER_ERROR");

	// TODO how to change the descriptions of enums
	private String description;

	private YahooXmlType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
