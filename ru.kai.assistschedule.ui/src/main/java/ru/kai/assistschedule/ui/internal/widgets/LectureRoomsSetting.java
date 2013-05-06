package ru.kai.assistschedule.ui.internal.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LectureRoomsSetting {
	private final Shell shell;

	public LectureRoomsSetting(Composite parent) {
		shell = new Shell(parent.getShell(), SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.CLOSE);
		shell.setSize(500, 300);
		shell.setText("Настройка аудиторий");
		shell.setLocation(shell.getDisplay().getActiveShell().getLocation().x + getIncrement(true),
				shell.getDisplay().getActiveShell().getLocation().y + getIncrement(false));
		createView(shell);
		shell.open();
	}
	
	private void createView(Shell shell) {
		shell.setLayout(new FormLayout());
		
		Composite composite = new Composite(shell, SWT.NONE);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 10);
		data.left = new FormAttachment(0, 10);
		data.bottom = new FormAttachment(100, -10);
		data.right = new FormAttachment(100, -10);
		composite.setLayout(new FormLayout());
		composite.setLayoutData(data);

		Button addBtn = new Button(composite, SWT.PUSH);
		addBtn.setText("Добавить");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		addBtn.setLayoutData(data);

		Button labBtn = new Button(composite, SWT.CHECK);
		labBtn.setText("л.р.");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(addBtn, -10);
		labBtn.setLayoutData(data);
		
		Button pracBtn = new Button(composite, SWT.CHECK);
		pracBtn.setText("пр.");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(labBtn, -10);
		pracBtn.setLayoutData(data);

		Button lecBtn = new Button(composite, SWT.CHECK);
		lecBtn.setText("лек.");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(pracBtn, -10);
		lecBtn.setLayoutData(data);
		
		Text roomNumber = new Text(composite, SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lecBtn, -10);
		roomNumber.setLayoutData(data);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Список аудиторий:");
		data = new FormData();
		data.top = new FormAttachment(roomNumber, 10);
		data.left = new FormAttachment(0, 0);
		label.setLayoutData(data);
		
		Button delBtn = new Button(composite, SWT.PUSH);
		delBtn.setText("Удалить");
		data = new FormData();
		data.bottom = new FormAttachment(100, 0);
		data.right = new FormAttachment(100, 0);
		delBtn.setLayoutData(data);
		
		List list = new List(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		data = new FormData();
		data.top = new FormAttachment(label, 2);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(delBtn, -10);
		data.bottom = new FormAttachment(100, 0);
		list.setLayoutData(data);
	}
	
	/**
     * Возвращает отступ модального окна от левого верхнего угла основного
     *
     * @param isWidth true - если приращение расчитывается для ширины
     * @return приращение
     */
    private int getIncrement(boolean isWidth) {
        int d;
        if (isWidth) {
            d = (shell.getDisplay().getActiveShell().getBounds().width - shell.getSize().x) / 2;
        } else {
            d = (shell.getDisplay().getActiveShell().getBounds().height - shell.getSize().y) / 2;
        }
        return d;
    }
	
}
