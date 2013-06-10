package ru.kai.assistschedule.core.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.ExcelWorker;
import ru.kai.assistschedule.core.cache.load.FormOfClass;
import ru.kai.assistschedule.core.cache.load.LoadEntry;
import ru.kai.assistschedule.core.exceptions.ExcelFileIsNotOpenedException;
import jxl.Cell;
import jxl.Sheet;

public class FirstLevelCache {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static FirstLevelCache instance = new FirstLevelCache();
	
	private FirstLevelCache() {
	}

	public static FirstLevelCache getInstance() {
		return instance;
	}

	// Переменные для РАСПИСАНИЯ
	private List<ScheduleEntry> elements = new ArrayList<ScheduleEntry>();

	private Set<String> groupNames = new TreeSet<String>();

	private Set<String> daysOfWeek = new TreeSet<String>();

	private Set<String> times = new TreeSet<String>();

	private Set<String> dates = new TreeSet<String>();

	private Set<String> disciplines = new TreeSet<String>();

	private Set<String> lessonTypes = new TreeSet<String>();

	private Set<String> classRooms = new TreeSet<String>();

	private Set<String> buildings = new TreeSet<String>();

	private Set<String> positions = new TreeSet<String>();

	private Set<String> professors = new TreeSet<String>();

	private Set<String> departments = new TreeSet<String>();

	public void addElement() {
	}

	// Переменные для НАГРУЗКИ
	private List<LoadEntry> loadEntries = new ArrayList<LoadEntry>();

	private Set<String> NN = new TreeSet<String>();
	private Set<String> semestr = new TreeSet<String>();
	private Set<String> disc = new TreeSet<String>();
	private Set<String> educationForm = new TreeSet<String>();
	private Set<String> spec_group = new TreeSet<String>();
	private Set<String> groupCount = new TreeSet<String>();
	private Set<String> subGroupsCount = new TreeSet<String>();
	private Set<String> weekCount = new TreeSet<String>();
	private Set<String> lec_hoursInWeek = new TreeSet<String>();
	private Set<String> lec_totalHours = new TreeSet<String>();
	private Set<String> lec_professor = new TreeSet<String>();
	private Set<String> prac_hoursInWeek = new TreeSet<String>();
	private Set<String> prac_totalHours = new TreeSet<String>();
	private Set<String> prac_professor = new TreeSet<String>();
	private Set<String> lab_hoursInWeek = new TreeSet<String>();
	private Set<String> lab_totalHours = new TreeSet<String>();
	private Set<String> lab_professor = new TreeSet<String>();

	// Стек некоректных записей

	private List<Integer> stack = new ArrayList<Integer>();

	// КОНЕЦ переменных для НАГРУЗКИ

	public List<Integer> getStack() {
		return stack;
	}

	public void setStack(List<Integer> stack) {
		this.stack = stack;
	}

