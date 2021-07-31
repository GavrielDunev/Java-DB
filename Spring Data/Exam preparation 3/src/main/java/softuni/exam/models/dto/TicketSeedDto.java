package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class TicketSeedDto {

    @XmlElement(name = "serial-number")
    private String serialNumber;
    @XmlElement
    private BigDecimal price;
    @XmlElement(name = "take-off")
    private String takeoff;
    @XmlElement(name = "from-town")
    private TownNameDto fromTown;
    @XmlElement(name = "to-town")
    private TownNameDto toTown;
    @XmlElement
    private PassengerEmailDto passenger;
    @XmlElement
    private PlaneRegisterNumberDto plane;

    public TicketSeedDto() {
    }

    @Size(min = 2)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Positive
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTakeoff() {
        return takeoff;
    }

    public void setTakeoff(String takeoff) {
        this.takeoff = takeoff;
    }

    @NotNull
    public TownNameDto getFromTown() {
        return fromTown;
    }

    public void setFromTown(TownNameDto fromTown) {
        this.fromTown = fromTown;
    }

    @NotNull
    public TownNameDto getToTown() {
        return toTown;
    }

    public void setToTown(TownNameDto toTown) {
        this.toTown = toTown;
    }

    @NotNull
    public PassengerEmailDto getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerEmailDto passenger) {
        this.passenger = passenger;
    }

    @NotNull
    public PlaneRegisterNumberDto getPlane() {
        return plane;
    }

    public void setPlane(PlaneRegisterNumberDto plane) {
        this.plane = plane;
    }
}
