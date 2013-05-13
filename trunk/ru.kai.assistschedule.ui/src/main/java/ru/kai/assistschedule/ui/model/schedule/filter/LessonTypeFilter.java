package ru.kai.assistschedule.ui.model.schedule.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TreeItem;

import ru.kai.assistschedule.core.cache.DaysOfWeek;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;

public class LessonTypeFilter extends ViewerFilter {
	
	private Set<LessonType> set = new HashSet<LessonType>();
	
	public LessonTypeFilter(Set<String> selected) {
		for(String s: selected) {
			if("лек".equals(s)) {
				set.add(LessonType.LEC);
			} else if("пр".equals(s)) {
				set.add(LessonType.PRAC);
			} else if("л.р.".equals(s)) {
				set.add(LessonType.LABS);
			} else if("и.з.".equals(s)) {
				set.add(LessonType.IZ);
			} else if("".equals(s)) {
				set.add(LessonType.OTHER);
			}
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ScheduleEntry classRow = (ScheduleEntry) element;
		if(set.contains(classRow.lessonType)) {
			return true;
		}
		return false;
	}

}
