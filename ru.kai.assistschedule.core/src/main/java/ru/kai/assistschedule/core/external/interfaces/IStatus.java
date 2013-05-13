/**
 * 
 */
package ru.kai.assistschedule.core.external.interfaces;

import java.util.List;

import org.eclipse.swt.graphics.Font;

/**
 * @author Роман Консоль
 */
public interface IStatus {

	public void setText(String text);

	public void setFont(Font font);

	public void append(String string);

	public void appendLinks(List<String> links);

}
