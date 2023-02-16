package com.weelgo.chainmapping.core.json.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class JSN_UniqueIdentifier {
	private String unique_identifier;

	public String getUnique_identifier() {
		return unique_identifier;
	}

	public void setUnique_identifier(String unique_identifier) {
		this.unique_identifier = unique_identifier;
	}

}
