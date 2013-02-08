package ru.kai.assistschedule.ui.model.schedule.sort;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public abstract class AbstractScheduleSorter extends ViewerSorter {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	protected boolean isDirectSort;

	public AbstractScheduleSorter(boolean isDirectSort) {
		super();
		this.isDirectSort = isDirectSort;
	}
	
	protected abstract int compare(ScheduleEntry first, ScheduleEntry second);

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		ScheduleEntry first = (ScheduleEntry) e1;
		ScheduleEntry second = (ScheduleEntry) e2;
		
		return compare(first, second);
	}

}
