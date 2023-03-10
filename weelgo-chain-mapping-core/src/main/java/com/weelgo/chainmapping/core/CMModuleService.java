package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.assertNotNullOrEmpty;
import static com.weelgo.core.CoreUtils.assertNotNullOrEmptyFatal;
import static com.weelgo.core.CoreUtils.cleanString;
import static com.weelgo.core.CoreUtils.isNotNullOrEmpty;
import static com.weelgo.core.ValidatorUtils.checkGroupName;
import static com.weelgo.core.ValidatorUtils.checkPackageName;
import static com.weelgo.core.ValidatorUtils.checkTaskName;
import static com.weelgo.core.ValidatorUtils.checkNeedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.ICloneableObject;
import com.weelgo.core.INamedObject;
import com.weelgo.core.IUpdatablebject;
import com.weelgo.core.IUuidGenerator;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.UuidGenerator;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.core.exceptions.WeelgoException;
import com.weelgo.core.undoredo.IUndoRedoModelProvider;
import com.weelgo.core.undoredo.UndoRedoManager;

public class CMModuleService implements IUuidObject, INamedObject, IModuleUniqueIdentifierObject,
		ICloneableObject<CMModuleService>, IUpdatablebject<CMModuleService>, IUuidGenerator {

	private UndoRedoManager undoRedoManager = CMFactory.create(UndoRedoManager.class);
	private Object container;
	private String lastSaveDataFingerprint;
	private CMGroup rootGroup;
	private List<CMGroup> groups = new ArrayList<>();
	private List<CMTask> tasks = new ArrayList<>();
	private List<CMNeed> needs = new ArrayList<>();
	private List<CMLink> links = new ArrayList<>();
	private IUuidGenerator uuidGenerator = new UuidGenerator();

	private Map<String, IUuidObject> objectMapByUuid;
	private Map<String, CMGroup> groupMapByUuid;
	private Map<String, CMGroup> groupMapByPackagePath;
	private Map<String, CMTask> taskMapByUuid;
	private Map<String, CMNeed> needMapByUuid;
	private Map<String, CMLink> linkMapByUuid;
	private Map<String, List<CMTask>> taskChilds;
	private Map<String, List<CMNeed>> needChilds;
	private Map<String, List<CMGroup>> groupChilds;
	private Map<String, List<CMNode>> inputs;
	private Map<String, List<CMNode>> outputs;
	private Map<String, List<CMLink>> incomingLinks;
	private Map<String, List<CMLink>> outgoingLinks;

	@Override
	public void populateObject(CMModuleService toPopulate) {
		if (toPopulate != null) {
			toPopulate.setGroups(CoreUtils.cloneList(groups));
			toPopulate.setTasks(CoreUtils.cloneList(tasks));
			toPopulate.setLinks(CoreUtils.cloneList(links));
			toPopulate.setNeeds(CoreUtils.cloneList(needs));

			Map map = CoreUtils.putListIntoMap(groups);
			if (rootGroup != null) {
				CMGroup root = (CMGroup) map.get(rootGroup.getUuid());
				toPopulate.setRootGroup(root);
			}
		}
	}

	@Override
	public void updateObject(CMModuleService objectToUpdate) {
		if (objectToUpdate != null) {
			objectToUpdate.needReloadObjects();

			// On update uniquement les données, pas le parent container ni le last save

			CoreUtils.updateList(objectToUpdate.groups, groups);
			CoreUtils.updateList(objectToUpdate.tasks, tasks);
			CoreUtils.updateList(objectToUpdate.links, links);
			CoreUtils.updateList(objectToUpdate.needs, needs);
		}
	}

	public CMModuleService() {
		IUndoRedoModelProvider<CMModuleService> undoRedoProvider = new IUndoRedoModelProvider<CMModuleService>() {

			@Override
			public void pushToModel(CMModuleService o) {
				if (o != null) {
					o = o.cloneObject();
					o.updateObject(getThisModuleService());
				}
			}

			@Override
			public CMModuleService getClonedModel() {
				return CoreUtils.cloneObject(getThisModuleService());
			}

			@Override
			public void modelSaved() {
				if (!CoreUtils.isNotNullOrEmpty(lastSaveDataFingerprint)) {
					markServiceSaved();
				}
			}
		};
		undoRedoManager.setUndoRedoModelProvider(undoRedoProvider);
	}

	public void needReloadObjects() {
		groupMapByUuid = null;
	}

	public void needReloadInputsOutputs() {
		inputs = null;
		outputs = null;
		incomingLinks = null;
		outgoingLinks = null;
	}

	public void loadObjectsIntoMap(boolean forceReload) {
		if (groupMapByUuid == null || forceReload) {
			groupMapByUuid = new HashMap<>();
			taskMapByUuid = new HashMap<>();
			objectMapByUuid = new HashMap<>();
			groupMapByPackagePath = new HashMap<>();
			linkMapByUuid = new HashMap<>();
			needMapByUuid = new HashMap<>();

			taskChilds = new HashMap<>();
			needChilds = new HashMap<>();
			groupChilds = new HashMap<>();

			inputs = null;
			outputs = null;
			incomingLinks = null;
			outgoingLinks = null;

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

			if (needs != null) {
				for (CMNeed obj : needs) {
					if (obj != null) {
						needMapByUuid.put(obj.getUuid(), obj);
						objectMapByUuid.put(obj.getUuid(), obj);

						if (isNotNullOrEmpty(obj.getGroupUuid())) {
							List<CMNeed> lst = needChilds.get(obj.getGroupUuid());
							if (lst == null) {
								lst = new ArrayList<>();
								needChilds.put(obj.getGroupUuid(), lst);
							}
							lst.add(obj);
						}
					}
				}
			}

			if (links != null) {
				for (CMLink o : links) {
					if (o != null) {
						linkMapByUuid.put(o.getUuid(), o);
						objectMapByUuid.put(o.getUuid(), o);
					}
				}
			}
		}
	}

	public void loadInputOutputs() {
		loadObjectsIntoMap(false);

		if (inputs == null || outputs == null) {
			incomingLinks = new HashMap<>();
			outgoingLinks = new HashMap<>();
			HashMap<String, List<CMNode>> temporaryInputs = new HashMap<>();
			HashMap<String, List<CMNode>> temporaryOutputs = new HashMap<>();

			Map<String, Map<String, CMNode>> inputsTmp = new HashMap<>();
			Map<String, Map<String, CMNode>> outputsTmp = new HashMap<>();

			if (links != null) {
				for (CMLink link : links) {
					if (link != null) {

						CMNode src = getObjectByUuid(link.getSourceUuid());
						CMNode trg = getObjectByUuid(link.getTargetUuid());

						if (trg != null && src != null) {
							Map<String, CMNode> inputs2 = inputsTmp.get(trg.getUuid());
							if (inputs2 == null) {
								inputs2 = new HashMap<>();
								inputsTmp.put(trg.getUuid(), inputs2);
							}
							inputs2.put(src.getUuid(), src);

							Map<String, CMNode> outputs2 = outputsTmp.get(src.getUuid());
							if (outputs2 == null) {
								outputs2 = new HashMap<>();
								outputsTmp.put(src.getUuid(), outputs2);
							}
							outputs2.put(trg.getUuid(), trg);
						}

					}
				}
			}

			for (Map.Entry<String, Map<String, CMNode>> entry : inputsTmp.entrySet()) {
				String key = entry.getKey();
				Map<String, CMNode> map = entry.getValue();
				ArrayList<CMNode> arl = new ArrayList<>();
				arl.addAll(map.values());
				temporaryInputs.put(key, arl);
				ArrayList<CMLink> arl2 = new ArrayList<>();
				for (CMNode cmNode : arl) {
					String uuid = CoreUtils.getLinkUUID(cmNode.getUuid(), key);
					CMLink lnk = getLinkByUuid(uuid);
					if (lnk != null) {
						arl2.add(lnk);
					}
				}
				incomingLinks.put(key, arl2);
			}

			for (Map.Entry<String, Map<String, CMNode>> entry : outputsTmp.entrySet()) {
				String key = entry.getKey();
				Map<String, CMNode> map = entry.getValue();
				ArrayList<CMNode> arl = new ArrayList<>();
				arl.addAll(map.values());
				temporaryOutputs.put(key, arl);

				ArrayList<CMLink> arl2 = new ArrayList<>();
				for (CMNode cmNode : arl) {
					String uuid = CoreUtils.getLinkUUID(key, cmNode.getUuid());
					CMLink lnk = getLinkByUuid(uuid);
					if (lnk != null) {
						arl2.add(lnk);
					}
				}
				outgoingLinks.put(key, arl2);
			}
			inputs = temporaryInputs;
			outputs = temporaryOutputs;

		}
	}

	public List<CMLink> getInputOrOutputLinks(CMNode node, boolean isInput) {
		if (node != null) {
			loadInputOutputs();
			if (incomingLinks != null && outgoingLinks != null) {
				if (isInput) {
					return incomingLinks.get(node.getUuid());
				} else {
					return outgoingLinks.get(node.getUuid());
				}
			}
		}
		return null;
	}

	public List<CMNode> getInputOrOutput(CMNode node, boolean isInput) {
		if (node != null) {
			loadInputOutputs();
			if (inputs != null && outputs != null) {
				if (isInput) {
					return inputs.get(node.getUuid());
				} else {
					return outputs.get(node.getUuid());
				}
			}
		}
		return null;
	}

	public List<CMNode> getInputElements(CMNode node) {
		if (node != null) {
			return getInputOrOutput(node, true);
		}
		return null;
	}

	public List<CMNode> getOutputElements(CMNode node) {
		if (node != null) {
			return getInputOrOutput(node, false);
		}
		return null;
	}

	public List<CMLink> getInputLinks(CMNode node) {
		if (node != null) {
			return getInputOrOutputLinks(node, true);
		}
		return null;
	}

	public List<CMLink> getOutputLinks(CMNode node) {
		if (node != null) {
			return getInputOrOutputLinks(node, false);
		}
		return null;
	}

	public CMModuleService getThisModuleService() {
		return this;
	}

	public static CMModuleService createModule(String name) {
		CMGroup gp = new CMGroup();
		gp.setName(name);

		CMModuleService module = new CMModuleService();
		module.setRootGroup(gp);

		return module;
	}

	@Override
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
		if (tasks != null) {
			for (CMTask tk : tasks) {
				checkTask(tk);
			}
		}

		if (needs != null) {
			for (CMNeed tk : needs) {
				checkNeed(tk);
			}
		}

		// TODO il faut faire les vérification de noms et autres ici

		needReloadObjects();
	}

	public void checkTask(CMTask tsk) {
		if (tsk != null) {
			tsk.setModuleUniqueIdentifier(rootGroup.getModuleUniqueIdentifier());
		}
	}

	public void checkNeed(CMNeed tsk) {
		if (tsk != null) {
			tsk.setModuleUniqueIdentifier(rootGroup.getModuleUniqueIdentifier());
		}
	}

	public void checkGroup(CMGroup gp) {
		if (gp != null) {
			gp.setModuleUniqueIdentifier(rootGroup.getModuleUniqueIdentifier());
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

	public Object getContainer() {
		return container;
	}

	public void setContainer(Object parentContainer) {
		this.container = parentContainer;
	}

	public String findNameForNewNeed(String parentGroupUuid) {
		CMGroup gp = getGroupByUuid(parentGroupUuid);
		if (gp != null) {
			List<CMNeed> childs = getNeedChilds(parentGroupUuid);
			Map<String, CMNeed> map = new HashMap<>();
			if (childs != null) {
				for (CMNeed cmTask : childs) {
					if (cmTask != null) {
						map.put(cmTask.getName(), cmTask);
					}
				}
			}

			String nameTmp = "";
			int index = 0;
			boolean stop = false;
			do {

				index++;
				nameTmp = "New need " + index;

			} while (map.containsKey(nameTmp));

			return nameTmp;
		}
		return "";
	}

	public String findNameForNewTask(String parentGroupUuid) {
		CMGroup gp = getGroupByUuid(parentGroupUuid);
		if (gp != null) {
			List<CMTask> childs = getTaskChilds(parentGroupUuid);
			Map<String, CMTask> map = new HashMap<>();
			if (childs != null) {
				for (CMTask cmTask : childs) {
					if (cmTask != null) {
						map.put(cmTask.getName(), cmTask);
					}
				}
			}

			String nameTmp = "";
			int index = 0;
			boolean stop = false;
			do {

				index++;
				nameTmp = "New task " + index;

			} while (map.containsKey(nameTmp));

			return nameTmp;
		}
		return "";
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

	public CMGroup createGroup(CMModulesManager mm, String name, String packageName, String parentGroupUuid) {
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
		gp.setUuid(generateUuid());
		gp.setName(name);
		gp.setPackageName(packageName);
		gp.setPackageParentPath(createPackageParentPath(parent));
		gp.setGroupUuid(parentGroupUuid);
		gp.setType(CMGroup.TYPE_GROUP);
		checkGroup(gp);

		// We check if there is a folder of an other module
		// It's possible there is already a folder corresponding to an old load not yet
		// saved

		CMGenericDataSource ds = mm.getDataSourceOfModuleService(this);
		Object parentFolder = ds.getHierarchicalTreeSystemProvider().getParentFolder(getContainer());
		Object groupFolder = ds.getFolderOfGroup(parentFolder, gp);
		CMModuleService ser = mm.findModuleService(groupFolder);
		if (ser != null
				&& !CoreUtils.isStrictlyEqualsString(ser.getModuleUniqueIdentifier(), getModuleUniqueIdentifier())) {
			throwDynamicException(WeelgoException.MODULE_ALREADY_EXIST);
		}

//				ds.isModulePackageFree(null, parentGroupUuid, packageName)

		getGroups().add(gp);

		needReloadObjects();

		return gp;
	}

	public List<CMNode> moveNodes(List<CMNode> lst) {
		List<CMNode> arl = new ArrayList<>();

		if (lst != null) {
			for (CMNode n : lst) {
				if (n != null) {
					CMNode realNode = getObjectByUuid(n.getUuid());
					if (realNode != null) {
						realNode.setPositionX(n.getPositionX());
						realNode.setPositionY(n.getPositionY());
						arl.add(realNode);
					}
				}
			}
		}
		return arl;
	}

	public List<CMNode> modifyNodeNamePosition(List<CMNode> lst) {
		List<CMNode> arl = new ArrayList<>();

		if (lst != null) {
			for (CMNode n : lst) {
				if (n != null) {
					CMNode realNode = getObjectByUuid(n.getUuid());
					if (realNode != null) {
						String nameposition = n.getNamePosition();
						if (!CMNode.NAME_BOTTOM.equals(nameposition) && !CMNode.NAME_TOP.equals(nameposition)
								&& !CMNode.NAME_RIGHT.equals(nameposition) && !CMNode.NAME_LEFT.equals(nameposition)) {
							throwInvalidInput();
						}
						realNode.setNamePosition(nameposition);
						arl.add(realNode);
					}
				}
			}
		}
		return arl;
	}

	public void removeNodes(String[] nodesUuid) {
		if (nodesUuid != null) {
			for (String uuid : nodesUuid) {
				CoreUtils.removeObjectFromList(uuid, tasks);
			}
		}

		needReloadObjects();
	}

	public CMTask modifyTaskName(String taskName, String taskUuid) {
		taskName = cleanString(taskName);
		checkTaskName(taskName);

		CMTask task = getObjectByUuid(taskUuid);
		assertNotNullOrEmpty(task);
		task.setName(taskName);

		checkTask(task);
		needReloadObjects();

		return task;
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

		String taskUuid = generateUuid();
		// We check if there is already a task with same UUID
		if (getObjectByUuid(taskUuid) != null) {
			throwDynamicException(WeelgoException.OBJECT_ALREADY_EXIST);
		}

		CMTask task = CMFactory.create(CMTask.class);
		task.setGroupUuid(gp.getUuid());
		task.setName(taskName);
		task.setUuid(taskUuid);
		task.setPositionX(posX);
		task.setPositionY(posY);
		task.setModuleUniqueIdentifier(getModuleUniqueIdentifier());

		tasks.add(task);

		checkTask(task);
		needReloadObjects();

		return task;
	}

	public CMNeed createNeed(String name, String parentGroupUuid, int posX, int posY) {
		name = cleanString(name);
		parentGroupUuid = cleanString(parentGroupUuid);
		checkNeedName(name);

		CMGroup gp = rootGroup;

		if (isNotNullOrEmpty(parentGroupUuid)) {
			// We check the group exist
			CMGroup gpTmp = getGroupByUuid(parentGroupUuid);
			if (gpTmp != null) {
				gp = gpTmp;
			}
		}

		String taskUuid = generateUuid();
		// We check if there is already a task with same UUID
		if (getObjectByUuid(taskUuid) != null) {
			throwDynamicException(WeelgoException.OBJECT_ALREADY_EXIST);
		}

		CMNeed o = CMFactory.create(CMNeed.class);
		o.setGroupUuid(gp.getUuid());
		o.setName(name);
		o.setUuid(taskUuid);
		o.setPositionX(posX);
		o.setPositionY(posY);
		o.setModuleUniqueIdentifier(getModuleUniqueIdentifier());

		needs.add(o);

		checkNeed(o);
		needReloadObjects();

		return o;
	}

	public <T> T getObjectByUuid(String uuid) {
		loadObjectsIntoMap(false);
		if (objectMapByUuid != null) {
			return (T) objectMapByUuid.get(uuid);
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

	public <T extends IUuidObject> T getObject(T o) {
		loadObjectsIntoMap(false);
		if (o != null) {
			return (T) getObjectByUuid(o.getUuid());
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

	public CMLink linkNodes(String sourceUuid, String targetUuid) {

		loadObjectsIntoMap(false);
		CMNode source = getObjectByUuid(sourceUuid);
		CMNode target = getObjectByUuid(targetUuid);
		if (source != null && target != null) {
			CMLink lnk = new CMLink();
			lnk.setSourceUuid(source.getUuid());
			lnk.setTargetUuid(target.getUuid());
			if (getLinkByUuid(lnk.getUuid()) == null) {
				links.add(lnk);
				if (linkMapByUuid != null) {
					linkMapByUuid.put(lnk.getUuid(), lnk);
				}
				needReloadObjects();
			}
			return lnk;
		}
		return null;
	}

	// TODO faire la vérification de boucle sur le réseau

	public CMLink getLinkByUuid(String uuid) {
		loadObjectsIntoMap(false);
		if (uuid != null && !uuid.isEmpty() && linkMapByUuid != null) {
			return linkMapByUuid.get(uuid);
		}
		return null;
	}

	public List<Object> getChilds(IUuidObject o) {
		if (o != null) {
			return getChilds(o.getUuid());
		}
		return null;
	}

	public List<Object> getChilds(String parentUuid) {

		if (isNotNullOrEmpty(parentUuid)) {
			loadObjectsIntoMap(false);
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
			addInList.accept(taskChilds);
			addInList.accept(needChilds);

			return arl;
		}
		return null;
	}

	public List<CMGroup> getGroupChilds(String parentUuid) {
		if (isNotNullOrEmpty(parentUuid)) {
			loadObjectsIntoMap(false);
			return groupChilds.get(parentUuid);
		}
		return null;
	}

	public List<CMTask> getTaskChilds(String parentUuid) {
		if (isNotNullOrEmpty(parentUuid)) {
			loadObjectsIntoMap(false);
			return taskChilds.get(parentUuid);
		}
		return null;
	}

	public List<CMNeed> getNeedChilds(String parentUuid) {
		if (isNotNullOrEmpty(parentUuid)) {
			loadObjectsIntoMap(false);
			return needChilds.get(parentUuid);
		}
		return null;
	}

	public boolean isDirty() {
		if (getUndoRedoManager() != null) {
			String currentDataFingerprint = getUndoRedoManager().getCurrentNodeDataFingerprint();

			if (CoreUtils.isNotNullOrEmpty(currentDataFingerprint)) {
				return !CoreUtils.isStrictlyEqualsString(currentDataFingerprint, lastSaveDataFingerprint);
			}
		}

		return true;
	}

	public void markServiceSaved() {
		if (getUndoRedoManager() != null) {
			String currentDataFingerprint = getUndoRedoManager().getCurrentNodeDataFingerprint();
			lastSaveDataFingerprint = currentDataFingerprint;
		}
	}

	public String getLastSaveDataFingerprint() {
		return lastSaveDataFingerprint;
	}

	public void setLastSaveDataFingerprint(String lastSaveDataFingerprint) {
		this.lastSaveDataFingerprint = lastSaveDataFingerprint;
	}

	@Override
	public CMModuleService createThisObject() {
		return CMFactory.create(CMModuleService.class);
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

	public List<CMTask> getTasks() {
		return tasks;
	}

	public void setTasks(List<CMTask> tasks) {
		this.tasks = tasks;
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

	@Override
	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {

	}

	public UndoRedoManager getUndoRedoManager() {
		return undoRedoManager;
	}

	public void setUndoRedoManager(UndoRedoManager undoRedoManager) {
		this.undoRedoManager = undoRedoManager;
	}

	@Override
	public String toString() {
		return getName();
	}

	public IUuidGenerator getUuidGenerator() {
		return uuidGenerator;
	}

	public void setUuidGenerator(IUuidGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Override
	public String generateUuid() {
		return uuidGenerator.generateUuid();
	}

	@Override
	public String generateUuid(int length) {
		return uuidGenerator.generateUuid(length);
	}

	public List<CMLink> getLinks() {
		return links;
	}

	public void setLinks(List<CMLink> links) {
		this.links = links;
	}

	public List<CMNeed> getNeeds() {
		return needs;
	}

	public void setNeeds(List<CMNeed> needs) {
		this.needs = needs;
	}

}
