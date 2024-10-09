package edu.grinnell.csc207.util;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Nicole Moreno Gonzalez
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V> pairs[];

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> newArray = new AssociativeArray<>();
    newArray.size = this.size;
    newArray.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length);
    for (int i = 0; i < this.size; i++) {
      newArray.pairs[i] = this.pairs[i].clone();
    } // for
    return newArray;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  public String toString() {
    if (size == 0) {
      return "{}";
    } // if
    String result = "{";
    for (int i = 0; i < size; i++) {
      result += pairs[i].key + ":" + (pairs[i].val == null ? "null" : pairs[i].val.toString());
      if (i < size - 1) {
        result += ", ";
      } // if
    } // for
    result += "}";
    return result;
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   *
   * @param key
   *   The key whose value we are seeting.
   * @param value
   *   The value of that key.
   *
   * @throws NullKeyException
   *   If the client provides a null key.
   */
  public void set(K key, V value) throws NullKeyException {
    if (key == null) {
      throw new NullKeyException("Key cannot be null");
    } // if
    try {
      int index = find(key);
      pairs[index].val = value;
    } catch (KeyNotFoundException e) {
      if (size >= pairs.length) {
        expand();
      } // if
      pairs[size] = new KVPair<>(key, value);
      size++;
    } // try/catch
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key
   *   A key
   * @return
   *  A pair k,v.
   *
   * @throws KeyNotFoundException
   *   when the key is null or does not appear in the associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    if (key == null) {
      throw new KeyNotFoundException("Key cannot be null");
    } // if
    int index = find(key);
    return pairs[index].val;
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should
   * return false for the null key.
   * @param key
   *  A key

   * @return
   *  True if key found, false otherwise.
   */
  public boolean hasKey(K key) {
    if (key == null) {
      return false;
    } // if
    try {
      find(key);
      return true;
    } catch (KeyNotFoundException e) {
      return false;
    } // try/catch
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.

   * @param key
   *  The key of a pair.
   */
  public void remove(K key) {
    try {
      int index = find(key);
      if (index != size - 1) {
        pairs[index] = pairs[size - 1];
      } // if
      pairs[size - 1] = null;
      size--;
    } catch (KeyNotFoundException e) {
      // Key doesn't exist, so do nothing
    } // try/catch
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   * @return
   *  Amount of kv pairs.
   */
  public int size() {
    return this.size;
  } // size()

  /**
   * Get all keys in the associative array.
   * @return an array of all keys
   */
  public String[] keys() {
    String[] keyArray = new String[size];  
    for (int i = 0; i < size; i++) {
      keyArray[i] = (String) pairs[i].key;  
    }
    return keyArray;
  }

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   *
   * @param key
   *   The key of the entry.

   * @return
   *  Index
   *
   * @throws KeyNotFoundException
   *   If the key does not appear in the associative array.
   */
  int find(K key) throws KeyNotFoundException {
    for (int i = 0; i < size; i++) {
      if (pairs[i].key.equals(key)) {
        return i;
      } // if
    } // for
    throw new KeyNotFoundException("Key not found: " + key);
  } // find(K)

} // class AssociativeArray
