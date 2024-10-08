import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.nio.file.Files;
import java.nio.file.Path;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

/**
 * Tests of AACMappings.
 *
 * @author Samuel A. Rebelsky
 */
public class TestAACMappings {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /** Our standard configureation file. */
  static final String TEST_CONFIG =
      """
      one fruit
      >a apple
      >b banana
      >c cherry
      two clothes
      >d dress
      >e extra ear rings
      >f fitted shirt
      >g gloves
      three miscellaneous
      >h hamper
      >i igloo
      """;

  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The place we store temporary values.
   */
  static Path tempDir;

  // +-------+-------------------------------------------------------
  // | Setup |
  // +-------+

  /**
   * Set up our temporary directory.
   */
  @BeforeAll
  static void setup() throws IOException {
    tempDir = Files.createTempDirectory("TestAACMappings");
  } // setup()

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Determine if an array of strings contains a string.
   */
  static boolean containsString(String[] strings, String str) {
    for (String s : strings) {
      if (s.equals(str)) {
        return true;
      } // if
    } // for
    return false;
  } // containsString(String[], String)

  /**
   * Set up a new temporary config file.
   *
   * @param contents
   *   The data that belong in the file.
   *
   * @return the path to the file.
   *
   * @throws IOException if one of the many IO commands fails.
   */
  String configFile(String contents) throws IOException {
    Path filePath = Files.createTempFile(tempDir, "", "");
    File config = new File(filePath.toString());
    config.deleteOnExit();
    FileWriter writer = new FileWriter(config);
    writer.write(contents, 0, contents.length());
    writer.close();
    // System.out.println("Config file: " + filePath.toString());
    return filePath.toString();
  } // configFile(String)

  // +-------+-------------------------------------------------------
  // | Tests |
  // +-------+

