package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.json.v1.JSN_CMGroup;
import com.weelgo.chainmapping.core.json.v1.JSN_UniqueIdentifier;
import com.weelgo.chainmapping.core.json.v1.Populator;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.IUuidUpdateListProcessor;
import com.weelgo.core.ValidatorUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.core.exceptions.WeelgoException;

public class CMFileSystemDataSource extends CMGenericDataSource {

	private static Logger logger = LoggerFactory.getLogger(CMFileSystemDataSource.class);
	public static final String GROUP_DATA_FILE_NAME = "group_data.json";
	public static final String MODULE_DATA_FILE_NAME = "module_data.json";
	private Object rootFolder;
	private Map<String, String> elementToShow = new HashMap<>();

	public Object getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(Object rootFolder) {
		this.rootFolder = rootFolder;
	}

	@Override
	public List<Object> getContainers() {
		return getHierarchicalTreeSystemProvider().getChildFolders(rootFolder);
	}

	@Override
	public void load(IProgressMonitor progressMonitor, CMModuleService service) {
		Map<String, CMModuleService> servicesMap = CoreUtils.putObjectIntoMap(service);
		load(progressMonitor, servicesMap);
	}

	@Override
	public void load(IProgressMonitor progressMonitor, Map<String, CMModuleService> servicesMap) {
		assertNotNullOrEmptyFatal(servicesMap);
		elementToShow.clear();
		if (rootFolder != null) {
			if (getHierarchicalTreeSystemProvider().isFolderExist(rootFolder)) {
				Map<String, CMModuleService> loadedServices = new HashMap<>();
				load(rootFolder, null, null, loadedServices);

				// We have the services loaded
				for (Map.Entry<String, CMModuleService> entry : loadedServices.entrySet()) {
					String key = entry.getKey();
					CMModuleService val = entry.getValue();
					if (val != null) {
						val.check();
					}
				}

				// Now we need to update
				List<CMModuleService> oldServices = new ArrayList<>();

				oldServices.addAll(servicesMap.values());

				List<CMModuleService> newServices = new ArrayList<>();
				if (loadedServices != null) {
					newServices.addAll(loadedServices.values());
				}

				CMUpdateListProcessor<CMModuleService> servicesUpdator = new CMUpdateListProcessor<>(oldServices,
						newServices);
				servicesUpdator.compileList();
				List<CMModuleService> toRemove = servicesUpdator.getToRemoveElements();
				List<CMModuleService> toAdd = servicesUpdator.getNewElements();
				removeListFromMap(toRemove, servicesMap);
				putListIntoMap(toAdd, servicesMap);

				for (Map.Entry<String, CMModuleService> entry : servicesMap.entrySet()) {

					CMModuleService oldServ = entry.getValue();
					if (oldServ != null) {
						CMModuleService newServ = loadedServices.get(oldServ.getUuid());
						if (newServ != null) {

							newServ.updateObject(oldServ);
						}
					}
				}
			}
		}
	}

	public void load(Object folder, CMModuleService currentService, Map<String, List<String>> inputsMap,
			Map<String, CMModuleService> servicesMap) {
		forceMarkToShow(folder);
		Object dataFile = getHierarchicalTreeSystemProvider().getFile(folder, GROUP_DATA_FILE_NAME);

		// Si le fichier existe on le lit
		Object[] ob = loadGroupOrModule(dataFile, currentService, servicesMap, inputsMap);
		currentService = (CMModuleService) ob[0];
		inputsMap = (Map<String, List<String>>) ob[1];
		List<Object> childFolders = getHierarchicalTreeSystemProvider().getChildFolders(folder);

		if (childFolders != null) {
			for (Object child : childFolders) {
				if (child != null) {
					load(child, currentService, inputsMap, servicesMap);
				}
			}
		}

		if (inputsMap != null) {
			Map<String, CMLink> linksMap = new HashMap<>();
			// On créé les liens
			for (Map.Entry<String, List<String>> entry : inputsMap.entrySet()) {
				String output = entry.getKey();
				List<String> inputs = entry.getValue();
				if (CoreUtils.isNotNullOrEmpty(output) && inputs != null) {
					for (String input : inputs) {
						if (CoreUtils.isNotNullOrEmpty(input)) {
							CMLink lnk = new CMLink();
							lnk.setSourceUuid(input);
							lnk.setTargetUuid(output);
							CoreUtils.putObjectIntoMap(lnk, linksMap);
						}
					}
				}
			}
			if (currentService != null) {
				currentService.setLinks(CoreUtils.putMapIntoList(linksMap));
			}
		}

	}

