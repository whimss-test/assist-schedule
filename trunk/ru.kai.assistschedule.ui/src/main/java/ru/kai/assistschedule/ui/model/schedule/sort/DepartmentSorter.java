package ru.kai.assistschedule.ui.model.schedule.sort;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class DepartmentSorter extends AbstractScheduleSorter {

	public DepartmentSorter(boolean isDirectSort) {
		super(isDirectSort);
	}

	@Override
	protected int compare(ScheduleEntry first, ScheduleEntry second) {
//		LOGGER.debug(String.format("first[%s], second[%s]",
//				first.day, second.day));
		if (isDirectSort) {
			return first.kafedra.compareTo(second.kafedra);
		} else {
			return second.kafedra.compareTo(first.kafedra);
		}
	}

	
}
