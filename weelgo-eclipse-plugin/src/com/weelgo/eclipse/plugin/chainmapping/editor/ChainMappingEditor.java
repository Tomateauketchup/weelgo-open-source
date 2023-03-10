package com.weelgo.eclipse.plugin.chainmapping.editor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.zoom.ZoomListener;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weelgo.chainmapping.core.CMModuleService;
import com.weelgo.chainmapping.core.CMNode;
import com.weelgo.chainmapping.core.IModuleUniqueIdentifierObject;
import com.weelgo.core.IDisposableObject;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.KeyHelper;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.ActivateToolAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateNeedAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.CreateTaskAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.GenericSelectionAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.ModifyNodeNamePositionAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.PackAlignNodesAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.RemoveNodesAction;
import com.weelgo.eclipse.plugin.chainmapping.editor.actions.VAlignNodesAction;
import com.weelgo.eclipse.plugin.handlers.SaveHandler;
import com.weelgo.eclipse.plugin.job.CMOpenChainMappingEditorJob;

public class ChainMappingEditor extends GraphicalEditor implements IDisposableObject, IModuleUniqueIdentifierObject {

	public static final char CREATE_TASK_KEY = 't';
	public static final char CREATE_NEED_KEY = 'n';
	public static final char SELECTION_TOOL_KEY = 's';
	public static final char LINK_TOOL_KEY = 'l';

	private EventReciever eventReciever;
	private static Logger logger = LoggerFactory.getLogger(ChainMappingEditor.class);
	private IExecutionListener saveListener;
	public static final String ID = "com.weelgo.eclipse.plugin.chainmapping.editor.ChainMappingEditor";

