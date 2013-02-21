package ru.kai.assistschedule.core.cache.load;

public class FormOfClass {
	
	public FormOfClass(int aHoursInWeek, int weekCount, String aProfessor){
		hoursInWeek = aHoursInWeek;
		professor = aProfessor;
		totalHours = weekCount*hoursInWeek;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hoursInWeek;
		result = prime * result
				+ ((professor == null) ? 0 : professor.hashCode());
		result = prime * result + totalHours;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormOfClass other = (FormOfClass) obj;
		if (hoursInWeek != other.hoursInWeek)
			return false;
		if (professor == null) {
			if (other.professor != null)
				return false;
		} else if (!professor.equals(other.professor))
			return false;
		if (totalHours != other.totalHours)
			return false;
		return true;
	}

	public int hoursInWeek;
	public int totalHours;
	public String professor;
}
