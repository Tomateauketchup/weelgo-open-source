package com.weelgo.chainmapping.core;

import static com.weelgo.core.CoreUtils.assertNotNullOrEmpty;
import static com.weelgo.core.CoreUtils.cleanString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.weelgo.core.Constants;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.IUuidObject;
import com.weelgo.core.exceptions.WeelgoException;

public class CMModulesManager implements HierarchicalTreeSystemNavProvider {

	public List<CMGenericDataSource> sources = new ArrayList<>();
	public List<CMModuleService> services = new ArrayList<>();
	public Map<String, CMModuleService> servicesMap = new HashMap<>();

	public void loadAllModules(IProgressMonitor progressMonitor) {
		Map<String, CMModuleService> servicesMapTmp = CoreUtils.putListIntoMap(services);

		if (sources != null) {
			for (CMGenericDataSource src : sources) {
				if (src != null) {
					src.load(progressMonitor, servicesMapTmp);
				}
			}
		}
		services.clear();
		services.addAll(servicesMapTmp.values());
		servicesMapTmp.clear();
		CoreUtils.putListIntoMap(services, servicesMap);
	}

	public void loadModule(String moduleUniqueIdentifier, IProgressMonitor progressMonitor) {
		CMModuleService ser = getServiceByModuleUniqueIdentifierId(moduleUniqueIdentifier);
		CMGenericDataSource src = getDataSourceOfModuleService(ser);
		if (src != null) {
			src.load(progressMonitor, ser);
		}

	}

	public CMGenericDataSource getDataSourceOfModuleService(CMModuleService ser) {
		if (ser != null) {
			return (CMGenericDataSource) loopOnSourcesAndGetMine(ser, t -> {
				return t;
			});
		}
		return null;
	}

	public CMReturnObj getObjectByUuid(IUuidObject obj) {
		if (obj != null && CoreUtils.isNotNullOrEmpty(obj.getUuid())) {
			return getObjectByUuid(obj.getUuid());
		}
		return null;
	}

	public CMGenericDataSource findDataSource(Object o) {
		if (sources != null) {
			for (CMGenericDataSource src : sources) {
				if (src != null && src.isMine(o)) {
					return src;
				}
			}
		}
		return null;
	}

	public CMModuleService findModuleService(Object o) {

		if (o != null && o instanceof IModuleUniqueIdentifierObject) {
			IModuleUniqueIdentifierObject ob = (IModuleUniqueIdentifierObject) o;
			return getServiceByModuleUniqueIdentifierId(ob.getModuleUniqueIdentifier());
		}

		CMModuleService ser = findServiceFromResourceObject(o);
		if (ser != null) {
			return ser;
		}

		return null;
	}

	public String findModuleUniqueIdentifierId(Object o) {
		CMModuleService ser = findModuleService(o);
		if (ser != null) {
			return ser.getModuleUniqueIdentifier();
		}
		return "";
	}

	public CMModuleService getServiceByModuleUniqueIdentifierId(String id) {
		if (servicesMap != null && CoreUtils.isNotNullOrEmpty(id)) {
			return servicesMap.get(id);
		}
		return null;
	}

	public CMReturnObj getObjectByUuid(String uuid) {
		for (CMModuleService ser : services) {
			if (ser != null) {
				Object o = ser.getObjectByUuid(uuid);
				if (o != null) {
					CMReturnObj ret = new CMReturnObj();
					ret.service = ser;
					ret.object = o;
					return ret;
				}
			}
		}
		return null;
	}

	public void saveModule(IProgressMonitor progressMonitor, String moduleUniqueIdentifier) {
		CMModuleService srv = getServiceByModuleUniqueIdentifierId(moduleUniqueIdentifier);
		if (srv != null) {
			if (sources != null) {
				for (CMGenericDataSource src : sources) {
					if (src != null && src.isMine(srv)) {
						src.save(progressMonitor, srv);
						break;
					}
				}
			}
		}
	}

	public void saveAll(IProgressMonitor progressMonitor) {
		if (services != null) {
			for (CMModuleService ser : services) {
				if (ser != null) {
					saveModule(progressMonitor, ser.getModuleUniqueIdentifier());
				}
			}
		}
	}

