package com.jmt;


import java.util.Set;

/**
 * An interface for a graph in which every vertex and edge
 * is assigned a unique ID. In addition, every vertex has a data
 * object attached to it and every edge has an attribute attached
 * to it.
 *
 * @param <V> Vertex-data type
 * @param <E> Edge-attribute type
 */
public interface Graph<V,E>
{
    /**
     * Adds a new vertex to the graph
     *
     * @param v Data to attach to vertex
     * @return the ID of the vertex created
     */
    public int addVertex(V v);

    /**
     * Adds an edge to the graph.
     *
     * @param srcID The ID of the source vertex
     * @param targetID The ID of the target vertex
     * @param attr The attribute of the edge
     * @return The ID of the edge created
     * @throws IllegalArgumentException If srcID or targetID are not valid vertices
     */
    public int addEdge(int srcID, int targetID, E attr) throws IllegalArgumentException;

    /**
     * Returns the vertex-set of the graph
     */
    public Set<Integer> getVertices();

    /**
     * Returns the edge-set of the graph
     */
    public Set<Integer> getEdges();

    /**
     * Returns the attribute of the edgeID
     * @throws IllegalArgumentException if the id is invalid
     */
    public E getAttribute(int id) throws IllegalArgumentException;

    /**
     * Returns the data of the vertexID
     * @throws IllegalArgumentException if the id is invalid
     */
    public V getData(int id) throws IllegalArgumentException;

    /**
     * Returns the source of the edge
     * @throws IllegalArgumentException if the id is invalid
     */
    public int getSource(int id) throws IllegalArgumentException;

    /**
     * Returns the target of the edge
     * @throws IllegalArgumentException if the id is invalid
     */
    public int getTarget(int id) throws IllegalArgumentException;

    /**
     * Returns the outgoing edges of the vertex
     * @throws IllegalArgumentException if the id is invalid
     */
    public Set<Integer> getEdgesOf(int id) throws IllegalArgumentException;
}
