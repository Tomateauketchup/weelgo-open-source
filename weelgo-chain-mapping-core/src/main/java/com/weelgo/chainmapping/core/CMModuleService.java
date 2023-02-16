package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.cleanString;
import static com.weelgo.core.CoreUtils.isNotNullOrEmpty;
import static com.weelgo.core.ValidatorUtils.checkTaskName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.core.exceptions.WeelgoException;
import static com.weelgo.core.CoreUtils.*;
import static com.weelgo.core.ValidatorUtils.*;

public class CMModuleService implements IUuidObject, INamedObject {

	private Object parentContainer;
	private CMGroup rootGroup;
	private List<CMGroup> groups = new ArrayList<>();
	private List<CMTask> tasks = new ArrayList<>();
	private List<CMDeliverable> deliverables = new ArrayList<>();

	private Map<String, IUuidObject> objectMapByUuid;
	private Map<String, CMGroup> groupMapByUuid;
	private Map<String, CMGroup> groupMapByPackagePath;
	private Map<String, CMTask> taskMapByUuid;
	private Map<String, CMDeliverable> deliverableMapByUuid;
	private Map<String, List<CMTask>> taskChilds;
	private Map<String, List<CMDeliverable>> deliverableChilds;
	private Map<String, List<CMGroup>> groupChilds;

	public static CMModuleService createModule(String name) {
		CMGroup gp = new CMGroup();
		gp.setName(name);

		CMModuleService module = new CMModuleService();
		module.setRootGroup(gp);

		return module;
	}

	public String getModuleUniqueIdentifier() {
		if (rootGroup != null) {
			return rootGroup.getModuleUniqueIdentifier();
		}
		return "";
	}

	public void check() {
		assertNotNullOrEmptyFatal(rootGroup);
		assertNotNullOrEmptyFatal(rootGroup.getModuleUniqueIdentifier());
		if (groups != null) {
			for (CMGroup gp : groups) {
				checkGroup(gp);
			}
		}

		// TODO il faut faire les vérification de noms et autres ici

		needReloadObjects();
	}

	public void checkGroup(CMGroup gp) {
		if (gp != null) {
			gp.setModuleUniqueIdentifier(rootGroup.getModuleUniqueIdentifier());
			gp.calculateUuid(rootGroup.getModuleUniqueIdentifier());
			gp.calculateGroupUuid(rootGroup.getModuleUniqueIdentifier());
			gp.calculatePackageFullPath();
		}
	}

	public String createPackageParentPath(CMGroup gpParent) {
		if (gpParent != null) {
			return CoreUtils.createPackageString(gpParent.getPackageParentPath(), gpParent.getPackageName());
		}
		return "";
	}

	public CMGroup getRootGroup() {
		return rootGroup;
	}

	public void setRootGroup(CMGroup rootGroup) {
		this.rootGroup = rootGroup;
	}

	public Object getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(Object parentContainer) {
		this.parentContainer = parentContainer;
	}

	public String[] findNameForNewGroup(String parentGroupUuid) {
		parentGroupUuid = cleanString(parentGroupUuid);
		assertNotNullOrEmpty(parentGroupUuid);
		int index = 1;
		String baseName = "New Group";
		String baseNamePackage = "new_group";

		String bn = "";
		String pk = "";
		do {

			bn = baseName + " " + index;
			pk = baseNamePackage + "_" + index;

			index++;

		} while (isPackageAlreadyExists(pk, parentGroupUuid));

		return new String[] { bn, pk };

	}

	public boolean isPackageAlreadyExists(String packageName, String parentGroupUuid) {

		packageName = cleanString(packageName);
		parentGroupUuid = cleanString(parentGroupUuid);
		assertNotNullOrEmpty(parentGroupUuid);

		checkPackageName(packageName);
		CMGroup gp = getGroupByUuid(parentGroupUuid);
		assertNotNullOrEmpty(gp);

		String path = CoreUtils.createPackageString(gp.getPackageFullPath(), packageName);
		gp = getGroupByPackageFullPath(path);

		if (gp == null) {
			return false;
		}

		return true;
	}

	public CMGroup createGroup(String name, String packageName, String parentGroupUuid) {
		name = cleanString(name);
		packageName = cleanString(packageName);
		parentGroupUuid = cleanString(parentGroupUuid);
		assertNotNullOrEmpty(parentGroupUuid);

		checkGroupName(name);
		checkPackageName(packageName);

		if (isPackageAlreadyExists(packageName, parentGroupUuid)) {
			throwDynamicException(WeelgoException.GROUP_ALREADY_EXIST);
		}

		CMGroup parent = getGroupByUuid(parentGroupUuid);
		if (parent == null) {
			throwInvalidInput();
		}

		CMGroup gp = new CMGroup();
		gp.setName(name);
		gp.setPackageName(packageName);
		gp.setPackageParentPath(createPackageParentPath(parent));
		gp.setGroupUuid(parentGroupUuid);
		gp.setType(CMGroup.TYPE_GROUP);
		checkGroup(gp);
		getGroups().add(gp);

		needReloadObjects();

		return gp;
	}

