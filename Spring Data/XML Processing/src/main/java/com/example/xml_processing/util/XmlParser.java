package com.example.xml_processing.util;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface XmlParser {
    <T> T fromFile(String filePath, Class<T> tClass) throws JAXBException, FileNotFoundException;

    <T> void writeToFile(String filePath, T entity) throws JAXBException, IOException;
}
