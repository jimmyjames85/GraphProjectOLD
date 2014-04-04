package com.jmt;

import java.util.*;

/**
 * Created by jim on 4/1/14.
 */
public class IntegerSetOLD implements Set<Integer>
{
    /**
     *
     */
    private static int ARRAY_PADDING = 2;

    private int[] data;
    private int size;

    private void resize()
    {
        int arrCapacity = data.length - ARRAY_PADDING;

        if (size * 2 > arrCapacity)
            doubleCapacity();
        else if (size / 2 < arrCapacity)
            halveCapacity();
    }

    private void doubleCapacity()
    {
        int[] newArr = new int[data.length * 2];
        for (int i = 0; i < size; i++)
            newArr[i] = data[i];

        data = newArr;
    }

    private void halveCapacity()
    {
        //TODO verify halving Capacity doesn't delete data
        int[] newArr = new int[data.length / 2];
        for (int i = 0; i < size; i++)
            newArr[i] = data[i];
        data = newArr;
    }

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

        if (data[mid] == integer)
            return mid;


        if (integer < data[mid])
            return findRec(integer, left, mid - 1);
        else
            return findRec(integer, mid + 1, right);
    }

    public IntegerSetOLD()
    {
        size = 0;
        data = new int[ARRAY_PADDING];
    }


    public IntegerSetOLD(int[] arr)
    {
        size = 0;
        data = new int[ARRAY_PADDING];

        //TODO this is O(n*n)
        for (int i = 0; i < arr.length; i++)
            add(arr[i]);
    }


    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size != 0;
    }

    @Override
    public boolean contains(Object o)
    {
        if (o == null)
            throw new NullPointerException();
        if (!o.getClass().equals(Integer.class))
            throw new ClassCastException();

        Integer i = (Integer) o;

        int loc = findRec(i, 0, size - 1);

        //findRec might return an out of bounds location i.e. size + 1
        if (loc >= size)
            return false;

        return i.intValue() == data[loc];
    }

    @Override
    public Iterator<Integer> iterator()
    {
        Iterator<Integer> itr = new Iterator<Integer>()
        {
            Integer[] arr = (Integer[]) toArray();
            int curInteger = 0;

            @Override
            public boolean hasNext()
            {
                return curInteger < toArray().length;
            }

            @Override
            public Integer next()
            {
                if (curInteger < arr.length)
                    return arr[curInteger++];

                throw new NoSuchElementException();
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
        Integer[] ret = new Integer[size];
        for (int i = 0; i < size; i++)
            ret[i] = data[i];

        return (Object[]) ret;
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        if (a == null)
            throw new NullPointerException();

        //TODO a.getClass().getSuperClass() ????
        if (!a.getClass().equals(Integer.class))
            throw new ArrayStoreException();

        return (T[]) toArray();
    }

    @Override
    public boolean add(Integer integer)
    {
        //find the insertion location
        int loc = findRec(integer, 0, size - 1);


        //check if this set already contains the integer
        if (data[loc] == integer)
            return false;

        //make room for the new integer
        for (int i = size; i > loc; i--)
            data[i] = data[i - 1];

        //insert new integer
        data[loc] = integer;

        //update size and capacity
        size++;
        resize();

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


        int loc = findRec(((Integer) o).intValue(), 0, size - 1);

        if (loc < size && (data[loc] == ((Integer) o).intValue()))
        {
            //remove and resize
            size--;
            for (int i = loc; i < size; i++)
                data[loc] = data[loc + 1];

            resize();
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


    public static void main(String args[])
    {
    }

}