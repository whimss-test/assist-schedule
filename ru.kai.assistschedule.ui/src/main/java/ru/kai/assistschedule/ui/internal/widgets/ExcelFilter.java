package ru.kai.assistschedule.ui.internal.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.ui.internal.views.patterns.AbstractScheduleElementFactory;
import ru.kai.assistschedule.ui.model.schedule.ExcelFilterContentProvider;
import ru.kai.assistschedule.ui.model.schedule.ExcelFilterLabelProvider;
import ru.kai.assistschedule.ui.model.schedule.filter.AllowOnlyMatchScheduleElementFilter;
import ru.kai.assistschedule.ui.model.schedule.sort.AbstractScheduleSorter;
import ru.kai.assistschedule.ui.model.schedule.sort.DaySorter;
import ru.kai.assistschedule.ui.model.schedule.sort.GroupSorter;

public class ExcelFilter {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int windowWidth = 200;
	
	private GridColumn column;

	private Shell columnShell;
	
	private GridTableViewer gridTableViewer;

	private List<String> uniqueScheduleElements;

	private AllowOnlyMatchScheduleElementFilter treeViewerFilter;
	
	public ExcelFilter(GridColumn column) {
		this(column, null);
	}
	
	public ExcelFilter(GridColumn column, GridTableViewer gridTableViewer) {
		this(column, gridTableViewer, null);
	}
	
	public ExcelFilter(GridColumn column, GridTableViewer gridTableViewer, 
			 List<String> uniqueScheduleElements) {
		super();
		this.column = column;
		this.gridTableViewer = gridTableViewer;
		this.uniqueScheduleElements = uniqueScheduleElements;
		
		final GridColumn dayColumn = column;
		Shell mainShell = dayColumn.getParent().getShell();
		// GridColumn column = dayColumn;
		// column.getHeaderRenderer()
		final Shell child = new Shell(mainShell, SWT.DOUBLE_BUFFERED);
//		child.setBackground(new Color(child.getDisplay(), new RGB(200, 200, 0)));
		columnShell = child;
		child.setLocation(
				dayColumn.getParent().toDisplay(0, 0).x
						+ dayColumn.getHeaderRenderer().getBounds().x
						+ dayColumn.getHeaderRenderer().getBounds().width
						- windowWidth,
				dayColumn.getParent().toDisplay(0, 0).y
						+ dayColumn.getHeaderRenderer().getBounds().height);
		
		child.setLayout(new FormLayout());
		
		final Composite composite = new Composite(child, SWT.NONE);

//		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.right = new FormAttachment(100, 0);
		composite.setLayoutData(data);
		
		createView(composite);
		child.setSize(0, 0);
		child.open();
		
		dayColumn.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int i = 1;
				while (i++ < 11) {
					child.setSize(windowWidth, i * 25);
					try {
						TimeUnit.MILLISECONDS.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		child.addShellListener(new ShellListener() {
			
			@Override
			public void shellIconified(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
			
			@Override
			public void shellDeiconified(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
			
			@Override
			public void shellDeactivated(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println("shellDeactivated");
				dayColumn.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i = 10;
						while (i-- > 0) {
							child.setSize(windowWidth, i * 25);
							try {
								TimeUnit.MILLISECONDS.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						child.close();
					}
				});
			}
			
			@Override
			public void shellClosed(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e);
			}
		});
	}
	
	private void createView(Composite composite) {
		composite.setLayout(new FormLayout());
		
		Button buttonASC = new Button(composite, SWT.FLAT | SWT.WRAP);
		buttonASC.setText("Прямая сортировка");
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(50, 0);
		buttonASC.setLayoutData(data);
		buttonASC.addSelectionListener(new SelectionAdapter() {
			private boolean isDirectSort = false;
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gridTableViewer == null) {
					return;
				}
				gridTableViewer.setSorter(AbstractScheduleElementFactory
						.createSorter(column.getText(), true));
				column.setSort(SWT.DOWN);
			}
			
		});
		
		
		Button buttonDESC = new Button(composite, SWT.FLAT | SWT.WRAP);
		buttonDESC.setText("Обратная сортировка");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(buttonASC, 10);
		data.right = new FormAttachment(100, 0);
		buttonDESC.setLayoutData(data);
		buttonDESC.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (gridTableViewer == null) {
					return;
				}
				gridTableViewer.setSorter(AbstractScheduleElementFactory
						.createSorter(column.getText(), false));
				column.setSort(SWT.UP);
			}
			
		});
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Фильтрация элементов в дереве");
		data = new FormData();
		data.top = new FormAttachment(buttonASC, 10);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		label.setLayoutData(data);
		
		final Text textFilter = new Text(composite, SWT.BORDER);
		textFilter.setToolTipText("Начни фильтровать=)");
		
		data = new FormData();
		data.top = new FormAttachment(label, 5);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		textFilter.setLayoutData(data);
		
		Label labelResize = new Label(composite, SWT.WRAP);
		labelResize.setImage(ImageCache.getImage("icons/resize13x13.png"));
		labelResize.setToolTipText("Изменение размера окна");
		data = new FormData();
//		data.width = 13;
//		data.height = 13;
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		labelResize.setLayoutData(data);
		resizeWindow(composite, labelResize);
		
		
		Button cancel = new Button(composite, SWT.FLAT);
		cancel.setText("Отмена");
		data = new FormData();
		data.bottom = new FormAttachment(labelResize, -5);
//		data.left = new FormAttachment(0, 10);
		data.right = new FormAttachment(100, -5);
		cancel.setLayoutData(data);


		Button ok = new Button(composite, SWT.FLAT);
		ok.setText("Ок");
		data = new FormData();
		data.bottom = new FormAttachment(labelResize, -5);
