package com.example.demo5.service;

import java.util.List;
import com.example.demo5.entity.Book;
public interface BookService {

    List<Book> listBooks();

    Book getBook(Long id);

    String getBookImage(Long id);

    Book createBook(String authUsername, BookCommand command);

    Book updateBook(String authUsername, Long id, BookCommand command);

    void deleteBook(String authUsername, Long id);

    public record BookCommand(String title, String author, String category, Integer stock, Integer recommendationIndex,
                              String image, String status) {
    }
}
