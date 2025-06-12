package app.models;

import java.time.LocalDate;
import java.util.List;

public class Order {
 private String id;
 private Customer customer;
 private List<OrderItem> items;
 private LocalDate date;

}