package ru.kai.assistschedule.ui.observer;

public class AddProfessorInScheduleEntry implements Notification {

	public String professor;
	
	public int id;

	public AddProfessorInScheduleEntry(String id, String string) {
		professor = string;
		try {
			this.id = Integer.parseInt(id);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
