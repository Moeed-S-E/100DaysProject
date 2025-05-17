package app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

import app.dao.MemberDAO;
import app.entity.Member;

public class MembersController {
    @FXML
    private TableView<Member> membersTable;
    @FXML
    private TableColumn<Member, Integer> memberIDColumn;
    @FXML
    private TableColumn<Member, String> nameColumn;
    @FXML
    private TableColumn<Member, String> emailColumn;
    @FXML
    private TableColumn<Member, String> phoneColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    private final MemberDAO memberDAO = new MemberDAO();

    @FXML
    public void initialize() {
        // Bind table columns
        memberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        loadMembers();
    }

    public void loadMembers() {
        try {
            ObservableList<Member> members = FXCollections.observableArrayList(memberDAO.getAllMembers());
            membersTable.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addMember() {
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            Member member = new Member(0, name, email, phone);
            memberDAO.addMember(member);
            loadMembers();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
    }
}