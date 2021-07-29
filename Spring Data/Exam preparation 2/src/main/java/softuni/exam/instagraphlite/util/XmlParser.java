package softuni.exam.instagraphlite.util;

import javax.xml.bind.JAXBException;

public interface XmlParser {

    <T> T readFromFile(String path, Class<T> tClass) throws JAXBException;
}
