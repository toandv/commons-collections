/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/AbstractTestBidiMap.java,v 1.6 2003/11/02 18:29:33 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.iterators.MapIterator;

/**
 * Abstract test class for {@link BidiMap} methods and contracts.
 * 
 * @version $Revision: 1.6 $ $Date: 2003/11/02 18:29:33 $
 * 
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public abstract class AbstractTestBidiMap extends AbstractTestMap {

    // Test data.
    private static final Object[][] entriesKV =
        new Object[][] {
            new Object[] { "key1", "value1" },
            new Object[] { "key2", "value2" },
            new Object[] { "key3", "value3" }
    };
    private static final Object[][] entriesVK =
        new Object[][] {
            new Object[] { "value1", "key1" },
            new Object[] { "value2", "key2" },
            new Object[] { "value3", "key3" }
    };
    private final Object[][] entries;

    public AbstractTestBidiMap(String testName) {
        super(testName);
        entries = entriesKV;
    }

    public AbstractTestBidiMap() {
        super("Inverse");
        entries = entriesVK;
    }

    //-----------------------------------------------------------------------
    /**
     * Implement to create an empty <code>BidiMap</code>.
     * 
     * @return an empty <code>BidiMap</code> implementation.
     */
    protected abstract BidiMap makeEmptyBidiMap();

    /**
     * Override to create a full <code>BidiMap</code> other than the default.
     * 
     * @return a full <code>BidiMap</code> implementation.
     */
    protected BidiMap makeFullBidiMap() {
        final BidiMap map = makeEmptyBidiMap();
        for (int i = 0; i < entries.length; i++) {
            map.put(entries[i][0], entries[i][1]);
        }
        return map;
    }

    /**
     * Override to return the empty BidiMap.
     */
    protected final  Map makeEmptyMap() {
        return makeEmptyBidiMap();
    }

    /**
     * Override to indicate to AbstractTestMap this is a BidiMap.
     */
    protected boolean isAllowDuplicateValues() {
        return false;
    }
    
    /**
     * Override as DualHashBidiMap didn't exist until version 3.
     */
    protected String getCompatibilityVersion() {
        return "3";
    }

    // BidiPut
    //-----------------------------------------------------------------------
    public void testBidiPut() {
        BidiMap map = makeEmptyBidiMap();
        BidiMap inverse = map.inverseBidiMap();
        assertEquals(0, map.size());
        assertEquals(map.size(), inverse.size());
        
        map.put("A", "B");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("B", map.get("A"));
        assertEquals("A", inverse.get("B"));
        
        map.put("A", "C");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("C", map.get("A"));
        assertEquals("A", inverse.get("C"));
        
        map.put("B", "C");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("C", map.get("B"));
        assertEquals("B", inverse.get("C"));
        
        map.put("E", "F");
        assertEquals(2, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("F", map.get("E"));
        assertEquals("E", inverse.get("F"));
    }

    /**
     * Verifies that {@link #map} is still equal to {@link #confirmed}.
     * <p>
     * This implementation checks the inverse map as well.
     */
    protected void verify() {
        verifyInverse();
        super.verify();
    }

    protected void verifyInverse() {
        assertEquals(map.size(), ((BidiMap) map).inverseBidiMap().size());
        Map map1 = new HashMap(map);
        Map map2 = new HashMap(((BidiMap) map).inverseBidiMap());
        Set keys1 = map1.keySet();
        Set keys2 = map2.keySet();
        Collection values1 = map1.values();
        Collection values2 = map2.values();
        assertEquals(true, keys1.containsAll(values2));
        assertEquals(true, values2.containsAll(keys1));
        assertEquals(true, values1.containsAll(keys2));
        assertEquals(true, keys2.containsAll(values1));
    }
    
    // testGetKey
    //-----------------------------------------------------------------------
    public void testBidiGetKey() {
        doTestGetKey(makeFullBidiMap(), entries[0][0], entries[0][1]);
    }

    public void testBidiGetKeyInverse() {
        doTestGetKey(
            makeFullBidiMap().inverseBidiMap(),
            entries[0][1],
            entries[0][0]);
    }

    private final void doTestGetKey(BidiMap map, Object key, Object value) {
        assertEquals("Value not found for key.", value, map.get(key));
        assertEquals("Key not found for value.", key, map.getKey(value));
    }

    // testInverse
    //-----------------------------------------------------------------------
    public void testBidiInverse() {
        final BidiMap map = makeFullBidiMap();
        final BidiMap inverseMap = map.inverseBidiMap();

        assertSame(
            "Inverse of inverse is not equal to original.",
            map,
            inverseMap.inverseBidiMap());

        assertEquals(
            "Value not found for key.",
            entries[0][0],
            inverseMap.get(entries[0][1]));

        assertEquals(
            "Key not found for value.",
            entries[0][1],
            inverseMap.getKey(entries[0][0]));
    }

    //-----------------------------------------------------------------------
    public void testBidiModifyEntrySet() {
        modifyEntrySet(makeFullBidiMap());
        modifyEntrySet(makeFullBidiMap().inverseBidiMap());
    }

    private final void modifyEntrySet(BidiMap map) {
        // Gets first entry
        final Map.Entry entry = (Map.Entry)map.entrySet().iterator().next();

        // Gets key and value
        final Object key = entry.getKey();
        final Object oldValue = entry.getValue();

        // Sets new value
        final Object newValue = "newValue";
        entry.setValue(newValue);

        assertEquals(
            "Modifying entrySet did not affect underlying Map.",
            newValue,
            map.get(key));

        assertNull(
            "Modifying entrySet did not affect inverse Map.",
            map.getKey(oldValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiClear() {
        BidiMap map = makeFullBidiMap();
        map.clear();
        assertTrue("Map was not cleared.", map.isEmpty());
        assertTrue("Inverse map was not cleared.", map.inverseBidiMap().isEmpty());

        // Tests clear on inverse
        map = makeFullBidiMap().inverseBidiMap();
        map.clear();
        assertTrue("Map was not cleared.", map.isEmpty());
        assertTrue("Inverse map was not cleared.", map.inverseBidiMap().isEmpty());

    }

    //-----------------------------------------------------------------------
    public void testBidiRemove() {
        remove(makeFullBidiMap(), entries[0][0]);
        remove(makeFullBidiMap().inverseBidiMap(), entries[0][1]);

        removeKey(makeFullBidiMap(), entries[0][1]);
        removeKey(makeFullBidiMap().inverseBidiMap(), entries[0][0]);
    }

    private final void remove(BidiMap map, Object key) {
        final Object value = map.remove(key);
        assertTrue("Key was not removed.", !map.containsKey(key));
        assertNull("Value was not removed.", map.getKey(value));
    }

    private final void removeKey(BidiMap map, Object value) {
        final Object key = map.removeKey(value);
        assertTrue("Key was not removed.", !map.containsKey(key));
        assertNull("Value was not removed.", map.getKey(value));
    }

    //-----------------------------------------------------------------------
    public void testBidiKeySetValuesOrder() {
        resetFull();
        Iterator keys = map.keySet().iterator();
        Iterator values = map.values().iterator();
        for (; keys.hasNext() && values.hasNext();) {
            Object key = keys.next();
            Object value = values.next();
            assertSame(map.get(key), value);
        }
        assertEquals(false, keys.hasNext());
        assertEquals(false, values.hasNext());
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByKeySet() {
        removeByKeySet(makeFullBidiMap(), entries[0][0], entries[0][1]);
        removeByKeySet(makeFullBidiMap().inverseBidiMap(), entries[0][1], entries[0][0]);
    }

    private final void removeByKeySet(BidiMap map, Object key, Object value) {
        map.keySet().remove(key);

        assertTrue("Key was not removed.", !map.containsKey(key));
        assertTrue("Value was not removed.", !map.containsValue(value));

        assertTrue(
            "Key was not removed from inverse map.",
            !map.inverseBidiMap().containsValue(key));
        assertTrue(
            "Value was not removed from inverse map.",
            !map.inverseBidiMap().containsKey(value));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByEntrySet() {
        removeByEntrySet(makeFullBidiMap(), entries[0][0], entries[0][1]);
        removeByEntrySet(makeFullBidiMap().inverseBidiMap(), entries[0][1], entries[0][0]);
    }

    private final void removeByEntrySet(BidiMap map, Object key, Object value) {
        Map temp = new HashMap();
        temp.put(key, value);
        map.entrySet().remove(temp.entrySet().iterator().next());

        assertTrue("Key was not removed.", !map.containsKey(key));
        assertTrue("Value was not removed.", !map.containsValue(value));

        assertTrue(
            "Key was not removed from inverse map.",
            !map.inverseBidiMap().containsValue(key));
        assertTrue(
            "Value was not removed from inverse map.",
            !map.inverseBidiMap().containsKey(value));
    }

    public BulkTest bulkTestMapEntrySet() {
        return new TestBidiMapEntrySet();
    }

    public class TestBidiMapEntrySet extends TestMapEntrySet {
        public TestBidiMapEntrySet() {
            super();
        }
        public void testMapEntrySetIteratorEntrySetValueCrossCheck() {
            Object key1 = getSampleKeys()[0];
            Object key2 = getSampleKeys()[1];
            Object newValue1 = getNewSampleValues()[0];
            Object newValue2 = getNewSampleValues()[1];
                
            resetFull();
            // explicitly get entries as sample values/keys are connected for some maps
            // such as BeanMap
            Iterator it = TestBidiMapEntrySet.this.collection.iterator();
            Map.Entry entry1 = getEntry(it, key1);
            it = TestBidiMapEntrySet.this.collection.iterator();
            Map.Entry entry2 = getEntry(it, key2);
            Iterator itConfirmed = TestBidiMapEntrySet.this.confirmed.iterator();
            Map.Entry entryConfirmed1 = getEntry(itConfirmed, key1);
            itConfirmed = TestBidiMapEntrySet.this.confirmed.iterator();
            Map.Entry entryConfirmed2 = getEntry(itConfirmed, key2);
            TestBidiMapEntrySet.this.verify();
                
            if (isSetValueSupported() == false) {
                try {
                    entry1.setValue(newValue1);
                } catch (UnsupportedOperationException ex) {
                }
                return;
            }

            // these checked in superclass                
            entry1.setValue(newValue1);
            entryConfirmed1.setValue(newValue1);
            entry2.setValue(newValue2);
            entryConfirmed2.setValue(newValue2);
            
            // at this point
            // key1=newValue1, key2=newValue2
            try {
                entry2.setValue(newValue1);  // should remove key1
            } catch (IllegalArgumentException ex) {
                return;  // simplest way of dealing with tricky situation
            }
            entryConfirmed2.setValue(newValue1);
            AbstractTestBidiMap.this.confirmed.remove(key1);
            assertEquals(newValue1, entry2.getValue());
            assertEquals(true, AbstractTestBidiMap.this.map.containsKey(entry2.getKey()));
            assertEquals(true, AbstractTestBidiMap.this.map.containsValue(newValue1));
            assertEquals(newValue1, AbstractTestBidiMap.this.map.get(entry2.getKey()));
            assertEquals(false, AbstractTestBidiMap.this.map.containsKey(key1));
            assertEquals(false, AbstractTestBidiMap.this.map.containsValue(newValue2));
            TestBidiMapEntrySet.this.verify();
            
            // check for ConcurrentModification
            it.next();  // if you fail here, maybe you should be throwing an IAE, see above
            if (isRemoveSupported()) {
                it.remove();
            }
        }
    }
        
    public BulkTest bulkTestInverseMap() {
        return new TestInverseBidiMap(this);
    }

    class TestInverseBidiMap extends AbstractTestBidiMap {
        final AbstractTestBidiMap main;
        
        public TestInverseBidiMap(AbstractTestBidiMap main) {
            super();
            this.main = main;
        }
        protected BidiMap makeEmptyBidiMap() {
            return main.makeEmptyBidiMap().inverseBidiMap();
        }
        protected BidiMap makeFullBidiMap() {
            return main.makeFullBidiMap().inverseBidiMap();
        }
        protected Object[] getSampleKeys() {
            return main.getSampleValues();
        }
        protected Object[] getSampleValues() {
            return main.getSampleKeys();
        }
        
        protected String getCompatibilityVersion() {
            return main.getCompatibilityVersion();
        }
        protected boolean isAllowNullKey() {
            return main.isAllowNullKey();
        }
        protected boolean isAllowNullValue() {
            return main.isAllowNullValue();
        }
        protected boolean isPutAddSupported() {
            return main.isPutAddSupported();
        }
        protected boolean isPutChangeSupported() {
            return main.isPutChangeSupported();
        }
        protected boolean isSetValueSupported() {
            return main.isSetValueSupported();
        }
        protected boolean isRemoveSupported() {
            return main.isRemoveSupported();
        }

    }
    
    //-----------------------------------------------------------------------
    public void testBidiMapIteratorEmpty() {
        resetEmpty();
        BidiMap bidi = (BidiMap) map;
        MapIterator it = bidi.mapIterator();
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {}
        try {
            it.getKey();
            fail();
        } catch (IllegalStateException ex) {
        }
        try {
            it.getValue();
            fail();
        } catch (IllegalStateException ex) {
        }
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {
        }
        try {
            it.setValue(null);
            fail();
        } catch (IllegalStateException ex) {
        }
        try {
            it.asMapEntry();
            fail();
        } catch (IllegalStateException ex) {
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testBidiMapIteratorFull() {
        resetFull();
        BidiMap bidi = (BidiMap) map;
        MapIterator it = bidi.mapIterator();
        
        assertEquals(true, it.hasNext());
        Map.Entry lastEntry = null;
        Object lastKey = null;
        Object lastValue = null;
        while (it.hasNext()) {
            Object key = it.next();
            assertSame(key, it.getKey());
        
            Object value = it.getValue();
            assertSame(bidi.get(key), value);
            
            Map.Entry entry = it.asMapEntry();
            assertTrue(entry != lastEntry);
            if (lastKey != null && lastValue != null) {
                assertSame(lastKey, lastEntry.getKey());
                assertSame(lastValue, lastEntry.getValue());
            }
            assertSame(key, entry.getKey());
            assertSame(value, entry.getValue());
            
            lastEntry = entry;
            lastKey = key;
            lastValue = value;
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testBidiMapIteratorRemove() {
        resetFull();
        BidiMap bidi = (BidiMap) map;
        MapIterator it = bidi.mapIterator();
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        if (isRemoveSupported() == false) {
            try {
                it.remove();
                fail();
            } catch (UnsupportedOperationException ex) {
            }
            return;
        }
        
        it.remove();
        confirmed.remove(key);
        assertEquals(false, bidi.containsKey(key));
        verify();
        
        try {
            it.remove();  // second remove fails
        } catch (IllegalStateException ex) {
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testBidiMapIteratorSet() {
        Object newValue1 = getOtherValues()[0];
        Object newValue2 = getOtherValues()[1];
        
        resetFull();
        BidiMap bidi = (BidiMap) map;
        MapIterator it = bidi.mapIterator();
        assertEquals(true, it.hasNext());
        Object key1 = it.next();
        
        if (isSetValueSupported() == false) {
            try {
                it.setValue(newValue1);
                fail();
            } catch (UnsupportedOperationException ex) {
            }
            return;
        }
        
        it.setValue(newValue1);
        confirmed.put(key1, newValue1);
        assertSame(key1, it.getKey());
        assertSame(newValue1, it.getValue());
        assertEquals(true, bidi.containsKey(key1));
        assertEquals(true, bidi.containsValue(newValue1));
        assertEquals(newValue1, bidi.get(key1));
        verify();
        
        it.setValue(newValue1);  // same value - should be OK
        confirmed.put(key1, newValue1);
        assertSame(key1, it.getKey());
        assertSame(newValue1, it.getValue());
        assertEquals(true, bidi.containsKey(key1));
        assertEquals(true, bidi.containsValue(newValue1));
        assertEquals(newValue1, bidi.get(key1));
        verify();
        
        Object key2 = it.next();
        it.setValue(newValue2);
        confirmed.put(key2, newValue2);
        assertSame(key2, it.getKey());
        assertSame(newValue2, it.getValue());
        assertEquals(true, bidi.containsKey(key2));
        assertEquals(true, bidi.containsValue(newValue2));
        assertEquals(newValue2, bidi.get(key2));
        verify();
        
        // at this point
        // key1=newValue1, key2=newValue2
        try {
            it.setValue(newValue1);  // should remove key1
            fail();
        } catch (IllegalArgumentException ex) {
            return;  // simplest way of dealing with tricky situation
        }
        confirmed.put(key2, newValue1);
        AbstractTestBidiMap.this.confirmed.remove(key1);
        assertEquals(newValue1, it.getValue());
        assertEquals(true, bidi.containsKey(it.getKey()));
        assertEquals(true, bidi.containsValue(newValue1));
        assertEquals(newValue1, bidi.get(it.getKey()));
        assertEquals(false, bidi.containsKey(key1));
        assertEquals(false, bidi.containsValue(newValue2));
        verify();
            
        // check for ConcurrentModification
        it.next();  // if you fail here, maybe you should be throwing an IAE, see above
        if (isRemoveSupported()) {
            it.remove();
        }
    }

    //-----------------------------------------------------------------------
    public void testBidiMapIteratorSetRemoveSet() {
        if (isSetValueSupported() == false || isRemoveSupported() == false) {
            return;
        }
        Object newValue1 = getOtherValues()[0];
        
        resetFull();
        BidiMap bidi = (BidiMap) map;
        MapIterator it = bidi.mapIterator();
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        it.setValue(newValue1);
        confirmed.put(key, newValue1);
        verify();
        
        it.remove();
        confirmed.remove(key);
        verify();
        
        try {
            it.setValue(newValue1);
            fail();
        } catch (IllegalStateException ex) {
        }
        verify();
    }

}
