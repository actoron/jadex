/*
 * Message.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Aug 30, 2005.  
 * Last revision $Revision$ by:
 * $Author$ on $Date$.
 */
package jadex.commons.collection;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implements a map from strings to objects as a hash table.
 * <code>null</code> key is allowed; <code>null</code> values are allowed.
 * 
 * @author walczak
 * @since Aug 30, 2005
 */
public class FastHashMap implements Map, java.io.Serializable
{
   private static final int    START_CAPACITY = 32;

   private static final double LOAD_FACTOR    = 0.75;

   private static final Object NONE           = NONE.class;

   private static final class NONE implements java.io.Serializable
   {/**/}

   private transient Object[] keys;

   private transient Object[] values;

   private transient int      size;

   private transient int      rehash_limit;

   private transient int      hash_range;

   private transient Object   nvar;

   /**
    * Constructor for Message.
    */
   public FastHashMap()
   {
      keys = new Object[START_CAPACITY];
      values = new Object[START_CAPACITY];
      size = 0;
      rehash_limit = (int) (keys.length * LOAD_FACTOR);
      hash_range = keys.length - 1;
      nvar = NONE;
   }

   /**
    * @param key
    * @param value
    * @return the old value or null
    */
   public Object put(final Object key, final Object value)
   {
      if (key == null)
      {
         Object on = nvar == NONE ? null : nvar;
         nvar = value;
         return on;
      }
      final int hc = key.hashCode();
      final int h = hc & hash_range;
      int i = h;
      while (i > 0)
      {
         if (keys[--i] == null)
         {
            keys[i] = key;
            values[i] = value;

            if (++size > rehash_limit) rehash();
            return null;
         }
         if (eq(key, hc, keys[i]))
         {
            Object ov = values[i];
            values[i] = value;
            return ov;
         }
      }
      i = keys.length;
      while (i > h)
      {
         if (keys[--i] == null)
         {
            keys[i] = key;
            values[i] = value;

            if (++size > rehash_limit) rehash();
            return null;
         }
         if (eq(key, hc, keys[i]))
         {
            Object ov = values[i];
            values[i] = value;
            return ov;
         }
      }

      return null;
   }

   private final static boolean eq(Object a, int ah, Object b)
   {
      return a == b || (ah == b.hashCode() && a.equals(b));
   }

   /** 
    * @param v1
    * @param v2
    * @return true if both objects are equal
    */
   private final static boolean eq(Object v1, Object v2)
   {
      return v1 == v2 || (v1 != null && v1.equals(v2));
   }

   /**
    *
    */
   private final void rehash()
   {
      final Object[] ok = keys;
      final Object[] ov = values;

      keys = new Object[keys.length << 1];
      values = new Object[keys.length];

      size = 0;
      rehash_limit = (int) (keys.length * LOAD_FACTOR);
      hash_range = keys.length - 1;

      int i = ok.length;
      while (i > 0)
      {
         if (ok[--i] != null) put(ok[i], ov[i]);
      }
   }

   /**
    * @param key
    * @return the object for this key or null
    */
   public Object get(final Object key)
   {
      if (key == null) return nvar == NONE ? null : nvar;
      final int hc = key.hashCode();
      final int h = hc & hash_range;
      int i = h;
      while (i > 0)
      {
         if (keys[--i] == null) { return null; }
         if (eq(key, hc, keys[i])) { return values[i]; }
      }
      i = keys.length;
      while (i > h)
      {
         if (keys[--i] == null) { return null; }
         if (eq(key, hc, keys[i])) { return values[i]; }
      }
      return null;
   }

   /**
    * @param key
    * @return true if there is a key of this kind
    */
   public boolean containsKey(final Object key)
   {
      if (key == null) return nvar != NONE;
      final int hc = key.hashCode();
      final int h = hc & hash_range;
      int i = h;
      while (i > 0)
      {
         if (keys[--i] == null) return false;
         if (eq(key, hc, keys[i])) return true;
      }
      i = keys.length;
      while (i > h)
      {
         if (keys[--i] == null) return false;
         if (eq(key, hc, keys[i])) return true;
      }
      return false;
   }

   /**
    * @return the keys from this message
    */
   public Object[] getKeys()
   {
      return keys;
   }

   /** 
    * @return The size of the map.
    * @see java.util.Map#size()
    */
   public int size()
   {
      return size;
   }

   /** 
    * @return True, if the map is empty.
    * @see java.util.Map#isEmpty()
    */
   public boolean isEmpty()
   {
      return size == 0;
   }

   /** 
    * @param value
    * @return True, if the value was found.
    * @see java.util.Map#containsValue(java.lang.Object)
    */
   public boolean containsValue(Object value)
   {
      int i = keys.length;
      while (i > 0)
      {
         if (keys[--i] != null && eq(value, values[i])) return true;
      }
      return false;
   }

