package ru.kai.assistschedule.core.cache;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SecondLevelCache<T> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	protected CacheDataSourceProvider dataSourceProvider;
	
	protected JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    /**
     * Первичная инициализация необходимых таблиц
     */
    protected abstract void initTables();

    /**
     * Сохранение элементов кэша в таблицах (элементы могут быть как новые, так и существующие
     *
     * @param cachedElements Список элементов кэша
     */
    protected abstract void saveCachedElementsInTables(T... cachedElements);

    /**
     * Удаление элементов кэша из таблицы
     *
     * @param removedElementsId Список идентификаторов удаляемых элементов
     * @return Список удаленных из хранилища элементов
     */
    protected abstract List<T> removeCachedElementsFromTables(String... removedElementsId);


    /**
     * Скачать список измененных после даты <code>synchronizationDate</code> элементов с сервера.
     *
     * @param synchronizationDate Дата синхронизации
     * @return Список элементов
     */
    protected abstract List<T> downloadChangedElements(Date synchronizationDate);

    /**
     * Скачать элемент с сервера
     *
     * @param id Идентификатор элемента
     * @return Элемент
     */
    protected abstract T downloadElement(String id);

    /**
     * Скачать список удаленных после даты <code>synchronizationDate</code> элементов с сервера.
     *
     * @param synchronizationDate Дата синхронизации
     * @return Список удаленных элементов
     */
    protected abstract List<T> downloadRemovedElements(Date synchronizationDate);


    /**
     * Получение даты синхронизации
     *
     * @return Дата последней синхронизации (максимальный changeDate)
     */
    protected abstract Date getSynchronizationDate();

    /**
     * Чтение элемента из таблицы
     *
     * @param id Идентификатор элемента
     * @return Элемент (без инициализации)
     */
    protected abstract T getElementFromTable(String id);

    protected abstract List<T> getElementsFromTable();


    /**
     * Инициализация компонента после входа в систему
     *
     * @throws Exception
     */
    protected synchronized void onInitialize() throws Exception {
        jdbcTemplate = new JdbcTemplate(dataSourceProvider.getCacheDataSource());
        initTables();
        synchronizeCache();
    }
    
    public String getServiceName() {
        return getClass().getInterfaces()[0].getName();
    }

    private void synchronizeCache() {
        LOGGER.info("Синхронизация кэша {}...", getServiceName());
        Date synchronizationDate = getSynchronizationDate();
        if (synchronizationDate != null) {
        	LOGGER.debug("Время старшего объекта кэша {}: {}", getServiceName(), synchronizationDate);
        } else {
        	LOGGER.debug("Синхронизация кэша {} не проводилась", getServiceName());
        }

//        String channelName = getChannelName();
//        if (channelName != null) {
//        	LOGGER.debug("Подписка кэша {} на события канала {}", getServiceName(), channelName);
//            messagingService.addChannelMessageListener(channelName, this);
//        } else {
//        	LOGGER.debug("Кэш {} не подписан на события", getServiceName());
//        }

        LOGGER.info("Синхронизация кэша {} по времени {}", getServiceName(), synchronizationDate);
        List<T> changedItems = downloadChangedElements(synchronizationDate);
        LOGGER.debug("Запрос измененных объектов кэша {}. Количество {}", getServiceName(), changedItems.size());
        List<T> removedItems = downloadRemovedElements(synchronizationDate);
        LOGGER.debug("Запрос удаленных объектов кэша {}. Количество {}", getServiceName(), removedItems.size());

        LOGGER.debug("Добавление {} записей в хранилище", changedItems.size());


        if (!changedItems.isEmpty()) {
            saveCachedElementsInTables(changedItems.toArray(
                    (T[]) Array.newInstance(changedItems.get(0).getClass(), changedItems.size()))
            );
        }


        LOGGER.debug("Удаление {} записей из хранилища", removedItems.size());
        if (!removedItems.isEmpty()) {
            String[] keys = new String[removedItems.size()];
            int i = 0;
            for (T removedItem : removedItems) {
//                keys[i++] = removedItem.getPrimaryKey();
            }
            removeCachedElementsFromTables(keys);
        }


        LOGGER.info("Кэш {} синхронизирован", getServiceName());
    }

    /**
     * Деинициализация компонента после выхода из сисетмы
     *
     * @throws Exception
     */
    protected void onDeinitialize() throws Exception {

    }
    
}
