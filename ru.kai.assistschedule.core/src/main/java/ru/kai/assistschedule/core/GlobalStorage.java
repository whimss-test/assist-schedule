package ru.kai.assistschedule.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.SAXException;

import ru.kai.assistschedule.core.cache.LectureRoom;
import ru.kai.assistschedule.core.cache.LessonType;
import ru.kai.assistschedule.core.xml.DOMSerializer;

/**
 * Класс для сохранения параметров и настроек приложения в файл
 * 
 * @author Дамир
 * 
 */
public class GlobalStorage {

    /**
     * Поля класса определяют, что будет HashMap и что данный класс будет типа
     * SINGLETONE
     */
	private HashMap<String, Object> fHashMap;
	private static GlobalStorage SINGLETON;
	public static String selectedSchedule, selectedProffsLoad;
	public static String[][] matrix;
	public static Date beginingOfSemestr, endOfSemestr;

	public static List<LectureRoom> lectureRooms = new ArrayList<LectureRoom>();
	
	public static List<LectureRoom> getLectureRooms() {
		return lectureRooms;
	}
	
	public static void addLectureRoom(LectureRoom room) {
		lectureRooms.add(room);
		SINGLETON.fHashMap.put("autoFillAu", serializeLectureRooms(lectureRooms));
	}

	public static void removeLectureRoom(LectureRoom room) {
		lectureRooms.remove(room);
		SINGLETON.fHashMap.put("autoFillAu", serializeLectureRooms(lectureRooms));
	}
	
    /**
     * Закрытый конструктор, который создает HashMap при первом вызове в блоке
     * статической инициализации
     */
	private GlobalStorage() {
		fHashMap = new HashMap<String, Object>();
	}

    /**
     * Метод возвращения объектов по ключу
     *
     * @param key
     *            - ключ
     * @return соответствующий объект
     */
	public static Object get(String key) {
		return SINGLETON.fHashMap.get(key);
	}

    /**
     * Возвращает объект по ключу, либо объект по умолчанию, если при таком
     * ключе объект не существуют
     *
     * @param key
     *            - ключ
     * @param deflt
     *            - объект по умолчанию
     * @return объект, если он найден при данном ключе, либо объект по
     *         умолчанию, кот был передан в конструкторе
     */
	public static Object get(String key, Object deflt) {
		Object obj = SINGLETON.fHashMap.get(key);
		if (obj == null)
			return deflt;
		else
			return obj;
	}

    /**
     * Достаёт объекты Integer, либо число по умолчанию, если при таком ключе
     * объект не существуют
     *
     * @param key
     *            - ключ
     * @param deflt
     *            - значение по умолчанию
     * @return Integer
     */
	public static int getInt(String key, int deflt) {
		Object obj = SINGLETON.fHashMap.get(key);
		if (obj == null)
			return deflt;
		else
			return new Integer((String) obj).intValue();
	}

    /**
     * Сохраняет все параметры из HashTab в файл
     *
     * @param file
     *            - куда сохраняем
     * @return true если успешно выполнено
     * @throws IOException
     *             - при неудачной сеиализации в файл
     */
	public static boolean save(File file) throws IOException {
        // Создание нового дерева DOM
		// get an instance of the DOMImplementation registry
		  DOMImplementationRegistry registry;
		  DOMImplementation domImpl;
		try {
			registry = DOMImplementationRegistry.newInstance();
			// get a DOM implementation the Level 3 XML module
			domImpl = registry.getDOMImplementation("XML 3.0");
			Document doc = domImpl.createDocument(null, "ScheduleHelper-settings", null);
			Element root = doc.getDocumentElement();
			Element propertiesElement = doc.createElement("properties");
			root.appendChild(propertiesElement);
			Set<String> set = SINGLETON.fHashMap.keySet();
			if (set != null) {
				for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
	                // Создаём элемент
					Element propertyElement = doc.createElement("property");
	                // Создаём параметр
					String key = iterator.next().toString();
					propertyElement.setAttribute("key", key);
	                // Записываем само значение
					Text nameText = doc.createTextNode(get(key).toString());
	                // Добавляем в propertY
					propertyElement.appendChild((Node) nameText);
	                // Добавляем в propertIES
					propertiesElement.appendChild(propertyElement);
				}
			}
	        // Сериализация DOM дерева в файл
			DOMSerializer serializer = new DOMSerializer();
			serializer.serialize(doc, file);
			return true;
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		return false;
	}

