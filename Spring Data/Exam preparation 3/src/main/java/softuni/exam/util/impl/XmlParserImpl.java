package softuni.exam.util.impl;

import org.springframework.stereotype.Component;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@Component
public class XmlParserImpl implements XmlParser {

    @Override
    @SuppressWarnings("unchecked")
    public <E> E fromFile(String path, Class<E> eClass) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(eClass);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (E) unmarshaller.unmarshal(new File(path));
    }
}