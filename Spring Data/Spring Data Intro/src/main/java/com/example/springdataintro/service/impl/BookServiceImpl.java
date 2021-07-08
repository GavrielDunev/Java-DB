package com.example.springdataintro.service.impl;

import com.example.springdataintro.model.entity.*;
import com.example.springdataintro.repository.BookRepository;
import com.example.springdataintro.service.AuthorService;
import com.example.springdataintro.service.BookService;
import com.example.springdataintro.service.CategoryService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private static final String BOOKS_FILE_PATH = "src/main/resources/files/books.txt";

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    public BookServiceImpl(BookRepository bookRepository, CategoryService categoryService, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.authorService = authorService;
    }

    @Override
    public void seedBooks() throws IOException {
        if (this.bookRepository.count() > 0) {
            return;
        }

        Files.readAllLines(Path.of(BOOKS_FILE_PATH))
                .forEach(row -> {
                    String[] bookInfo = row.split("\\s+");
                    Book book = createBook(bookInfo);
                    this.bookRepository.save(book);
                });
    }

    @Override
    public List<Book> findAllBooksByReleaseDateAfter(int year) {
        return this.bookRepository.findAllByReleaseDateAfter(LocalDate.of(year, 12, 31));
    }

    @Override
    public List<String> findAllAuthorsWithBooksReleasedBefore(int year) {
        return this.bookRepository.findAllByReleaseDateBefore(LocalDate.of(year, 1, 1))
                .stream()
                .map(book -> String.format("%s %s", book.getAuthor().getFirstName(), book.getAuthor().getLastName()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllByAuthorFirstNameAndLastNameOrderedByReleaseDateAndBookTitle(String firstName, String lastName) {
        return this.bookRepository.findAllByAuthor_FirstNameAndAuthor_LastNameOrderByReleaseDateDescTitle(firstName, lastName)
                .stream()
                .map(book -> String.format("%s %s %d",
                        book.getTitle(),
                        book.getReleaseDate(),
                        book.getCopies()))
                .collect(Collectors.toList());
    }

    private Book createBook(String[] bookInfo) {
        EditionType editionType = EditionType.values()[Integer.parseInt(bookInfo[0])];
        LocalDate localDate = LocalDate.parse(bookInfo[1], DateTimeFormatter.ofPattern("d/M/yyyy"));
        Integer copies = Integer.parseInt(bookInfo[2]);
        BigDecimal price = new BigDecimal(bookInfo[3]);
        AgeRestriction ageRestriction = AgeRestriction.values()[Integer.parseInt(bookInfo[4])];
        String title = Arrays.stream(bookInfo)
                .skip(5)
                .collect(Collectors.joining(" "));
        Author author = this.authorService.getRandomAuthor();
        Set<Category> categories = this.categoryService.getRandomCategories();

        return new Book(editionType, localDate, copies, price, ageRestriction, title, author, categories);
    }
}
