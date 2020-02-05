package org.server.test.infinispan.distributable;

import java.io.IOException;
import java.io.Serializable;

/**
 * A simple class to be used by the servlet in order to generate values 
 * to be stored by the session.
 */
public class Mutable implements Serializable {

    private static final long serialVersionUID = -5129400250276547619L;
    private transient boolean serialized = false;
    private int value;

    public Mutable(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void increment() {
        this.value += 1;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public boolean wasSerialized() {
        return this.serialized;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.serialized = true;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.serialized = true;
    }
}