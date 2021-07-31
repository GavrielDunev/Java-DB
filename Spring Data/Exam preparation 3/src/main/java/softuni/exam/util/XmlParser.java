package softuni.exam.util;

import javax.xml.bind.JAXBException;

public interface XmlParser {

    <E> E fromFile(String path, Class<E> eClass) throws JAXBException;
}