    /**
     * Очищает HashTab
     */
	public static void clear() {
		SINGLETON.fHashMap.clear();
	}

    /**
     * Запись в HashTab(Защищена! Выбрасывает IllegalArgumentException)
     *
     * @param key
     *            - (String) ключ
     * @param data
     *            - (String) значение
     */
	public static void put(String key, Object data) throws IllegalArgumentException {
        // Защита от записи NULL элементов в значение
		if (data == null)
			throw new IllegalArgumentException();
		else
			SINGLETON.fHashMap.put(key, data);
		
		if("autoFillAu".equals(key)) {
			fillLectureRooms(String.valueOf(data));
		}
	}
	
	private static String serializeLectureRooms(List<LectureRoom> rooms) {
		StringBuilder builder = new StringBuilder();
		for(LectureRoom room: rooms) {
			builder.append(room.toString());
			builder.append("#");
		}
		return (builder.length() > 0) ? builder.substring(0, builder.length() - 1) : "";
	}

    private static void fillLectureRooms(String data) {
    	String arr[] = data.split("#");
    	LectureRoom room;
    	List<LessonType> lessonTypes;
    	for(String s: arr) {
    		String tokens[] = s.split(" ");
    		room = new LectureRoom();
    		lessonTypes = new ArrayList<LessonType>();
    		for(int i = 0; i < tokens.length; i++) {
    			if(0 == i) {
    				room.setName(tokens[i]);
    			} else {
    				if("лек".equals(tokens[i])) {
    					lessonTypes.add(LessonType.LEC);
    				} else if("пр".equals(tokens[i])) {
    					lessonTypes.add(LessonType.PRAC);
    				} else if("л.р.".equals(tokens[i])) {
    					lessonTypes.add(LessonType.LABS);
    				} else if("и.з.".equals(tokens[i])) {
    					lessonTypes.add(LessonType.IZ);
    				} else if("".equals(tokens[i])) {
    					lessonTypes.add(LessonType.OTHER);
    				}
    			}
    		}
    		room.setLessonTypes(lessonTypes);
    		lectureRooms.add(room);
    	}
	}

	/**
     * Заполняет HashTab из файла
     *
     * @param file
     *            - файл
     * @return true, если успешно и false в противном случае
     * @throws ParserConfigurationException
     *             - связан с открытием файла
     * @throws IOException
     *             - при парсинге
     * @throws SAXException
     *             - при парсинге
     */
	public static boolean load(File file) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		if (doc == null)
			throw new NullPointerException();
		NodeList propertiesNL = doc.getDocumentElement().getChildNodes();
		if (propertiesNL != null) {
			for (int i = 0; (i < propertiesNL.getLength()); i++) {
				if (propertiesNL.item(i).getNodeName().equals("properties")) {
					NodeList propertyList = propertiesNL.item(i).getChildNodes();
					for (int j = 0; j < propertyList.getLength(); j++) {
						NamedNodeMap attributes = propertyList.item(j).getAttributes();
						if (attributes != null) {
							Node n = attributes.getNamedItem("key");
							NodeList childs = propertyList.item(j).getChildNodes();
							if (childs != null) {
								for (int k = 0; k < childs.getLength(); k++) {
									if (childs.item(k).getNodeType() == Node.TEXT_NODE) {
										put(n.getNodeValue(), childs.item(k).getNodeValue());
									}
								}
							}
						}
					}
				}
			}
			return true;
		} else
			return false;
	}

    /**
     * Удаляет запись по данному значению ключа
     *
     * @param key
     */
	public static void del(String key) {
		SINGLETON.fHashMap.remove(key);
	}

    /**
     * Статический блок инициализации
     */
	static {
		SINGLETON = new GlobalStorage();
	}
}