	@Override
	public boolean isMine(Object o) {
		return super.isMine(o);
	}

	@Override
	public boolean isHiddenElement(Object elem) {
		if (elem != null) {

			if (getHierarchicalTreeSystemProvider().isFileSystemElement(elem) == false) {
				return false;
			}

			String id = getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(elem);
			if (isNotNullOrEmpty(id)) {
				if (elementToShow.containsKey(id)) {
					return false;
				}
			}
		}
		return true;
	}

	public void forceMarkToShow(Object elem) {
		if (elem != null) {
			String id = isMarkeableToSHow(elem);
			if (isNotNullOrEmpty(id)) {
				elementToShow.put(id, id);
			}
		}
	}

	public String isMarkeableToSHow(Object elem) {
		return null;
	}

	public void markToShow(Object elem) {
		if (elem != null) {
			String id = getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(elem);
			if (isNotNullOrEmpty(id)) {
				elementToShow.put(id, id);
			}

			markToShow(getHierarchicalTreeSystemProvider().getParentFolder(elem));

		}
	}

	public Object[] loadGroupOrModule(Object dataFile, CMModuleService currentService,
			Map<String, CMModuleService> servicesMap, Map<String, List<String>> inputsMap) {

		if (getHierarchicalTreeSystemProvider().isFileExist(dataFile)) {
			// Si le fichier existe on le lit
			try {
				JSN_CMGroup gp = getHierarchicalTreeSystemProvider().deserializeJsonFile(dataFile, JSN_CMGroup.class);
				if (gp != null) {
					CMGroup newGp = new CMGroup();
					Object[] ob = Populator.jsonToModelPopulator(gp, newGp, null, currentService, inputsMap);

					CMModuleService serTmp = (CMModuleService) ob[0];
					Map<String, List<String>> inputsMapTmp = (Map<String, List<String>>) ob[1];

					if (currentService == null || currentService.equals(serTmp) == false) {
						// Un nouveau service a été détecté
						currentService = serTmp;
						inputsMap = inputsMapTmp;
					}

					if (newGp.isModule()) {

						// If the group is a module, there is a file with the unique identifier
						Object parentFolder = getHierarchicalTreeSystemProvider().getParentFolder(dataFile);
						Object identifierFile = getHierarchicalTreeSystemProvider().getFile(parentFolder,
								MODULE_DATA_FILE_NAME);
						if (!getHierarchicalTreeSystemProvider().isFileExist(identifierFile)) {
							ExceptionsUtils.throwException("Module unique identifier can't be null or empty.");
						}
						JSN_UniqueIdentifier uniqueIdentifier = getHierarchicalTreeSystemProvider()
								.deserializeJsonFile(identifierFile, JSN_UniqueIdentifier.class);
						assertNotNullOrEmptyFatal(uniqueIdentifier, "Module unique identifier can't be null or empty.");

						String modUniqueIdentifier = uniqueIdentifier.getUnique_identifier();
						assertNotNullOrEmptyFatal(modUniqueIdentifier,
								"Module unique identifier can't be null or empty.");
						newGp.setModuleUniqueIdentifier(modUniqueIdentifier);
						// On a détecté un nouveau module, il faut créer un service

						currentService.setRootGroup(newGp);
						currentService.getGroups().add(newGp);
						currentService.setContainer(parentFolder);
						servicesMap.put(modUniqueIdentifier, currentService);

						markToShow(parentFolder);

					} else if (currentService != null) {

						currentService.getGroups().add(newGp);

					}

				}
			} catch (Exception e) {
				ExceptionsUtils.ManageException(e, logger);
			}
		}
		return new Object[] { currentService, inputsMap };
	}