	public Object getFolderOfGroup(CMModuleService ser, CMGroup group) {
		if (ser != null && group != null) {
			CMGenericDataSource ds = getDataSourceOfModuleService(ser);
			if (ds != null) {
				return ds.getFolderOfGroup(ser, group);
			}
		}
		return null;
	}

	public String[] findNameForNewGroup(CMModuleService ser, String parentGroupUuid) {
		if (ser != null) {
			parentGroupUuid = cleanString(parentGroupUuid);
			assertNotNullOrEmpty(parentGroupUuid);

			CMGenericDataSource ds = getDataSourceOfModuleService(ser);
			if (ds != null) {
				CMGroup p = ser.getGroupByUuid(parentGroupUuid);
				if (p != null) {
					Object gpFolder = getFolderOfGroup(ser, p);
					if (gpFolder != null) {

						int index = 1;
						String baseName = "New Group";
						String baseNamePackage = "new_group";

						String bn = "";
						String pk = "";
						boolean continueLoop = true;
						do {

							bn = baseName + " " + index;
							pk = baseNamePackage + "_" + index;

							index++;

							continueLoop = ser.isPackageAlreadyExists(pk, parentGroupUuid);
							if (continueLoop == false) {
								// On doit regarder si il existe pas également un module dans le groupe
								Object newGroupFolder = ds.getHierarchicalTreeSystemProvider().getFolder(gpFolder, pk);
								CMModuleService serTmp = findModuleService(newGroupFolder);
								if (serTmp != null
										&& CoreUtils.isStrictlyEqualsString(serTmp.getModuleUniqueIdentifier(),
												ser.getModuleUniqueIdentifier()) == false) {
									continueLoop = true;
								}
							}

						} while (continueLoop);

						return new String[] { bn, pk };
					}
				}
			}
		}
		return null;

	}

	public List<CMGenericDataSource> getSources() {
		return sources;
	}

	public void setSources(List<CMGenericDataSource> sources) {
		this.sources = sources;
	}

	public List<CMModuleService> getServices() {
		return services;
	}

	public void setServices(List<CMModuleService> services) {
		this.services = services;
	}

	public String getFolderFullPathOfObject(Object o) {
		if (o != null) {
			if (o instanceof CMGroup) {
				CMGroup gp = (CMGroup) o;
				CMModuleService serv = findModuleService(gp);
				if (serv != null) {
					CMGenericDataSource ds = getDataSourceOfModuleService(serv);
					if (ds != null) {
						Object modRootFolder = serv.getContainer();

						modRootFolder = ds.getHierarchicalTreeSystemProvider().getParentFolder(modRootFolder);
						Object folder = ds.getFolderOfGroup(modRootFolder, gp);
						if (folder != null) {
							return ds.getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(folder);
						}
					}
				}
			}
		}
		return null;
	}

	public String createModule(IProgressMonitor progressMonitor, CMGenericDataSource ds,
			Object moduleParentFolderOrCMGroup, String moduleName, String modulePackageName) {

		Object moduleParentFolder = moduleParentFolderOrCMGroup;

		if (moduleParentFolderOrCMGroup != null && moduleParentFolderOrCMGroup instanceof CMGroup) {
			CMGroup gp = (CMGroup) moduleParentFolderOrCMGroup;
			CMModuleService serv = findModuleService(gp);
			if (serv != null) {
				// We check if there is a groupwith same name in model but not yet saved
				CoreUtils.assertFalse(serv.isPackageAlreadyExists(modulePackageName, gp.getUuid()),
						WeelgoException.GROUP_ALREADY_EXIST);
				Object modRootFolder = serv.getContainer();
				modRootFolder = ds.getHierarchicalTreeSystemProvider().getParentFolder(modRootFolder);
				moduleParentFolder = ds.getFolderOfGroup(modRootFolder, gp);
			}
		}

		CoreUtils.assertNotNullOrEmpty(moduleParentFolder);

		CMModuleService service = new CMModuleService();
		CMGroup gp = new CMGroup();
		service.setRootGroup(gp);
		service.getGroups().add(gp);
		gp.setModuleUniqueIdentifier(CoreUtils.generateUUIDString());
		gp.setName(moduleName);
		gp.setPackageName(modulePackageName);
		gp.setType(CMGroup.TYPE_MODULE);
		service.check();

		Object moduleFolder = ds.createModule(progressMonitor, moduleParentFolder, service);
		service.setContainer(moduleFolder);
		services.add(service);
		CoreUtils.putObjectIntoMap(service, servicesMap);
		loadModule(service.getModuleUniqueIdentifier(), progressMonitor);

		return service.getModuleUniqueIdentifier();
	}

