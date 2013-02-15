package ru.kai.assistschedule.ui.internal.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.IScheduleTable;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.ui.internal.widgets.ExcelFilter;

public abstract class AbstractScheduleTable implements IScheduleTable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private GridTableViewer v;

	private Composite composite;

	public AbstractScheduleTable(Composite parent) {
		parent.setLayout(new FillLayout());
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		v = new GridTableViewer(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		v.setLabelProvider(getLabelProvider());
		v.setContentProvider(getContentProvider());
		v.getGrid().setCellSelectionEnabled(true);

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
		groupColumn.setText("Группа");
		groupColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(groupColumn, v, new ArrayList<String>(firstLevelCache.getGroupNames()));
				logger.debug("firstLevelCache = " + String.valueOf(firstLevelCache));
			}
			
		});
		

		final GridColumn dayColumn = new GridColumn(v.getGrid(), SWT.NONE);
		dayColumn.setWidth(100);
		dayColumn.setText("День недели");
		dayColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(dayColumn, v, new ArrayList<String>(firstLevelCache.getDaysOfWeek()));
			}
			
		});
		
		final GridColumn timeColumn = new GridColumn(v.getGrid(), SWT.NONE);
		timeColumn.setWidth(100);
		timeColumn.setText("Время");
		timeColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(timeColumn, v, 
						new ArrayList<String>(firstLevelCache.getTimes()));
			}
			
		});
		
		final GridColumn dateColumn = new GridColumn(v.getGrid(), SWT.NONE);
		dateColumn.setWidth(100);
		dateColumn.setText("Дата");
		dateColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(dateColumn, v, 
						new ArrayList<String>(firstLevelCache.getDates()));
			}
			
		});

		final GridColumn disciplineColumn = new GridColumn(v.getGrid(), SWT.NONE);
		disciplineColumn.setWidth(100);
		disciplineColumn.setText("Дисциплина");
		disciplineColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(disciplineColumn, v, 
						new ArrayList<String>(firstLevelCache.getDisciplines()));
			}
			
		});

		final GridColumn lessonTypeColumn = new GridColumn(v.getGrid(), SWT.NONE);
		lessonTypeColumn.setWidth(100);
		lessonTypeColumn.setText("Вид занятий");
		lessonTypeColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(lessonTypeColumn, v, 
						new ArrayList<String>(firstLevelCache.getLessonTypes()));
			}
			
		});

		final GridColumn classRoomColumn = new GridColumn(v.getGrid(), SWT.NONE);
		classRoomColumn.setWidth(100);
		classRoomColumn.setText("Аудитория");
		classRoomColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(classRoomColumn, v, 
						new ArrayList<String>(firstLevelCache.getClassRooms()));
			}
			
		});

		final GridColumn buildingColumn = new GridColumn(v.getGrid(), SWT.NONE);
		buildingColumn.setWidth(100);
		buildingColumn.setText("Здание");
		buildingColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(buildingColumn, v, 
						new ArrayList<String>(firstLevelCache.getBuildings()));
			}
			
		});

		final GridColumn positionColumn = new GridColumn(v.getGrid(), SWT.NONE);
		positionColumn.setWidth(100);
		positionColumn.setText("Должность");
		positionColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(positionColumn, v, 
						new ArrayList<String>(firstLevelCache.getPositions()));
			}
			
		});

		final GridColumn professorColumn = new GridColumn(v.getGrid(), SWT.NONE);
		professorColumn.setWidth(100);
		professorColumn.setText("Преподаватель");
		professorColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(professorColumn, v, 
						new ArrayList<String>(firstLevelCache.getProfessors()));
			}
			
		});

		final GridColumn departmentColumn = new GridColumn(v.getGrid(), SWT.NONE);
		departmentColumn.setWidth(100);
		departmentColumn.setText("Кафедра");
		departmentColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(departmentColumn, v, 
						new ArrayList<String>(firstLevelCache.getDepartments()));
			}
			
		});

		// v.setInput(getInput());
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.getGrid().setRowHeaderVisible(true);

		listeners();
	}

	protected abstract void listeners();

	protected abstract IBaseLabelProvider getLabelProvider();

	protected abstract IContentProvider getContentProvider();
	
	protected FirstLevelCache firstLevelCache;

	@Override
	public void setInput(List<ScheduleEntry> elements) {
		v.setInput(elements);
	}

	public void setFocus() {
		composite.setFocus();
	}

	public void dispose() {
		composite.dispose();
	}

	private void createDropDownWindow() {

	}

	@Override
	public void setDataSource(FirstLevelCache firstLevelCache) {
		this.firstLevelCache = firstLevelCache;
	}
}
