package ru.kai.assistschedule.ui.observer;

/**
 * Наблюдатель, сообщает об изменениях произошедших в модели
 *
 * @author Администратор
 *
 */
public interface ModelObserver {

    /**
     * Обновить
     */
    public void update(IViewModel model, Notification notice);

    public boolean containsSender(IViewModel sender);

}
