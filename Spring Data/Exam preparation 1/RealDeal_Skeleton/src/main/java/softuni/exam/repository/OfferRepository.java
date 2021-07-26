package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    
}
