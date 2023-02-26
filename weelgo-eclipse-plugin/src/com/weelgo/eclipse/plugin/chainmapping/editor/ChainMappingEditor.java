package com.weelgo.eclipse.plugin.chainmapping.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.IDisposableObject;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.KeyHelper;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateTaskAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.GenericSelectionAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.RemoveNodesAction;
import com.weelgo.eclipse.plugin.handlers.SaveHandler;
import com.weelgo.eclipse.plugin.job.CMOpenChainMappingEditorJob;

public class ChainMappingEditor extends GraphicalEditorWithFlyoutPalette
		implements IDisposableObject, IModuleUniqueIdentifierObject {

	private EventReciever eventReciever;
	private static Logger logger = LoggerFactory.getLogger(ChainMappingEditor.class);

	public static final String ID = "com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor";

	public ChainMappingEditor() {
		setEditDomain(new DefaultEditDomain(this) {

			@Override
			public void keyDown(KeyEvent keyEvent, EditPartViewer viewer) {
				if (KeyHelper.isCTRL_Z(keyEvent)) {
					Factory.getUndoRedoService().undoModel(getModuleUniqueIdentifier());
				}
				if (KeyHelper.isCTRL_Y(keyEvent)) {
					Factory.getUndoRedoService().redoModel(getModuleUniqueIdentifier());
				}
				if (KeyHelper.isCTRL_S(keyEvent)) {
					SaveHandler sh = new SaveHandler();
					sh.executeWithModuleIdentifier(getModuleUniqueIdentifier());
				}
				super.keyDown(keyEvent, viewer);
			}

		});
		eventReciever = EventReciever.CREATE();
		eventReciever.setChainMappingEditor(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		disposeObject();
	}

	@Override
	public void disposeObject() {
		Factory.dispose(eventReciever);
		Factory.dispose(getGraphicalViewer().getRootEditPart());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(CMEditorEditPartFactory.CREATE());

		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		viewer.setRootEditPart(rootEditPart);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));

		// La liste des zooms possible. 1 = 100%
		double[] zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);

		// On ajoute certains zooms prédéfinis
		List<String> zoomContributions = new ArrayList<>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		configureKeyHandler();

		DynamicContextMenu menu = new DynamicContextMenu(viewer, getActionRegistry());
		viewer.setContextMenu(menu);
	}

	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModuleService());
	}

	public void configureKeyHandler() {

		KeyHandler keyHandler = new KeyHandler();
		keyHandler.put(KeyStroke.getPressed('t', 't', 0), getActionRegistry().getAction(CreateTaskAction.CREATE_TASK));

		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
				getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		keyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

		keyHandler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
		// On peut meme zoomer avec la molette de la souris.
		getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
				MouseWheelZoomHandler.SINGLETON);

		getGraphicalViewer().setKeyHandler(keyHandler);

	}

	public void refreshIsDirty() {
		boolean isDirty = getChainMappingEditorInput().isDirty();
		String name = getChainMappingEditorInput().getName();
		if (isDirty) {
			name = "*" + name;
		}
		setPartName(name);
	}

	public void refreshForCreationOrRemove() {
		getGraphicalViewer().getContents().refresh();
	}

	public void refreshVisualsOnly() {
		CMEditorEditPart ep = getEditorEditPart();
		if (ep != null) {
			ep.refreshVisualsOnly();
		}
	}

	public CMEditorEditPart getEditorEditPart() {
		RootEditPart root = getGraphicalViewer().getRootEditPart();
		if (root != null) {
			return (CMEditorEditPart) root.getContents();
		}
		return null;
	}

	@Override
	protected void createActions() {
		super.createActions();

		addAction(new CreateTaskAction(this));
		addAction(new RemoveNodesAction(this));
	}

	public void addAction(GenericSelectionAction selectAct) {
		ActionRegistry registry = getActionRegistry();
		registry.registerAction(selectAct);
		getSelectionActions().add(selectAct.getId());
	}

	@Override
	protected PaletteRoot getPaletteRoot() {

		PaletteRoot root = new PaletteRoot();

		// Ajout de l'outil de selection et de l'outil de selection groupe
		PaletteGroup manipGroup = new PaletteGroup("Tools");
		root.add(manipGroup);

		SelectionToolEntry selectionToolEntry = new SelectionToolEntry();
		manipGroup.add(selectionToolEntry);

		// Definition l'entree dans la palette qui sera utilise par defaut :
		// 1.lors de la premiere ouverture de la palette
		// 2.lorsqu'un element de la palette ren
		root.setDefaultEntry(selectionToolEntry);

		return root;
	}


	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	public static void openEditor(final String moduleUniqueIdentifier) {

		CMOpenChainMappingEditorJob j = CMOpenChainMappingEditorJob.CREATE();
		j.setModuleUniqueIdentifier(moduleUniqueIdentifier);
		j.doSchedule();
	}

	public ChainMappingEditorInput getChainMappingEditorInput() {
		return (ChainMappingEditorInput) getEditorInput();
	}

	public CMModuleService getModuleService() {
		return getChainMappingEditorInput().getModuleService();
	}

	@Override
	public String getModuleUniqueIdentifier() {
		return getChainMappingEditorInput().getModuleUniqueIdentifier();
	}

	@Override
	public void setModuleUniqueIdentifier(String moduleUniqueIdentifier) {

	}

}