	public class CMUpdateListProcessor<T extends IUuidObject> extends IUuidUpdateListProcessor<T> {

		public CMUpdateListProcessor(List<T> oldTagsArray, List<T> newTagsArray) {
			super(oldTagsArray, newTagsArray);

		}

		@Override
		public String generateUUid() {
			return "";
		}

	}

	@Override
	public void save(IProgressMonitor progressMonitor, CMModuleService moduleService) {

		if (moduleService != null) {
			save(progressMonitor, moduleService.getContainer(), moduleService);
		}
	}

	public void save(IProgressMonitor progressMonitor, Object moduleRootFolder, CMModuleService service) {
		try {

			if (!getHierarchicalTreeSystemProvider().isFolderExist(moduleRootFolder)) {
				ExceptionsUtils.throwException("Module folder doesn't exist.");
			}
			assertNotNullOrEmptyFatal(service);
			String uniqueIdentifier = service.getModuleUniqueIdentifier();
			assertNotNullOrEmptyFatal(uniqueIdentifier);

			JSN_UniqueIdentifier id = new JSN_UniqueIdentifier();
			id.setUnique_identifier(uniqueIdentifier);
			Object jsnFile = getHierarchicalTreeSystemProvider().getFile(moduleRootFolder, MODULE_DATA_FILE_NAME);
			getHierarchicalTreeSystemProvider().serialiseInJsonFile(jsnFile, id);

			Object moduleRootFolderParent = getHierarchicalTreeSystemProvider().getParentFolder(moduleRootFolder);
			if (!getHierarchicalTreeSystemProvider().isFolderExist(moduleRootFolderParent)) {
				ExceptionsUtils.throwException("Root folder doesn't exist.");
			}
			List<CMGroup> groups = service.getGroups();
			Map<String, CMGroup> map = new HashMap<>();
			if (groups != null) {
				for (CMGroup gp : groups) {
					if (gp != null) {
						String folderId = saveGroup(progressMonitor, moduleRootFolderParent, gp, service);
						if (CoreUtils.isNotNullOrEmpty(folderId)) {
							map.put(folderId, gp);
						}
					}
				}

			}
			removeNotUserFolders(moduleRootFolder, map);
			service.markServiceSaved();

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
	}

	public void removeNotUserFolders(Object folderToTest, Map<String, CMGroup> usedPath) {
		if (folderToTest != null) {
			String id = getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(folderToTest);
			boolean keep = false;
			if (usedPath != null) {
				keep = usedPath.containsKey(id);
			}
			if (keep == false) {
				// We check if there is module in subfolder
				keep = hasModuleInFolder(folderToTest);

				if (keep) {
					// If there is a module, we need to check if it's in this folder
					Object moduleFile = getHierarchicalTreeSystemProvider().getFile(folderToTest,
							MODULE_DATA_FILE_NAME);
					if (getHierarchicalTreeSystemProvider().isFile(moduleFile)
							&& getHierarchicalTreeSystemProvider().isFileExist(moduleFile)) {
						// We do nothing
					} else {
						// We remove the group file
						Object groupFile = getHierarchicalTreeSystemProvider().getFile(folderToTest,
								GROUP_DATA_FILE_NAME);
						if (getHierarchicalTreeSystemProvider().isFile(groupFile)
								&& getHierarchicalTreeSystemProvider().isFileExist(groupFile)) {
							getHierarchicalTreeSystemProvider().deleteFile(groupFile);
						}
					}
				}
			}
			if (keep == false) {
				getHierarchicalTreeSystemProvider().deleteFolder(folderToTest);
			} else {
				List<Object> childs = getHierarchicalTreeSystemProvider().getChildFolders(folderToTest);
				if (childs != null) {
					for (Object c : childs) {
						removeNotUserFolders(c, usedPath);
					}
				}
			}
		}
	}

	public boolean hasModuleInFolder(Object folder) {
		if (getHierarchicalTreeSystemProvider().isFolder(folder)) {
			Object moduleFile = getHierarchicalTreeSystemProvider().getFile(folder, MODULE_DATA_FILE_NAME);
			if (getHierarchicalTreeSystemProvider().isFile(moduleFile)
					&& getHierarchicalTreeSystemProvider().isFileExist(moduleFile)) {
				return true;
			}

			List<Object> childs = getHierarchicalTreeSystemProvider().getChildFolders(folder);
			if (childs != null) {
				for (Object c : childs) {
					boolean has = hasModuleInFolder(c);
					if (has) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public String saveGroup(IProgressMonitor progressMonitor, Object moduleRootFolder, CMGroup group,
			CMModuleService moduleServices) {
		try {

			assertNotNullOrEmptyFatal(moduleRootFolder);
			assertNotNullOrEmptyFatal(group);

			Object groupFolder = getFolderOfGroup(moduleRootFolder, group);
			getHierarchicalTreeSystemProvider().createFolder(groupFolder);

			if (!getHierarchicalTreeSystemProvider().isFolderExist(groupFolder)) {
				ExceptionsUtils.throwException("Folder doesn't exists.");
			}

			JSN_CMGroup jsn = new JSN_CMGroup();
			Populator.modelToJsonPopulator(group, jsn, moduleServices);
			Object jsnFile = getHierarchicalTreeSystemProvider().getFile(groupFolder, GROUP_DATA_FILE_NAME);
			getHierarchicalTreeSystemProvider().serialiseInJsonFile(jsnFile, jsn);
			return getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(groupFolder);

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
		return null;
	}

	@Override
	public boolean isModulePackageFree(IProgressMonitor progressMonitor, Object moduleParentFolder,
			String packageName) {
		packageName = cleanString(packageName);
		assertTrue(ValidatorUtils.isValidPackageName(packageName));
		assertNotNullOrEmpty(moduleParentFolder);

		Object moduleFolder = getHierarchicalTreeSystemProvider().getFolder(moduleParentFolder, packageName);
		if (getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder)) {
			return false;
		}
		return true;
	}

	@Override
	public Object createModule(IProgressMonitor progressMonitor, Object moduleParentFolder, CMModuleService service) {
		try {

			assertNotNullOrEmptyFatal(service.getRootGroup());
			String packageName = service.getRootGroup().getPackageName();
			assertNotNullOrEmptyFatal(service.getRootGroup().getModuleUniqueIdentifier());
			assertNotNullOrEmptyFatal(packageName);

			assertTrue(ValidatorUtils.isValidPackageName(packageName), WeelgoException.INVALID_PACKAGE_NAME);

			Object moduleFolder = getHierarchicalTreeSystemProvider().getFolder(moduleParentFolder, packageName);

			// Is a folder already exist, we check if it's a module folder. If not we delete
			// it because it's a group that has been deleted but not saved
			if (getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder)) {
				Object file = getHierarchicalTreeSystemProvider().getFile(moduleFolder, MODULE_DATA_FILE_NAME);
				assertFalse(getHierarchicalTreeSystemProvider().isFileExist(file),
						WeelgoException.MODULE_ALREADY_EXIST);
				getHierarchicalTreeSystemProvider().deleteFolder(moduleFolder);
			}
			assertFalse(getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder),
					WeelgoException.MODULE_ALREADY_EXIST);

			moduleFolder = getHierarchicalTreeSystemProvider().createFolder(moduleParentFolder, packageName);

			assertTrue(getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder),
					WeelgoException.OBJECT_NULL_OR_EMPTY);

			// On peut sauvegarder
			save(progressMonitor, moduleFolder, service);

			return moduleFolder;

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}

		return null;
	}
}
