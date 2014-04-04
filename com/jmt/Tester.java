package com.jmt;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by jtappe on 4/3/2014.
 */
public class Tester
{

    public static void testMyGraph()
    {

        System.out.println("Hello, Graph World!!");

        MyGraph<Point, String> g = new MyGraph<Point, String>();


        g.addVertex(new Point(0, 2));
        g.addVertex(new Point(4, 8));
        g.addVertex(new Point(2, 234));
        g.addVertex(new Point(23, 23));
        g.addVertex(new Point(221, 25));


        g.addEdge(1, 5, "1 TO 5");
        g.addEdge(5, 1, "5 TO 1");
        g.addEdge(2, 5, "2 TO 5");
        g.addEdge(3, 4, "3 TO 4");
        int edgeFive = g.addEdge(4, 5, "4 TO 5");


        Iterator<Integer> itr = g.getVertices().iterator();

        while (itr.hasNext())
        {
            int nextID = itr.next();
            int x = g.getData(nextID).x;
            int y = g.getData(nextID).y;
            System.out.println("VID: " + nextID + "\t(" + x + "," + y + ")");
        }

        itr = g.getEdges().iterator();
        while (itr.hasNext())
        {
            int nextID = itr.next();
            System.out.println("EID: " + nextID + "\t'" + g.getAttribute(nextID) + ";");
        }

        System.out.println("*************");


        itr = g.getEdgesOf(edgeFive).iterator();
        while (itr.hasNext())
        {
            int nextId = itr.next();

            System.out.println("EID " + nextId + "\t'" + g.getAttribute(nextId) + "'");
        }


    }


    public static ArrayList<String> inputStringToParsableArray(String str)
    {
        ArrayList<String> ret = new ArrayList<String>();

        int left = 0;
        int right = str.indexOf(',');
        if (right == -1)
            right = str.length();

        while (left < right)
        {
            ret.add(str.substring(left, right).trim());
            left = right + 1;
            right = str.indexOf(',', right + 1);
            if (right == -1)
                right = str.length();
        }
        return ret;
    }

    public static double[] inputStringToVertex(String str)
    {
        int comma1 = str.indexOf(',');
        int comma2 = str.indexOf(',', comma1 + 1);

        int vid = Integer.parseInt(str.substring(0, comma1).trim());
        double lat = Double.parseDouble(str.substring(comma1 + 1, comma2).trim());
        double lon = Double.parseDouble(str.substring(comma2 + 1, str.length()));

        double[] ret = new double[3];

        ret[0] = vid;
        ret[1] = lat;
        ret[2] = lon;

        return ret;
    }


    public static int getAssignedVID(ArrayList<Point> vidMapping, int fileVID)
    {
        int loc = findRecFileVID(vidMapping,fileVID,0,vidMapping.size()-1);

        if(loc<vidMapping.size() && vidMapping.get(loc).x == fileVID)
            return vidMapping.get(loc).y;

        throw new IllegalArgumentException("could not find " + fileVID);

    }

    //assumes vidMapping is sorted by fileVID
    private static int findRecFileVID(ArrayList<Point> vidMapping, int fileVID,int left, int right)
    {
        int mid = (left + right) / 2;

        if (mid > right)
            return mid;

        if (mid < left)
            return left;

        if (fileVID == vidMapping.get(mid).x)
            return mid;

        if (fileVID < mid)
            return findRecFileVID (vidMapping,fileVID, left, mid - 1);
        else
            return findRecFileVID(vidMapping,fileVID, mid + 1, right);
    }


    //if byFileVID is true than sort is by FileVID
    public static void sortCoordinateMapping(ArrayList<Point> vidMapping, boolean byFileVID)
    {
        recMergeSort(vidMapping, 0, vidMapping.size()-1, byFileVID);
    }

    public int getGIT()
    {
        return -1;
    }

    private static void recMergeSort(ArrayList<Point> arr, int left, int right, boolean byFileVID)
    {

        if (left >= right)
            return;

        //changes for git test


        int mid = (left + right) / 2;

        recMergeSort(arr, left, mid, byFileVID);
        recMergeSort(arr, mid + 1, right, byFileVID);

        ArrayList<Point> tmp = new ArrayList<Point>();
        int li = left;
        int ri = mid + 1;
        while (li <= mid && ri <= right)
        {
            boolean addLeft = true;
            if (byFileVID)
                addLeft = (arr.get(li).x < arr.get(ri).x);
            else
                addLeft = (arr.get(li).y < arr.get(ri).y);

            if (addLeft)
                tmp.add(arr.get(li++));
            else
                tmp.add(arr.get(ri++));
        }
        while (li <= mid)
            tmp.add(arr.get(li++));
        while (ri <= right)
            tmp.add(arr.get(ri++));

        //transfer tmp to orig
        for (int i = 0; i < tmp.size(); i++)
            arr.set(left++, tmp.get(i));//replace


    }


    public static MyGraph<Coordinate, String> openGraph(String file)
    {
        /*
        VERTICES: <NUM VERTICS>
        <VERTEX ID>,<LATTITUDE>,<LONGITUDE>
        ...
        EDGES: <NUM EDGES>
        <VID SOURCE>,<VID TARGET>,<WEIGHT>[,<STREET NAME>]
        */


        MyGraph<Coordinate, String> graph = new MyGraph<Coordinate, String>();


        //Point p = (file VID, Graph assigned VID) //mapping
        ArrayList<Point> vidMapping = new ArrayList<Point>();

        try
        {
            Scanner scanner = new Scanner(new File(file));
            String curLine = scanner.nextLine();
            int totVerts = 0;
            if (curLine.startsWith("VERTICES:"))
                totVerts = Integer.parseInt(curLine.substring(9).trim());
            else
                return null;

            System.out.println(totVerts);

            for (int i = 0; i < totVerts; i++)
            {
                ArrayList<String> items = inputStringToParsableArray(scanner.nextLine());

                int fileVid = Integer.parseInt(items.get(0));
                double latitude = Double.parseDouble(items.get(1));
                double longitude = Double.parseDouble(items.get(2));


                //ADD VERTEX and store mapping
                int graphVid = graph.addVertex(new Coordinate(latitude, longitude));
                vidMapping.add(new Point(fileVid, graphVid));
            }


            //SORT vidMapping
            sortCoordinateMapping(vidMapping,true);





            int totEdges = 0;
            curLine = scanner.nextLine();
            if (curLine.startsWith("EDGES:"))
                totEdges = Integer.parseInt(curLine.substring(6).trim());
            else
                return null;





            for (int i = 0; i < totEdges; i++)
            {
                ArrayList<String> items = inputStringToParsableArray(scanner.nextLine());

                int vidSrc = Integer.parseInt(items.get(0));
                int vidTarget = Integer.parseInt(items.get(1));
                double weight = Double.parseDouble(items.get(2));
                String street = "";
                if (items.size() > 3)
                    street = items.get(3);

                graph.addEdge(getAssignedVID(vidMapping,vidSrc),getAssignedVID(vidMapping,vidTarget),weight + " " + street);

                //ADD EDGE

//                System.out.println(vidSrc + " -> " + vidTarget + " (" + weight + ") " + street);
            }


            while (scanner.hasNext())
            {
                System.out.println(scanner.nextLine());
            }

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        return null;
    }


    public static void main(String args[])
    {


        MyGraph<Coordinate, String> simpleGraph = openGraph("C:/Users/jtappe/IdeaProjects/GraphProject/src/simpleGraph.txt");

        //        MyGraph<Point, String> g1 = new MyGraph();


    }
}
