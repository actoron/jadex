/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;

import org.relaxng.datatype.Datatype;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.util.StringPair;

/**
 * Creates a new Expression by combining existing expressions.
 * 
 * all expressions are memorized and unified so that every subexpression
 * will be shared and reused. Optimization will be also done transparently.
 * For example, createChoice(P,P) will result in P. createSequence(P,nullSet)
 * will result in nullSet.
 * 
 * Furthermore, associative operators are grouped to the left.
 * createChoice( (P|Q), (R|S) ) will be ((P|Q)|R)|S.
 * 
 * <P>
 * Although this unification is essential, this is also the performance
 * bottle neck. In particular, createChoice and createSequence are two most
 * commonly called methods.
 * 
 * <p>
 * For example, when validating a DocBook XML (150KB) twice against
 * DocBook.trex(237KB), createChoice is called 63000 times and createSequence
 * called 23000 times. (the third is the createOptional method and only 1560 times.)
 * And they took more than 10% of validation time, which is the worst
 * time-consuming method.
 * 
 * <p>
 * Therefore, please beware that this class includes several ugly code optimization.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ExpressionPool implements java.io.Serializable {
    
    public final Expression createAttribute( NameClass nameClass ) {
        return unify(new AttributeExp(nameClass,Expression.anyString));
    }
    public final Expression createAttribute( NameClass nameClass, Expression content, String defaultValue) {
        if(content==Expression.nullSet) {
            return content;
        }
        AttributeExp exp = new AttributeExp(nameClass,content);
        exp.setDefaultValue(defaultValue);
        return unify(exp);
    }
    
    public final Expression createEpsilon() { return Expression.epsilon; }
    public final Expression createNullSet() { return Expression.nullSet; }
    public final Expression createAnyString() { return Expression.anyString; }
    
    public final Expression createChoice( Expression left, Expression right ) {
        if( left==Expression.nullSet )        return right;
        if( right==Expression.nullSet )        return left;
        
        if( left==Expression.epsilon && right.isEpsilonReducible() )    return right;
        if( right==Expression.epsilon && left.isEpsilonReducible() )    return left;
        
        // TODO: should we re-order choice in a consistent manner?
        
        // associative operators are grouped to the left
        if( right instanceof ChoiceExp ) {
            final ChoiceExp c = (ChoiceExp)right;
            return createChoice( createChoice(left,c.exp1), c.exp2 );
        }

        // eliminate duplicate choice items by checking that the right
        // is already included in the left.
        Expression next = left;

        while( true ) {
            if( next==right )    return left;    // left is already in the choice
            if(!(next instanceof ChoiceExp))    break;
                
            ChoiceExp cp = (ChoiceExp)next;
            if( cp.exp2==right )    return left;
            next = cp.exp1;
        }

        // special (optimized) unification.
        // this will prevent unnecessary ChoiceExp instanciation.
        Expression o = expTable.getBinExp( left, right, ChoiceExp.class );
        if(o==null)
            // different thread may possibly be doing the same thing at the same time.
            // so we have to call unify method, too synchronize update.
            return unify( new ChoiceExp(left,right) );
        else
            return o;
    }
    
    public final Expression createOneOrMore( Expression child ) {
        if( child == Expression.epsilon
        ||  child == Expression.anyString
        ||  child == Expression.nullSet
        ||  child instanceof OneOrMoreExp )
            return child;
        
        return unify(new OneOrMoreExp(child));
    }
    
    public final Expression createZeroOrMore( Expression child ) {
        return createOptional(createOneOrMore(child));
    }
    
    public final Expression createOptional( Expression child ) {
        // optimization will be done in createChoice method.
        return createChoice(child,Expression.epsilon);
    }
    
    public final Expression createData( XSDatatype dt ) {
        String ns = dt.getNamespaceUri();
        if(ns==null)    ns="\u0000";    // use something that doesn't collide with others.
        return createData( dt, new StringPair(ns,dt.displayName()) );
    }
    
    public final Expression createData( Datatype dt, StringPair typeName ) {
        return createData( dt, typeName, Expression.nullSet );
    }
    
    public final Expression createData( Datatype dt, StringPair typeName, Expression except ) {
        return unify( new DataExp(dt,typeName,except) );
    }
    
    public final Expression createValue( XSDatatype dt, Object value ) {
        return createValue( dt, new StringPair("",dt.displayName()), value );
    }
    
    public final Expression createValue( Datatype dt, StringPair typeName, Object value ) {
        return unify( new ValueExp(dt,typeName,value) );
    }
    
    public final Expression createList( Expression exp ) {
        if(exp==Expression.nullSet) return exp;
        return unify( new ListExp(exp) );
    }
    
    public final Expression createMixed( Expression body ) {
        if( body==Expression.nullSet )        return Expression.nullSet;
        if( body==Expression.epsilon )        return Expression.anyString;
        
        return unify( new MixedExp(body) );
    }
    
    public final Expression createSequence( Expression left, Expression right ) {
        if( left ==Expression.nullSet
        ||    right==Expression.nullSet )    return Expression.nullSet;
        if( left ==Expression.epsilon )    return right;
        if( right==Expression.epsilon )    return left;
        
        // associative operators are grouped to the left
        if( right instanceof SequenceExp ) {
            final SequenceExp s = (SequenceExp)right;
            return createSequence( createSequence(left,s.exp1), s.exp2 );
        }
        
        // special (optimized) unification.
        Expression o = expTable.getBinExp( left, right, SequenceExp.class );
        if(o==null) {
            return unify( new SequenceExp(left,right) );
        }
        return o;
    }

    public final Expression createConcur( Expression left, Expression right ) {
        if( left==Expression.nullSet || right==Expression.nullSet )    return Expression.nullSet;
        if( left==Expression.epsilon ) {
            if( right.isEpsilonReducible() )    return Expression.epsilon;
            else                                return Expression.nullSet;
        }
        if( right==Expression.epsilon ) {
            if( left.isEpsilonReducible() )        return Expression.epsilon;
            else                                return Expression.nullSet;
        }
        
        // associative operators are grouped to the left
        if( right instanceof ConcurExp ) {
            final ConcurExp c = (ConcurExp)right;
            return createConcur( createConcur(left, c.exp1), c.exp2 );
        }
        
        return unify(new ConcurExp(left,right));
    }
    
    public final Expression createInterleave( Expression left, Expression right ) {
        if( left == Expression.epsilon )    return right;
        if( right== Expression.epsilon )    return left;
        if( left == Expression.nullSet
        ||  right== Expression.nullSet )    return Expression.nullSet;
        
        // associative operators are grouped to the left
        if( right instanceof InterleaveExp ) {
            final InterleaveExp i = (InterleaveExp)right;
            return createInterleave( createInterleave(left, i.exp1), i.exp2 );
        }
        
        return unify(new InterleaveExp(left,right));
    }
    
    
    /** hash table that contains all expressions currently known to this table. */
    private final ClosedHash expTable;
    
    /**
     * creates new expression pool as a child pool of the given parent pool.
     * 
     * <P>
     * Every expression memorized in the parent pool can be retrieved, but update
     * operations are only performed upon the child pool.
     * In this way, the parent pool can be shared among the multiple threads without
     * interfering performance.
     * 
     * <P>
     * Furthermore, you can throw away a child pool after a certain time period to
     * prevent it from eating up memory.
     */
    public ExpressionPool( ExpressionPool parent )    { expTable = new ClosedHash(parent.expTable); }
    public ExpressionPool()                            { expTable = new ClosedHash(); }
    
    
    
    /**
     * unifies expressions.
     * 
     * If the equivalent expression is already registered in the table,
     * destroy newly created one (so that no two objects represents
     * same expression structure).
     * 
     * If it's not registered, then register it and return it.
     */
    protected final Expression unify( Expression exp ) {
        // call of get method need not be synchronized.
        // the implementation guarantee that simulatenous calls to get & put
        // will work correctly.
        Object o = expTable.get(exp);
        
        if(o==null) {
            // expression may not be registered. So try it again with lock
            synchronized(expTable) {
                o = expTable.get(exp);
                if(o==null) {
                    // this check prevents two same expressions to be added simultaneously.
                    // expression is not registered.
                    expTable.put( exp );
                    return exp;
                }
            }
        }
        
        // expression is already registered.
        return (Expression)o;
    }


    /**
     * expression cache by closed hash.
     * 
     * Special care has to be taken wrt threading.
     * This implementation allows get and put method to be called simulatenously.
     */
    public final static class ClosedHash implements java.io.Serializable {
        /** The hash table data. */
        private Expression table[];

        /** The total number of mappings in the hash table. */
        private int count;

        /**
         * The table is rehashed when its size exceeds this threshold.  (The
         * value of this field is (int)(capacity * loadFactor).)
         */
        private int threshold;

        /** The load factor for the hashtable. */
        private static final float loadFactor = 0.3f;
        private static final int initialCapacity = 191;

        /**
         * The parent hash table.
         * can be null. items in the parent hash table will be returned by
         * get method.
         * 
         * <p>
         * The field is essentially final but because of the serialization
         * support we cannot mark it as such.
         */
        private ClosedHash parent;

        public ClosedHash() {
            this(null);
        }

        public ClosedHash(ClosedHash parent) {
            table = new Expression[initialCapacity];
            threshold = (int) (initialCapacity * loadFactor);
            this.parent = parent;
        }

        public Expression getBinExp(Expression left, Expression right, Class<?> type) {
            int hash = (left.hashCode()+right.hashCode())^type.hashCode();
            return getBinExp( hash, left, right, type );
        }
        private Expression getBinExp(int hash, Expression left, Expression right, Class<?> type) {
            if (parent != null) {
                Expression e = parent.getBinExp(hash, left, right, type);
                if (e != null)
                    return e;
            }

            Expression tab[] = table;
            int index = (hash & 0x7FFFFFFF) % tab.length;

            while (true) {
                final Expression e = tab[index];
                if (e == null)
                    return null;
                if (e.hashCode() == hash && e.getClass() == type) {
                    BinaryExp be = (BinaryExp)e;
                    if (be.exp1 == left && be.exp2 == right)
                        return be;
                }
                index = (index + 1) % tab.length;
            }
        }

        public Expression get(int hash, Expression child, Class<?> type) {
            if (parent != null) {
                Expression e = parent.get(hash, child, type);
                if (e != null)
                    return e;
            }

            Expression tab[] = table;
            int index = (hash & 0x7FFFFFFF) % tab.length;

            while (true) {
                final Expression e = tab[index];
                if (e == null)
                    return null;
                if (e.hashCode() == hash && e.getClass() == type) {
                    UnaryExp ue = (UnaryExp)e;
                    if (ue.exp == child)
                        return ue;
                }
                index = (index + 1) % tab.length;
            }
        }
        public Expression get(Expression key) {
            if (parent != null) {
                Expression e = parent.get(key);
                if (e != null)
                    return e;
            }

            Expression tab[] = table;
            int index = (key.hashCode() & 0x7FFFFFFF) % tab.length;

            while (true) {
                final Expression e = tab[index];
                if (e == null)
                    return null;
                if (e.equals(key))
                    return e;
                index = (index + 1) % tab.length;
            }
        }

        /**
         * rehash.
         * 
         * It is possible for one thread to call get method
         * while another thread is performing rehash.
         * Keep this in mind.
         */
        private void rehash() {
            // create a new table first.
            // meanwhile, other threads can safely access get method.
            int oldCapacity = table.length;
            Expression oldMap[] = table;

            int newCapacity = oldCapacity * 2 + 1;
            Expression newMap[] = new Expression[newCapacity];

            for (int i = oldCapacity; i-- > 0;)
                if (oldMap[i] != null) {
                    int index = (oldMap[i].hashCode() & 0x7FFFFFFF) % newMap.length;
                    while (newMap[index] != null)
                        index = (index + 1) % newMap.length;
                    newMap[index] = oldMap[i];
                }

            // threshold is not accessed by get method.
            threshold = (int) (newCapacity * loadFactor);
            // switch!
            table = newMap;
        }

        /**
         * put method. No two threads can call this method simulatenously,
         * and it's the caller's responsibility to enforce it.
         */
        public void put(Expression newExp) {
            if (count >= threshold)
                rehash();

            Expression tab[] = table;
            int index = (newExp.hashCode() & 0x7FFFFFFF) % tab.length;

            while (tab[index] != null)
                index = (index + 1) % tab.length;
            tab[index] = newExp;

            count++;
        }
        
        // serialization support
        private static final long serialVersionUID = -2924295970572669668L;
        
        private static final ObjectStreamField[] serialPersistentFields = { 
            new ObjectStreamField("count", Integer.TYPE),
            new ObjectStreamField("streamVersion", Byte.TYPE),
            new ObjectStreamField("parent", ExpressionPool.class) 
        }; 
        
        private void writeObject(ObjectOutputStream s) throws IOException {
            ObjectOutputStream.PutField fields = s.putFields();
            fields.put("count",count);
            fields.put("parent",parent);
            fields.put("streamVersion",(byte)1);
            s.writeFields();
            
            for( int i=0; i<table.length; i++ )
                if( table[i]!=null )
                    s.writeObject(table[i]);
        }
        
        private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException {
            // prepare to read the alternate persistent fields
            ObjectInputStream.GetField fields = s.readFields();
            
            byte version = fields.get("streamVersion",(byte)0);
            
            if( version==0 ) {
                // read in the old version format
                count = fields.get("count",0);
                parent = (ClosedHash)fields.get("parent",null);
                table = (Expression[])fields.get("table",null);
                threshold = fields.get("threshold",0);
            } else {
                // read the new format
                int objCnt = fields.get("count",0);
                parent = (ClosedHash)fields.get("parent",null);
                
                int size = (int)(objCnt/loadFactor)*2+10;
                threshold = count*2;
                count = 0;
                table = new Expression[size];
                for( int i=0; i<count; i++ )
                    put( (Expression)s.readObject() );
            }
        }
    }
    
    // serialization support
    private static final long serialVersionUID = 1;
}
