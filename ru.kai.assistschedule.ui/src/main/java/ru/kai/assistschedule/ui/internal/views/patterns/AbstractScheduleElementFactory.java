package ru.kai.assistschedule.ui.internal.views.patterns;

import java.util.Set;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TreeItem;

import ru.kai.assistschedule.ui.model.schedule.filter.BuildingFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.ClassRoomFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.DateFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.DayFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.DepartmentFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.DisciplineFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.GroupNameFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.LessonTypeFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.PositionFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.ProfessorFilter;
import ru.kai.assistschedule.ui.model.schedule.filter.TimeFilter;
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
	
	public static ViewerFilter createFilter(String columnName, Set<String> selected) {
		if (columnName.equals("Группа")) {
			return new GroupNameFilter(selected);
		} else if (columnName.equals("День недели")) {
			return new DayFilter(selected);
		} else if (columnName.equals("Время")) {
			return new TimeFilter(selected);
		} else if (columnName.equals("Дата")) {
			return new DateFilter(selected);
		} else if (columnName.equals("Дисциплина")) {
			return new DisciplineFilter(selected);
		} else if (columnName.equals("Вид занятий")) {
			return new LessonTypeFilter(selected);
		} else if (columnName.equals("Аудитория")) {
			return new ClassRoomFilter(selected);
		} else if (columnName.equals("Здание")) {
			return new BuildingFilter(selected);
		} else if (columnName.equals("Должность")) {
			return new PositionFilter(selected);
		} else if (columnName.equals("Преподаватель")) {
			return new ProfessorFilter(selected);
		} else if (columnName.equals("Кафедра")) {
			return new DepartmentFilter(selected);
		}
		return null;
	}
}
