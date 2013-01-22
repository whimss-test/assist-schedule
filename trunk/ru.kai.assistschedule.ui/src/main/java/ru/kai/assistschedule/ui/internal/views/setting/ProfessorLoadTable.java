package ru.kai.assistschedule.ui.internal.views.setting;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.Time;
import ru.kai.assistschedule.core.calendar.Class;
import ru.kai.assistschedule.ui.model.professorLoad.ProfessorLoadContentProvider;
import ru.kai.assistschedule.ui.model.professorLoad.ProfessorLoadLabelProvider;
import ru.kai.assistschedule.ui.internal.views.AbstractProfessorsLoadTable;

public class ProfessorLoadTable extends AbstractProfessorsLoadTable {

    protected static final Logger LOG = LoggerFactory
	    .getLogger(ProfessorLoadTable.class);

    public ProfessorLoadTable(Composite parent) {
	super(parent);
    }

    @Override
    protected void listeners() {
	// TODO Auto-generated method stub

    }

    @Override
    protected Class[] getInput() {
	Class[] elements = new Class[10];
	for (int i = 0; i < 10; i++) {
	    elements[i] = new Class(Time.at08_00, "lectureRoom_" + i,
		    "discipline_" + i, LessonType.LEC, "group_" + i,
		    "professor_" + i, "department_" + i);
	}

	return elements;
    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
	return new ProfessorLoadLabelProvider();
    }

    @Override
    protected IContentProvider getContentProvider() {
	return new ProfessorLoadContentProvider();
    }

}
