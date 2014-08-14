package com.fight2.util;

public interface IAsyncCallback {
    // ===========================================================
    // Methods
    // ===========================================================
    public abstract void workToDo();

    public abstract void onComplete();

}