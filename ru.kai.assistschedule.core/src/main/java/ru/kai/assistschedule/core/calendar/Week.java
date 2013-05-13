package ru.kai.assistschedule.core.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Week {

	public List<Day> days = new ArrayList<Day>();

	public boolean maybeStreamClass(int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).maybeStreamClass(day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean maybeStreamClassBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).maybeStreamClassBeforeTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public boolean maybeStreamClassAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).maybeStreamClassAfterTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public Class getMaybeStreamClass(int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			Class entry = days.get(i).getMaybeStreamClass(day, newClass);
			if(entry != null){
				return entry;
			}
		}
		return null;
	}

	public Class getMaybeStreamClassBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			Class entry = days.get(i).getMaybeStreamClassBeforeTheDate(dateOfTheDay, day, newClass);
			if(entry != null){
				return entry;
			}
		}
		return null;
	}
	
	public Class getMaybeStreamClassAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			Class entry = days.get(i).getMaybeStreamClassAfterTheDate(dateOfTheDay, day, newClass);
			if(entry != null){
				return entry;
			}
		}
		return null;
	}
	
	public List<String> findEmptyClassRoom(int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			List<String> emptyClassrooms = days.get(i).findEmptyClassRoom(day, newClass);
			if(emptyClassrooms != null){
				return emptyClassrooms;
			}
		}
		return null;
	}
	
	public List<String> findEmptyClassRoomBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			List<String> emptyClassrooms = days.get(i).findEmptyClassRoomBeforeTheDate(dateOfTheDay, day, newClass);
			if(emptyClassrooms != null){
				return emptyClassrooms;
			}
		}
		return null;
	}
	
	public List<String> findEmptyClassRoomAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			List<String> emptyClassrooms = days.get(i).findEmptyClassRoomAfterTheDate(dateOfTheDay, day, newClass);
			if(emptyClassrooms != null){
				return emptyClassrooms;
			}
		}
		return null;
	}
	
	public boolean isStreamClass(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).isStreamClass(day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean isStreamClassBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).isStreamClassBeforeTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean isStreamClassAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).isStreamClassAfterTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public Class getClassByTimeAndClassroom(int day, Class newClass){
		for(int i = 0; i < this.days.size(); i++){
			Class entry = days.get(i).getClassByTimeAndClassroom(day, newClass);
			if(entry != null){
				return entry;
			}
		}
		return null;
	}

	public Class getClassByTimeAndClassroomBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			Class entry = days.get(i).getClassByTimeAndClassroomBeforeTheDate(dateOfTheDay, day, newClass);
			if(entry != null){
				return entry;
			}
		}
		return null;
	}

	public Class getClassByTimeAndClassroomAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			Class entry = days.get(i).getClassByTimeAndClassroomAfterTheDate(dateOfTheDay, day, newClass);
			if(entry != null){
				return entry;
			}
		}
		return null;
	}
	
	public boolean contains(int day, Class newClass){
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).contains(day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean containsBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).containsBeforeTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}

	public boolean containsAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).containsAfterTheDate(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}
	
	public void addToAllSemestr(int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			days.get(i).add(day, newClass);
		}
	}

	public void addBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			days.get(i).addBeforeTheDate(dateOfTheDay, day, newClass);
		}
	}

	public void addAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			days.get(i).addAfterTheDate(dateOfTheDay, day, newClass);
		}
	}
	
	public void addGroupToStream(int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			days.get(i).addGroup(day, newClass);
		}
	}

	public void addGroupToStreamBeforeTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			days.get(i).addGroupBeforeTheDate(dateOfTheDay, day, newClass);
		}
	}

	public void addGroupToStreamAfterTheDate(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			days.get(i).addGroupAfterTheDate(dateOfTheDay, day, newClass);
		}
	}

	public boolean isDateAreEqual(Calendar dateOfTheDay, int day, Class newClass) {
		boolean result = false;
		for(int i = 0; i < this.days.size(); i++){
			if(days.get(i).isDateAreEqual(dateOfTheDay, day, newClass)){
				return true;
			}
		}
		return result;
	}

	public Calendar getDateOfClassBegining(Calendar dateOfTheDay, int day, Class newClass) {
		for(int i = 0; i < this.days.size(); i++){
			Calendar result = days.get(i).getDateOfClassBegining(dateOfTheDay, day, newClass);
			if(result != null)
				return result;
		}
		return null;
	}

}
