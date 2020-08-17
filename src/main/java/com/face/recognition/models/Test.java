//package com.face.recognition.models;
//
//import java.sql.*;
//import java.util.*;
//import java.util.logging.Logger;
//
//public class Test {
//
//    private static final Logger log;
//
//    static {
//        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
//        log =Logger.getLogger(Test.class.getName());
//    }
//
//    public static void main(String[] args) throws Exception {
//        log.info("Loading application properties");
//        Properties properties = new Properties();
//        properties.load(Test.class.getClassLoader().getResourceAsStream("application.properties"));
//
//        log.info("Connecting to the database");
//        Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties);
//        log.info("Database connection test: " + connection.getCatalog());
//
////        log.info("Create database schema");
////        Scanner scanner = new Scanner(Test.class.getClassLoader().getResourceAsStream("schema.sql"));
////        Statement statement = connection.createStatement();
////        while (scanner.hasNextLine()) {
////            statement.execute(scanner.nextLine());
////        }
//
//
//		User user = new User((long) 1, "Vignesh", "123456789");
//        insertData(user, connection);
//        user = readData(connection);
//        /* todo.setDetails("congratulations, you have updated data!");
//        updateData(todo, connection);
//        deleteData(todo, connection);
//		*/
//
//        log.info("Closing database connection");
//        connection.close();
//    }
//
//    private static void insertData(User user, Connection connection) throws SQLException {
//        log.info("Insert data");
//        PreparedStatement insertStatement = connection
//                .prepareStatement("INSERT INTO [User] (UserName, Password) VALUES (?, ?);");
//
//        insertStatement.setString(1, user.getUserName());
//        insertStatement.setString(2, user.getPassword());
//        insertStatement.executeUpdate();
//    }
//
//    private static User readData(Connection connection) throws SQLException {
//        log.info("Read data");
//        PreparedStatement readStatement = connection.prepareStatement("SELECT * FROM [User];");
//        ResultSet resultSet = readStatement.executeQuery();
//        if (!resultSet.next()) {
//            log.info("There is no data in the database!");
//            return null;
//        }
//        User user = new User();
//        user.setUserID(resultSet.getLong("UserID"));
//        user.setUserName(resultSet.getString("UserName"));
//        user.setPassword(resultSet.getString("Password"));
//        log.info("Data read from the database: " + user.toString());
//        return user;
//    }
//}