import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Locale;
import java.util.Scanner;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

/**
 * Creates a GUI that has a grid of images that represent the communication
 * device of the AAC.
 * 
 * @author Catie Baker
 *
 */
public class AAC implements ActionListener {

	private JFrame frame;
	private static Synthesizer synthesizer;
	private int startIndex;
	private int endIndex;
	private static final int NUM_ACROSS = 3;
	private static final int NUM_DOWN = 3;
	private String[] images;
	private AACPage page;
	private Scanner input;

	/**
	 * Creates the AAC display for the file provided
	 * 
	 * @param filename the name of the file that contains the images and text that
	 *                 will be in the AAC
	 */
	public AAC(String filename) {
		// this.page = new AACCategory("test");
		this.page = new AACMappings(filename);
		this.images = this.page.getImageLocs();
		this.startIndex = 0;
		this.endIndex = Math.min(NUM_ACROSS * NUM_DOWN, this.images.length);
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(500, 500));
		loadImages(NUM_ACROSS, NUM_DOWN);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		input = new Scanner(System.in);
	}

	/**
	 * Loads the images in the screen in a width by length grid
	 * 
	 * @param width  the number of images across to display
	 * @param length the number of images down to display
	 */
	public void loadImages(int width, int length) {
		Container pane = frame.getContentPane();
		pane.removeAll();

		// add options to go to home screen
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		String category = "Home Page";
		if (!this.page.getCategory().equals("")) {
			category = this.page.getCategory();
		}
		c.gridx = 0;
		c.gridy = 0;
		JLabel cat = new JLabel(category);
		cat.setFont(new Font("Serif", Font.PLAIN, 36));
		topPanel.add(cat, c);
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 3));
		if (this.page instanceof AACMappings) {
			JButton home = new JButton(new ImageIcon("img/home.png"));
			home.setActionCommand("");
			home.addActionListener(this);
			panel1.add(home);
			JButton save = new JButton(new ImageIcon("img/save.png"));
			save.setActionCommand("save");
			save.addActionListener(this);
			panel1.add(save);
		}
		JButton add = new JButton(new ImageIcon("img/plus.png"));
		add.setActionCommand("add");
		add.addActionListener(this);
		panel1.add(add);
		c.gridx = 0;
		c.gridy = 1;
		topPanel.add(panel1, c);
		pane.add(topPanel, BorderLayout.PAGE_START);

		// if on page 2+, add back button
		if (startIndex > 0) {
			JButton backArrow = new JButton(new ImageIcon("img/back-to.png"));
			backArrow.setActionCommand("back");
			backArrow.addActionListener(this);
			pane.add(backArrow, BorderLayout.LINE_START);
		}

		// add images
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(width, length));
		int currImage = startIndex;
		for (int y = 0; y < length; y++) {
			for (int x = 0; x < width; x++) {
				if (currImage < this.images.length) {
					ImageIcon img = new ImageIcon(images[currImage]);
					JButton button = new JButton(img);
					button.setActionCommand(images[currImage]);
					button.addActionListener(this);
					panel.add(button);
					currImage++;

				}
			}
		}
		pane.add(panel, BorderLayout.CENTER);

		// if have later pages, add next button
		if (endIndex < images.length) {
			JButton nextArrow = new JButton(new ImageIcon("img/next.png"));
			nextArrow.setActionCommand("next");
			nextArrow.addActionListener(this);
			pane.add(nextArrow, BorderLayout.LINE_END);
		}

		JLabel ack = new JLabel("All provided icons are from icons8: icons8.com");
		pane.add(ack, BorderLayout.PAGE_END);
		pane.revalidate();
		pane.requestFocusInWindow();
	}

	public static void main(String[] args) {

		try {
			// Set property as Kevin Dictionary
			System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");

			// Register Engine
			Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");
			synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));

			// Allocate synthesizer
			synthesizer.allocate();

			// Resume Synthesizer
			synthesizer.resume();

		} catch (Exception e) {
			e.printStackTrace();
		}
		AAC aac = new AAC("AACMappings.txt");
	}

	/**
	 * Responds to the click of a button. If the button is a category or action
	 * (e.g. home, next), it updates the screen. If the button is an image within
	 * the category, it speaks aloud the text
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("back")) {
			this.startIndex -= NUM_ACROSS * NUM_DOWN;
			this.endIndex -= NUM_ACROSS * NUM_DOWN;
		} else if (actionCommand.equals("next")) {
			this.startIndex += NUM_ACROSS * NUM_DOWN;
			this.endIndex = Math.min(endIndex + NUM_ACROSS * NUM_DOWN, this.images.length);
		} else if (actionCommand.equals("save") && this.page instanceof AACMappings) {
			((AACMappings) this.page).writeToFile("AACMappingsNew.txt");
			this.images = this.page.getImageLocs();
			this.startIndex = 0;
			this.endIndex = Math.min(NUM_ACROSS * NUM_DOWN, this.images.length);
		} else if (actionCommand.equals("add")) {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(frame);
			String imageLoc = "";
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				imageLoc = chooser.getSelectedFile().getPath();
				String result = (String) JOptionPane.showInputDialog(frame, "What is the text?", "AAC Add",
						JOptionPane.PLAIN_MESSAGE, null, null, "");
				if (result != null && result.length() > 0) {
					this.page.addItem(imageLoc, result);
				}
			}
			this.images = this.page.getImageLocs();
			this.startIndex = 0;
			this.endIndex = Math.min(NUM_ACROSS * NUM_DOWN, this.images.length);
		} else if (actionCommand.equals("") && this.page instanceof AACMappings) {
			((AACMappings) this.page).reset();
			this.images = this.page.getImageLocs();
			this.startIndex = 0;
			this.endIndex = Math.min(NUM_ACROSS * NUM_DOWN, this.images.length);
		} else {
			if (this.page.getCategory().equals("")) {
				this.page.select(actionCommand);
				this.images = this.page.getImageLocs();
				this.startIndex = 0;
				this.endIndex = Math.min(NUM_ACROSS * NUM_DOWN, this.images.length);
			} else {
				try {
					String toSpeak = this.page.select(actionCommand);
					synthesizer.speakPlainText(toSpeak, null);
					synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		loadImages(NUM_ACROSS, NUM_DOWN);

	}

}