	public void readLoadSheet() {
		logger.debug("starting read Professors Load...");
		if (!ExcelWorker.isLoadOpened()) {
			return;
		}
		Sheet loadSh = null;
		try {
			loadSh = ExcelWorker.getSheetOfLoad("Лист1");
		} catch (ExcelFileIsNotOpenedException e) {
			logger.error(e.getMessage());
			// Надо обработать правильно! По сути не должно выскакивать, т.к.
			// проверяется выше в if
			e.printStackTrace();
		}
		int added = 0;
		int NN;
		String semestr = null;
		String disc;
		String educationForm;
		String spec_group;
		int groupCount = 0;
		int subGroupsCount = 0;
		int weekCount = 0;
		FormOfClass lec = null;
		FormOfClass prac = null;
		FormOfClass labs = null;

		for (int i = 4; i < loadSh.getRows(); i++) {
			// Получаем строку из файла нагрузки в массив ячеек
			Cell[] curE = loadSh.getRow(i);
			// Начало валидации
			// В первой (не в нулевой!) ячейке должен быть порядковый номер типа
			// int!
			try {
				NN = Integer.valueOf(
						ExcelWorker.splitStr(curE[1].getContents())).intValue();
			} catch (Exception e) {
				// Если не возможно конвертировать, то строка игнорируется и
				// переходим к следующей
				continue;
			}
			// Дисциплина, форма занятия и названия групп и специальностей
			// записываются в соответствующие переменные
			disc = ExcelWorker.splitStr(curE[2].getContents());
			educationForm = ExcelWorker.splitStr(curE[4].getContents());
			spec_group = ExcelWorker.splitStr(curE[5].getContents());
			// В 8,9 ячейках должно быть указано количество групп и подгрупп.
			// Пробуем их конвертировать
			try {
				groupCount = Integer.valueOf(
						ExcelWorker.splitStr(curE[8].getContents())).intValue();
				subGroupsCount = Integer.valueOf(
						ExcelWorker.splitStr(curE[9].getContents())).intValue();
			} catch (Exception e) {
				// Если не получается, то строка игнорируется
				continue;
			}
			// Если в обоих семестрах отсутствуют часы лекций, то переходим
			// дальше
			if ((ExcelWorker.splitStr(curE[12].getContents()).equals("0") || ExcelWorker
					.splitStr(curE[12].getContents()).equals(""))
					&& (ExcelWorker.splitStr(curE[46].getContents())
							.equals("0") || ExcelWorker.splitStr(
							curE[46].getContents()).equals(""))) {
				continue;
			}// Если занятия должны проходить в первом семестре года, то
				// количество часов часов в 12 ячейке д.б. отлично от 0
			else if (!ExcelWorker.splitStr(curE[12].getContents()).equals("0")
					&& !ExcelWorker.splitStr(curE[12].getContents()).equals("")) {
				// Семестр устанавливается в осень
				semestr = "autumn";
				// Конвертируем количество недель в семестре
				try {
					weekCount = Integer.valueOf(
							ExcelWorker.splitStr(curE[11].getContents()))
							.intValue();
				} // Если не удалось, то пропускаем строку
				catch (Exception e) {
					continue;
				}
				// Создаем виды занятий для предмета: лекции, практики и
				// лабораторные
				lec = new FormOfClass(
						convertToFloat(ExcelWorker.splitStr(curE[12]
								.getContents())), weekCount,
						ExcelWorker.splitStr(curE[14].getContents()));
				prac = new FormOfClass(
						convertToFloat(ExcelWorker.splitStr(curE[15]
								.getContents())), weekCount,
						ExcelWorker.splitStr(curE[17].getContents()));
				labs = new FormOfClass(
						convertToFloat(ExcelWorker.splitStr(curE[18]
								.getContents())), weekCount,
						ExcelWorker.splitStr(curE[20].getContents()));
			}// Если занятия проходят весной, то количество лекций в 46 позиции
				// д.б. отлично от 0
			else if (!ExcelWorker.splitStr(curE[46].getContents()).equals("0")
					&& !ExcelWorker.splitStr(curE[46].getContents()).equals("")) {
				// Семестр устанавлевается весна
				semestr = "spring";
				// Пытаемся конвертировать количество недель в семестре
				try {
					weekCount = Integer.valueOf(
							ExcelWorker.splitStr(curE[45].getContents()))
							.intValue();
				} // Если не удается, то пропускаем строку
				catch (Exception e) {
					continue;
				}
				// Создаем виды занятий для предмета: лекции, практики и
				// лабораторные
				lec = new FormOfClass(
						convertToFloat(ExcelWorker.splitStr(curE[46]
								.getContents())), weekCount,
						ExcelWorker.splitStr(curE[48].getContents()));
				prac = new FormOfClass(
						convertToFloat(ExcelWorker.splitStr(curE[49]
								.getContents())), weekCount,
						ExcelWorker.splitStr(curE[51].getContents()));
				labs = new FormOfClass(
						convertToFloat(ExcelWorker.splitStr(curE[52]
								.getContents())), weekCount,
						ExcelWorker.splitStr(curE[54].getContents()));
			}
			// Добавляем полученные данные в список занятий нагрузки
			loadEntries.add(new LoadEntry(NN, semestr, disc, educationForm,
					spec_group, groupCount, subGroupsCount, weekCount, lec,
					prac, labs));
			// Увеличиваем количество добавленных записей
			added = added + 1;
			// Заполняем данные для сортировки и фильтрации списка
			fillLoadSets(NN, semestr, disc, educationForm, spec_group,
					groupCount, subGroupsCount, weekCount, lec, prac, labs);
		}
		logger.debug(String.format(
				"Нагрузка прочитано, добавлено %d элементов",
				loadEntries.size()));
	}

