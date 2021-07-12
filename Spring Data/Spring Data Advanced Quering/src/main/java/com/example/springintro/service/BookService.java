package com.example.springintro.service;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;
import com.example.springintro.model.entity.EditionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);

    List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year);

    List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName);

    List<String> findBookTitlesByAgeRestriction(AgeRestriction ageRestriction);

    List<String> findBookTitlesByEditionTypeAndNumberOfCopies(EditionType editionType, int copies);

    List<String> findBookTitlesWithPriceLessThanAndGreaterThan(BigDecimal lower, BigDecimal upper);

    List<String> findBookTitlesNotReleasedInGivenYear(int year);

    List<String> findBooksReleasedBefore(String releaseDate);

    List<String> findBooksWithTitlesContaining(String pattern);

    List<String> findAllByAuthorLastNameStartsWith(String pattern);

    int findCountOfBooksWithTitleLongerThan(int length);

    List<String> findAllByTitle(String title);

    int increaseCopiesOfBooksAfterReleaseDateWithNumber(LocalDate date, int number);

    int removeBooksByCopiesLowerThan(int number);

    int findNumberOfBooksByAuthor(String firstName, String lastName);
}
