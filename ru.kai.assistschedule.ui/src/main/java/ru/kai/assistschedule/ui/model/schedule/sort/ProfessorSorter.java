package ru.kai.assistschedule.ui.model.schedule.sort;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class ProfessorSorter extends AbstractScheduleSorter {

	public ProfessorSorter(boolean isDirectSort) {
		super(isDirectSort);
		logger.debug("ProfessorSorter: " + this);
	}

	@Override
	protected int compare(ScheduleEntry first, ScheduleEntry second) {
//		LOGGER.debug(String.format("first[%s], second[%s]",
//				first.day, second.day));
		if (isDirectSort) {
			return first.prepodavatel.compareTo(second.prepodavatel);
		} else {
			return second.prepodavatel.compareTo(first.prepodavatel);
		}
	}

	
}
