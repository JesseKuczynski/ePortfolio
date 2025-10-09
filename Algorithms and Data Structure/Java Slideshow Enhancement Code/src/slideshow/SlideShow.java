package slideshow;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SlideShow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel slidePane;
    private JPanel textPane;
    private JPanel buttonPane;
    private JPanel bottomContainer;
    private CardLayout card;
    private CardLayout cardText;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnRandom;
    private JButton btnToggleTheme;
    private JLabel lblSlide;
    private JLabel lblTextArea;

    private final int totalSlides = 5;
    private int currentIndex = 1;     
    private final Random rng = new Random();

    // Theme 
    private static class Theme {
        final String name;
        final Color bg;     
        final Color fg;     
        final Color btnBg;  
        final Color btnFg;  
        Theme(String name, Color bg, Color fg, Color btnBg, Color btnFg) {
            this.name = name; this.bg = bg; this.fg = fg; this.btnBg = btnBg; this.btnFg = btnFg;
        }
    }

    private final Theme[] themes = new Theme[] {
        new Theme("Light",  new Color(235,243,255), Color.BLACK, new Color(246,250,255), Color.BLACK),
        new Theme("Dark",   new Color(30,34,39),    Color.WHITE, new Color(45,49,55),    Color.WHITE),
        new Theme("Blue",  new Color(12,61,89),    Color.WHITE, new Color(17,88,123),   Color.WHITE),
        new Theme("Green", new Color(24,52,36),    new Color(232,246,236), new Color(33,76,52), Color.WHITE),
        new Theme("Red", new Color(67,23,36),    new Color(255,232,220), new Color(90,28,45), Color.WHITE)
    };
    private int themeIndex = 0; // Initialize on Light theme

    public SlideShow() throws HeadlessException {
        initComponent();
    }

    private void initComponent() {
        card = new CardLayout();
        cardText = new CardLayout();
        slidePane = new JPanel(card);
        textPane  = new JPanel(cardText);
        buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPrev = new JButton("Previous");
        btnNext = new JButton("Next");
        btnRandom = new JButton("Random Slide");
        btnToggleTheme = new JButton("Theme: " + themes[themeIndex].name); 

        bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(textPane, BorderLayout.CENTER);
        bottomContainer.add(buttonPane, BorderLayout.SOUTH);

        // Setup frame attributes
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("Top 5 Destinations SlideShow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(slidePane, BorderLayout.CENTER);
        getContentPane().add(bottomContainer, BorderLayout.SOUTH);

        // Build slides
        for (int i = 1; i <= totalSlides; i++) {
            lblSlide = new JLabel();
            lblTextArea = new JLabel();

            lblSlide.setText(getResizeIcon(i));
            lblTextArea.setText(getTextDescription(i));
            lblTextArea.setFont(lblTextArea.getFont().deriveFont(Font.PLAIN, 16f));

         // Setting the layouts for the panels
            slidePane.add(lblSlide, "card" + i);
            textPane.add(lblTextArea, "cardText" + i);
        }

        // Logic to add each of the slides and text
        showByIndex(currentIndex);

        btnPrev.addActionListener((ActionEvent e) -> goPrevious());
        btnNext.addActionListener((ActionEvent e) -> goNext());
        btnRandom.addActionListener((ActionEvent e) -> jumpRandom());
        btnToggleTheme.addActionListener((ActionEvent e) -> toggleTheme());

        // Button Order
        buttonPane.add(btnPrev);
        buttonPane.add(btnRandom);
        buttonPane.add(btnNext);
        buttonPane.add(btnToggleTheme);

        // Initial theme
        applyTheme();
    }

    // Navigation
    private void showByIndex(int idx) {
        if (idx < 1) idx = totalSlides;
        if (idx > totalSlides) idx = 1;
        card.show(slidePane, "card" + idx);
        cardText.show(textPane, "cardText" + idx);
        currentIndex = idx;
    }

    private void goPrevious() { showByIndex(currentIndex - 1); }
    private void goNext()     { showByIndex(currentIndex + 1); }

    private void jumpRandom() {
        int idx;
        do { idx = rng.nextInt(totalSlides) + 1; } while (idx == currentIndex);
        showByIndex(idx);
    }

    private void toggleTheme() {
        themeIndex = (themeIndex + 1) % themes.length;
        applyTheme();
    }

    private void applyTheme() {
        Theme t = themes[themeIndex];

        // Panels
        slidePane.setBackground(t.bg);
        textPane.setBackground(t.bg);
        buttonPane.setBackground(t.bg);
        bottomContainer.setBackground(t.bg);

        // Text labels 
        for (int i = 1; i <= totalSlides; i++) {
            JLabel txt = (JLabel) textPane.getComponent(i - 1);
            txt.setForeground(t.fg);
        }

        // Buttons
        JButton[] buttons = { btnPrev, btnNext, btnRandom, btnToggleTheme };
        for (JButton b : buttons) {
            b.setBackground(t.btnBg);
            b.setForeground(t.btnFg);
            b.setFocusPainted(false);
        }

 
        btnToggleTheme.setText("Theme: " + t.name);
    }

    // Images
    private String getResizeIcon(int i) {
        String image = "";
        if (i == 1) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/CostaRica.jpg") + "'></body></html>";
        } else if (i == 2) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/Bali.jpg") + "'></body></html>";
        } else if (i == 3) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/MeditationIndia.jpg") + "'></body></html>";
        } else if (i == 4) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/GreeceCruise.jpg") + "'></body></html>";
        } else if (i == 5) {
            image = "<html><body><img width='800' height='500' src='" + getClass().getResource("/resources/Thailand.jpg") + "'></body></html>";
        }
        return image;
    }

    private String getTextDescription(int i) {
        String text = "";
        if (i == 1) {
            text = "<html><body><font size='5'>#1 Costa Rica Wellness Resort.</font> <br>Spectacular jungle views and hiking.</body></html>";
        } else if (i == 2) {
            text = "<html><body><font size='5'>#2 Bali Detox Resort.</font> <br> Island getaway with diving and snorkeling.</body></html>";
        } else if (i == 3) {
            text = "<html><body><font size='5'>#3 Udaipur, Rajasthan, India Meditation Retreat.</font> <br> Breathtaking landscape that helps with tranquil meditation.</body></html>";
        } else if (i == 4) {
            text = "<html><body><font size='5'>#4 Mykonos Island Detox Cruise.</font> <br> A luxurious Mediterranean cruise.</body></html>";
        } else if (i == 5) {
            text = "<html><body><font size='5'>#5 Koh Lipe Wellness Beach Resort.</font> <br> A wondrous coastal view.</body></html>";
        }
        return text;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SlideShow ss = new SlideShow();
            ss.setVisible(true);
        });
    }
}
