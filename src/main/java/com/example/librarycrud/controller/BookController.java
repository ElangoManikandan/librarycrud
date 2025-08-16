package com.example.librarycrud.controller;

import com.example.librarycrud.entity.Book;
import com.example.librarycrud.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*") // Allow frontend requests
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // Get all books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new book
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        // Manual validation
        String error = validateBook(book);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    // Update book
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        String error = validateBook(bookDetails);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(bookDetails.getTitle());
                    book.setAuthor(bookDetails.getAuthor());
                    book.setGenre(bookDetails.getGenre());
                    book.setPrice(bookDetails.getPrice());
                    bookRepository.save(book);
                    return ResponseEntity.ok(book);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Manual validation method
    private String validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return "Title is mandatory";
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return "Author is mandatory";
        }
        if (book.getPrice() < 0) {
            return "Price must be positive";
        }
        return null; // no error
    }
}
