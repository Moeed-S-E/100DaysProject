package app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

import app.dao.BookDAO;
import app.entity.Book;

public class BooksController {
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, Integer> bookIDColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, Integer> quantityColumn;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField quantityField;

    private final BookDAO bookDAO = new BookDAO();

    @FXML
    public void initialize() {
        // Bind table columns
        bookIDColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        loadBooks();
    }

    public void loadBooks() {
        try {
            ObservableList<Book> books = FXCollections.observableArrayList(bookDAO.getAllBooks());
            booksTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addBook() {
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            Book book = new Book(0, title, author, genre, quantity);
            bookDAO.addBook(book);
            loadBooks();
            clearFields();
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        genreField.clear();
        quantityField.clear();
    }
}