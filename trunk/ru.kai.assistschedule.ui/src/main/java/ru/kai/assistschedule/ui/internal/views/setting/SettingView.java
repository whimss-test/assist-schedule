package ru.kai.assistschedule.ui.internal.views.setting;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import ru.kai.assistschedule.core.GlobalStorage;
import ru.kai.assistschedule.ui.internal.views.ApplicationToolbar;

public class SettingView extends ViewPart {
	
	private static final Logger LOG = LoggerFactory.getLogger(SettingView.class);

    public static final String ID = "ru.kai.assistantschedule.setting.view";

    private ActivityPShelf shelf;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
//    	parent.setFont(new Font(parent.getDisplay(), new FontData("PT Serif",10,SWT.ITALIC)));
        shelf = new ActivityPShelf(parent);
        try {
        	String pathHack[] = System.getProperty("settings").split("file:");
        	GlobalStorage.load(new File(pathHack[1]));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    	shelf.setFocus();
    }

    @Override
    public void dispose() {
    	try {
    		String pathHack[] = System.getProperty("settings").split("file:");
			
			if(GlobalStorage.save(new File(pathHack[1]))) {
				LOG.info("Настройки сохранены!");
			}
    		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	shelf.dispose();
        super.dispose();
    }
    
}