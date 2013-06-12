package ru.kai.assistschedule.ui.observer;

public class MessageNotification implements Notification {

	public String msg;
	
	public MessageNotification(String string) {
		msg = string;
	}
}
