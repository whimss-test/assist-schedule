package ru.kai.assistschedule.ui.internal.views.utils;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import ru.kai.assistschedule.ui.internal.widgets.ImageCache;

public class Popup {

	private final Shell shell;
	
	private final Popup instance;
		
	static int[] circle(int r, int offsetX, int offsetY) {
		int[] polygon = new int[8 * r + 4];
		// x^2 + y^2 = r^2
		for (int i = 0; i < 2 * r + 1; i++) {
			int x = i - r;
			int y = (int) Math.sqrt(r * r - x * x);
			polygon[2 * i] = offsetX + x;
			polygon[2 * i + 1] = offsetY + y;
			polygon[8 * r - 2 * i - 2] = offsetX + x;
			polygon[8 * r - 2 * i - 1] = offsetY - y;
		}
		return polygon;
	}

	public Popup(Composite parent, String text, final int duration, int SWT_COLOR) {
		instance = this;
		shell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		shell.setBackground(parent.getDisplay().getSystemColor(SWT_COLOR));
	    
		// define a region that looks like a key hole
	    Region region = new Region();
//	    region.add(new int[] { 0, 0, 0, 60, 60, 60, 60, 0 });
	    region.add(circle(20, 220, 100));
	    region.add(circle(20, 20, 100));
	    region.add(circle(20, 220, 20));
	    region.add(circle(20, 20, 20));
//	    region.subtract(circle(90, 100, 100));
	    region.add(new int[] { 20, 0, 20, 120, 220, 120, 220, 0 });
	    region.add(new int[] { 0, 20, 0, 100, 240, 100, 240, 20 });

//	    region.subtract(circle(20, 67, 50));
//	    region.subtract(new int[] { 67, 50, 55, 105, 79, 105 });
	    // define the shape of the shell using setRegion
	    shell.setRegion(region);
	    
	    Rectangle size = region.getBounds();
	    shell.setSize(size.width, size.height);
	    
	    // add ability to move shell around
	    Listener l = new Listener() {
	      Point origin;

	      public void handleEvent(Event e) {
	        switch (e.type) {
	        case SWT.MouseDown:
	          origin = new Point(e.x, e.y);
	          break;
	        case SWT.MouseUp:
	          origin = null;
	          break;
	        case SWT.MouseMove:
	          if (origin != null) {
	            Point p = shell.getDisplay().map(shell, null, e.x, e.y);
	            shell.setLocation(p.x - origin.x, p.y - origin.y);
	          }
	          break;
	        }
	      }
	    };
	    shell.addListener(SWT.MouseDown, l);
	    shell.addListener(SWT.MouseUp, l);
	    shell.addListener(SWT.MouseMove, l);
	    
	    shell.setLayout(new FormLayout());
	    
	    Label label = new Label(shell, SWT.NONE);
	    label.setBackground(shell.getBackground());
	    label.setText(text);
	    
	    FormData data = new FormData();
	    data.top = new FormAttachment(0, 20);
	    data.left = new FormAttachment(0, 20);
	    label.setLayoutData(data);
	    label.pack();
	    
	    Label labelClosePopup = new Label(shell, SWT.WRAP);
	    labelClosePopup.setBackground(shell.getBackground());
	    labelClosePopup.setImage(ImageCache.getImage("icons/closePopup.png"));
	    labelClosePopup.setToolTipText("Закрыть всплывающее окно");
	    labelClosePopup.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				shell.close();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    data = new FormData();
	    data.top = new FormAttachment(0, 10);
	    data.right = new FormAttachment(100, -10);
	    labelClosePopup.setLayoutData(data);
	    labelClosePopup.pack();
	    
	    shell.setLocation(parent.getShell().toDisplay(0, 0).x + parent.getShell().getBounds().width - 260, 
	    		parent.getShell().toDisplay(0, 0).y + parent.getShell().getBounds().height - 240 + 80);
	    shell.setAlpha(0);
	    
	    new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					TimeUnit.MILLISECONDS.sleep(duration);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if(shell.isDisposed()) {
						return;
					}
					shell.getDisplay().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							instance.close();
						}
					});
				}
			}
		}).start();
	}
	
	//http://www.vogella.com/blog/2010/03/16/transparent-shell/
	public static Popup make(Composite parent, String text, int duration, int SWT_COLOR) {
		Popup popup = new Popup(parent, text, duration, SWT_COLOR);
		return popup;
	}
	
	public void show() {
		shell.open();
		shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				while (shell.getAlpha() < 255) {
			        int alphaValue = shell.getAlpha() + 8;
			        int newAlpha = alphaValue < 255 ? alphaValue : 255;
			        shell.setAlpha(newAlpha);

			        if (shell.getAlpha() == 0) {
			            break; 
			        }
			        try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        shell.update();
			    }
			}
		});
	}
	
	private void close() {
		shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				while (shell.getAlpha() > 0) {
			        int alphaValue = shell.getAlpha() - 8;
			        int newAlpha = alphaValue > 0 ? alphaValue : 0;
			        shell.setAlpha(newAlpha);

			        if (shell.getAlpha() == 255){
			            break; 
			        }
			        try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        shell.update();
			    }
				
				shell.close();
			}
		});
	}
}
