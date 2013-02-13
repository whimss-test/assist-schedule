package ru.kai.assistschedule.core.cache;

import javax.sql.DataSource;

public interface CacheDataSourceProvider {
	public DataSource getCacheDataSource();
}
