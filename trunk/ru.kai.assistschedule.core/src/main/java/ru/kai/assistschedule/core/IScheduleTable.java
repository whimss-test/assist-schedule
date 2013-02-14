package ru.kai.assistschedule.core;

import java.util.List;

import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.ScheduleEntry;

public interface IScheduleTable {
	public void setInput(List<ScheduleEntry> elements);
	
	public void setDataSource(FirstLevelCache firstLevelCache);
}
