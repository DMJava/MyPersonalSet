package com.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class MySet <E> extends AbstractSet<E> implements Set<E>, Serializable, Cloneable{
    private static final Object PRESENT = new Object();

    private transient HashMap<E, Object> map;

    public MySet() {
        map = new HashMap<>();
    }

    public MySet(int capacity) {
        map = new HashMap<>(capacity);
    }

    public MySet(Collection<? extends E> collection) {
        map = new HashMap<>(Math.max((int) (collection.size() / .75f) + 1, 16));
        addAll(collection);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    public boolean add(E e) {
        if (!map.containsKey(e)) {
            map.put(e, PRESENT);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Object clone() {
        try {
            MySet<E> cloneSet = new MySet<>();
            cloneSet.addAll(this);
            cloneSet.map = (HashMap<E, Object>) map.clone();
            return cloneSet;
        } catch (Throwable e) {
            throw new InternalError(e);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        int capacity = HashMapReflectionHelper.callHiddenMethod(map, "capacity");
        float loadFactor = HashMapReflectionHelper.callHiddenMethod(map, "loadFactor");
        int size = map.size();
        objectOutputStream.writeInt(capacity);
        objectOutputStream.writeFloat(loadFactor);
        objectOutputStream.writeInt(size);
        Set<E> set = map.keySet();
        for (E element : set) {
            objectOutputStream.writeObject(element);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int capacity = objectInputStream.readInt();
        float loadFactor = objectInputStream.readFloat();
        int size = objectInputStream.readInt();
        map = new HashMap<>(capacity, loadFactor);
        for (int i = 0; i < size; i++) {
            map.put((E) objectInputStream.readObject(), PRESENT);
        }
    }
}