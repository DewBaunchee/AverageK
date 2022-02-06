module by.varyvoda.matvey.averagek {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens by.varyvoda.matvey.averagek to javafx.fxml;
    exports by.varyvoda.matvey.averagek;
}