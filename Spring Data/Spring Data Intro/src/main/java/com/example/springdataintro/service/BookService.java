package com.example.springdataintro.service;

import com.example.springdataintro.model.entity.Book;

import java.io.IOException;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;
    List<Book> findAllBooksByReleaseDateAfter(int year);

    List<String> findAllAuthorsWithBooksReleasedBefore(int year);

    List<String> findAllByAuthorFirstNameAndLastNameOrderedByReleaseDateAndBookTitle(String firstName, String lastName);
}
