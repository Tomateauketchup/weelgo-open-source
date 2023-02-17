package com.weelgo.chainmapping.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.IProgressMonitor;
import com.weelgo.core.IUuidObject;

public class CMModulesManager implements HierarchicalTreeSystemNavProvider {

	public List<CMGenericDataSource> sources = new ArrayList<>();

	public List<CMModuleService> services = new ArrayList<>();
	public Map<String, CMModuleService> servicesMap = new HashMap<>();

	public void load(IProgressMonitor progressMonitor) {
		Map<String, CMModuleService> servicesMapTmp = CoreUtils.putIntoMap(services);

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
		CoreUtils.putIntoMap(services, servicesMap);
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

	public String createModule(IProgressMonitor progressMonitor, CMGenericDataSource ds, Object moduleParentFolder,
			String moduleName, String modulePackageName) {
		CMModuleService service = new CMModuleService();
		CMGroup gp = new CMGroup();
		service.setRootGroup(gp);
		service.getGroups().add(gp);
		gp.setModuleUniqueIdentifier(CoreUtils.generateUUIDString());
		gp.setName(moduleName);
		gp.setPackageName(modulePackageName);
		gp.setType(CMGroup.TYPE_MODULE);
		service.check();

		ds.createModule(progressMonitor, moduleParentFolder, service);		
		return gp.getModuleUniqueIdentifier();
	}

	public boolean isModulePackageFree(IProgressMonitor progressMonitor, CMGenericDataSource ds,
			Object moduleParentFolder, String packageName) {
		if (ds != null) {
			return ds.isModulePackageFree(progressMonitor, moduleParentFolder, packageName);
		}
		return false;
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
				if (ser != null && ser.getParentContainer() != null && isSameFolder(o, ser.getParentContainer())) {
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

	public List<Object> getChildFolders() {
		ArrayList<Object> arl = new ArrayList<>();

		if (sources != null) {
			for (CMGenericDataSource src : sources) {
				if (src != null) {
					CoreUtils.putIntoList(src.getContainers(), arl);
				}
			}
		}

		return arl;
	}

	@Override
	public Object getParentFolder(Object child) {

		return loopOnSources(child, null, (t) -> {
			return t.getHierarchicalTreeSystemProvider().getParentFolder(child);
		});

	}

	@Override
	public List<Object> getChildFolders(Object folder) {

		return (List<Object>) loopOnSources(folder, null, (t) -> {

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
		Boolean b = (Boolean) loopOnSources(o, null, (t) -> {
			return t.isHiddenElement(o);
		});

		if (b != null) {
			return b;
		}
		return false;
	}

	@Override
	public String getName(Object o) {

		return (String) loopOnSources(o, null, (t) -> {
			return t.getHierarchicalTreeSystemProvider().getName(o);
		});
	}

	@Override
	public boolean isSameFolder(Object folder1, Object folder2) {

		Boolean b = (Boolean) loopOnSources(folder1, folder2, (t) -> {
			return t.getHierarchicalTreeSystemProvider().isSameFolder(folder1, folder2);
		});
		if (b != null) {
			return b;
		}
		return false;
	}

	@Override
	public boolean isFile(Object o) {
		Boolean b = (Boolean) loopOnSources(o, null, (t) -> {
			return t.getHierarchicalTreeSystemProvider().isFile(o);
		});

		if (b != null) {
			return b;
		}
		return false;
	}

	@Override
	public boolean isFolder(Object o) {
		Boolean b = (Boolean) loopOnSources(o, null, (t) -> {
			return t.getHierarchicalTreeSystemProvider().isFolder(o);
		});
		if (b != null) {
			return b;
		}
		return false;
	}

	public Object loopOnSources(Object o, Object o2, Function<CMGenericDataSource, Object> func) {
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

	public void saveModelForUndoRedo(String moduleUniqueidentifier) {
		CMModuleService serv = getServiceByModuleUniqueIdentifierId(moduleUniqueidentifier);
		if (serv != null && serv.getUndoRedoManager() != null) {
			serv.getUndoRedoManager().saveModel();
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

}
