module com.example.eventmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires java.naming;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires java.sql;


    // 1. Allow JavaFX to access your controllers
    opens com.example.eventmanager.controller to javafx.fxml;

    // 2. Allow hbrnate to access your entities
    opens com.example.eventmanager.entity to org.hibernate.orm.core;
    // 3. Allow JavaFX to access the resources folder

    opens fxml to javafx.fxml;

    exports com.example.eventmanager;
    exports com.example.eventmanager.controller;
}