package com.example.springintro;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;
import com.example.springintro.model.entity.EditionType;
import com.example.springintro.service.AuthorService;
import com.example.springintro.service.BookService;
import com.example.springintro.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final BufferedReader bufferedReader;

    public CommandLineRunnerImpl(CategoryService categoryService, AuthorService authorService, BookService bookService, BufferedReader bufferedReader) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();

        //printAllBooksAfterYear(2000);
        //printAllAuthorsNamesWithBooksWithReleaseDateBeforeYear(1990);
        //printAllAuthorsAndNumberOfTheirBooks();
        //printALlBooksByAuthorNameOrderByReleaseDate("George", "Powell");

        System.out.println("Enter number of exercise:");
        int ex = Integer.parseInt(bufferedReader.readLine());

        switch (ex) {
            case 1 -> bookTitlesByAgeRestriction();
            case 2 -> goldenBooks();
            case 3 -> booksByPrice();
            case 4 -> notReleasedBooks();
            case 5 -> booksReleasedBeforeDate();
            case 6 -> authorsSearch();
            case 7 -> booksSearch();
            case 8 -> bookTitlesSearch();
            case 9 -> countBooks();
            case 10 -> totalBookCopies();
            case 11 -> reducedBook();
            case 12 -> increaseBookCopies();
            case 13 -> removeBooks();
            case 14 -> storedProcedure();
        }
    }

    private void storedProcedure() throws IOException {
        System.out.println("Enter author's full name:");
        String[] fullName = bufferedReader.readLine().split("\\s+");
        String firstName = fullName[0];
        String lastName = fullName[1];
        int numberOfBooks = this.bookService.findNumberOfBooksByAuthor(firstName, lastName);
        System.out.printf("%s %s has written %d books%n", firstName, lastName, numberOfBooks);
    }

    private void removeBooks() throws IOException {
        System.out.println("Enter number of copies:");
        int number = Integer.parseInt(bufferedReader.readLine());
        System.out.println(this.bookService.removeBooksByCopiesLowerThan(number));
    }

    private void increaseBookCopies() throws IOException {
        System.out.println("Enter date:");
        DateTimeFormatter df = new DateTimeFormatterBuilder()
                .appendPattern("dd MMM yyyy")
                .toFormatter(Locale.ENGLISH);
        LocalDate date = LocalDate.parse(bufferedReader.readLine(), df);
        System.out.println("Enter number:");
        int number = Integer.parseInt(bufferedReader.readLine());
        int copies = this.bookService.increaseCopiesOfBooksAfterReleaseDateWithNumber(date, number);
        System.out.println(copies);
    }

    private void reducedBook() throws IOException {
        System.out.println("Enter title:");
        String title = bufferedReader.readLine();
        this.bookService.findAllByTitle(title)
                .forEach(System.out::println);
    }

    private void totalBookCopies() {
        this.authorService.findBookCopiesByAuthor()
                .forEach(System.out::println);
    }

    private void countBooks() throws IOException {
        System.out.println("Enter title length:");
        int length = Integer.parseInt(bufferedReader.readLine());
        int count = this.bookService.findCountOfBooksWithTitleLongerThan(length);
        System.out.println(count);
    }

    private void bookTitlesSearch() throws IOException {
        System.out.println("Enter patten:");
        String pattern = bufferedReader.readLine();
        this.bookService.findAllByAuthorLastNameStartsWith(pattern).
                forEach(System.out::println);
    }

    private void booksSearch() throws IOException {
        System.out.println("Enter patten:");
        String pattern = bufferedReader.readLine();
        this.bookService.findBooksWithTitlesContaining(pattern)
                .forEach(System.out::println);
    }

    private void authorsSearch() throws IOException {
        System.out.println("Enter pattern:");
        String pattern = bufferedReader.readLine();
        this.authorService.findAuthorsWithFirstNameEndsWith(pattern)
                .forEach(System.out::println);
    }

    private void booksReleasedBeforeDate() throws IOException {
        System.out.println("Enter release date:");
        String releaseDate = bufferedReader.readLine();
        this.bookService.findBooksReleasedBefore(releaseDate)
                .forEach(System.out::println);

    }

    private void notReleasedBooks() throws IOException {
        System.out.println("Enter year:");
        int year = Integer.parseInt(bufferedReader.readLine());
        this.bookService.findBookTitlesNotReleasedInGivenYear(year)
                .forEach(System.out::println);
    }

    private void booksByPrice() {
        this.bookService.findBookTitlesWithPriceLessThanAndGreaterThan(BigDecimal.valueOf(5), BigDecimal.valueOf(40))
                .forEach(System.out::println);
    }

    private void goldenBooks() {
        this.bookService.findBookTitlesByEditionTypeAndNumberOfCopies(EditionType.GOLD, 5000)
                .forEach(System.out::println);
    }

    private void bookTitlesByAgeRestriction() throws IOException {
        System.out.println("Enter age restriction (Minor, Teen or Adult).");
        String restriction = bufferedReader.readLine().toUpperCase();
        AgeRestriction ageRestriction = AgeRestriction.valueOf(restriction);
        List<String> titles = this.bookService.findBookTitlesByAgeRestriction(ageRestriction);
        titles.forEach(System.out::println);
    }

    private void printALlBooksByAuthorNameOrderByReleaseDate(String firstName, String lastName) {
        bookService
                .findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(firstName, lastName)
                .forEach(System.out::println);
    }

    private void printAllAuthorsAndNumberOfTheirBooks() {
        authorService
                .getAllAuthorsOrderByCountOfTheirBooks()
                .forEach(System.out::println);
    }

    private void printAllAuthorsNamesWithBooksWithReleaseDateBeforeYear(int year) {
        bookService
                .findAllAuthorsWithBooksWithReleaseDateBeforeYear(year)
                .forEach(System.out::println);
    }

    private void printAllBooksAfterYear(int year) {
        bookService
                .findAllBooksAfterYear(year)
                .stream()
                .map(Book::getTitle)
                .forEach(System.out::println);
    }

    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}
