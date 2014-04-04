package com.jmt;

import java.util.*;

/**
 *
 * This allows for a set of Objects that have unique IDs to be organized in a set.
 * The ArrayList of Identifiable's, data, is always sorted by object members' unique
 * id. The IntegerSet idSet shall always be the same size as data, and only contains
 * the unique id's corresponding to this IdentifiableSet. Whenever data is updated
 * idSet must be updated. We do this so any call to getIDList can be run in O(1) time.
 *
 *
 *
 * Created by jtappe on 4/3/2014.
 */
public class IdentifiableSet implements Set<Identifiable>
{


    //Any time we add to data we must add to idSet
    //Any time we remove from data we must remove from idSet
    private ArrayList<Identifiable> data;
    private IntegerSet idSet;


    public IdentifiableSet()
    {
        data = new ArrayList<Identifiable>();
        idSet = new IntegerSet();
    }

    public Identifiable findByUID(int uid)
    {
        int loc = findRec(uid, 0, size() - 1);

        if (loc < size() && data.get(loc).getUID() == uid)
            return data.get(loc);

        return null;
    }


    private int findRec(int uid, int left, int right)
    {
        int mid = (left + right) / 2;

        //here we didn't find integer and the new index should be right + 1 which is out of bounds but we can expand to the right
        if (mid > right)
            return mid;

        //we didn't find integer and the new index should be 'left' which is still in bounds
        if (mid < left)
            return left;

        int cmp = data.get(mid).getUID() - uid;
        //      int cmp = data.get(mid).compareTo(i);

        if (cmp == 0)
            return mid;

        if (cmp > 0)
            return findRec(uid, left, mid - 1);
        else
            return findRec(uid, mid + 1, right);
    }


    @Override
    public int size()
    {
        return data.size();
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o)
    {
        if (o == null)
            throw new NullPointerException();

        Identifiable val = (Identifiable) o;

        int loc = findRec(val.getUID(), 0, size() - 1);


        //findRec might return an out of bounds location i.e. size + 1
        if (loc >= size())
            return false;


        return data.get(loc).getUID() == val.getUID();
    }

    @Override
    public Iterator<Identifiable> iterator()
    {
        Iterator<Identifiable> itr = new Iterator<Identifiable>()
        {
            Object[] arr = (Object[]) toArray();
            int curInteger = 0;

            @Override
            public boolean hasNext()
            {
                return curInteger < toArray().length;
            }

            @Override
            public Identifiable next()
            {
                if (curInteger < arr.length)
                    return (Identifiable) arr[curInteger++];

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

    public IntegerSet getIDList()
    {
        return idSet;
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
    public boolean add(Identifiable val)
    {
        //find the insertion location
        int loc = findRec(val.getUID(), 0, size() - 1);

        if (loc >= size())
        {   //it wasn't found and we just need to add it

            data.add(val);
            idSet.add(val.getUID());

            return true;
        }


        //check if this set already contains the val
        if (data.get(loc).getUID() == val.getUID())
            return false;

        data.add(loc, val);
        idSet.add(val.getUID());

        //success!
        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        int loc = findRec(((Identifiable) o).getUID(), 0, size());

        //o is not in our set
        if (loc >= size())
            return false;

        //is o in our set??
        if (data.get(loc).getUID() == ((Identifiable) o).getUID())
        {
            idSet.remove(data.get(loc).getUID());
            data.remove(loc);

            return true;
        }

        //o is not in our set
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        Iterator<Object> itr = (Iterator<Object>) c.iterator();
        boolean containsAll = true;

        while (containsAll && itr.hasNext())
            containsAll = containsAll && contains(itr.next());

        return containsAll;
    }

    @Override
    public boolean addAll(Collection<? extends Identifiable> c)
    {
        //Optional
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        //Optional
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        //Optional
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        //Optional
        throw new UnsupportedOperationException();
    }
}
