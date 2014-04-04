package com.jmt;



import java.util.List;

/**
 * An interface that represents a shortest path algorithm
 *
 * @param <V> the vertex-data type
 * @param <E> the edge-attribute type
 */
public interface Dijkstra<V,E>
{
    /**
     * Sets the graph to use in computation
     */
    public void setGraph(Graph<V,E> graph);

    /**
     * Sets the start vertex the algorithm will use in computation
     * @throws IllegalArgumentException if the start vertex does not exist in the graph
     * @throws IllegalStateException if no graph has been set
     */
    public void setStart(int startId) throws IllegalArgumentException, IllegalStateException;

    /**
     * Sets the weighing to be used in computing the cost of traversing an edge
     */
    public void setWeighing(Weighing<E> weighing);

    /**
     * Computes the shortest path to all vertices from the start vertex in the graph
     * using the weighing function
     * @throws IllegalStateException if the graph, start vertex, or weighing object have not been set
     */
    public void computeShortestPath() throws IllegalStateException;

    /**
     * Returns the path from the start vertex to the end vertex provided
     * @return a list representing the shortest path. The first element is the start vertex, and the last
     *         is the end vertex.
     * @throws IllegalArgumentException if endId is not a vertex in the graph
     * @throws IllegalStateException if computeShortestPath has not been called since any of the set methods
     *                               were last called
     */
    public List<Integer> getPath(int endId) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns the cost of the shortest path from the start vertex to the end vertex where
     * cost is defined by the sum of the weights of all the edges that connects the path as
     * defined by the weighing object.
     * @throws IllegalArgumentException if endId is not a vertex in the graph
     * @throws IllegalStateException if computeShortestPath has not been called since any of the set methods
     *                               were last called
     */
    public double getCost(int endId) throws IllegalArgumentException, IllegalStateException;
}
