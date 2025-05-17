package app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

import app.dao.IssuedBookDAO;
import app.entity.IssuedBook;

public class IssuedBooksController {
    @FXML
    private TableView<IssuedBook> issuedBooksTable;
    @FXML
    private TableColumn<IssuedBook, Integer> issueIDColumn;
    @FXML
    private TableColumn<IssuedBook, Integer> bookIDColumn;
    @FXML
    private TableColumn<IssuedBook, Integer> memberIDColumn;
    @FXML
    private TableColumn<IssuedBook, LocalDate> issueDateColumn;
    @FXML
    private TableColumn<IssuedBook, LocalDate> returnDateColumn;
    @FXML
    private TextField bookIDField;
    @FXML
    private TextField memberIDField;
    @FXML
    private TextField issueDateField;
    @FXML
    private TextField returnDateField;

    private final IssuedBookDAO issuedBookDAO = new IssuedBookDAO();

    @FXML
    public void initialize() {
        // Bind table columns
        issueIDColumn.setCellValueFactory(new PropertyValueFactory<>("issueID"));
        bookIDColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        memberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        loadIssuedBooks();
    }

    public void loadIssuedBooks() {
        try {
            ObservableList<IssuedBook> issuedBooks = FXCollections.observableArrayList(issuedBookDAO.getAllIssuedBooks());
            issuedBooksTable.setItems(issuedBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void issueBook() {
        try {
            int bookID = Integer.parseInt(bookIDField.getText());
            int memberID = Integer.parseInt(memberIDField.getText());
            LocalDate issueDate = LocalDate.parse(issueDateField.getText());
            LocalDate returnDate = returnDateField.getText().isEmpty() ? null : LocalDate.parse(returnDateField.getText());

            IssuedBook issuedBook = new IssuedBook(0, bookID, memberID, issueDate, returnDate);
            issuedBookDAO.issueBook(issuedBook);
            loadIssuedBooks();
            clearFields();
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        bookIDField.clear();
        memberIDField.clear();
        issueDateField.clear();
        returnDateField.clear();
    }
}