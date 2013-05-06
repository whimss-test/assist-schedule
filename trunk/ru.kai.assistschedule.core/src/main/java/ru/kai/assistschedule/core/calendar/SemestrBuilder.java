package ru.kai.assistschedule.core.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SemestrBuilder {
	public List<Week> semestr = new ArrayList<Week>();
	private Calendar GC = GregorianCalendar.getInstance();
	
	public SemestrBuilder(Date begin, Date end){
		GC.setTime(begin);
		while(GC.getTime().before(end) || GC.getTime().equals(end)){
			semestr.add(new Week());
			switch (GC.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.MONDAY: semestr.get(semestr.size()-1).days.add(new Day(GC.getTime(), Calendar.MONDAY)); GC.add(Calendar.DATE, 1); if(GC.getTime().after(end))break;
				case Calendar.TUESDAY: semestr.get(semestr.size()-1).days.add(new Day(GC.getTime(), Calendar.TUESDAY)); GC.add(Calendar.DATE, 1); if(GC.getTime().after(end))break;
				case Calendar.WEDNESDAY: semestr.get(semestr.size()-1).days.add(new Day(GC.getTime(), Calendar.WEDNESDAY)); GC.add(Calendar.DATE, 1); if(GC.getTime().after(end))break;
				case Calendar.THURSDAY: semestr.get(semestr.size()-1).days.add(new Day(GC.getTime(), Calendar.THURSDAY)); GC.add(Calendar.DATE, 1); if(GC.getTime().after(end))break;
				case Calendar.FRIDAY: semestr.get(semestr.size()-1).days.add(new Day(GC.getTime(), Calendar.FRIDAY)); GC.add(Calendar.DATE, 1); if(GC.getTime().after(end))break;
				case Calendar.SATURDAY: semestr.get(semestr.size()-1).days.add(new Day(GC.getTime(), Calendar.SATURDAY)); GC.add(Calendar.DATE, 1); if(GC.getTime().after(end))break;
				default: GC.add(Calendar.DATE, 1);
			}
		}
		GC.setTime(begin);
	}

	public boolean isStreamClass(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if(semestr.get(i).isStreamClass(day, newClass)){
				return true;
			}
		}
		return result;
	} 

	public boolean isStreamClassInEvenWeek(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 1 ){//Избегаем нечетных недель
				continue;
			}
			if(semestr.get(i).isStreamClass(day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public boolean isStreamClassInUnevenWeek(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 0 ){//Избегаем четных недель
				continue;
			}
			if(semestr.get(i).isStreamClass(day, newClass)){
				return true;
			}
		}
		return result;
	} 

	public boolean isStreamClassBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if(semestr.get(i).isStreamClassBeforeTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean isStreamClassAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if(semestr.get(i).isStreamClassAfterTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public Class getClassByTimeAndClassroom(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			Class entry = semestr.get(i).getClassByTimeAndClassroom(day, newClass);
			if (entry != null){
				return entry;
			}
		}
		return null;
	} 

	public Class getClassByTimeAndClassroomInEvenWeek(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 1 ){//Избегаем нечетных недель
				continue;
			}
			Class entry = semestr.get(i).getClassByTimeAndClassroom(day, newClass);
			if (entry != null){
				return entry;
			}
		}
		return null;
	} 

	public Class getClassByTimeAndClassroomInUnevenWeek(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 0 ){//Избегаем четных недель
				continue;
			}
			Class entry = semestr.get(i).getClassByTimeAndClassroom(day, newClass);
			if (entry != null){
				return entry;
			}
		}
		return null;
	} 

	public Class getClassByTimeAndClassroomBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			Class entry = semestr.get(i).getClassByTimeAndClassroomBeforeTheDate(dateOfTheDay, day, newClass);
			if (entry != null){
				return entry;
			}
		}
		return null;
	}

	public Class getClassByTimeAndClassroomAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			Class entry = semestr.get(i).getClassByTimeAndClassroomAfterTheDate(dateOfTheDay, day, newClass);
			if (entry != null){
				return entry;
			}
		}
		return null;
	}
	
	public boolean contains(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if(semestr.get(i).contains(day, newClass)){
				return true;
			}
		}
		return result;
	} 

	public boolean containsInEvenWeek(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 1 ){//Избегаем нечетных недель
				continue;
			}
			if (semestr.get(i).contains(day, newClass)){
				return true;
			}
		}
		return result;
	} 
	
	public boolean containsInUnevenWeek(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 0 ){//Избегаем четных недель
				continue;
			}
			if (semestr.get(i).contains(day, newClass)){
				return true;
			}
		}
		return result;
	} 

	public boolean containsBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if (semestr.get(i).containsBeforeTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean containsAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if (semestr.get(i).containsAfterTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public void addToAllSemestr(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			semestr.get(i).addToAllSemestr(day, newClass);
		}
	}

	public void addToEvenWeeksOfSemestr(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 1 ){//Избегаем нечетных недель
				continue;
			}
			semestr.get(i).addToAllSemestr(day, newClass);
		}
	}

	public void addToUnevenWeeksOfSemestr(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 0 ){//Избегаем четных недель
				continue;
			}
			semestr.get(i).addToAllSemestr(day, newClass);
		}
	}
	
	public void addBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			semestr.get(i).addBeforeTheDate(dateOfTheDay, day, newClass);
		}
	}

	public void addAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			semestr.get(i).addAfterTheDate(dateOfTheDay, day, newClass);
		}
	}
	
	public void addGroupToStream(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			semestr.get(i).addGroupToStream(day, newClass);
		}
	}

	public void addGroupToStreamInEvenWeek(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 1 ){//Избегаем нечетных недель
				continue;
			}
			semestr.get(i).addGroupToStream(day, newClass);
		}
	}

	public void addGroupToStreamInUnevenWeek(int day, Class newClass){
		for(int i = 0; i < this.semestr.size(); i++){
			if ( (i+1)%2 == 0 ){//Избегаем четных недель
				continue;
			}
			semestr.get(i).addGroupToStream(day, newClass);
		}
	}

	public void addGroupToStreamBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			semestr.get(i).addGroupToStreamBeforeTheDate(dateOfTheDay, day, newClass);
		}
	}

	public void addGroupToStreamAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			semestr.get(i).addGroupToStreamAfterTheDate(dateOfTheDay, day, newClass);
		}
	}

	public boolean isDatesAreEqual(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.semestr.size(); i++){
			if(semestr.get(i).isDateAreEqual(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}

	public Calendar getDateOfClassBegining(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.semestr.size(); i++){
			Calendar entry = semestr.get(i).getDateOfClassBegining(dateOfTheDay, day, newClass);
			if (entry != null)
				return entry;
		}
		return null;
	}

}
