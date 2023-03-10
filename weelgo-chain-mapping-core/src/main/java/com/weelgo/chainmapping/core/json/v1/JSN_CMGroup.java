package com.weelgo.chainmapping.core.json.v1;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class JSN_CMGroup {

	private String uuid;
	private String name;
	private String package_name;
	private String package_parent_path;
	private String type;
	private List<JSN_CMTask> tasks = new ArrayList<>();
	private List<JSN_CMNeed> needs = new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getPackage_parent_path() {
		return package_parent_path;
	}

	public void setPackage_parent_path(String package_parent_path) {
		this.package_parent_path = package_parent_path;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<JSN_CMTask> getTasks() {
		return tasks;
	}

	public void setTasks(List<JSN_CMTask> tasks) {
		this.tasks = tasks;
	}

	public List<JSN_CMNeed> getNeeds() {
		return needs;
	}

	public void setNeeds(List<JSN_CMNeed> needs) {
		this.needs = needs;
	}

}
