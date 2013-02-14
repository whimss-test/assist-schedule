package ru.kai.assistschedule.core;

import ru.kai.assistschedule.core.cache.FirstLevelCache;

public class MainCommand {

	private static FirstLevelCache firstLevelCache;
	
	private static IScheduleTable scheduleTableSetting;
	
	private static IScheduleTable scheduleTableProcessing;
	
	public static void setFirstLevelCache(FirstLevelCache cache) {
		firstLevelCache = cache;
		if(null != scheduleTableSetting) {
			scheduleTableSetting.setInput(firstLevelCache.getEntries());
			scheduleTableSetting.setDataSource(firstLevelCache);
		}
		if(null != scheduleTableProcessing) {
			scheduleTableProcessing.setInput(firstLevelCache.getEntries());
			scheduleTableProcessing.setDataSource(firstLevelCache);
		}
	}
	
	public static void setScheduleTableSetting(IScheduleTable scheduleTable) {
		if(null == scheduleTable) {
			return;
		}
		scheduleTableSetting = scheduleTable;
		if(null != firstLevelCache) {
			scheduleTableSetting.setInput(firstLevelCache.getEntries());
			scheduleTableSetting.setDataSource(firstLevelCache);
		}
	}

	public static void setScheduleTableProcessing(IScheduleTable scheduleTable) {
		if(null == scheduleTable) {
			return;
		}
		scheduleTableProcessing = scheduleTable;
		if(null != firstLevelCache) {
			scheduleTableProcessing.setInput(firstLevelCache.getEntries());
			scheduleTableProcessing.setDataSource(firstLevelCache);
		}
	}
}