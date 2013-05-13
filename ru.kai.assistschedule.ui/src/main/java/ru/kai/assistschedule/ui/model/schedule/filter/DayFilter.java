package ru.kai.assistschedule.ui.model.schedule.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TreeItem;

import ru.kai.assistschedule.core.cache.DaysOfWeek;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;

public class DayFilter extends ViewerFilter {
	
	private Set<DaysOfWeek> set = new HashSet<DaysOfWeek>();
	
	public DayFilter(Set<String> selected) {
		for(String s: selected) {
			if("пн".equals(s)) {
				set.add(DaysOfWeek.mon);
			} else if("вт".equals(s)) {
				set.add(DaysOfWeek.tue);
			} else if("ср".equals(s)) {
				set.add(DaysOfWeek.wed);
			} else if("чт".equals(s)) {
				set.add(DaysOfWeek.thu);
			} else if("пт".equals(s)) {
				set.add(DaysOfWeek.fri);
			} else if("сб".equals(s)) {
				set.add(DaysOfWeek.sat);
			}
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ScheduleEntry classRow = (ScheduleEntry) element;
		if(set.contains(classRow.day)) {
			return true;
		}
		return false;
	}

}
