package ru.kai.assistschedule.ui.observer;

public class LinkToScheduleEntry implements Notification {

	public int id;
	
	public LinkToScheduleEntry(String string) {
		try {
			id = Integer.parseInt(string);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
