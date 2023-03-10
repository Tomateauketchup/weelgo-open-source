package com.weelgo.chainmapping.core.json.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weelgo.chainmapping.core.CMGroup;
import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNeed;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.chainmapping.core.CMTask;

public class Populator {

	public static Object[] jsonToModelPopulator(Object source, Object toPopulate, CMGroup parentGroup,
			CMModuleService module, Map<String, List<String>> inputsMap) {
		if (source instanceof JSN_CMGroup && toPopulate instanceof CMGroup) {
			CMGroup to = (CMGroup) toPopulate;
			JSN_CMGroup src = (JSN_CMGroup) source;
			to.setName(src.getName());
			to.setPackageName(src.getPackage_name());
			to.setPackageParentPath(src.getPackage_parent_path());
			to.setType(src.getType());
			to.setUuid(src.getUuid());

			if (to.isModule()) {
				module = new CMModuleService();
				inputsMap = new HashMap<>();
			}

			if (module != null) {
				List<JSN_CMTask> tasks = src.getTasks();
				if (tasks != null) {
					for (JSN_CMTask t : tasks) {
						if (t != null) {
							CMTask tsk = new CMTask();
							jsonToModelPopulator(t, tsk, to, module, inputsMap);
							module.getTasks().add(tsk);
							module.needReloadObjects();
						}
					}
				}
				List<JSN_CMNeed> needs = src.getNeeds();
				if (needs != null) {
					for (JSN_CMNeed t : needs) {
						if (t != null) {
							CMNeed tsk = new CMNeed();
							jsonToModelPopulator(t, tsk, to, module, inputsMap);
							module.getNeeds().add(tsk);
							module.needReloadObjects();
						}
					}
				}
			}

		} else if (source instanceof JSN_CMTask && toPopulate instanceof CMTask) {
			CMTask to = (CMTask) toPopulate;
			JSN_CMTask src = (JSN_CMTask) source;
			if (parentGroup != null) {
				to.setGroupUuid(parentGroup.getUuid());
			}
			to.setName(src.getName());
			to.setNamePosition(src.getName_position());
			to.setPositionX(src.getPosition_x());
			to.setPositionY(src.getPosition_y());
			to.setUuid(src.getUuid());
			if (inputsMap != null && src.getInputs() != null && src.getInputs().size() > 0) {
				inputsMap.put(src.getUuid(), src.getInputs());
			}
		} else if (source instanceof JSN_CMNeed && toPopulate instanceof CMNeed) {
			CMNeed to = (CMNeed) toPopulate;
			JSN_CMNeed src = (JSN_CMNeed) source;
			if (parentGroup != null) {
				to.setGroupUuid(parentGroup.getUuid());
			}
			to.setName(src.getName());
			to.setNamePosition(src.getName_position());
			to.setPositionX(src.getPosition_x());
			to.setPositionY(src.getPosition_y());
			to.setUuid(src.getUuid());
			if (inputsMap != null && src.getInputs() != null && src.getInputs().size() > 0) {
				inputsMap.put(src.getUuid(), src.getInputs());
			}
		}
		return new Object[] { module, inputsMap };
	}

	public static void modelToJsonPopulator(Object source, Object toPopulate, CMModuleService module) {
		if (source instanceof CMGroup && toPopulate instanceof JSN_CMGroup) {
			JSN_CMGroup to = (JSN_CMGroup) toPopulate;
			CMGroup src = (CMGroup) source;
			to.setUuid(src.getUuid());
			to.setName(src.getName());
			to.setPackage_name(src.getPackageName());
			to.setPackage_parent_path(src.getPackageParentPath());
			to.setType(src.getType());
			if (module != null) {
				List<CMTask> tasks = module.getTaskChilds(src.getUuid());
				if (tasks != null) {
					for (CMTask cmTask : tasks) {
						if (cmTask != null) {
							JSN_CMTask jsnTask = new JSN_CMTask();
							modelToJsonPopulator(cmTask, jsnTask, module);
							to.getTasks().add(jsnTask);
						}
					}
				}
				List<CMNeed> needs = module.getNeedChilds(src.getUuid());
				if (needs != null) {
					for (CMNeed cmTask : needs) {
						if (cmTask != null) {
							JSN_CMNeed jsnTask = new JSN_CMNeed();
							modelToJsonPopulator(cmTask, jsnTask, module);
							to.getNeeds().add(jsnTask);
						}
					}
				}
			}
		} else if (source instanceof CMTask && toPopulate instanceof JSN_CMTask) {
			JSN_CMTask to = (JSN_CMTask) toPopulate;
			CMTask src = (CMTask) source;
			to.setName(src.getName());
			to.setName_position(src.getNamePosition());
			to.setPosition_x(src.getPositionX());
			to.setPosition_y(src.getPositionY());
			to.setUuid(src.getUuid());

			if (module != null) {
				List<CMNode> inputs = module.getInputElements(src);
				if (inputs != null) {
					for (CMNode cmNode : inputs) {
						if (cmNode != null) {
							to.getInputs().add(cmNode.getUuid());
						}
					}
				}
			}
		} else if (source instanceof CMNeed && toPopulate instanceof JSN_CMNeed) {
			JSN_CMNeed to = (JSN_CMNeed) toPopulate;
			CMNeed src = (CMNeed) source;
			to.setName(src.getName());
			to.setName_position(src.getNamePosition());
			to.setPosition_x(src.getPositionX());
			to.setPosition_y(src.getPositionY());
			to.setUuid(src.getUuid());

			if (module != null) {
				List<CMNode> inputs = module.getInputElements(src);
				if (inputs != null) {
					for (CMNode cmNode : inputs) {
						if (cmNode != null) {
							to.getInputs().add(cmNode.getUuid());
						}
					}
				}
			}
		}

	}

}
