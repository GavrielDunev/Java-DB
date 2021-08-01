package com.example.football.repository;

import com.example.football.models.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByEmail(String email);

    @Query("select p from Player p where p.birthDate > ?1 and p.birthDate < ?2 " +
            "order by p.stat.shooting DESC, p.stat.passing DESC, p.stat.endurance DESC, p.lastName")
    List<Player> findAllByBirthDateAfterAndBirthDateBeforeOrderByShootingPassingEnduranceLastName(LocalDate after, LocalDate before);
}
