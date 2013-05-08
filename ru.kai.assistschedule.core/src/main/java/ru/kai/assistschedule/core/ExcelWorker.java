package ru.kai.assistschedule.core;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kai.assistschedule.core.calendar.Class;

import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.Time;
import ru.kai.assistschedule.core.calendar.SemestrBuilder;
import ru.kai.assistschedule.core.exceptions.ExcelFileIsNotOpenedException;
import ru.kai.assistschedule.core.exceptions.SheduleIsNotOpenedException;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelWorker {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(ExcelWorker.class);

    private static Workbook workbookSchedule, workbookLoad;

    private static Sheet sheetOfSchedule, sheetOfLoad;

    private static Range[] range;

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
            // MessageBox msgBox = new MessageBox(new Shell());
            // msgBox.setMessage("Нет данных для поиска. Сначала необходимо сделать проверку №1!");
            // msgBox.open();
            return matrix;
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 4; j < sheetOfLoad.getRows(); j++) { //
                String[] groups = null;
                if (compareDiscipline(splitStr(sheetOfLoad.getCell(2, j)
                        .getContents()), matrix[i][2], percentMatch)) { // matrix[i][2].equals(splitStr(sheetOfLoad.getCell(2,
                    // j).getContents()))
                    groups =
                            getGroupNames(splitStr(sheetOfLoad.getCell(5, j)
                                    .getContents()));
                    if (groups == null)
                        continue;
                    for (int k = 0; k < groups.length; k++) {
                        if (matrix[i][1].equals(groups[k])) {
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
	public static void AddInEveryWeek(Text console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		int added = 0, errors = 0, doubleAdded = 0;

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !isAddableToScheduleToBuilding7(currentEntry))
				continue;
			if (splitStr(currentEntry[3].getContents()).equals("") || Pattern.matches( "[чн]е[чт]/[нч]е[чт]", splitStr(currentEntry[3].getContents()).toLowerCase())){

				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses++;
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

					if(SB.contains(day, newClass)){
						if(SB.isStreamClass(day, newClass)){
							SB.addGroupToStream(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroom(day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console);	//Вывод подробных данных
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
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + kafPMIclasses + "\n");
	}
	
	public static void AddInEvenWeek(Text console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		int added = 0, errors = 0, doubleAdded = 0;

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			if (Pattern.matches( "[ч][её]?[т]?", splitStr(currentEntry[3].getContents()).toLowerCase() )){

				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses++;
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

					if(SB.containsInEvenWeek(day, newClass)){
						if(SB.isStreamClassInEvenWeek(day, newClass)){
							SB.addGroupToStreamInEvenWeek(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomInEvenWeek(day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console);	//Вывод подробных данных
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

		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + kafPMIclasses + "\n");
	}
	
	public static void AddInUnevenWeek(Text console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		int added = 0, doubleAdded = 0, errors = 0;

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			if (Pattern.matches( "[н][е]?[ч]?", splitStr(currentEntry[3].getContents()).toLowerCase() )){

				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses++;
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

					if(SB.containsInUnevenWeek(day, newClass)){
						if(SB.isStreamClassInUnevenWeek(day, newClass)){
							SB.addGroupToStreamInUnevenWeek(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomInUnevenWeek(day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console);	//Вывод подробных данных
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
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + kafPMIclasses + "\n");
	}

	public static void AddBefore(Text console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		int added = 0, doubleAdded = 0, errors = 0;

		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry) || !currentEntry[7].getContents().equals("7"))
				continue;
			String date = deleteSpaces(splitStr(currentEntry[3].getContents()).toLowerCase());
			if (Pattern.matches( "^[дп]о[0-9]{1,2}[.,/][0-9]{2}.*", date)){
				int day, month, year;
				try{
					day = (Pattern.matches( "^с[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(2, 3)): new Integer(date.substring(2, 4));
					month = (Pattern.matches( "^с[0-9]{1}[.,/][0-9]{2}.*", date))? new Integer(date.substring(4, 6)).intValue()-1: new Integer(date.substring(5, 7)).intValue()-1;
					year = new Integer(SB.semestr.get(0).days.get(0).dateStr.substring(12, 16));
				} catch (NumberFormatException e) {continue;}
				
				Calendar dateOfTheDay = new GregorianCalendar(year, month, day);

				
				String str = splitStr(currentEntry[6].getContents());
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(splitStr(currentEntry[10].getContents().toLowerCase())))){
						kafPMIclasses++;
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

					if(SB.containsBeforeTheDate(dateOfTheDay, day, newClass)){
						if(SB.isStreamClassBeforeTheDate(dateOfTheDay, day, newClass)){
							SB.addGroupToStreamBeforeTheDate(dateOfTheDay, day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomBeforeTheDate(dateOfTheDay, day, newClass);
							console.append(deleteSpaces(splitStr(currentEntry[1].getContents())) + " " + deleteSpaces(splitStr(currentEntry[2].getContents())) + " Аудитория: " + newClass.lectureRoom);//Ошибка. Вывести общие данные
							errorAnalysis(entry, newClass, console);	//Вывод подробных данных
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
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + kafPMIclasses + "\n");
	}

	public static void AddAfter(Text console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		int added = 0, doubleAdded = 0, errors = 0;

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
						kafPMIclasses++;
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
					Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
					LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
					Class newClass = new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents()));

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
							errorAnalysis(entry, newClass, console);	//Вывод подробных данных
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
		
		console.append("\nВсего записей обработано: " + itogoEntries + "\n");
		console.append("Добавлено: " + added + "\n");
		console.append("Повторно добавлено в поток: " + doubleAdded + "\n");
		console.append("Ошибок: " + errors + "\n");
		console.append("Не добавлено занятий на кафедре ПМИ: " + kafPMIclasses + "\n");
	}

	public static void AddByDates(Text console, SemestrBuilder SB) throws SheduleIsNotOpenedException{
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		String errorLog = "";
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
	
	public static void GenerateSchedule(SemestrBuilder SB) throws SheduleIsNotOpenedException {
		if (!isScheduleOpened()) // Если не открыто расписание
			throw new SheduleIsNotOpenedException();
		selectSheetInSchedule(0); // Открываем лист с расписанием(обычно это первый лист)
		int kafPMIclasses = 0, itogoEntries = 0;
		String errorLog = "";
		int added = 0, errors = 0;
		for (int i = 1; i < sheetOfSchedule.getRows(); i++) {
			Cell[] currentEntry = sheetOfSchedule.getRow(i);
			if (!isValidRow(currentEntry))
				continue;
			if (!currentEntry[7].getContents().equals("7")) // Если не 7 здание,то игнор и дальше
				continue;
			
			String str = splitStr(currentEntry[6].getContents());
			if (Pattern.matches("[Кк][Аа][Фф]", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
				String kafPMI = currentEntry[10].getContents();
				if (Pattern.matches("[Пп][Мм][Ии]*", kafPMI))
					kafPMIclasses++;
				// Реализовать заполнение списка занятий на кафедре для
				// дальнейшего распределения по аудиториям!
				// Реализовать вызов метода, который будет определять по
				// каким дням занятия(чет, неч, даты и т.д.)
			} else if (Pattern.matches("[Вв][Цц].*", str)) {
				continue; // Игнор ВЦ
			} else if (Pattern.matches("[0-9]{3,3}[А-Яа-я]?", str)) {
				itogoEntries++;
				int day = convetToDayOfWeek(splitStr(currentEntry[1].getContents())); // конвертируем день
				Time time = convertToEnumTime(splitStr(currentEntry[2].getContents())); // конвертируем вермя
				LessonType FoC = convertToEnumFormOfClass(splitStr(currentEntry[5].getContents())); // --//-- форму занятий
				String datesOfClass = splitStr(currentEntry[3].getContents()).toLowerCase(); // берем дни занятия
				if (datesOfClass.equals("") || Pattern.matches("чет/неч", datesOfClass.toLowerCase()) || Pattern.matches("неч/чет", datesOfClass.toLowerCase()) || Pattern.matches("ч.*/н.*", datesOfClass.toLowerCase()) || Pattern.matches("н.*/ч.*", datesOfClass.toLowerCase())) {
					for (int j = 0; j < SB.semestr.size(); j++) {// Бежим по неделям
						for (int k = 0; k < SB.semestr.get(j).days.size(); k++) {// Бежим по дням
							if (SB.semestr.get(j).days.get(k).DayOfWeek == day) {// Находим нужный день
								// проверяем накладки
								List<Class> classes = SB.semestr.get(j).days.get(k).classes;
								if (classes.size() == 0) {
									classes.add(new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents())));
									added++;
									break;
								} else{
									int l;
									for (l = 0; l < classes.size(); l++) {
										if (time == classes.get(l).time) {// Время совпало???
											if (splitStr(currentEntry[6].getContents()).equals(classes.get(l).lectureRoom)) {// Аудитория совпала???
												if (splitStr(currentEntry[4].getContents()).equals(classes.get(l).discipline)) {// Дисциплина совпала???
													if (FoC == classes.get(l).lessonType) {// Форма занятия совпала?
														if (splitStr(currentEntry[9].getContents()).equals(classes.get(l).professor)) {// Преподаватель совпал???
															classes.get(l).group += "," + splitStr(currentEntry[0].getContents());
															break;
														} else{
															errors++;
															errorLog += "Два преподавателя в одной аудитории\n";
															errorLog += classes.get(l).group + " " + classes.get(l).lectureRoom + " " + classes.get(l).discipline + " " + classes.get(l).professor + "\n";
															errorLog += currentEntry[0].getContents() + " " + currentEntry[6].getContents() + " " + currentEntry[4].getContents() + " " + currentEntry[9].getContents() + "\n";
															errorLog += "===================================\n";
														}
													} else{
														errors++;
														errorLog += "Не совпала форма занятий\n";
														errorLog += classes.get(l).group + " " + classes.get(l).lectureRoom + " " + classes.get(l).discipline + " " + classes.get(l).professor + "\n";
														errorLog += currentEntry[0].getContents() + " " + currentEntry[6].getContents() + " " + currentEntry[4].getContents() + " " + currentEntry[9].getContents() + "\n";
														errorLog += "========================\n";
													}
												} else{
													errors++;
													errorLog += "Не совпала дисциплина\n";
													errorLog += classes.get(l).group + " " + classes.get(l).lectureRoom + " " + classes.get(l).discipline + " " + classes.get(l).professor + "\n";
													errorLog += currentEntry[0].getContents() + " " + currentEntry[6].getContents() + " " + currentEntry[4].getContents() + " " + currentEntry[9].getContents() + "\n";
													errorLog += "=====================\n";
												}
											} else {// Аудитория не совпала, добавляем
												continue;
											}
										} else {// Время не совпало
											continue;
										}
									}
									if (l == classes.size() && errorLog.isEmpty()) {// Здесь ошибки быть не может и спокойно добавляем
										classes.add(new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents())));
										added++;
									} else if (!errorLog.isEmpty()) {// Если ошибка все таки есть то выводим её и не добавляем!!!
										classes.add(new Class(time, splitStr(currentEntry[6].getContents()), splitStr(currentEntry[4].getContents()), FoC, splitStr(currentEntry[0].getContents()), splitStr(currentEntry[9].getContents()), splitStr(currentEntry[10].getContents())));
										added++;
										System.out.print(errorLog);
										errorLog = "";
									}
								}
							}
						}
					}
				} else if (Pattern.matches("чет", datesOfClass) || Pattern.matches("че.*", datesOfClass) || Pattern.matches("ч.*", datesOfClass)) {
				} else if (Pattern.matches("неч", datesOfClass) || Pattern.matches("не.*", datesOfClass) || Pattern.matches("н.*", datesOfClass)) {
				} else if (Pattern.matches("до.*", datesOfClass)) {
				} else if (Pattern.matches("по.*", datesOfClass)) {
				} else if (Pattern.matches("с.*", datesOfClass)) {
				} else if (Pattern.matches("[0-9]{1,2}[.][0-9]{1,2}[,][0-9]{1,2}[.][0-9]{1,2}[,][0-9]{1,2}[.][0-9]{1,2}[,][0-9]{1,2}[.][0-9]{1,2}", datesOfClass)) {
					System.out.println("4 даты");
				} else if (Pattern.matches("[[0-9]{1,2}[.][0-9]{1,2}[,]]{3,3}[0-9]{1,2}[.][0-9]{1,2}[\\/][[0-9]{1,2}[.][0-9]{1,2}[,]]{3,3}[0-9]{1,2}[.][0-9]{1,2}", datesOfClass)) {
					System.out.println("8 дат");
				}

			}

		}
		System.out.println("Добавлено: " + added);
		System.out.println("Ошибок: " + errors);
		System.out.println(kafPMIclasses);
		System.out.println(errorLog);
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
	 * @return тип перечилсения FormOfClass либо null, если не найдет
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
	private static void errorAnalysis(Class AddedEntry, Class newEntry, Text console){
		String errorMsg = " Группы: " + AddedEntry.group + " и " + newEntry.group;
		if( !AddedEntry.discipline.equals(newEntry.discipline) ){
			errorMsg += " Не совпадение дисциплин!\n";
			errorMsg += "Существующая запись: " + AddedEntry.discipline + "\n";
			errorMsg += "Добавляемая  запись: " + newEntry.discipline + "\n\n";
		} else if( !AddedEntry.professor.equals(newEntry.professor) ){
			errorMsg += " Не совпадение преподавателя!\n";
			errorMsg += "Существующая запись: " + AddedEntry.professor + "\n";
			errorMsg += "Добавляемая  запись: " + newEntry.professor + "\n\n";
		} else if( !AddedEntry.lessonType.equals(newEntry.lessonType) ){
			errorMsg += " Не совпадает форма занятий!\n\n";
		} else if( !AddedEntry.department.equals(newEntry.department) ){
			errorMsg += " Не совпадает кафедра!\n";
			errorMsg += "Существующая запись: " + AddedEntry.department + "\n";
			errorMsg += "Добавляемая  запись: " + newEntry.department + "\n\n";
		}
		console.append(errorMsg);
	}

    
}