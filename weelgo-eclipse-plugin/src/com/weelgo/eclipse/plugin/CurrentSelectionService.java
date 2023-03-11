package com.weelgo.eclipse.plugin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.weelgo.chainmapping.core.IDataSourceObject;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.CoreUtils;
import com.weelgo.core.exceptions.ExceptionsUtils;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor;
import com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditorInput;
import com.weelgo.eclipse.plugin.chainmapping.editor.NodeEditPart;

@Creatable
@Singleton
public class CurrentSelectionService {

	@Inject
	EventBroker eventBroker;

	@Inject
	SelectionAdapter selectionAdapter;

	private Object currentSelection;

	@Inject
	public void setSelectionChanged(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Object selection) {
		currentSelection = selection;
		eventBroker.sentEvent(CMEvents.SELECTION_CHANGED, selection);

	}

	public Object getCurrentSelection() {
		return currentSelection;
	}

	public List findSelectedElementsIntoChainMappingEditor(String uniqueModuleIdentifier, Class... wantedClass) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		IEditorReference[] editors = page.getEditorReferences();
		if (editors != null) {
			for (IEditorReference r : editors) {
				if (r != null) {
					try {
						IEditorInput ei = r.getEditorInput();
						if (ei instanceof ChainMappingEditorInput c) {
							if (CoreUtils.isStrictlyEqualsString(uniqueModuleIdentifier,
									c.getModuleUniqueIdentifier())) {
								IEditorPart editor = r.getEditor(false);
								if (editor instanceof ChainMappingEditor ce) {
									return ce.getSelectedElements(wantedClass);
								}
							}
						}
					} catch (PartInitException e) {
						ExceptionsUtils.logException(e, null);
					}
				}
			}
		}
		return null;
	}

	public List findListMulti(Class... wantedClass) {
		return selectionAdapter.findListMulti(currentSelection, wantedClass);
	}

	public <T> T find(Class<T> wantedClass) {
		return selectionAdapter.find(currentSelection, wantedClass);
	}

	public IModuleUniqueIdentifierObject findModuleUniqueIdentifierObject() {
		return selectionAdapter.findModuleUniqueIdentifierObject(currentSelection);
	}

	public String findModuleUniqueIdentifierObjectId() {
		return selectionAdapter.findModuleUniqueIdentifierObjectId(currentSelection);
	}

	public IDataSourceObject findDataSourceObject(Object objectToCheck) {
		return selectionAdapter.findDataSourceObject(objectToCheck);
	}

	public String findDataSourceUuid(Object objectToCheck) {
		return selectionAdapter.findDataSourceUuid(objectToCheck);
	}

	public <T> List<T> findList(Class<T> c) {
		return selectionAdapter.findList(currentSelection, c);
	}

	public List findList() {
		return selectionAdapter.findList(currentSelection);
	}
}
