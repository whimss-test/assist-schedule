package ru.kai.assistschedule.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.kai.assistschedule.core.cache.DaysOfWeek;
import ru.kai.assistschedule.core.cache.FirstLevelCache;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.cache.ScheduleEntry;
import ru.kai.assistschedule.core.cache.Time;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtils {

	public static WritableWorkbook writableWorkbook;
    
    public static WritableSheet writableSheet;
    
	public static boolean openSchedule(String inputName) {
		Workbook existingWorkbook = null;
		try {
        	File prevFile = new File(inputName);
        	existingWorkbook = Workbook.getWorkbook(prevFile);
        	String prevFileAbsolutePath = prevFile.getAbsolutePath();
        	writableWorkbook = Workbook.createWorkbook(
        		new File(prevFileAbsolutePath
        			.substring(0, prevFileAbsolutePath.length()-4) + "_NEW.xls")
        			, existingWorkbook);
            return true;
        } catch (BiffException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
        	if(null != existingWorkbook) {
        		existingWorkbook.close();
        	}
        }
    }
	
	public static void getSheet(int num) {
		writableSheet = writableWorkbook.getSheet(num);
	}
	
	public static void getSheet(String sheetName) {
		writableSheet = writableWorkbook.getSheet(sheetName);
	}
	
	public static void clearSheet(String pathToWorkbook, int sheetNum) {
		try {
			if(!openSchedule(pathToWorkbook)) {
				throw new IllegalArgumentException("Не возможно открыть файл : " + pathToWorkbook);
			}
			getSheet(sheetNum);
			int amountOfRows = writableSheet.getRows();
			int amountOfColumns = writableSheet.getColumns();
			
//			writableSheet.removeRow(66);
			while(1 < amountOfRows) {
				for(int i = 0; i < amountOfColumns; i++) {
					try {
						writableSheet.addCell(new Label(i, amountOfRows, ""));
					} catch (RowsExceededException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WriteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				writableSheet.removeRow(amountOfRows--);
//				System.out.println(amountOfRows);
				amountOfRows--;
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if(null == writableWorkbook) { return; }
				writableWorkbook.write();
				writableWorkbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void fillSheet(String pathToWorkbook, int sheetNum, List<ScheduleEntry> entries) {
		try {
			if(!openSchedule(pathToWorkbook)) {
				throw new IllegalArgumentException("Не возможно открыть файл : " + pathToWorkbook);
			}
			getSheet(sheetNum);
			ScheduleEntry entry;
			int offset = 1;
			for(int i = 1; i < entries.size(); i++) {
				entry = entries.get(i);
				writableSheet.addCell(new Label(0, i + offset, entry.groupName));
				writableSheet.addCell(new Label(1, i + offset, getDay(entry.day)));
				writableSheet.addCell(new Label(2, i + offset, getTime(entry.time)));
				writableSheet.addCell(new Label(3, i + offset, entry.date));
				writableSheet.addCell(new Label(4, i + offset, entry.discipline));
				writableSheet.addCell(new Label(5, i + offset, getLessonType(entry.lessonType)));
				writableSheet.addCell(new Label(6, i + offset, entry.classRoom));
				writableSheet.addCell(new Label(7, i + offset, entry.building));
				writableSheet.addCell(new Label(8, i + offset, entry.doljnost));
				writableSheet.addCell(new Label(9, i + offset, entry.prepodavatel));
				writableSheet.addCell(new Label(10, i + offset, entry.kafedra));
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(null == writableWorkbook) { return; }
				writableWorkbook.write();
				writableWorkbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getDay(DaysOfWeek daysOfWeek) {
		if(null == daysOfWeek) {
			return "";
		}
		switch (daysOfWeek) {
			case mon: return "пн";
			case tue: return "вт";
			case wed: return "ср";
			case thu: return "чт";
			case fri: return "пт";
			case sat: return "сб";
			default: return "";
		}
	}

	public static String getTime(Time time) {
		if(null == time) {
			return "";
		}
		switch (time) {
			case at08_00: return "8:00";
			case at09_40: return "9:40";
			case at11_30: return "11:30";
			case at13_10: return "13:10";
			case at15_00: return "15:00";
			case at16_40: return "16:40";
			case at18_15: return "18:15";
			case at19_45: return "19:45";
			default: return "";
		}
	}

	public static String getLessonType(LessonType lessonType) {
		if(null == lessonType) {
			return "";
		}
		switch (lessonType) {
			case LEC: return "лек";
			case PRAC: return "пр";
			case LABS: return "л.р.";
			case IZ: return "и.з.";
			case OTHER: return "";
			default: return "";
		}
	}
}
