//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class AppFrame extends JFrame {
//    private JTextArea textArea;
//    private JLabel label;
//    private JButton button;
//    private final int WIDTH = 500, HEIGHT = 500;
//    private int count;
//    public AppFrame() {
//        super("Word Counter App");
//
//        label = new JLabel("Word Counter App", JLabel.CENTER);
//        label.setPreferredSize(new Dimension(WIDTH,30));
//
//        textArea = new JTextArea();
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);
//        textArea.setPreferredSize(new Dimension(WIDTH,HEIGHT-60));
//        button = new JButton("Count");
//        button.setPreferredSize(new Dimension(60,30));
//        button.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				count = textArea.getText().length();
//				JOptionPane.showMessageDialog(rootPane, "Words: "+count, "Word Conted", JOptionPane.PLAIN_MESSAGE);
//			}
//		});
//        JScrollPane scrollPane = new JScrollPane(textArea);
//
//        
//        setLayout(new BorderLayout());
//        add(label, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//        add(button,BorderLayout.PAGE_END);
//
//        
//        setSize(WIDTH, HEIGHT);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setVisible(true); 
//    }
//
//    
//}
