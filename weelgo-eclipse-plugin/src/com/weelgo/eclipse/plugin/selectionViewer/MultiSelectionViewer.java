package com.weelgo.eclipse.plugin.selectionViewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.weelgo.core.CoreUtils;
import com.weelgo.core.IDisposableObject;
import com.weelgo.core.IUuidObject;
import com.weelgo.eclipse.plugin.Factory;
import com.weelgo.eclipse.plugin.job.CMJob;
import com.weelgo.eclipse.plugin.ui.ListBoxUiProperty;
import com.weelgo.eclipse.plugin.ui.TextUiProperty;
import com.weelgo.eclipse.plugin.ui.UiProperty;

public class MultiSelectionViewer extends SelectionView<List<IUuidObject>> {

	private List<Row> rows = new ArrayList<>();
	private Map<String, Row> rowMap = new HashMap<>();
	private Map<String, Col> colMap = new HashMap<>();
	private Map<String, Row> selectedRowMap = new HashMap<String, Row>();
	private TableViewer viewer;
	private TableViewerColumn firstColumn;
	private int selectedColumn = -1;

	@Override
	public Composite createContent(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = Factory.createGridLayoutNoMargin(1);
		mainComposite.setLayout(layout);

		viewer = new TableViewer(mainComposite, SWT.MULTI | SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(viewer.getControl());

		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (selectedColumn == 0) {
					selectedRowMap.clear();
					List<Row> selectedRows = Factory.getSelectionAdapter().findList(event.getStructuredSelection(),
							Row.class);
					if (selectedRows != null && selectedRows.size() > 1) {
						CoreUtils.putListIntoMap(selectedRows, selectedRowMap);
					}
					Row[] a = null;
					if (rows != null && rows.size() > 0) {
						a = (Row[]) rows.toArray(new Row[rows.size()]);
					}
					viewer.update(a, null);
				}
			}
		});
		viewer.getTable().addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent event) {
				selectedColumn = -1;
				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = table.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					for (int i = 0, n = table.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							selectedColumn = i;
							break;
						}
					}
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		viewer.setInput(rows);

		return mainComposite;
	}

	@Override
	public void populateView(List<IUuidObject> object) {

		viewer.getTable().clearAll();
		viewer.getTable().removeAll();
		CoreUtils.dispose(colMap);
		rowMap.clear();
		rows.clear();
		colMap.clear();
		selectedRowMap.clear();
		if (firstColumn != null) {
			firstColumn.getColumn().dispose();
		}

		firstColumn = new TableViewerColumn(viewer, SWT.NONE);
		firstColumn.getColumn().setWidth(50);
		firstColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Row r) {
					String uuid = r.getUuid();
					if (CoreUtils.isNotNullOrEmpty(uuid)) {
						if (selectedRowMap.containsKey(uuid)) {
							return "x";
						}
					}
				}
				return "";
			}

		});

		if (object != null) {
			for (IUuidObject o : object) {
				if (o != null) {
					PurePropertiesViewer view = SelectionViewerPart.createViewFromObject(o);
					if (view != null) {
						List<UiProperty> props = view.getPropertiesList();

						Row r = new Row();
						r.setData(o);
						if (props != null) {
							for (UiProperty p : props) {
								if (p != null) {
									p.populate(o);
									r.getPropertiesMap().put(p.getId(), p);

									String colId = p.getId();
									if (!colMap.containsKey(colId)) {
										Col c = new Col();
										c.setProperty(p);
										CoreUtils.putObjectIntoMap(c, colMap);
										c.create();
									}

								}

							}
						}
						rows.add(r);
						CoreUtils.putObjectIntoMap(r, rowMap);
					}
				}
			}
		}

		viewer.refresh();
	}

	@Override
	public List<CMJob> applyChanges() {
		List<CMJob> lst = new ArrayList<CMJob>();
		if (rows != null) {
			for (Row r : rows) {
				if (r != null) {
					for (Map.Entry<String, String> entry : r.getValuesMap().entrySet()) {
						String key = entry.getKey();
						String val = entry.getValue();

						UiProperty prop = r.getPropertiesMap().get(key);
						if (prop != null && val != null) {
							boolean equal = prop.isDataEquals(prop.getData(), val);
							if (equal == false) {
								CoreUtils.putListIntoList(prop.applyChanges(val), lst);
							}
						}
					}
				}
			}
		}
		return lst;
	}

	@Override
	public boolean validateInputs() {
		if (rows != null) {
			for (Row row : rows) {
				if (row != null) {
					Collection<UiProperty> col = row.getPropertiesMap().values();
					for (UiProperty uiProperty : col) {
						if (uiProperty != null) {
							String value = row.getValuesMap().get(uiProperty.getId());
							if (value != null) {
								// On prend pas les null car ça veut dire que les cellules n'ont pas été
								// initialisées
								String message = uiProperty.validateInput(value);
								if (isNotNullOrEmpty(message)) {
									updateStatus(message);
									return false;
								}
							}
						}
					}
				}
			}
		}
		updateStatus(null);
		return true;
	}

	@Override
	public boolean isDataEquals(List<IUuidObject> object) {

		if (object != null) {
			for (IUuidObject iUuidObject : object) {
				if (iUuidObject != null) {
					Row r = rowMap.get(iUuidObject.getUuid());
					if (r != null) {
						for (Map.Entry<String, String> entry : r.getValuesMap().entrySet()) {
							String key = entry.getKey();
							String val = entry.getValue();

							UiProperty prop = r.getPropertiesMap().get(key);
							if (prop != null && val != null) {
								boolean equal = prop.isDataEquals(iUuidObject, val);
								if (equal == false) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public void disposeObject() {

		if (viewer != null) {
			viewer.getControl().dispose();
		}
		viewer = null;
		CoreUtils.dispose(colMap);
		super.disposeObject();
	}

	public class Row implements IUuidObject {
		private IUuidObject data;
		private Map<String, UiProperty> propertiesMap = new HashMap<>();
		private Map<String, String> valuesMap = new HashMap<>();

		public IUuidObject getData() {
			return data;
		}

		public void setData(IUuidObject data) {
			this.data = data;
		}

		public Map<String, UiProperty> getPropertiesMap() {
			return propertiesMap;
		}

		public void setPropertiesMap(Map<String, UiProperty> propertiesMap) {
			this.propertiesMap = propertiesMap;
		}

		@Override
		public String getUuid() {
			return data.getUuid();
		}

		@Override
		public void setUuid(String uuid) {

		}

		public Map<String, String> getValuesMap() {
			return valuesMap;
		}

		public void setValuesMap(Map<String, String> valuesMap) {
			this.valuesMap = valuesMap;
		}

	}

	public class Col implements IUuidObject, IDisposableObject {
		private TableViewerColumn column;
		private UiProperty property;

		@Override
		public void disposeObject() {
			if (column != null) {
				column.getColumn().dispose();
			}
			column = null;
			property = null;
		}

		public void create() {

			Function<Object, String> getData = (element) -> {

				String val = "";
				if (element instanceof Row r) {
					val = r.getValuesMap().get(getId());
					if (val == null) {
						UiProperty prop = r.getPropertiesMap().get(getId());
						if (prop != null) {
							val = prop.getDataFromObjectString();
						}
					}
				}
				return val;
			};

			column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setWidth(200);
			column.getColumn().setText(getName());
			column.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {

					return getData.apply(element);
				}
			});

			EditingSupport editSupport = new EditingSupport(viewer) {

				@Override
				protected void setValue(Object element, Object value) {

					String newValue = null;
					List<Row> rowToUpdate = new ArrayList<MultiSelectionViewer.Row>();
					if (element instanceof Row r) {
						rowToUpdate.add(r);
						if (value instanceof String val) {
							newValue = val;
						} else if (value instanceof Integer) {
							UiProperty prop = r.getPropertiesMap().get(getId());
							if (prop instanceof ListBoxUiProperty libx) {
								List lst = libx.getListElements();
								if (lst != null) {
									String val = (String) lst.get((int) value);
									newValue = val;
								}
							}
						}

						if (newValue != null) {
							r.getValuesMap().put(getId(), newValue);
							// On doit également mettre la valeur sur les row sélectionées
							if (selectedRowMap.containsKey(r.getUuid())) {
								for (Map.Entry<String, Row> entry : selectedRowMap.entrySet()) {
									String key = entry.getKey();
									Row row = entry.getValue();

									row.getValuesMap().put(getId(), newValue);
									rowToUpdate.add(row);

								}
							}
						}
					}

					if (rowToUpdate.size() > 0) {
						viewer.update((Row[]) rowToUpdate.toArray(new Row[rowToUpdate.size()]), null);
					}

					validateInputs();
				}

				@Override
				protected Object getValue(Object element) {

					String str = getData.apply(element);

					if (element instanceof Row r) {
						UiProperty prop = r.getPropertiesMap().get(getId());
						if (prop instanceof ListBoxUiProperty libx) {
							List lst = libx.getListElements();
							if (lst != null) {
								return lst.indexOf(str);
							}
						}
					}

					return str;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					if (element instanceof Row r) {
						UiProperty prop = r.getPropertiesMap().get(getId());
						if (prop != null) {
							if (prop instanceof TextUiProperty) {
								return new TextCellEditor(viewer.getTable());
							} else if (prop instanceof ListBoxUiProperty libx) {
								return new ComboBoxCellEditor(viewer.getTable(),
										CoreUtils.transformListToStringArray(libx.getListElements()), SWT.READ_ONLY);
							}
						}
					}
					return null;
				}

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}
			};
			column.setEditingSupport(editSupport);
		}

		public String getName() {
			return property.getName();
		}

		@Override
		public String getUuid() {
			return property.getId();
		}

		public String getId() {
			return getUuid();
		}

		public TableViewerColumn getColumn() {
			return column;
		}

		public void setColumn(TableViewerColumn column) {
			this.column = column;
		}

		public UiProperty getProperty() {
			return property;
		}

		public void setProperty(UiProperty property) {
			this.property = property;
		}

		@Override
		public void setUuid(String uuid) {

		}

	}

}
