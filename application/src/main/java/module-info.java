module application {
    requires kotlin.stdlib;
    requires javafx.controls;
    requires kotlinx.coroutines.core.jvm;
    requires shared;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires com.fasterxml.jackson.kotlin;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires java.desktop;
    requires javafx.swing;
    requires itextpdf;
    requires org.json;
    exports wb;
}