//		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(cancel, -5);
		ok.setLayoutData(data);
		
		final TreeViewer treeViewerFilteredData = new TreeViewer(composite, SWT.CHECK);
		treeViewerFilteredData.setContentProvider(new ExcelFilterContentProvider());
		treeViewerFilteredData.setLabelProvider(new ExcelFilterLabelProvider());
		if(null != uniqueScheduleElements) {
			treeViewerFilteredData.setInput(uniqueScheduleElements);
		} else {
			treeViewerFilteredData.setInput(new ArrayList<String>());
		}
		data = new FormData();
		data.top = new FormAttachment(textFilter, 5);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(ok, -5);
		treeViewerFilteredData.getTree().setLayoutData(data);
		
		//Слушатель на вовод текста по которому будет фильтроваться treeViewer
		textFilter.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent e) {
				final StringBuilder newText = new StringBuilder(textFilter.getText());
                newText.replace(e.start, e.end, e.text);
                if (" ".equals(newText.toString())) {
                    return;
                }
                final TreeViewer treeViewer = treeViewerFilteredData;

                if (0 != treeViewer.getFilters().length) {
                    if (null != treeViewerFilter) {
                        treeViewer.removeFilter(treeViewerFilter);
                    }
                }
                
                treeViewerFilter = new AllowOnlyMatchScheduleElementFilter(newText.toString());
                treeViewer.getTree().setRedraw(false);
                treeViewer.addFilter(treeViewerFilter);
                treeViewer.getTree().getDisplay().asyncExec(new Runnable() {

                    public List<TreeItem> getAllTreeItems(TreeItem[] items) {
                        List<TreeItem> treeItems = new ArrayList<TreeItem>();
                        if (0 != items.length) {
                            for (TreeItem item : items) {
                                treeItems.add(item);
                                treeItems.addAll(getAllTreeItems(item.getItems()));
                            }
                        }
                        return treeItems;
                    }

                    @Override
                    public void run() {
                        synchronized (treeViewer) {
                            boolean isLive = true;
                            while (isLive) {
                                if (!treeViewer.isBusy()) {

                                    treeViewer.getTree().setRedraw(false);

                                    if (2 < newText.toString().length()) {
                                        treeViewer.expandAll();
                                        List<TreeItem> treeItems =
                                                getAllTreeItems(treeViewer.getTree().getItems());
                                        for (TreeItem item : treeItems) {
                                            String scheduleElementName = String.valueOf(item.getData());
                                            String patternText = newText.toString().toLowerCase();
                                            if (scheduleElementName.contains(patternText)) {
                                                item.setBackground(
                                                        new Color(treeViewer.getTree().getDisplay(), 220, 255, 255)
                                                );
                                            } else {
                                                item.setBackground(
                                                        treeViewer.getTree().getDisplay().getSystemColor(
                                                                SWT.COLOR_WHITE
                                                        )
                                                );
                                            }
                                        }
                                    } else {
                                        treeViewer.collapseAll();
                                        treeViewer.expandToLevel(2);
                                    }
                                    isLive = false;
                                    treeViewer.getTree().setRedraw(true);
                                }

                            }
                        }
                    }
                });
                treeViewer.getTree().setRedraw(true);
			}
		});
		
	}
	
	private void resizeWindow(final Composite composite, Label labelResize) {
		final Point[] offset = new Point[1];
		Listener listener = new Listener() {
		      public void handleEvent(Event event) {
		        switch (event.type) {
		        case SWT.MouseDown:
		        	logger.debug(String.format("event.x[%d], event.y[%d]", event.x, event.y));
		          Rectangle rect = composite.getBounds();
		          if (rect.contains(event.x, event.y)) {
		            Point pt1 = composite.toDisplay(0, 0);
		            Point pt2 = columnShell.toDisplay(event.x, event.y);
		            offset[0] = new Point(pt2.x - pt1.x, pt2.y - pt1.y);
		          }
		          logger.debug(String.format("top.x[%d], top.y[%d], width[%d], height[%d]", 
		        		  columnShell.toDisplay(0, 0).x, columnShell.toDisplay(0, 0).y,
		        		  columnShell.getBounds().width, columnShell.getBounds().height));
		          break;
		        case SWT.MouseMove:
		          if (offset[0] != null) {
		            Point pt = offset[0];
//		            composite.setLocation(event.x - pt.x, event.y - pt.y);
		            logger.debug(String.format("event.x[%d], event.y[%d]", event.x, event.y));
		            logger.debug(String.format("pt.x[%d], pt.x[%d]", pt.x, pt.y));
//		            if(event.x > 100 || event.y > 100) {
//		            	columnShell.setSize(event.x, event.y);
//		            }
		            int x = columnShell.toDisplay(0, 0).x;
		            int y = columnShell.toDisplay(0, 0).y;
		            int width = columnShell.getBounds().width;
		            int height = columnShell.getBounds().height;
		            logger.debug(String.format("newX[%d], newY[%d]", width + event.x, height + event.y));
		            columnShell.setRedraw(false);
		            columnShell.setSize(width + event.x, height + event.y);
		            columnShell.setRedraw(true);
		          }
		          break;
		        case SWT.MouseUp:
		          offset[0] = null;
		          logger.debug(String.format("event.x[%d], event.y[%d]", event.x, event.y));
		          break;
		        }
		      }
		    };
		    labelResize.addListener(SWT.MouseDown, listener);
		    labelResize.addListener(SWT.MouseUp, listener);
		    labelResize.addListener(SWT.MouseMove, listener);
	}
	
}
