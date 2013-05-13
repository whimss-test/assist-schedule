package ru.kai.assistschedule.ui.internal.views.processing;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.MainCommand;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
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
		v.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = 
						(IStructuredSelection) event.getSelection();
				ScheduleEntry entry = (ScheduleEntry)selection.getFirstElement(); 
				if(entry != null) {
					System.out.println(entry.id);
				}
			}
		});	
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
