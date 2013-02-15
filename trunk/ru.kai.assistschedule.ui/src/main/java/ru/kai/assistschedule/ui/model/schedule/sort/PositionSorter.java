package ru.kai.assistschedule.ui.model.schedule.sort;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class PositionSorter extends AbstractScheduleSorter {

	public PositionSorter(boolean isDirectSort) {
		super(isDirectSort);
	}

	@Override
	protected int compare(ScheduleEntry first, ScheduleEntry second) {
//		LOGGER.debug(String.format("first[%s], second[%s]",
//				first.day, second.day));
		if (isDirectSort) {
			return first.doljnost.compareTo(second.doljnost);
		} else {
			return second.doljnost.compareTo(first.doljnost);
		}
	}

	
}
