package ru.kai.assistschedule.ui.internal.views.patterns;

import ru.kai.assistschedule.ui.model.schedule.sort.AbstractScheduleSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.BuildingSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.ClassRoomSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.DateSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.DaySorter;
import ru.kai.assistschedule.ui.model.schedule.sort.DepartmentSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.DisciplineSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.GroupSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.LessonTypeSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.PositionSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.ProfessorSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.TimeSorter;

public abstract class AbstractScheduleElementFactory {
	
	public static AbstractScheduleSorter createSorter(String columnName, boolean isDirectSort) {
		if (columnName.equals("Группа")) {
			return new GroupSorter(isDirectSort);
		} else if (columnName.equals("День недели")) {
			return new DaySorter(isDirectSort);
		} else if (columnName.equals("Время")) {
			return new TimeSorter(isDirectSort);
		} else if (columnName.equals("Дата")) {
			return new DateSorter(isDirectSort);
		} else if (columnName.equals("Дисциплина")) {
			return new DisciplineSorter(isDirectSort);
		} else if (columnName.equals("Вид занятий")) {
			return new LessonTypeSorter(isDirectSort);
		} else if (columnName.equals("Аудитория")) {
			return new ClassRoomSorter(isDirectSort);
		} else if (columnName.equals("Здание")) {
			return new BuildingSorter(isDirectSort);
		} else if (columnName.equals("Должность")) {
			return new PositionSorter(isDirectSort);
		} else if (columnName.equals("Преподаватель")) {
			return new ProfessorSorter(isDirectSort);
		} else if (columnName.equals("Кафедра")) {
			return new DepartmentSorter(isDirectSort);
		} else {
			return null;
		}
	}
}
