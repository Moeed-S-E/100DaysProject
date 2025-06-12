package app.models;

public class Product {
    public Product(String name, int stock, double price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }
    private String id;
    private String name;
    private int stock;
    private double price;
    public String getId() {
		return id;
	}
    public String getName() {
		return name;
	}
    public double getPrice() {
		return price;
	}
    public int getStock() {
		return stock;
	}
    public void setId(String id) {
		this.id = id;
	}
    public void setStock(int stock) {
		this.stock = stock;
	}
    public void setPrice(double price) {
		this.price = price;
	}
    public void setName(String name) {
		this.name = name;
	}
}