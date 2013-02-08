package ru.kai.assistschedule.ui.internal.views;

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

import ru.kai.assistschedule.core.IScheduleTable;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.ui.internal.widgets.ExcelFilter;

public abstract class AbstractScheduleTable implements IScheduleTable {

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
				ExcelFilter filter = new ExcelFilter(groupColumn, v);
			}
			
		});
		

		final GridColumn dayColumn = new GridColumn(v.getGrid(), SWT.NONE);
		dayColumn.setWidth(100);
		dayColumn.setText("День");
		dayColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				ExcelFilter filter = new ExcelFilter(dayColumn, v);
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
				ExcelFilter filter = new ExcelFilter(timeColumn);
			}
			
		});
		
		GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Дата");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Дисциплина");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Вид занятий");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Аудитория");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Здание");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Должность");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Преподователь");

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText("Кафедра");

		// v.setInput(getInput());
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.getGrid().setRowHeaderVisible(true);

		listeners();
	}

	protected abstract void listeners();

	protected abstract IBaseLabelProvider getLabelProvider();

	protected abstract IContentProvider getContentProvider();

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
}
