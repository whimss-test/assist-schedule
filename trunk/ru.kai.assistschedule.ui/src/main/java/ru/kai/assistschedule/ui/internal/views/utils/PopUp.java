package ru.kai.assistschedule.ui.internal.views.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class PopUp {

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
	
	static int[] roundRectangle(int x, int y) {
		int[] polygon = new int[8 * x + 4];
////		PaintEvent event
		return polygon;
	}

	public PopUp(Composite parent) {
		final Shell shell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		shell.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
	    // define a region that looks like a key hole
	    Region region = new Region();
//	    region.add(new int[] { 0, 0, 0, 60, 60, 60, 60, 0 });
	    region.add(circle(20, 220, 180));
	    region.add(circle(20, 20, 180));
	    region.add(circle(20, 220, 20));
	    region.add(circle(20, 20, 20));
//	    region.subtract(circle(90, 100, 100));
	    region.add(new int[] { 20, 0, 20, 200, 220, 200, 220, 0 });
	    region.add(new int[] { 0, 20, 0, 180, 240, 180, 240, 20 });

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
	    // add ability to close shell
	    Button b = new Button(shell, SWT.PUSH);
	    b.setBackground(shell.getBackground());
	    b.setText("close");
	    b.pack();
	    b.setLocation(10, 68);
	    b.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event e) {
	        shell.close();
	      }
	    });
	    
	    Label label = new Label(shell, SWT.NONE);
	    label.setBackground(shell.getBackground());
	    label.setText("This is pop up=)");
	    label.pack();
	    
	    shell.setLocation(parent.toDisplay(0, 0).x + parent.getBounds().width - 240, parent.toDisplay(0, 0).y + parent.getBounds().height - 200);
	    shell.setAlpha(shell.getAlpha() - 150);
	    shell.open();
	}
	
	//http://www.vogella.com/blog/2010/03/16/transparent-shell/

}
