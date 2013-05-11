package ru.kai.assistschedule.ui.internal.widgets;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.kai.assistschedule.core.GlobalStorage;
import ru.kai.assistschedule.core.cache.LectureRoom;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.ui.internal.views.utils.Popup;
import ru.kai.assistschedule.ui.model.lectureRoom.LectureRoomContentProvider;
import ru.kai.assistschedule.ui.model.lectureRoom.LectureRoomLabelProvider;

public class LectureRoomsSetting {
	private final Shell shell;

	private Composite composite;

	private ListViewer listViewer;

	public LectureRoomsSetting(Composite parent) {
		shell = new Shell(parent.getShell(), SWT.TITLE | SWT.BORDER
				| SWT.RESIZE | SWT.CLOSE);
		shell.setSize(500, 300);
		shell.setText("Настройка аудиторий");
		shell.setLocation(shell.getDisplay().getActiveShell().getLocation().x
				+ getIncrement(true), shell.getDisplay().getActiveShell()
				.getLocation().y
				+ getIncrement(false));

		composite = new Composite(shell, SWT.NONE);

		listViewer = new ListViewer(composite);
		listViewer.setContentProvider(new LectureRoomContentProvider());
		listViewer.setLabelProvider(new LectureRoomLabelProvider());
		createView(shell);
		listeners();
		listViewer.setInput(GlobalStorage.getLectureRooms());
		shell.open();
	}

	// /**
	// * Множество названий аудитории
	// * @param classRooms
	// */
	// public void setInput(Set<String> classRooms) {
	// java.util.List<LectureRoom> list = new ArrayList<LectureRoom>();
	// for (String s: classRooms) {
	// if(s.matches("[0-9][0-9][0-9][А-Яа-я]{0,1}")) {
	// LectureRoom room = new LectureRoom();
	// room.setName(s);
	// list.add(room);
	// }
	// }
	// listViewer.setInput(list);
	// }

	private void listeners() {
		
//		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				IStructuredSelection selection = (IStructuredSelection) event
//						.getSelection();
//				StringBuffer sb = new StringBuffer("Selection - ");
//				sb.append("tatal " + selection.size() + " items selected: ");
//				for (Iterator iterator = selection.iterator(); iterator
//						.hasNext();) {
//					sb.append(iterator.next() + ", ");
//				}
//				System.out.println(sb);
//			}
//		});
//
//		roomNumber.addVerifyListener(new VerifyListener() {
//
//			@Override
//			public void verifyText(VerifyEvent e) {
//				String text = roomNumber.getText();
//				if (text.length() == 3) {
//					if (e.text.matches("[А-Яа-я]{0,1}")) {
//						e.doit = true;
//						return;
//					}
//				} else if (text.length() < 3) {
//					if (e.text.matches("[0-9]{0,3}")) {
//						e.doit = true;
//						return;
//					}
//				} else if (text.length() == 4) {
//					if (e.text.matches("")) {
//						e.doit = true;
//						return;
//					}
//				}
//				e.doit = false;
//			}
//		});
		
		addBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				LectureRoom room = new LectureRoom();
				try {
					fillRoom(room);
					GlobalStorage.addLectureRoom(room);
					listViewer.add(room);
					clearFields();
					StructuredSelection selection = new StructuredSelection(room);
					listViewer.setSelection(selection);
				} catch (IllegalArgumentException exp) {
					MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
					box.setMessage(exp.getMessage());
					box.open();
				}
			}

		});

		delBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				LectureRoom delRoom = (LectureRoom) 
						((IStructuredSelection) listViewer.getSelection()).getFirstElement();
				if(null == delRoom) {
					MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
					box.setMessage("Выберите аудиторию для удаления!");
					box.open();
					return;
				}
				GlobalStorage.removeLectureRoom(delRoom);
				listViewer.remove(delRoom);
				Popup.make(listViewer.getList().getParent(), 
						String.format("Аудитория %s успешно удалена!", delRoom.getName()), 
						2000, SWT.COLOR_GREEN).show();
			}

		});
	}
	
	private void clearFields() {
		roomNumber.setText("");
		labBtn.setSelection(false);
		lecBtn.setSelection(false);
		pracBtn.setSelection(false);
	}

	private void fillRoom(LectureRoom room) {
		String text = roomNumber.getText();
		if (text.isEmpty()) {
			throw new IllegalArgumentException("Незадан номер аудитории!");
		}
		if (room == null) {
			room = new LectureRoom();
		}
		if(text.matches("[0-9]{3}[А-Яа-я]{0,1}")) {
			room.setName(roomNumber.getText());
		} else {
			throw new IllegalArgumentException("Номер должен содержать три цифры в начале и [русскую букву(необязательно)]!");
		}
		
		java.util.List<LessonType> lessonTypes = new ArrayList<LessonType>();

		if (labBtn.getSelection()) {
			lessonTypes.add(LessonType.LABS);
		}

		if (lecBtn.getSelection()) {
			lessonTypes.add(LessonType.LEC);
		}

		if (pracBtn.getSelection()) {
			lessonTypes.add(LessonType.PRAC);
		}

		room.setLessonTypes(lessonTypes);
	}

	private Button addBtn;

	private Button labBtn;

	private Button pracBtn;

	private Button lecBtn;

	private Button delBtn;

	private Text roomNumber;

	private void createView(Shell shell) {
		shell.setLayout(new FormLayout());

		FormData data = new FormData();
		data.top = new FormAttachment(0, 10);
		data.left = new FormAttachment(0, 10);
		data.bottom = new FormAttachment(100, -10);
		data.right = new FormAttachment(100, -10);
		composite.setLayout(new FormLayout());
		composite.setLayoutData(data);

		addBtn = new Button(composite, SWT.PUSH);
		addBtn.setText("Добавить");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		addBtn.setLayoutData(data);

		labBtn = new Button(composite, SWT.CHECK);
		labBtn.setText("л.р.");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(addBtn, -10);
		labBtn.setLayoutData(data);

		pracBtn = new Button(composite, SWT.CHECK);
		pracBtn.setText("пр.");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(labBtn, -10);
		pracBtn.setLayoutData(data);

		lecBtn = new Button(composite, SWT.CHECK);
		lecBtn.setText("лек.");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.right = new FormAttachment(pracBtn, -10);
		lecBtn.setLayoutData(data);

		roomNumber = new Text(composite, SWT.BORDER);
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

		delBtn = new Button(composite, SWT.PUSH);
		delBtn.setText("Удалить");
		data = new FormData();
		data.bottom = new FormAttachment(100, 0);
		data.right = new FormAttachment(100, 0);
		delBtn.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(label, 2);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(delBtn, -10);
		data.bottom = new FormAttachment(100, 0);
		listViewer.getList().setLayoutData(data);
	}

	/**
	 * Возвращает отступ модального окна от левого верхнего угла основного
	 * 
	 * @param isWidth
	 *            true - если приращение расчитывается для ширины
	 * @return приращение
	 */
	private int getIncrement(boolean isWidth) {
		int d;
		if (isWidth) {
			d = (shell.getDisplay().getActiveShell().getBounds().width - shell
					.getSize().x) / 2;
		} else {
			d = (shell.getDisplay().getActiveShell().getBounds().height - shell
					.getSize().y) / 2;
		}
		return d;
	}

}
