/**
 * 
 */
package ru.kai.assistschedule.ui.internal.views.status;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ru.kai.assistschedule.core.external.interfaces.IStatus;
import ru.kai.assistschedule.ui.internal.views.status.StatusView;
import ru.kai.assistschedule.ui.observer.IViewModel;
import ru.kai.assistschedule.ui.observer.LinkToScheduleEntry;
import ru.kai.assistschedule.ui.observer.NotificationCenter;


/**
 * Singleton - для работы со строкой состояния
 * @author Роман
 *
 */
public class StatusImpl implements IStatus, IViewModel {
    
	protected NotificationCenter _notificationCenter;
	
    private static IStatus instance;
    
    private StyleRange linkStyle;
    
    private StatusImpl() {
    	_notificationCenter = NotificationCenter.getDefaultCenter();
    	_notificationCenter.addModel(this);
    	
    	linkStyle = new StyleRange();
    	linkStyle.underline = true;
    	linkStyle.underlineStyle = SWT.UNDERLINE_LINK;
    	
    	StatusView.t2.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {
                // It is up to the application to determine when and how a link should be activated.
                // In this snippet links are activated on mouse down when the control key is held down
//                if ((event.stateMask & SWT.MOD1) != 0) {
//                    
//                }
                try {
                    int offset = StatusView.t2.getOffsetAtLocation(new Point(event.x, event.y));
                    StyleRange style = StatusView.t2.getStyleRangeAtOffset(offset);
                    if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                        System.out.println("Click on a Link");
                    
                    int start = 0, i = 0, end = 0;
                    while(!StatusView.t2.getTextRange((start = offset-(i++)), 1).equals(" ")) {}
                    end = start;
                    i=1;
                    while(!StatusView.t2.getTextRange((end = start+(i++)), 1).equals(" ")) {}
                    end--;
//                    System.out.println(String.format("start %d, end %d", start, end));
                    String link = StatusView.t2.getText(start, end);
                    link = link.replace(" ", "");
                    link = link.substring(9, link.length());
//                    System.out.println("Click on a Link - " + link + " Cell number: " + link);
                    _notificationCenter.postNotification(StatusImpl.this, new LinkToScheduleEntry(link));
                    }
                } catch (IllegalArgumentException e) {
                    // no character under event.x, event.y
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
	public void appendLinks(List<String> links) {
    	int[] ranges = new int[2*links.size()];
    	StyleRange[] styles = new StyleRange[links.size()];
    	for(int i = 0; i < links.size(); i++) {
    		int n = i*2; 
    		ranges[n] = StatusView.t2.getText().indexOf(links.get(i));
    		ranges[n+1] = links.get(i).length();
    		styles[i] = linkStyle;
    	}
        StatusView.t2.setStyleRanges(ranges, styles);
	}

    
}
