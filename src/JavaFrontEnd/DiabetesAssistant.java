package JavaFrontEnd;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DiabetesAssistant extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Map<String, String> userData = new HashMap<>();
    private Map<String, Integer> attemptCount = new HashMap<>();
    private Color bgColor = new Color(245, 248, 250);
    private Color primaryColor = new Color(41, 128, 185);
    private Color accentColor = new Color(52, 152, 219);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(231, 76, 60);

 // Measurement configuration with index tracking
    private static class MeasurementConfig {
        String name;
        String displayName;
        String unit;
        double min;
        double max;
        double absoluteMin;
        double absoluteMax;
        String instructions;
        int index;  // Added index tracking

        public MeasurementConfig(String name, String displayName, String unit, 
                                double min, double max, double absoluteMin, double absoluteMax,
                                String instructions, int index) {
            this.name = name;
            this.displayName = displayName;
            this.unit = unit;
            this.min = min;
            this.max = max;
            this.absoluteMin = 0;
            this.absoluteMax = absoluteMax;
            this.absoluteMin = absoluteMin;
            this.instructions = instructions;
            this.index = index;  // Store index position
        }
        
        // Added ordinal method to get position
        public int ordinal() {
            return index;
        }
    }

    // Updated measurements with index parameter
    private static final MeasurementConfig[] MEASUREMENTS = {
        new MeasurementConfig("glucose", "fasting blood glucose level", "mg/dL", 70, 100, 40, 400,
            "<html><b>How to measure blood glucose:</b><br><br>"
            + "1. <b>At home:</b> Use a glucose meter after 8 hours of fasting<br>"
            + "2. <b>At pharmacy:</b> Visit any pharmacy for a quick test<br>"
            + "3. <b>Doctor's office:</b> Schedule a blood test with your physician</html>", 0),
            
        new MeasurementConfig("diastolic", "diastolic blood pressure (lower number)", "mm Hg", 60, 80, 30, 130,
            "<html><b>How to measure blood pressure:</b><br><br>"
            + "• Sit quietly for 5 minutes before measuring<br>"
            + "• Place cuff on bare upper arm at heart level<br>"
            + "• Avoid caffeine/exercise 30 minutes before</html>", 1),
            
        new MeasurementConfig("skin_thickness", "triceps skin fold thickness", "mm", 10, 25, 5, 60,
            "<html><b>About skin fold measurement:</b><br><br>"
            + "This is typically measured by health professionals<br>"
            + "using specialized calipers at the back of your upper arm</html>", 2),
            
        new MeasurementConfig("insulin", "insulin level", "mu U/ml", 2, 25, 0.5, 300,
            "<html><b>About insulin levels:</b><br><br>"
            + "This requires a blood test ordered by your doctor.<br>"
            + "It measures how much insulin is in your bloodstream.</html>", 3),
            
        new MeasurementConfig("bmi", "BMI (Body Mass Index)", "", 18.5, 24.9, 10, 70,
            "<html><b>Calculating BMI:</b><br><br>"
            + "BMI = weight(kg) / height(m)<sup>2</sup><br>"
            + "Example: 70kg ÷ (1.75m × 1.75m) = 22.9</html>", 4),
            
        new MeasurementConfig("dpf", "Diabetes Pedigree Function", "", 0.1, 1.2, 0, 3,
            "<html><b>About this value:</b><br><br>"
            + "This estimates genetic influence based on family history.<br>"
            + "Your doctor can help calculate this for you.</html>", 5),
            
        new MeasurementConfig("age", "your age", "years", 18, 45, 1/*0.083*/, 160, 
            "<html><b>Why we ask:</b><br><br>"
            + "Diabetes risk increases with age.<br>"
            + "We use this to personalize your assessment.</html>", 6)
    };

    public DiabetesAssistant() {
        setTitle("Health Assistant");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(bgColor);
        
        createWelcomePanel();
        createDiabetesCheckPanel();
        createTestIntroPanel();
        createPregnancyPanel();
        createPregnancyTimesPanel();
        createMeasurementPanels();
        createResultPanel();
        
        add(cardPanel);
        cardLayout.show(cardPanel, "welcome");
        setVisible(true);
    }

    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("welcome");
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Welcome to Your Health Companion");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(primaryColor);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel message = new JLabel("<html><div style='text-align: center;'>Hi there! I'm your personal health assistant.<br>How are you feeling today?</div></html>");
        message.setFont(new Font("SansSerif", Font.PLAIN, 20));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(bgColor);
        
        RoundedButton greatBtn = new RoundedButton("Feeling great!");
        styleButton(greatBtn, successColor);
        greatBtn.addActionListener(e -> {      	
        	cardLayout.show(cardPanel, "diabetes_check");
        });
        
        RoundedButton notGoodBtn = new RoundedButton("Not so good");
        styleButton(notGoodBtn, warningColor);
        notGoodBtn.addActionListener(e -> {
        	cardLayout.show(cardPanel, "diabetes_check");
        });
        
        buttonPanel.add(greatBtn);
        buttonPanel.add(notGoodBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(message, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        cardPanel.add(panel, "welcome");
    }

    private void createDiabetesCheckPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("diabetes_check");
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel message = new JLabel("<html><div style='text-align: center;'>Taking care of your health is important.<br>Would you like to check your diabetes risk today?</div></html>");
        message.setFont(new Font("SansSerif", Font.PLAIN, 20));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel quote = new JLabel("<html><div style='text-align: center;'><i>\"Prevention is better than cure.\"</i></div></html>");
        quote.setFont(new Font("SansSerif", Font.ITALIC, 16));
        quote.setForeground(new Color(100, 100, 100));
        quote.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(bgColor);
        
        RoundedButton yesBtn = new RoundedButton("Yes, let's do it");
        styleButton(yesBtn, successColor);
        yesBtn.addActionListener(e -> {
        	cardLayout.show(cardPanel, "test_intro");
        });
        
        RoundedButton noBtn = new RoundedButton("Not now");
        styleButton(noBtn, warningColor);
        noBtn.addActionListener(e -> cardLayout.show(cardPanel, "welcome"));
        
        buttonPanel.add(yesBtn);
        buttonPanel.add(noBtn);
        
        addBackButton(panel, "welcome");
        addExitButton(panel);
        
        panel.add(quote, BorderLayout.NORTH);
        panel.add(message, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        cardPanel.add(panel, "diabetes_check");
    }

    private void createTestIntroPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel title = new JLabel("Quick & Easy Check");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(primaryColor);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel message = new JLabel("<html><div style='text-align: center; padding: 0 50px;'>"
            + "Great choice! This will only take a few minutes.<br><br>"
            + "We'll ask for some simple health measurements<br>"
            + "that you can easily check at home or with your doctor."
            + "</div></html>");
        message.setFont(new Font("SansSerif", Font.PLAIN, 18));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel tip = new JLabel("<html><div style='text-align: center; color: #2E86C1;'>"
            + "Tip: Have your recent health numbers handy if possible!"
            + "</div></html>");
        tip.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 14));
        tip.setHorizontalAlignment(SwingConstants.CENTER);
        
        RoundedButton startBtn = new RoundedButton("I'm ready to begin!");
        styleButton(startBtn, accentColor);
        startBtn.setPreferredSize(new Dimension(250, 50));
        startBtn.addActionListener(e -> cardLayout.show(cardPanel, "pregnancy"));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        buttonPanel.add(startBtn);
        
        addBackButton(panel, "diabetes_check");
        addExitButton(panel);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(message, BorderLayout.CENTER);
        panel.add(tip, BorderLayout.SOUTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        //this.setVisible (true);
        cardPanel.add(panel, "test_intro");
    }

    private void createPregnancyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel question = new JLabel("<html><div style='text-align: center;'>For our health assessment,<br>have you ever been pregnant?</div></html>");
        question.setFont(new Font("SansSerif", Font.PLAIN, 20));
        question.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(bgColor);
        
        RoundedButton yesBtn = new RoundedButton("Yes");
        styleButton(yesBtn, accentColor);
        yesBtn.addActionListener(e -> cardLayout.show(cardPanel, "pregnancy_times"));
        
        RoundedButton noBtn = new RoundedButton("No");
        styleButton(noBtn, accentColor);
        noBtn.addActionListener(e -> {
        	userData.put("pregnancies", "0");
        	cardLayout.show(cardPanel, "glucose");
        });
        
        buttonPanel.add(yesBtn);
        buttonPanel.add(noBtn);
        
        addBackButton(panel, "test_intro");
        addExitButton(panel);
        
        panel.add(question, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        cardPanel.add(panel, "pregnancy");
    }

    private void createPregnancyTimesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel question = new JLabel("<html><div style='text-align: center;'>How many times have you been pregnant?</div></html>");
        question.setFont(new Font("SansSerif", Font.PLAIN, 20));
        question.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(bgColor);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        JTextField timesField = new JTextField(10);
        timesField.setMaximumSize(new Dimension(200, 40));
        timesField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        timesField.setHorizontalAlignment(JTextField.CENTER);
        
        JLabel example = new JLabel("(e.g., 1, 2, 3...)");
        example.setFont(new Font("SansSerif", Font.ITALIC, 14));
        example.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(timesField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(example);
        
        RoundedButton nextBtn = new RoundedButton("Continue");
        styleButton(nextBtn, accentColor);
        nextBtn.addActionListener(e -> {
            try {
                int times = Integer.parseInt(timesField.getText().trim());
                if (times >= 0) {
                    userData.put("pregnancies", String.valueOf(times));
                    cardLayout.show(cardPanel, "glucose");
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a number 0 or higher", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        addBackButton(panel, "pregnancy");
        addExitButton(panel);
        
        panel.add(question, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(nextBtn, BorderLayout.SOUTH);
        
        cardPanel.add(panel, "pregnancy_times");
    }

    private void createMeasurementPanels() {
        for (int i = 0; i < MEASUREMENTS.length; i++) {
            MeasurementConfig config = MEASUREMENTS[i];
            String nextCard = (i < MEASUREMENTS.length - 1) ? MEASUREMENTS[i + 1].name : "result";
            cardPanel.add(createMeasurementPanel(config, nextCard), config.name);
            cardPanel.add(createInstructionsPanel(config), "instructions_" + config.name);
        }
    }

    private JPanel createMeasurementPanel(MeasurementConfig config, String nextCard) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        String rangeText = String.format("(Normal range: %.0f-%.0f %s)", config.min, config.max, config.unit);
        JLabel question = new JLabel("<html><div style='text-align: center;'>What is your " + config.displayName + "?<br>" + rangeText + "</div></html>");
        question.setFont(new Font("SansSerif", Font.PLAIN, 20));
        question.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(bgColor);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        JTextField valueField = new JTextField(10);
        valueField.setMaximumSize(new Dimension(200, 40));
        valueField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        valueField.setHorizontalAlignment(JTextField.CENTER);
        
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(valueField);
        
        RoundedButton nextBtn = new RoundedButton("Continue");
        styleButton(nextBtn, accentColor);
        
        RoundedButton dontKnowBtn = new RoundedButton("I'm not sure");
        styleButton(dontKnowBtn, new Color(149, 165, 166));
        dontKnowBtn.addActionListener(e -> cardLayout.show(cardPanel, "instructions_" + config.name));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        buttonPanel.add(dontKnowBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(nextBtn);
        
        nextBtn.addActionListener(e -> {
            String input = valueField.getText().trim();
            if (input.isEmpty()) {
                handleInvalidInput(config, "Please enter a value");
                return;
            }
            
            try {
                double value = Double.parseDouble(input);
                if (value < config.absoluteMin || value > config.absoluteMax) {
                    handleInvalidInput(config, String.format("Please enter between %.0f and %.0f", 
                        config.absoluteMin, config.absoluteMax));
                } else {
                    userData.put(config.name, String.valueOf(value));
                    attemptCount.put(config.name, 0); // Reset attempts
                    if (nextCard.equals("result")) {
                    	
                        if (checkAndHandleMissingValues()) {
                        	String prediction = callModel();
                        	
                        	new Thread(() -> {
                        	    try {
                        	        Thread.sleep(3000); // 3 seconds
                        	    } catch (InterruptedException ee) {
                        	        //Nothing happens if the thread fails to sleep.
                        	    }

                        	    // Switch to final result panel on the Event Dispatch Thread
                        	    SwingUtilities.invokeLater(() -> {
                        	    	createFinalResultPanel(prediction);
                        	        cardLayout.show(cardPanel, "result_final");
                        	    });
                        	}).start();

                        } else {
                        	JOptionPane.showMessageDialog(this, "Can't predict from the given values. The model needs all the information.", "Error", JOptionPane.WARNING_MESSAGE);
                        	cardLayout.show(cardPanel, "welcome");
                        }  
                        
                    } else {
                        cardLayout.show(cardPanel, nextCard);
                    }
                }
            } catch (NumberFormatException ex) {
                handleInvalidInput(config, "Please enter a valid number");
            }
        });
        
        // Fixed back button navigation using config.ordinal()
        String backScreen = (config.ordinal() == 0) ? "pregnancy_times" : MEASUREMENTS[config.ordinal() - 1].name;
        addBackButton(panel, backScreen);
        addExitButton(panel);
        
        panel.add(question, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void handleInvalidInput(MeasurementConfig config, String message) {
        int attempts = attemptCount.getOrDefault(config.name, 0) + 1;
        attemptCount.put(config.name, attempts);
        
        if (attempts >= 3) {
            userData.put(config.name, "null");
            JOptionPane.showMessageDialog(this, "We'll proceed without this value", "Information", JOptionPane.INFORMATION_MESSAGE);
            
            // Fixed next card navigation using config.ordinal()
            String nextCard = (config.ordinal() == MEASUREMENTS.length - 1) ? 
                "result" : MEASUREMENTS[config.ordinal() + 1].name;
                
            cardLayout.show(cardPanel, nextCard);
        } else {
            JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createInstructionsPanel(MeasurementConfig config) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("Measurement Help");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(primaryColor);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextPane instructions = new JTextPane();
        instructions.setContentType("text/html");
        instructions.setText(config.instructions);
        instructions.setEditable(false);
        instructions.setBackground(bgColor);
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        RoundedButton backBtn = new RoundedButton("← Back to Question");
        styleButton(backBtn, accentColor);
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, config.name));
        
        addExitButton(panel);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(instructions), BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private void createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel title = new JLabel("Assessment Complete!");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(primaryColor);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel message = new JLabel("<html><div style='text-align: center; padding: 20px;'>"
            + "Thank you for providing your health information.<br><br>"
            + "We're now analyzing your data with our medical AI...<br>"
            + "This usually takes just a moment."
            + "</div></html>");
        message.setFont(new Font("SansSerif", Font.PLAIN, 18));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        
        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(bgColor);
        progressPanel.add(progressBar);
        
        addExitButton(panel);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(message, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);
        
        cardPanel.add(panel, "result");
    }

    // Helper methods
    private void styleButton(RoundedButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 45));
    }
    
    private void addBackButton(JPanel panel, String backScreen) {
        RoundedButton backBtn = new RoundedButton("← Back");
        styleButton(backBtn, new Color(149, 165, 166));
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, backScreen));

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeft.setBackground(bgColor);
        topLeft.add(backBtn);

        panel.add(topLeft, BorderLayout.WEST); // Add to the WEST or NORTHWEST region
    }

    private void addExitButton(JPanel panel) {
        RoundedButton exitBtn = new RoundedButton("Exit");
        styleButton(exitBtn, new Color(149, 165, 166));
        exitBtn.addActionListener(e -> {
            userData.clear();
            attemptCount.clear();
            cardLayout.show(cardPanel, "welcome");
        });

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRight.setBackground(bgColor);
        topRight.add(exitBtn);

        panel.add(topRight, BorderLayout.EAST); // Add to the EAST or NORTHEAST region
    }

    // Custom Rounded Button
    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
    
 // 🆕 Ask about missing data before result
    private boolean checkAndHandleMissingValues() {
        boolean hasNulls = userData.values().stream().anyMatch(v -> v.equals("null"));
        
        if (hasNulls) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Some values are missing. Do you want to fill them with default values?",
                "Missing Data",
                JOptionPane.YES_NO_OPTION);
            	//print ();
            if (choice == JOptionPane.YES_OPTION) {
                fillMissingWithDefaults();  // 🆕 fill nulls with config.min
                //callModel();
            } else {
            	return false;
            }
        }
        
        //  Always go to result panel after handling
        cardLayout.show(cardPanel, "result");
        return true;
    }
    
 // Fill null values with config.min
    private void fillMissingWithDefaults() {
        for (MeasurementConfig config : MEASUREMENTS) {
            if (!userData.containsKey(config.name) || userData.get(config.name).equals("null")) {
                userData.put(config.name, String.valueOf(config.min));
            }
        }
    }

    private String callModel() {
    	
    	String [] data = new String [userData.size()];
    	
    	int cont = 0;
    	data[cont] = userData.get("pregnancies");
    	cont++;
    	
    	for (int i = 0; i < MEASUREMENTS.length; i++) {
    	    String key = MEASUREMENTS[i].name;
    	    String valueStr = userData.get(key);

    	    if (valueStr != null && !valueStr.equals("null")) {
    	       
    	    	data[cont] = valueStr.trim();
    	    	cont++;
    	    } else {
    	    	return "Can't predict using incomplete data";
    	    }
    	    
    	}
    	ProcessBuilder pb = new ProcessBuilder(
                "python", 
                "Model/predict_diabetes.py",
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                data[6],
                data[7]
            );
    	// Start the process
        try {
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(
		    new InputStreamReader(process.getInputStream()));
		    String prediction = reader.readLine();
		    
		    return prediction;
		} catch (IOException e) {
			return "The model has failed to predict.";
		}
    	
    }
    
    private void createFinalResultPanel(String prediction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Your Health Assessment Result");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(primaryColor);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        String messageText;
        if ("1".equalsIgnoreCase(prediction.trim())) {
            messageText = "<html><div style='text-align: center;'>⚠️ Based on the data, there's a high chance of diabetes.<br>"
                + "Please consult a healthcare professional soon.<br><br>"
                + "<b>Tip:</b> Monitor your glucose, exercise regularly, and maintain a healthy diet.</div></html>";
        } else {
        	if (prediction.trim().equalsIgnoreCase("0")) {
        		messageText = "<html><div style='text-align: center;'>🎉 Your numbers look good!<br>"
                        + "Keep up with regular check-ups and healthy living.</div></html>";
        	} else {
        		messageText = prediction;
        	}
            
        }

        JLabel message = new JLabel(messageText);
        message.setFont(new Font("SansSerif", Font.PLAIN, 18));
        message.setHorizontalAlignment(SwingConstants.CENTER);

        RoundedButton restartBtn = new RoundedButton("↩ Restart");
        styleButton(restartBtn, accentColor);
        restartBtn.addActionListener(e -> {
            userData.clear();
            attemptCount.clear();
            cardLayout.show(cardPanel, "welcome");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(bgColor);
        bottomPanel.add(restartBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(message, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "result_final");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DiabetesAssistant app = new DiabetesAssistant();
            app.setVisible(true);
        });
 

    }
}