	/** Функция чтение с листа xls и вытаскивания записей построчно */
	public void readFromSheet() {
		if (!ExcelWorker.isScheduleOpened()) {
			return;
		}
		Sheet scheduleSheet = null;
		try {
			scheduleSheet = ExcelWorker.getSheetOfSchedule(0);
		} catch (ExcelFileIsNotOpenedException e) {
			// Надо обработать правильно! По сути не должно выскакивать, т.к.
			// проверяется выше в if
			e.printStackTrace();
		}
		int added = 0; // ДЛЯ дебага
		int notAdded = 0; // для дебага вывести в Log
		String sGroupName;
		String sDayOfWeek;
		String sTime;
		String sDate;
		String sDiscipline;
		String sLessonType;
		String sClassRoom;
		String sBuilding;
		String sPosition;
		String sProfessor;
		String sDepartment;

		for (int i = 1; i < scheduleSheet.getRows(); i++) {
			Cell[] curE = scheduleSheet.getRow(i);
			if (!isValidRow(curE)) {
				stack.add(elements.size());
				notAdded++;// нужно сделать список некорректных записей
				// continue;
			}
			sGroupName = ExcelWorker.splitStr(curE[0].getContents());
			sDayOfWeek = ExcelWorker.splitStr(curE[1].getContents());
			sTime = ExcelWorker.splitStr(curE[2].getContents());
			sDate = ExcelWorker.splitStr(curE[3].getContents());
			sDiscipline = ExcelWorker.splitStr(curE[4].getContents());
			sLessonType = ExcelWorker.splitStr(curE[5].getContents());
			sClassRoom = ExcelWorker.splitStr(curE[6].getContents());
			sBuilding = ExcelWorker.splitStr(curE[7].getContents());
			sPosition = ExcelWorker.splitStr(curE[8].getContents());
			sProfessor = ExcelWorker.splitStr(curE[9].getContents());
			sDepartment = ExcelWorker.splitStr(curE[10].getContents());

			Time time = convertToEnumTime(sTime); // конвертируем время
			LessonType lesType = convertToEnumFormOfClass(sLessonType); // форму
																		// занятий
			DaysOfWeek day = convertToDayOfWeek(sDayOfWeek);
			
			ScheduleEntry entry = new ScheduleEntry(sGroupName, day, time, sDate,
					sDiscipline, lesType, sClassRoom, sBuilding, sPosition,
					sProfessor, sDepartment);
			entry.id = (i + 1);
			
			elements.add(entry);
			if(i != 1) {
				fillSets(sGroupName, sDayOfWeek, sTime, sDate, sDiscipline,
						sLessonType, sClassRoom, sBuilding, sPosition, sProfessor,
						sDepartment);	
			}
			
			added++;
		}
		
		logger.debug(String.format(
				"Расписание прочитано, добавлено %d элементов", elements.size()));
	}

	/**
	 * Возвращаем элементы нагрузки
	 * 
	 * @return
	 */
	public List<LoadEntry> getLoadElements() {
		return loadEntries;
	}

	public List<ScheduleEntry> getScheduleElements() {
		return elements;
	}

	public Set<String> getUniqueSetByName(String name) {
		if(Constants.Schedule.GROUP.equals(name)) {
			return getGroupNames();
		} else if(Constants.Schedule.DAY_OF_WEEK.equals(name)) {
			return getDaysOfWeek();
		} else if(Constants.Schedule.TIME.equals(name)) {
			return getTimes();
		} else if(Constants.Schedule.DATE.equals(name)) {
			return getDates();
		} else if(Constants.Schedule.DISCIPLINE.equals(name)) {
			return getDisciplines();
		} else if(Constants.Schedule.LESSON_TYPE.equals(name)) {
			return getLessonTypes();
		} else if(Constants.Schedule.CLASSROOM.equals(name)) {
			return getClassRooms();
		} else if(Constants.Schedule.BUILDING.equals(name)) {
			return getBuildings();
		} else if(Constants.Schedule.POSITION.equals(name)) {
			return getPositions();
		} else if(Constants.Schedule.PROFESSOR.equals(name)) {
			return getProfessors();
		} else if(Constants.Schedule.DEPARTMENT.equals(name)) {
			return getDepartments();
		} else {
			return new HashSet<String>();
		}
	}
	
	public Set<String> getGroupNames() {
		return groupNames;
	}

	public Set<String> getDaysOfWeek() {
		return daysOfWeek;
	}

	public Set<String> getTimes() {
		return times;
	}

	public Set<String> getDates() {
		return dates;
	}

	public Set<String> getDisciplines() {
		return disciplines;
	}

	public Set<String> getLessonTypes() {
		return lessonTypes;
	}

	public Set<String> getClassRooms() {
		return classRooms;
	}