   /** 
    * @param key
    * @return The object associated to the key.
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Object remove(Object key)
   {
      if (key == null)
      {
         Object ov = nvar == NONE ? null : nvar;
         nvar = NONE;
         return ov;
      }
      final int hc = key.hashCode();
      final int h = hc & hash_range;
      int i = h;
      while (i > 0)
      {
         if (keys[--i] == null) { return null; }
         if (eq(key, hc, keys[i]))
         {
            keys[i] = null;
            --size;
            return values[i];
         }
      }
      i = keys.length;
      while (i > h)
      {
         if (keys[--i] == null) { return null; }
         if (eq(key, hc, keys[i]))
         {
            keys[i] = null;
            --size;
            return values[i];
         }
      }
      return null;
   }

   /** 
    * @param map
    * @see java.util.Map#putAll(java.util.Map)
    */
   public void putAll(Map map)
   {
      Iterator ies = map.entrySet().iterator();

      while (ies.hasNext())
      {
         Map.Entry entry = (Entry) ies.next();
         put(entry.getKey(), entry.getValue());
      }

   }

   /** 
    * 
    * @see java.util.Map#clear()
    */
   public void clear()
   {
      size = 0;
      int i = keys.length;
      while (i > 00)
      {
         keys[--i] = null;
      }
      nvar = NONE;
   }

   transient Set key_set = null;

   /** 
    * @return The key set.
    * @see java.util.Map#keySet()
    */
   public Set keySet()
   {
      if (key_set == null) key_set = new AbstractSet()
         {

            public Iterator iterator()
            {
               return new Iterator()
                  {
                     int i = 0;

                     public boolean hasNext()
                     {
                        return i < keys.length;
                     }

                     public Object next()
                     {
                        for (; i < keys.length; i++)
                        {
                           if (keys[i] != null) return keys[i];
                        }
                        throw new NoSuchElementException();
                     }

                     public void remove()
                     {
                        if (keys[i] != null)
                        {
                           keys[i] = null;
                           --size;
                        }
                     }

                  };
            }

            public int size()
            {
               return size;
            }

         };
      return key_set;
   }

   transient Collection values_col = null;

   /** 
    * @return The values.
    * @see java.util.Map#values()
    */
   public Collection values()
   {
      if (values_col == null) values_col = new AbstractCollection()
         {

            public Iterator iterator()
            {
               return new Iterator()
                  {
                     int i = 0;

                     public boolean hasNext()
                     {
                        return i < keys.length;
                     }

                     public Object next()
                     {
                        for (; i < keys.length; i++)
                        {
                           if (keys[i] != null) return values[i];
                        }
                        throw new NoSuchElementException();
                     }

                     public void remove()
                     {
                        if (keys[i] != null)
                        {
                           keys[i] = null;
                           --size;
                        }
                     }

                  };
            }

            public int size()
            {
               return size;
            }

         };
      return values_col;
   }

   transient Set entry_set = null;

   /** 
    * @return The set of entries.
    * @see java.util.Map#entrySet()
    */
   public Set entrySet()
   {
      if (entry_set == null) entry_set = new AbstractSet()
         {

            public Iterator iterator()
            {
               return new Iterator()
                  {
                     int i = 0;

                     public boolean hasNext()
                     {
                        return i < keys.length;
                     }

                     public Object next()
                     {
                        for (; i < keys.length; i++)
                        {
                           
                           if (keys[i] != null) return new Map.Entry()
                              {
                                 final int e_index = i;
                                 
                                 public Object getKey()
                                 {
                                    return keys[e_index];
                                 }

                                 public Object getValue()
                                 {
                                    return values[e_index];
                                 }

                                 public Object setValue(Object arg0)
                                 {
                                    return values[e_index] = arg0;
                                 }

                              };
                        }
                        return null;
                     }

                     public void remove()
                     {
                        if (keys[i] != null)
                        {
                           keys[i] = null;
                           --size;
                        }
                     }

                  };
            }

            public int size()
            {
               return size;
            }

         };
      return entry_set;
   }

   /**
    * @return the string representation of this message
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      int i = keys.length;
      sb.append('{');

      while (i > 00)
      {
         if (keys[--i] != null)
         {
            sb.append(keys[i]);
            sb.append('=');
            sb.append(values[i]);
            sb.append(',');
         }
      }
      sb.append("null=");
      sb.append(nvar);
      sb.append('}');
      return sb.toString();
   }

   /**
    */
   private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException
   {
      int i = keys.length;
      s.writeInt(i);
      s.writeInt(size);

      while (i > 00)
      {
         if (keys[--i] != null)
         {
            s.writeObject(keys[i]);
            s.writeObject(values[i]);
         }
      }
      s.writeObject(nvar);
   }

   private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
   {
      int i = s.readInt();
      keys = new Object[i];
      values = new Object[i];
      rehash_limit = (int) (i * LOAD_FACTOR);
      hash_range = i - 1;

      size = 0;
      // Read the keys and values, and put the mappings in the HashMap
      for (i = s.readInt(); i > 00; i--)
      {
         put(s.readObject(), s.readObject());
      }
      nvar = s.readObject();
   }

   private static final long serialVersionUID = 362722346524651265L;

}