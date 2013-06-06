package ru.kai.assistschedule.core;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kai.assistschedule.core.calendar.Class;

import ru.kai.assistschedule.core.cache.LectureRoom;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.Time;
import ru.kai.assistschedule.core.calendar.SemestrBuilder;
import ru.kai.assistschedule.core.exceptions.ExcelFileIsNotOpenedException;
import ru.kai.assistschedule.core.exceptions.SheduleIsNotOpenedException;
import ru.kai.assistschedule.core.external.interfaces.IStatus;
import ru.kai.assistschedule.core.utils.FileFinder;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelWorker {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(ExcelWorker.class);

    private static Workbook workbookSchedule, workbookLoad;
    
    public static WritableWorkbook writableSchedule;
    
    public static WritableSheet writableSheet;

    private static Sheet sheetOfSchedule, sheetOfLoad;

    private static Range[] range;
    
	public static Workbook getSchedule(){
		return workbookSchedule;
	}
	
	public static Workbook getLoad(){
		return workbookLoad;
	}

    // ****************************//
    // ========================== //
    // ДЕЖУРНЫЕ ФУНКЦИИ!!! //
    // ========================== //
    // ****************************//
    /**
     * Открывает книгу с расписанием Excel 97-03 по пути
     * 
     * @param inputName - путь к файлу
     */
    public static boolean openSchedule(String inputName) {
        try {
            workbookSchedule = Workbook.getWorkbook(new File(inputName));
            return true;
        } catch (BiffException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Открывает книгу с нагрузкой Excel 97-03 по пути
     * 
     * @param inputName - путь к файлу
     */
    public static boolean openLoad(String inputName) {
        try {
            workbookLoad = Workbook.getWorkbook(new File(inputName));
            return true;
        } catch (BiffException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isScheduleOpened() {
        return (workbookSchedule != null) ? true : false;
    }

    public static boolean isLoadOpened() {
        return (workbookLoad != null) ? true : false;
    }

    /**
     * Закрывает расписание
     */
    public static void closeSchedule() {
        if (workbookSchedule != null)
            workbookSchedule.close();
    }

    /**
     * Закрывает нагрузку
     */
    public static void closeLoad() {
        if (workbookLoad != null)
            workbookLoad.close();
    }

    /**
     * Получает лист в расписании.
     * 
     * @param index - номер листа. Нумерация начинается с 0.
     */
    public static void selectSheetInSchedule(int index) {
        if (isScheduleOpened())
            sheetOfSchedule = workbookSchedule.getSheet(index);
    }

    /**
     * Получает лист в в нагрузке.
     * 
     * @param index - номер листа. Нумерация начинается с 0.
     */
    public static void selectSheetInLoad(int index) {
        if (isLoadOpened())
            sheetOfLoad = workbookLoad.getSheet(index);
    }
    
    /**
     * Получает лист в в нагрузке.
     * 
     * @param index - имя листа
     */
    public static void selectSheetInLoad(String name) {
        if (isLoadOpened())
            sheetOfLoad = workbookLoad.getSheet(name);
    }

    /**
     * Функция возвращает ссылку на лист в расписании. Параметром можно задать
     * номер листа. Обычно это 0-ой лист в случае, если книга ещё не открыта, то
     * будет сгенерирована искл. ситуация. если параметр задан не в тех пределах
     * по умолчанию вернется первый лист книги
     * 
     * @throws ScheduleIsNotOpenedException
     * @return - лист книги с расписанием.
     */
    public static Sheet getSheetOfSchedule(int number)
            throws ExcelFileIsNotOpenedException {
        if (isScheduleOpened()) {
            if (number >= 0 && number < workbookSchedule.getNumberOfSheets()) {
                selectSheetInSchedule(number);
                return sheetOfSchedule;
            } else {
                selectSheetInSchedule(0);
                return sheetOfSchedule;
            }
        } else {
            throw new ExcelFileIsNotOpenedException(
                    "Нет подключения к книге Excel! Сначала нужно подключиться к книге, потом открывать лист.");
        }
    }
 
    /**
     * Возвращает ссылку на лист с нагрузкой из файла нагрузки
     * @param number - индекс листа
     * @return лист с нагрузкой
     * @throws ExcelFileIsNotOpenedException, если книга ещё не открыта
     */
    public static Sheet getSheetOfLoad(int number)
            throws ExcelFileIsNotOpenedException {
        if (isLoadOpened()) {
            if (number >= 0 && number < workbookLoad.getNumberOfSheets()) {
                selectSheetInLoad(number);
                return sheetOfLoad;
            } else {
                selectSheetInLoad(0);
                return sheetOfLoad;
            }
        } else {
            throw new ExcelFileIsNotOpenedException(
                    "Нет подключения к книге Excel! Сначала нужно подключиться к книге, потом открывать лист.");
        }
    }

    /**
     * Находит лист с нагрузкой по названию листа
     * @param name название листа
     * @return лист с нагрузкой
     * @throws ExcelFileIsNotOpenedException, если книга не открыта
     */
    public static Sheet getSheetOfLoad(String name) throws ExcelFileIsNotOpenedException {
        if (isLoadOpened()) {
            selectSheetInLoad(name);
            return sheetOfLoad;
        } else {
            throw new ExcelFileIsNotOpenedException(
                    "Нет подключения к книге Excel! Сначала нужно подключиться к книге, потом открывать лист.");
        }
    }

    /**
     * Не доделана!
     */
    public static void tryToFindMergedCells() {
        range = sheetOfSchedule.getMergedCells();
        for (Range cell : range) {
            System.out.print(cell.getTopLeft().getColumn() + " "
                    + cell.getTopLeft().getRow());
            System.out.println(cell.getBottomRight().getColumn() + " "
                    + cell.getBottomRight().getRow());
        }
    }

    // ****************************//
    // ========================== //
    // ФУНКЦИОНАЛ ПРОГРАММЫ!!! //
    // ========================== //
    // ****************************//
    /**
     * Поиск недостающих преподавателей и формирование двумерной матрицы с
     * данными.
     */
    public static String[][] searchEmptyCellsOfPMI() {
        int countOfEmptyCells = 0, startPos;
        String outputInfo[][];

        selectSheetInSchedule(0);
        int i = 0;

        Pattern patternOfGroupName = Pattern.compile("[0-9][0-9][0-9][0-9]");
        while (true) {
            Matcher matchGroupName =
                    patternOfGroupName.matcher(sheetOfSchedule.getCell(0, i)
                            .getContents());
            if (matchGroupName.matches())
                break;
            i++;
        }
        startPos = i;
        while (i < sheetOfSchedule.getRows()) {
            if (sheetOfSchedule.getCell(10, i).getContents().toUpperCase()
                    .equals("ПМИ")
                    && sheetOfSchedule.getCell(9, i).getContents().isEmpty()) {
                countOfEmptyCells++;
            }
            i++;
        }
        outputInfo = new String[countOfEmptyCells][5];
        i = startPos;
        countOfEmptyCells = 0;
        while (i < sheetOfSchedule.getRows()) {
            if (sheetOfSchedule.getCell(10, i).getContents().toUpperCase()
                    .equals("ПМИ")
                    && sheetOfSchedule.getCell(9, i).getContents().isEmpty()) {
                outputInfo[countOfEmptyCells][0] = "" + (i + 1);
                outputInfo[countOfEmptyCells][1] =
                        splitStr(sheetOfSchedule.getCell(0, i).getContents());
                outputInfo[countOfEmptyCells][2] =
                        splitStr(sheetOfSchedule.getCell(4, i).getContents());
                outputInfo[countOfEmptyCells][3] =
                        splitStr(sheetOfSchedule.getCell(5, i).getContents());
                countOfEmptyCells++;
            }
            i++;
        }
        return outputInfo;
    }

    /**
     * Функция будет открываеть лист с именем "Лист1" и в перспективе работать с
     * его содержимым для анализа корректности расписания
     */
    public static String[][] openGeneralLoad(String[][] matrix, int season,
            int percentMatch) { // Season== 0 - autumn else if == 1 - spring
        sheetOfLoad = workbookLoad.getSheet("Лист1");
        if (sheetOfLoad == null) {
            // TODO заменить MessageBox - вызовом метода из UI
            LOGGER.info("Не найден \"Лист1\" в нагрузке!"
                    + System.getProperty("line.separator")
                    + "Следует открыть Excel файл нагрузки и изменить "
                    + "название листа \"общей нагрузки\" на\"Лист1\"");
            // MessageBox msgBox = new MessageBox(new Shell());
            // msgBox.setMessage("Не найден \\\"Лист1\\\" в нагрузке!\" + \"\\nСледует открыть Excel файл нагрузки и изменить название листа \\\"общей нагрузки\\\" на\\\"Лист1\\\"");
            // msgBox.open();
            return matrix;
        }

        if (matrix == null) {
            LOGGER.info("Нет данных для поиска. "
                    + "Сначала необходимо сделать проверку №1!");
            return matrix;
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 4; j < sheetOfLoad.getRows(); j++) { //
                String[] group = null;
                if (compareDiscipline(splitStr(sheetOfLoad.getCell(2, j)
                        .getContents()), matrix[i][2], percentMatch)) { // matrix[i][2].equals(splitStr(sheetOfLoad.getCell(2,
                    // j).getContents()))
                    group =
                            getGroupNames(splitStr(sheetOfLoad.getCell(5, j)
                                    .getContents()));
                    if (group == null)
                        continue;
                    for (int k = 0; k < group.length; k++) {
                        if (matrix[i][1].equals(group[k])) {
                            if (season == 0) {
                                if (splitStr(matrix[i][3]).equals("лек")) {
                                    if (sheetOfLoad.getCell(14, j)
                                            .getContents() != null) {
                                        matrix[i][4] =
                                                splitStr(sheetOfLoad.getCell(
                                                        14, j).getContents());
                                        break;
                                    } else {
                                        matrix[i][4] = "Не найдено в нагрузке";
                                        break;
                                    }
                                } else if (splitStr(matrix[i][3]).equals("пр")) {
                                    if (sheetOfLoad.getCell(17, j)
                                            .getContents() != null) {
                                        matrix[i][4] =
                                                splitStr(sheetOfLoad.getCell(
                                                        17, j).getContents());
                                        break;
                                    } else {
                                        matrix[i][4] = "Не найдено в нагрузке";
                                        break;
                                    }
                                } else if (splitStr(matrix[i][3])
                                        .equals("л.р.")) {
                                    if (sheetOfLoad.getCell(20, j)
                                            .getContents() != null) {
                                        matrix[i][4] =
                                                splitStr(sheetOfLoad.getCell(
                                                        20, j).getContents());
                                        break;
                                    } else {
                                        matrix[i][4] = "Не найдено в нагрузке";
                                        break;
                                    }
                                }
                            } else if (season == 1) {
                                if (splitStr(matrix[i][3]).equals("лек")) {
                                    if (sheetOfLoad.getCell(48, j)
                                            .getContents() != null) {
                                        matrix[i][4] =
                                                splitStr(sheetOfLoad.getCell(
                                                        48, j).getContents());
                                        break;
                                    } else {
                                        matrix[i][4] = "Не найдено в нагрузке";
                                        break;
                                    }
                                } else if (splitStr(matrix[i][3]).equals("пр")) {
                                    if (sheetOfLoad.getCell(51, j)
                                            .getContents() != null) {
                                        matrix[i][4] =
                                                splitStr(sheetOfLoad.getCell(
                                                        51, j).getContents());
                                        break;
                                    } else {
                                        matrix[i][4] = "Не найдено в нагрузке";
                                        break;
                                    }
                                } else if (splitStr(matrix[i][3])
                                        .equals("л.р.")) {
                                    if (sheetOfLoad.getCell(54, j)
                                            .getContents() != null) {
                                        matrix[i][4] =
                                                splitStr(sheetOfLoad.getCell(
                                                        54, j).getContents());
                                        break;
                                    } else {
                                        matrix[i][4] = "Не найдено в нагрузке";
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return matrix;
    }

    /**
     * Фукнция проверяет накладки по аудиториям 7го здания
     * 
     * @return строку с совпадениями по аудиториям
     */
    /**
     * @return
     */
    public static String checkAuditoriesBlunders() {
        String errorLog = "";
        SortedSet<String> auditoriesSet = new TreeSet<String>();
        selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это
                                  // первый лист)

        /** Составляем сортированный список аудиторий */
        for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
            if (!sheetOfSchedule.getCell(7, i).getContents().equals("7"))
                continue;
            String str = splitStr(sheetOfSchedule.getCell(6, i).getContents());

            /** Если аудитория не указана, то мы её не учитываем */
            if (str == null || str.equals(""))
                continue;
            /** Обработка кафедр */
            if (Pattern.matches("[Кк][Аа][Фф]", str)
                    || Pattern.matches("[Кк][Аа].", str)
                    || Pattern.matches("[Кк]..", str)) {
                str = "каф";
                String key_value = "";
                String kaf =
                        splitStr(sheetOfSchedule.getCell(10, i).getContents());
                if (kaf == null || kaf.equals(""))
                    continue;
                key_value += str.toLowerCase() + kaf.toUpperCase();
                if (auditoriesSet.contains(key_value))
                    continue;
                else
                    auditoriesSet.add(key_value);
            }

            /** Обработка ВЦ */
            else if (Pattern.matches("[Вв][Цц].*", str)) {
                continue; // Добавить обработку ВЦ
            }
            /** Обработка читальных залов */
            else if (Pattern.matches("[Чч].*[Зз].*", str)) {
                if (auditoriesSet.contains("ч.з."))
                    continue;
                else
                    auditoriesSet.add("ч.з.");
            }/** Обработка обычных аудиторий */
            else if (Pattern.matches("[0-9]{1,3}[А-Яа-я]{0,1}", str)) {
                if (!auditoriesSet.contains(str))
                    auditoriesSet.add(str);
            }
        }
        Iterator<String> it = auditoriesSet.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        it = auditoriesSet.iterator();
        while (it.hasNext()) {
            Week oddW = new Week(false);
            Week evenW = new Week(true);
            String regex = it.next();
            if (Pattern.matches(regex, "ч.з."))
                regex = "[Чч].*[Зз].*";
            for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
                if (!sheetOfSchedule.getCell(7, i).getContents().equals("7"))
                    continue;
                String audit = sheetOfSchedule.getCell(6, i).getContents();
                if (audit == null)
                    continue;
                if (!Pattern.matches(regex, audit))
                    continue;
                String str =
                        splitStr(sheetOfSchedule.getCell(3, i).getContents());
                if (str != null) {
                    str = str.replace(" ", ""); // убираем все пробелы
                    // str = str.replace("..", "."); //Специфичная ошибка...
                }
                if (str == null || str.equals("")
                        || Pattern.matches("[Чч]../[Нн]..", str)
                        || Pattern.matches("[Нн]../[Чч]..", str)) {
                    String key =
                            splitStr(sheetOfSchedule.getCell(1, i)
                                    .getContents())
                                    + splitStr(sheetOfSchedule.getCell(2, i)
                                            .getContents());
                    if (key != null) {
                        if (!evenW.containsKey(key) && !oddW.containsKey(key)) {
                            evenW.addLesson(key, splitStr(sheetOfSchedule
                                    .getCell(0, i).getContents())
                                    + " Дисциплина: "
                                    + splitStr(sheetOfSchedule.getCell(4, i)
                                            .getContents()));
                            oddW.addLesson(key, splitStr(sheetOfSchedule
                                    .getCell(0, i).getContents())
                                    + " Дисциплина: "
                                    + splitStr(sheetOfSchedule.getCell(4, i)
                                            .getContents()));
                        } else {
                            String[] infoStr =
                                    evenW.getValue(key).split(" Дисциплина: ");
                            /**
                             * Если дисциплины совпадают, то записываем как
                             * поток в ту же ячейку, а если различаются, то
                             * записываем в лог
                             */
                            if (infoStr.length == 2
                                    && compareDiscipline(
                                            infoStr[1],
                                            splitStr(sheetOfSchedule.getCell(4,
                                                    i).getContents()), 100)) {
                                evenW.addLesson(key, infoStr[0]
                                        + ", "
                                        + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents())
                                        + " Дисциплина: "
                                        + splitStr(sheetOfSchedule
                                                .getCell(4, i).getContents()));
                                oddW.addLesson(key, infoStr[0]
                                        + ", "
                                        + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents())
                                        + " Дисциплина: "
                                        + splitStr(sheetOfSchedule
                                                .getCell(4, i).getContents()));
                            } else if (infoStr.length == 2
                                    && !compareDiscipline(
                                            infoStr[1],
                                            splitStr(sheetOfSchedule.getCell(4,
                                                    i).getContents()), 100))
                                errorLog +=
                                        ((errorLog.length() == 0 ? "" : "\n")
                                                + "Еженедельная накладка! Аудитория: "
                                                + regex + " Время: " + key
                                                + " Группы: " + infoStr[0]
                                                + " и " + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents()));
                        }
                    }
                } else if (str != null
                        && Pattern
                                .matches(
                                        "[0-9]{1,2}[.][0-9]{1,2}[,][0-9]{1,2}[.][0-9]{1,2}[,][0-9]{1,2}[.][0-9]{1,2}[,][0-9]{1,2}[.][0-9]{1,2}.*",
                                        str)) {
                    ; // не зайдет сюда, т.к. все такие пары назначаются
                      // кафедрам, кот прога не просматривает
                }
            }
            for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
                if (!sheetOfSchedule.getCell(7, i).getContents().equals("7"))
                    continue;
                String audit = sheetOfSchedule.getCell(6, i).getContents();
                if (audit == null)
                    continue;
                if (!Pattern.matches(regex, audit))
                    continue;
                String str =
                        splitStr(sheetOfSchedule.getCell(3, i).getContents());
                if (str != null
                        && (Pattern.matches("[Чч][ЕеЁё][Тт]", str)
                                || Pattern.matches("[Чч][ЕеЁё].", str) || Pattern
                                    .matches("[Чч]..", str))) {
                    String key =
                            splitStr(sheetOfSchedule.getCell(1, i)
                                    .getContents())
                                    + splitStr(sheetOfSchedule.getCell(2, i)
                                            .getContents());
                    if (key != null)
                        if (!evenW.containsKey(key))
                            evenW.addLesson(key, splitStr(sheetOfSchedule
                                    .getCell(0, i).getContents())
                                    + "(чет)"
                                    + " Дисциплина: "
                                    + splitStr(sheetOfSchedule.getCell(4, i)
                                            .getContents()));
                        else {
                            String[] infoStr =
                                    evenW.getValue(key).split(" Дисциплина: ");
                            /**
                             * Если дисциплины совпадают, то записываем как
                             * поток в ту же ячейку, а если различаются, то
                             * записываем в лог
                             */
                            if (infoStr.length == 2
                                    && compareDiscipline(
                                            infoStr[1],
                                            splitStr(sheetOfSchedule.getCell(4,
                                                    i).getContents()), 100))
                                evenW.addLesson(key, infoStr[0]
                                        + ", "
                                        + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents())
                                        + "(чет)"
                                        + " Дисциплина: "
                                        + splitStr(sheetOfSchedule
                                                .getCell(4, i).getContents()));
                            else if (infoStr.length == 2
                                    && !compareDiscipline(
                                            infoStr[1],
                                            splitStr(sheetOfSchedule.getCell(4,
                                                    i).getContents()), 100))
                                errorLog +=
                                        ((errorLog.length() == 0 ? "" : "\n")
                                                + "Чётная накладка! Аудитория: "
                                                + regex + " Время: " + key
                                                + " Группы: " + infoStr[0]
                                                + " и " + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents()));
                        }
                } else if (str != null
                        && (Pattern.matches("[Нн][Ее][Чч]", str)
                                || Pattern.matches("[Нн][Ее].", str) || Pattern
                                    .matches("[Нн]..", str))) {
                    String key =
                            splitStr(sheetOfSchedule.getCell(1, i)
                                    .getContents())
                                    + splitStr(sheetOfSchedule.getCell(2, i)
                                            .getContents());
                    if (key != null)
                        if (!oddW.containsKey(key))
                            oddW.addLesson(key, splitStr(sheetOfSchedule
                                    .getCell(0, i).getContents())
                                    + " Дисциплина: "
                                    + splitStr(sheetOfSchedule.getCell(4, i)
                                            .getContents()));
                        else {
                            String[] infoStr =
                                    oddW.getValue(key).split(" Дисциплина: ");

                            /**
                             * Если дисциплины совпадают, то записываем как
                             * поток в ту же ячейку, а если различаются, то
                             * записываем в лог
                             */
                            if (infoStr.length == 2
                                    && compareDiscipline(
                                            infoStr[1],
                                            splitStr(sheetOfSchedule.getCell(4,
                                                    i).getContents()), 100))
                                oddW.addLesson(key, infoStr[0]
                                        + ", "
                                        + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents())
                                        + "(неч)"
                                        + " Дисциплина: "
                                        + splitStr(sheetOfSchedule
                                                .getCell(4, i).getContents()));
                            else if (infoStr.length == 2
                                    && !compareDiscipline(
                                            infoStr[1],
                                            splitStr(sheetOfSchedule.getCell(4,
                                                    i).getContents()), 100))
                                errorLog +=
                                        ((errorLog.length() == 0 ? "" : "\n")
                                                + "Нечётная накладка! Аудитория: "
                                                + regex + " Время: " + key
                                                + " Группы: " + infoStr[0]
                                                + " и " + splitStr(sheetOfSchedule
                                                .getCell(0, i).getContents()));
                        }
                }
            }
        }
        return errorLog;
    }

	public static void countsOfCorrectRows() throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int correct = 0;

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (isValidRow(currentEntry) && isAddableToScheduleToBuilding7(currentEntry))
				correct++;
		}
		System.out.println("Количество корректных записей в таблице: " + correct);
	}
	
	//@ FIXME: Text console - это типа статуса, куда будут выводиться все
	// сообщения об ошибках и информация для принятия решения. Попробуй
	// вывести и посмотреть как все это работает.
	// Запускать таким образом:
	
//	t2.setText("");
//	SemestrBuilder SB = new SemestrBuilder(GlobalStorage.beginingOfSemestr, GlobalStorage.endOfSemestr);
//	try {
//		t2.append("========== ВЫВОД ОШИБОК ПО ВСЕМ НЕДЕЛЯМ ==========\n\n");
//		ExcelWorker.AddInEveryWeek(t2, SB);
//		t2.append("\n========== ВЫВОД ОШИБОК ПО ЧЕТ. НЕДЕЛЯМ ==========\n\n");
//		ExcelWorker.AddInEvenWeek(t2, SB);
//		t2.append("\n========== ВЫВОД ОШИБОК ПО ЧЕТ. НЕДЕЛЯМ ==========\n\n");
//		ExcelWorker.AddInUnevenWeek(t2, SB);
//		t2.append("\n========== ВЫВОД ОШИБОК ДО ЗАДАННОЙ ДАТЫ ==========\n\n");
//		ExcelWorker.AddBefore(t2, SB);
//		t2.append("\n========== ВЫВОД ОШИБОК ПОСЛЕ ЗАДАННОЙ ДАТЫ ==========\n\n");
//		ExcelWorker.AddAfter(t2, SB);
//		
//	} catch (SheduleIsNotOpenedException e) {
//		t2.setText("Расписание не открыто! Обработка отменена...");
//	}
	
	//t2 - это обычный текстбокс. у Меня все работало. Будут вопросы, обращайся.

	
	/**
	 * Функция проверяет совпадения и добавляет в каждую неделю
	 */
	public static void AddInEveryWeek(IStatus console, SemestrBuilder SB, List<String> links) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<Integer> kafPMIclasses = new ArrayList<Integer>();

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !isAddableToScheduleToBuilding7(currentEntry))
				continue;
			if (splitStr(currentEntry[3].getContents()).equals("") || Pattern.matches( "[чн]е[чт]/[нч]е[чт]", splitStr(currentEntry[3].getContents()).toLowerCase())){

				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses.add(new Integer(i));
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
					newClass.id = (i + 1);
					
					if(SB.contains(day, newClass)){
						if(SB.isStreamClass(day, newClass)){
							SB.addGroupToStream(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroom(day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
							errors++;
						}
					} else {
						SB.addToAllSemestr(day, newClass);	// Спокойно добавляем
						added++;
					}
					
				}

				itogoEntries++;
			}
		}

		/**
		 * Теперь по заполненному списку ссылок на строчки с занятиями на кафедре ПМИ
		 * добавляем эти занятия и назначаем им свободные аудитории
		 */
		for (int i = 0; i < kafPMIclasses.size(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(kafPMIclasses.get(i));
			int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
			Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
			LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
			Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

			if (SB.maybeStreamClass(day, newClass)) {
				Class entry = SB.getMaybeStreamClass(day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoom(day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
				if (emptyClassrooms.isEmpty()){
					console.append("\nСвободных аудиторий нет!\n\n");
				} else {
					newClass.lectureRoom = emptyClassrooms.get(0);
					console.append("\nНазначена аудитория: " + newClass.lectureRoom + " из возможных: ");
					for (int k = 0; k < emptyClassrooms.size(); k++) {
						console.append( (k==0?"":", ") + emptyClassrooms.get(k) + (k==(emptyClassrooms.size()-1)?"\n\n":""));
					}
					SB.addToAllSemestr(day, newClass);
					AddedInPMI++;
					added++;
				}
			}
		}

		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + (kafPMIclasses.size() - AddedInPMI) + "\n");
	}
	
	public static void AddInEvenWeek(IStatus console, SemestrBuilder SB, List<String> links) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<Integer> kafPMIclasses = new ArrayList<Integer>();

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			if (Pattern.matches( "[ч][её]?[т]?", splitStr(currentEntry[3].getContents()).toLowerCase() )){

				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses.add(new Integer(i));
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
					newClass.id = (i + 1);
					
					if(SB.containsInEvenWeek(day, newClass)){
						if(SB.isStreamClassInEvenWeek(day, newClass)){
							SB.addGroupToStreamInEvenWeek(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomInEvenWeek(day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
							errors++;
						}
					} else {
						SB.addToEvenWeeksOfSemestr(day, newClass);	// Спокойно добавляем
						added++;
					}
				}

				itogoEntries++;
			}
		}
		
		/**
		 * Теперь по заполненному списку ссылок на строчки с занятиями на кафедре ПМИ
		 * добавляем эти занятия и назначаем им свободные аудитории
		 */
		for (int i = 0; i < kafPMIclasses.size(); i++){
			Cell[] currentEntry = sheetOfSchedule.getRow(kafPMIclasses.get(i));
			int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
			Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
			LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
			Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

			if (SB.maybeStreamClassInEvenWeek(day, newClass)){
				Class entry = SB.getMaybeStreamClassInEvenWeek(day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoomInEvenWeek(day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
				if (emptyClassrooms.isEmpty()){
					console.append("\nСвободных аудиторий нет!\n\n");
				} else {
					newClass.lectureRoom = emptyClassrooms.get(0);
					console.append("\nНазначена аудитория: " + newClass.lectureRoom + " из возможных: ");
					for (int k = 0; k < emptyClassrooms.size(); k++){
						console.append( (k==0?"":", ") + emptyClassrooms.get(k) + (k==(emptyClassrooms.size()-1)?"\n\n":""));
					}
					SB.addToEvenWeeksOfSemestr(day, newClass);
					AddedInPMI++;
					added++;
				}
			}
		}
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + (kafPMIclasses.size()-AddedInPMI) + "\n");
	}
	
	public static void AddInUnevenWeek(IStatus console, SemestrBuilder SB, List<String> links) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<Integer> kafPMIclasses = new ArrayList<Integer>();
		
		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			if (Pattern.matches( "[н][е]?[ч]?", splitStr(currentEntry[3].getContents()).toLowerCase() )){

				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses.add(new Integer(i));
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
					newClass.id = (i + 1);
					
					if(SB.containsInUnevenWeek(day, newClass)){
						if(SB.isStreamClassInUnevenWeek(day, newClass)){
							SB.addGroupToStreamInUnevenWeek(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomInUnevenWeek(day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
							errors++;
						}
					} else {
						SB.addToUnevenWeeksOfSemestr(day, newClass);	// Спокойно добавляем
						added++;
					}
				}
				
				itogoEntries++;
			}
		}

		/**
		 * Теперь по заполненному списку ссылок на строчки с занятиями на кафедре ПМИ
		 * добавляем эти занятия и назначаем им свободные аудитории
		 */
		for (int i = 0; i < kafPMIclasses.size(); i++){
			Cell[] currentEntry = sheetOfSchedule.getRow(kafPMIclasses.get(i));
			int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
			Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
			LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
			Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

			if (SB.maybeStreamClassInUnevenWeek(day, newClass)){
				Class entry = SB.getMaybeStreamClassInUnevenWeek(day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoomInUnevenWeek(day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
				if (emptyClassrooms.isEmpty()){
					console.append("\nСвободных аудиторий нет!\n\n");
				} else {
					newClass.lectureRoom = emptyClassrooms.get(0);
					console.append("\nНазначена аудитория: " + newClass.lectureRoom + " из возможных: ");
					for (int k = 0; k < emptyClassrooms.size(); k++){
						console.append( (k==0?"":", ") + emptyClassrooms.get(k) + (k==(emptyClassrooms.size()-1)?"\n\n":""));
					}
					SB.addToUnevenWeeksOfSemestr(day, newClass);
					AddedInPMI++;
					added++;
				}
			}
		}
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + (kafPMIclasses.size()-AddedInPMI) + "\n");
	}

	public static void AddBefore(IStatus console, SemestrBuilder SB, List<String> links) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<Integer> kafPMIclasses = new ArrayList<Integer>();

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			String date = deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase());
			if (Pattern.matches( "^[дп]о[0-9]{1,2}[.,/][0-9]{2}.*", date)){
				int day, month, year;
				try{
					day = (Pattern.matches( "^[дп]о[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(2, 3)): new Integer(date.substring(2, 4));
					month = (Pattern.matches( "^[дп]о[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(4, 6)).intValue()-1: new Integer(date.substring(5, 7)).intValue()-1;
					year = new Integer(SB.semestr.get(0).days.get(0).dateStr.substring(12, 16));
				} catch (NumberFormatException e) {continue;}
				
				Calendar dateOfTheDay = new GregorianCalendar(year, month, day);

				
				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses.add(new Integer(i));
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
					newClass.id = (i + 1);
					
					if(SB.containsBeforeTheDate(dateOfTheDay, day, newClass)){
						if(SB.isStreamClassBeforeTheDate(dateOfTheDay, day, newClass)){
							SB.addGroupToStreamBeforeTheDate(dateOfTheDay, day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomBeforeTheDate(dateOfTheDay, day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
							errors++;
						}
					} else {
						SB.addBeforeTheDate(dateOfTheDay, day, newClass);	// Спокойно добавляем
						added++;
					}
				}

				
				itogoEntries++;
			}
		}
		
		
		/**
		 * Теперь по заполненному списку ссылок на строчки с занятиями на кафедре ПМИ
		 * добавляем эти занятия и назначаем им свободные аудитории
		 */
		for (int i = 0; i < kafPMIclasses.size(); i++){
			Cell[] currentEntry = sheetOfSchedule.getRow(kafPMIclasses.get(i));

			String date = deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase());
			int day, month, year;
			try{
				day = (Pattern.matches( "^[дп]о[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(2, 3)): new Integer(date.substring(2, 4));
				month = (Pattern.matches( "^[дп]о[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(4, 6)).intValue()-1: new Integer(date.substring(5, 7)).intValue()-1;
				year = new Integer(SB.semestr.get(0).days.get(0).dateStr.substring(12, 16));
			} catch (NumberFormatException e) {continue;}
				
			Calendar dateOfTheDay = new GregorianCalendar(year, month, day);
			
			day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
			Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
			LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
			Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
			
			if (SB.maybeStreamClassBeforeTheDate(dateOfTheDay, day, newClass)){
				Class entry = SB.getMaybeStreamClassBeforeTheDate(dateOfTheDay, day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoomBeforeTheDate(dateOfTheDay, day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
				if (emptyClassrooms.isEmpty()){
					console.append("\nСвободных аудиторий нет!\n\n");
				} else {
					newClass.lectureRoom = emptyClassrooms.get(0);
					console.append("\nНазначена аудитория: " + newClass.lectureRoom + " из возможных: ");
					for (int k = 0; k < emptyClassrooms.size(); k++){
						console.append( (k==0?"":", ") + emptyClassrooms.get(k) + (k==(emptyClassrooms.size()-1)?"\n\n":""));
					}
					SB.addBeforeTheDate(dateOfTheDay, day, newClass);
					AddedInPMI++;
					added++;
				}
			}
		}
		
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + (kafPMIclasses.size()-AddedInPMI) + "\n");
	}

	public static void AddAfter(IStatus console, SemestrBuilder SB, List<String> links) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<Integer> kafPMIclasses = new ArrayList<Integer>();

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			String date = deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase());
			if (Pattern.matches( "^с[0-9]{1,2}[.,/][0-9]{2}.*", date)){
				int day, month, year;
				try{
					day = (Pattern.matches( "^с[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(1, 2)).intValue(): new Integer(date.substring(1, 3)).intValue();
					month = (Pattern.matches( "^с[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(3, 5)).intValue()-1: new Integer(date.substring(4, 6)).intValue()-1;
					year =  new Integer(SB.semestr.get(0).days.get(0).dateStr.substring(12, 16));
				} catch (NumberFormatException e) {continue;}
				
				Calendar dateOfTheDay = new GregorianCalendar(year, month, day);
				
				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses.add(new Integer(i));
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
					newClass.id = (i + 1);
					
					if(SB.containsAfterTheDate(dateOfTheDay, day, newClass)){
						if(SB.isStreamClassAfterTheDate(dateOfTheDay, day, newClass)){
							if (SB.isDatesAreEqual(dateOfTheDay, day, newClass)){
								SB.addGroupToStreamAfterTheDate(dateOfTheDay, day, newClass);//Занятие в потоке, добавить группу к занятию
								added++;
								doubleAdded++;
							} else {
								Calendar excistingDate = SB.getDateOfClassBegining(dateOfTheDay, day, newClass);
								Class excistingEntry = SB.getClassByTimeAndClassroomAfterTheDate(dateOfTheDay, day, newClass);
								console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
								console.append(" Группы: " + excistingEntry.group + " и " + newClass.group + "Не совпадает дата начала занятий!\n");
								console.append("Существующая запись: " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(excistingDate.getTime()) + "\n");
								console.append("Добавляемая  запись: " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfTheDay.getTime()) + "\n");
								errors++;
							}
						} else {
							Class entry = SB.getClassByTimeAndClassroomAfterTheDate(dateOfTheDay, day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
							errors++;
						}
					} else {
						SB.addAfterTheDate(dateOfTheDay, day, newClass);	// Спокойно добавляем
						added++;
					}
				}

				itogoEntries++;
			}
		}
		
		/**
		 * Теперь по заполненному списку ссылок на строчки с занятиями на кафедре ПМИ
		 * добавляем эти занятия и назначаем им свободные аудитории
		 */
		for (int i = 0; i < kafPMIclasses.size(); i++){
			Cell[] currentEntry = sheetOfSchedule.getRow(kafPMIclasses.get(i));

			String date = deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase());
			int day, month, year;
			try{
				day = (Pattern.matches( "^с[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(1, 2)).intValue(): new Integer(date.substring(1, 3)).intValue();
				month = (Pattern.matches( "^с[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(3, 5)).intValue()-1: new Integer(date.substring(4, 6)).intValue()-1;
				year =  new Integer(SB.semestr.get(0).days.get(0).dateStr.substring(12, 16));
			} catch (NumberFormatException e) {continue;}
				
			Calendar dateOfTheDay = new GregorianCalendar(year, month, day);
			
			day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
			Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
			LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
			Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));
			
			if (SB.maybeStreamClassAfterTheDate(dateOfTheDay, day, newClass)){
				Class entry = SB.getMaybeStreamClassAfterTheDate(dateOfTheDay, day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoomAfterTheDate(dateOfTheDay, day, newClass);
				console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
				if (emptyClassrooms.isEmpty()){
					console.append("\nСвободных аудиторий нет!\n\n");
				} else {
					newClass.lectureRoom = emptyClassrooms.get(0);
					console.append("\nНазначена аудитория: " + newClass.lectureRoom + " из возможных: ");
					for (int k = 0; k < emptyClassrooms.size(); k++){
						console.append( (k==0?"":", ") + emptyClassrooms.get(k) + (k==(emptyClassrooms.size()-1)?"\n\n":""));
					}
					SB.addAfterTheDate(dateOfTheDay, day, newClass);
					AddedInPMI++;
					added++;
				}
			}
		}
		
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + (kafPMIclasses.size()-AddedInPMI) + "\n");
	}

	public static void AddByDates(IStatus console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		int added = 0, doubleAdded = 0, errors = 0;

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			if (Pattern.matches( "[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2}", deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase()))){
				itogoEntries++;
			} else if(Pattern.matches( "[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2}/[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2},[0-9]{1,2}.[0-9]{2}", deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase()))){
				itogoEntries++;
			}
		}
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + kafPMIclasses + "\n");
	}
	
	public static List<String> getPMIprofessors(){
		List<String> professors = new ArrayList<String>();
		selectSheetInSchedule(0);
		int i = 0;
		while (true) {
			if (Pattern.matches("[0-9]{4}", deleteSpaces(splitStr(sheetOfSchedule.getCell(0, i).getContents()))))
				break;
			i++;
		}
		for ( ; i < sheetOfSchedule.getRows(); i++) {
			if (deleteSpaces(splitStr(sheetOfSchedule.getCell(10, i).getContents())).toUpperCase().equals("ПМИ")) {
				String tmp = splitStr(sheetOfSchedule.getCell(9, i).getContents());
				String[] str = new String[1];
				if (Pattern.matches("[А-Я]{1}[а-я]*[ ][А-Я]{1}[.][А-Я]{1}[.].*", tmp)){
					str = tmp.split("[(]?[0-9]{1,2}[;][0-9]{1,2}[)]?[,]?[ ]*", 5);
				}
				for (int j = 0; j < str.length; j++){
					if (!professors.contains(str[j]) && !str[j].isEmpty()){
						professors.add(str[j]);
					}
				}
			}
		}
		
		return professors;
	}

	public static void generateProffessorSchedule(String name){
		
		try {
			writableSchedule = Workbook.createWorkbook(new File("D:" + File.separatorChar + name + ".xls"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		writableSchedule.createSheet(name, 0);
		writableSheet = writableSchedule.getSheet(0);

		selectSheetInSchedule(0);

		int i = 0, k = 0;
		while (true) {
			if (Pattern.matches("[0-9]{4}", deleteSpaces(splitStr(sheetOfSchedule.getCell(0, i).getContents()))))
				break;
			i++;
		}
		for ( ; i < sheetOfSchedule.getRows(); i++) {
			
			String tmp = splitStr(sheetOfSchedule.getCell(9, i).getContents());
			if (!tmp.contains(name)){
				continue;	//Если не содержит данного преподавателя, то идем дальше
			}
			
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			WritableCell newCell = null;
			for (int j = 0 ; j < currentEntry.length ; j++){
				Cell readCell = currentEntry[j];
				newCell = new Label(j, k, readCell.getContents());
				try {
					writableSheet.addCell(newCell);
				} catch (RowsExceededException e) {e.printStackTrace();
				} catch (WriteException e) {e.printStackTrace();}
			}
			k++;
		}
		
		try {
			ExcelWorker.writableSchedule.write();
			ExcelWorker.writableSchedule.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		
		
		/**
		 * Запуск файла
		 */
		String excelPath = "";
		String pathToFile = "D:" + File.separatorChar + name + ".xls";
		boolean isException = false;
		for(int n = 0; n < 2; n++) {
			List<String> tmp = new ArrayList<String>();
			excelPath = (String) GlobalStorage.get("excelPath");
			if(excelPath == null || excelPath.isEmpty()) {
				excelPath = ExcelWorker.getExcelPath();
				GlobalStorage.put("excelPath", excelPath);
			}
			tmp.add(excelPath);
			tmp.add(pathToFile);
			LOGGER.info(tmp.toString());
			try {
				new ProcessBuilder(tmp).start();
				isException = false;
				break;
			} catch (IOException e) {
				excelPath = ExcelWorker.getExcelPath();
				GlobalStorage.put("excelPath", excelPath);
				e.printStackTrace();
				isException = true;
			}
		}
		if(isException) {
			MessageBox msgBox = new MessageBox(new Shell());
            msgBox.setMessage(String.format("На вашем компьютере не найдена программа Microsoft Excel!" + System.getProperty("line.separator") +
            		"Созданный файл расписания преподователя находиться в директории: [%s]", pathToFile));
            msgBox.open();
		}
	}

	private static String getExcelPath() {
		File file = new File("C:/");
        String list[] = file.list();
        if (null == list) {
            throw new IllegalArgumentException("Нет такой директории или файла!");
        }
        List<String> programmFilePaths = new ArrayList<String>();
        for (String aList : list) {
            if(aList.startsWith("Program Files")) {
                programmFilePaths.add(file.getPath() + aList);
            }
        }
        for(int i = 0; i < programmFilePaths.size(); i++) {
            file = new File(programmFilePaths.get(i));
            list = file.list();
            for (String aList : list) {
                if(aList.startsWith("Microsoft Office")) {
                    programmFilePaths.set(i, file.getPath() + System.getProperty("file.separator") + aList);
                }
            }
        }
        FileFinder finder = new FileFinder();
        try {
            List searchRes = null;
            //если задано регулярное выражение...
            List<String> parentDirs = programmFilePaths;
            for(String parent: parentDirs) {
//                System.out.println("parent : " + parent);
                if (null == searchRes) {
                    searchRes = finder.findAll(parent, "EXCEL.EXE");
                } else {
                    searchRes.addAll(finder.findAll(parent, "EXCEL.EXE"));
                }
            }
            //выводим результаты
            for(int i = 0; i < searchRes.size(); i++) {
                File curObject = (File)searchRes.get(i);
                if(curObject.isDirectory()) {
                    System.out.println(
                            curObject.getName() + " (папка)");
                }
                else {
                    System.out.println(curObject.getName() + ": FullPath: " + curObject.getAbsolutePath()
                            + " (" + curObject.length() + " байт)");
                    return curObject.getAbsolutePath();
                }
            }
            System.out.println("Найдено " + finder.getFilesNumber() +
                    " файлов и " + finder.getDirectoriesNumber() +
                    " папок.");
        } catch(Exception err) {
            System.out.println(err.getMessage());
        }
        return "";
	}

    // ****************************//
    // ========================== //
    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ!!! //
    // ========================== //
    // ****************************//

    /**
     * Конвертирует строковое представление дня недели в статик поле int класса
     * Calendar.
     */
    public static int convetToDayOfWeek(String aDay) {
        aDay = aDay.toLowerCase();
        if (aDay.equals("пн"))
            return Calendar.MONDAY;
        else if (aDay.equals("вт"))
            return Calendar.TUESDAY;
        else if (aDay.equals("ср"))
            return Calendar.WEDNESDAY;
        else if (aDay.equals("чт"))
            return Calendar.THURSDAY;
        else if (aDay.equals("пт"))
            return Calendar.FRIDAY;
        else if (aDay.equals("сб"))
            return Calendar.SATURDAY;
        else
            return -1;
    }

    /**
     * Функция для сравнения названий предметов. Сравнивает по символьно.
     * 
     * @param arg1 , arg2 - строки которые нужно сравнить.
     * @return true, если более половины символов сошлись. При этом меньшая
     *         строка берется за основную.
     */
    public static boolean compareDiscipline(String arg1, String arg2,
            int percentMatch) {
        if (arg1 == null || arg2 == null)
            return false;
        char[] chr1 = arg1.toCharArray();
        char[] chr2 = arg2.toCharArray();
        char[] helper;
        if (chr1.length > chr2.length) { // mStr1 всегда будет больше по длинне
                                         // или равен mStr2
            helper = chr1;
            chr1 = chr2;
            chr2 = helper;
            helper = null;
        }
        int result = 0, length = chr1.length;
        for (int i = 0; i < chr1.length; i++) {
            if (chr1[i] == (chr2[i]))
                result++;
        }
        if ((float) result / length >= percentMatch / 100.0F) // Делим на
            // длинную строку
            // для получения
            // адекватных
            // данных
            return true;
        else
            return false;
    }

    /**
     * Обрабатывает строку и удаляет табуляцию, переводы строки и лишние пробелы
     * 
     * @param input - строка, которую нужно обработать
     * @return строку либо null при входной строке нулевой длинны
     */
    public static String splitStr(String input) {
        if (input == null || input.isEmpty())
            return "";
        input = input.replaceAll("\t", " ");
        input = input.replaceAll("\n", " ");
        input = input.replaceAll("\r", " ");
        String[] strMatrix = input.split(" ");
        String result = "";
        for (int i = 0; i < strMatrix.length; i++) {
            if (!strMatrix[i].isEmpty())
                result += (result.isEmpty() ? "" : " ") + strMatrix[i];
        }
        return result;
    }

    /**
     * Фунция для вытаскивания из строки имен групп
     * 
     * @param arg0 строка с разлицными данными, содержащая или не содержащая
     *            имена групп в формате ####, где #-цифра
     * @return массив с именами групп
     */
    public static String[] getGroupNames(String arg0) {
        if (arg0.isEmpty())
            return null;
        String resultStr = "";
        String[] resultMatrix = arg0.split(" ");
        for (int i = 0; i < resultMatrix.length; i++) {
            if (Pattern.matches("[0-9][0-9][0-9][0-9],*", resultMatrix[i])) {
                if (Pattern.matches("[0-9][0-9][0-9][0-9],", resultMatrix[i]))
                    resultMatrix[i] =
                            resultMatrix[i].substring(0,
                                    resultMatrix[i].length() - 1);
                resultStr +=
                        (resultStr.length() == 0 ? "" : " ") + resultMatrix[i];
            }
        }
        return resultMatrix = resultStr.split(" ");
    }

	/**
	 * Функция проверяет содержимое записи на валидность. Если появляется
	 * неадекватный параметр, возвращается FALSE
	 */
	private static boolean isValidRow(Cell[] entity) {
		boolean result = true;
		if (Pattern.matches("[0-9]{4}", deleteSpaces(splitStr(entity[0].getContents())))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("[ПпВвСсЧч][НнТтРрБб]", deleteSpaces(splitStr(entity[1].getContents())))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("1?[0-9]:[0-5][0-9]", deleteSpaces(splitStr(entity[2].getContents())))) {
			result = true;
		} else
			return false;
//		/**
//		 * Частный случаи, когда проврека не доходит до конца: дни консультаций,
//		 * проектные дни и т.д....
//		 */
//		if (splitStr(entity[5].getContents()).equals("")) {
//			if (splitStr(entity[4].getContents()).toLowerCase().equals("день консультаций по самостоятельной работе")){
//				return result;
//			} else if(splitStr(entity[4].getContents()).toLowerCase().equals("проектный день")){
//				return result;
//			} else if(splitStr(entity[4].getContents()).toLowerCase().equals("производственный день")) {
//				return result;
//			}
//		}
//		if (splitStr(entity[5].getContents()).toLowerCase().equals("и.з.")) {
//			if(splitStr(entity[4].getContents()).toLowerCase().equals("военная подготовка")){
//				return result;
//			} else if(splitStr(entity[4].getContents()).toLowerCase().equals("основы медицинских знаний и охрана здоровья")){
//				return result;
//			} else if(splitStr(entity[4].getContents()).toLowerCase().equals("основы медицинских знаний и охрана здоровья детей")){
//				return result;
//			} else if(splitStr(entity[4].getContents()).toLowerCase().equals("физическая культура")){
//				return result;
//			}
//		}
//		/**
//		 * Конец частного случая
//		 */
		if (!splitStr(entity[4].getContents()).equals("")) {
			result = true;
		} else
			return false;
//		if (!splitStr(entity[6].getContents()).equals("")){ //Pattern.matches("[0-9]{3}[А-Яа-я]{1}", deleteSpaces(splitStr(entity[6].getContents()))) || Pattern.matches("[Кк][Аа]?[Фф]?.*", deleteSpaces(splitStr(entity[6].getContents()))) || Pattern.matches("[Вв][Цц]", deleteSpaces(splitStr(entity[6].getContents()))) || Pattern.matches("[ч][.]?.*[з][.]?.*", deleteSpaces(splitStr(entity[6].getContents())).toLowerCase())) {
//			result = true;
//		} else
//			return false;
//		if (!splitStr(entity[7].getContents()).equals("")) {
//			result = true;
//		} else
//			return false;

		return result;
	}

	/**
	 * Удаляет все пробелы из строки
	 * @param input входная строка с пробелами
	 * @return выходная строка без пробелов
	 */
	private static String deleteSpaces(String input){
		return input.replace(" ", "");
	}

	/**
	 * Функция анализирует возможность добавления данной записи, по имеющейся в ней информации
	 * @param entity запись для анализа
	 * @return true, если в записи достаточно информации для добавления в расписание
	 */
	private static boolean isAddableToScheduleToBuilding7(Cell[] entity){
		boolean result = true;
		if (deleteSpaces(splitStr(entity[7].getContents())).equals("7")) {
			result = true;
		} else
			return false;
		if (Pattern.matches("лек.*", deleteSpaces(splitStr(entity[5].getContents().toLowerCase()))) || 
				Pattern.matches("л.*р.*", deleteSpaces(splitStr(entity[5].getContents().toLowerCase()))) ||
				Pattern.matches("пр.*", deleteSpaces(splitStr(entity[5].getContents().toLowerCase())))) {
			result = true;
		} else
			return false;
		if ( Pattern.matches( "[0-9]{3}[а-я]?", deleteSpaces(splitStr(entity[6].getContents().toLowerCase()))) ||
				(deleteSpaces(splitStr(entity[6].getContents().toLowerCase())).equals("каф") && Pattern.matches( "пми", deleteSpaces(splitStr(entity[10].getContents().toLowerCase())))) /*||
				(deleteSpaces(splitStr(entity[6].getContents().toLowerCase())).equals("вц") && Pattern.matches( "пми", deleteSpaces(splitStr(entity[10].getContents().toLowerCase()))))*/ ) {
			result = true;
		} else
			return false;
		
		return result;
	}

	/**
	 * Конвертирует время во внутреннее представление
	 * 
	 * @param aTime
	 *            строковое время
	 * @return тип перечисления либо null
	 */
	private static Time convertToEnumTime(String aTime) {
		if (aTime.equals("8:00"))
			return Time.at08_00;
		else if (aTime.equals("9:40"))
			return Time.at09_40;
		else if (aTime.equals("11:30"))
			return Time.at11_30;
		else if (aTime.equals("13:10"))
			return Time.at13_10;
		else if (aTime.equals("15:00"))
			return Time.at15_00;
		else if (aTime.equals("16:40"))
			return Time.at16_40;
		else if (aTime.equals("18:15"))
			return Time.at18_15;
		else if (aTime.equals("19:45"))
			return Time.at19_45;
		else
			return null;
	}

	/**
	 * Конвертирует во внутреннее представление формы занятия
	 * 
	 * @param aFoC
	 *            пр лек или л.р.
	 * @return тип перечилсения LessonType либо null, если не найдет
	 *         соответствия
	 */
	private static LessonType convertToEnumFormOfClass(String aFoC) {
		if (aFoC.equals("лек"))
			return LessonType.LEC;
		else if (aFoC.equals("пр"))
			return LessonType.PRAC;
		else if (aFoC.equals("л.р."))
			return LessonType.LABS;
		else
			return null;
	}

	/**
	 * 
	 * @param AddedEntry
	 * @param newEntry
	 * @param console
	 */
	private static void errorAnalysis(Class AddedEntry, Class newEntry, IStatus console, List<String> links){
		String errorMsg = " Группы: " + AddedEntry.group + " и " + newEntry.group;
		if( !AddedEntry.discipline.equals(newEntry.discipline) ){
			errorMsg += " Не совпадение дисциплин!\n";
			String link = "Показать_" + AddedEntry.id;
			links.add(link);
			errorMsg += "Существующая запись: " + AddedEntry.discipline + " "+link+" \n";
			
			link = "Показать_" + newEntry.id;
			links.add(link);
			errorMsg += "Добавляемая  запись: " + newEntry.discipline + " "+link+" \n\n";
		} else if( !AddedEntry.professor.equals(newEntry.professor) ){
			errorMsg += " Не совпадение преподавателя!\n";
			String link = "Показать_" + AddedEntry.id;
			links.add(link);
			errorMsg += "Существующая запись: " + AddedEntry.professor + " "+link+" \n";
			
			link = "Показать_" + newEntry.id;
			links.add(link);
			errorMsg += "Добавляемая  запись: " + newEntry.professor + " "+link+" \n\n";
		} else if( !AddedEntry.lessonType.equals(newEntry.lessonType) ){
			errorMsg += " Не совпадает форма занятий!\n\n";
		} else if( !AddedEntry.department.equals(newEntry.department) ){
			errorMsg += " Не совпадает кафедра!\n";
			String link = "Показать_" + AddedEntry.id;
			links.add(link);
			errorMsg += "Существующая запись: " + AddedEntry.department + " "+link+" \n";
			
			link = "Показать_" + newEntry.id;
			links.add(link);
			errorMsg += "Добавляемая  запись: " + newEntry.department + " "+link+" \n\n";
		}
		console.append(errorMsg);
	}

    
}