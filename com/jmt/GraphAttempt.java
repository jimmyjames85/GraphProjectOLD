package com.jmt;

/**
 * Created by jim on 3/31/14.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Stack;

public class GraphAttempt<E extends Comparable<? super E>>
{

    private boolean isDirected;
    private int searchTime;

    public ArrayList<Node> vertices;

    private ArrayList<NodeProcessor<E>> processors;

    public GraphAttempt()
    {
        searchTime = 0;
        isDirected = false;
        vertices = new ArrayList<Node>();
        processors = new ArrayList<GraphAttempt.NodeProcessor<E>>();
    }

    public void addNodeProcessor(NodeProcessor<E> proc)
    {
        processors.add(proc);
    }

    public ArrayList<E> findShortestPath(E from, E to)
    {
        initializeSearch();
        BFS(from);

        ArrayList<E> ret = new ArrayList<E>();
        Node cur = findVertex(to);

        ret.add(cur.data);
        while (cur.parent != null)
        {
            cur = cur.parent;
            ret.add(cur.data);
        }
        return ret;
    }

    public void topologicalSort()
    {
        if (isCyclic())
            return;

        NodeProcessor<E> proc = new NodeProcessor<E>()
        {
            String ret = "";

            public void processEarly(Node v)
            {
            }

            public void processEdge(Node from, Node to)
            {
            }

            public void processLate(Node v)
            {
                ret += " " + v.data;
            }

            @Override
            public String toString()
            {
                return ret;
            }

        };

        addNodeProcessor(proc);

        initializeSearch();
        doExhaustiveDFS();
        System.out.println(proc.toString());

/*
* // Sort by process time sortNodeList(vertices, SORTBY_PROCESSTIME);
*
* // Save result by reverse process time ArrayList<E> ret = new
* ArrayList<E>(); for (int i = vertices.size() - 1; i >= 0; i--)
* ret.add(vertices.get(i).data);
*
* // resort by data sortNodeList(vertices, SORTBY_DATA);
*/
        // return result

    }

    private static final int SORTBY_DATA = 1;
    private static final int SORTBY_PROCESSTIME = 2;
    private static final int SORTBY_DISCOVERTIME = 4;

    private void sortNodeList(ArrayList<Node> list, int sortBy)
    {
        sortNodeListRec(list, 0, list.size() - 1, sortBy);
    }

    private void sortNodeListRec(ArrayList<Node> list, int left, int right, int sortBy)
    {
        if (left >= right)
            return;

        int mid = (left + right) / 2;

        sortNodeListRec(list, left, mid, sortBy);
        sortNodeListRec(list, mid + 1, right, sortBy);

        {
            ArrayList<Node> copy = new ArrayList<GraphAttempt<E>.Node>();

            // MERGE
            int a = left;
            int b = mid + 1;

            while (a <= mid && b <= right)
            {
                int cmp;
                if (sortBy == SORTBY_DATA)
                    cmp = list.get(a).data.compareTo(list.get(b).data);
                else if (sortBy == SORTBY_PROCESSTIME)
                    cmp = list.get(a).processTime - list.get(b).processTime;
                else if (sortBy == SORTBY_DISCOVERTIME)
                    cmp = list.get(a).discoverTime - list.get(b).discoverTime;
                else
                    throw new IllegalArgumentException();

                if (cmp < 0)
                    copy.add(list.get(a++));
                else
                    copy.add(list.get(b++));
            }
            while (a <= mid)
                copy.add(list.get(a++));
            while (b <= right)
                copy.add(list.get(b++));

            // Put back
            int c = 0;
            for (int i = left; i <= right; i++)
                list.set(i, copy.get(c++));
        }

    }

    public void doExhaustiveDFS()
    {
        initializeSearch();
        for (int i = 0; i < vertices.size(); i++)
        {
            Node cur = vertices.get(i);
            if (cur.state == Node.UNDISCOVERED)
                DFS(cur.data);
        }
    }

    /**
     * Depth First Search
     * <p/>
     * Not exhaustive sets node discover and process times accordingly
     *
     * @param start
     * @throws NoSuchElementException
     */
    private void DFS(E start) throws NoSuchElementException
    {
        Node parent = null;

        Node cur = findVertex(start);
        if (cur == null)
            throw new NoSuchElementException();

        Stack<Node> stack = new Stack<GraphAttempt<E>.Node>();
        stack.push(cur);

        while (!stack.isEmpty())
        {
            cur = stack.peek();
            if (cur.state == Node.UNDISCOVERED)
            {
                cur.parent = parent;
                // Visit Node
                cur.discoverTime = ++searchTime;
                cur.state = Node.DISCOVERED;
                processEarly(cur);
            }

            ArrayList<Node> neighbors = cur.neighbors;

            // Every node has a marker to check which children it has searched
            // we continue our search from where ever we left off
            //
            // we are searching for the next undiscovered child
            while (cur.curChild < cur.neighbors.size() && cur.neighbors.get(cur.curChild).state != Node.UNDISCOVERED)
            {

                // we still want to process all edges
                // we must do that if
                // the child is not our parent
                // &&
                // we are not the child's parent
                // &&
                // (the child has not yet been proccessed OR we are directed
                // graph)
                if ((cur.parent != neighbors.get(cur.curChild) && neighbors.get(cur.curChild).parent != cur) && (neighbors.get(cur.curChild).state != Node.PROCESSED || isDirected))
                    processEdge(cur, neighbors.get(cur.curChild));

                cur.curChild++;
            }

            // if we found a non-parent, non-processed neighbor then lets
            // explore it
            if (cur.curChild < cur.neighbors.size())
            {

                processEdge(cur, neighbors.get(cur.curChild));
                if (neighbors.get(cur.curChild).state == Node.UNDISCOVERED)
                {
                    parent = cur;
                    stack.push(neighbors.get(cur.curChild));
                }
            }
            else
            // all neighbors have been visited and we are done
            {
                // Complete Visit
                cur.processTime = ++searchTime;
                cur.state = Node.PROCESSED;
                processLate(cur);

                stack.pop();
            }
        }

    }

    public void doExhaustiveBFS()
    {
        initializeSearch();
        for (int i = 0; i < vertices.size(); i++)
        {
            Node cur = vertices.get(i);
            if (cur.state == Node.UNDISCOVERED)
                BFS(cur.data);
        }
    }

    public int countEdges()
    {

        NodeProcessor<E> tester = new NodeProcessor<E>()
        {
            int edges = 0;

            public void processEarly(Node v)
            {
            }

            public void processEdge(Node from, Node to)
            {
                edges++;
            }

            public void processLate(Node v)
            {

            }

            @Override
            public int hashCode()
            {
                return edges;
            }

        };

        addNodeProcessor(tester);
        initializeSearch();
        doExhaustiveBFS();
        return tester.hashCode();
    }

    public boolean isCyclic()
    {
        NodeProcessor<E> tester = new NodeProcessor<E>()
        {
            public boolean cyclic = false;

            public void processEarly(GraphAttempt<E>.Node v)
            {
            }

            public void processEdge(Node from, Node to)
            {
                if (to.getState() == Node.DISCOVERED)
                    cyclic = true;
            }

            public void processLate(Node v)
            {
            }

            // A way to access the results
            public boolean equals(Object obj)
            {
                return cyclic;
            }

        };
        addNodeProcessor(tester);
        doExhaustiveDFS();
        return tester.equals(true);
    }

    /**
     * Breadth First Search
     * <p/>
     * Not exhaustive sets node discover and process times accordingly
     *
     * @param start
     */
    private void BFS(E start) throws NoSuchElementException
    {

        Node cur = findVertex(start);
        if (cur == null)
            throw new NoSuchElementException();

        // Visit starting node
        cur.parent = null;
        cur.discoverTime = ++searchTime;
        cur.state = Node.DISCOVERED;

        ArrayDeque<Node> queue = new ArrayDeque<GraphAttempt<E>.Node>();
        queue.add(cur);

        while (!queue.isEmpty())
        {

            cur = queue.pop();
            processEarly(cur);
            ArrayList<Node> neighbors = cur.neighbors;

            // neighbor list is sorted in order
            for (int i = 0; i < neighbors.size(); i++)
            {
                Node curNey = neighbors.get(i);

                if (curNey.state != Node.PROCESSED || isDirected)
                    processEdge(cur, curNey);

                if (curNey.state == Node.UNDISCOVERED)
                {
                    curNey.state = Node.DISCOVERED;
                    curNey.discoverTime = ++searchTime;
                    curNey.parent = cur;
                    queue.add(curNey);
                }
            }

            // we've processed this node now
            cur.processTime = ++searchTime;
            cur.state = Node.PROCESSED;
            processLate(cur);

        }

    }

    private void processEarly(Node v)
    {
        for (int i = 0; i < processors.size(); i++)
            processors.get(i).processEarly(v);
    }

    private void processLate(Node v)
    {
        for (int i = 0; i < processors.size(); i++)
            processors.get(i).processLate(v);
    }

    private void processEdge(Node from, Node to)
    {
        for (int i = 0; i < processors.size(); i++)
            processors.get(i).processEdge(from, to);
    }

    /**
     * resets every nodes discover and process times and resets their states to
     * undiscovered
     */
    private void initializeSearch()
    {
        searchTime = 0;
        for (int i = 0; i < vertices.size(); i++)
        {
            Node cur = vertices.get(i);
            cur.curChild = 0;
            cur.state = Node.UNDISCOVERED;
            cur.discoverTime = cur.processTime = -1;
            cur.parent = null;
        }
    }

    // adds node and sorts node and resorts-list
    public boolean addVertex(E v)
    {
        if (hasVertex(v))
            return false;

        vertices.add(new Node(v));

        sortNodeListRec(vertices, 0, vertices.size() - 1, SORTBY_DATA);
        return true;
    }

    /**
     * @param u from
     * @param v to
     * @return
     */
    public boolean addDirectedEdge(E u, E v)
    {
        isDirected = true;
        Node nodeU = findVertex(u);
        Node nodeV = findVertex(v);

        if (nodeU == null || nodeV == null)
            return false;

        nodeU.addNeighbor(nodeV);
        return true;
    }

    /**
     * Technically adds two directed edges
     *
     * @param u
     * @param v
     * @return
     */
    public boolean addUndirectedEdge(E u, E v)
    {
        Node nodeA = findVertex(u);
        Node nodeB = findVertex(v);

        if (nodeA == null || nodeB == null)
            return false;

        nodeA.addNeighbor(nodeB);
        nodeB.addNeighbor(nodeA);
        return true;
    }

    public boolean hasVertex(E v)
    {
        return findVertex(v) != null;

    }

    private Node findVertex(E v)
    {
        return findRec(v, 0, vertices.size() - 1);
    }

    private Node findRec(E elem, int left, int right)
    {

        if (left > right)
            return null;
        else if (left == right && elem.compareTo(vertices.get(left).data) != 0)
            return null;

        int mid = (left + right) / 2;

        int cmp = elem.compareTo(vertices.get(mid).data);

        if (cmp == 0)
            return vertices.get(mid);
        else if (cmp < 0)
            return findRec(elem, left, mid);
        else
            return findRec(elem, mid + 1, right);

    }

    public void removeDeg2()
    {
        String out = "";
        for (int i = 0; i < vertices.size(); i++)
        {

            Node n = vertices.get(i);
            if (n.neighbors.size() == 2)
                out += " " + n.data;

        }
        System.out.println("Delete List: " + out);

        NodeProcessor<E> proc = new NodeProcessor<E>()
        {
            @Override
            public void processEarly(Node v)
            {
                v.delete = v.neighbors.size() == 2;
            }

            @Override
            public void processEdge(Node from, Node to)
            {
            }

            @Override
            public void processLate(Node v)
            {
                if (v.delete)
                {
                    // if v was marked for deletion than v has at most 2
                    // vertices
                    // it is possible that a prior deletion caused the deg(v) to
                    // be less than 2
                    if (v.neighbors.size() == 2)
                    {
                        // in the first case we know we have only two neighboors
                        // a and b
                        Node a = v.neighbors.get(0);
                        Node b = v.neighbors.get(1);

                        // let us remove v from a and b's set of neighbors
                        // and replace with a new edge
                        if (!a.neighbors.contains(b))
                        {
                            // Degrees don't change
                            int replaceIndexA = a.neighbors.indexOf(v);
                            int replaceIndexB = b.neighbors.indexOf(v);
                            a.neighbors.set(replaceIndexA, b);
                            b.neighbors.set(replaceIndexB, a);
                        }
                        else
                        {
                            // Degrees will change

                            // In this case the new replacement edge will be a
                            // duplicate
                            // so all we need to do is remove v from a and b's
                            // set of neighbors
                            int aIndexOfV = a.neighbors.indexOf(v);
                            int bIndexOfV = b.neighbors.indexOf(v);
                            a.neighbors.remove(aIndexOfV);
                            b.neighbors.remove(bIndexOfV);

                            if (a.curChild > aIndexOfV)
                                a.curChild--;
                            if (b.curChild > bIndexOfV)
                                b.curChild--;
                        }

                    }
                    else if (v.neighbors.size() == 1)
                    {
                        Node a = v.neighbors.get(0);

                        // we need to remove v from a's set of neighbors
                        int aIndexOfV = a.neighbors.indexOf(v);
                        a.neighbors.remove(aIndexOfV);
                        if (a.curChild > aIndexOfV)
                            a.curChild--;
                    }
                    // else no neighbors nothing to update

                    vertices.remove(vertices.indexOf(v));

                }
            }

        };

        addNodeProcessor(proc);
        initializeSearch();
        doExhaustiveDFS();

        for (int i = 0; i < vertices.size(); i++)
        {
            Node cur = vertices.get(i);
            System.out.print(cur.data + " [");
            for (int n = 0; n < cur.neighbors.size(); n++)
                System.out.print(" " + cur.neighbors.get(n).data);
            System.out.println(" ]");
        }
    }

    public interface NodeProcessor<T extends Comparable<? super T>>
    {
        void processEarly(GraphAttempt<T>.Node v);

        void processEdge(GraphAttempt<T>.Node from, GraphAttempt<T>.Node to);

        void processLate(GraphAttempt<T>.Node v);
    }

    public class Node
    {
        public static final int UNDISCOVERED = 1;
        public static final int DISCOVERED = 2;
        public static final int PROCESSED = 4;

        private E data;
        private ArrayList<Node> neighbors;

        // used to keep track of which children it has searched
        protected int curChild;
        protected int state;
        protected Node parent;

        // TODO delete
        protected boolean delete;

        protected int discoverTime, processTime;

        public Node(E data)
        {
            this.data = data;
            neighbors = new ArrayList<GraphAttempt<E>.Node>();

            state = UNDISCOVERED;
            discoverTime = processTime = 0;
            parent = null;
            delete = false;
        }

        /**
         * These are out degree neighbors
         *
         * @param n
         */
        protected void addNeighbor(Node n)
        {
            if (!neighbors.contains(n))
                neighbors.add(n);

            sortNodeListRec(neighbors, 0, neighbors.size() - 1, SORTBY_DATA);
        }

        public E getData()
        {
            return data;
        }

        public int getState()
        {
            return state;
        }

        public Node getParent()
        {
            return parent;
        }

        public int getDiscoverTime()
        {
            return discoverTime;
        }

        public int getProcessTime()
        {
            return processTime;
        }
    }

    public static <E extends Comparable<? super E>> void addEdges(E[] arr, GraphAttempt<E> g)
    {
        for (int i = 0; i < arr.length; i += 2)
            g.addUndirectedEdge(arr[i], arr[i + 1]);

    }

    public static void main(String[] args)
    {

        GraphAttempt<Character> g1 = new GraphAttempt<Character>();
        for (char i = 'A'; i <= 'J'; i++)
            g1.addVertex(i);

        Character a[] = {'A', 'I', 'A', 'D', 'A', 'B', 'B', 'C', 'B', 'D', 'B', 'E', 'C', 'E', 'C', 'F', 'D', 'E', 'D', 'G', 'E', 'F', 'E', 'G', 'E', 'H', 'F', 'H', 'G', 'H', 'G', 'I', 'G', 'J', 'H', 'J', 'I', 'J'};
        addEdges(a, g1);


        GraphAttempt<Character> g2 = new GraphAttempt<Character>();
        for (char i = 'A'; i <= 'P'; i++)
            g2.addVertex(i);

        g2.addUndirectedEdge('A', 'B');
        g2.addUndirectedEdge('A', 'E');
        g2.addUndirectedEdge('B', 'C');
        g2.addUndirectedEdge('B', 'F');
        g2.addUndirectedEdge('C', 'G');
        g2.addUndirectedEdge('C', 'D');
        g2.addUndirectedEdge('D', 'H');
        g2.addUndirectedEdge('E', 'F');
        g2.addUndirectedEdge('E', 'I');
        g2.addUndirectedEdge('F', 'G');
        g2.addUndirectedEdge('F', 'J');
        g2.addUndirectedEdge('G', 'K');
        g2.addUndirectedEdge('G', 'H');
        g2.addUndirectedEdge('H', 'L');
        g2.addUndirectedEdge('I', 'J');
        g2.addUndirectedEdge('I', 'M');
        g2.addUndirectedEdge('J', 'K');
        g2.addUndirectedEdge('J', 'N');
        g2.addUndirectedEdge('K', 'L');
        g2.addUndirectedEdge('K', 'O');
        g2.addUndirectedEdge('L', 'P');
        g2.addUndirectedEdge('M', 'N');
        g2.addUndirectedEdge('N', 'O');
        g2.addUndirectedEdge('O', 'P');

        GraphAttempt<Character> g3 = new GraphAttempt<Character>();
        for (char i = 'A'; i <= 'H'; i++)
            g3.addVertex(i);

        g3.addUndirectedEdge('A', 'G');
        g3.addUndirectedEdge('A', 'B');
        g3.addUndirectedEdge('A', 'D');
        g3.addUndirectedEdge('B', 'F');
        g3.addUndirectedEdge('B', 'E');
        g3.addUndirectedEdge('C', 'H');
        g3.addUndirectedEdge('C', 'F');
        g3.addUndirectedEdge('D', 'F');
        g3.addUndirectedEdge('E', 'G');

        GraphAttempt<Integer> t1 = new GraphAttempt<Integer>();
        for (int i = 0; i <= 6; i++)
            t1.addVertex(i);

        t1.addDirectedEdge(0, 1);
        t1.addDirectedEdge(0, 5);
        t1.addDirectedEdge(0, 2);
        t1.addDirectedEdge(1, 4);
        t1.addDirectedEdge(5, 2);
        t1.addDirectedEdge(3, 2);
        t1.addDirectedEdge(3, 6);
        t1.addDirectedEdge(3, 4);
        t1.addDirectedEdge(3, 5);
        t1.addDirectedEdge(6, 4);
        t1.addDirectedEdge(6, 0);

        GraphAttempt<Character> t2 = new GraphAttempt<Character>();
        for (char i = 'A'; i <= 'J'; i++)
            t2.addVertex(i);

        t2.addDirectedEdge('A', 'B');
        t2.addDirectedEdge('A', 'D');
        t2.addDirectedEdge('B', 'C');
        t2.addDirectedEdge('B', 'D');
        t2.addDirectedEdge('B', 'E');
        t2.addDirectedEdge('C', 'F');
        t2.addDirectedEdge('D', 'E');
        t2.addDirectedEdge('D', 'G');
        t2.addDirectedEdge('E', 'C');
        t2.addDirectedEdge('E', 'F');
        t2.addDirectedEdge('E', 'G');
        t2.addDirectedEdge('G', 'F');
        t2.addDirectedEdge('G', 'I');
        t2.addDirectedEdge('H', 'G');
        t2.addDirectedEdge('H', 'J');
        t2.addDirectedEdge('H', 'F');
        t2.addDirectedEdge('I', 'J');

        GraphAttempt<Integer> bip = new GraphAttempt<Integer>();
        for (int i = 0; i < 11; i++)
            bip.addVertex(i);

        bip.addDirectedEdge(0, 1);
        bip.addDirectedEdge(1, 2);
        bip.addDirectedEdge(2, 3);
        bip.addDirectedEdge(3, 4);
        bip.addDirectedEdge(4, 5);
        bip.addDirectedEdge(5, 6);
        bip.addDirectedEdge(6, 7);
        bip.addDirectedEdge(7, 8);
        bip.addDirectedEdge(8, 9);
        bip.addDirectedEdge(9, 10);

        GraphAttempt<Integer> c5 = new GraphAttempt<Integer>();
        for (int i = 0; i < 12; i++)
            c5.addVertex(i);

        c5.addUndirectedEdge(0, 1);
        c5.addUndirectedEdge(0, 4);
        c5.addUndirectedEdge(1, 2);
        c5.addUndirectedEdge(1, 7);
        c5.addUndirectedEdge(1, 8);
        c5.addUndirectedEdge(2, 3);
        c5.addUndirectedEdge(3, 4);
        c5.addUndirectedEdge(3, 6);
        c5.addUndirectedEdge(3, 9);
        c5.addUndirectedEdge(3, 10);
        c5.addUndirectedEdge(3, 11);
        c5.addUndirectedEdge(4, 5);
        c5.addUndirectedEdge(5, 6);
        c5.addUndirectedEdge(7, 8);
        c5.addUndirectedEdge(9, 10);
        c5.addUndirectedEdge(10, 11);

        // c5.removeDeg2();

        // t1.topologicalSort();
        // System.out.println(g1.countEdges());

        GraphAttempt<String> kids = new GraphAttempt<String>();
        kids.addVertex("John");
        kids.addVertex("Jim");
        kids.addVertex("Dustin");
        kids.addVertex("Cort");
        kids.addVertex("Rikki");
        kids.addVertex("Buddy");
        kids.addVertex("Jack");
        kids.addVertex("Charlie");
        kids.addVertex("Bruce");

        kids.addDirectedEdge("John", "Jim");
        kids.addDirectedEdge("John", "Rikki");
        kids.addDirectedEdge("John", "Bruce");
        kids.addDirectedEdge("Bruce", "Rikki");
        kids.addDirectedEdge("Rikki", "Jim");
        kids.addDirectedEdge("Cort", "Charlie");
        kids.addDirectedEdge("Charlie", "Bruce");
        kids.addDirectedEdge("Dustin", "Jack");
        kids.addDirectedEdge("Jack", "Jim");
        kids.addDirectedEdge("Buddy", "Jim");

        kids.topologicalSort();
        // System.out.println(kids.isCyclic());

        // System.out.println("G1 is " + (g1.isBipartite() ? "" : "not ") +
        // "bipartite.");

        // g3.isBipartite();

/*
* ArrayList<Character> path = g2.findShortestPath('A', 'P'); for (int i
* = 0; i < path.size(); i++) System.out.print(path.get(i) + " ");
*/

        t2.topologicalSort();

        NodeProcessor<Character> proc = new NodeProcessor<Character>()
        {

            int tabs = 0;

            private String tabs()
            {
                String ret = "";
                for (int i = 0; i < tabs; i++)
                    ret += "\t";
                return ret;
            }

            @Override
            public void processEarly(GraphAttempt<Character>.Node v)
            {
                System.out.println(tabs() + "$" + v.data);
                tabs++;
            }

            @Override
            public void processEdge(GraphAttempt<Character>.Node from, GraphAttempt<Character>.Node to)
            {
                System.out.println(tabs() + from.data + "->" + to.data);
            }

            @Override
            public void processLate(GraphAttempt<Character>.Node v)
            {
                tabs--;
                System.out.println(tabs() + "!" + v.data);
            }
        };


        //g2.addNodeProcessor(proc);
        g2.doExhaustiveBFS();

    }
}