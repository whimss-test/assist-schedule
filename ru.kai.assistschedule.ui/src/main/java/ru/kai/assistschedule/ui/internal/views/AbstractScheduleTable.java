package ru.kai.assistschedule.ui.internal.views;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerEditor;
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

import ru.kai.assistschedule.core.IScheduleTable;
import ru.kai.assistschedule.core.cache.Constants;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.ui.internal.views.utils.Popup;
import ru.kai.assistschedule.ui.internal.widgets.ExcelFilter;

public abstract class AbstractScheduleTable implements IScheduleTable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private GridTableViewer v;

	private Composite composite;

	/**
	 * Сохраняемые настройки окна фильтра
	 */
	private Map<String, List<Object>> excelFilterPreferences = 
			new HashMap<String, List<Object>>();

	public AbstractScheduleTable(Composite parent) {
		parent.setLayout(new FillLayout());
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

		v.setCellEditors(new CellEditor[] { new TextCellEditor(v.getGrid()),
				new TextCellEditor(v.getGrid()) });
		v.setCellModifier(new ICellModifier() {

			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				return "Column " + property + " => " + element.toString();
			}

			public void modify(Object element, String property, Object value) {

			}

		});

		v.setColumnProperties(new String[] { "1", "2" });
		// v.setColumnProperties(new String[]
		// {"Группа","Время","Дисциплина","Вид занятий","Преподователь","Кафедра"});

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

		final GridColumn groupColumn = new GridColumn(v.getGrid(), SWT.NONE);
		groupColumn.setWidth(100);
		groupColumn.setText(Constants.Schedule.GROUP);
		groupColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(groupColumn,
						new ArrayList<String>(firstLevelCache.getGroupNames()))
						.show();
			}

		});

		final GridColumn dayColumn = new GridColumn(v.getGrid(), SWT.NONE);
		dayColumn.setWidth(100);
		dayColumn.setText(Constants.Schedule.DAY_OF_WEEK);
		dayColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(dayColumn,
						new ArrayList<String>(firstLevelCache.getDaysOfWeek()))
						.show();
			}

		});

		final GridColumn timeColumn = new GridColumn(v.getGrid(), SWT.NONE);
		timeColumn.setWidth(100);
		timeColumn.setText(Constants.Schedule.TIME);
		timeColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(timeColumn,
						new ArrayList<String>(firstLevelCache.getTimes()))
						.show();
			}

		});

		final GridColumn dateColumn = new GridColumn(v.getGrid(), SWT.NONE);
		dateColumn.setWidth(100);
		dateColumn.setText(Constants.Schedule.DATE);
		dateColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(dateColumn,
						new ArrayList<String>(firstLevelCache.getDates()))
						.show();
			}

		});

		final GridColumn disciplineColumn = new GridColumn(v.getGrid(),
				SWT.NONE);
		disciplineColumn.setWidth(100);
		disciplineColumn.setText(Constants.Schedule.DISCIPLINE);
		disciplineColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(disciplineColumn,
						new ArrayList<String>(firstLevelCache.getDisciplines()))
						.show();
			}

		});

		final GridColumn lessonTypeColumn = new GridColumn(v.getGrid(),
				SWT.NONE);
		lessonTypeColumn.setWidth(100);
		lessonTypeColumn.setText(Constants.Schedule.LESSON_TYPE);
		lessonTypeColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(lessonTypeColumn,
						new ArrayList<String>(firstLevelCache.getLessonTypes()))
						.show();
			}

		});

		final GridColumn classRoomColumn = new GridColumn(v.getGrid(), SWT.NONE);
		classRoomColumn.setWidth(100);
		classRoomColumn.setText(Constants.Schedule.CLASSROOM);
		classRoomColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(classRoomColumn,
						new ArrayList<String>(firstLevelCache.getClassRooms()))
						.show();
			}

		});

		final GridColumn buildingColumn = new GridColumn(v.getGrid(), SWT.NONE);
		buildingColumn.setWidth(100);
		buildingColumn.setText(Constants.Schedule.BUILDING);
		buildingColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(buildingColumn,
						new ArrayList<String>(firstLevelCache.getBuildings()))
						.show();
			}

		});

		final GridColumn positionColumn = new GridColumn(v.getGrid(), SWT.NONE);
		positionColumn.setWidth(100);
		positionColumn.setText(Constants.Schedule.POSITION);
		positionColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(positionColumn,
						new ArrayList<String>(firstLevelCache.getPositions()))
						.show();
			}

		});

		final GridColumn professorColumn = new GridColumn(v.getGrid(), SWT.NONE);
		professorColumn.setWidth(100);
		professorColumn.setText(Constants.Schedule.PROFESSOR);
		professorColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(professorColumn,
						new ArrayList<String>(firstLevelCache.getProfessors()))
						.show();
			}

		});

		final GridColumn departmentColumn = new GridColumn(v.getGrid(),
				SWT.NONE);
		departmentColumn.setWidth(100);
		departmentColumn.setText(Constants.Schedule.DEPARTMENT);
		departmentColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				makeExcelFilter(departmentColumn,
						new ArrayList<String>(firstLevelCache.getDepartments()))
						.show();
			}

		});

		// v.setInput(getInput());
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.getGrid().setRowHeaderVisible(true);

		listeners();
	}

	private ExcelFilter makeExcelFilter(GridColumn column, List<String> list) {
		return new ExcelFilter(column, v, list);
	}

	/**
	 * Ссылка на настройки фильтра по названию колонки
	 * @param columnName
	 * @return
	 */
	private List<Object> getExcelFilter(String columnName) {
		return excelFilterPreferences.get(columnName);
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

		Popup.make(v.getGrid().getParent(), "Расписание загружено!", 4000,
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
