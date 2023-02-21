package com.weelgo.chainmapping.core.navigator;

import java.util.ArrayList;
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

public class NavigatorModel extends NavNode {

	Map<String, NavNode> nodesMap = new HashMap<>();
	private Map<String, List<NavNode>> childsMap = new HashMap<>();

	public void update(CMModulesManager manager) {
		List<CMGenericDataSource> sources = manager.getSources();
		Map<String, NavNode> newNodesMap = new HashMap<>();
		Map<CMModuleService, String> moduleUuidMap = new HashMap<>();
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
					NavNode n = new NavNode();
					n.setName("Source " + index);
					n.setObjectType(NavNode.TYPE_SOURCE);
					n.setUuid(n.getName());
					n.setParentUuid("");
					CoreUtils.putObjectIntoMap(n, newNodesMap);

					// On fait une première passe pour créer les éléments des différents modules
					List<CMModuleService> modules = manager.getModuleServicesOfDataSource(ds);
					if (modules != null) {
						for (CMModuleService module : modules) {
							if (module != null) {

								// On créé les éléments de type dossier
								List<NavNode> lst = new ArrayList<>();
								createParentsNodesForFolder(module.getContainer(), ds, lst, dsContainer);
								Collections.reverse(lst);

								String uuid = n.getUuid();
								for (NavNode nodeTmp : lst) {
									if (nodeTmp != null) {
										nodeTmp.setParentUuid(uuid);
										uuid = uuid + Constants.UUID_PACKAGE_SEPARATOR + nodeTmp.getName();
										nodeTmp.setUuid(uuid);
										if (newNodesMap.containsKey(uuid) == false) {
											CoreUtils.putObjectIntoMap(nodeTmp, newNodesMap);
										}
									}
								}
								moduleUuidMap.put(module, uuid);
							}
						}

						for (CMModuleService module : modules) {
							if (module != null) {
								String containerUuid = moduleUuidMap.get(module);
								NavNode nodeTmp = newNodesMap.get(containerUuid);
								if (nodeTmp != null) {
									nodeTmp.setObjectType(NavNode.TYPE_MODULE);
								}
								List<CMGroup> childs = module.getGroupChilds(module.getRootGroup().getUuid());
								if (childs != null) {
									for (CMGroup c : childs) {
										createChildsNodesForModule(containerUuid, c, module, newNodesMap);
									}
								}
							}
						}
					}

				}
			}
		}

		List<NavNode> oldNodes = CoreUtils.putMapIntoList(nodesMap);
		List<NavNode> newNodes = CoreUtils.putMapIntoList(newNodesMap);
		CoreUtils.updateList(oldNodes, newNodes);

		nodesMap.clear();
		CoreUtils.putListIntoMap(oldNodes, nodesMap);

	}

	public void createChildsNodesForModule(String parentUuid, CMGroup gp, CMModuleService module,
			Map<String, NavNode> newNodes) {
		if (module != null && gp != null && CoreUtils.isNotNullOrEmpty(parentUuid)) {
			String uuid = parentUuid + Constants.UUID_PACKAGE_SEPARATOR + gp.getPackageName();
			NavNode gpTmp = newNodes.get(uuid);
			if (gpTmp == null) {
				gpTmp = new NavNode();
				gpTmp.setName(gp.getPackageName());
				gpTmp.setUuid(uuid);
				gpTmp.setParentUuid(parentUuid);
				CoreUtils.putObjectIntoMap(gpTmp, newNodes);
			}
			gpTmp.setObjectType(NavNode.TYPE_GROUP);
			List<CMGroup> childs = module.getGroupChilds(gp.getUuid());
			if (childs != null) {
				for (CMGroup c : childs) {
					createChildsNodesForModule(uuid, c, module, newNodes);
				}
			}
		}
	}

	public void createParentsNodesForFolder(Object folder, CMGenericDataSource ds, List<NavNode> nodes,
			Map<String, Object> dsContainer) {
		if (ds != null && folder != null) {

			NavNode n = new NavNode();
			n.setName(ds.getHierarchicalTreeSystemProvider().getName(folder));
			n.setObjectType(NavNode.TYPE_FOLDER);
			nodes.add(n);

			String id = ds.getHierarchicalTreeSystemProvider().getUniqueIdForFolderOrFile(folder);
			if (dsContainer.containsKey(id) == false) {
				Object parent = ds.getHierarchicalTreeSystemProvider().getParentFolder(folder);
				createParentsNodesForFolder(parent, ds, nodes, dsContainer);
			}
		}
	}

}
