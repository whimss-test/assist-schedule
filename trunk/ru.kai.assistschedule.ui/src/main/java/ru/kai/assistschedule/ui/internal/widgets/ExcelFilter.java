package ru.kai.assistschedule.ui.internal.widgets;

import org.eclipse.nebula.cwt.base.BaseCombo;
import org.eclipse.nebula.cwt.v.VCanvas;
import org.eclipse.nebula.cwt.v.VGridLayout;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ExcelFilter extends BaseCombo {

	VPanel filterPanel;
	
	public ExcelFilter(Composite parent, int style) {
		super(parent, style);
		setOpen(false);
	}

	@Override
	protected boolean setContentFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	private void createFilter() {
		Shell shell = getContentShell();
		int style = SWT.BORDER | SWT.DOUBLE_BUFFERED;
		VCanvas canvas = new VCanvas(shell, style);
	    filterPanel = canvas.getPanel();
	    filterPanel.setWidget(canvas);
	    VGridLayout layout = new VGridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 1;
		filterPanel.setLayout(layout);
		setContent(filterPanel.getComposite());
	}

	@Override
	protected void setOpen(boolean open) {
		// TODO Auto-generated method stub
		super.setOpen(open);
		createFilter();
	}
	
	
}
