package ru.kai.assistschedule.ui.internal.widgets;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ImageCache {

    private static HashMap<String, Image> mImageMap;
    
    static {
    	mImageMap = new HashMap<String, Image>();
    }
    
    /**
     * Returns an image that is also cached if it has to be created and does not already exist in the cache.
     * 
     * @param fileName Filename of image to fetch
     * @return Image file or null if it could not be found
     */
    public static Image getImage(String fileName) {
        Image image = mImageMap.get(fileName);
        if (image == null) {
            image = createImage(fileName);
            mImageMap.put(fileName, image);
        }
        return image;
    }

    // creates the image, and tries really hard to do so
    private static Image createImage(String fileName) {
        ClassLoader classLoader = ImageCache.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(fileName);
        if (is == null) {
            // the old way didn't have leading slash, so if we can't find the image stream,
            // let's see if the old way works.
            is = classLoader.getResourceAsStream(fileName.substring(1));

            if (is == null) {
                is = classLoader.getResourceAsStream(fileName);
                if (is == null) {
                    is = classLoader.getResourceAsStream(fileName.substring(1));
                    if (is == null) {
                        try {
                            is = new FileInputStream(fileName);
                            if (is== null) {
                                return null;
                            }
                        } catch (FileNotFoundException e) {
                            return null;
                        }
                        //logger.debug("null input stream for both " + path + " and " + path);
                    }
                }
            }
        }

        Image img = new Image(Display.getDefault(), is);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    /**
     * Disposes ALL images that have been cached.
     *
     */
    public static void dispose() {
//        Iterator e = mImageMap.values().iterator();
//        while (e.hasNext()) {
//            ((Image)e.next()).dispose();
//        }
        for (Image image : mImageMap.values()) {
            if(!image.isDisposed()) {
                image.dispose();
            }
        }
    }
}
