package com.jmt;

import java.util.*;

/**
 * Created by jim on 4/1/14.
 */
public class IntegerSet implements Set<Integer>
{
    protected ArrayList<Integer> data;

    //returns the loc of the integer (or where it should be inserted)
    private int findRec(Integer integer, int left, int right)
    {
        int mid = (left + right) / 2;

        //here we didn't find integer and the new index should be right + 1 which is out of bounds but we can expand to the right
        if (mid > right)
            return mid;

        //we didn't find integer and the new index should be 'left' which is still in bounds
        if (mid < left)
            return left;

        int cmp = integer.compareTo(data.get(mid));

        if (cmp == 0)
            return mid;


        if (cmp < 0)
            return findRec(integer, left, mid - 1);
        else
            return findRec(integer, mid + 1, right);
    }

    public IntegerSet()
    {
        data = new ArrayList<Integer>();
    }


    public IntegerSet(int[] arr)
    {
        data = new ArrayList<Integer>();

        //TODO this is O(n*n)
        for (int i = 0; i < arr.length; i++)
            add(arr[i]);
    }


    @Override
    public int size()
    {
        return data.size();
    }

    @Override
    public boolean isEmpty()
    {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        if (o == null)
            throw new NullPointerException();
        if (!o.getClass().equals(Integer.class))
            throw new ClassCastException();

        Integer i = (Integer) o;
        int loc = findRec(i, 0, size() - 1);

        //findRec might return an out of bounds location i.e. size + 1
        if (loc >= size())
            return false;

        return (i.equals(data.get(loc)));

    }

    @Override
    public Iterator<Integer> iterator()
    {

        Iterator<Integer> itr = new Iterator<Integer>()
        {
            Iterator<Integer> itr = data.iterator();

            @Override
            public boolean hasNext()
            {
                return itr.hasNext();
            }

            @Override
            public Integer next()
            {
                return itr.next();
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };

        return itr;
    }

    @Override
    public Object[] toArray()
    {
        return data.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return data.toArray(a);
    }

    @Override
    public boolean add(Integer integer)
    {
        //find the insertion location
        int loc = findRec(integer, 0, size() - 1);

        //simply append integer to the end of the list
        if (loc >= size())
            return data.add(integer);

        //check if this set already contains the integer
        if (data.get(loc).equals(integer))
            return false;

        //insert new integer
        data.set(loc, integer);

        //success!
        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        if (o == null)
            throw new NullPointerException();
        if (!o.getClass().equals(Integer.class))
            throw new ClassCastException();


        int loc = findRec(((Integer) o).intValue(), 0, size() - 1);

        if (loc < size() && data.get(loc).equals(((Integer) o)))
        {
            //remove
            data.remove(loc);
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> objects)
    {
        Iterator<Object> itr = (Iterator<Object>) objects.iterator();
        boolean containsAll = true;

        while (containsAll && itr.hasNext())
            containsAll = containsAll && contains(itr.next());

        return containsAll;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> integers)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> objects)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> objects)
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();

    }
}