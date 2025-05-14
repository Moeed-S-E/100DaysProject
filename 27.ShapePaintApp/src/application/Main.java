package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static class Shape {
        ShapeType type;
        double x1, y1, x2, y2;
        Color color;
        boolean isFilled;

        Shape(ShapeType type, double x1, double y1, double x2, double y2, Color color, boolean isFilled) {
            this.type = type;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.isFilled = isFilled;
        }

        void draw(GraphicsContext gc) {
            gc.setStroke(color);
            gc.setFill(color);
            double x = Math.min(x1, x2);
            double y = Math.min(y1, y2);
            double width = Math.abs(x2 - x1);
            double height = Math.abs(y2 - y1);

            switch (type) {
                case LINE:
                    gc.strokeLine(x1, y1, x2, y2);
                    break;
                case RECTANGLE:
                    if (isFilled) gc.fillRect(x, y, width, height);
                    else gc.strokeRect(x, y, width, height);
                    break;
                case CIRCLE:
                    if (isFilled) gc.fillOval(x, y, width, height);
                    else gc.strokeOval(x, y, width, height);
                    break;
                case TRIANGLE:
                    double[] xPoints = { (x1 + x2) / 2, Math.min(x1, x2), Math.max(x1, x2) };
                    double[] yPoints = { Math.min(y1, y2), Math.max(y1, y2), Math.max(y1, y2) };
                    if (isFilled) gc.fillPolygon(xPoints, yPoints, 3);
                    else gc.strokePolygon(xPoints, yPoints, 3);
                    break;
            }
        }
    }

    private enum ShapeType {
        LINE, RECTANGLE, CIRCLE, TRIANGLE
    }
    private ShapeType currentShape = ShapeType.LINE;
    private double startX, startY, endX, endY;
    private Color currentColor = Color.BLACK;
    private boolean fillShape = false;
    private List<Shape> shapes = new ArrayList<>();
    private boolean moveMode = false;
    private Shape selectedShape = null;
    
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            canvas = new Canvas(600, 400);
            graphicsContext = canvas.getGraphicsContext2D();
            root.setCenter(canvas);
            
            canvas.widthProperty().bind(root.widthProperty());
            canvas.heightProperty().bind(root.heightProperty().subtract(50));
            
            ToolBar toolBar = new ToolBar();
            
            Button lineBtn = new Button("Line");
            Button rectBtn = new Button("Rectangle");
            Button circleBtn = new Button("Circle");
            Button triangleButton = new Button("Triangle");
            Button moveBtn = new Button("Move");
            Button clearBtn = new Button("Clear");
            
            ColorPicker colorPicker = new ColorPicker(Color.BLACK);
            CheckBox fillCheckbox = new CheckBox("Fill");
            
            toolBar.getItems().addAll(lineBtn, rectBtn, circleBtn, triangleButton, moveBtn, new Separator(),
                    colorPicker, fillCheckbox, new Separator(), clearBtn);
            root.setTop(toolBar);
            
            lineBtn.setOnAction(e -> {
                currentShape = ShapeType.LINE;
                moveMode = false;
                System.out.println("Selected: LINE");
            });
            rectBtn.setOnAction(e -> {
                currentShape = ShapeType.RECTANGLE;
                moveMode = false;
                System.out.println("Selected: RECTANGLE");
            });
            circleBtn.setOnAction(e -> {
                currentShape = ShapeType.CIRCLE;
                moveMode = false;
                System.out.println("Selected: CIRCLE");
            });
            triangleButton.setOnAction(e -> {
                currentShape = ShapeType.TRIANGLE;
                moveMode = false;
                System.out.println("Selected: TRIANGLE");
            });
            moveBtn.setOnAction(e -> {
                moveMode = !moveMode;
                System.out.println("Move mode: " + (moveMode ? "ON" : "OFF"));
            });
            clearBtn.setOnAction(e -> {
                shapes.clear();
                graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            });
            
            colorPicker.setOnAction(e -> currentColor = colorPicker.getValue());
            fillCheckbox.setOnAction(e -> fillShape = fillCheckbox.isSelected());
            
            canvas.setOnMousePressed(this::handleMousePressed);
            canvas.setOnMouseReleased(this::handleMouseReleased);
            canvas.setOnMouseDragged(e -> {
                if (moveMode && selectedShape != null) {
                    double dx = e.getX() - startX;
                    double dy = e.getY() - startY;
                    selectedShape.x1 += dx;
                    selectedShape.y1 += dy;
                    selectedShape.x2 += dx;
                    selectedShape.y2 += dy;
                    startX = e.getX();
                    startY = e.getY();
                    redrawCanvas();
                }
            });
            
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Shape Paint App");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        if (moveMode) {
            selectedShape = null;
            for (Shape shape : shapes) {
                double x = Math.min(shape.x1, shape.x2);
                double y = Math.min(shape.y1, shape.y2);
                double width = Math.abs(shape.x2 - shape.x1);
                double height = Math.abs(shape.y2 - shape.y1);
                if (startX >= x && startX <= x + width && startY >= y && startY <= y + height) {
                    selectedShape = shape;
                    break;
                }
            }
        }
    }
    
    private void handleMouseReleased(MouseEvent event) {
        if (moveMode) return;
        endX = event.getX();
        endY = event.getY();
        
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endX - startX);
        
        if (width < 1 || height < 1 && currentShape != ShapeType.LINE) {
            return;
        }
        
        shapes.add(new Shape(currentShape, startX, startY, endX, endY, currentColor, fillShape));
        redrawCanvas();
    }
    
    private void redrawCanvas() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape shape : shapes) {
            shape.draw(graphicsContext);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}