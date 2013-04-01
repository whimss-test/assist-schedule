package ru.kai.assistschedule.ui.model.schedule.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TreeItem;

import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;

public class TimeFilter extends ViewerFilter {
	
	private Set<Time> set = new HashSet<Time>();
	
	public TimeFilter(Object[] items) {
		String strTime; 
		for(Object item : items) {
			strTime = String.valueOf(item);
			if("8:00".equals(strTime)) {
				set.add(Time.at08_00);
			} else if("9:40".equals(strTime)) {
				set.add(Time.at09_40);
			} else if("11:30".equals(strTime)) {
				set.add(Time.at11_30);
			} else if("13:10".equals(strTime)) {
				set.add(Time.at13_10);
			} else if("15:00".equals(strTime)) {
				set.add(Time.at15_00);
			} else if("16:40".equals(strTime)) {
				set.add(Time.at16_40);
			} else if("18:15".equals(strTime)) {
				set.add(Time.at18_15);
			} else if("19:45".equals(strTime)) {
				set.add(Time.at19_45);
			}
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ScheduleEntry classRow = (ScheduleEntry) element;
		if(set.contains(classRow.time)) {
			return true;
		}
		return false;
	}

}
