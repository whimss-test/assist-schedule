package ru.kai.assistschedule.core.cache;

public enum DaysOfWeek {
	mon(1), 
	tue(2), 
	wed(3), 
	thu(4), 
	fri(5), 
	sat(6);
	
	/**
	 * Вес для каждого дня, необходим для сортировки
	 */
	private final int weight;
	
	private DaysOfWeek(int weight) {
		this.weight = weight;
	}
}
