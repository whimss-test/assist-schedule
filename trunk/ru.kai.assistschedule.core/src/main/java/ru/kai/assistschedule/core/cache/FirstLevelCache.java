package ru.kai.assistschedule.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import ru.kai.assistschedule.core.ExcelWorker;
import ru.kai.assistschedule.core.exceptions.SheduleIsNotOpenedException;

import jxl.Cell;
import jxl.Sheet;

public class FirstLevelCache {

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
	
	public void addElement(){}

	/**Функция чтение с листа xls и вытаскивания записей построчно*/
	public void readFromSheet(){
		if (!ExcelWorker.isScheduleOpened()){
			return;
		}
		Sheet scheduleSheet = null;
		try {
			scheduleSheet = ExcelWorker.getSheetOfSchedule(0);
		} catch (SheduleIsNotOpenedException e) {
			// Надо обработать правильно! По сути не должно выскакивать, т.к. проверяется выше в if
			e.printStackTrace();
		}
		int added = 0;	//ДЛЯ дебага
		int notAdded = 0;	//для дебага вывести в Log
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
				notAdded++;//нужно сделать список некорректных записей
				continue;
			} else {
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
				LessonType lesType = convertToEnumFormOfClass(sLessonType); // форму занятий
				DaysOfWeek day = convertToDayOfWeek(sDayOfWeek); 
				elements.add(new ScheduleEntry(sGroupName, day, time, sDate, 
						sDiscipline, lesType, sClassRoom, sBuilding, sPosition, 
						sProfessor, sDepartment));
				fillSets(sGroupName, sDayOfWeek, sTime, sDate, sDiscipline, 
						sLessonType, sClassRoom, sBuilding, sPosition, 
						sProfessor, sDepartment);
				added++;
			}
		}
	}
	
	public List<ScheduleEntry> getElements() {
		return elements;
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
	 * Заполняем списки с уникальными значениями, чтобы использовать их в сортировке.
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
	public List<ScheduleEntry> getEntries(){
		return elements;
	}

	/**********************/
	/**ВНУТРЕННИЕ ФУНКЦИИ**/
	/**********************/
	
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
	 * @param aDay
	 * @return
	 */
	private DaysOfWeek convertToDayOfWeek(String aDay){
		if(Pattern.matches("[Пп][Нн]", aDay)){
			return DaysOfWeek.mon;
		} else if(Pattern.matches("[Вв][Тт]", aDay)){
			return DaysOfWeek.tue;
		} else if(Pattern.matches("[Сс][Рр]", aDay)){
			return DaysOfWeek.wed;
		} else if(Pattern.matches("[Чч][Тт]", aDay)){
			return DaysOfWeek.thu;
		} else if(Pattern.matches("[Пп][Тт]", aDay)){
			return DaysOfWeek.fri;
		} else if(Pattern.matches("[Сс][Бб]", aDay)){
			return DaysOfWeek.sat;
		} else return null;
	}

	/**
	 * Функция проверяет содержимое записи на валидность. Если появляется
	 * неадекватный параметр, возвращается FALSE
	 */
	private boolean isValidRow(Cell[] entity) {
		boolean result = true;
		if (Pattern.matches("[0-9][0-9][0-9][0-9]", ExcelWorker.splitStr(entity[0].getContents()))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("[ПпВвСсЧч][НнТтРрБб]", ExcelWorker.splitStr(entity[1].getContents()))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("1*[0-9]:[0-5][0-9]", ExcelWorker.splitStr(entity[2].getContents()))) {
			result = true;
		} else
			return false;
		/**
		 * Частный случаи, когда проврека не доходит до конца: дни консультаций,
		 * проектные дни и т.д....
		 */
		if (ExcelWorker.splitStr(entity[5].getContents()).equals("")) {
			if (ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("день консультаций по самостоятельной работе")){
				return result;
			} else if(ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("проектный день")){
				return result;
			} else if(ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("производственный день")) {
				return result;
			}
		}
		if (ExcelWorker.splitStr(entity[5].getContents()).toLowerCase().equals("и.з.")) {
			if(ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("военная подготовка")){
				return result;
			} else if(ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("основы медицинских знаний и охрана здоровья")){
				return result;
			} else if(ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("основы медицинских знаний и охрана здоровья детей")){
				return result;
			} else if(ExcelWorker.splitStr(entity[4].getContents()).toLowerCase().equals("физическая культура")){
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
