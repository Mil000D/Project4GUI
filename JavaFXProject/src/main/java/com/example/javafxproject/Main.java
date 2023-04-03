package com.example.javafxproject;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;


public class Main extends Application {

    private ObservableList<Shape> shapes = FXCollections.observableArrayList();
    private Canvas canvas = new Canvas(850, 650);
    private Color usedColorFill = Color.WHITE;
    private String usedColorStringFill = "White";
    private Color usedColorOutline = Color.BLACK;
    private String usedColorStringOutline = "Black";
    private SimpleObjectProperty<Color> leftPanelColorFill = new SimpleObjectProperty<>(Color.BLACK);
    private SimpleStringProperty leftPanelColorStringFill = new SimpleStringProperty("Black");
    private SimpleObjectProperty<Color> leftPanelColorOutline = new SimpleObjectProperty<>(Color.BLACK);
    private SimpleStringProperty leftPanelColorStringOutline = new SimpleStringProperty("Black");
    private ListView<Shape> listView = new ListView<>(shapes);
    private Label l = new Label();
    private IntegerProperty x = new SimpleIntegerProperty();
    private IntegerProperty y = new SimpleIntegerProperty();
    private Shape currentShape = null;
    private IntegerProperty dx = new SimpleIntegerProperty();
    private IntegerProperty dy = new SimpleIntegerProperty();


    public static void main(String[] args) {
        launch(args);
    }

    private void binding(MouseEvent event, IntegerProperty x, IntegerProperty y) {
        IntegerProperty xx = new SimpleIntegerProperty((int) event.getX());
        IntegerProperty yy = new SimpleIntegerProperty((int) event.getY());
        x.bind(xx);
        y.bind(yy);
    }


    public void start(Stage stage) {
        canvas.setOnMouseReleased(mouseEvent -> currentShape = null);
        canvas.setOnMousePressed(mouseEvent -> {
            binding(mouseEvent, x, y);
            for (int i = 0; i < shapes.size(); i++) {
                if (shapes.get(i).isFigureClicked(x, y)) {
                    currentShape = shapes.get(i);
                    listView.getSelectionModel().select(i);
                    dx.setValue(x.getValue());
                    dy.setValue(y.getValue());
                    paint();
                }
            }
        });
        canvas.setOnMouseDragged(mouseEvent ->
        {
            binding(mouseEvent, x, y);
            if (currentShape != null) {
                IntegerBinding i = new IntegerBinding() {
                    {
                        super.bind(x, dx);
                    }

                    protected int computeValue() {
                        return x.get() - dx.get();
                    }
                };
                IntegerBinding ii = new IntegerBinding() {
                    {
                        super.bind(y, dy);
                    }

                    protected int computeValue() {
                        return y.get() - dy.get();
                    }
                };
                currentShape.move(i, ii);
                dx.setValue(x.getValue());
                dy.setValue(y.getValue());
                l.textProperty().bind(listView.getSelectionModel().selectedItemProperty().asString());
                listView.refresh();
                paint();
            }
        });
        paint();
        StackPane pane = new StackPane(canvas);
        BorderPane root = new BorderPane(pane);
        root.setTop(topPanel());
        root.setLeft(leftTopPanel());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public VBox leftTopPanel() {
        VBox panel = new VBox();
        listView.setMaxSize(400, 400);
        panel.getChildren().addAll(listView, leftBottomPanel());

        return panel;

    }

    public VBox leftBottomPanel() {
        l.textProperty().bind(listView.getSelectionModel().selectedItemProperty().asString());
        VBox panelBottom = new VBox();
        panelBottom.setVisible(false);
        ChangeListener<Shape> listener =
                (obsVal, oldV, newV) -> panelBottom.setVisible(true);
        listView.getSelectionModel().selectedItemProperty().addListener(listener);
        panelBottom.getChildren().add(l);
        ComboBox<String> combobox = new ComboBox<>();
        combobox.setEditable(false);
        Color[] colors = {Color.BLACK, Color.BLUE, Color.YELLOW, Color.BROWN,
                Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.RED, Color.GRAY};
        String[] colorString = {"Black", "Blue", "Yellow", "Brown",
                "Cyan", "Green", "Magenta", "Orange", "Red", "Gray"};
        combobox.getItems().addAll(colorString);
        combobox.setValue("Black");
        combobox.setOnAction(actionEvent -> {
            leftPanelColorFill.setValue(colors[combobox.getSelectionModel().getSelectedIndex()]);
            leftPanelColorStringFill.setValue(colorString[combobox.getSelectionModel().getSelectedIndex()]);
        });

        Button fillColorButton = new Button();
        fillColorButton.textProperty().bind(new StringBinding() {
            {
                super.bind(combobox.getSelectionModel().selectedItemProperty());
            }

            protected String computeValue() {
                return "Change fill color to : " + combobox.getSelectionModel().selectedItemProperty().get();
            }
        });
        fillColorButton.setOnAction(actionEvent -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                listView.selectionModelProperty().get().getSelectedItem().colorFill.bind(leftPanelColorFill);
                listView.selectionModelProperty().get().getSelectedItem().colorStringFill.bind(leftPanelColorStringFill);
                l.textProperty().bind(listView.getSelectionModel().selectedItemProperty().asString());
                listView.refresh();
                paint();
                listView.selectionModelProperty().get().getSelectedItem().colorFill.unbind();
                listView.selectionModelProperty().get().getSelectedItem().colorStringFill.unbind();
            }
        });

