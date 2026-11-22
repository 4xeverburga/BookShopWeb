package com.bookshopweb.utils;

public interface ConstantUtils {
    int DB_PORT = Integer.getInteger("bookshop.db.port", 3306);
    String SERVER_NAME = System.getProperty("bookshop.db.host", "localhost");
    String DB_NAME = System.getProperty("bookshop.db.name", "bookshopdb");
    String DB_USERNAME = System.getProperty("bookshop.db.user", "root");
    String DB_PASSWORD = System.getProperty("bookshop.db.password", "12345");
    String IMAGE_PATH = System.getProperty("bookshop.image.path", "/var/webapp/images"); // Dùng "C:\\var\\webapp\\images" trên Windows
}
