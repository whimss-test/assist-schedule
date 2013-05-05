package ru.kai.assistschedule.ui.internal.views.setting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class SettingView extends ViewPart {

    public static final String ID = "ru.kai.assistantschedule.setting.view";

    private ActivityPShelf shelf;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
//    	parent.setFont(new Font(parent.getDisplay(), new FontData("PT Serif",10,SWT.ITALIC)));
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