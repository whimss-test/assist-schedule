package ru.kai.assistschedule.ui.internal.views.setting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.ExcelWorker;
import ru.kai.assistschedule.core.GlobalStorage;
import ru.kai.assistschedule.core.MainCommand;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.external.interfaces.IStatus;
import ru.kai.assistschedule.core.utils.ExcelUtils;
import ru.kai.assistschedule.ui.internal.views.status.StatusImpl;
import ru.kai.assistschedule.ui.internal.widgets.LectureRoomsSetting;
import ru.kai.assistschedule.ui.internal.widgets.ProgressBarModalWindow;

public class ActivityPShelf {

	protected static final Logger LOG = LoggerFactory
			.getLogger(ActivityPShelf.class);

	private PShelf _shelf;

	// private Text schedullePathText;
	//
	// private Text loadPathText;

	// Получаем экземпляр консоли, для вывода в него вспомогательной информации
	private IStatus status = StatusImpl.getInstance();

	public ActivityPShelf(final Composite parent) {
		parent.setLayout(new FillLayout());
		_shelf = new PShelf(parent, SWT.NONE);

		// Optionally, change the renderer
		// shelf.setRenderer(new RedmondShelfRenderer());

		PShelfItem upload = new PShelfItem(_shelf, SWT.NONE);
		upload.setText("Загрузка");
		upload.getBody().setLayout(getGridLayout());

		Button uploadScheduleBtn = new Button(upload.getBody(), SWT.FLAT);
		uploadScheduleBtn.setText("Расписание");
		uploadScheduleBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uploadScheduleBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				openShedule();
			}

		});

		Button uploadProfessorsLoadBtn = new Button(upload.getBody(), SWT.FLAT);
		uploadProfessorsLoadBtn.setText("Нагрузка");
		uploadProfessorsLoadBtn.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		uploadProfessorsLoadBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				openProfessorsLoad();
			}

		});

		PShelfItem download = new PShelfItem(_shelf, SWT.NONE);
		download.setText("Выгрузка");
		download.getBody().setLayout(getGridLayout());

		Button downloadScheduleBtn = new Button(download.getBody(), SWT.FLAT);
		downloadScheduleBtn.setText("Расписание");
		downloadScheduleBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downloadScheduleBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				LOG.info("downloadScheduleBtn: " + evt);
				String newSheetFilePath = (String) GlobalStorage.get("selectedSchedule");
				ExcelUtils.clearSheet(newSheetFilePath, 0);
				ExcelUtils.fillSheet(newSheetFilePath, 0, FirstLevelCache.getInstance().getEntries());
				//				/**
//				 * Копирование файла
//				 */
//				String newFile = (String) GlobalStorage.get("selectedSchedule");
//				String oldFile = newFile.substring(0, newFile.length()-4) + "_NEW.xls";
//				FileChannel src = null, dest = null;
//				try {
//					src = new FileInputStream(newFile).getChannel();
//					dest = new FileOutputStream(oldFile).getChannel();
//					src.transferTo(0, src.size(), dest);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally{
//					try {
//						src.close();
//						dest.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
			}
		});
		
		PShelfItem lectureRooms = new PShelfItem(_shelf, SWT.NONE);
		lectureRooms.setText("Аудитории");
		lectureRooms.getBody().setLayout(getGridLayout());

		Button lectureRoomsAutocompleteBtn = new Button(lectureRooms.getBody(),
				SWT.FLAT);
		lectureRoomsAutocompleteBtn.setText("Автозаполнение");
		lectureRoomsAutocompleteBtn.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		Button lectureRoomsSettingBtn = new Button(lectureRooms.getBody(), SWT.FLAT);
		lectureRoomsSettingBtn.setText("Настройка аудиторий");
		lectureRoomsSettingBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lectureRoomsSettingBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new LectureRoomsSetting(parent);
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

	/**
	 * Только копируют в schedullePathText
	 * 
	 * @author Дамир
	 */
	private void openShedule() {
		LOG.debug("Создаем Диалог На ОТКРЫТИЕ(!) файла, прописываем title, "
				+ "путь по умолчанию и фильтры. "
				+ "Получаем String переменную результата и выводим её.");
		LOG.info("Создаем Диалог На ОТКРЫТИЕ(!) файла, прописываем title, "
				+ "путь по умолчанию и фильтры. "
				+ "Получаем String переменную результата и выводим её.");

		try {
			/**
			 * Создаем Диалог На ОТКРЫТИЕ(!) файла, прописываем title, путь по
			 * умолчанию и фильтры. Получаем String переменную результата и
			 * выводим её.
			 */
			FileDialog fd = new FileDialog(_shelf.getShell(), SWT.OPEN);
			fd.setText("Открыть расписание");
			fd.setFilterPath("C:/");
			String[] filterExt = { "*.xls" };
			fd.setFilterExtensions(filterExt);
			if ((GlobalStorage.selectedSchedule = fd.open()) != null) {
				// schedullePathText.setText(GlobalStorage.selectedSchedule);
				 ProgressBarModalWindow barModalWindow = new
				 ProgressBarModalWindow(_shelf);
				GlobalStorage.put("selectedSchedule",
						GlobalStorage.selectedSchedule);
				try {
					ExcelWorker.openSchedule(GlobalStorage.selectedSchedule);
					FirstLevelCache firstLevelCache = FirstLevelCache
							.getInstance();
					firstLevelCache.readFromSheet();
					MainCommand.setFirstLevelCache(firstLevelCache);
				} catch (Exception e) {
					status.setText(e.getLocalizedMessage());
				}
				status.setText("Расписание открыто");
			}
			LOG.debug("openShedule has finished");
		} catch (Exception exception) {
			exception.getStackTrace();
			LOG.error(exception.getMessage());
		}
	}

	/**
	 * Только копирует строку в loadPathText
	 * 
	 * @author Дамир
	 */
	private void openProfessorsLoad() {
		LOG.debug("OpenLoadOfProffs");
		try {
			/**
			 * Создаем Диалог На ОТКРЫТИЕ(!) файла, прописываем title, путь по
			 * умолчанию и фильтры. Получаем String переменную результата и
			 * выводим её.
			 */
			FileDialog fd = new FileDialog(_shelf.getShell(), SWT.OPEN);
			fd.setText("Открыть нагрузку");
			fd.setFilterPath("C:/");
			String[] filterExt = { "*.xls" };
			fd.setFilterExtensions(filterExt);
			if ((GlobalStorage.selectedProffsLoad = fd.open()) != null) {
				// loadPathText.setText(GlobalStorage.selectedProffsLoad);
				GlobalStorage.put("selectedProffsLoad",
						GlobalStorage.selectedProffsLoad);
				try {
					ExcelWorker.openLoad(GlobalStorage.selectedProffsLoad);
					FirstLevelCache firstLevelCache = FirstLevelCache
							.getInstance();
					firstLevelCache.readLoadSheet();
					MainCommand.setFirstLevelCache(firstLevelCache);
				} catch (Exception e) {
					status.setText(e.getLocalizedMessage());
					LOG.debug(e.toString());
				}
				status.setText("Нагрузка открыта");
			}
		} catch (Exception exception) {
			LOG.error(exception.getMessage());
		}
	}

	public void setFocus() {
		_shelf.setFocus();
	}

	public void dispose() {
		_shelf.dispose();
	}

}
