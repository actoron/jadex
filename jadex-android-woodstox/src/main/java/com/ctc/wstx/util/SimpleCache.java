package com.ctc.wstx.util;

import java.util.*;

/**
 * Simple Map implementation usable for caches where contents do not
 * expire, but where size needs to remain bounded.
 *<p>
 * Note: we probably should use weak references, or something similar
 * to limit maximum memory usage. This could be implemented in many
 * ways, perhaps by using two areas: first, smaller one, with strong
 * refs, and secondary bigger one that uses soft references.
 */
public final class SimpleCache
{
    protected final LimitMap mItems;

    protected final int mMaxSize;

    public SimpleCache(int maxSize)
    {
        mItems = new LimitMap(maxSize);
        mMaxSize = maxSize;
    }

    public Object find(Object key) {
        return mItems.get(key);
    }

    public void add(Object key, Object value)
    {
        mItems.put(key, value);
    }

    /*
    ///////////////////////////////////////////////////////////////////////
    // Helper classes
    ///////////////////////////////////////////////////////////////////////
     */

    final static class LimitMap
        extends LinkedHashMap
    {
        private static final long serialVersionUID = 1L;

        protected final int mMaxSize;

        public LimitMap(int size)
        {
            super(size, 0.8f, true);
            // Let's not allow silly low values...
            mMaxSize = size;
        }
        
        public boolean removeEldestEntry(Map.Entry eldest) {
            return (size() >= mMaxSize);
        }
    }
}
