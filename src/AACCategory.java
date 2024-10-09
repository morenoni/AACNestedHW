import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;
import java.util.NoSuchElementException;


/**
 * Represents the mappings for a single category of items that should
 * be displayed
 * 
 * @author Catie Baker & Nicole Moreno Gonzalez. 
 *
 */
public class AACCategory implements AACPage {

  private String categoryName; 
  private AssociativeArray<String, String> items;

	/**
	 * Creates a new empty category with the given name
	 * @param name the name of the category
	 */
	public AACCategory(String name) {
    this.categoryName = name;
    this.items = new AssociativeArray<>();
	}
	
	/**
	 * Adds the image location, text pairing to the category
	 * @param imageLoc the location of the image
	 * @param text the text that image should speak
	 */
	public void addItem(String imageLoc, String text) {
    try {
      items.set(imageLoc, text); 
    } catch (NullKeyException e) {
      System.err.println("Error: Image location cannot be null.");
    }
	}

	/**
	 * Returns an array of all the images in the category
	 * @return the array of image locations; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
    return items.keys();
	}

	/**
	 * Returns the name of the category
	 * @return the name of the category
	 */
	public String getCategory() {
		return this.categoryName;
	}

	/**
	 * Returns the text associated with the given image in this category
	 * @param imageLoc the location of the image
	 * @return the text associated with the image
	 * @throws NoSuchElementException if the image provided is not in the current
	 * 		   category
	 */
	public String select(String imageLoc) {
    try {
      return items.get(imageLoc); 
    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Image not found: " + imageLoc);
    }
	}

	/**
	 * Determines if the provided images is stored in the category
	 * @param imageLoc the location of the category
	 * @return true if it is in the category, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return items.hasKey(imageLoc);
	}

  public String getCurrentCategory() {
    return getCategory();
  }
}
