package com.example.springdataintro.service;


import com.example.springdataintro.model.entity.Author;

import java.io.IOException;
import java.util.List;

public interface AuthorService {
    void seedAuthors() throws IOException;

    Author getRandomAuthor();

    List<String> getAuthorsOrderedByCountOfBooks();
}