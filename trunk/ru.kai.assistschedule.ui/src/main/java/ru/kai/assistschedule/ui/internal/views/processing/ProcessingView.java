package ru.kai.assistschedule.ui.internal.views.processing;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ProcessingView extends ViewPart {

    public static final String ID = "ru.kai.assistantschedule.processing.view";

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
	// TODO Auto-generated method stub
	shelf.dispose();
	super.dispose();
    }

}