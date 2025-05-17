package app.entity;

import java.time.LocalDate;

public class IssuedBook {
    private int issueID;
    private int bookID;
    private int memberID;
    private LocalDate issueDate;
    private LocalDate returnDate;

    public IssuedBook(int issueID, int bookID, int memberID, LocalDate issueDate, LocalDate returnDate) {
        this.issueID = issueID;
        this.bookID = bookID;
        this.memberID = memberID;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public int getIssueID() {
        return issueID;
    }
    public int getBookID() {
        return bookID;
    }
    public int getMemberID() {
        return memberID;
    }
    public LocalDate getIssueDate() {
        return issueDate;
    }
    public LocalDate getReturnDate() {
        return returnDate;
    }
    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }
    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}