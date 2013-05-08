package ru.kai.assistschedule.core.cache;

import java.util.List;
import java.util.UUID;

/**
 * Аудитория, необходим для окна настройки аудитории
 * @author Роман
 *
 */
public class LectureRoom {

	private UUID id;
	
	/** Аудитория */
	private String name;

	/** Вид занятия */
	private List<LessonType> lessonTypes;

	public LectureRoom() {
		super();
		id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<LessonType> getLessonTypes() {
		return lessonTypes;
	}

	public void setLessonTypes(List<LessonType> lessonTypes) {
		this.lessonTypes = lessonTypes;
	}

}
