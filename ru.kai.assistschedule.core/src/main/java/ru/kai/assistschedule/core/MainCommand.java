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
//		if (null != firstLevelCache && firstLevelCache.equals(cache)) {
//			return;
//		}
		
		firstLevelCache = cache;
		if (null != scheduleTableSetting) {
			scheduleTableSetting.setDataSource(firstLevelCache);
			scheduleTableSetting.setInput(firstLevelCache.getEntries());
		}
		if (null != scheduleTableProcessing) {
			scheduleTableProcessing.setDataSource(firstLevelCache);
			scheduleTableProcessing.setInput(firstLevelCache.getEntries());
		}
		if (null != professorLoadTableSetting) {
			professorLoadTableSetting.setDataSource(firstLevelCache);
			professorLoadTableSetting.setInput(firstLevelCache.getLoadElements());
		}
		if (null != professorLoadTableProcessing) {
			professorLoadTableProcessing.setDataSource(firstLevelCache);
			professorLoadTableProcessing.setInput(firstLevelCache.getLoadElements());
		}
	}

	public static void setScheduleTableSetting(IScheduleTable scheduleTable) {
		if (null == scheduleTable) {
			return;
		}
		scheduleTableSetting = scheduleTable;
		if (null != firstLevelCache) {
			scheduleTableSetting.setDataSource(firstLevelCache);
			scheduleTableSetting.setInput(firstLevelCache.getEntries());
			
		}
	}

	public static void setScheduleTableProcessing(IScheduleTable scheduleTable) {
		if (null == scheduleTable) {
			return;
		}
		scheduleTableProcessing = scheduleTable;
		if (null != firstLevelCache) {
			scheduleTableProcessing.setDataSource(firstLevelCache);
			scheduleTableProcessing.setInput(firstLevelCache.getEntries());
			
		}
	}

	public static void setProfessorLoadTableSetting(
			IProfessorLoadTable professorLoadTable) {
		if (null == professorLoadTable) {
			return;
		}
		professorLoadTableSetting = professorLoadTable;
		if (null != firstLevelCache) {
			professorLoadTableSetting.setDataSource(firstLevelCache);
			professorLoadTableSetting.setInput(firstLevelCache.getLoadElements());
			
		}
	}

	public static void setProfessorLoadTableProcessing(
			IProfessorLoadTable professorLoadTable) {
		if (null == professorLoadTable) {
			return;
		}
		professorLoadTableProcessing = professorLoadTable;
		if (null != firstLevelCache) {
			professorLoadTableProcessing.setDataSource(firstLevelCache);
			professorLoadTableProcessing.setInput(firstLevelCache.getLoadElements());
			
		}
	}

}