        ComboBox<String> comboboxOutline = new ComboBox<>();
        comboboxOutline.setEditable(false);
        comboboxOutline.getItems().addAll(colorString);
        comboboxOutline.setValue("Black");
        comboboxOutline.setOnAction(actionEvent -> {
            leftPanelColorOutline.setValue(colors[comboboxOutline.getSelectionModel().getSelectedIndex()]);
            leftPanelColorStringOutline.setValue(colorString[comboboxOutline.getSelectionModel().getSelectedIndex()]);
        });

        Button outlineColorButton = new Button();
        outlineColorButton.textProperty().bind(new StringBinding() {
            {
                super.bind(comboboxOutline.getSelectionModel().selectedItemProperty());
            }

            protected String computeValue() {
                return "Change outline color to : " + comboboxOutline.getSelectionModel().selectedItemProperty().get();
            }
        });
        outlineColorButton.setOnAction(actionEvent -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                listView.selectionModelProperty().get().getSelectedItem().colorOutline.bind(leftPanelColorOutline);
                listView.selectionModelProperty().get().getSelectedItem().colorStringOutline.bind(leftPanelColorStringOutline);
                l.textProperty().bind(listView.getSelectionModel().selectedItemProperty().asString());
                listView.refresh();
                paint();
                listView.selectionModelProperty().get().getSelectedItem().colorOutline.unbind();
                listView.selectionModelProperty().get().getSelectedItem().colorStringOutline.unbind();
            }
        });
        Button changeLayer = new Button("Change layer to the last one");
        changeLayer.setOnAction(actionEvent -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                Shape s = listView.getSelectionModel().getSelectedItem();
                shapes.remove(listView.getSelectionModel().getSelectedIndex());
                shapes.add(s);
                listView.getSelectionModel().selectLast();
                for (int i = 0; i < shapes.size(); i++) {
                    shapes.get(i).layer.setValue(i);
                }
                paint();
                listView.refresh();
            }
        });
        Button delete = new Button("Delete shape");
        delete.setOnAction(actionEvent -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                shapes.remove(listView.getSelectionModel().getSelectedIndex());
                for (int i = 0; i < shapes.size(); i++) {
                    shapes.get(i).layer.setValue(i);
                }
                paint();
                listView.refresh();
            }
        });

        panelBottom.getChildren().addAll(fillColorButton, combobox, outlineColorButton, comboboxOutline, changeLayer, delete);
        return panelBottom;
    }

    public boolean checkingString(String s) {
        int intValue;
        try {
            intValue = Integer.parseInt(s);
            return intValue >= 1;
        } catch (NumberFormatException e) {
            System.out.println("ERROR");
        }
        return false;
    }

    public EventHandler<ActionEvent> MenuEvent(Color color, String colorString, boolean fillColor) {
        EventHandler<ActionEvent> event = e -> {
            if (fillColor) {
                usedColorFill = color;
                usedColorStringFill = colorString;
            } else {
                usedColorOutline = color;
                usedColorStringOutline = colorString;
            }
        };
        return event;
    }

    public Menu creatingMenu(boolean fillColor) {
        Menu colorMenu;
        if (fillColor) {
            colorMenu = new Menu("Fill color");
        } else {
            colorMenu = new Menu("Outline color");
        }
        MenuItem black = new MenuItem("Black");
        black.setOnAction(MenuEvent(Color.BLACK, "Black", fillColor));
        MenuItem blue = new MenuItem("Blue");
        blue.setOnAction(MenuEvent(Color.BLUE, "Blue", fillColor));
        MenuItem yellow = new MenuItem("Yellow");
        yellow.setOnAction(MenuEvent(Color.YELLOW, "Yellow", fillColor));
        MenuItem brown = new MenuItem("Brown");
        brown.setOnAction(MenuEvent(Color.BROWN, "Brown", fillColor));
        MenuItem cyan = new MenuItem("Cyan");
        cyan.setOnAction(MenuEvent(Color.CYAN, "Cyan", fillColor));
        MenuItem green = new MenuItem("Green");
        green.setOnAction(MenuEvent(Color.GREEN, "Green", fillColor));
        MenuItem magenta = new MenuItem("Magenta");
        magenta.setOnAction(MenuEvent(Color.MAGENTA, "Magenta", fillColor));
        MenuItem orange = new MenuItem("Orange");
        orange.setOnAction(MenuEvent(Color.ORANGE, "Orange", fillColor));
        MenuItem red = new MenuItem("Red");
        red.setOnAction(MenuEvent(Color.RED, "Red", fillColor));
        MenuItem gray = new MenuItem("Gray");
        gray.setOnAction(MenuEvent(Color.GRAY, "Gray", fillColor));
        colorMenu.getItems().addAll(black, blue, yellow, brown, cyan, green, magenta, orange, red, gray);
        return colorMenu;
    }

    public HBox topPanel() {
        Menu menu = new Menu("Menu");
        Menu shapeDraw = new Menu("Shapes");
        MenuItem m1 = new MenuItem("Draw circle");
        m1.setOnAction((e) -> newShape(new Circle()));
        MenuItem m2 = new MenuItem("Draw square");
        m2.setOnAction((e) -> newShape(new Square()));
        MenuItem m3 = new MenuItem("Draw rectangle");
        m3.setOnAction((e) -> newShape(new Rectangle()));
        MenuItem m4 = new MenuItem("Draw hexagon");
        m4.setOnAction((e) -> newShape(new Hexagon()));
        MenuItem m5 = new MenuItem("Draw sun");
        CustomMenuItem textF = new CustomMenuItem();
        textF.setHideOnClick(false);
        TextField tt = new TextField();
        m5.setOnAction(actionEvent -> {
            String s = tt.getText();
            if (checkingString(s)) {
                newShape(new SunShape(s));
            }
        });
        textF.setContent(tt);

        MenuItem m6 = new MenuItem("Draw rings");
        CustomMenuItem textFF = new CustomMenuItem();
        textFF.setHideOnClick(false);
        TextField ttt = new TextField();
        m6.setOnAction(actionEvent -> {
            String s = ttt.getText();
            if (checkingString(s)) {
                newShape(new Rings(s));
            }
        });
        textFF.setContent(ttt);
        SeparatorMenuItem sep = new SeparatorMenuItem();
        shapeDraw.getItems().addAll(m1, m2, m3, m4, m5, textF, sep, m6, textFF);
        menu.getItems().addAll(shapeDraw, creatingMenu(true), creatingMenu(false));


        Button circleButton = new Button("Draw circle");
        circleButton.setOnAction((e) -> newShape(new Circle()));
        Button squareButton = new Button("Draw square");
        squareButton.setOnAction((e) -> newShape(new Square()));
        Button rectangleButton = new Button("Draw rectangle");
        rectangleButton.setOnAction((e) -> newShape(new Rectangle()));
        Button hexagonButton = new Button("Draw hexagon");
        hexagonButton.setOnAction((e) -> newShape(new Hexagon()));
        TextField tf = new TextField();
        TextField tf2 = new TextField();


        ComboBox<String> comboboxFill = new ComboBox<>();
        comboboxFill.setEditable(false);
        Color[] color = {Color.BLACK, Color.BLUE, Color.YELLOW, Color.BROWN,
                Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.RED, Color.GRAY};
        String[] colorString = {"Black", "Blue", "Yellow", "Brown",
                "Cyan", "Green", "Magenta", "Orange", "Red", "Gray"};
        comboboxFill.getItems().addAll(colorString);
        comboboxFill.setValue("Black");
        comboboxFill.setOnAction(actionEvent -> {
            usedColorFill = color[comboboxFill.getSelectionModel().getSelectedIndex()];
            usedColorStringFill = colorString[comboboxFill.getSelectionModel().getSelectedIndex()];
        });
        comboboxFill.setVisible(false);


        ComboBox<String> comboboxOutline = new ComboBox<>();
        comboboxOutline.setEditable(false);
        comboboxOutline.getItems().addAll(colorString);
        comboboxOutline.setValue("Black");
        comboboxOutline.setOnAction(actionEvent -> {
            usedColorOutline = (color[comboboxOutline.getSelectionModel().getSelectedIndex()]);
            usedColorStringOutline = colorString[comboboxOutline.getSelectionModel().getSelectedIndex()];
        });
        HBox hbox = new HBox();
        Button sunButton = new Button("Draw sun");
        sunButton.setOnAction(actionEvent -> {
            String s = tf.getText();
            if (checkingString(s)) {
                newShape(new SunShape(s));
            }
        });
        Button ringsButton = new Button("Draw rings");
        ringsButton.setOnAction(actionEvent -> {
            String s = tf2.getText();
            if (checkingString(s)) {
                newShape(new Rings(s));
            }
        });
        CheckBox checkBox = new CheckBox("Fill : ");
        checkBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        checkBox.selectedProperty().addListener((obsV, oldV, newV) -> {
            if (newV) {
                comboboxFill.setVisible(true);
                usedColorFill = Color.BLACK;
                usedColorStringFill = "Black";
            } else {
                comboboxFill.setVisible(false);
                usedColorFill = (Color.WHITE);
                usedColorStringFill = "White";
            }
        });
        MenuBar bar = new MenuBar();
        bar.getMenus().add(menu);
        hbox.getChildren().addAll(bar, squareButton, circleButton, rectangleButton, hexagonButton, sunButton,
                tf, ringsButton, tf2, comboboxOutline, checkBox, comboboxFill);

        return hbox;
    }

    public void paint() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape elem : shapes) {
            elem.draw(g);
        }
    }

    public void newShape(Shape shape) {
        shape.setColor(usedColorFill, usedColorStringFill, usedColorOutline, usedColorStringOutline);
        shapes.add(shape);
        shape.layer.setValue(shapes.size() - 1);
        listView.refresh();
        paint();
    }

}


