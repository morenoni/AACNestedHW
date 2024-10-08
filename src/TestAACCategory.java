import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;


/**
 * Tests of AACCategory.
 *
 * @author Samuel A. Rebelsky
 */
public class TestAACCategory {
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

  // +-------+-------------------------------------------------------
  // | Tests |
  // +-------+

  /**
   * Can we create a new category?
   */
  @Test
  void testCreateCategory() {
    AACCategory category = new AACCategory("test");
    assertNotNull(category, "Newly created category is not null.");
    assertEquals("test", category.getCategory(), 
        "Newly created category has the correct name");
  } // testCreateCategory

  /**
   * Tests of adding/selecting items. Since the specs don't indicate
   * what happens if we call addItem multiple times with the same key,
   * we don't check that issue.
   */
  @Test
  void testAddSelect() {
    AACCategory category = new AACCategory("testAddSelect");
    category.addItem("imageA", "Apple");
    category.addItem("imageB", "Banana");
    category.addItem("imageC", "Cherry");
    assertEquals("Apple", category.select("imageA"));
    assertEquals("Banana", category.select("imageB"));
    assertEquals("Cherry", category.select("imageC"));
  } // testAddSelect()

  /**
   * Tests of select in which the image is not there.
   */
  @Test
  void testSelectMissing() {
    AACCategory category = new AACCategory("testSelectMissing");

    // Nothing should be in the empty category.
    try {
      category.select("imageA");
      fail("select(\"imageA\") did not throw an exception");
    } catch (Exception e) {
      // Nothing to do; it threw an exception as expected.
    } // try/catch
    try {
      category.select("");
      fail("select(\"\") did not throw an exception");
    } catch (Exception e) {
      // Nothing to do; it threw an exception as expected.
    } // try/catch

    // Add a few things and then look for another
    category.addItem("imageA", "Apple");
    category.addItem("imageB", "Banana");
    category.addItem("imageC", "Cherry");
    try {
      category.select("imagea");
      fail("select(\"imagea\") did not throw an exception");
    } catch (Exception e) {
      // Nothing to do; it threw an exception as expected.
    } // try/catch
  } // testSelectMissing()

  /**
   * Tests of hasImage.
   */
  void testHasImage() {
    AACCategory category = new AACCategory("testHasImage");

    assertFalse(category.hasImage("imageA"), 
        "Should not have imageA to start with");
    assertFalse(category.hasImage(""), 
        "Should not have empty string to start with");

    category.addItem("imageA", "Apple");
    assertTrue(category.hasImage("imageA"), 
        "Should have imageA after adding it.");
    assertFalse(category.hasImage("imageB"),
        "Should not yet habve imageB");

    category.addItem("imageB", "Banana");
    assertTrue(category.hasImage("imageB"), 
        "Should have imageB after adding it.");
    assertTrue(category.hasImage("imageA"), 
        "Should still have imageA after adding imageB.");

    category.addItem("imageC", "Cherry");
    assertTrue(category.hasImage("imageC"), 
        "Should have imageC after adding it.");
    assertTrue(category.hasImage("imageA"), 
        "Should still have imageA after adding imageB and imageC.");
    assertTrue(category.hasImage("imageB"), 
        "Should still have imageB after adding imageC.");
    assertFalse(category.hasImage("imagec"),
        "Should not have similarly named image");
  } // testHasImage()

  /**
   * Test of getImageLocs.
   */
  @Test
  void testImageLocs() {
    AACCategory category = new AACCategory("testImageLocs");

    assertArrayEquals(new String[] {}, category.getImageLocs(),
        "Empty category should have no image locations");

    category.addItem("imageA", "Apple");
    assertArrayEquals(new String[] {"imageA"}, category.getImageLocs(),
        "After adding one element");

    category.addItem("imageB", "Banana");
    String[] ab = category.getImageLocs();
    assertEquals(2, ab.length, "After adding two images, length 2");
    assertTrue(containsString(ab, "imageA"), "imageLocs contains imageA");
    assertTrue(containsString(ab, "imageB"), "imageLocs contains imageB");

    category.addItem("imageC", "Cherry");
    String[] abc = category.getImageLocs();
    assertEquals(3, abc.length, "After adding three images, length 3");
    assertTrue(containsString(abc, "imageA"), "imageLocs contains imageA");
    assertTrue(containsString(abc, "imageB"), "imageLocs contains imageB");
    assertTrue(containsString(abc, "imageC"), "imageLocs contains imageC");
  } // testImageLocs()

} // class TestAACCategory
