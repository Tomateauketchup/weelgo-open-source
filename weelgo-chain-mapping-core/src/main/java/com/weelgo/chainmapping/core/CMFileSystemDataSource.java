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
	public void load(IProgressMonitor progressMonitor, Map<String, CMModuleService> servicesMap) {
		assertNotNullOrEmptyFatal(servicesMap);
		elementToShow.clear();
		if (rootFolder != null) {
			if (getHierarchicalTreeSystemProvider().isFolderExist(rootFolder)) {
				Map<String, CMModuleService> loadedServices = new HashMap<>();
				load(rootFolder, null, loadedServices);

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
				removeFromMap(toRemove, servicesMap);
				putIntoMap(toAdd, servicesMap);

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

	public void load(Object folder, CMModuleService currentService, Map<String, CMModuleService> servicesMap) {
		forceMarkToShow(folder);
		Object dataFile = getHierarchicalTreeSystemProvider().getFile(folder, GROUP_DATA_FILE_NAME);

		// Si le fichier existe on le lit
		currentService = loadGroupOrModule(dataFile, currentService, servicesMap);

		List<Object> childFolders = getHierarchicalTreeSystemProvider().getChildFolders(folder);

		if (childFolders != null) {
			for (Object child : childFolders) {
				if (child != null) {
					load(child, currentService, servicesMap);
				}
			}
		}

	}

	@Override
	public boolean isMine(Object o) {
		// TODO problème du isMine : Indique su la ressource est bien de ce file system.
		// Pour l'instant il n'y a que la workspace donc on retourne oui.
		return true;
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

	public CMModuleService loadGroupOrModule(Object dataFile, CMModuleService currentService,
			Map<String, CMModuleService> servicesMap) {

		if (getHierarchicalTreeSystemProvider().isFileExist(dataFile)) {
			// Si le fichier existe on le lit
			try {
				JSN_CMGroup gp = getHierarchicalTreeSystemProvider().deserializeJsonFile(dataFile, JSN_CMGroup.class);
				if (gp != null) {
					CMGroup newGp = new CMGroup();
					Populator.jsonToModelPopulator(gp, newGp);

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
						currentService = new CMModuleService();
						currentService.setRootGroup(newGp);
						currentService.getGroups().add(newGp);
						currentService.setParentContainer(parentFolder);
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
		return currentService;
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
			save(progressMonitor, moduleService.getParentContainer(), moduleService);
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

			moduleRootFolder = getHierarchicalTreeSystemProvider().getParentFolder(moduleRootFolder);
			if (!getHierarchicalTreeSystemProvider().isFolderExist(moduleRootFolder)) {
				ExceptionsUtils.throwException("Root folder doesn't exist.");
			}
			List<CMGroup> groups = service.getGroups();
			if (groups != null) {
				for (CMGroup gp : groups) {
					if (gp != null) {
						saveGroup(progressMonitor, moduleRootFolder, gp);
					}
				}
			}

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
	}

	public void saveGroup(IProgressMonitor progressMonitor, Object moduleRootFolder, CMGroup group) {
		try {

			assertNotNullOrEmptyFatal(moduleRootFolder);
			assertNotNullOrEmptyFatal(group);
			String packageParentPath = group.getPackageFullPath();
			// On décompose
			String[] parents = getPackages(packageParentPath);

			Object groupFolder = moduleRootFolder;
			if (parents != null) {
				for (String str : parents) {
					if (isNotNullOrEmpty(str)) {
						groupFolder = getHierarchicalTreeSystemProvider().createFolder(groupFolder, str);
					}
				}
			}
//			if (isFolderExist(groupFolder) == false) {
//				createFolder(groupFolder);
//			}
			if (!getHierarchicalTreeSystemProvider().isFolderExist(groupFolder)) {
				ExceptionsUtils.throwException("Folder doesn't exists.");
			}

			JSN_CMGroup jsn = new JSN_CMGroup();
			Populator.modelToJsonPopulator(group, jsn);
			Object jsnFile = getHierarchicalTreeSystemProvider().getFile(groupFolder, GROUP_DATA_FILE_NAME);
			getHierarchicalTreeSystemProvider().serialiseInJsonFile(jsnFile, jsn);

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
	}

	@Override
	public boolean isModulePackageFree(IProgressMonitor progressMonitor, Object moduleParentFolder,
			String packageName) {
		packageName = cleanString(packageName);
		assertTrue(ValidatorUtils.isValidPackageName(packageName));
		assertNotNullOrEmpty(moduleParentFolder);
		assertTrue(getHierarchicalTreeSystemProvider().isFolderExist(moduleParentFolder));
		Object moduleFolder = getHierarchicalTreeSystemProvider().getFolder(moduleParentFolder, packageName);
		if (getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder)) {
			return false;
		}
		return true;
	}

	@Override
	public void createModule(IProgressMonitor progressMonitor, Object moduleParentFolder, CMModuleService service) {
		try {

			assertNotNullOrEmptyFatal(service.getRootGroup());
			String packageName = service.getRootGroup().getPackageName();
			assertNotNullOrEmptyFatal(service.getRootGroup().getModuleUniqueIdentifier());
			assertNotNullOrEmptyFatal(packageName);

			assertTrue(getHierarchicalTreeSystemProvider().isFolderExist(moduleParentFolder));
			assertTrue(ValidatorUtils.isValidPackageName(packageName), WeelgoException.INVALID_PACKAGE_NAME);

			Object moduleFolder = getHierarchicalTreeSystemProvider().getFolder(moduleParentFolder, packageName);

			assertFalse(getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder),
					WeelgoException.MODULE_ALREADY_EXIST);

			moduleFolder = getHierarchicalTreeSystemProvider().createFolder(moduleParentFolder, packageName);

			assertTrue(getHierarchicalTreeSystemProvider().isFolderExist(moduleFolder),
					WeelgoException.OBJECT_NULL_OR_EMPTY);

			// On peut sauvegarder
			save(progressMonitor, moduleFolder, service);

		} catch (Exception e) {
			ExceptionsUtils.ManageException(e, logger);
		}
	}
}
