package ru.kai.assistschedule.core.cache.load;

public class FormOfClass {
	
	public FormOfClass(float aHoursInWeek, int weekCount, String aProfessor){
		hoursInWeek = aHoursInWeek;
		professor = aProfessor;
		totalHours = weekCount*hoursInWeek;
	}
	
	public float hoursInWeek;
	public float totalHours;
	public String professor;
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(hoursInWeek);
		result = prime * result
				+ ((professor == null) ? 0 : professor.hashCode());
		result = prime * result + Float.floatToIntBits(totalHours);
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
		if (Float.floatToIntBits(hoursInWeek) != Float
				.floatToIntBits(other.hoursInWeek))
			return false;
		if (professor == null) {
			if (other.professor != null)
				return false;
		} else if (!professor.equals(other.professor))
			return false;
		if (Float.floatToIntBits(totalHours) != Float
				.floatToIntBits(other.totalHours))
			return false;
		return true;
	}
}
