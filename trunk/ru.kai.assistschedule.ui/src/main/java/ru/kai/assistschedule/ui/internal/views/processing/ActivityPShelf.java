package ru.kai.assistschedule.ui.internal.views.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.varia.FallbackErrorHandler;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.omg.CORBA._PolicyStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.ExcelWorker;
import ru.kai.assistschedule.core.GlobalStorage;
import ru.kai.assistschedule.core.calendar.SemestrBuilder;
import ru.kai.assistschedule.core.exceptions.SheduleIsNotOpenedException;
import ru.kai.assistschedule.core.external.interfaces.IStatus;
import ru.kai.assistschedule.ui.internal.views.status.StatusImpl;
import ru.kai.assistschedule.ui.observer.AddProfessorInScheduleEntry;
import ru.kai.assistschedule.ui.observer.IViewModel;
import ru.kai.assistschedule.ui.observer.ModelObserver;
import ru.kai.assistschedule.ui.observer.NotificationCenter;

public class ActivityPShelf implements IViewModel {


	protected NotificationCenter _notificationCenter;
	
	protected static final Logger LOG = LoggerFactory
			.getLogger(ActivityPShelf.class);

	private PShelf _shelf;

	// private Text schedullePathText;
	//
	// private Text loadPathText;

	// Получаем экземпляр консоли, для вывода в него вспомогательной информации
	private IStatus status = StatusImpl.getInstance();

	private Button _findProfessorsBtn;

	private Button _fallBtn;

	private Button _springBtn;

	private Combo _matchesPercentagesCombo;

	private Button _generateScheduleBtn;

	private final CDateTime _fromCdt;

	private final CDateTime _toCdt;

