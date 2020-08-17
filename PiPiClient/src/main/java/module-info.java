module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires socket.io.client;
    requires engine.io.client;
    requires com.jfoenix;
    requires javafx.graphics;
    requires javafx.base;
    requires json;
    requires emoji.java;


    opens org.example to javafx.fxml;
    opens org.example.AfterLogin to javafx.fxml;
    opens org.example.Prequals to javafx.fxml;
    opens org.example.AfterLogin.Games to javafx.fxml;
    opens org.example.AfterLogin.Games.SnakeAndLadder to javafx.fxml, javafx.graphics;
    opens org.example.AfterLogin.Games.TicTacToe to javafx.fxml, javafx.graphics;
    exports org.example.AfterLogin.Games.SnakeAndLadder;
    exports org.example.AfterLogin.Games;
    exports org.example.AfterLogin.Games.TicTacToe;
    exports org.example.Prequals;
    exports org.example.AfterLogin;
    exports org.example;
}