abstract class Shape {
    SimpleObjectProperty<Color> colorFill = new SimpleObjectProperty<>(Color.WHITE);
    SimpleStringProperty colorStringFill = new SimpleStringProperty("White");
    SimpleObjectProperty<Color> colorOutline = new SimpleObjectProperty<>(Color.BLACK);
    SimpleStringProperty colorStringOutline = new SimpleStringProperty("Black");
    SimpleIntegerProperty layer = new SimpleIntegerProperty(0);


    abstract void move(IntegerBinding dx, IntegerBinding dy);

    public void setColor(Color color, String colorString, Color colorOutline, String colorStringOutline) {
        this.colorFill = new SimpleObjectProperty<>(color);
        this.colorStringFill = new SimpleStringProperty(colorString);
        this.colorStringOutline = new SimpleStringProperty(colorStringOutline);
        this.colorOutline = new SimpleObjectProperty<>(colorOutline);
    }

    abstract boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy);

    abstract void draw(GraphicsContext g);


    @Override
    public String toString() {
        if (colorStringFill.getValue().equals("White")) {
            return "Empty\n" + "Outline color : " + colorStringOutline.getValue() + "\n" +
                    "Layer : " + layer.getValue();
        } else {
            return "Filled\n" + "Fill color : " + colorStringFill.getValue() + "\n" +
                    "Outline color : " + colorStringOutline.getValue() + "\n" +
                    "Layer : " + layer.getValue();
        }
    }
}

