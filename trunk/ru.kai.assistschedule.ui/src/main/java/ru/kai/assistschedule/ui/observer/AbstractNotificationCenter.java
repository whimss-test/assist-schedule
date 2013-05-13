package ru.kai.assistschedule.ui.observer;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Роман
 * Date: 21.06.12
 * Time: 11:05
 */
public abstract class AbstractNotificationCenter {

    /**
     * Содержит наблюдателей за данной моделью.
     */
    private Set<ModelObserver> _modelObservers = new HashSet<ModelObserver>();

    /**
     * Добавляем наблюдателя в общий список.
     *
     * @param aObserver наблюдатель
     */
    public void addObserver(ModelObserver aObserver) {
        _modelObservers.add(aObserver);
    }

    public void removeObserver(ModelObserver aObserver) {
        _modelObservers.remove(aObserver);
    }

    public void postNotification(IViewModel model, Notification notice) {
        /*
            отправляем события, только от тех моделей, которые нужны наблюдателю
         */
        for (ModelObserver mo : _modelObservers) {
            if (mo.containsSender(model)) {
                mo.update(model, notice);
            }
        }
    }

}