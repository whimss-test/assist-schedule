package ru.kai.assistschedule.ui.internal.views.setting;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class SettingView extends ViewPart {

    public static final String ID = "ru.kai.assistantschedule.setting.view";

    private ActivityPShelf shelf;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        shelf = new ActivityPShelf(parent);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    	shelf.setFocus();
    }

    @Override
    public void dispose() {
    	shelf.dispose();
        super.dispose();
    }
    
}