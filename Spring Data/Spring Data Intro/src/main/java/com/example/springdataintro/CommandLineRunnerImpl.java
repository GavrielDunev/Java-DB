package com.example.springdataintro;

import com.example.springdataintro.model.entity.Book;
import com.example.springdataintro.service.AuthorService;
import com.example.springdataintro.service.BookService;
import com.example.springdataintro.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;

    public CommandLineRunnerImpl(CategoryService categoryService, AuthorService authorService, BookService bookService) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) throws Exception {
        seedDatabase();
        printBookTitlesReleasedAfter(2000);
        printAuthorsWithBooksReleasedBefore(1990);
        printAuthorsOrderByNumberOfBooks();
        printBooksByAuthorOrderedByReleaseDateAndBookTitle("George", "Powell");
    }

    private void printBooksByAuthorOrderedByReleaseDateAndBookTitle(String firstName, String lastName) {
        this.bookService.findAllByAuthorFirstNameAndLastNameOrderedByReleaseDateAndBookTitle(firstName, lastName)
                .forEach(System.out::println);
    }

    private void printAuthorsOrderByNumberOfBooks() {
        this.authorService.getAuthorsOrderedByCountOfBooks()
                .forEach(System.out::println);
    }

    private void printAuthorsWithBooksReleasedBefore(int year) {
        this.bookService.findAllAuthorsWithBooksReleasedBefore(year)
                .forEach(System.out::println);
    }

    private void printBookTitlesReleasedAfter(int year) {
        this.bookService.findAllBooksByReleaseDateAfter(year)
                .stream()
                .map(Book::getTitle)
                .forEach(System.out::println);
    }

    private void seedDatabase() throws IOException {
        this.categoryService.seedCategories();
        this.authorService.seedAuthors();
        this.bookService.seedBooks();
    }

}
