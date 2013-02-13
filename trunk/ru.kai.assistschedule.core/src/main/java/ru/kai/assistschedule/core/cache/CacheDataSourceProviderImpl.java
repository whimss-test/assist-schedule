package ru.kai.assistschedule.core.cache;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import ru.kai.assistschedule.core.exceptions.ServiceException;

public class CacheDataSourceProviderImpl implements CacheDataSourceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheDataSourceProviderImpl.class);
	
	private SingleConnectionDataSource dataSource;
	
	public CacheDataSourceProviderImpl() throws Exception {
		LOGGER.info("Создание источника данных локального кэша");
		
		try {
            dataSource = new SingleConnectionDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUsername("admin");
            dataSource.setPassword("admin");
            dataSource.setUrl("jdbc:h2:~/.assistschedule/db/storage;");
            LOGGER.debug("Источник данных: {} ", dataSource.getUrl());
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
		LOGGER.info("Источник данных локального кэша создан");
	}
	
	@Override
	public DataSource getCacheDataSource() {
		return dataSource;
	}

}
