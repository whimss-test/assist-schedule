package ru.kai.assistschedule.ui.model;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.cache.Constants;
import ru.kai.assistschedule.core.cache.DaysOfWeek;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;

/**
 * Изменяет значения ячеек в расписании
 * 
 * @author Roman
 * 
 */
public class ScheduleEntryCellModifier implements ICellModifier {

	private final static Logger logger = 
			LoggerFactory.getLogger(ScheduleEntryCellModifier.class);
	
	private Viewer viewer;

	public ScheduleEntryCellModifier(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		ScheduleEntry p = (ScheduleEntry) element;
		if (Constants.Schedule.GROUP.equals(property))
			return p.groupName;
		else if (Constants.Schedule.DAY_OF_WEEK.equals(property)) {
			logger.debug(p.day.toString());
			switch (p.day) {
				case mon: return 0;
				case tue: return 1;
				case wed: return 2;
				case thu: return 3;
				case fri: return 4;
				case sat: return 5;
			}
			return 0;
		} else if (Constants.Schedule.TIME.equals(property)) {
			switch (p.time) {
				case at08_00: return 0;
				case at09_40: return 1;
				case at11_30: return 2;
				case at13_10: return 3;
				case at15_00: return 4;
				case at16_40: return 5;
				case at18_15: return 6;
				case at19_45: return 7;
			}
			return 0;
		} else if (Constants.Schedule.DATE.equals(property))
			return p.date;
		else if (Constants.Schedule.DISCIPLINE.equals(property))
			return p.discipline;
		else if (Constants.Schedule.LESSON_TYPE.equals(property)) {
			switch (p.lessonType) {
				case LEC: return 0;
				case PRAC: return 1;
				case LABS: return 2;
				case IZ: return 3;
				case OTHER: return 4;
			}
			return 0;
		} else if (Constants.Schedule.CLASSROOM.equals(property))
			return p.classRoom;
		else if (Constants.Schedule.BUILDING.equals(property))
			return p.building;
		else if (Constants.Schedule.POSITION.equals(property))
			return p.doljnost;
		else if (Constants.Schedule.PROFESSOR.equals(property))
			return p.prepodavatel;
		else if (Constants.Schedule.DEPARTMENT.equals(property))
			return p.kafedra;
		else
			return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item)
			element = ((Item) element).getData();

		ScheduleEntry p = (ScheduleEntry) element;
		if (Constants.Schedule.GROUP.equals(property))
			p.groupName = String.valueOf(value);
		else if (Constants.Schedule.DAY_OF_WEEK.equals(property)) {
			int i = (Integer) value;
			logger.debug(String.valueOf(i));
			switch (i) {
				case 0: p.day = DaysOfWeek.mon; break;
				case 1: p.day = DaysOfWeek.tue; break;
				case 2: p.day = DaysOfWeek.wed; break;
				case 3: p.day = DaysOfWeek.thu; break;
				case 4: p.day = DaysOfWeek.fri; break;
				case 5: p.day = DaysOfWeek.sat; break;
			}
		} else if (Constants.Schedule.TIME.equals(property)) {
			int i = (Integer) value;
			switch (i) {
				case 0: p.time = Time.at08_00; break;
				case 1: p.time = Time.at09_40; break;
				case 2: p.time = Time.at11_30; break;
				case 3: p.time = Time.at13_10; break;
				case 4: p.time = Time.at15_00; break;
				case 5: p.time = Time.at16_40; break;
				case 6: p.time = Time.at18_15; break;
				case 7: p.time = Time.at19_45; break;
			}
		} else if (Constants.Schedule.DATE.equals(property))
			p.date = String.valueOf(value);
		else if (Constants.Schedule.DISCIPLINE.equals(property))
			p.discipline = String.valueOf(value);
		else if (Constants.Schedule.LESSON_TYPE.equals(property)) {
			int i = (Integer) value;
			switch (i) {
				case 0: p.lessonType = LessonType.LEC; break;
				case 1: p.lessonType = LessonType.PRAC; break;
				case 2: p.lessonType = LessonType.LABS; break;
				case 3: p.lessonType = LessonType.IZ; break;
				case 4: p.lessonType = LessonType.OTHER; break;
			}
		} else if (Constants.Schedule.CLASSROOM.equals(property))
			p.classRoom = String.valueOf(value);
		else if (Constants.Schedule.BUILDING.equals(property))
			p.building = String.valueOf(value);
		else if (Constants.Schedule.POSITION.equals(property))
			p.doljnost = String.valueOf(value);
		else if (Constants.Schedule.PROFESSOR.equals(property))
			p.prepodavatel = String.valueOf(value);
		else if (Constants.Schedule.DEPARTMENT.equals(property))
			p.kafedra = String.valueOf(value);

		viewer.refresh();
	}

}
