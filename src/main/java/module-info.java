module com.quark.chatformatter {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.fxmisc.richtext;

    opens com.quark.chatformatter to javafx.fxml;
    exports com.quark.chatformatter;
}