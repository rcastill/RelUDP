package net;

/**
 * Interface intended to iterate over a container of
 * <code>DataType</code>'s and give instructions
 * to each object.
 * @param <T> Container type
 */

@FunctionalInterface
public interface Instruction<T> {
    public abstract void instruct(T obj);
}
