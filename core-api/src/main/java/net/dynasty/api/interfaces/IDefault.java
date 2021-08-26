package net.dynasty.api.interfaces;

public interface IDefault<T> {

    void load(T key);

    void update();

    T getUniqueId();

}