	public ActivityPShelf(Composite parent) {
		_notificationCenter = NotificationCenter.getDefaultCenter();
		_notificationCenter.addModel(this);
		for(ModelObserver observer: _notificationCenter.getObservers()) {
			if(observer instanceof ScheduleTable) {
				((ScheduleTable) observer).addModel(this);
			}
		}
		
		
		parent.setLayout(new FillLayout());
		_shelf = new PShelf(parent, SWT.NONE);

		// Optionally, change the renderer
		// shelf.setRenderer(new RedmondShelfRenderer());

		PShelfItem professorsShelf = new PShelfItem(_shelf, SWT.NONE);
		professorsShelf.setText("Преподователи");
		professorsShelf.getBody().setLayout(getGridLayout());

		// Create the first Group
		Group semesterGroup = new Group(professorsShelf.getBody(),
				SWT.SHADOW_IN);
		semesterGroup.setText("Семестр");
		semesterGroup.setLayout(new RowLayout(SWT.VERTICAL));
		semesterGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_fallBtn = new Button(semesterGroup, SWT.RADIO);
		_fallBtn.setText("Осень");
		_fallBtn.setSelection(true);

		_springBtn = new Button(semesterGroup, SWT.RADIO);
		_springBtn.setText("Весна");

		String matchesPercentages[] = { "50", "75", "100" };
		Group matchesPercentagesGroup = new Group(professorsShelf.getBody(),
				SWT.SHADOW_IN);
		matchesPercentagesGroup.setText("Процент совпадения");
		matchesPercentagesGroup.setLayout(new GridLayout());
		matchesPercentagesGroup.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		_matchesPercentagesCombo = new Combo(matchesPercentagesGroup,
				SWT.DROP_DOWN | SWT.READ_ONLY);
		_matchesPercentagesCombo.setItems(matchesPercentages);
		_matchesPercentagesCombo.select(2);
		_matchesPercentagesCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));

		_findProfessorsBtn = new Button(professorsShelf.getBody(), SWT.WRAP);
		_findProfessorsBtn.setText("Поиск и заполнение преподователей");
		_findProfessorsBtn
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ==============================================================

		PShelfItem scheduleSheld = new PShelfItem(_shelf, SWT.NONE);
		scheduleSheld.setText("Расписание");
		scheduleSheld.getBody().setLayout(getGridLayout());

		Group fromDateGroup = new Group(scheduleSheld.getBody(), SWT.SHADOW_IN);
		fromDateGroup.setText("Дата начала");
		fromDateGroup.setLayout(new GridLayout());
		fromDateGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_fromCdt = new CDateTime(fromDateGroup, CDT.BORDER | CDT.DROP_DOWN);
		_fromCdt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group toDateGroup = new Group(scheduleSheld.getBody(), SWT.SHADOW_IN);
		toDateGroup.setText("Дата окончания");
		toDateGroup.setLayout(new GridLayout());
		toDateGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_toCdt = new CDateTime(toDateGroup, CDT.BORDER | CDT.DROP_DOWN);
		_toCdt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		_generateScheduleBtn = new Button(scheduleSheld.getBody(), SWT.WRAP);
		_generateScheduleBtn.setText("Сформировать расписание");
		_generateScheduleBtn.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		_generateScheduleBtn.setEnabled(false);

		listeners();
	}

	private void listeners() {
		/**
		 * Назначение обработок кнопкам
		 */
		_findProfessorsBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				fullCheck();
			}
		});

		_fromCdt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				GlobalStorage.beginingOfSemestr = _fromCdt.getSelection();
				checkToEnableGenerateScheduleBtn();
			}

		});

		_toCdt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				GlobalStorage.endOfSemestr = _toCdt.getSelection();
				checkToEnableGenerateScheduleBtn();
			}

		});

		_generateScheduleBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				status.setText("");
				SemestrBuilder SB = new SemestrBuilder(GlobalStorage.beginingOfSemestr, GlobalStorage.endOfSemestr);
				try {
					status.append("========== ВЫВОД ОШИБОК ПО ВСЕМ НЕДЕЛЯМ ==========\n\n");
					ExcelWorker.AddInEveryWeek(status, SB);
					status.append("\n========== ВЫВОД ОШИБОК ПО ЧЕТ. НЕДЕЛЯМ ==========\n\n");
					ExcelWorker.AddInEvenWeek(status, SB);
					status.append("\n========== ВЫВОД ОШИБОК ПО НЕЧЕТ. НЕДЕЛЯМ ==========\n\n");
					ExcelWorker.AddInUnevenWeek(status, SB);
					status.append("\n========== ВЫВОД ОШИБОК ДО ЗАДАННОЙ ДАТЫ ==========\n\n");
					ExcelWorker.AddBefore(status, SB);
					status.append("\n========== ВЫВОД ОШИБОК ПОСЛЕ ЗАДАННОЙ ДАТЫ ==========\n\n");
					ExcelWorker.AddAfter(status, SB);
				} catch (SheduleIsNotOpenedException e) {
					status.setText("Расписание не открыто! Обработка отменена...");
				}
			}
		});

	}

	private void checkToEnableGenerateScheduleBtn() {
		if (GlobalStorage.beginingOfSemestr != null
				&& GlobalStorage.endOfSemestr != null
				&& (GlobalStorage.beginingOfSemestr
						.before(GlobalStorage.endOfSemestr) || GlobalStorage.beginingOfSemestr
						.equals(GlobalStorage.endOfSemestr))) {
			_generateScheduleBtn.setEnabled(true);
		} else {
			_generateScheduleBtn.setEnabled(false);
		}
	}

	/**
	 * Осуществляется поиск преподователей
	 */
	private void fullCheck() {
		// ====================== 1 - я проверка ==========================

		status.setText("");
		if (!ExcelWorker.isScheduleOpened()) {
			MessageBox box = new MessageBox(_shelf.getShell(),
					SWT.ICON_INFORMATION);
			box.setMessage("Необходимо загрузить расписание!");
			box.open();
			return;
		}
		if (!ExcelWorker.isLoadOpened()) {
			MessageBox box = new MessageBox(_shelf.getShell(),
					SWT.ICON_INFORMATION);
			box.setMessage("Необходимо загрузить нагрузку!");
			box.open();
			return;
		}

		LOG.debug("Началась 1 - я проверка!");
		GlobalStorage.matrix = ExcelWorker.searchEmptyCellsOfPMI();
		int maxLength = 0;
		for (int i = 0; i < GlobalStorage.matrix.length; i++)
			if (GlobalStorage.matrix[i][2].length() > maxLength)
				maxLength = GlobalStorage.matrix[i][2].length();
		String str;
//		for (str = ""; str.length() != (maxLength / 8); str += "\t") {
//		}
//		status.setFont(new Font(_shelf.getDisplay(), new FontData("Courier",
//				10, SWT.BOLD)));
		status.append(String.format("%-15s %-15s %-30.30s %15s\n", 
				"Строка:", "Группа:", "Предмет:", "Форма:"));
//		status.setFont(new Font(_shelf.getDisplay(), new FontData("Courier",
//				10, SWT.NORMAL)));
		
		
		for (int i = 0; i < GlobalStorage.matrix.length; i++) {
			status.append(i == 0 ? "" : "\n");
//			for (str = ""; (GlobalStorage.matrix[i][2].length() / 8 + str
//					.length()) != (maxLength / 8 + 1); str += "\t") {
//			}
			status.append(String.format("%-15s %-15s %-30.30s %15s", GlobalStorage.matrix[i][0], GlobalStorage.matrix[i][1],
					GlobalStorage.matrix[i][2], GlobalStorage.matrix[i][3]));
		}
		
		String foundRecords = String.format("\nНайдено записей: %d",
				GlobalStorage.matrix.length);
		printStatus(foundRecords);
		// ====================== 2 - я проверка ==========================
		professorSecondCheck();
//		LOG.debug("Началась 2 - я проверка!");
//		int suc = 0;
//		int season = 0; // autumn as default
//		if (_fallBtn.getSelection() == true)
//			season = 0; // autumn
//		else if (_springBtn.getSelection() == true)
//			season = 1; // spring
//		if (ExcelWorker.isLoadOpened()) {
//			int percent = 100;
//			if (_matchesPercentagesCombo.getSelectionIndex() == 0)
//				percent = 50;
//			else if (_matchesPercentagesCombo.getSelectionIndex() == 1)
//				percent = 75;
//			else if (_matchesPercentagesCombo.getSelectionIndex() == 2)
//				percent = 100;
//			GlobalStorage.matrix = ExcelWorker.openGeneralLoad(
//					GlobalStorage.matrix, season, percent);
//			if (GlobalStorage.matrix == null)
//				return;
//			status.setText("");
//			maxLength = 0;
//			for (int i = 0; i < GlobalStorage.matrix.length; i++)
//				if (GlobalStorage.matrix[i][2].length() > maxLength)
//					maxLength = GlobalStorage.matrix[i][2].length();
//
//			for (str = ""; str.length() != (maxLength / 8); str += "\t") {}
////			status.append("Строка: Группа: Предмет:" + str + "Форма:  Найдено:"
////					+ "\n");
//			status.append(String.format("%-15s %-15s %-"+maxLength+"s %15s %15s\n", 
//					"Строка:", "Группа:", "Предмет:", "Форма:", "Найдено:"));
//			List<String> links = new ArrayList<String>(GlobalStorage.matrix.length);
//			for (int i = 0; i < GlobalStorage.matrix.length; i++) {
//				if (GlobalStorage.matrix[i][4] != null)
//					suc++;
//				status.append(i == 0 ? "" : "\n");
//				for (str = ""; (GlobalStorage.matrix[i][2].length() / 8 + str
//						.length()) != (maxLength / 8 + 1); str += "\t") {
//				}
////				status.append(GlobalStorage.matrix[i][0] + "\t"
////						+ GlobalStorage.matrix[i][1] + "\t"
////						+ GlobalStorage.matrix[i][2] + str
////						+ GlobalStorage.matrix[i][3] + "\t"
////						+ GlobalStorage.matrix[i][4]);
//				status.append(String.format("%-15s %-15s %-"+maxLength+"s %15s %15s", 
//						GlobalStorage.matrix[i][0], GlobalStorage.matrix[i][1],
//						GlobalStorage.matrix[i][2], GlobalStorage.matrix[i][3],
//						GlobalStorage.matrix[i][4]));
//
//				String link = "Показать_" + GlobalStorage.matrix[i][0];
//				links.add(link);
//				status.append(" "+link+" ");
//				_notificationCenter.postNotification(ActivityPShelf.this, 
//						new AddProfessorInScheduleEntry(GlobalStorage.matrix[i][0], GlobalStorage.matrix[i][4]));
//			}
//			status.appendLinks(links);
//			foundRecords = String.format(
//					"\nНайдено записей: %d, из них Успешно найдено: %d",
//					GlobalStorage.matrix.length, suc);
//			printStatus(foundRecords);
//		}
	}
	
	private void professorSecondCheck() {
		LOG.debug("Началась 2 - я проверка!");
		
		int suc = 0;
		int season = 0; // autumn as default
		if (_fallBtn.getSelection() == true)
			season = 0; // autumn
		else if (_springBtn.getSelection() == true)
			season = 1; // spring
		if (ExcelWorker.isLoadOpened()) {
			int percent = 100;
			if (_matchesPercentagesCombo.getSelectionIndex() == 0)
				percent = 50;
			else if (_matchesPercentagesCombo.getSelectionIndex() == 1)
				percent = 75;
			else if (_matchesPercentagesCombo.getSelectionIndex() == 2)
				percent = 100;
			GlobalStorage.matrix = ExcelWorker.openGeneralLoad(GlobalStorage.matrix, season, percent);
			if (GlobalStorage.matrix == null)
				return;
			status.setText("");
			
			
			/**
			 * Копирование файла
			 */
			String newFile = (String) GlobalStorage.get("selectedSchedule");
			String oldFile = newFile.substring(0, newFile.length()-4) + "_OLD.xls";
			FileChannel src = null, dest = null;
			try {
				src = new FileInputStream(newFile).getChannel();
				dest = new FileOutputStream(oldFile).getChannel();
				src.transferTo(0, src.size(), dest);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					src.close();
					dest.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			int maxLength = 0;
			for (int i = 0; i < GlobalStorage.matrix.length; i++)
				if (GlobalStorage.matrix[i][2].length() > maxLength)
					maxLength = GlobalStorage.matrix[i][2].length();

			try {
				ExcelWorker.writableSchedule = Workbook.createWorkbook(new File(newFile), ExcelWorker.getSchedule());
			} catch (FileNotFoundException e) {
				status.setText("Закройте файл и повторите попытку!");
				return;
			} catch (Exception e) {e.printStackTrace();}
//			ExcelWorker.writableSchedule.createSheet("Расписание", 0);
			ExcelWorker.writableSheet = ExcelWorker.writableSchedule.getSheet(0);

			String str;
			for (str = ""; str.length() != (maxLength / 8); str += "\t") {
			}
			status.append("Строка: Группа: Предмет:" + str + "Форма:  Найдено:" + "\n");
			List<String> links = new ArrayList<String>(GlobalStorage.matrix.length);
			for (int i = 0; i < GlobalStorage.matrix.length; i++) {
				if (GlobalStorage.matrix[i][4] != null){
					suc++;
					Label label = new Label( 9, (new Integer(GlobalStorage.matrix[i][0])-1), GlobalStorage.matrix[i][4]);
					try {
						ExcelWorker.writableSheet.addCell(label);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					}catch (WriteException e) {
						e.printStackTrace();
					}
				}
				
				status.append(i == 0 ? "" : "\n");
				for (str = ""; (GlobalStorage.matrix[i][2].length() / 8 + str.length()) != (maxLength / 8 + 1); str += "\t") {
				}
				status.append(GlobalStorage.matrix[i][0] + "\t" + GlobalStorage.matrix[i][1] + "\t" + GlobalStorage.matrix[i][2] + str + GlobalStorage.matrix[i][3] + "\t" + GlobalStorage.matrix[i][4]);
				
				String link = "Показать_" + GlobalStorage.matrix[i][0];
				links.add(link);
				status.append(" "+link+" ");
				_notificationCenter.postNotification(ActivityPShelf.this, 
						new AddProfessorInScheduleEntry(GlobalStorage.matrix[i][0], GlobalStorage.matrix[i][4]));
			}
			status.appendLinks(links);
			
			try {
				ExcelWorker.writableSchedule.write();
				ExcelWorker.writableSchedule.close();
			} catch (WriteException e) {
				System.out.print(e.getMessage());
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
			
			ExcelWorker.closeSchedule();
			ExcelWorker.openSchedule(newFile);
			
			status.append("\nНайдено записей: " + GlobalStorage.matrix.length + "\t\tУспешно найдено: " + suc);
		}
	}

	private void printStatus(String string) {
		status.append(string);
		LOG.debug(string);
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
