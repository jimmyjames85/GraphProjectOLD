package com.jmt;

import com.jmt.Graph;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jim on 4/2/14.
 */
public class MyGraph<V, E> implements Graph<V, E>
{


    private IdentifiableSet vertexList;
    private IdentifiableSet edgeList;


    public MyGraph()
    {
        vertexList = new IdentifiableSet();
        edgeList = new IdentifiableSet();

    }


    private Vertex<V> getVertex(int vID) throws IllegalArgumentException
    {
        Vertex<V> v = (Vertex<V>) vertexList.findByUID(vID);

        if (v == null)
            throw new IllegalArgumentException("No such Vertex ID " + vID);

        return v;
    }

    private Edge<E> getEdge(int eID) throws IllegalArgumentException
    {
        Edge<E> e = (Edge<E>) edgeList.findByUID(eID);

        if (e == null)
            throw new IllegalArgumentException("No such Edge ID " + eID);

        return e;
    }


    @Override
    public int addVertex(V data)
    {
        Vertex v = new Vertex(data);
        vertexList.add(v);
        return v.getUID();
    }


    //BIGO 2*lg(n)
    @Override
    public int addEdge(int srcID, int targetID, E attr) throws IllegalArgumentException
    {
        //getVertex() will throw IllegalArgumentException if srcID or targetID's are not found
        Vertex<V> src = getVertex(srcID);
        Vertex<V> target = getVertex(targetID);


        Edge<E> e = new Edge(src.getUID(), target.getUID(), attr);
        int eID = e.getUID();

        //TODO these return true/false but new edge should be unique
        //TODO is it possible these vertices already have these edges registered
        src.addEdgeOut(eID);
        target.addEdgeIn(eID);
        edgeList.add(e);

        return e.getUID();
    }

    @Override
    public Set<Integer> getVertices()
    {
        return vertexList.getIDList();
    }

    @Override
    public Set<Integer> getEdges()
    {
        return edgeList.getIDList();
    }

    @Override
    public E getAttribute(int eID) throws IllegalArgumentException
    {
        return getEdge(eID).getAttr();
    }


    @Override
    public V getData(int vID) throws IllegalArgumentException
    {
        return getVertex(vID).getData();
    }

    @Override
    public int getSource(int eID) throws IllegalArgumentException
    {
        return getEdge(eID).getSourceID();
    }

    @Override
    public int getTarget(int eID) throws IllegalArgumentException
    {
        return getEdge(eID).getTargetID();
    }

    @Override
    public Set<Integer> getEdgesOf(int vID) throws IllegalArgumentException
    {
        return getVertex(vID).getEdges();
    }


}
