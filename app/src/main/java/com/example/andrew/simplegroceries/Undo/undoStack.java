package com.example.andrew.simplegroceries.Undo;

import java.util.Stack;

/**
 * Created by Andrew on 9/6/2015.
 */

/* Undo feature is implemented by storing a stack of user actions.  Each time the user does anything,
* that action is added onto the stack.  When the user hits back, the program will pop the stack, and call
* that UserAction's undo()*/
public class UndoStack {
    Stack<UserAction> actionStack;

    public UndoStack() {
        actionStack = new Stack<UserAction>();
    }

    public void pushAction(UserAction userAction) {
        actionStack.push(userAction);
    }

    public boolean undo() {
        if (actionStack.isEmpty()) {
            return false;
        } else {
            actionStack.pop().undo();
            return true;
        }

    }


}
