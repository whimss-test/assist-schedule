/**
 * 
 */
package ru.kai.assistschedule.ui.internal.views.status;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ru.kai.assistschedule.ui.internal.views.status.StatusView;


/**
 * Singleton - для работы со строкой состояния
 * @author Роман
 *
 */
public class StatusImpl implements IStatus {
    
    private static IStatus instance;
    
    private StyleRange linkStyle;
    
    private StatusImpl() {
    	linkStyle = new StyleRange();
    	linkStyle.underline = true;
    	linkStyle.underlineStyle = SWT.UNDERLINE_LINK;
    	
    	StatusView.t2.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {
                // It is up to the application to determine when and how a link should be activated.
                // In this snippet links are activated on mouse down when the control key is held down
                if ((event.stateMask & SWT.MOD1) != 0) {
                    try {
                        int offset = StatusView.t2.getOffsetAtLocation(new Point(event.x, event.y));
                        StyleRange style = StatusView.t2.getStyleRangeAtOffset(offset);
                        if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                            System.out.println("Click on a Link");
                        }
                    } catch (IllegalArgumentException e) {
                        // no character under event.x, event.y
                    }

                }
            }
        });
    }
    
    public static IStatus getInstance() {
        if(instance == null) {
            instance = new StatusImpl();
        }
        return instance;
    }
    /* (non-Javadoc)
     * @see ru.kai.assistantschedule.status.open.IStatus#setText(java.lang.String)
     */
    @Override
    public void setText(String text) {
        StatusView.t2.setText(text);
    }

    @Override
    public void setFont(Font font) {
        StatusView.t2.setFont(font);
    }

    @Override
    public void append(String string) {
        StatusView.t2.append(string);
    }

    @Override
	public void appendLink(String link) {
    	System.out.println("appendLink: " + link);
    	append(" "+link+" ");
		// TODO Auto-generated method stub
    	int[] ranges = {StatusView.t2.getText().indexOf(link), link.length()};
        StyleRange[] styles = {linkStyle};
        StatusView.t2.setStyleRanges(ranges, styles);
	}

    
}
