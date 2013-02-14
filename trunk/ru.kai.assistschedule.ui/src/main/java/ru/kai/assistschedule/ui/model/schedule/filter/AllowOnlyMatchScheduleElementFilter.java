package ru.kai.assistschedule.ui.model.schedule.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Выдает выборку элементов названия которых удовлетворяет шаблону.
 * 
 * @author Роман
 *
 */
public class AllowOnlyMatchScheduleElementFilter extends ViewerFilter {

	private String pattern;
	
	private List<String> matchObjects = new ArrayList<String>();

    public List<String> getMatchObjects() {
        return matchObjects;
    }
	
	public AllowOnlyMatchScheduleElementFilter(String pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		String scheduleElementName = String.valueOf(element);
		
		//Возвращает true, если название элемента расписания содержит строку удовл. шаблону
        return isMatch(scheduleElementName);
	}
	
	private boolean isMatch (String scheduleElementName) {
        if (scheduleElementName.toLowerCase().contains(pattern.toLowerCase())) {
            matchObjects.add(scheduleElementName);
            return true;
        }
        return false;
    }

}
