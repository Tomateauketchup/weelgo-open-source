package com.weelgo.chainmapping.core.json.v1;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class JSN_CMTask {

	private String uuid;
	private String name;
	private String group_uuid;
	private int position_x;
	private int position_y;
	private String name_position;
	private List<String> inputs = new ArrayList<>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup_uuid() {
		return group_uuid;
	}

	public void setGroup_uuid(String group_uuid) {
		this.group_uuid = group_uuid;
	}

	public int getPosition_x() {
		return position_x;
	}

	public void setPosition_x(int position_x) {
		this.position_x = position_x;
	}

	public int getPosition_y() {
		return position_y;
	}

	public void setPosition_y(int position_y) {
		this.position_y = position_y;
	}

	public String getName_position() {
		return name_position;
	}

	public void setName_position(String name_position) {
		this.name_position = name_position;
	}

	public List<String> getInputs() {
		return inputs;
	}

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

}
