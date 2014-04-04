package com.jmt;


/**
 * An interface that defines a weight distribution over
 * a set of edges to be used in routing algorithms.
 *
 * @param <E> the edge-attribute type
 */
public interface Weighing<E>
{
    /**
     * Computes the weight of the edge
     */
    public double weight(E edge);
}
