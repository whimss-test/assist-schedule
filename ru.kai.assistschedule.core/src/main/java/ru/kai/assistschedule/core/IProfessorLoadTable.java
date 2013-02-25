package ru.kai.assistschedule.core;

import java.util.List;

import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.load.LoadEntry;

public interface IProfessorLoadTable {
	public void setInput(List<LoadEntry> elements);

	public void setDataSource(FirstLevelCache firstLevelCache);
}
