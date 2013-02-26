package ru.kai.assistschedule.ui.model.professorLoad;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ru.kai.assistschedule.core.cache.load.FormOfClass;
import ru.kai.assistschedule.core.cache.load.LoadEntry;
import ru.kai.assistschedule.core.calendar.Class;

public class ProfessorLoadLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableFontProvider, ITableColorProvider {
	
	FontRegistry registry = new FontRegistry();

	@Override
	public Color getForeground(Object element, int columnIndex) {
//		if (((MyModel) element).counter % 2 == 1) {
//			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
//		}
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
//		if (((MyModel) element).counter % 2 == 0) {
//			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
//		}
		return null;
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
//		if (((MyModel) element).counter % 2 == 0) {
//			return registry.getBold(Display.getCurrent().getSystemFont()
//					.getFontData()[0].getName());
//		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		// Выводим данные в колонках
		LoadEntry classRow = (LoadEntry) element;
		
		switch (columnIndex) {
			case 0:
				return String.valueOf(classRow.id);
			case 1:
				return classRow.semestr;
			case 2:
				return classRow.educationForm;
			case 3:
				return classRow.spec_group;
			case 4:
				return classRow.discipline;
			case 5:
				return String.valueOf(classRow.groupCount);
			case 6:
				return String.valueOf(classRow.subGroupCount);
			case 7:
				return String.valueOf(classRow.weekCount);
			case 8:
				return formOfClass(classRow.lec);
			case 9:
				return formOfClass(classRow.prac);
			case 10:
				return formOfClass(classRow.labs);
			default:
				return "";
		}
	}
	
	private String formOfClass(FormOfClass formOfClass) {
		if (null == formOfClass) {
			return "";
		}
		return String.format("%f/%f/%s", formOfClass.hoursInWeek,
				formOfClass.totalHours, formOfClass.professor);
	}

}
