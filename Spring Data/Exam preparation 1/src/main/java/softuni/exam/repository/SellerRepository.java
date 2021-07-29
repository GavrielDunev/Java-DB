package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {

}
