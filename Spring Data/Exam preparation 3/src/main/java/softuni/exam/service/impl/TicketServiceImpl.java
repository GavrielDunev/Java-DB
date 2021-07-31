package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TicketSeedRootDto;
import softuni.exam.models.entity.Ticket;
import softuni.exam.repository.TicketRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.PlaneService;
import softuni.exam.service.TicketService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TicketServiceImpl implements TicketService {

    private static final String TICKETS_FILE_PATH = "src/main/resources/files/xml/tickets.xml";

    private final TicketRepository ticketRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final TownService townService;
    private final PassengerService passengerService;
    private final PlaneService planeService;

    public TicketServiceImpl(TicketRepository ticketRepository, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil, TownService townService, PassengerService passengerService, PlaneService planeService) {
        this.ticketRepository = ticketRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.townService = townService;
        this.passengerService = passengerService;
        this.planeService = planeService;
    }

    @Override
    public boolean areImported() {
        return this.ticketRepository.count() > 0;
    }

    @Override
    public String readTicketsFileContent() throws IOException {
        return Files.readString(Path.of(TICKETS_FILE_PATH));
    }

    @Override
    public String importTickets() throws JAXBException {
        TicketSeedRootDto ticketSeedRootDto = xmlParser.fromFile(TICKETS_FILE_PATH, TicketSeedRootDto.class);
        StringBuilder sb = new StringBuilder();

        ticketSeedRootDto.getTickets()
                .stream()
                .filter(ticketSeedDto -> {
                    boolean isValid = this.validationUtil.isValid(ticketSeedDto);

                    sb.append(isValid ? String.format("Successfully imported Ticket %s - %s",
                            ticketSeedDto.getFromTown().getName(), ticketSeedDto.getToTown().getName())
                            : "Invalid Ticket")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(ticketSeedDto -> {
                    Ticket ticket = modelMapper.map(ticketSeedDto, Ticket.class);

                    ticket.setFromTown(this.townService.findByName(ticketSeedDto.getFromTown().getName()));
                    ticket.setToTown(this.townService.findByName(ticketSeedDto.getToTown().getName()));
                    ticket.setPassenger(this.passengerService.findByEmail(ticketSeedDto.getPassenger().getEmail()));
                    ticket.setPlane(this.planeService.findByRegisterNumber(ticketSeedDto.getPlane().getRegisterNumber()));

                    return ticket;
                })
                .forEach(this.ticketRepository::save);

        return sb.toString();
    }
}
