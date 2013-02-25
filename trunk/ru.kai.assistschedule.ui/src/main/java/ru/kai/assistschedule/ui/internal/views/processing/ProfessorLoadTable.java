package ru.kai.assistschedule.ui.internal.views.processing;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.core.MainCommand;
import ru.kai.assistschedule.ui.internal.views.AbstractProfessorsLoadTable;
import ru.kai.assistschedule.ui.model.professorLoad.ProfessorLoadContentProvider;
import ru.kai.assistschedule.ui.model.professorLoad.ProfessorLoadLabelProvider;

public class ProfessorLoadTable extends AbstractProfessorsLoadTable {

	protected static final Logger LOG = LoggerFactory
			.getLogger(ProfessorLoadTable.class);

	public ProfessorLoadTable(Composite parent) {
		super(parent);
		MainCommand.setProfessorLoadTableProcessing(this);
	}

	@Override
	protected void listeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new ProfessorLoadLabelProvider();
	}

	@Override
	protected IContentProvider getContentProvider() {
		return new ProfessorLoadContentProvider();
	}

}
