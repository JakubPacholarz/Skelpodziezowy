package com.example.sklep;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    if (item instanceof Long) {
                        long value = (Long) item;
                        long hours = value / 3600;
                        long minutes = (value % 3600) / 60;
                        long seconds = value % 60;
                        setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    } else {
                        setText(item.toString());
                    }
                }
            }
        };
    }
}
