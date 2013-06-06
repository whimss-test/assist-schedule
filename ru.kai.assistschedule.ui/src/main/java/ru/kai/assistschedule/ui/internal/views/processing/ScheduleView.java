package ru.kai.assistschedule.ui.internal.views.processing;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.ScheduleEntry;

public class ScheduleView extends ViewPart {

    public static final String ID = "ru.kai.assistantschedule.processing.schedule.view";

    private ScheduleTable scheduleTable;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
    	scheduleTable = new ScheduleTable(parent);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    	scheduleTable.setFocus();
    }

    @Override
    public void dispose() {
//    	List<ScheduleEntry> entries = FirstLevelCache.getInstance().getEntries();
//    	int i = 0;
//    	for(ScheduleEntry entry: entries) {
//    		System.out.println(entry);
//    		if(i++ >100) break;
//    	}
        // TODO Auto-generated method stub
        scheduleTable.dispose();
        super.dispose();
    }
    
}