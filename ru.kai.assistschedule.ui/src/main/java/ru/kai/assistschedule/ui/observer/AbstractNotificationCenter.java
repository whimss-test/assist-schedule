package ru.kai.assistschedule.ui.observer;

import java.util.HashSet;
import java.util.Set;

import ru.kai.assistschedule.ui.internal.views.processing.ScheduleTable;

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
     * Содержит модели за которыми можно наблюдать
     */
    private Set<IViewModel> _models = new HashSet<IViewModel>();
    
    public Set<ModelObserver> getObservers() {
		return _modelObservers;
	}

	public Set<IViewModel> getModels() {
		return _models;
	}

	public void addModel(IViewModel model) {
    	_models.add(model);
    }

    public void removeModel(IViewModel model) {
    	_models.remove(model);
    }
    
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