/**
 * Created by Илья on 28.11.2015.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Classic {

    public static Connection conn;

    public static void createDB() {
        Statement s = null;
        try {
            s = conn.createStatement();
            s.execute("create table if not exists [Customers] ([C_ID] INT PRIMARY KEY, [CustomerName] NVARCHAR(50));");
            s.execute("create table if not exists [Orders] ([O_ID] INT PRIMARY KEY, [C_ID] INT, FOREIGN KEY(C_ID) REFERENCES Customers(C_ID));");
            s.execute("create table if not exists [Products] ([P_ID] INT PRIMARY KEY, [ProductsName] NVARCHAR(50));");
            s.execute("create table if not exists [OrdersProducts] ([OP_ID] INT PRIMARY KEY, [O_ID] INT, [P_ID] INT, FOREIGN KEY(O_ID) REFERENCES Orders(O_ID), FOREIGN KEY(P_ID) REFERENCES Products(P_ID));");
        } catch (SQLException e) {
            System.out.println("При создании базы данных произошла ошибка.");
        }
        System.out.println("База данных создана успешно.");
    }

    // <editor-fold defaultstate="collapsed" desc="Заказчики">
    public static void executeForCustomers(String[] args) throws SQLException {
        switch (args[1]) {
            case "create":
                createCustomer(Integer.parseInt(args[2]), args[3]);
                break;
            case "read":
                readCustomers();
                break;
            case "update":
                updateCustomer(Integer.parseInt(args[2]), args[3]);
                break;
            case "delete":
                deleteCustomer(Integer.parseInt(args[2]));
                break;
            default:
                break;
        }
    }

    public static void createCustomer(int C_ID, String customerName) {
        try {
            PreparedStatement s = conn.prepareStatement("insert into Customers values (?,?)");
            s.setInt(1, C_ID);
            s.setString(2, customerName);
            s.execute();
        } catch (SQLException e) {
            System.out.println("При создании Заказчика произошла ошибка.");
        }
        System.out.println("Заказчик добавлен");
    }

    public static void updateCustomer(int C_ID, String customerName) {
        try {
            PreparedStatement s = conn.prepareStatement("update Customers set CustomerName=? where C_ID=?");
            s.setString(1, customerName);
            s.setInt(2, C_ID);
            s.execute();
        } catch (SQLException e) {
            System.out.println("При редактировании Заказчика произошла ошибка.");
        }
        System.out.println("Заказчик обновлён");
    }

    public static void deleteCustomer(int C_ID) {
        try {
            PreparedStatement s = conn.prepareStatement("delete from Customers where C_ID=?");
            s.setInt(1, C_ID);
            s.execute();
        } catch (SQLException e) {
            System.out.println("При удалении поля Заказчика произошла ошибка.");
        }
        System.out.println("Заказчик удалён"); // может написать "Поле заказчика удалено", а то как-то двусмысленно?
        // TODO удалить комментарии, что не имеют отношения к коду
    }

    public static void readCustomers() {
        try {
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery("select * from Customers");
            ResultSetMetaData metaData = results.getMetaData();
            int noCols = metaData.getColumnCount();

            System.out.printf("%-10s\t", "CustomerID");
            System.out.printf("%-10s\t", "CustomerName");
            System.out.println();
            while (results.next()) {
                for (int i = 1; i <= noCols; i++) {
                    System.out.printf("%-10s\t", results.getObject(i));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("При выводе всех записей о Заказчиках произошла ошибка.");
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Заказы">
    public static void executeForOrders(String[] args) throws SQLException {
        switch (args[1]) {
            case "create":
                createOrder(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                break;
            case "read":
                readOrders();
                break;
            case "update":
                updateOrder(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                break;
            case "delete":
                deleteOrder(Integer.parseInt(args[2]));
                break;
            default:
                break;
        }
    }

    public static void createOrder(int O_ID, int C_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("insert into Orders values (?,?)");
        s.setInt(1, O_ID);
        s.setInt(2, C_ID);
        s.execute();
        System.out.println("Заказ добавлен");
    }

    public static void updateOrder(int O_ID, int C_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("update Orders set C_ID=? where O_ID=?");
        s.setInt(1, C_ID);
        s.setInt(2, O_ID);
        s.execute();
        System.out.println("Заказ обновлён");
    }

    public static void deleteOrder(int O_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("delete from Orders where O_ID=?");
        s.setInt(1, O_ID);
        s.execute();
        System.out.println("Заказ удалён");
    }

    public static void readOrders() throws SQLException {
        Statement s = conn.createStatement();
        ResultSet results = s.executeQuery("select B.O_ID, A.CustomerName from orders as B inner join customers as A on A.C_ID=B.C_ID");
        ResultSetMetaData metaData = results.getMetaData();
        int noCols = metaData.getColumnCount();
        System.out.printf("%-10s\t", "OrderID");
        System.out.printf("%-10s\t", "Customer");
        System.out.println();
        while (results.next()) {
            for (int i = 1; i <= noCols; i++) {
                System.out.printf("%-10s\t", results.getObject(i));
            }
            System.out.println();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Товары">
    public static void executeForProducts(String[] args) throws SQLException {
        switch (args[1]) {
            case "create":
                createProduct(Integer.parseInt(args[2]), args[3]);
                break;
            case "update":
                updateProduct(Integer.parseInt(args[2]), args[3]);
                break;
            case "delete":
                deleteProduct(Integer.parseInt(args[2]));
                break;
            case "read":
                readProducts();
                break;
            default:
                break;
        }
    }

    public static void createProduct(int P_ID, String productName) throws SQLException {
        PreparedStatement s = conn.prepareStatement("insert into Products values (?,?)");
        s.setInt(1, P_ID);
        s.setString(2, productName);
        s.execute();
        System.out.println("Товар добавлен");
    }

    public static void updateProduct(int P_ID, String productName) throws SQLException {
        PreparedStatement s = conn.prepareStatement("update Products set ProductsName=? where P_ID=?");
        s.setString(1, productName);
        s.setInt(2, P_ID);
        System.out.println("Товар изменён");
    }

    public static void deleteProduct(int P_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("delete from Products where P_ID=?");
        s.setInt(1, P_ID);
        s.execute();
        System.out.println("Товар удалён");
    }

    public static void readProducts() throws SQLException {
        Statement s = conn.createStatement();
        ResultSet results = s.executeQuery("select * from Products");
        ResultSetMetaData metaData = results.getMetaData();
        int noCols = metaData.getColumnCount();
        System.out.printf("%-10s\t", "ProductID");
        System.out.printf("%-10s\t", "ProductName");
        System.out.println();
        while (results.next()) {
            for (int i = 1; i <= noCols; i++) {
                System.out.printf("%-10s\t", results.getObject(i));
            }
            System.out.println();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Заказы-Товары">
    public static void executeForOrdersProducts(String[] args) throws SQLException {
        switch (args[1]) {
            case "create":
                createOrdersProducts(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                break;
            case "update":
                updateOrdersProducts(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                break;
            case "delete":
                deleteOrdersProducts(Integer.parseInt(args[2]));
                break;
            case "read":
                readOrdersProducts();
                break;
            default:
                break;
        }
    }

    public static void createOrdersProducts(int OP_ID, int O_ID, int P_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("insert into OrdersProducts values (?,?,?)");
        s.setInt(1, OP_ID);
        s.setInt(2, O_ID);
        s.setInt(3, P_ID);
        s.execute();
        System.out.println("Добавлено");
    }

    public static void updateOrdersProducts(int OP_ID, int O_ID, int P_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("update OrdersProducts set O_ID=?, P_ID=? where OP_ID=?");
        s.setInt(1, O_ID);
        s.setInt(2, P_ID);
        s.setInt(3, OP_ID);
        s.execute();
        System.out.println("Изменено");
    }

    public static void deleteOrdersProducts(int OP_ID) throws SQLException {
        PreparedStatement s = conn.prepareStatement("delete from OrdersProducts where OP_ID=?");
        s.setInt(1, OP_ID);
        s.execute();
        System.out.println("Удалено");
    }

    public static void readOrdersProducts() throws SQLException {
        Statement s = conn.createStatement();
        ResultSet results = s.executeQuery("select D.CustomerName, B.ProductsName from OrdersProducts as A inner join products as B on A.P_ID=B.P_ID inner join orders as C on C.O_ID=A.O_ID inner join customers as D on D.C_ID=C.C_ID");
        ResultSetMetaData metaData = results.getMetaData();
        int noCols = metaData.getColumnCount();
        System.out.printf("%-10s\t", "Customer");
        System.out.printf("%-10s\t", "ProductOrdered");
        System.out.println();
        while (results.next()) {
            for (int i = 1; i <= noCols; i++) {
                System.out.printf("%-10s\t", results.getObject(i));
            }
            System.out.println();
        }
    }
    // </editor-fold>

    public  static void Info() {
        System.out.println("=========МЕНЮ=======");
        System.out.println("1: Создать Заказчика");
        System.out.println("2: Создать Товар");
        System.out.println("3: Создать Заказ");
        System.out.println("4: Товар многие-ко-многим Заказ");
        System.out.println("5: Все заказчики");
        System.out.println("6: Все заказы");
        System.out.println("7: Все товары");
        System.out.println("8: Все Товар м-к-м Заказ");
        System.out.println("9: Редактирование Заказчика");
        System.out.println("10: Редактирование Товара");
        System.out.println("11: Редактирование Заказа");
        System.out.println("12: Редактирование Товар многие-ко-многим Заказ");
        System.out.println("13: Удалить запись о Заказчике");
        System.out.println("14: Удалить товар из базы");
        System.out.println("15: Удалить заказ");
        System.out.println("16: Удалить Товар м-к-м Заказ");
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // CRUD - Create, Read, Update, Delete
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:Classic.db"); // Classic.db -- название нашей базы данных
        createDB();
        System.out.println("База данных загружена успешно.");
        System.out.println("Tips: Необходимо ввести «quit», чтобы выйти из приложения.");
        Info(); // Выводит пользователю информацию о нумерованных командах
        System.out.println("\nВведите номер команды:");

        Scanner sc = new Scanner(System.in);
        String userCommand;
        while ((userCommand = sc.nextLine()).compareTo("quit") != 0) {
            Scanner scaner = new Scanner(System.in);
            String cmd = null; // Команда пользователя
            String[] userCommandParsed = null; // Будем разделять
            String custId; // Номер заказдчика
            String custName; // Имя заказчика
            String prodId; // Номер товара
            String prodName; // Наименование товара
            String ordId; // Номер заказа
            String opId; // Номер Товар м-к-м Заказ
            switch (userCommand) {
                case "1":
                    System.out.println("===Добавление заказчика===");
                    System.out.println("Введите новый номер заказчика в таблице: ");
                    System.out.println("(Убедитесь, что номер не совпадает с предыдущими номерами в таблице Customers)");
                    custId = scaner.nextLine();
                    System.out.println("Введите имя: ");
                    custName = scaner.nextLine();
                    cmd = "Customers create "+custId+" "+custName;
                    userCommandParsed = cmd.split("\\s+");
                    executeForCustomers(userCommandParsed);
                    break;
                case "2":
                    System.out.println("===Добавление товара===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер товара в таблице: ");
                    prodId = scaner.nextLine();
                    System.out.println("Введите название товара: ");
                    prodName = scaner.nextLine();
                    cmd = "Products create "+prodId+" "+prodName;
                    userCommandParsed = cmd.split("\\s+");
                    executeForProducts(userCommandParsed);
                    break;
                case "3":
                    System.out.println("===Добавление заказа===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер заказа: ");
                    ordId = scaner.nextLine();
                    System.out.println("Введите номер заказчика: ");
                    custId = scaner.nextLine();
                    cmd = "Orders create "+ordId+" "+custId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrders(userCommandParsed);
                    break;
                case "4":
                    System.out.println("===Товар многие-ко-многим Заказ===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер Товар м-к-м Заказ: ");
                    opId = scaner.nextLine();
                    System.out.println("Введите номер заказа: ");
                    ordId = scaner.nextLine();
                    System.out.println("Введите номер товара: ");
                    prodId = scaner.nextLine();
                    cmd = "OrdersProducts create "+opId+" "+ordId+" "+prodId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrdersProducts(userCommandParsed);
                    break;
                case "5":
                    System.out.println("===Все заказчики===");
                    cmd = "Customers read";
                    userCommandParsed = cmd.split("\\s+");
                    executeForCustomers(userCommandParsed);
                    break;
                case "6":
                    System.out.println("===Все заказы===");
                    cmd = "Orders read";
                    userCommandParsed = cmd.split("\\s+");
                    executeForCustomers(userCommandParsed);
                    break;
                case "7":
                    System.out.println("===Все товары===");
                    cmd = "Products read";
                    userCommandParsed = cmd.split("\\s+");
                    executeForProducts(userCommandParsed);
                    break;
                case "8":
                    System.out.println("===Все Товар м-к-м Заказ===");
                    cmd = "OrdersProducts read";
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrdersProducts(userCommandParsed);
                    break;
                case "9":
                    System.out.println("===Редактирование заказчика===");
                    System.out.println("Введите номер заказчика в таблице: ");
                    custId = scaner.nextLine();
                    System.out.println("Введите новое имя: ");
                    custName = scaner.nextLine();
                    cmd = "Customers update "+custId+" "+custName;
                    userCommandParsed = cmd.split("\\s+");
                    executeForCustomers(userCommandParsed);
                    break;
                case "10":
                    System.out.println("===Редактирование товара===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер товара в таблице: ");
                    prodId = scaner.nextLine();
                    System.out.println("Введите новое название товара: ");
                    prodName = scaner.nextLine();
                    cmd = "Products update "+prodId+" "+prodName;
                    userCommandParsed = cmd.split("\\s+");
                    executeForProducts(userCommandParsed);
                    break;
                case "11":
                    System.out.println("===Редактирование заказа===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер заказа: ");
                    ordId = scaner.nextLine();
                    System.out.println("Введите новый номер заказчика: ");
                    custId = scaner.nextLine();
                    cmd = "Orders update "+ordId+" "+custId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrders(userCommandParsed);
                    break;
                case "12":
                    System.out.println("===Редактирование Товар многие-ко-многим Заказ===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер Товар м-к-м Заказ: ");
                    opId = scaner.nextLine();
                    System.out.println("Введите номер заказа: ");
                    ordId = scaner.nextLine();
                    System.out.println("Введите номер товара: ");
                    prodId = scaner.nextLine();
                    cmd = "OrdersProducts update "+opId+" "+ordId+" "+prodId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrdersProducts(userCommandParsed);
                    break;
                case "13":
                    System.out.println("===Удаление заказчика===");
                    System.out.println("Введите номер заказчика в таблице: ");
                    System.out.println("(Поле заказчика удалится навсегда)");
                    custId = scaner.nextLine();
                    cmd = "Customers delete "+custId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForCustomers(userCommandParsed);
                    break;
                case "14":
                    System.out.println("===Удаление товара===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер товара в таблице: ");
                    prodId = scaner.nextLine();
                    cmd = "Products delete "+prodId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForProducts(userCommandParsed);
                    break;
                case "15":
                    System.out.println("===Удаление заказа===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер заказа: ");
                    ordId = scaner.nextLine();
                    cmd = "Orders delete "+ordId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrders(userCommandParsed);
                    break;
                case "16":
                    System.out.println("===Удаление Товар многие-ко-многим Заказ===");
                    scaner = new Scanner(System.in);
                    System.out.println("Введите номер Товар м-к-м Заказ: ");
                    opId = scaner.nextLine();
                    cmd = "OrdersProducts delete "+opId;
                    userCommandParsed = cmd.split("\\s+");
                    executeForOrdersProducts(userCommandParsed);
                    break;
                case "help":
                    Info();
                default:
                    System.out.println("Введите номер команды. Введите help для вывода справочной информации");
                    break;
            }
        }
    }
}

