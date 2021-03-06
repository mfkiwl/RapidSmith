/*
 * Copyright (c) 2010 Brigham Young University
 *
 * This file is part of the BYU RapidSmith Tools.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package edu.byu.ece.rapidSmith.device.helper;

import edu.byu.ece.rapidSmith.device.WireConnection;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * DO NOT USE THIS CLASS!  This class was specially developed for the Device 
 * wire connections hash map.  It is specifically optimized for that purpose.
 * Created on: Mar 18, 2011
 */
public class WireHashMap implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1812997315891050408L;

	/**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.85f;
    
    /**
     * The keys table. Length MUST Always be a power of two.
     */
    public transient int[] keys;
    
    /**
     * The corresponding values table.
     */
    public transient WireConnection[][] values;
    
    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

	// These variables are used to track the whether the caches are up to date.
	// A cache is up to date if it is equivalent to the wireHashMapModification
	// value.  Any put operation updates the wireHashMapModification value
	private transient int wireHashMapModification = 0;
	private transient int keySetCacheModification = -1; // initialize as out of date
	private transient int valuesCacheModification = -1; // initialize as out of date

	// Caches are stored as soft references to avoid being a memory drain
	// when not in use.
	private transient SoftReference<Set<Integer>> keySetCache;
	private transient SoftReference<ArrayList<WireConnection[]>> valuesCache;

    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;

	private Integer hash;

	/** This map requires an initial capacity.  This map will not grow. */
    public WireHashMap(int capacity, float loadFactor){
        if (capacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               capacity);
        if (capacity > MAXIMUM_CAPACITY)
            capacity = MAXIMUM_CAPACITY;

        // Find a power of 2 >= initialCapacity
        int finalCapacity = 4;
        while (finalCapacity < capacity)
            finalCapacity <<= 1;
        
        this.loadFactor = loadFactor;
        threshold = (int)(finalCapacity * loadFactor);
        
        keys = new int[finalCapacity];
	    Arrays.fill(keys, -1);
	    values = new WireConnection[finalCapacity][];
        size = 0;
    }
    
    public WireHashMap(int capacity){
    	this(capacity, DEFAULT_LOAD_FACTOR);
    }
    
    public WireHashMap(){
    	this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

	public WireHashMap(WireHashMap other) {
		this(other.threshold, other.loadFactor);
		for (Integer i : other.keySet()) {
			put(i, other.get(i));
		}
	}

	/**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        return h & (length-1);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }

	private int indexFor(int key) {
		int i = key & (keys.length-1);
		while(keys[i] != key && keys[i] != -1){
			i+=3;
			if(i >= keys.length) i=i&3;
		}
		return i;
	}
    
    public WireConnection[] get(int key){
	    int i = indexFor(key);
	    if (keys[i] == -1)
		    return null;
        return values[i];
    } 

    public void put(int key, WireConnection[] value){
		int i = indexFor(key);
        if(keys[i] == -1)
	        size++;
        keys[i] = key;
        values[i] = value;
	    wireHashMapModification++;

        if(size > threshold){
        	grow();
        }
    }
    
    private void grow(){
    	int newCapacity = keys.length*2;
        threshold = (int)(newCapacity * loadFactor);
    	int[] oldKeys = keys;
    	WireConnection[][] oldValues = values;
        keys = new int[newCapacity];
	    Arrays.fill(keys, -1);
        values = new WireConnection[newCapacity][];
        size = 0;
        for(int i=0; i < oldKeys.length; i++){
			if(oldKeys[i] != -1){
				put(oldKeys[i], oldValues[i]);
			}
		}
    }
    
    public Set<Integer> keySet(){
	    // check if the cached keySets are current
	    Set<Integer> keySet = keySetCache == null ? null : keySetCache.get();
	    if (keySet != null && keySetCacheModification == wireHashMapModification)
		    return keySet;
	    keySetCacheModification = wireHashMapModification;

	    // build the keyset cache
	    keySet = new HashSet<>();
	    for (int key : keys) {
		    if (key != -1)
			    keySet.add(key);
	    }
	    keySetCache = new SoftReference<>(keySet);
    	return keySet;
    }
    
    public ArrayList<WireConnection[]> values(){
	    // check if the cached values are current;
	    ArrayList<WireConnection[]> valuesList = valuesCache == null ? null : valuesCache.get();
	    if (valuesList != null && valuesCacheModification == wireHashMapModification)
		    return valuesList;
	    valuesCacheModification = wireHashMapModification;

	    // build the values cache
	    valuesList = new ArrayList<>(size);
    	for (int i = 0; i < keys.length; i++) {
			if(keys[i] != -1)
				valuesList.add(values[i]);
		}
	    valuesCache = new SoftReference<>(valuesList);
    	return valuesList;
    }

	@Override
	public int hashCode() {
		if (hash != null)
			return hash;
		hash = 0;
		for (Integer i : keySet()) {
			hash += i * 7;
			if (get(i) != null) {
				hash += Arrays.deepHashCode(get(i)) * 13;
			}
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;

		WireHashMap other = (WireHashMap) obj;
		if (!keySet().equals(other.keySet())) {
			return false;
		}
		for (Integer key : keySet()) {
			if (!Arrays.deepEquals(get(key), other.get(key))) {
				return false;
			}
		}
		return true;
	}
}
