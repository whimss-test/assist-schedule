package ru.kai.assistschedule.ui.model.schedule.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TreeItem;

import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;

public class ClassRoomFilter extends ViewerFilter {
	
	private Set<String> set;
	
	public ClassRoomFilter(Set<String> selected) {
		if(null == selected) {
			set = new HashSet<String>();
		}
		set = selected;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ScheduleEntry classRow = (ScheduleEntry) element;
		if(set.contains(classRow.classRoom)) {
			return true;
		}
		return false;
	}

}
