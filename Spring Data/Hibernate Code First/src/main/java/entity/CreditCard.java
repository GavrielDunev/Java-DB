package entity;

import javax.persistence.*;

@Entity
@Table(name = "credit_card")
public class CreditCard extends BillingDetail{
    private CardType type;
    private Integer expirationMonth;
    private Integer expirationYear;

    public CreditCard() {
    }

    public CreditCard(CardType type, Integer expirationMonth, Integer expirationYear) {
        this.type = type;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
    }

    @Enumerated(EnumType.STRING)
    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    @Column(name = "expiration_mongth")
    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    @Column(name = "expiration_year")
    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }
}
