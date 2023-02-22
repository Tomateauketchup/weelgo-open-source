package com.weelgo.chainmapping.core.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weelgo.chainmapping.core.CMGenericDataSource;
import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMModulesManager;
import com.weelgo.core.Constants;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.IUuidObject;

public class NavigatorModel extends NavNode {

	private List<NavNode> nodes = new ArrayList<>();
	Map<String, NavNode> nodesMapByUuid;
	private Map<String, List<NavNode>> childsMapByUuid;

	public void update(CMModulesManager manager) {
		Collection<CMGenericDataSource> sources = manager.getDataSources();
		Map<String, NavNode> newNodesMap = new HashMap<>();
		Map<CMModuleService, String> moduleUuidMap = new HashMap<>();
		Map<String, CMModuleService> moduleByNavNodeUuidMap = new HashMap<>();
		Map<String, Object> dsContainer = new HashMap<>();
		int index = 0;
		if (sources != null) {
			for (CMGenericDataSource ds : sources) {
				if (ds != null) {

					List<Object> projects = ds.getContainers();
					if (projects != null) {
						for (Object prj : projects) {
							if (prj != null) {
								String id = ds.getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(prj);
								dsContainer.put(id, prj);
							}
						}
					}

					index++;
					NavNode sourceNode = new NavNode();
					sourceNode.setDataSourceUuid(ds.getUuid());
					sourceNode.setName(ds.getName());
					sourceNode.setObjectType(NavNode.TYPE_SOURCE);
					sourceNode.setUuid(sourceNode.getName());
					sourceNode.setParentUuid("");
					CoreUtils.putObjectIntoMap(sourceNode, newNodesMap);

					// On fait une première passe pour créer les éléments des différents modules
					List<CMModuleService> modules = manager.getModuleServicesOfDataSource(ds);
					if (modules != null) {
						for (CMModuleService module : modules) {
							if (module != null) {

								// On créé les éléments de type dossier
								List<NavNode> lst = new ArrayList<>();
								createParentsNodesForFolder(module.getContainer(), ds, lst, dsContainer);
								Collections.reverse(lst);

								String uuid = sourceNode.getUuid();
								for (NavNode nodeTmp : lst) {
									if (nodeTmp != null) {
										nodeTmp.setParentUuid(uuid);
										uuid = uuid + Constants.UUID_PACKAGE_SEPARATOR + nodeTmp.getName();
										nodeTmp.setUuid(uuid);
										if (!newNodesMap.containsKey(uuid)) {
											CoreUtils.putObjectIntoMap(nodeTmp, newNodesMap);
										}
									}
								}
								moduleUuidMap.put(module, uuid);
								moduleByNavNodeUuidMap.put(uuid, module);
							}
						}

						for (CMModuleService module : modules) {
							if (module != null) {
								String containerNavNodeUuid = moduleUuidMap.get(module);
								NavNode nodeTmp = newNodesMap.get(containerNavNodeUuid);
								if (nodeTmp != null) {
									nodeTmp.setObjectType(NavNode.TYPE_MODULE);
									nodeTmp.setData(module);
									nodeTmp.setDirty(module.isDirty());
									nodeTmp.setSubmodulesDirty(false);
								}
								List<CMGroup> childs = module.getGroupChilds(module.getRootGroup().getUuid());
								if (childs != null) {
									for (CMGroup c : childs) {
										createChildsNodesForModule(containerNavNodeUuid, c, module, newNodesMap, ds);
									}
								}
							}
						}

						for (CMModuleService module : modules) {
							if (module != null) {
								checkSubmodulesDirty(false, module, moduleUuidMap, moduleByNavNodeUuidMap, newNodesMap);
							}

						}
					}

					// On fait une passe sur les folder "non utilisés"
					List<Object> childs = ds.getContainers();
					if (childs != null) {
						for (Object c : childs) {
							getNotUsedChildFolders(sourceNode.getUuid(), c, ds, newNodesMap, dsContainer);
						}
					}

				}
			}
		}

		List<NavNode> oldNodes = nodes;
		List<NavNode> newNodes = CoreUtils.putMapIntoList(newNodesMap);
		CoreUtils.updateList(oldNodes, newNodes);

		needReloadObjects();

	}