	public CMTask createTask(String taskName, String parentGroupUuid, int posX, int posY) {
		taskName = cleanString(taskName);
		parentGroupUuid = cleanString(parentGroupUuid);
		checkTaskName(taskName);

		CMGroup gp = rootGroup;

		if (isNotNullOrEmpty(parentGroupUuid)) {
			// We check the group exist
			CMGroup gpTmp = getGroupByUuid(parentGroupUuid);
			if (gpTmp != null) {
				gp = gpTmp;
			}
		}

		String taskUuid = CoreUtils.createPackageString(gp.getUuid(), taskName);
		// We check if there is already a task with same UUID
		if (getObjectByUuid(taskUuid) != null) {
			throwDynamicException(WeelgoException.OBJECT_ALREADY_EXIST);
		}

		CMTask task = new CMTask();
		task.setGroupUuid(gp.getUuid());
		task.setName(taskName);
		task.setUuid(taskUuid);
		task.setPosX(posX);
		task.setPosY(posX);

		tasks.add(task);

		needReloadObjects();

		return task;
	}

	public Object getObjectByUuid(String uuid) {
		loadObjectsIntoMap(false);
		if (objectMapByUuid != null) {
			return objectMapByUuid.get(uuid);
		}
		return null;
	}

	public CMGroup getGroupByUuid(String uuid) {
		loadObjectsIntoMap(false);
		if (groupMapByUuid != null) {
			return groupMapByUuid.get(uuid);
		}
		return null;
	}

	public CMGroup getGroupByPackageFullPath(String packageFullPath) {
		loadObjectsIntoMap(false);
		if (groupMapByPackagePath != null) {
			return groupMapByPackagePath.get(packageFullPath);
		}
		return null;
	}

	public void needReloadObjects() {
		groupMapByUuid = null;
	}

	public void loadObjectsIntoMap(boolean forceReload) {
		if (groupMapByUuid == null || forceReload) {
			groupMapByUuid = new HashMap<>();
			taskMapByUuid = new HashMap<>();
			deliverableMapByUuid = new HashMap<>();
			objectMapByUuid = new HashMap<>();
			groupMapByPackagePath = new HashMap<>();

			taskChilds = new HashMap<>();
			deliverableChilds = new HashMap<>();
			groupChilds = new HashMap<>();

			CoreUtils.putIntoMap(groups, groupMapByUuid);
			CoreUtils.putIntoMap(tasks, taskMapByUuid);
			CoreUtils.putIntoMap(deliverables, deliverableMapByUuid);
			CoreUtils.putIntoMap(groups, objectMapByUuid);
			CoreUtils.putIntoMap(tasks, objectMapByUuid);
			CoreUtils.putIntoMap(deliverables, objectMapByUuid);

			if (groups != null) {
				for (CMGroup obj : groups) {
					if (obj != null) {
						groupMapByUuid.put(obj.getUuid(), obj);
						objectMapByUuid.put(obj.getUuid(), obj);

						groupMapByPackagePath.put(obj.getPackageFullPath(), obj);

						if (isNotNullOrEmpty(obj.getGroupUuid())) {
							List<CMGroup> lst = groupChilds.get(obj.getGroupUuid());
							if (lst == null) {
								lst = new ArrayList<>();
								groupChilds.put(obj.getGroupUuid(), lst);
							}
							lst.add(obj);
						}
					}
				}
			}

			if (tasks != null) {
				for (CMTask obj : tasks) {
					if (obj != null) {
						taskMapByUuid.put(obj.getUuid(), obj);
						objectMapByUuid.put(obj.getUuid(), obj);

						if (isNotNullOrEmpty(obj.getGroupUuid())) {
							List<CMTask> lst = taskChilds.get(obj.getGroupUuid());
							if (lst == null) {
								lst = new ArrayList<>();
								taskChilds.put(obj.getGroupUuid(), lst);
							}
							lst.add(obj);
						}
					}
				}
			}

			if (deliverables != null) {
				for (CMDeliverable obj : deliverables) {
					if (obj != null) {
						deliverableMapByUuid.put(obj.getUuid(), obj);
						objectMapByUuid.put(obj.getUuid(), obj);

						if (isNotNullOrEmpty(obj.getGroupUuid())) {
							List<CMDeliverable> lst = deliverableChilds.get(obj.getGroupUuid());
							if (lst == null) {
								lst = new ArrayList<>();
								deliverableChilds.put(obj.getGroupUuid(), lst);
							}
							lst.add(obj);
						}
					}
				}
			}
		}
	}

	public List<Object> getChilds(IUuidObject o) {
		if (o != null) {
			return getChilds(o.getUuid());
		}
		return null;
	}

	public List<Object> getChilds(String parentUuid) {

		if (isNotNullOrEmpty(parentUuid)) {
			ArrayList<Object> arl = new ArrayList<>();

			Consumer<Map> addInList = map -> {

				if (map != null) {
					List lst = (List) map.get(parentUuid);
					if (lst != null) {
						arl.addAll(lst);
					}
				}
			};

			addInList.accept(groupChilds);
			addInList.accept(deliverableChilds);
			addInList.accept(taskChilds);

			return arl;
		}
		return null;
	}

	public List<CMGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<CMGroup> groups) {
		this.groups = groups;
	}

	public void throwInvalidInput() {
		ExceptionsUtils.throwDynamicException(WeelgoException.INVALID_INPUT);
	}

	public void throwDynamicException(String type) {
		ExceptionsUtils.throwDynamicException(type);
	}

	@Override
	public String getUuid() {
		return getModuleUniqueIdentifier();
	}

	@Override
	public void setUuid(String uuid) {

	}

	@Override
	public String getName() {
		if (rootGroup != null) {
			return rootGroup.getName();
		}
		return "";
	}

	@Override
	public void setName(String name) {

	}

}
