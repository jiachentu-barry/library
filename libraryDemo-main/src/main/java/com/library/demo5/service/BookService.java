package com.library.demo5.service;

import java.util.List;
import com.library.demo5.entity.Book;
public interface BookService {

    List<Book> listBooks();

    Book getBook(Long id);

    String getBookImage(Long id);

    Book createBook(String authUsername, BookCommand command);

    Book updateBook(String authUsername, Long id, BookCommand command);

    void deleteBook(String authUsername, Long id);

    public static final class BookCommand {
        private final String title;
        private final String author;
        private final String category;
        private final Integer stock;
        private final Integer recommendationIndex;
        private final String image;
        private final String status;

        public BookCommand(String title, String author, String category, Integer stock, Integer recommendationIndex,
                           String image, String status) {
            this.title = title;
            this.author = author;
            this.category = category;
            this.stock = stock;
            this.recommendationIndex = recommendationIndex;
            this.image = image;
            this.status = status;
        }

        public String title() {
            return title;
        }

        public String author() {
            return author;
        }

        public String category() {
            return category;
        }

        public Integer stock() {
            return stock;
        }

        public Integer recommendationIndex() {
            return recommendationIndex;
        }

        public String image() {
            return image;
        }

        public String status() {
            return status;
        }
    }
}
