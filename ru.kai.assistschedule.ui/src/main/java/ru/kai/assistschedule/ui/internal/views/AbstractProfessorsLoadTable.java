package ru.kai.assistschedule.ui.internal.views;

import java.util.List;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import ru.kai.assistschedule.core.IProfessorLoadTable;
import ru.kai.assistschedule.core.cache.Constants;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.load.LoadEntry;

public abstract class AbstractProfessorsLoadTable implements
		IProfessorLoadTable {

	private GridTableViewer v;

	private Composite composite;

	public AbstractProfessorsLoadTable(Composite parent) {
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

		GridColumn column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.NN);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.SEMESTER);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.EDUCATION_FORM);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.SPECIALITY_AND_GROUP);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.DISCIPLINE);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.GROUP_AMOUNT);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.SUBGROUP_AMOUNT);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.WEEK_AMOUNT);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.LECTURES);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.PRACTICS);

		column = new GridColumn(v.getGrid(), SWT.NONE);
		column.setWidth(100);
		column.setText(Constants.ProfessorLoad.LABS);

		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.getGrid().setRowHeaderVisible(true);

		listeners();
	}

	protected abstract void listeners();

	protected abstract IBaseLabelProvider getLabelProvider();

	protected abstract IContentProvider getContentProvider();

	protected FirstLevelCache firstLevelCache;

	public void setFocus() {
		composite.setFocus();
	}

	public void dispose() {
		composite.dispose();
	}

	@Override
	public void setInput(List<LoadEntry> elements) {
		v.setInput(elements);
	}

	@Override
	public void setDataSource(FirstLevelCache firstLevelCache) {
		this.firstLevelCache = firstLevelCache;
	}
}