	public Set<String> getBuildings() {
		return buildings;
	}

	public Set<String> getPositions() {
		return positions;
	}

	public Set<String> getProfessors() {
		return professors;
	}

	public Set<String> getDepartments() {
		return departments;
	}

	/**
	 * Заполняем списки с уникальными значениями, чтобы использовать их в
	 * сортировке.
	 * 
	 * @param NN
	 *            номер из нагрузки
	 * @param semestr
	 *            осень или весна
	 * @param disc
	 *            дисциплина
	 * @param educationForm
	 *            форма обучения (дневная, вечерка и т.д.)
	 * @param spec_group
	 *            специальность и группа
	 * @param groupCount
	 *            число груп для расчета часов практик
	 * @param subGroupsCount
	 *            число подгруп для расчета часов лаб
	 * @param weekCount
	 *            количество недель в семесте
	 * @param lec
	 *            мой тип данных, которй содержит 3 поля: часов/нед; всего
	 *            часов(вычисляяется автоматически) и имя преподавателя
	 * @param prac
	 *            то же что и lec
	 * @param labs
	 *            то же что и lec
	 */
	private void fillLoadSets(int NN, String semestr, String disc,
			String educationForm, String spec_group, int groupCount,
			int subGroupsCount, int weekCount, FormOfClass lec,
			FormOfClass prac, FormOfClass labs) {
		this.NN.add(String.valueOf(NN));
		this.semestr.add(semestr);
		this.disc.add(disc);
		this.educationForm.add(educationForm);
		this.spec_group.add(spec_group);
		this.groupCount.add(String.valueOf(groupCount));
		this.subGroupsCount.add(String.valueOf(subGroupsCount));
		this.weekCount.add(String.valueOf(weekCount));
		if (lec != null) {
			this.lec_hoursInWeek.add(String.valueOf(lec.hoursInWeek));
			this.lec_totalHours.add(String.valueOf(lec.totalHours));
			this.lec_professor.add(lec.professor);
		}
		if (prac != null) {
			this.prac_hoursInWeek.add(String.valueOf(prac.hoursInWeek));
			this.prac_totalHours.add(String.valueOf(prac.totalHours));
			this.prac_professor.add(prac.professor);
		}
		if (labs != null) {
			this.lab_hoursInWeek.add(String.valueOf(labs.hoursInWeek));
			this.lab_totalHours.add(String.valueOf(labs.totalHours));
			this.lab_professor.add(labs.professor);
		}
	}

	/**
	 * Заполняем списки с уникальными значениями, чтобы использовать их в
	 * сортировке.
	 * 
	 * @param groupName
	 * @param dayOfWeek
	 * @param time
	 * @param date
	 * @param discipline
	 * @param lessonType
	 * @param classRoom
	 * @param building
	 * @param position
	 * @param professor
	 * @param department
	 */
	private void fillSets(String groupName, String dayOfWeek, String time,
			String date, String discipline, String lessonType,
			String classRoom, String building, String position,
			String professor, String department) {
		groupNames.add(groupName);
		daysOfWeek.add(dayOfWeek);
		times.add(time);
		dates.add(date);
		disciplines.add(discipline);
		lessonTypes.add(lessonType);
		classRooms.add(classRoom);
		buildings.add(building);
		positions.add(position);
		professors.add(professor);
		departments.add(department);
	}

	/**
	 * Функция вернет ссылку на список записей
	 */
	public List<ScheduleEntry> getEntries() {
		return elements;
	}

	/**********************/
	/** ВНУТРЕННИЕ ФУНКЦИИ **/
	/**********************/

	/**
	 * Конвертирует из строки в число типа float, избегая ошибки
	 * 
	 * @param sNumber
	 *            - строка содержащая число
	 * @return результат конвертации
	 */
	private float convertToFloat(String sNumber) {
		sNumber = sNumber.equals("") ? "0" : sNumber; // Если пустая строка, то
														// ставим 0
		sNumber = sNumber.contains(",") ? sNumber.replace(",", ".") : sNumber; // Если
																				// запятая,
																				// то
																				// меняем
																				// их
																				// на
																				// точку
		return Float.valueOf(sNumber).floatValue();
	}