  /**
   * Can we create a new AACMappings with an empty description file?
   * This test can't fail, but it can exit with an error.
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testCreateEmpty() throws IOException {
    String fName = configFile("\n");
    AACMappings mappings = new AACMappings(fName);
    assertNotNull(mappings);
  } // testCreateEmpty()

  /**
   * Can we create a new AACMappings that only has top-level descriptions?
   * This test can't fail, but it can exit with an error.
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testCreateTopLevel() throws IOException {
    String fName = configFile("one one\ntwo two\nthree three\n");
    AACMappings mappings = new AACMappings(fName);
    assertNotNull(mappings);
  } // testCreateTopLevel()

  /**
   * Can we create a new AACMappings that has the normal format?
   * This test can't fail, but it can exit with an error.
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test 
  public void testCreateNormal() throws IOException {
    String fName = configFile(TEST_CONFIG);
    AACMappings mappings = new AACMappings(fName);
    assertNotNull(mappings);
  } // testCreateNormal()

  /**
   * Do we get the correct top-level domain?
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testTopLevelCategory() throws IOException {
    AACMappings mappings;

    mappings = new AACMappings(configFile(""));
    assertEquals("", mappings.getCategory(), "top level, empty config");

    mappings = 
        new AACMappings(configFile("one food\ntwo clothes\nthree misc"));
    assertEquals("", mappings.getCategory(), "top level, empty categories");

    mappings = 
        new AACMappings(configFile(TEST_CONFIG));
    assertEquals("", mappings.getCategory(), "top level, test config");
  } // testTopLevelCategory()

  /**
   * Do we get the correct category after switching categories? Also
   * checks some issues with reset and select.
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testGetCategoryA() throws IOException {
    AACMappings mappings = 
        new AACMappings(configFile("one uno\ntwo dos\nthree tres"));
    assertEquals("", mappings.select("one"), "selecting one");
    assertEquals("uno", mappings.getCategory(), "selected one");

    mappings.reset();
    assertEquals("", mappings.select("two"), "selecting two");
    assertEquals("dos", mappings.getCategory(), "selected two");

    mappings.reset();
    assertEquals("", mappings.select("three"), "selecting three");
    assertEquals("tres", mappings.getCategory(), "selected three");
  } // testGetCategoryA()

  /**
   * Do we get the correct category after switching categories? Also
   * checks some issues with reset and select.
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testGetCategoryB() throws IOException {
    AACMappings mappings = new AACMappings(configFile(TEST_CONFIG));
    assertEquals("", mappings.select("one"), "selecting one");
    assertEquals("fruit", mappings.getCategory(), "selected one");

    mappings.reset();
    assertEquals("", mappings.select("two"), "selecting two");
    assertEquals("clothes", mappings.getCategory(), "selected two");

    mappings.reset();
    assertEquals("", mappings.select("three"), "selecting three");
    assertEquals("miscellaneous", mappings.getCategory(), "selected three");
  } // testGetCategoryB()

  /**
   * Do we get the appropriate list of image locations with no
   * top-level categories?
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testImageLocsEmpty() throws IOException {
    AACMappings mappings = new AACMappings(configFile(""));
    assertArrayEquals(new String[] {}, mappings.getImageLocs(),
        "Top-level locations, no categories");
  } // testImageLocsEmpty()

  /**
   * Do we get the appropriate list of image locations with only
   * top-level categories?
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testImageLocsTopLevel() throws IOException {
    AACMappings mappings = 
        new AACMappings(configFile("uno one\ndos two\ntres three\n"));

    String[] categories = mappings.getImageLocs();
    assertEquals(3, categories.length, "three top-level categories");
    assertTrue(containsString(categories, "uno"), 
        "the first top-level category");
    assertTrue(containsString(categories, "dos"), 
        "the second top-level category");
    assertTrue(containsString(categories, "tres"), 
        "the third top-level category");

    assertEquals("", mappings.select("uno"), "selecting uno");
    assertArrayEquals(new String[] {}, mappings.getImageLocs(),
        "nothing in category uno");

    mappings.reset();
    assertEquals("", mappings.select("dos"), "selecting dos");
    assertArrayEquals(new String[] {}, mappings.getImageLocs(), 
        "nothing in category dos");

    mappings.reset();
    assertEquals("", mappings.select("tres"), "selecting tres");
    assertArrayEquals(new String[] {}, mappings.getImageLocs(),
        "nothing in category tres");

    mappings.reset();
    categories = mappings.getImageLocs();
    assertEquals(3, categories.length, "still three top-level categories");
    assertTrue(containsString(categories, "uno"), 
        "the first top-level category");
    assertTrue(containsString(categories, "dos"), 
        "the second top-level category");
    assertTrue(containsString(categories, "tres"), 
        "the third top-level category");
  } // testImageLocsTopLevel()

  /**
   * Do we get the appropriate list of image locations with only
   * top-level categories?
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testImageLocsNested() throws IOException {
    AACMappings mappings = new AACMappings(configFile(TEST_CONFIG));

    String[] images = mappings.getImageLocs();
    assertEquals(3, images.length, "three top-level categories");
    assertTrue(containsString(images, "one"), "category one");
    assertTrue(containsString(images, "two"), "category two");
    assertTrue(containsString(images, "three"), "category three");

    assertEquals("", mappings.select("one"), "selecting one");
    images = mappings.getImageLocs();
    assertEquals(3, images.length, "three fruits");
    assertTrue(containsString(images, "a"), "a is a fruit");
    assertTrue(containsString(images, "b"), "b is a fruit");
    assertTrue(containsString(images, "c"), "c is a fruit");

    mappings.reset();
    assertEquals("", mappings.select("two"), "selecting two");
    images = mappings.getImageLocs();
    assertEquals(4, images.length, "four types of clothes");
    assertTrue(containsString(images, "d"), "d is a type of clothes");
    assertTrue(containsString(images, "e"), "e is a type of clothes");
    assertTrue(containsString(images, "f"), "f is a type of clothes");
    assertTrue(containsString(images, "g"), "g is a type of clothes");

    mappings.reset();
    assertEquals("", mappings.select("three"), "selecting three");
    images = mappings.getImageLocs();
    assertEquals(2, images.length, "two miscellaneous items");
    assertTrue(containsString(images, "h"), "h is miscellaneous");
    assertTrue(containsString(images, "i"), "i is miscellaneous");

    mappings.reset();
    images = mappings.getImageLocs();
    assertEquals(3, images.length, "still three top-level categories");
    assertTrue(containsString(images, "one"), "category one");
    assertTrue(containsString(images, "two"), "category two");
    assertTrue(containsString(images, "three"), "category three");
  } // testImageLocsNested()

  /**
   * Test select in a fairly common situation.
   * 
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testSelectStandard() throws IOException {
    AACMappings mappings = new AACMappings(configFile(TEST_CONFIG));

    assertEquals("", mappings.select("one"), "one is a category");
    assertEquals("apple", mappings.select("a"), "a is for apple");
    assertEquals("banana", mappings.select("b"), "b is for banana");
    assertEquals("cherry", mappings.select("c"), "c is for cherry");

    mappings.reset();
    assertEquals("", mappings.select("two"), "two is a category");
    assertEquals("dress", mappings.select("d"), "d is for dress");
    assertEquals("extra ear rings", mappings.select("e"), 
        "e is for extra ear rings");
    assertEquals("fitted shirt", mappings.select("f"), "f is for fitted shitt");
    assertEquals("gloves", mappings.select("g"), "g is for gloves");

    mappings.reset();
    assertEquals("", mappings.select("three"), "three is a category");
    assertEquals("hamper", mappings.select("h"), "h is for hamper");
    assertEquals("igloo", mappings.select("i"), "i is for igloo");
  } // testSelectStandard()

  /**
   * Test select in some situation in which it should throw exceptions.
   * 
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testSelectExceptional() throws IOException {
    AACMappings mappings = new AACMappings(configFile(""));
    String str;

    try {
      str = mappings.select("one");
      fail("select(\"one\") returned \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch

    mappings = new AACMappings(configFile("one won\ntwo too\n"));

    try {
      str = mappings.select("five");
      fail("select(\"five\") returned \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch

    assertEquals("", mappings.select("one"), "Selecting category one");
    try {
      str = mappings.select("one");
      fail("select(\"one\") return \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch

    mappings = new AACMappings(configFile(TEST_CONFIG));

    assertEquals("", mappings.select("two"), "Selecting category two");
    // A different category name shouldn't work within a category
    try {
      str = mappings.select("two");
      fail("select(\"two\") returned \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch
    // An image from a different category shouldn't work within a category
    try {
      str = mappings.select("a");
      fail("select(\"a\") returned \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch
    // A value shouldn't work within a category.
    try {
      str = mappings.select("dress");
      fail("select(\"dress\") returned \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch
    // A random word shouldn't work within a category.
    try {
      str = mappings.select("whatever");
      fail("select(\"whatever\") returned \"" + str + "\"");
    } catch (Exception e) {
      // We expected an exception
    } // try/catch
  } // testSelectExceptional()

  /**
   * Test adding new values to the empty state.
   *
   * @throws IOException
   *   If an IO operation fails.
   */
  @Test
  public void testAdd() throws IOException {
    AACMappings mappings = new AACMappings(configFile(""));

    mappings.addItem("p", "pizza toppings");
    assertArrayEquals(new String[] {"p"}, mappings.getImageLocs(),
        "After adding our first category");

    assertEquals("", mappings.select("p"), "selecting p");
    assertArrayEquals(new String[] {}, mappings.getImageLocs(),
        "Nothing in category p (at least not yet)");

    mappings.addItem("o", "onion");
    mappings.addItem("p", "pepperoni");
    mappings.addItem("f", "fuzzy fish");
    assertEquals("fuzzy fish", mappings.select("f"), "selecting topping f");
    assertEquals("onion", mappings.select("o"), "selecting topping o");
    assertEquals("pepperoni", mappings.select("p"), "selecting topping p");
    assertEquals(3, mappings.getImageLocs().length, "3 toppings");

    mappings.reset();
    mappings.addItem("q", "words that start with q");
    assertEquals(2, mappings.getImageLocs().length, "two categories");
    assertEquals("", mappings.select("q"));
    assertArrayEquals(new String[] {}, mappings.getImageLocs(),
        "Nothing in category q (at least not yet)");
    mappings.addItem("image of silence", "quiet");
    mappings.addItem("image of a small fruit", "quince");
    mappings.addItem("p", "not quite q");
    mappings.addItem("image of an old-fashioned pen", "quill");
    assertEquals("not quite q", mappings.select("p"), "p is not q");
    assertEquals(4, mappings.getImageLocs().length, "four qs");

    mappings.reset();
    assertEquals("", mappings.select("p"), "returning to pizza topping");
    assertEquals("pepperoni", mappings.select("p"), "selecting topping p");
    mappings.addItem("g", "garlic");
    assertEquals(4, mappings.getImageLocs().length, "4 toppings");
    assertEquals("pepperoni", mappings.select("p"), "selecting topping p");
  } // testAdd()
} // class TestAACCategory
