package com.jmt;

/**
 * Created by jtappe on 4/3/2014.
 */
public class Vertex<E> implements Identifiable
{
    private static int nextVertexID = 1;
    private int vUID;

    private E data;

    private IntegerSet edgesIn, edgesOut, edgesAll;


    public Vertex(E data)
    {
        this.data = data;
        vUID = nextVertexID++;


        edgesIn = new IntegerSet();
        edgesOut = new IntegerSet();
        edgesAll = new IntegerSet();
    }

    @Override
    public int getUID()
    {
        return vUID;
    }


    public boolean addEdgeIn(int eID)
    {
        boolean ret = edgesIn.add(eID);

        if (ret)
            edgesAll.add(eID);

        return ret;
    }

    public boolean addEdgeOut(int eID)
    {
        boolean ret = edgesOut.add(eID);
        if (ret)
            edgesAll.add(eID);
        return ret;
    }

    public IntegerSet getEdges()
    {
        return edgesAll;
    }

    public IntegerSet getEdgesIn()
    {
        return edgesIn;
    }

    public IntegerSet getEdgesOut()
    {
        return edgesOut;
    }

    public E getData()
    {
        return data;
    }
}