	public void checkSubmodulesDirty(boolean isSubmoduleDirty, CMModuleService module,
			Map<CMModuleService, String> moduleUuidMap, Map<String, CMModuleService> moduleByNavNodeUuidMap,
			Map<String, NavNode> newNodesMap) {
		if (module != null && moduleUuidMap != null && moduleByNavNodeUuidMap != null && newNodesMap != null) {
			String containerNavNodeUuid = moduleUuidMap.get(module);
			NavNode node = newNodesMap.get(containerNavNodeUuid);
			if (!node.isSubmodulesDirty()) {
				node.setSubmodulesDirty(isSubmoduleDirty);
			}
			if (!isSubmoduleDirty) {
				isSubmoduleDirty = module.isDirty();
			}
			NavNode parent = newNodesMap.get(CoreUtils.cleanString(node.getParentUuid()));
			checkSubmodulesDirty(isSubmoduleDirty, parent, moduleUuidMap, moduleByNavNodeUuidMap, newNodesMap);
		}
	}

	public void checkSubmodulesDirty(boolean isSubmoduleDirty, NavNode node, Map<CMModuleService, String> moduleUuidMap,
			Map<String, CMModuleService> moduleByNavNodeUuidMap, Map<String, NavNode> newNodesMap) {
		if (node != null && moduleUuidMap != null && moduleByNavNodeUuidMap != null) {
			// On vérifie si c'est un module
			CMModuleService serv = moduleByNavNodeUuidMap.get(node.getUuid());
			if (serv != null) {
				checkSubmodulesDirty(isSubmoduleDirty, serv, moduleUuidMap, moduleByNavNodeUuidMap, newNodesMap);
			} else {
				if (!node.isSubmodulesDirty()) {
					node.setSubmodulesDirty(isSubmoduleDirty);
				}
				NavNode parent = newNodesMap.get(CoreUtils.cleanString(node.getParentUuid()));
				checkSubmodulesDirty(isSubmoduleDirty, parent, moduleUuidMap, moduleByNavNodeUuidMap, newNodesMap);
			}
		}
	}

	public void getNotUsedChildFolders(String parentUuid, Object folder, CMGenericDataSource ds,
			Map<String, NavNode> newNodes, Map<String, Object> dsContainer) {
		if (folder != null && CoreUtils.isNotNullOrEmpty(parentUuid) && ds != null && newNodes != null) {
			String name = ds.getHierarchicalTreeSystemProvider().getName(folder);
			String uuid = parentUuid + Constants.UUID_PACKAGE_SEPARATOR + name;

			NavNode node = newNodes.get(uuid);
			if (node == null) {
				node = new NavNode();
				node.setName(name);
				node.setUuid(uuid);
				node.setParentUuid(parentUuid);
				node.setData(folder);
				node.setDataSourceUuid(ds.getUuid());

				String id = ds.getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(folder);
				if (!dsContainer.containsKey(id)) {
					node.setObjectType(TYPE_FOLDER);
				} else {
					node.setObjectType(TYPE_PROJECT);
				}
				CoreUtils.putObjectIntoMap(node, newNodes);
			}

			List<Object> childs = ds.getHierarchicalTreeSystemProvider().getChildFolders(folder);
			if (childs != null) {
				for (Object c : childs) {
					getNotUsedChildFolders(uuid, c, ds, newNodes, dsContainer);
				}
			}
		}
	}

