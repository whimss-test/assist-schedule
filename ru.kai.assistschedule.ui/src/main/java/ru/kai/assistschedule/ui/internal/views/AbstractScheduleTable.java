package ru.kai.assistschedule.ui.internal.views;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerEditor;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import ru.kai.assistschedule.core.IScheduleTable;
import ru.kai.assistschedule.core.cache.Constants;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.ui.internal.views.status.StatusImpl;
import ru.kai.assistschedule.ui.internal.views.utils.Popup;
import ru.kai.assistschedule.ui.internal.widgets.ExcelFilter;
import ru.kai.assistschedule.ui.model.ScheduleEntryCellModifier;
import ru.kai.assistschedule.ui.observer.IViewModel;
import ru.kai.assistschedule.ui.observer.LinkToScheduleEntry;
import ru.kai.assistschedule.ui.observer.ModelObserver;
import ru.kai.assistschedule.ui.observer.Notification;
import ru.kai.assistschedule.ui.observer.NotificationCenter;

public abstract class AbstractScheduleTable implements IScheduleTable, ModelObserver {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String[] PROPS = { Constants.Schedule.GROUP, 
		Constants.Schedule.DAY_OF_WEEK, Constants.Schedule.TIME, 
		Constants.Schedule.DATE, Constants.Schedule.DISCIPLINE, 
		Constants.Schedule.LESSON_TYPE, Constants.Schedule.CLASSROOM,
		Constants.Schedule.BUILDING, Constants.Schedule.POSITION,
		Constants.Schedule.PROFESSOR, Constants.Schedule.DEPARTMENT };
	
	protected GridTableViewer v;

	private Composite composite;

	/**
	 * Сохраняемые настройки окна фильтра
	 */
	private Map<String, Set<String>> excelFilterSelected = 
			new HashMap<String, Set<String>>();
	
	/**
	 * Храним ссылки на окошки с фильтрами
	 */
	private Map<String, ExcelFilter> excelFilres = new HashMap<String, ExcelFilter>();
	
	/**
	 * Храним ссылки на колонки таблицы
	 */
	private Map<String, GridColumn> columns = new HashMap<String, GridColumn>();
	
	/**
     * Содержит все зарегистрированные модели для данного контроллера.
     */
    protected Set<IViewModel> registeredModels = new HashSet<IViewModel>();;

	@Override
	public void update(IViewModel model, Notification notice) {
		// TODO Auto-generated method stub
		if(notice instanceof LinkToScheduleEntry) {
			List<ScheduleEntry> list = (List<ScheduleEntry>) v.getInput();
			IStructuredSelection selection;
			for(ScheduleEntry entry: list) {
				if(entry.id == ((LinkToScheduleEntry) notice).id) {
					selection = new StructuredSelection(entry);
					v.setSelection(selection, true);
					break;
				}
			}
		}
	}

	@Override
	public boolean containsSender(IViewModel sender) {
		return registeredModels.contains(sender);
	}

	public AbstractScheduleTable(Composite parent) {
		parent.setLayout(new FillLayout());
		registeredModels.add((IViewModel)StatusImpl.getInstance());
		NotificationCenter.getDefaultCenter().addObserver(this);
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		v = new GridTableViewer(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		v.setLabelProvider(getLabelProvider());
		v.setContentProvider(getContentProvider());
		v.getGrid().setCellSelectionEnabled(true);

		Bundle bundle = Platform.getBundle("ru.kai.assistschedule.ui");
		URL fileURL = bundle.getEntry("resources/font/PTF55F.TTF");
		File file = null;
		try {
			file = new File(FileLocator.resolve(fileURL).toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (!file.exists()) {
			throw new IllegalStateException(file.toString());
		}
		if (!parent.getDisplay().loadFont(file.toString())) {
			throw new IllegalStateException(file.toString());
		}

		v.getGrid().setFont(
				new Font(parent.getDisplay(), new FontData("PT Serif", 10,
						SWT.NORMAL)));

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				v) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR);
			}
		};

		GridViewerEditor.create(v, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
		for(final String columnTitle: PROPS) {
			final GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
			column.setWidth(100);
			column.setText(columnTitle);
			columns.put(columnTitle, column);
			column.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					
					excelFilres.get(columnTitle).show();
//					makeExcelFilter(column, firstLevelCache.getUniqueSetByName(columnTitle)).show();
				}

			});
		}

		// v.setInput(getInput());
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.getGrid().setRowHeaderVisible(true);
		
		Grid grid = v.getGrid();
		CellEditor[] editors = new CellEditor[11];
	    editors[0] = new TextCellEditor(grid);
	    editors[1] = new ComboBoxCellEditor(grid, new String[]{"пн","вт","ср","чт","пт","сб"}, SWT.READ_ONLY);
	    editors[2] = new ComboBoxCellEditor(grid, new String[]{"8:00","9:40","11:30","13:10","15:00","16:40", "18:15", "19:45"}, SWT.READ_ONLY);
	    editors[3] = new TextCellEditor(grid);
	    editors[4] = new TextCellEditor(grid);
	    editors[5] = new ComboBoxCellEditor(grid, new String[]{"лек","пр","л.р.","и.з.",""}, SWT.READ_ONLY);
	    editors[6] = new TextCellEditor(grid);
	    editors[7] = new TextCellEditor(grid);
	    editors[8] = new TextCellEditor(grid);
	    editors[9] = new TextCellEditor(grid);
	    editors[10] = new TextCellEditor(grid);

		v.setColumnProperties(PROPS);
	    v.setCellModifier(new ScheduleEntryCellModifier(v));
	    v.setCellEditors(editors);
		
		listeners();
	}

	private ExcelFilter makeExcelFilter(GridColumn column, Set<String> unique) {
		return new ExcelFilter(column, v, 
				unique, excelFilterSelected.get(column.getText()));
	}

	protected abstract void listeners();

	protected abstract IBaseLabelProvider getLabelProvider();

	protected abstract IContentProvider getContentProvider();

	protected FirstLevelCache firstLevelCache;

	@Override
	public void setInput(List<ScheduleEntry> elements) {
		List<ScheduleEntry> elem = new ArrayList<ScheduleEntry>(elements);

		for (int i = firstLevelCache.getStack().size() - 1; i >= 0; i--) {
			elem.remove((int) firstLevelCache.getStack().get(i));
		}

		v.setInput(elem);
		for(final String colTitle: PROPS) {
			Set<String> uniqueElementsSet = 
					firstLevelCache.getUniqueSetByName(colTitle);
			if(excelFilterSelected.get(colTitle) == null) {
				excelFilterSelected.put(colTitle, new HashSet<String>(uniqueElementsSet));
			}
			if(excelFilres.get(colTitle) == null) {
				excelFilres.put(colTitle, 
						new ExcelFilter(columns.get(colTitle), v, 
								uniqueElementsSet, excelFilterSelected.get(colTitle)));
			}
		}
		
		Popup.make(v.getGrid().getParent(), "Расписание загружено!", 3000,
				SWT.COLOR_GREEN).show();
	}

	public void setFocus() {
		composite.setFocus();
	}

	public void dispose() {
		composite.dispose();
	}

	@Override
	public void setDataSource(FirstLevelCache firstLevelCache) {
		this.firstLevelCache = firstLevelCache;
	}
}
