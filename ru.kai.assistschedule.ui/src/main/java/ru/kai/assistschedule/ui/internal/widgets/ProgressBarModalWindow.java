package ru.kai.assistschedule.ui.internal.widgets;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressBarModalWindow {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProgressBarModalWindow.class);
	/**
     * Отдельный Shell для диалогового окна
     */
    private Shell child;
    
    /**
     * Ширина модального окна
     */
    private int _windowWidth = 500;

    /**
     * Высота модального окна
     */
    private int _windowHeight = 100;
    
	public ProgressBarModalWindow(Composite parent) {
		child = new Shell(parent.getShell(), SWT.APPLICATION_MODAL);
//		child.setLocation(
//				dayColumn.getParent().toDisplay(0, 0).x
//						+ dayColumn.getHeaderRenderer().getBounds().x
//						+ dayColumn.getHeaderRenderer().getBounds().width
//						- 150,
//				dayColumn.getParent().toDisplay(0, 0).y
//						+ dayColumn.getHeaderRenderer().getBounds().height);

		child.setLocation(child.getDisplay().getActiveShell().getLocation().x + getIncrement(true),
				child.getDisplay().getActiveShell().getLocation().y + getIncrement(false));
		
		child.setLayout(new GridLayout());
		final Composite composite = new Composite(child, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, true, true));
		createView(composite);
		child.setSize(_windowWidth, _windowHeight);
		child.open();
	}

	private void createView(Composite composite) {
		composite.setLayout(new GridLayout());
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Подождите. Происходит загрузка расписания...");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final ProgressBar pb1 = new ProgressBar(composite, SWT.HORIZONTAL | SWT.SMOOTH);
		pb1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    pb1.setMinimum(0);
	    pb1.setMaximum(30);
	    pb1.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				LOGGER.debug("paint ProgressBar " + e);
				if (pb1.getSelection() == pb1.getMaximum()) {
					LOGGER.debug("ProgressBar getSelection: " + pb1.getSelection());
					isClosable++;
					if (isNotCheckCloseStarted) {
						checkClose(pb1);
						isNotCheckCloseStarted = false;
					}
				}
				
			}
			
			
		});
	    
	 // Start the first ProgressBar
	    new LongRunningOperation(child.getDisplay(), pb1).start();
	}
	private boolean isNotCheckCloseStarted = true;
	private volatile int isClosable = 10; 
	
	private final void checkClose(final ProgressBar progressBar) {
		
		
		new Thread(new Runnable() {
			private volatile boolean isNotMaxSelection = true;
			@Override
			public void run() {
				
				while(!child.isDisposed()) {
					try {
						TimeUnit.MILLISECONDS.sleep(100l);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					isClosable -= 3;
					LOGGER.debug("isClosable = " + isClosable);
					if(isClosable < 0) {
						child.getDisplay().asyncExec(new Runnable() {
							public void run() {
								child.close();
							}
						});
						return;
					}
				}
			}
		}).start();
	}
	
	/**
	 * This class simulates a long-running operation
	 */
	class LongRunningOperation extends Thread {
	  private Display display;
	  private volatile ProgressBar progressBar;

	  public LongRunningOperation(Display display, ProgressBar progressBar) {
	    this.display = display;
	    this.progressBar = progressBar;
	  }
	  public void run() {
	    // Perform work here--this operation just sleeps
	    for (int i = 0; i < 30; i++) {
	      try {
	        Thread.sleep(100);
	      } catch (InterruptedException e) {
	        // Do nothing
	      }
	      display.asyncExec(new Runnable() {
	        public void run() {
	          if (progressBar.isDisposed()) return;

	          // Increment the progress bar
	          progressBar.setSelection(progressBar.getSelection() + 1);
//	          if (progressBar.getSelection() == progressBar.getMaximum()) {
//	        	  try {
//						TimeUnit.MILLISECONDS.sleep(3500L);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					child.close();
//	          }
	        }
	      });
	    }
	  }
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
            d = (child.getDisplay().getActiveShell().getBounds().width - _windowWidth) / 2;
        } else {
            d = (child.getDisplay().getActiveShell().getBounds().height - _windowHeight) / 2;
        }
        return d;
    }
	
}
