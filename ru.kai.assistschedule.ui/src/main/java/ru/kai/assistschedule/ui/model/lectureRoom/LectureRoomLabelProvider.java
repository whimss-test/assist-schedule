package ru.kai.assistschedule.ui.model.lectureRoom;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ru.kai.assistschedule.core.cache.LectureRoom;
import ru.kai.assistschedule.core.cache.LessonType;

public class LectureRoomLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		LectureRoom room = (LectureRoom) element;
		StringBuilder builder = new StringBuilder();
		builder.append(room.getName());
		for(LessonType type: room.getLessonTypes()) {
			builder.append("  ");
			builder.append(getLessonType(type));
		}
		return builder.toString();
	}

	private String getLessonType(LessonType lessonType) {
		switch (lessonType) {
		case LEC:
			return "лек";
		case PRAC:
			return "пр";
		case LABS:
			return "л.р.";
		case IZ:
			return "и.з.";
		case OTHER:
			return "";
		default:
			return "";
		}
	}
}
