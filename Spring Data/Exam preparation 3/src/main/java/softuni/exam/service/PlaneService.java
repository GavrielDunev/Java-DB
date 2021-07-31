package softuni.exam.service;

import softuni.exam.models.entity.Plane;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface PlaneService {

    boolean areImported();

    String readPlanesFileContent() throws IOException;
	
	String importPlanes() throws JAXBException;

	Plane findByRegisterNumber(String registerNumber);
}
