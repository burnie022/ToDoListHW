package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    private String description;
    private String dueDate;
    //added selection
    private String selection;

    //added selection to constructor and added getter/setter
    public ToDoItem(String description, String dueDate, String selection) {
        this.description = description;
        this.dueDate = dueDate;
        this.selection = selection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }
}
