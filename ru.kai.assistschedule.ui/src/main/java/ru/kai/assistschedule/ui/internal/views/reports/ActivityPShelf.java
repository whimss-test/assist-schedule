package ru.kai.assistschedule.ui.internal.views.reports;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.ExcelWorker;
import ru.kai.assistschedule.ui.internal.views.status.StatusImpl;

public class ActivityPShelf {

	protected static final Logger LOG = LoggerFactory
			.getLogger(ActivityPShelf.class);

	private PShelf _shelf;

	// private Text schedullePathText;
	//
	// private Text loadPathText;

	// Получаем экземпляр консоли, для вывода в него вспомогательной информации
	// private IStatus status = StatusImpl.getInstance();
	
	private String selectedProf = "";

	public ActivityPShelf(Composite parent) {
		parent.setLayout(new FillLayout());
		_shelf = new PShelf(parent, SWT.NONE);

		// Optionally, change the renderer
		// shelf.setRenderer(new RedmondShelfRenderer());

		PShelfItem professorsScheduleShelf = new PShelfItem(_shelf, SWT.NONE);
		professorsScheduleShelf.setText("Расписание преподователей");
		professorsScheduleShelf.getBody().setLayout(getGridLayout());

		Button uploadScheduleBtn = new Button(
				professorsScheduleShelf.getBody(), SWT.FLAT);
		uploadScheduleBtn.setText("Общее");
		uploadScheduleBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uploadScheduleBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// openShedule();
			}

		});

		Button uploadProfessorsLoadBtn = new Button(
				professorsScheduleShelf.getBody(), SWT.FLAT);
		uploadProfessorsLoadBtn.setText("Частное");
		uploadProfessorsLoadBtn.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		uploadProfessorsLoadBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// openProfessorsLoad();
				if(selectedProf.isEmpty()) {
					MessageBox box = new MessageBox(_shelf.getShell(),
							SWT.ICON_INFORMATION);
					box.setMessage("Необходимо выбрать преподователя!");
					box.open();
					return;
				}
				ExcelWorker.generateProffessorSchedule(selectedProf);
			}

		});
		
		Combo combo = new Combo(professorsScheduleShelf.getBody(), SWT.DROP_DOWN);
		String[] professors = new String[ExcelWorker.getPMIprofessors().size()];
		ExcelWorker.getPMIprofessors().toArray(professors);
	    combo.setItems(professors);
	    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    combo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
//				System.out.println(((Combo)e.getSource()).getSelectionIndex());
				selectedProf = ((Combo)e.getSource()).getText();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}

	private GridLayout getGridLayout() {
		GridLayout layout = new GridLayout();
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;

		return layout;
	}

	public void setFocus() {
		_shelf.setFocus();
	}

	public void dispose() {
		_shelf.dispose();
	}

}
