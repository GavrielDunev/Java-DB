package com.example.springintro.repository;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;
import com.example.springintro.model.entity.EditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByReleaseDateAfter(LocalDate releaseDateAfter);

    List<Book> findAllByReleaseDateBefore(LocalDate releaseDateBefore);

    List<Book> findAllByAuthor_FirstNameAndAuthor_LastNameOrderByReleaseDateDescTitle(String author_firstName, String author_lastName);

    List<Book> findAllByAgeRestriction(AgeRestriction ageRestriction);

    List<Book> findAllByEditionTypeAndCopiesLessThan(EditionType editionType, Integer copies);

    List<Book> findAllByPriceLessThanOrPriceGreaterThan(BigDecimal lower, BigDecimal upper);

    List<Book> findAllByReleaseDateBeforeOrReleaseDateAfter(LocalDate before, LocalDate after);

    List<Book> findAllByTitleContaining(String pattern);

    List<Book> findAllByAuthorLastNameStartsWith(String str);

    @Query("SELECT COUNT(b) FROM Book b WHERE LENGTH(b.title) > :number")
    int countAllByTitleLengthGreaterThan(@Param("number") int number);

    List<Book> findAllByTitle(String title);

    @Modifying
    @Query("UPDATE Book b SET b.copies = b.copies + :num WHERE b.releaseDate > :date")
    int updateAllByReleaseDateAfterWithNumber(@Param("date") LocalDate releaseDate,
                                              @Param("num") int number);

    @Modifying
    int removeAllByCopiesIsLessThan(int number);

    @Query(value = "CALL amount_of_books_by_author(:first_name, :last_name);", nativeQuery = true)
    int countBooksByGivenAuthor(@Param("first_name") String firstName,
                                @Param("last_name") String lastName);
}