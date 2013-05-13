package ru.kai.assistschedule.ui.observer;

public class NotificationCenter extends AbstractNotificationCenter {

    private static volatile NotificationCenter _defaultCenter;

    private NotificationCenter() {
        //
    }

    //multi thread-safe
    public static NotificationCenter getDefaultCenter() {
        if (_defaultCenter == null) {
            synchronized (NotificationCenter.class) {
                if (_defaultCenter == null) {
                    _defaultCenter = new NotificationCenter();
                }
            }
        }
        return _defaultCenter;
    }
    
}
