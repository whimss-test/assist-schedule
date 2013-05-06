package ru.kai.assistschedule.core.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.Time;

public class Day {
	private Calendar dateOfTheDay = new GregorianCalendar();
	public final String dateStr;
//@ FIXME: У меня в программе используется Class как элемент списка,
//	В принципе смысл такой же остается.
//	public List<ScheduleEntry> classes = new ArrayList<ScheduleEntry>();
	public List<Class> classes = new ArrayList<Class>();
	public int DayOfWeek;
	
	public Day(Date date, int aDayOfWeek){
		dateOfTheDay.setTime(date);
		DayOfWeek = aDayOfWeek;
		String[] tmp = date.toString().split(" ");
		dateStr = tmp[0]+" "+tmp[2]+" "+" "+tmp[1]+" "+tmp[5]+" "+tmp[3];
	}

	public String getDate(){
		return dateOfTheDay.getTime().toString();
	}

	public void addClass(Time t, String audit, String disc, LessonType FoC, String prepod, String kaf, String groups ){
		classes.add(new Class(t, audit, disc, FoC, groups, prepod, kaf));
	}

	/**
	 * Функция проверки свободности данной аудитории в данное время.
	 * Это не говорит, что это накладка, возможны проведения занатий в потоке
	 * @param day 
	 * @param newClass занятие, которое нужно добавить в общее расписание
	 * @return true, если в это время данная аудитория занята
	 */
	public boolean contains(int day, Class newClass){
		boolean result = false;
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isTimeAndClassroomEquals(newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean containsBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		if (this.dateOfTheDay.after(dateOfTheDay)){
			return result;
		}
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isTimeAndClassroomEquals(newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean containsAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		if (this.dateOfTheDay.before(dateOfTheDay)){
			return result;
		}
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isTimeAndClassroomEquals(newClass)){
				return true;
			}
		}
		return result;
	}

	/**
	 * Функция проверяет является ли это занятие потоковым на основе совпадения дисциплины,
	 * формы занятия, преподавателя и кафедры
	 * @param newClass проверяемое занятие
	 * @return true - если это занятие в потоке
	 */
	public boolean isStreamClass(int day, Class newClass){
		boolean result = false;
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isStreamClass(newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean isStreamClassBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		if (this.dateOfTheDay.after(dateOfTheDay)){
			return result;
		}
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isStreamClass(newClass)){
				return true;
			}
		}
		return result;
	}
	
	public boolean isStreamClassAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		if (this.dateOfTheDay.before(dateOfTheDay)){
			return result;
		}
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isStreamClass(newClass)){
				return true;
			}
		}
		return result;
	}
	
	public void add(int day, Class newClass) {
		if (DayOfWeek != day){
			return;
		} else {
			classes.add(newClass);
		}
	}

	public void addBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		if (this.dateOfTheDay.after(dateOfTheDay)){
			return;
		}
		if (DayOfWeek != day){
			return;
		} else {
			classes.add(newClass);
		}
	}
	
	public void addAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		if (this.dateOfTheDay.before(dateOfTheDay)){
			return;
		}
		if (DayOfWeek != day){
			return;
		} else {
			classes.add(newClass);
		}
	}
	
	public void addGroup(int day, Class newClass) {
		if (DayOfWeek != day){
			return;
		} else {
			for (int i = 0; i < classes.size(); i++){
				if (classes.get(i).isStreamClass(newClass)){
					if(!classes.get(i).group.contains(newClass.group)){
						classes.get(i).group += ", " + newClass.group;
						break;
					}
				}
			}
		}
	}

	public void addGroupBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		if (this.dateOfTheDay.after(dateOfTheDay)){
			return;
		}
		if (DayOfWeek != day){
			return;
		} else {
			for (int i = 0; i < classes.size(); i++){
				if (classes.get(i).isStreamClass(newClass)){
					if(!classes.get(i).group.contains(newClass.group)){
						classes.get(i).group += ", " + newClass.group;
						break;
					}
				}
			}
		}
	}
	
	public void addGroupAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		if (this.dateOfTheDay.before(dateOfTheDay)){
			return;
		}
		if (DayOfWeek != day){
			return;
		} else {
			for (int i = 0; i < classes.size(); i++){
				if (classes.get(i).isStreamClass(newClass)){
					if(!classes.get(i).group.contains(newClass.group)){
						classes.get(i).group += ", " + newClass.group;
						break;
					}
				}
			}
		}
	}

	public void emptyAuditory(int day, Time time, Class newClass){
		if (DayOfWeek != day){
			return;
		} else {
		}
	}

	public Class getClassByTimeAndClassroom(int day, Class newClass){
		if (DayOfWeek != day){
			return null;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isTimeAndClassroomEquals(newClass)){
				return classes.get(i);
			}
		}
		return null;
	}

	public Class getClassByTimeAndClassroomBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		if (this.dateOfTheDay.after(dateOfTheDay)){
			return null;
		}
		if (DayOfWeek != day){
			return null;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isTimeAndClassroomEquals(newClass)){
				return classes.get(i);
			}
		}
		return null;
	}
	
	public Class getClassByTimeAndClassroomAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		if (this.dateOfTheDay.before(dateOfTheDay)){
			return null;
		}
		if (DayOfWeek != day){
			return null;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isTimeAndClassroomEquals(newClass)){
				return classes.get(i);
			}
		}
		return null;
	}


	public boolean isDateAreEqual(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		if (DayOfWeek != day){
			return result;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isStreamClass(newClass) && dateOfTheDay.equals(this.dateOfTheDay)){
				return true;
			}
		}
		return result;
	}
	
	public Calendar getDateOfClassBegining(Calendar dateOfTheDay, int day, Class newClass) {
		if (DayOfWeek != day){
			return null;
		}
		for(int i = 0; i < this.classes.size(); i++){
			if(classes.get(i).isStreamClass(newClass)){
				if (dateOfTheDay.equals(this.dateOfTheDay))
					return null;
				else
					return this.dateOfTheDay;
			}
		}
		return null;
	}
}