	@Override
	protected void createGraphicalViewer(Composite parent) {
		GraphicalViewer viewer = new ScrollingGraphicalViewer() {

			@Override
			protected void createDefaultRoot() {
				setRootEditPart(new ScalableFreeformRootEditPart() {

					@Override
					protected LayeredPane createPrintableLayers() {
						FreeformLayeredPane layeredPane = new FreeformLayeredPane();
						layeredPane.add(new ConnectionLayer(), CONNECTION_LAYER);
						layeredPane.add(new FreeformLayer(), PRIMARY_LAYER);
						return layeredPane;
					}

					@Override
					protected ZoomManager createZoomManager(ScalableFigure scalableFigure, Viewport viewport) {
						return new ZoomManager(scalableFigure, viewport) {
							@Override
							protected void primSetZoom(double zoom) {

								double prevZoom = getEditorEditPart().getZoom();

								org.eclipse.swt.graphics.Point pCurstor = Factory
										.getCursorPosition(getViewer().getControl());
								Point curstorPosition = new Point(pCurstor.x, pCurstor.y);
								Point realCursorPositionBefore = curstorPosition.scale(1 / prevZoom);

//								System.out.println("Real Cursor position before : " + realCursorPositionBefore);

								super.primSetZoom(zoom);

								getEditorEditPart().zoomCHanged();

								pCurstor = Factory.getCursorPosition(getViewer().getControl());
								curstorPosition = new Point(pCurstor.x, pCurstor.y);
								Point realCursorPositionAfter = curstorPosition.scale(1 / zoom);

//								System.out.println("Real Cursor position after : " + realCursorPositionAfter);

								Dimension dif = realCursorPositionBefore.getDifference(realCursorPositionAfter);

								Point realViewportCenter = getViewport().getClientArea().getCenter().scale(1 / zoom);

								Point realViewportNewCenter = realViewportCenter.getTranslated(dif);

								Rectangle realViewportArea = getViewport().getClientArea().scale(1 / zoom);

//								System.out.println("Real Viewport center position : " + realViewportCenter);
//								System.out.println("Real Viewport area : " + realViewportArea);
//								System.out.println("*****************");

								int viewportHeight = realViewportArea.height;
								int viewportWidth = realViewportArea.width;

								int newViewportX = realViewportNewCenter.x - Math.round(viewportWidth / 2f);
								int newViewportY = realViewportNewCenter.y - Math.round(viewportHeight / 2f);

								Point newViewport = new Point(newViewportX, newViewportY).scale(zoom);

								getViewport().validate();
								setViewLocation(newViewport);

							}
						};
					}

				});
			}

		};
		viewer.createControl(parent);
		((FigureCanvas) viewer.getControl()).setScrollBarVisibility(FigureCanvas.NEVER);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

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
//				if (KeyHelper.isCTRL_S(keyEvent)) {
//					SaveHandler sh = new SaveHandler();
//					sh.executeWithModuleIdentifier(getModuleUniqueIdentifier());
//				}
				super.keyDown(keyEvent, viewer);
			}

		});
		getEditDomain().setDefaultTool(new PanAndSelectionTool());
		getEditDomain().setActiveTool(getEditDomain().getDefaultTool());
		eventReciever = EventReciever.CREATE();
		eventReciever.setChainMappingEditor(this);

	}

	public boolean isLinkTool() {
		return getEditDomain().getActiveTool() instanceof LinkTool;
	}

	public void selectTool(String tool) {
		if (ActivateToolAction.TOOL_LINK.equals(tool)) {
			LinkTool t = new LinkTool(this);
			getEditDomain().setActiveTool(t);
		} else if (ActivateToolAction.TOOL_SELECTION.equals(tool)) {
			getEditDomain().setActiveTool(getEditDomain().getDefaultTool());
		}
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

		ScalableFreeformRootEditPart part = (ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart();
		ZoomManager manager = part.getZoomManager();
		part.getZoomManager().addZoomListener(new ZoomListener() {

			@Override
			public void zoomChanged(double newZoom) {
				getEditorEditPart().setZoom(newZoom);
			}
		});

		// La liste des zooms possible. 1 = 100%
		double[] zoomLevels = new double[] { 0.05, 0.10, 0.15, 0.2, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0 };
		manager.setZoomLevels(zoomLevels);

		configureKeyHandler();

		DynamicContextMenu menu = new DynamicContextMenu(viewer, getActionRegistry(), this);
		viewer.setContextMenu(menu);
	}

	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	@Override
	protected void initializeGraphicalViewer() {
//		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModuleService());

	}

	public void configureKeyHandler() {

		KeyHandler keyHandler = new KeyHandler();
		keyHandler.put(KeyStroke.getPressed(CREATE_TASK_KEY, CREATE_TASK_KEY, 0),
				getActionRegistry().getAction(CreateTaskAction.CREATE_TASK));
		keyHandler.put(KeyStroke.getPressed(CREATE_NEED_KEY, CREATE_NEED_KEY, 0),
				getActionRegistry().getAction(CreateNeedAction.CREATE_NEED));
		keyHandler.put(KeyStroke.getPressed('v', 'v', 0),
				getActionRegistry().getAction(VAlignNodesAction.V_ALIGN_NODES));
		keyHandler.put(KeyStroke.getPressed('p', 'p', 0),
				getActionRegistry().getAction(PackAlignNodesAction.PACK_ALIGN_NODES));
		keyHandler.put(KeyStroke.getPressed(LINK_TOOL_KEY, LINK_TOOL_KEY, 0),
				getActionRegistry().getAction(ActivateToolAction.TOOL_LINK));
		keyHandler.put(KeyStroke.getPressed(SELECTION_TOOL_KEY, SELECTION_TOOL_KEY, 0),
				getActionRegistry().getAction(ActivateToolAction.TOOL_SELECTION));

		keyHandler.put(KeyStroke.getPressed(SWT.ARROW_DOWN, 0),
				getActionRegistry().getAction(ModifyNodeNamePositionAction.MODIFY_NODES_NAME_POSITION_BOTTOM));
		keyHandler.put(KeyStroke.getPressed(SWT.ARROW_UP, 0),
				getActionRegistry().getAction(ModifyNodeNamePositionAction.MODIFY_NODES_NAME_POSITION_TOP));
		keyHandler.put(KeyStroke.getPressed(SWT.ARROW_LEFT, 0),
				getActionRegistry().getAction(ModifyNodeNamePositionAction.MODIFY_NODES_NAME_POSITION_LEFT));
		keyHandler.put(KeyStroke.getPressed(SWT.ARROW_RIGHT, 0),
				getActionRegistry().getAction(ModifyNodeNamePositionAction.MODIFY_NODES_NAME_POSITION_RIGHT));

		// On peut meme zoomer avec la molette de la souris.
		getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
				MouseWheelZoomHandler.SINGLETON);

		getGraphicalViewer().setKeyHandler(keyHandler);

	}

	@Override
	public boolean isDirty() {
		return getChainMappingEditorInput().isDirty();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		SaveHandler.executeWithModuleIdentifier(getModuleUniqueIdentifier());
	}

	public void checkDirty() {
		firePropertyChange(PROP_DIRTY);
	}

	public void refreshForCreationOrRemove() {
		getGraphicalViewer().getContents().refresh();
		refreshVisualsOnly();
	}

	public void refreshVisualsOnly() {
		calculateAndRefreshCorners();
		CMEditorEditPart ep = getEditorEditPart();
		if (ep != null) {
			ep.refreshVisualsOnly();
		}
	}

	public void calculateAndRefreshCorners() {
		getEditorEditPart().calculateAndrefreshCorners();
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
		addAction(new CreateNeedAction(this));
		addAction(new RemoveNodesAction(this));
		addAction(new VAlignNodesAction(this));
		addAction(new PackAlignNodesAction(this));

		addAction(new ModifyNodeNamePositionAction(CMNode.NAME_BOTTOM, this));
		addAction(new ModifyNodeNamePositionAction(CMNode.NAME_TOP, this));
		addAction(new ModifyNodeNamePositionAction(CMNode.NAME_LEFT, this));
		addAction(new ModifyNodeNamePositionAction(CMNode.NAME_RIGHT, this));

		addAction(new ActivateToolAction(ActivateToolAction.TOOL_LINK, this));
		addAction(new ActivateToolAction(ActivateToolAction.TOOL_SELECTION, this));

	}

	@Override
	public ActionRegistry getActionRegistry() {
		return super.getActionRegistry();
	}

	public void addAction(GenericSelectionAction selectAct) {
		ActionRegistry registry = getActionRegistry();
		registry.registerAction(selectAct);
		getSelectionActions().add(selectAct.getId());
	}

	protected PaletteRoot getPaletteRoot() {

		PaletteRoot root = new PaletteRoot();

		// Ajout de l'outil de selection et de l'outil de selection groupe
		PaletteGroup manipGroup = new PaletteGroup("Tools");
		root.add(manipGroup);

		SelectionToolEntry selectionToolEntry = new SelectionToolEntry();
//		manipGroup.add(selectionToolEntry);
		PanningSelectionToolEntry panEntry = new PanningSelectionToolEntry();
		manipGroup.add(panEntry);

		// Definition l'entree dans la palette qui sera utilise par defaut :
		// 1.lors de la premiere ouverture de la palette
		// 2.lorsqu'un element de la palette ren
		root.setDefaultEntry(panEntry);

		return root;
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