	public boolean isModulePackageFree(IProgressMonitor progressMonitor, CMGenericDataSource ds,
			Object moduleParentFolderOrCMGroup, String packageName) {
		if (ds != null) {

			Object moduleParentFolder = moduleParentFolderOrCMGroup;

			if (moduleParentFolderOrCMGroup instanceof CMGroup) {
				CMGroup gp = (CMGroup) moduleParentFolderOrCMGroup;
				CMModuleService serOfGroup = findModuleService(moduleParentFolder);
				if (serOfGroup != null) {
					// We must find the folder corresponding to this group
					Object parent = ds.getHierarchicalTreeSystemProvider().getParentFolder(serOfGroup.getContainer());
					Object groupFolder = ds.getFolderOfGroup(parent, gp);
					if (groupFolder != null) {
						moduleParentFolder = groupFolder;
					}
				}

			}

			// We check on file system
			boolean isFree = ds.isModulePackageFree(progressMonitor, moduleParentFolder, packageName);
			if (isFree) {
				// We check if there is some packages on CM group that are not yet saved
				// We go throug parent tu discover the first module

				Object newModuleRootFolder = ds.getHierarchicalTreeSystemProvider().getFolder(moduleParentFolder,
						packageName);

				CMGroup possibleGp = findGroup(newModuleRootFolder);
				if (possibleGp != null) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public CMGroup findGroup(Object folder) {
		CMModuleService ser = findModuleService(folder);
		if (ser != null) {
			CMGenericDataSource ds = findDataSource(ser);
			if (ds != null) {
				Object serviceRootFolder = ser.getContainer();
				Object newModuleRootFolder = folder;

				Object folderTmp = newModuleRootFolder;

				// To have the package we need t get the delta path
				String possibleGroupPackage = ds.getHierarchicalTreeSystemProvider().getName(folderTmp);
				boolean stop = false;
				do {

					folderTmp = ds.getHierarchicalTreeSystemProvider().getParentFolder(folderTmp);
					if (folderTmp == null) {
						stop = true;
					} else {
						String name = ds.getHierarchicalTreeSystemProvider().getName(folderTmp);
						possibleGroupPackage = name + Constants.UUID_PACKAGE_SEPARATOR + possibleGroupPackage;

						if (ds.getHierarchicalTreeSystemProvider().isSameFolder(folderTmp, serviceRootFolder)) {
							stop = true;
						}
					}

				} while (stop == false);

				CMGroup gp = ser.getGroupByPackageFullPath(possibleGroupPackage);
				return gp;
			}
		}
		return null;
	}

	public CMFileSystemDataSource createCMFileSystemDataSource() {
		return new CMFileSystemDataSource();
	}

	public CMModuleService findServiceFromResourceObject(Object o) {
		CMModuleService ser = getCorrespondingModuleServiceOfFolder(o);
		if (ser != null) {
			return ser;
		}
		Object parent = getParentFolder(o);
		if (parent == null) {
			return null;
		} else if (isRootContainerOfDataSource(parent) != null) {
			// We stop here the digging because next is root
			return getCorrespondingModuleServiceOfFolder(parent);
		} else {
			return findServiceFromResourceObject(parent);
		}
	}

	public CMModuleService getCorrespondingModuleServiceOfFolder(Object o) {
		if (o != null && services != null) {
			for (CMModuleService ser : services) {
				if (ser != null && ser.getContainer() != null && isSameFolder(o, ser.getContainer())) {
					return ser;
				}
			}
		}
		return null;
	}

	public CMGenericDataSource isRootContainerOfDataSource(Object o) {
		if (sources != null) {
			for (CMGenericDataSource src : sources) {
				if (src != null && src.getContainers() != null) {
					if (src.getContainers().contains(o)) {
						return src;
					}
				}
			}
		}
		return null;
	}

	public List<Object> getChildsForTreeNavigator(CMGroup gp) {
		if (gp != null) {
			ArrayList<Object> arl = new ArrayList<>();
			// We gen normal groupd chiilds
			CMModuleService ser = findModuleService(gp);
			if (ser != null) {
				List<Object> lst = ser.getChilds(gp);
				CoreUtils.putListIntoList(lst, arl);
			}

			// We go check if there is any module to load
			CMGenericDataSource ds = getDataSourceOfModuleService(ser);
			Object folderOfGroup = ds.getFolderOfGroup(ser, gp);
			List<Object> childs = ds.getHierarchicalTreeSystemProvider().getChildFolders(folderOfGroup);
			if (childs != null) {
				for (Object c : childs) {
					CMModuleService serTm = findModuleService(c);
					if (serTm != null && CoreUtils.isStrictlyEqualsString(serTm.getModuleUniqueIdentifier(),
							ser.getModuleUniqueIdentifier()) == false) {
						// Module detected
						arl.add(serTm);
					}
				}
			}
			return arl;
		}
		return null;
	}

	public Object getParentForTreeNavigator(CMModuleService serv) {
		// Here the service can be into a Resource File or a group. We must find first
		// if he is in a group

		if (serv != null && serv.getContainer() != null) {
			Object moduleFolder = serv.getContainer();
			CMGenericDataSource ds = findDataSource(serv);
			if (ds != null) {
				Object parent = ds.getHierarchicalTreeSystemProvider().getParentFolder(moduleFolder);
				CMGroup possibleGP = findGroup(parent);
				if (possibleGP != null) {
					return possibleGP;
				}
				// We check if it can be a service
				CMModuleService ser = findModuleService(parent);
				if (ser != null) {
					if (ds.getHierarchicalTreeSystemProvider().isSameFolder(ser.getContainer(), parent)) {
						return ser.getRootGroup();
					}
				}
				return parent;
			}

		}

		return null;
	}

	public List<Object> getChildFolders() {
		ArrayList<Object> arl = new ArrayList<>();

		if (sources != null) {
			for (CMGenericDataSource src : sources) {
				if (src != null) {
					CoreUtils.putListIntoList(src.getContainers(), arl);
				}
			}
		}

		return arl;
	}

	@Override
	public Object getParentFolder(Object child) {

		return loopOnSourcesAndGetMine(child, (t) -> {
			return t.getHierarchicalTreeSystemProvider().getParentFolder(child);
		});

	}

	@Override
	public List<Object> getChildFolders(Object folder) {

		return (List<Object>) loopOnSourcesAndGetMine(folder, (t) -> {

			List<Object> lst = t.getHierarchicalTreeSystemProvider().getChildFolders(folder);
			swapFolderToServices(lst);
			return lst;
		});
	}

	public void swapFolderToServices(List<Object> lst) {
		if (lst != null) {
			for (int i = 0; i < lst.size(); i++) {
				Object o = lst.get(i);
				CMModuleService mod = getCorrespondingModuleServiceOfFolder(o);
				if (mod != null) {
					lst.set(i, mod);
				}
			}
		}
	}

	public boolean isHiddenElement(Object o) {
		Boolean b = (Boolean) loopOnSourcesAndGetMine(o, (t) -> {
			return t.isHiddenElement(o);
		});

		if (b != null) {
			return b;
		}
		return false;
	}

	@Override
	public String getName(Object o) {

		return (String) loopOnSourcesAndGetMine(o, (t) -> {
			return t.getHierarchicalTreeSystemProvider().getName(o);
		});
	}

	@Override
	public boolean isSameFolder(Object folder1, Object folder2) {

		Boolean b = (Boolean) loopOnSourcesAndGetMine(folder1, folder2, (t) -> {
			return t.getHierarchicalTreeSystemProvider().isSameFolder(folder1, folder2);
		});
		if (b != null) {
			return b;
		}
		return false;
	}

	@Override
	public boolean isFile(Object o) {
		Boolean b = (Boolean) loopOnSourcesAndGetMine(o, (t) -> {
			return t.getHierarchicalTreeSystemProvider().isFile(o);
		});

		if (b != null) {
			return b;
		}
		return false;
	}

	@Override
	public boolean isFolder(Object o) {
		Boolean b = (Boolean) loopOnSourcesAndGetMine(o, (t) -> {
			return t.getHierarchicalTreeSystemProvider().isFolder(o);
		});
		if (b != null) {
			return b;
		}
		return false;
	}

	public Object loopOnSourcesAndGetMine(Object o, Function<CMGenericDataSource, Object> func) {
		return loopOnSourcesAndGetMine(o, null, func);
	}

	public Object loopOnSourcesAndGetMine(Object o, Object o2, Function<CMGenericDataSource, Object> func) {
		if (sources != null) {
			for (CMGenericDataSource src : sources) {
				if (src != null && src.isMine(o)) {

					boolean ok = false;
					if (o2 != null) {
						ok = src.isMine(o2);
					} else {
						ok = true;
					}

					if (ok) {
						Object ob = func.apply(src);
						if (ob != null) {
							if (ob instanceof String) {
								String str = (String) ob;
								if (CoreUtils.isNotNullOrEmpty(str)) {
									return str;
								}
							} else if (ob instanceof Boolean) {
								boolean b = (boolean) ob;
								if (b == true) {
									return b;
								}
							} else {
								return ob;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public void markAllModelAsNotDirty() {
		if (services != null) {
			services.forEach(t -> markModelAsNotDirty(t.getModuleUniqueIdentifier()));
		}
	}

	public void markModelAsNotDirty(String modelId) {
		CMModuleService ser = getServiceByModuleUniqueIdentifierId(modelId);
		if (ser != null) {
			ser.markServiceSaved();
		}
	}

	public void saveAllModelsForUndoRedo(Object infoData) {
		if (services != null) {
			services.forEach(t -> saveModelForUndoRedo(t.getModuleUniqueIdentifier(), infoData));
		}
	}

	public void restoreAllModelsForUndoRedo() {
		if (services != null) {
			services.forEach(t -> restoreModelForUndoRedo(t.getModuleUniqueIdentifier()));
		}
	}

	public void saveModelForUndoRedo(String moduleUniqueidentifier, Object infoData) {
		CMModuleService serv = getServiceByModuleUniqueIdentifierId(moduleUniqueidentifier);
		if (serv != null && serv.getUndoRedoManager() != null) {
			serv.getUndoRedoManager().saveModel(infoData);
		}
	}

	public void restoreModelForUndoRedo(String moduleUniqueidentifier) {
		CMModuleService serv = getServiceByModuleUniqueIdentifierId(moduleUniqueidentifier);
		if (serv != null && serv.getUndoRedoManager() != null) {
			serv.getUndoRedoManager().restore();
		}
	}

	public void undoModelForUndoRedo(String moduleUniqueidentifier) {
		CMModuleService serv = getServiceByModuleUniqueIdentifierId(moduleUniqueidentifier);
		if (serv != null && serv.getUndoRedoManager() != null) {
			serv.getUndoRedoManager().undo();
		}
	}

	public void redoModelForUndoRedo(String moduleUniqueidentifier) {
		CMModuleService serv = getServiceByModuleUniqueIdentifierId(moduleUniqueidentifier);
		if (serv != null && serv.getUndoRedoManager() != null) {
			serv.getUndoRedoManager().redo();
		}
	}

	// TODO quand on fait un save et qu'il y a des dossier fils qui ne sont plus
	// utilisé, il faut les supprimer
	// TODO mettre en place l'implémentation des modules dans des modules

}