class Rectangle extends Shape {
    int x = 350;
    int y = 275;
    int width = 100, height = 50;

    void draw(GraphicsContext g) {
        g.setFill(colorFill.get());
        g.setStroke(colorOutline.get());
        g.fillRect(x, y, width, height);
        g.strokeRect(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Name of shape : Rectangle\n" +
                "Coordinates  x : " + x + " y : " + y + "\n" +
                super.toString();
    }

    void move(IntegerBinding dx, IntegerBinding dy) {
        x += dx.get();
        y += dy.get();
    }

    boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy) {
        return Bindings.and(Bindings.greaterThanOrEqual(ix, x), Bindings.lessThan(ix, x + width))
                .and(Bindings.greaterThanOrEqual(iy, y)).and(Bindings.lessThan(iy, y + height)).get();
    }


}


class Circle extends Shape {
    int x = 350;
    int y = 275;
    int width = 100, height = 100;

    public void draw(GraphicsContext g) {
        g.setFill(colorFill.get());
        g.setStroke(colorOutline.get());
        g.fillOval(x, y, width, height);
        g.strokeOval(x, y, width, height);
    }

    public void move(IntegerBinding dx, IntegerBinding dy) {
        x += dx.get();
        y += dy.get();
    }

    public boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy) {

        double r = width / 2;
        double xx = x + r - ix.get();
        double yy = y + r - iy.get();
        SimpleDoubleProperty xxyy = new SimpleDoubleProperty((xx * xx) + (yy * yy));
        SimpleDoubleProperty rr = new SimpleDoubleProperty(r * r);
        return Bindings.lessThan(xxyy, rr).get();

    }

    @Override
    public String toString() {
        return "Name of shape : Circle\n" +
                "Coordinates  x : " + x + " y : " + y + "\n" +
                super.toString();
    }
}


