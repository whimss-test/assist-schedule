package ru.kai.assistschedule.ui.model.schedule.sort;

import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class GroupSorter extends AbstractScheduleSorter {

	public GroupSorter(boolean isDirectSort) {
		super(isDirectSort);
	}

	@Override
	protected int compare(ScheduleEntry first, ScheduleEntry second) {
		try {
			int iFirst = Integer.parseInt(first.groupName);
			int iSecond = Integer.parseInt(second.groupName);

			if (isDirectSort) {
				if (iFirst > iSecond) {
					return 1;
				} else if (iFirst < iSecond) {
					return -1;
				}
			} else {
				if (iFirst > iSecond) {
					return -1;
				} else if (iFirst < iSecond) {
					return 1;
				}
			}
			return 0;
		} catch (NumberFormatException e) {
			logger.debug(String.format("Exception[%s], first[%s], second[%s]",
					e, first.groupName, second.groupName));
			if (isDirectSort) {
				return first.groupName.compareTo(second.groupName);
			} else {
				return second.groupName.compareTo(first.groupName);
			}
		}
	}

}
