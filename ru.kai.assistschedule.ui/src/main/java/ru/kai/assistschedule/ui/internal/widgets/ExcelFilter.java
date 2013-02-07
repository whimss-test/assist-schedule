package ru.kai.assistschedule.ui.internal.widgets;

import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExcelFilter {
	
	private GridColumn column;

	private Shell columnShell;
	
	public ExcelFilter(GridColumn column) {
		super();
		this.column = column;
		
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
		composite.setLayout(new GridLayout());
		Label label = new Label(composite, SWT.None);
		label.setText("Hello Child!");
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
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
	
	
}
