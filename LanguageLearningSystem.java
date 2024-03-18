import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.AbstractDocument.LeafElement;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
class qframe extends JFrame{
    public String[][] questions;
    public String[][][] options;
    public String[][] answers;

    public qframe(String username,int lang,String[][] questions, String[][][] options, String[][] answers) {
        this.questions = questions;
        this.options = options;
        this.answers = answers;

        setTitle("Quiz");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setPreferredSize(new Dimension(1500, 200));        

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(welcomeLabel);
        topPanel.setBorder(new EmptyBorder(5, 5, 0, 0)); 

        JLabel availableLanguagesLabel = new JLabel(LanguageLearningSystem.languages[lang-1] + "Quiz");
        availableLanguagesLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(availableLanguagesLabel);
        availableLanguagesLabel.setBorder(new EmptyBorder(5, 400, 0, 225)); 

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageLearningSystem.openMenuWindow(username);
                dispose(); 
            }
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageLearningSystem.frame.setVisible(true);
                dispose();
            }
        });
        
        toolbarPanel.add(backButton);
        toolbarPanel.add(logoutButton);
        topPanel.add(toolbarPanel);

        JPanel questionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

        for (int i = 0; i < 5; i++) {
            panel1.add(createQuestionPanel(i,lang));
        }

        for (int i = 5; i < 10; i++) {
            panel2.add(createQuestionPanel(i,lang));
        }
        questionPanel.add(panel1);
        questionPanel.add(panel2);

        JPanel submitPanel = new JPanel(new BorderLayout());
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int score = calculateScore(lang);
                updateQuizScore(username, lang, score);
                LanguageLearningSystem.openMenuWindow(username);
                dispose();
            }
        });
        submitPanel.add(submitButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(questionPanel, BorderLayout.CENTER);
        add(submitPanel, BorderLayout.SOUTH);
    }
    public JPanel createQuestionPanel(int index,int lang) {
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        JLabel questionLabel = new JLabel(questions[lang-1][index]);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionPanel.add(questionLabel);

        ButtonGroup buttonGroup = new ButtonGroup();
        for (String option : options[lang-1][index]) {
            JRadioButton radioButton = new JRadioButton(option);
            buttonGroup.add(radioButton);
            questionPanel.add(radioButton);
        }
        return questionPanel;
    }
    public int calculateScore(int lang) {
        int score = 0;
        for (int i = 0; i < 10; i++) {
            JPanel questionPanel;
            if (i < 5) {
                questionPanel = (JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(1)).getComponent(0)).getComponent(i);
            } else {
                questionPanel = (JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(1)).getComponent(1)).getComponent(i - 5);
            }
            JRadioButton selectedOption = getSelected(questionPanel);
            if (selectedOption != null) {
                String userAnswer = selectedOption.getText();
                if (userAnswer.equals(answers[lang-1][i])) {
                    score++;
                }
            }
        }
        return score;
    }
    public JRadioButton getSelected(JPanel questionPanel) {
        for (Component component : questionPanel.getComponents()) {
            if (component instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) component;
                if (radioButton.isSelected()) {
                    return radioButton;
                }
            }
        }
        return null; // No option selected
    }
    public void updateQuizScore(String username, int quizNumber, int score) {
        try {
            File file = new File("user_profiles.txt");
            File tempFile = new File("user_profiles_temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                    String storedUsername = parts[0];
                    if (username.equals(storedUsername)) {
                        int prevscore = Integer.parseInt(parts[quizNumber+3]);
                        if((prevscore == 11) || (score>prevscore)) {
                            parts[quizNumber + 3] = String.valueOf(score);
                            line = String.join(",",parts);
                        }
                    }
                writer.write(line + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            file.delete();
            tempFile.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
public class LanguageLearningSystem {
    public static  JFrame frame;
    public static String[] languages = {"English","French","Spanish"};
    public static void main(String[] args) {
        frame = new JFrame("Language Learning Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(frame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Welcome to Language Learning Program");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        centerPanel.add(titleLabel, constraints);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        centerPanel.add(usernameLabel, constraints);
        constraints.gridx = 1;
        centerPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        constraints.gridx = 0;
        constraints.gridy = 4;
        centerPanel.add(passwordLabel, constraints);
        constraints.gridx = 1;
        centerPanel.add(passwordField, constraints);

        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegistrationWindow();
                frame.dispose();    
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 5;
        centerPanel.add(signupButton, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (checkLogin(username, password)) {
                    openMenuWindow(username);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
                }
            }
        });

        constraints.gridx = 1;
        centerPanel.add(loginButton, constraints);
        panel.add(centerPanel, BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);
    }
    public static void openRegistrationWindow() {
        JFrame registrationFrame = new JFrame("Signup for a New Account");
        registrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registrationFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    
        JPanel registrationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 7, 6);

        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        registrationPanel.add(titleLabel, constraints);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 1;
        registrationPanel.add(emailLabel, constraints);
        constraints.gridx = 1;
        registrationPanel.add(emailField, constraints);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 2;
        registrationPanel.add(usernameLabel, constraints);
        constraints.gridx = 1;
        registrationPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        constraints.gridx = 0;
        constraints.gridy = 3;
        registrationPanel.add(passwordLabel, constraints);
        constraints.gridx = 1;
        registrationPanel.add(passwordField, constraints);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        constraints.gridx = 0;
        constraints.gridy = 4;
        registrationPanel.add(confirmPasswordLabel, constraints);
        constraints.gridx = 1;
        registrationPanel.add(confirmPasswordField, constraints);

        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(20);
        constraints.gridx = 0;
        constraints.gridy = 5;
        registrationPanel.add(ageLabel, constraints);
        constraints.gridx = 1;
        registrationPanel.add(ageField, constraints);

        JButton enterButton = new JButton("Enter");
        constraints.gridx = 1;
        constraints.gridy = 6;
        registrationPanel.add(enterButton, constraints);

        JButton backButton = new JButton("Back");
        constraints.gridx = 0;
        constraints.gridy = 6;
        registrationPanel.add(backButton,constraints);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
                registrationFrame.dispose();
            }
        });

        enterButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String dob = ageField.getText();

                if (!email.matches("\\w+@\\w+\\.\\w+")) {
                    JOptionPane.showMessageDialog(registrationFrame, "Invalid email format. Please use xyz@abc.com format.");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(registrationFrame, "Password and Confirm Password do not match.");
                    return;
                }
                if (usernameExists(username)) {
                    JOptionPane.showMessageDialog(registrationFrame, "Username already exists. Please choose a different username.");
                    return;
                }
                storeRegistrationInfo(username, password, email, dob);
                openMenuWindow(username);
                registrationFrame.dispose();
            }
        });
    
        registrationFrame.add(registrationPanel);
        registrationFrame.setVisible(true);
    }   
    public static void openMenuWindow(String username) {
        JFrame LangMenu = new JFrame("Menu");
        LangMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LangMenu.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setPreferredSize(new Dimension(1500, 100));        

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(welcomeLabel);
        topPanel.setBorder(new EmptyBorder(5, 5, 0, 0)); 

        JLabel LangLabel = new JLabel("Language Menu");
        LangLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(LangLabel);
        LangLabel.setBorder(new EmptyBorder(5, 400, 0, 250)); 

        JButton leaderboard = new JButton("LeaderBoard");
        leaderboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLeaderboard(username);
                LangMenu.dispose();
            }
        });
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true); 
                LangMenu.dispose();
            }
        });

        topPanel.add(leaderboard);
        topPanel.add(logoutButton);
        int[] userScores = getQuizScoresForUser(username);
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));

        JPanel subPanel1 = new JPanel();
        JPanel subPanel2 = new JPanel();
        JPanel subPanel3 = new JPanel();
        
        Font tFont = new Font("Arial", Font.BOLD, 18);
        TitledBorder t1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "ENGLISH");
        t1.setTitleJustification(t1.CENTER);
        t1.setTitleFont(tFont);
        subPanel1.setBorder(t1);
        
        TitledBorder t2 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "FRENCH");
        t2.setTitleJustification(t2.CENTER);
        t2.setTitleFont(tFont);
        subPanel2.setBorder(t2);
        
        TitledBorder t3 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "SPANISH");
        t3.setTitleJustification(t3.CENTER);
        t3.setTitleFont(tFont);
        subPanel3.setBorder(t3);
        
        JPanel bPanel1 = new JPanel();
        bPanel1.setLayout(new BoxLayout(bPanel1, BoxLayout.Y_AXIS)); 
        JPanel bPanel2 = new JPanel();
        bPanel2.setLayout(new BoxLayout(bPanel2, BoxLayout.Y_AXIS));
        JPanel bPanel3 = new JPanel();
        bPanel3.setLayout(new BoxLayout(bPanel3, BoxLayout.Y_AXIS));
        
        JButton E1Button = new JButton("Introduction to English");
        JButton E2Button = new JButton("Conversational Skills");
        JButton E3Button = new JButton("Different Tenses");
        JButton EQuizButton = new JButton("English Quiz");
        JButton F1Button = new JButton("Introduction to French");
        JButton F2Button = new JButton("Conversational Skills");
        JButton F3Button = new JButton("Different Tenses");
        JButton FQuizButton = new JButton("French Quiz");
        JButton S1Button = new JButton("Introduction to Spanish");
        JButton S2Button = new JButton("Conversational Skills");
        JButton S3Button = new JButton("Different Tenses");
        JButton SQuizButton = new JButton("Spanish Quiz");
        
        E1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("English - Lesson 1",1,1,username);
                LangMenu.dispose();
            }
        });
        E2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("English - Lesson 2",1,2,username);
                LangMenu.dispose();
            }
        });
        E3Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("English - Lesson 3",1,3,username);
                LangMenu.dispose();
            }
        });
        EQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openquiz(username, 1);
                LangMenu.dispose();
            }
        });
        F1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("FRENCH - Lesson 1",2,1,username);
                LangMenu.dispose();
            }
        });
        F2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("FRENCH - Lesson 2",2,2,username);
                LangMenu.dispose();
            }
        });
        F3Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("FRENCH - Lesson 3",2,3,username);
                LangMenu.dispose();
            }
        });
        FQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openquiz(username, 2);
                LangMenu.dispose();
            }
        });
        S1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("Spanish - Lesson 1",3,1,username);
                LangMenu.dispose();
            }
        });
        S2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("Spanish - Lesson 2",3,2,username);
                LangMenu.dispose();
            }
        });
        S3Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLessonFrame("Spanish - Lesson 3",3,3,username);
                LangMenu.dispose();
            }
        });
        SQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openquiz(username, 3);
                LangMenu.dispose();
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        bPanel1.add(E1Button);
        bPanel1.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel1.add(E2Button);
        bPanel1.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel1.add(E3Button);
        bPanel1.add(Box.createRigidArea(new Dimension(0, 100)));
        bPanel1.add(EQuizButton);
        JLabel score1;
        if(userScores[0]!=11) {
            score1 = new JLabel("Best Score: "+userScores[0]);
            score1.setFont(new Font("Arial",Font.PLAIN,18));
        }
        else {
            score1 = new JLabel("Not Attempted");
            score1.setFont(new Font("Arial",Font.PLAIN,18));
        }
        bPanel1.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel1.add(score1);
        subPanel1.setLayout(new GridBagLayout());
        subPanel1.add(bPanel1,gbc);

        bPanel2.add(F1Button);
        bPanel2.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel2.add(F2Button);
        bPanel2.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel2.add(F3Button);
        bPanel2.add(Box.createRigidArea(new Dimension(0, 100)));
        bPanel2.add(FQuizButton);
        
        JLabel score2;
        if(userScores[1]!=11) {
            score2 = new JLabel("Best Score: "+userScores[1]);
            score2.setFont(new Font("Arial",Font.PLAIN,18));
        }
        else {
            score2 = new JLabel("Not Attempted");
            score2.setFont(new Font("Arial",Font.PLAIN,18));
        }
        bPanel2.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel2.add(score2);
        subPanel2.setLayout(new GridBagLayout());
        subPanel2.add(bPanel2,gbc);

        bPanel3.add(S1Button);
        bPanel3.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel3.add(S2Button);
        bPanel3.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel3.add(S3Button);
        bPanel3.add(Box.createRigidArea(new Dimension(0, 100)));
        bPanel3.add(SQuizButton);
        JLabel score3;
        if(userScores[2]!=11) {
            score3 = new JLabel("Best Score: "+userScores[2]);
            score3.setFont(new Font("Arial",Font.PLAIN,18));
        }
        else {
            score3 = new JLabel("Not Attempted");
            score3.setFont(new Font("Arial",Font.PLAIN,18));
        }
        bPanel3.add(Box.createRigidArea(new Dimension(0, 20)));
        bPanel3.add(score3);
        subPanel3.setLayout(new GridBagLayout());
        subPanel3.add(bPanel3,gbc);

        columnPanel.add(subPanel1);
        columnPanel.add(subPanel2);
        columnPanel.add(subPanel3);

        LangMenu.add(columnPanel, BorderLayout.CENTER);
        LangMenu.add(topPanel, BorderLayout.NORTH);
        LangMenu.setVisible(true);
    }
    public static boolean checkLogin(String username, String password) {
        try (Scanner scanner = new Scanner(new File("user_profiles.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    if (username.equals(storedUsername) && password.equals(storedPassword)) {
                        return true;
                    }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void storeRegistrationInfo(String username, String password, String email, String dob) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_profiles.txt", true))) {
            writer.write(username + "," + password + "," + email + "," + dob + ",11,11,11");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean usernameExists(String username) {
        try (Scanner scanner = new Scanner(new File("user_profiles.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String storedUsername = parts[0];
                if (username.equals(storedUsername)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void openLessonFrame(String title,int la,int ls,String username) {
        JFrame lessonFrame = new JFrame(title);
        lessonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lessonFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setPreferredSize(new Dimension(1500, 100));        
        
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(welcomeLabel);
        topPanel.setBorder(new EmptyBorder(5, 5, 0, 0));

        JLabel lessonNo;
        lessonNo = new JLabel(languages[la-1] + " Lesson" + "-" + ls);
        lessonNo.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(lessonNo);
        lessonNo.setBorder(new EmptyBorder(5, 400, 0, 300));

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton menu = new JButton("Menu");
        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMenuWindow(username);            
                lessonFrame.dispose();
            }
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
                lessonFrame.dispose();
            }
        });
        
        toolbarPanel.add(menu);
        toolbarPanel.add(logoutButton);
        topPanel.add(toolbarPanel);
        lessonFrame.add(topPanel);

        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        JPanel lesson = new JPanel();

        Dimension dim = new Dimension(1200, 1000);

        if(la==1) {
            if(ls==1) {
                Font titleFont = new Font("Arial", Font.BOLD, 18);
                TitledBorder titledBorder1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Introduction to English");
                titledBorder1.setTitleJustification(titledBorder1.CENTER);
                titledBorder1.setTitleFont(titleFont);
                lesson.setBorder(titledBorder1);
                JTextArea alphabetList = new JTextArea(
                "-> The English language has 26 symbols/letters: \t\t -> These symbols can be written in lowercase as: \n\n" +
                "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z \t\t a b c d e f g h i j k l m n o p q r s t u v w x y z\n\n" +
                "-> Now, let's say each letter out loud as you read it. Practice your pronunciation.\n\n"+
                "-> There are 5 vowels:   A E I O U \n\n" + 
                "-> There are 21 consonants:   B C D F G H J K L M N P Q R S T V W X Y Z\n\n" +
                "-> Common phrases and words are: \tGood Morning    Thank You    Please    Excuse Me\n\n" +
                "-> Practice  conversations using these phrases\n\n" +
                "-> We form words using letters and sentences using words. Sentence are made up of:\n\n" +
                "1) Nouns: Nouns are words that represent people, places, things, or ideas in a sentence.\n\n"+
                "2) Verbs:Verbs are action words that express what the subject of a sentence does or is.\n\n" +
                "3) Adjectives: Adjectives are words that describe nouns, giving information about their characteristics.\n\n"+ 
                "4) Adverbs: Adverbs are words that modify verbs, adjectives, providing information about the manner, time, place.\n\n"+
                "-> The basic sentence structure in English is:\n\n" +
                "\"The quick brown fox (noun, adjectives) jumps (verb) happily (adverb) over the lazy dog (noun, adjectives).\""
                );
                alphabetList.setPreferredSize(dim);
                alphabetList.setFont(new Font("Arial", Font.PLAIN, 18));
                alphabetList.setWrapStyleWord(true);
                alphabetList.setLineWrap(true);
                alphabetList.setOpaque(false);
                alphabetList.setEditable(false);
                alphabetList.setFocusable(false);
                lesson.add(alphabetList);
                columnPanel.add(lesson);
            }
            else if(ls==2) {
                Font titleFont = new Font("Arial", Font.BOLD, 18);
                TitledBorder titledBorder1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Building Conversational Skills");
                titledBorder1.setTitleJustification(titledBorder1.CENTER);
                titledBorder1.setTitleFont(titleFont);
                lesson.setBorder(titledBorder1);
                JTextArea alphabetList = new JTextArea(
                "-> Learn how to talk about your day, hobbies, and interests. \t -> Practice introducing yourself and others.\n\n" + 
                "-> To ask questions in English we use the 5W's and 1H:\n\n" +
                "1) Who: Inquires about the person or people involved in an event or situation.\n\n"+
                "2) What: Seeks information about the object, action, or event.\n\n" + 
                "3) Where: Asks about the location or place where something happened.\n\n"+
                "4) When: Inquires about the time or period when an event occurred.\n\n"+
                "5) Why: Seeks the reason or purpose behind an action or event.\n\n"+
                "6) How: Asks for details regarding the method, manner, or process involved in something.\n\n"+
                "-> Essential verbs are: \"to have\"  \"to be\"  \"to do\" \n\n"+
                "-> 7 subject pronous are: \"I\"  \"You\"  \"He\"  \"She\"  \"It\"  \"We\"  \"They\" \n\n"+
                "-> To express preference in English we use phrases like:\n\n" +
                "1) \"I prefer\" \n\n"+ 
                "2) \"I like\" \n\n"+
                "3) \"I enjoy\" \n\n" 
                );
                alphabetList.setPreferredSize(dim);
                alphabetList.setFont(new Font("Arial", Font.PLAIN, 18));
                alphabetList.setWrapStyleWord(true);
                alphabetList.setLineWrap(true);
                alphabetList.setOpaque(false);
                alphabetList.setEditable(false);
                alphabetList.setFocusable(false);
                lesson.add(alphabetList);
                columnPanel.add(lesson);
            }
            else if(ls==3) {
                Font titleFont = new Font("Arial", Font.BOLD, 18);
                TitledBorder titledBorder1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Different Tenses in English");
                titledBorder1.setTitleJustification(titledBorder1.CENTER);
                titledBorder1.setTitleFont(titleFont);
                lesson.setBorder(titledBorder1);
                JTextArea alphabetList = new JTextArea(
                "-> Simple Present Tense:\n\n" + 
                "\t Used to describe regular actions, general truths, and habitual activities.\n\n" +
                "\t \"She works at the hospital.\" \n\n"+
                "-> Simple Past Tense:\n\n" + 
                "\t Used to describe completed actions in the past.\n\n" +
                "\t \"He visited Paris last summer.\" \n\n"+
                "-> Present Continous Tense:\n\n" + 
                "\t Used to describe actions happening at the moment or during a specific time period.\n\n" +
                "\t \"They are playing soccer right now.\" \n\n"+
                "-> Future Simple Tense:\n\n" +
                "\t Used to express future actions or predictions.\n\n"+
                "\t \"They will arrive tomorrow.\"\n\n" +
                "-> Present Perfect Tense:\n\n" +
                "\t Used to connect the past to the present and emphasize the result or relevance of a past action.   \"I have finished my homework.\""
                );
                alphabetList.setPreferredSize(dim);
                alphabetList.setFont(new Font("Arial", Font.PLAIN, 18));
                alphabetList.setWrapStyleWord(true);
                alphabetList.setLineWrap(true);
                alphabetList.setOpaque(false);
                alphabetList.setEditable(false);
                alphabetList.setFocusable(false);
                lesson.add(alphabetList);
                columnPanel.add(lesson);
            }
        }
        else if(la==2) {
            if(ls==1) {

            }
            else if(ls==2) {

            }
            else if(ls==3) {

            }
        }
        else {
            if(ls==1) {

            }
            else if(ls==2) {

            }
            else if(ls==3) {

            }
        }
        lessonFrame.add(columnPanel, BorderLayout.CENTER);
        lessonFrame.add(topPanel, BorderLayout.NORTH);
        lessonFrame.setVisible(true);
    }
    public static int[] getQuizScoresForUser(String username) {
        int[] quizScores = new int[3];
        try (Scanner scanner = new Scanner(new File("user_profiles.txt"))){
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String u = parts[0];
                if(username.equals(u)) {
                    quizScores[0] = Integer.parseInt(parts[4]);
                    quizScores[1] = Integer.parseInt(parts[5]);
                    quizScores[2] = Integer.parseInt(parts[6]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quizScores;
    }
    public static void openquiz(String username,int lang){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String[][] questions = {
                    {"Q1) How many alphabets are there in English?","Q2) Which of the following is not a vowel?",
                    "Q3) What is used to express actions in a sentence?","Q4) What can be used to ask questions?",
                    "Q5) How would you ask about the time period?","Q6) Which is not a subject pronoun?",
                    "Q7) Which statement is in Simple Past Tense?","Q8) Which statement is in Present Continous Tense?",
                    "Q9) Which statement is in Future Simple Tense?","Q10) Which statement is in Present Perfect Tense?"},
                    {"Question 1: What is the capital of France?","Question 2: Which planet is known as the Red Planet?",
                    "Question 3: What is the largest mammal on Earth?","Question 4: What is the capital of France?",
                    "Question 5: Which planet is known as the Red Planet?","Question 6: What is the largest mammal on Earth?",
                    "Question 7: What is the capital of France?","Question 8: Which planet is known as the Red Planet?",
                    "Question 9: What is the largest mammal on Earth?","Question 10: What is the capital of France"},
                    {"Question 1: What is the capital of France?","Question 2: Which planet is known as the Red Planet?",
                    "Question 3: What is the largest mammal on Earth?","Question 4: What is the capital of France?",
                    "Question 5: Which planet is known as the Red Planet?","Question 6: What is the largest mammal on Earth?",
                    "Question 7: What is the capital of France?","Question 8: Which planet is known as the Red Planet?",
                    "Question 9: What is the largest mammal on Earth?","Question 10: What is the capital of France"}
                };
                String[][][] options = {
                    {{"27", "24", "26"},{"Y", "O", "U"},{"Noun", "Verb", "Adjective"}, {"Wet", "Wild", "What"},
                    {"When", "Where", "Who"},{"They", "Thou", "She"},
                    {"Sam cooked dinner yesterday.", "She has lived in Liverpool all her life.", "We have a lesson next Monday."},
                    {"It's my birthday tomorrow.", "I've seen that film before.", "I am cooking pasta for lunch."},
                    {"I will be writing the letter tomorrow.", "I shall write an essay", "She will be taking her dog for a walk."},
                    {"It has been here the whole time.", "I washed the dishes.", "I played tennis when I was young."}},
                    {{"Paris", "London", "Berlin"},{"Mars", "Venus", "Jupiter"},{"Blue Whale", "Elephant", "Giraffe"},
                    {"Paris", "London", "Berlin"},{"Mars", "Venus", "Jupiter"},{"Blue Whale", "Elephant", "Giraffe"},
                    {"Paris", "London", "Berlin"},{"Mars", "Venus", "Jupiter"},{"Blue Whale", "Elephant", "Giraffe"},
                    {"Paris", "London", "Berlin"}},
                    {{"Paris", "London", "Berlin"},{"Mars", "Venus", "Jupiter"},{"Blue Whale", "Elephant", "Giraffe"},
                    {"Paris", "London", "Berlin"},{"Mars", "Venus", "Jupiter"},{"Blue Whale", "Elephant", "Giraffe"},
                    {"Paris", "London", "Berlin"},{"Mars", "Venus", "Jupiter"},{"Blue Whale", "Elephant", "Giraffe"},
                    {"Paris", "London", "Berlin"}}
                };
                String[][] answers = {
                    {"26","Y", "Verb", "What", "When", "Thou", "Sam cooked dinner yesterday.","I am cooking pasta for lunch.",
                    "I shall write an essay", "It has been here the whole time."},
                    {"Paris", "Mars", "Blue Whale", "Paris", "Mars", "Blue Whale", "Paris", "Mars", "Blue Whale", "Paris"},
                    {"Paris", "Mars", "Blue Whale", "Paris", "Mars", "Blue Whale", "Paris", "Mars", "Blue Whale", "Paris"}
                };

                qframe qf = new qframe(username,lang,questions,options,answers);
                qf.setVisible(true);
            }
        });
    }
    public static void openLeaderboard(String username) {
        JFrame LeadMenu = new JFrame("Leaderboard");
        LeadMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LeadMenu.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setPreferredSize(new Dimension(1500, 100));        

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(welcomeLabel);
        topPanel.setBorder(new EmptyBorder(5, 5, 0, 0)); 

        JLabel Label = new JLabel("Leaderboard");
        Label.setFont(new Font("Arial", Font.CENTER_BASELINE, 24));
        topPanel.add(Label);
        Label.setBorder(new EmptyBorder(5, 400, 0, 250)); 

        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMenuWindow(username);
                LeadMenu.dispose();
            }
        });
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true); 
                LeadMenu.dispose();
            }
        });

        topPanel.add(back);
        topPanel.add(logoutButton);

        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));

        JPanel subPanel1 = new JPanel();
        JPanel subPanel2 = new JPanel();
        JPanel subPanel3 = new JPanel();
        
        Font titleFont = new Font("Arial", Font.BOLD, 18);
        TitledBorder t1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "ENGLISH");
        t1.setTitleJustification(t1.CENTER);
        t1.setTitleFont(titleFont);
        subPanel1.setBorder(t1);
        
        TitledBorder t2 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "FRENCH");
        t2.setTitleJustification(t2.CENTER);
        t2.setTitleFont(titleFont);
        subPanel2.setBorder(t2);
        
        TitledBorder t3 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "SPANISH");
        t3.setTitleJustification(t3.CENTER);
        t3.setTitleFont(titleFont);
        subPanel3.setBorder(t3);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        int eno = countUser(1);
        int fno = countUser(2);
        int sno = countUser(3);
        int e[] = returnscore(eno,1);
        int f[] = returnscore(fno,2);
        int s[] = returnscore(sno,3);
        String e1[] = returnuser(eno,1);
        String f1[] = returnuser(fno,2);
        String s1[] = returnuser(sno,3);

        for(int i = 0;i<eno-1;i++) {
            for(int j = i+1;j<eno;j++){
                if(e[i]<e[j]) {
                    int te = e[i];
                    String t = e1[i];
                    e[i] = e[j];
                    e1[i] = e1[j];
                    e[j] = te;
                    e1[j] = t;
                }
            }
        }
        for(int i = 0;i<fno-1;i++) {
            for(int j = i+1;j<fno;j++){
                if(f[i]<f[j]) {
                    int te = f[i];
                    String t = f1[i];
                    f[i] = f[j];
                    f1[i] = f1[j];
                    f[j] = te;
                    f1[j] = t;
                }
            }
        }
        for(int i = 0;i<sno-1;i++) {
            for(int j = i+1;j<sno;j++){
                if(s[i]<s[j]) {
                    int te = s[i];
                    String t = s1[i];
                    s[i] = s[j];
                    s1[i] = s1[j];
                    s[j] = te;
                    s1[j] = t;
                }
            }
        }
        
        subPanel1.setLayout(new GridBagLayout());
        subPanel1.add(createLeaderboard(e,e1,eno),gbc);
        subPanel2.setLayout(new GridBagLayout());
        subPanel2.add(createLeaderboard(f,f1,fno),gbc);
        subPanel3.setLayout(new GridBagLayout());
        subPanel3.add(createLeaderboard(s,s1,sno),gbc);

        columnPanel.add(subPanel1);
        columnPanel.add(subPanel2);
        columnPanel.add(subPanel3);

        LeadMenu.add(topPanel, BorderLayout.NORTH);
        LeadMenu.add(columnPanel, BorderLayout.CENTER);
        LeadMenu.setVisible(true);
    }
    public static JPanel createLeaderboard(int[] a, String[] b,int no) {
        JPanel lpanel = new JPanel();
        lpanel.setLayout(new BoxLayout(lpanel, BoxLayout.X_AXIS));
        JPanel u = new JPanel();
        u.setLayout(new BoxLayout(u, BoxLayout.Y_AXIS));
        JPanel y = new JPanel();
        y.setLayout(new BoxLayout(y, BoxLayout.Y_AXIS));
        for(int i = 0;i<no && i<10;i++) {
            JLabel l = new JLabel(Integer.toString(i+1) + ")" + "  " + b[i]);
            l.setFont(new Font("Arial",Font.BOLD,20));
            JLabel m = new JLabel("    " + Integer.toString(a[i]));
            m.setFont(new Font("Arial",Font.BOLD,20));
            y.add(m);
            u.add(l);
        }
        lpanel.add(u);
        lpanel.add(y);
        return lpanel;
    } 
    public static int countUser(int lang){
        int c = 0;
        try (Scanner s = new Scanner(new File("user_profiles.txt"))){
            while(s.hasNextLine()) {
                String line = s.nextLine();
                String parts[] = line.split(",");
                if(Integer.parseInt(parts[lang+3])!=11){
                    c++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }
    public static int[] returnscore(int c,int lang) {
        int[] arr = new int[c];
        int x = 0;
        try (Scanner s = new Scanner(new File("user_profiles.txt"))){
            while(s.hasNextLine()) {
                String line = s.nextLine();
                String[] parts = line.split(",");
                if(Integer.parseInt(parts[lang+3])!=11) {
                    arr[x] = Integer.parseInt(parts[lang+3]);
                    x++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return arr;
    }
    public static String[] returnuser(int c,int lang) {
        String[] arr = new String[c];
        int x = 0;
        try (Scanner s = new Scanner(new File("user_profiles.txt"))){
            while(s.hasNextLine()) {
                String line = s.nextLine();
                String[] parts = line.split(",");
                if(Integer.parseInt(parts[lang+3])!=11) {
                    arr[x] = parts[0];
                    x++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return arr;
    }
}   
