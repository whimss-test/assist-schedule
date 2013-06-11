package ru.kai.assistschedule.core.utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import jxl.Cell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.ExcelWorker;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;
import ru.kai.assistschedule.core.calendar.Class;
import ru.kai.assistschedule.core.calendar.SemestrBuilder;
import ru.kai.assistschedule.core.exceptions.SheduleIsNotOpenedException;
import ru.kai.assistschedule.core.external.interfaces.IStatus;

public class ScheduleChecker {
	
	private final static Logger LOGGER = LoggerFactory
            .getLogger(ScheduleChecker.class);

	private static ScheduleChecker instance = new ScheduleChecker();
	
	private ScheduleChecker() {}
	
	public static ScheduleChecker getInstance() {
		if(null == instance) {
			instance = new ScheduleChecker();
		}
		return instance;
	}
	
	public void checkEveryWeek(IStatus console, SemestrBuilder SB, List<String> links) {
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<ScheduleEntry> kafPMIclasses = new ArrayList<ScheduleEntry>();
		
		//Данные из таблицы с расписанием
		List<ScheduleEntry> entries = FirstLevelCache.getInstance().getEntries();
		ScheduleEntry currentEntry;
		for (int i = 1; i < entries.size(); i++) {
			currentEntry = entries.get(i);
			if (!isValidEntry(currentEntry) || !isAddableToScheduleToBuilding7(currentEntry)) {
				continue;
			}
				
			if (currentEntry.date.equals("") || 
					Pattern.matches( "[чн]е[чт]/[нч]е[чт]", currentEntry.date.toLowerCase())) {

				String str = currentEntry.classRoom;
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || 
						Pattern.matches("[Кк][Аа].", str) || 
							Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(currentEntry.kafedra.toLowerCase()))){
						kafPMIclasses.add(currentEntry);
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = ExcelWorker.convetToDayOfWeek(ExcelUtils.getDay(currentEntry.day)); // конвертируем день
					Time time = currentEntry.time; // конвертируем вермя
					LessonType FoC = currentEntry.lessonType; // --//-- форму занятий
					Class newClass = new Class(time, currentEntry.classRoom, 
							currentEntry.discipline, FoC, currentEntry.groupName, 
							currentEntry.prepodavatel, currentEntry.kafedra);
					newClass.id = (i + 1);
					
					if(SB.contains(day, newClass)){
						if(SB.isStreamClass(day, newClass)){
							SB.addGroupToStream(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroom(day, newClass);
							console.append(deleteSpaces(ExcelUtils.getDay(currentEntry.day)) + " " + 
									deleteSpaces(ExcelUtils.getTime(currentEntry.time)) + " Аудитория: " + 
									newClass.lectureRoom);//Ошибка. Вывести общие данные
							
							ExcelWorker.errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
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
			currentEntry = kafPMIclasses.get(i);
			int day = ExcelWorker.convetToDayOfWeek(ExcelUtils.getDay(currentEntry.day)); // конвертируем день
			Time time = currentEntry.time; // конвертируем вермя
			LessonType FoC = currentEntry.lessonType; // --//-- форму занятий
			Class newClass = new Class(time, currentEntry.classRoom, 
					currentEntry.discipline, FoC, currentEntry.groupName, 
					currentEntry.prepodavatel, currentEntry.kafedra);

			if (SB.maybeStreamClass(day, newClass)) {
				Class entry = SB.getMaybeStreamClass(day, newClass);
				console.append(deleteSpaces(ExcelUtils.getDay(currentEntry.day)) + " " + 
						deleteSpaces(ExcelUtils.getTime(currentEntry.time)) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoom(day, newClass);
				console.append(deleteSpaces(ExcelUtils.getDay(currentEntry.day)) + " " + 
						deleteSpaces(ExcelUtils.getTime(currentEntry.time)) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
				if (emptyClassrooms.isEmpty()) {
					console.append("\nСвободных аудиторий нет!\n\n");
				} else {
					newClass.lectureRoom = emptyClassrooms.get(0);
					console.append("\nНазначена аудитория: " + newClass.lectureRoom + " из возможных: ");
					for (int k = 0; k < emptyClassrooms.size(); k++) {
						console.append( (k==0?"":", ") + emptyClassrooms.get(k) + (k==(emptyClassrooms.size()-1)?"\n\n":""));
					}
					LOGGER.debug("addInEveryWeek: " + newClass);
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
	
	public void checkEvenWeek(IStatus console, SemestrBuilder SB, List<String> links) {
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<ScheduleEntry> kafPMIclasses = new ArrayList<ScheduleEntry>();
		
		//Данные из таблицы с расписанием
		List<ScheduleEntry> entries = FirstLevelCache.getInstance().getEntries();
		ScheduleEntry currentEntry;
		
		for (int i = 1; i < entries.size(); i++) {
			currentEntry = entries.get(i);
			if (!isValidEntry(currentEntry) || !"7".equals(currentEntry.building))
				continue;
			
			if (Pattern.matches( "[ч][её]?[т]?", currentEntry.date.toLowerCase() )){

				String str = currentEntry.classRoom;
				if (Pattern.matches("[Кк][Аа]?[Фф]?", str) || Pattern.matches("[Кк][Аа].", str) || Pattern.matches("[Кк]..", str)) {
					if (Pattern.matches("пми", deleteSpaces(currentEntry.kafedra.toLowerCase()))) {
						kafPMIclasses.add(currentEntry);
					}
				} else if (Pattern.matches("[0-9]{3}[А-Яа-я]?", str)) {
					int day = ExcelWorker.convetToDayOfWeek(ExcelUtils.getDay(currentEntry.day)); // конвертируем день
					Time time = currentEntry.time; // конвертируем вермя
					LessonType FoC = currentEntry.lessonType; // --//-- форму занятий
					Class newClass = new Class(time, currentEntry.classRoom, 
							currentEntry.discipline, FoC, currentEntry.groupName, 
							currentEntry.prepodavatel, currentEntry.kafedra);
					newClass.id = (i + 1);
					
					if(SB.containsInEvenWeek(day, newClass)){
						if(SB.isStreamClassInEvenWeek(day, newClass)){
							SB.addGroupToStreamInEvenWeek(day, newClass);//Занятие в потоке, добавить группу к занятию
							added++;
							doubleAdded++;
						} else {
							Class entry = SB.getClassByTimeAndClassroomInEvenWeek(day, newClass);
							console.append(deleteSpaces(ExcelUtils.getDay(currentEntry.day)) + " " + 
									deleteSpaces(ExcelUtils.getTime(currentEntry.time)) + " Аудитория: " + 
									newClass.lectureRoom);//Ошибка. Вывести общие данные
							ExcelWorker.errorAnalysis(entry, newClass, console, links);	//Вывод подробных данных
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
			currentEntry = kafPMIclasses.get(i);
			int day = ExcelWorker.convetToDayOfWeek(ExcelUtils.getDay(currentEntry.day)); // конвертируем день
			Time time = currentEntry.time; // конвертируем вермя
			LessonType FoC = currentEntry.lessonType; // --//-- форму занятий
			Class newClass = new Class(time, currentEntry.classRoom, 
					currentEntry.discipline, FoC, currentEntry.groupName, 
					currentEntry.prepodavatel, currentEntry.kafedra);

			if (SB.maybeStreamClassInEvenWeek(day, newClass)){
				Class entry = SB.getMaybeStreamClassInEvenWeek(day, newClass);
				console.append(deleteSpaces(ExcelUtils.getDay(currentEntry.day)) + " " + 
						deleteSpaces(ExcelUtils.getTime(currentEntry.time)) + " Возможно занятие в потоке! Отредактируйте вручную!\n");
				console.append("Группа: "+entry.group + " " + entry.lessonType + " Дисциплиа: " + entry.discipline +" Аудитория: "+ entry.lectureRoom + " Преподаватель: " + entry.professor + "\n");
				console.append("Группа: "+newClass.group + " " + newClass.lessonType + " Дисциплиа: " + newClass.discipline +" Аудитория: "+ newClass.lectureRoom + " Преподаватель: " + newClass.professor + "\n\n");
			} else {
				List<String> emptyClassrooms = SB.findEmptyClassRoomInEvenWeek(day, newClass);
				console.append(deleteSpaces(ExcelUtils.getDay(currentEntry.day)) + " " + 
						deleteSpaces(ExcelUtils.getTime(currentEntry.time)) + " Группа(ы): " + newClass.group + " Дисциплина: " + newClass.discipline);
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
	
	public void checkUnevenWeek(IStatus console, SemestrBuilder SB, List<String> links) {
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<ScheduleEntry> kafPMIclasses = new ArrayList<ScheduleEntry>();
		
		//Данные из таблицы с расписанием
		List<ScheduleEntry> entries = FirstLevelCache.getInstance().getEntries();
		ScheduleEntry currentEntry;
		
		for (int i = 1; i < entries.size(); i++) {
			currentEntry = entries.get(i);
			if (!isValidEntry(currentEntry) || !"7".equals(currentEntry.building))
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

	public void checkBefore(IStatus console, SemestrBuilder SB, List<String> links) {
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<ScheduleEntry> kafPMIclasses = new ArrayList<ScheduleEntry>();
		
		//Данные из таблицы с расписанием
		List<ScheduleEntry> entries = FirstLevelCache.getInstance().getEntries();
		ScheduleEntry currentEntry;
		
		for (int i = 1; i < entries.size(); i++) {
			currentEntry = entries.get(i);
			if (!isValidEntry(currentEntry) || !"7".equals(currentEntry.building))
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

	public void checkAfter(IStatus console, SemestrBuilder SB, List<String> links) {
		int itogoEntries = 0, AddedInPMI = 0, added = 0, errors = 0, doubleAdded = 0;
		List<ScheduleEntry> kafPMIclasses = new ArrayList<ScheduleEntry>();
		
		//Данные из таблицы с расписанием
		List<ScheduleEntry> entries = FirstLevelCache.getInstance().getEntries();
		ScheduleEntry currentEntry;
		
		for (int i = 1; i < entries.size(); i++) {
			currentEntry = entries.get(i);
			if (!isValidEntry(currentEntry) || !"7".equals(currentEntry.building))
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
	
	/**
	 * Функция проверяет содержимое записи на валидность. Если появляется
	 * неадекватный параметр, возвращается FALSE
	 */
	private boolean isValidEntry(ScheduleEntry entry) {
		boolean result = true;
		if (Pattern.matches("[0-9]{4}", deleteSpaces(entry.groupName))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("[ПпВвСсЧч][НнТтРрБб]", deleteSpaces(ExcelUtils.getDay(entry.day)))) {
			result = true;
		} else
			return false;
		if (Pattern.matches("1?[0-9]:[0-5][0-9]", deleteSpaces(ExcelUtils.getTime(entry.time)))) {
			result = true;
		} else
			return false;
		if (!"".equals(entry.discipline)) {
			result = true;
		} else
			return false;

		return result;
	}
	
	/**
	 * Удаляет все пробелы из строки
	 * @param input входная строка с пробелами
	 * @return выходная строка без пробелов
	 */
	private String deleteSpaces(String input) {
		return input.replace(" ", "");
	}
	
	/**
	 * Функция анализирует возможность добавления данной записи, по имеющейся в ней информации
	 * @param entity запись для анализа
	 * @return true, если в записи достаточно информации для добавления в расписание
	 */
	private boolean isAddableToScheduleToBuilding7(ScheduleEntry entry){
		boolean result = true;
		if ("7".equals(entry.building)) {
			result = true;
		} else
			return false;
		String lessonType = deleteSpaces(ExcelUtils.getLessonType(entry.lessonType).toLowerCase());
		if (Pattern.matches("лек.*", lessonType) || 
				Pattern.matches("л.*р.*", lessonType) ||
					Pattern.matches("пр.*", lessonType)) {
			result = true;
		} else
			return false;
		
		if ( Pattern.matches( "[0-9]{3}[а-я]?", deleteSpaces(entry.classRoom.toLowerCase())) ||
				(deleteSpaces(entry.classRoom.toLowerCase()).equals("каф") && 
						Pattern.matches( "пми", deleteSpaces(entry.kafedra.toLowerCase()))) ) {
			result = true;
		} else
			return false;
		
		return result;
	}
	
}
