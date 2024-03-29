package ru.kai.assistschedule.ui.model.schedule.sort;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class DisciplineSorter extends AbstractScheduleSorter {

	public DisciplineSorter(boolean isDirectSort) {
		super(isDirectSort);
	}

	@Override
	protected int compare(ScheduleEntry first, ScheduleEntry second) {
//		LOGGER.debug(String.format("first[%s], second[%s]",
//				first.day, second.day));
		if (isDirectSort) {
			return first.discipline.compareTo(second.discipline);
		} else {
			return second.discipline.compareTo(first.discipline);
		}
	}

	
}