	/**
	 * Конвертирует во внутреннее представление формы занятия
	 * 
	 * @param aFoC
	 *            пр лек или л.р.
	 * @return тип перечилсения FormOfClass либо null, если не найдет
	 *         соответствия
	 */
	private LessonType convertToEnumFormOfClass(String aFoC) {
		if (aFoC.equals("лек"))
			return LessonType.LEC;
		else if (aFoC.equals("пр"))
			return LessonType.PRAC;
		else if (aFoC.equals("л.р."))
			return LessonType.LABS;
		else if (aFoC.equals("и.з."))
			return LessonType.LABS;
		else if (aFoC.equals(""))
			return LessonType.OTHER;
		else
			return null;
	}

	/**
	 * Конвертирует время во внутреннее представление
	 * 
	 * @param aTime
	 *            строковое время
	 * @return тип перечисления либо null
	 */
	private Time convertToEnumTime(String aTime) {
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
	 * Конвертация дня недели
	 * 
	 * @param aDay
	 * @return
	 */
	private DaysOfWeek convertToDayOfWeek(String aDay) {
		if (Pattern.matches("[Пп][Нн]", aDay)) {
			return DaysOfWeek.mon;
		} else if (Pattern.matches("[Вв][Тт]", aDay)) {
			return DaysOfWeek.tue;
		} else if (Pattern.matches("[Сс][Рр]", aDay)) {
			return DaysOfWeek.wed;
		} else if (Pattern.matches("[Чч][Тт]", aDay)) {
			return DaysOfWeek.thu;
		} else if (Pattern.matches("[Пп][Тт]", aDay)) {
			return DaysOfWeek.fri;
		} else if (Pattern.matches("[Сс][Бб]", aDay)) {
			return DaysOfWeek.sat;
		} else
			return null;
	}

	/**
	 * Функция проверяет содержимое записи на валидность. Если появляется
	 * неадекватный параметр, возвращается FALSE
	 */
	private boolean isValidRow(Cell[] entity) {
		boolean result = true;
		if (Pattern.matches("[0-9][0-9][0-9][0-9]",
				ExcelWorker.splitStr(entity[0].getContents()))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("[ПпВвСсЧч][НнТтРрБб]",
				ExcelWorker.splitStr(entity[1].getContents()))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("1*[0-9]:[0-5][0-9]",
				ExcelWorker.splitStr(entity[2].getContents()))) {
			result = true;
		} else
			return false;
		/**
		 * Частный случаи, когда проврека не доходит до конца: дни консультаций,
		 * проектные дни и т.д....
		 */
		if (ExcelWorker.splitStr(entity[5].getContents()).equals("")) {
			if (ExcelWorker.splitStr(entity[4].getContents()).toLowerCase()
					.equals("день консультаций по самостоятельной работе")) {
				return result;
			} else if (ExcelWorker.splitStr(entity[4].getContents())
					.toLowerCase().equals("проектный день")) {
				return result;
			} else if (ExcelWorker.splitStr(entity[4].getContents())
					.toLowerCase().equals("производственный день")) {
				return result;
			}
		}
		if (ExcelWorker.splitStr(entity[5].getContents()).toLowerCase()
				.equals("и.з.")) {
			if (ExcelWorker.splitStr(entity[4].getContents()).toLowerCase()
					.equals("военная подготовка")) {
				return result;
			} else if (ExcelWorker.splitStr(entity[4].getContents())
					.toLowerCase()
					.equals("основы медицинских знаний и охрана здоровья")) {
				return result;
			} else if (ExcelWorker
					.splitStr(entity[4].getContents())
					.toLowerCase()
					.equals("основы медицинских знаний и охрана здоровья детей")) {
				return result;
			} else if (ExcelWorker.splitStr(entity[4].getContents())
					.toLowerCase().equals("физическая культура")) {
				return result;
			}
		}
		/**
		 * Конец частного случая
		 */
		if (!ExcelWorker.splitStr(entity[4].getContents()).equals("")) {
			result = true;
		} else
			return false;
		if ((Pattern.matches("[Пп][Рр]",
				ExcelWorker.splitStr(entity[5].getContents())))
				|| (Pattern.matches("[Лл][Ее][Кк]",
						ExcelWorker.splitStr(entity[5].getContents())))
				|| (Pattern.matches("[Лл][.][Рр][.]",
						ExcelWorker.splitStr(entity[5].getContents())))) {
			result = true;
		} else
			return false;
		if (!ExcelWorker.splitStr(entity[6].getContents()).equals("")) {
			result = true;
		} else
			return false;
		if (!ExcelWorker.splitStr(entity[7].getContents()).equals("")) {
			result = true;
		} else
			return false;

		return result;
	}

}
