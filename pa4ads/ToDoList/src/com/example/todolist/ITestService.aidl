package com.example.todolist;

import com.example.todolist.ToDoItem;

interface ITestService
{
    ToDoItem getToDoItem();
    void setToDoItem(in ToDoItem tdi);
}