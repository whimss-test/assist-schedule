package ru.kai.assistschedule.ui.internal.views.processing;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.MainCommand;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.Time;
import ru.kai.assistschedule.core.calendar.Class;
import ru.kai.assistschedule.ui.model.schedule.ScheduleContentProvider;
import ru.kai.assistschedule.ui.model.schedule.ScheduleLabelProvider;
import ru.kai.assistschedule.ui.internal.views.AbstractScheduleTable;

public class ScheduleTable extends AbstractScheduleTable {

    protected static final Logger LOG = LoggerFactory
	    .getLogger(ScheduleTable.class);

    public ScheduleTable(Composite parent) {
	super(parent);
	MainCommand.setScheduleTableProcessing(this);
    }

    @Override
    protected void listeners() {
	// TODO Auto-generated method stub

    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
	return new ScheduleLabelProvider();
    }

    @Override
    protected IContentProvider getContentProvider() {
	return new ScheduleContentProvider();
    }

}
