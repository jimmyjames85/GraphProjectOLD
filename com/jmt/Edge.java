package com.jmt;

/**
 * Created by jtappe on 4/3/2014.
 */
public class Edge<E> implements Identifiable
{

    private static int nextVertexID = 1;
    private int eUID;
    private E attr;

    private int srcID;
    private int targetID;


    public Edge(int srcID, int targetID, E attr)
    {
        eUID = nextVertexID++;

        this.srcID = srcID;
        this.targetID = targetID;
        this.attr = attr;
    }

    @Override
    public int getUID()
    {
        return eUID;
    }

    public int getSourceID()
    {
        return srcID;
    }

    public int getTargetID()
    {
        return targetID;
    }

    public E getAttr()
    {
        return attr;
    }
}
