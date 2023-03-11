package com.weelgo.chainmapping.core.json.v1;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.weelgo.core.Color;

@JsonInclude(Include.NON_EMPTY)
public class JSN_CMGroup {

	private String uuid;
	private String name;
	private String package_name;
	private String package_parent_path;
	private String type;
	private List<JSN_CMTask> tasks = new ArrayList<>();
	private List<JSN_CMNeed> needs = new ArrayList<>();
	private boolean background_visible;
	private boolean border_visible;
	private int[] background_color = Color.convertIntoArray(Color.CREATE_DEFAULT_GROUP_BACKGROUND_COLOR());
	private int[] border_color = Color.convertIntoArray(Color.CREATE_DEFAULT_GROUP_BORDER_COLOR());

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

	public boolean isBackground_visible() {
		return background_visible;
	}

	public void setBackground_visible(boolean background_visible) {
		this.background_visible = background_visible;
	}

	public boolean isBorder_visible() {
		return border_visible;
	}

	public void setBorder_visible(boolean border_visible) {
		this.border_visible = border_visible;
	}

	public int[] getBackground_color() {
		return background_color;
	}

	public void setBackground_color(int[] background_color) {
		this.background_color = background_color;
	}

	public int[] getBorder_color() {
		return border_color;
	}

	public void setBorder_color(int[] border_color) {
		this.border_color = border_color;
	}

}
