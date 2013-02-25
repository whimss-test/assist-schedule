package ru.kai.assistschedule.core;

import ru.kai.assistschedule.core.cache.FirstLevelCache;

public class MainCommand {

	private static FirstLevelCache firstLevelCache;

	private static IScheduleTable scheduleTableSetting;

	private static IScheduleTable scheduleTableProcessing;

	private static IProfessorLoadTable professorLoadTableSetting;

	private static IProfessorLoadTable professorLoadTableProcessing;

	public static void setFirstLevelCache(FirstLevelCache cache) {
		if (null == cache) {
			return;
		}
		if (null != firstLevelCache && firstLevelCache.equals(cache)) {
			return;
		}
		
		firstLevelCache = cache;
		if (null != scheduleTableSetting) {
			scheduleTableSetting.setInput(firstLevelCache.getEntries());
			scheduleTableSetting.setDataSource(firstLevelCache);
		}
		if (null != scheduleTableProcessing) {
			scheduleTableProcessing.setInput(firstLevelCache.getEntries());
			scheduleTableProcessing.setDataSource(firstLevelCache);
		}
		if (null != professorLoadTableSetting) {
			professorLoadTableSetting.setInput(firstLevelCache
					.getLoadElements());
			professorLoadTableSetting.setDataSource(firstLevelCache);
		}
		if (null != professorLoadTableProcessing) {
			professorLoadTableProcessing.setInput(firstLevelCache
					.getLoadElements());
			professorLoadTableProcessing.setDataSource(firstLevelCache);
		}
	}

	public static void setScheduleTableSetting(IScheduleTable scheduleTable) {
		if (null == scheduleTable) {
			return;
		}
		scheduleTableSetting = scheduleTable;
		if (null != firstLevelCache) {
			scheduleTableSetting.setInput(firstLevelCache.getEntries());
			scheduleTableSetting.setDataSource(firstLevelCache);
		}
	}

	public static void setScheduleTableProcessing(IScheduleTable scheduleTable) {
		if (null == scheduleTable) {
			return;
		}
		scheduleTableProcessing = scheduleTable;
		if (null != firstLevelCache) {
			scheduleTableProcessing.setInput(firstLevelCache.getEntries());
			scheduleTableProcessing.setDataSource(firstLevelCache);
		}
	}

	public static void setProfessorLoadTableSetting(
			IProfessorLoadTable professorLoadTable) {
		if (null == professorLoadTable) {
			return;
		}
		professorLoadTableSetting = professorLoadTable;
		if (null != firstLevelCache) {
			professorLoadTableSetting.setInput(firstLevelCache.getLoadElements());
			professorLoadTableSetting.setDataSource(firstLevelCache);
		}
	}

	public static void setProfessorLoadTableProcessing(
			IProfessorLoadTable professorLoadTable) {
		if (null == professorLoadTable) {
			return;
		}
		professorLoadTableProcessing = professorLoadTable;
		if (null != firstLevelCache) {
			professorLoadTableProcessing.setInput(firstLevelCache.getLoadElements());
			professorLoadTableProcessing.setDataSource(firstLevelCache);
		}
	}

}
