import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SimpleChatClient {
    private JTextArea incoming;
    private JTextField outgoing;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    public static void main(String[] args) {
        SimpleChatClient chatClient = new SimpleChatClient();
        chatClient.buildGUI();
    }

    public void buildGUI() {
        // Creare și configurare fereastră
        JFrame frame = new JFrame("Mini-Chat");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Creare componente GUI
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Trimite");
        JButton connectButton = new JButton("Conectare");
        JButton disconnectButton = new JButton("Deconectare");

        // Adăugare listeneri de evenimente la butoane
        sendButton.addActionListener(new SendButtonListener());
        connectButton.addActionListener(new ConnectButtonListener());
        disconnectButton.addActionListener(new DisconnectButtonListener());

        // Creare panou pentru butoane
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(sendButton);

        // Adăugare componente la panoul principal
        mainPanel.add(qScroller, BorderLayout.CENTER);
        mainPanel.add(outgoing, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Adăugare panoul principal la fereastră
        frame.getContentPane().add(mainPanel);
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Metoda pentru configurarea conexiunii de rețea
    private void setUpNetworking() {
        try {
            String serverAddress = JOptionPane.showInputDialog("Introdu adresa serverului:");
            int serverPort = Integer.parseInt(JOptionPane.showInputDialog("Introdu portul serverului:"));
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();
            JOptionPane.showMessageDialog(null, "Conectat la server.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare de conectare la server.");
        }
    }

    // Metoda pentru trimiterea mesajelor către server
    private void sendMessage() {
        String message = outgoing.getText();
        out.println(message);
        outgoing.setText("");
    }

    // Metoda pentru deconectarea de la server
    private void disconnect() {
        try {
            socket.close();
            JOptionPane.showMessageDialog(null, "Deconectat de la server.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare de deconectare a serverului.");
        }
    }

    // Listener pentru butonul "Trimite"
    private class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }

    // Listener pentru butonul "Conectare"
    private class ConnectButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setUpNetworking();
        }
    }

    // Listener pentru butonul "Deconectare"
    private class DisconnectButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            disconnect();
        }
    }

    // Fir de execuție pentru citirea mesajelor primite de la server
    private class IncomingReader implements Runnable {
        public void run() {
            while (in.hasNextLine()) {
                String message = in.nextLine();
                incoming.append(message + "\n");
            }
        }
    }
}
