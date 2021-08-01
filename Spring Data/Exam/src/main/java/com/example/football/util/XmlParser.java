package com.example.football.util;

import javax.xml.bind.JAXBException;

public interface XmlParser {

    <T> T fromFile(String path, Class<T> tClass) throws JAXBException;
}
