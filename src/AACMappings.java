import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & Nicole Moreno Gonzalez 
 *
 */
public class AACMappings implements AACPage {

  private AssociativeArray<String, AACCategory> categoryMappings;
  private AACCategory currentCategory;
  private String currentCategoryName;
	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
    categoryMappings = new AssociativeArray<>();
    currentCategory = null;
    currentCategoryName = ""; 

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      AACCategory category = null;
      while ((line = br.readLine()) != null) {
        if (!line.startsWith(">")) {
          // New category
          String[] parts = line.split(" ", 2);
          category = new AACCategory(parts[1]); 
          categoryMappings.set(parts[0], category); 
        } else {                
          if (category != null) {
            String[] item = line.substring(1).split(" ", 2);
            category.addItem(item[0], item[1]);
          }
        }
      }
    } catch (IOException | NullKeyException e) {
        e.printStackTrace();
    }
	}
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) {
        if (currentCategoryName.isEmpty()) {
            // At top level: switching categories
            if (!categoryMappings.hasKey(imageLoc)) {
                throw new NoSuchElementException("Category not found: " + imageLoc);
            }
            currentCategoryName = imageLoc;
            try {
                currentCategory = categoryMappings.get(imageLoc);
            } catch (KeyNotFoundException e) {
                throw new NoSuchElementException("Error accessing category: " + imageLoc);
            }
            return ""; // Empty because we're switching categories
        } else {
            // Inside a category: return associated text
            if (!currentCategory.hasImage(imageLoc)) {
                throw new NoSuchElementException("Image not found: " + imageLoc);
            }
            return currentCategory.select(imageLoc);
        }
	}
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
    if (currentCategoryName.isEmpty()) {
        return categoryMappings.keys();
    } else {
        return currentCategory.getImageLocs();
    }
	}
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
        currentCategoryName = "";
        currentCategory = null;
	}
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            String[] categoryKeys = categoryMappings.keys();
            for (String key : categoryKeys) {
                AACCategory category = categoryMappings.get(key);
                pw.println(key + " " + category.getCategory());
                String[] imageLocs = category.getImageLocs();
                for (String img : imageLocs) {
                    pw.println(">" + img + " " + category.select(img));
                }
            }
        } catch (IOException | KeyNotFoundException e) {
            e.printStackTrace();
        }	
	}
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
        if (currentCategory == null) {
            try {
                categoryMappings.set(imageLoc, new AACCategory(text));
            } catch (NullKeyException e) {
                e.printStackTrace();
            }
        } else {
            currentCategory.addItem(imageLoc, text);
        }
	}


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
        return currentCategoryName.isEmpty() ? "" : currentCategory.getCategory();
	}


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
	        if (currentCategory == null) {
            return categoryMappings.hasKey(imageLoc);
        }
        return currentCategory.hasImage(imageLoc);
	}
}
