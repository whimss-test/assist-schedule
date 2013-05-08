/**
 * 
 */
package ru.kai.assistschedule.core.external.interfaces;

import org.eclipse.swt.graphics.Font;


/**
 * @author Роман
 * Консоль
 */
public interface IStatus {

   public void setText(String text);
   
   public void setFont(Font font);
   
   public void append(String string);

   public void appendLink(String link);
   
}
