package ru.kai.assistschedule.core.cache.load;

public class LoadEntry {
	
	/************************/
	/***** ПОЛЯ КЛАССА ******/
	/************************/
	public int id;
	
	public String semestr;
	
	public String discipline;
	
	public String educationForm;
	//Специальность и группа в формате "(230100)4111"
	public String spec_group;
	
	public int groupCount;
	
	public int subGroupCount;
	
	public int weekCount;
	
	public FormOfClass lec = null;
	
	public FormOfClass prac = null;
	
	public FormOfClass labs = null;

	public LoadEntry(int anId, String aSemestr, String aDiscipline, String anEducationForm,
			String aSpec_group, int aGrCount, int aSubGrCount, int aWeekCount,
			FormOfClass aLec, FormOfClass aPrac, FormOfClass aLabs){
		this.id = anId;
		this.semestr = aSemestr;
		this.discipline = aDiscipline;
		this.educationForm = anEducationForm;
		this.spec_group = aSpec_group;
		this.groupCount = aGrCount;
		this.subGroupCount = aSubGrCount;
		this.weekCount = aWeekCount;
		this.lec = aLec;
		this.prac = aPrac;
		this.labs = aLabs;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((discipline == null) ? 0 : discipline.hashCode());
		result = prime * result
				+ ((educationForm == null) ? 0 : educationForm.hashCode());
		result = prime * result + groupCount;
		result = prime * result + id;
		result = prime * result + ((labs == null) ? 0 : labs.hashCode());
		result = prime * result + ((lec == null) ? 0 : lec.hashCode());
		result = prime * result + ((prac == null) ? 0 : prac.hashCode());
		result = prime * result + ((semestr == null) ? 0 : semestr.hashCode());
		result = prime * result
				+ ((spec_group == null) ? 0 : spec_group.hashCode());
		result = prime * result + subGroupCount;
		result = prime * result + weekCount;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoadEntry other = (LoadEntry) obj;
		if (discipline == null) {
			if (other.discipline != null)
				return false;
		} else if (!discipline.equals(other.discipline))
			return false;
		if (educationForm == null) {
			if (other.educationForm != null)
				return false;
		} else if (!educationForm.equals(other.educationForm))
			return false;
		if (groupCount != other.groupCount)
			return false;
		if (id != other.id)
			return false;
		if (labs == null) {
			if (other.labs != null)
				return false;
		} else if (!labs.equals(other.labs))
			return false;
		if (lec == null) {
			if (other.lec != null)
				return false;
		} else if (!lec.equals(other.lec))
			return false;
		if (prac == null) {
			if (other.prac != null)
				return false;
		} else if (!prac.equals(other.prac))
			return false;
		if (semestr == null) {
			if (other.semestr != null)
				return false;
		} else if (!semestr.equals(other.semestr))
			return false;
		if (spec_group == null) {
			if (other.spec_group != null)
				return false;
		} else if (!spec_group.equals(other.spec_group))
			return false;
		if (subGroupCount != other.subGroupCount)
			return false;
		if (weekCount != other.weekCount)
			return false;
		return true;
	}

}