class SunShape extends Shape {
    int x = 400;
    int y = 300;
    int sunRays;


    SunShape(String sunRays) {
        this.sunRays = Integer.parseInt(sunRays);
    }

    public void draw(GraphicsContext g) {

        g.setFill(colorFill.get());
        g.setStroke(colorOutline.get());
        int a = x;
        int b = y;
        int m = 100;
        int r = m / 5;
        int r2 = m - r / 2;


        int angle = 350;
        for (int i = 1; i <= sunRays; i++) {
            angle -= 360 / sunRays;
            g.fillArc(x - r2, y - r2, 2 * r2, 2 * r2, angle, 20, ArcType.ROUND);
            g.strokeArc(x - r2, y - r2, 2 * r2, 2 * r2, angle, 20, ArcType.ROUND);
        }
        g.fillOval(a - r, b - r, 2 * r, 2 * r);
        g.strokeOval(a - r, b - r, 2 * r, 2 * r);

    }

    public void move(IntegerBinding dx, IntegerBinding dy) {
        x += dx.get();
        y += dy.get();
    }

    public boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy) {

        double r = 100;
        double xx = x - ix.get();
        double yy = y - iy.get();
        SimpleDoubleProperty xxyy = new SimpleDoubleProperty((xx * xx) + (yy * yy));
        SimpleDoubleProperty rr = new SimpleDoubleProperty(r * r);
        return Bindings.lessThan(xxyy, rr).get();
    }


    @Override
    public String toString() {
        return "Name of shape : Sun\n" +
                "Coordinates  x : " + x + " y : " + y + "\n" +
                super.toString();
    }
}

