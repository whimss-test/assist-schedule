package ru.kai.assistschedule.ui.internal.widgets;

import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.kai.assistschedule.ui.model.schedule.sort.DaySorter;
import ru.kai.assistschedule.ui.model.schedule.sort.GroupSorter;

public class ExcelFilter {
	
	private GridColumn column;

	private Shell columnShell;
	
	private GridTableViewer gridTableViewer;
	
	public ExcelFilter(GridColumn column) {
		this(column, null);
	}
	
	public ExcelFilter(GridColumn column, GridTableViewer gridTableViewer) {
		super();
		this.column = column;
		this.gridTableViewer = gridTableViewer;

		final GridColumn dayColumn = column;
		Shell mainShell = dayColumn.getParent().getShell();
		// GridColumn column = dayColumn;
		// column.getHeaderRenderer()
		final Shell child = new Shell(mainShell, SWT.DOUBLE_BUFFERED);
		child.setLocation(
				dayColumn.getParent().toDisplay(0, 0).x
						+ dayColumn.getHeaderRenderer().getBounds().x
						+ dayColumn.getHeaderRenderer().getBounds().width
						- 150,
				dayColumn.getParent().toDisplay(0, 0).y
						+ dayColumn.getHeaderRenderer().getBounds().height);

		child.setLayout(new GridLayout());
		final Composite composite = new Composite(child, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, true, true));
		createView(composite);
		child.setSize(0, 0);
		child.open();
		
		dayColumn.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int i = 1;
				while (i++ < 11) {
					child.setSize(150, i * 25);
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		child.addShellListener(new ShellListener() {
			
			@Override
			public void shellIconified(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
			
			@Override
			public void shellDeiconified(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
			
			@Override
			public void shellDeactivated(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println("shellDeactivated");
				dayColumn.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i = 10;
						while (i-- > 0) {
							child.setSize(150, i * 25);
							try {
								TimeUnit.MILLISECONDS.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						child.close();
					}
				});
			}
			
			@Override
			public void shellClosed(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
		});
	}
	
	private void createView(Composite composite) {
		composite.setLayout(new FormLayout());
		
		Button buttonASC = new Button(composite, SWT.FLAT);
		buttonASC.setText("Прямая сортировка");
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		buttonASC.setLayoutData(data);
		buttonASC.addSelectionListener(new SelectionAdapter() {
			private boolean isDirectSort = false;
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gridTableViewer == null) {
					return;
				}
				// TODO Auto-generated method stub
				if (column.getText().equals("Группа")) {
					gridTableViewer.setSorter(new GroupSorter(true));
				} else if (column.getText().equals("День")) {
					gridTableViewer.setSorter(new DaySorter(true));
				}
				column.setSort(SWT.DOWN);
			}
			
		});
		
		
		Button buttonDESC = new Button(composite, SWT.FLAT);
		buttonDESC.setText("Обратная сортировка");
		data = new FormData();
		data.top = new FormAttachment(buttonASC, 10);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		buttonDESC.setLayoutData(data);
		buttonDESC.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gridTableViewer == null) {
					return;
				}
				// TODO Auto-generated method stub
				if (column.getText().equals("Группа")) {
					gridTableViewer.setSorter(new GroupSorter(false));
				} else if (column.getText().equals("День")) {
					gridTableViewer.setSorter(new DaySorter(false));
				}
				column.setSort(SWT.UP);
			}
			
		});
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Фильтрация элементов в дереве");
		data = new FormData();
		data.top = new FormAttachment(buttonDESC, 20);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		label.setLayoutData(data);
		
		Text textFilter = new Text(composite, SWT.BORDER);
		textFilter.setToolTipText("Начни фильтровать=)");
		data = new FormData();
		data.top = new FormAttachment(label, 10);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		textFilter.setLayoutData(data);
		
		TreeViewer treeViewerFilteredData = new TreeViewer(composite);
		data = new FormData();
		data.top = new FormAttachment(textFilter, 10);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		treeViewerFilteredData.getTree().setLayoutData(data);
	}
	
}
