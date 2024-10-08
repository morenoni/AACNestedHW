package edu.grinnell.csc207.util;

/**
 * Exceptions that indicate that a key is not in an associative array
 * (dictionary, map, etc.).
 *
 * @author Samuel A. Rebelsky
 */
public class KeyNotFoundException extends Exception {
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new exception.
   */
  public KeyNotFoundException() {
    super("key not found");
  } // KeyNotFoundException()

  /**
   * Create a new exception with a particular message.
   *
   * @param message
   *   The message in the exceptoin.
   */
  public KeyNotFoundException(String message) {
    super(message);
  } // KeyNotFoundException(String)
} // class KeyNotFoundException