class Rings extends Shape {
    int rings;
    int x = 350;
    int y = 275;
    int width = 100;

    public Rings(String rings) {
        this.rings = Integer.parseInt(rings);
    }

    public void draw(GraphicsContext g) {
        int xx = x;
        int yy = y;
        int r = width;
        for (int k = rings; k >= 0; k--) {
            if (k % 2 == 0) {
                g.setFill(Color.WHITE);
            } else {
                g.setFill(colorFill.get());
            }
            g.setStroke(colorOutline.get());
            g.fillOval(xx, yy, r, r);
            g.strokeOval(xx, yy, r, r);
            r /= 2;
            xx = xx + r / 2;
            yy = yy + r / 2;
        }

    }

    public void move(IntegerBinding dx, IntegerBinding dy) {
        x += dx.get();
        y += dy.get();
    }

    public boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy) {

        double r = width / 2;
        double xx = x + r - ix.get();
        double yy = y + r - iy.get();
        SimpleDoubleProperty xxyy = new SimpleDoubleProperty((xx * xx) + (yy * yy));
        SimpleDoubleProperty rr = new SimpleDoubleProperty(r * r);
        return Bindings.lessThan(xxyy, rr).get();

    }


    @Override
    public String toString() {
        return "Name of shape : Rings\n" +
                "Coordinates  x : " + x + " y : " + y + "\n" +
                super.toString();
    }
}

class Square extends Shape {
    int x = 350;
    int y = 275;
    int width = 100, height = 100;

    void draw(GraphicsContext g) {
        g.setFill(colorFill.get());
        g.setStroke(colorOutline.get());
        g.fillRect(x, y, width, height);
        g.strokeRect(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Name of shape : Square\n" +
                "Coordinates  x : " + x + " y : " + y + "\n" +
                super.toString();
    }

    public void move(IntegerBinding dx, IntegerBinding dy) {
        x += dx.get();
        y += dy.get();
    }

    public boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy) {
        return Bindings.and(Bindings.greaterThanOrEqual(ix, x), Bindings.lessThan(ix, x + width))
                .and(Bindings.greaterThanOrEqual(iy, y)).and(Bindings.lessThan(iy, y + height)).get();
    }


}

class Hexagon extends Shape {
    int x = 350;
    int y = 275;
    int width = 100, height = 50;
    int width2 = 50, height2 = 100;

    void draw(GraphicsContext g) {
        g.setFill(colorFill.get());
        g.setStroke(colorOutline.getValue());
        g.strokeRect(x, y, width, height);
        g.strokeRect(x, y, width2, height2);
        g.fillRect(x, y, width, height);
        g.fillRect(x, y, width2, height2);

    }

    @Override
    public String toString() {
        return "Name of shape : Hexagon\n" +
                "Coordinates  x : " + x + " y : " + y + "\n" +
                super.toString();
    }

    public void move(IntegerBinding dx, IntegerBinding dy) {
        x += dx.get();
        y += dy.get();
    }

    public boolean isFigureClicked(IntegerProperty ix, IntegerProperty iy) {
        return Bindings.and(Bindings.greaterThanOrEqual(ix, x), Bindings.lessThan(ix, x + width))
                .and(Bindings.greaterThanOrEqual(iy, y)).and(Bindings.lessThan(iy, y + height))
                .or((Bindings.greaterThanOrEqual(ix, x)).and(Bindings.lessThan(ix, x + width2))
                        .and(Bindings.lessThan(iy, y + height2)).and(Bindings.greaterThanOrEqual(iy, y))).get();
    }


}



