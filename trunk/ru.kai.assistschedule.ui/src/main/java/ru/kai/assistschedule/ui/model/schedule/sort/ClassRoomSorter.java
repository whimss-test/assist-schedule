package ru.kai.assistschedule.ui.model.schedule.sort;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class ClassRoomSorter extends AbstractScheduleSorter {

	public ClassRoomSorter(boolean isDirectSort) {
		super(isDirectSort);
	}

	@Override
	protected int compare(ScheduleEntry first, ScheduleEntry second) {
//		LOGGER.debug(String.format("first[%s], second[%s]",
//				first.day, second.day));
		if (isDirectSort) {
			return first.day.compareTo(second.day);
		} else {
			return second.day.compareTo(first.day);
		}
	}

	
}
