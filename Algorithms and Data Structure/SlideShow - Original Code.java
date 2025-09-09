import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public class SlideShow extends JFrame {

	//Declare Variables
	private JPanel slidePane;
	private JPanel textPane;
	private JPanel buttonPane;
	private CardLayout card;
	private CardLayout cardText;
	private JButton btnPrev;
	private JButton btnNext;
	private JLabel lblSlide;
	private JLabel lblTextArea;

	/**
	 * Create the application.
	 */
	public SlideShow() throws HeadlessException {
		initComponent();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initComponent() {
		//Initialize variables to empty objects
		card = new CardLayout();
		cardText = new CardLayout();
		slidePane = new JPanel();
		textPane = new JPanel();
		textPane.setBackground(Color.BLUE);
		textPane.setBounds(5, 470, 790, 50);
		textPane.setVisible(true);
		buttonPane = new JPanel();
		btnPrev = new JButton();
		btnNext = new JButton();
		lblSlide = new JLabel();
		lblTextArea = new JLabel();

		//Setup frame attributes
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Top 5 Destinations SlideShow");
		getContentPane().setLayout(new BorderLayout(10, 50));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Setting the layouts for the panels
		slidePane.setLayout(card);
		textPane.setLayout(cardText);
		
		//logic to add each of the slides and text
		for (int i = 1; i <= 5; i++) {
			lblSlide = new JLabel();
			lblTextArea = new JLabel();
			lblSlide.setText(getResizeIcon(i));
			lblTextArea.setText(getTextDescription(i));
			slidePane.add(lblSlide, "card" + i);
			textPane.add(lblTextArea, "cardText" + i);
		}

		getContentPane().add(slidePane, BorderLayout.CENTER);
		getContentPane().add(textPane, BorderLayout.SOUTH);

		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

		btnPrev.setText("Previous");
		btnPrev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goPrevious();
			}
		});
		buttonPane.add(btnPrev);

		btnNext.setText("Next");
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goNext();
			}
		});
		buttonPane.add(btnNext);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

	/**
	 * Previous Button Functionality
	 */
	private void goPrevious() {
		card.previous(slidePane);
		cardText.previous(textPane);
	}
	
	/**
	 * Next Button Functionality
	 */
	private void goNext() {
		card.next(slidePane);
		cardText.next(textPane);
	}

	/**
	 * Method to get the images
	 */
		
		//Added new images to the resources and changed out the old test images with new resources. Comments crediting the authors are below.
	
	private String getResizeIcon(int i) {
		String image = ""; 
		if (i==1){
			image = "<html><body><img width= '800' height='500' src='" + getClass().getResource("/resources/CostaRica.jpg") + "'</body></html>";
		} else if (i==2){
			image = "<html><body><img width= '800' height='500' src='" + getClass().getResource("/resources/Bali.jpg") + "'</body></html>";
		} else if (i==3){
			image = "<html><body><img width= '800' height='500' src='" + getClass().getResource("/resources/MeditationIndia.jpg") + "'</body></html>";
		} else if (i==4){
			image = "<html><body><img width= '800' height='500' src='" + getClass().getResource("/resources/GreeceCruise.jpg") + "'</body></html>";
		} else if (i==5){
			image = "<html><body><img width= '800' height='500' src='" + getClass().getResource("/resources/Thailand.jpg") + "'</body></html>";
		}
		return image;
	}
		//Credited Authors For Images Below:
		//Bali - By Photo by CEphoto, Uwe Aranas or alternatively © CEphoto, Uwe Aranas, CC BY-SA 3.0, https://commons.wikimedia.org/w/index.php?curid=38846610
		//CostaRica - By Pigment-Ink - Own work, CC BY-SA 4.0, https://commons.wikimedia.org/w/index.php?curid=91454787
		//India - By © Vyacheslav Argenberg / http://www.vascoplanet.com/, CC BY 4.0, https://commons.wikimedia.org/w/index.php?curid=106020418
		//Greece - By Mstyslav Chernov - Self-photographed, http://mstyslav-chernov.com/, CC BY-SA 3.0, https://commons.wikimedia.org/w/index.php?curid=25788984
		//Thailand - By Koh Lipe Thailand: Koh Lipe Beach Resort - http://www.lipebeachresort.com/, CC0, https://commons.wikimedia.org/w/index.php?curid=46108523 
	/**
	 * Method to get the text values
	 */
	
		//Added proper descriptors for the changed images within the slideshow. Added change in font sizes for header of each image as well.
	
	private String getTextDescription(int i) {
		String text = ""; 
		if (i==1){
			text = "<html><body><font size='5'>#1 Costa Rica Wellness Resort.</font> <br>Spectacular jungle views and hiking.</body></html>";
		} else if (i==2){
			text = "<html><body><font size='5'>#2 Bali Detox Resort.</font> <br> Island getaway with diving and snorkling</body></html>";
		} else if (i==3){
			text = "<html><body><font size='5'>#3 Udaipur, Rajasthan, India Meditation Retreat. </font> <br> Breathtaking view of the landscape that helps with tranquil meditation.</body></html>";
		} else if (i==4){
			text = "<html><body><font size='5'>#4 Mykonos Island Detox Cruise. </font> <br> A luxiruous Mediterranean cruise. </body></html>";
		} else if (i==5){
			text = "<html><body><font size='5'>#5 Koh Lipe Wellness Beach Resort. </font> <br> A woundrous coastal view</body></html>";
		}
		return text;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				SlideShow ss = new SlideShow();
				ss.setVisible(true);
			}
		});
	}
}