	public void createChildsNodesForModule(String parentUuid, CMGroup gp, CMModuleService module,
			Map<String, NavNode> newNodes, CMGenericDataSource ds) {
		if (module != null && gp != null && CoreUtils.isNotNullOrEmpty(parentUuid) && ds != null) {
			String uuid = parentUuid + Constants.UUID_PACKAGE_SEPARATOR + gp.getPackageName();
			NavNode gpTmp = newNodes.get(uuid);
			if (gpTmp == null) {
				gpTmp = new NavNode();
				gpTmp.setName(gp.getPackageName());
				gpTmp.setUuid(uuid);
				gpTmp.setParentUuid(parentUuid);
				CoreUtils.putObjectIntoMap(gpTmp, newNodes);
			}
			gpTmp.setData(gp);
			gpTmp.setObjectType(NavNode.TYPE_GROUP);
			gpTmp.setDataSourceUuid(ds.getUuid());
			List<CMGroup> childs = module.getGroupChilds(gp.getUuid());
			if (childs != null) {
				for (CMGroup c : childs) {
					createChildsNodesForModule(uuid, c, module, newNodes, ds);
				}
			}
		}
	}

	public void createParentsNodesForFolder(Object folder, CMGenericDataSource ds, List<NavNode> nodes,
			Map<String, Object> dsContainer) {
		if (ds != null && folder != null) {

			NavNode n = new NavNode();
			n.setData(folder);
			n.setName(ds.getHierarchicalTreeSystemProvider().getName(folder));
			n.setObjectType(NavNode.TYPE_FOLDER);
			n.setDataSourceUuid(ds.getUuid());
			nodes.add(n);

			String id = ds.getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(folder);
			if (!dsContainer.containsKey(id)) {
				Object parent = ds.getHierarchicalTreeSystemProvider().getParentFolder(folder);
				createParentsNodesForFolder(parent, ds, nodes, dsContainer);
			} else {
				n.setObjectType(NavNode.TYPE_PROJECT);
			}
		}
	}

	public void needReloadObjects() {
		nodesMapByUuid = null;
	}

	public void loadObjectsIntoMap(boolean forceReload) {
		if (nodesMapByUuid == null || forceReload) {
			nodesMapByUuid = new HashMap<>();
			childsMapByUuid = new HashMap<>();

			if (nodes != null) {
				for (NavNode navNode : nodes) {
					if (navNode != null) {
						CoreUtils.putObjectIntoMap(navNode, nodesMapByUuid);

						String parentUuid = CoreUtils.cleanString(navNode.getParentUuid());
						List<NavNode> lst = childsMapByUuid.get(parentUuid);
						if (lst == null) {
							lst = new ArrayList<>();
							childsMapByUuid.put(parentUuid, lst);
						}
						lst.add(navNode);
					}
				}
			}
		}
	}

	public List<NavNode> getChilds(String parentUuid) {
		loadObjectsIntoMap(false);
		if (childsMapByUuid != null) {
			return childsMapByUuid.get(CoreUtils.cleanString(parentUuid));
		}
		return null;
	}

	public List<NavNode> getChilds(IUuidObject parentObject) {
		String uuid = "";
		if (parentObject != null) {
			uuid = parentObject.getUuid();
		}
		return getChilds(uuid);
	}

	public NavNode[] getChildsArray(IUuidObject parentObject) {
		List<NavNode> lst = getChilds(parentObject);
		if (lst != null && lst.size() > 0) {
			return lst.toArray(new NavNode[lst.size()]);
		}
		return null;
	}

	public NavNode getNodeByUuid(String uuid) {
		loadObjectsIntoMap(false);
		uuid = CoreUtils.cleanString(uuid);
		if (nodesMapByUuid != null) {
			return nodesMapByUuid.get(uuid);
		}
		return null;
	}

	public NavNode getNodeByName(String name) {
		if (nodes != null) {
			for (NavNode navNode : nodes) {
				if (navNode != null && CoreUtils.isStrictlyEqualsString(navNode.getName(), name)) {
					return navNode;
				}
			}
		}
		return null;
	}

	public NavNode getParentNodeByUuid(String uuid) {
		loadObjectsIntoMap(false);
		if (nodesMapByUuid != null) {
			uuid = CoreUtils.cleanString(uuid);
			return nodesMapByUuid.get(uuid);
		}
		return null;
	}

	public NavNode getParentNode(NavNode node) {
		String nodeUuid = "";
		if (node != null) {
			nodeUuid = node.getUuid();
		}
		nodeUuid = CoreUtils.cleanString(nodeUuid);
		return getParentNodeByUuid(nodeUuid);
	}

}
