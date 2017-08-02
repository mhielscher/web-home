//
//  ChatroomClient.java
//  ChatroomClient
//
//  Created by Matthew Hielscher on Wed Apr 14 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
//

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.net.*;

public class ChatroomClient extends JFrame {
	
	Socket connection;
	OutputConnectionHandler out;
	InputConnectionHandler in;
	
	// GUI Components
	JScrollPane scrollingChat;
	JTextArea chatBox;
	JTextField inputField;
	JButton sendButton;
	JLabel usernameLabel;
	JTextField usernameField;
	JLabel passwordLabel;
	JPasswordField passwordField;
	JButton connectButton;
	JLabel channelLabel;
	JTextField channelField;
	JLabel serverLabel;
	JTextField serverField;
	JLabel portLabel;
	JTextField portField;
	JCheckBox timestampCheckbox;
	JCheckBox beepCheckbox;
	
	public ChatroomClient() {
		super("");
		
		setSize(700, 600);
		setResizable(false);
		setVisible(true);
		
		initComponents();
		/*
		try { 
			connection = new Socket("dirt.hn.org", 7584);
		} catch (Exception e) {
			System.err.println("ERROR: Could not create connection to the server.");
		}
		
		out = new OutputConnectionHandler(connection);
		in = new InputConnectionHandler(connection, chatBox);
		in.start();
		out.login("WasabiFlux", "matt", "dirt");*/
	}
	
	public void initComponents() {
		Container c = getContentPane();
		c.setLayout(null);
		
		chatBox = new JTextArea(25, 46);
		chatBox.setEditable(false);
		chatBox.setLineWrap(true);
		chatBox.setWrapStyleWord(true);
		scrollingChat = new JScrollPane(chatBox);
		scrollingChat.setBounds(10, 10, 540, 520);
		c.add(scrollingChat);
		
		inputField = new JTextField("");
		inputField.setBounds(10, 540, 540, 25);
		inputField.addActionListener(new SendListener());
		c.add(inputField);
		
		sendButton = new JButton("Send");
		sendButton.setBounds(560, 535, 85, 30);
		sendButton.addActionListener(new SendListener());
		c.add(sendButton);
		
		usernameLabel = new JLabel("Username:");
		usernameLabel.setBounds(560, 10, 130, 25);
		c.add(usernameLabel);
		
		usernameField = new JTextField("");
		usernameField.setBounds(560, 40, 130, 20);
		c.add(usernameField);
		
		passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(560, 80, 130, 25);
		c.add(passwordLabel);
		
		passwordField = new JPasswordField("");
		passwordField.setBounds(560, 110, 130, 20);
		c.add(passwordField);
		
		connectButton = new JButton("Connect");
		connectButton.setBounds(575, 150, 100, 30);
		connectButton.addActionListener(new ConnectButtonListener(this));
		c.add(connectButton);
		
		channelLabel = new JLabel("Default Channel:");
		channelLabel.setBounds(560, 190, 130, 25);
		c.add(channelLabel);
		
		channelField = new JTextField("VNHS");
		channelField.setBounds(560, 220, 130, 20);
		c.add(channelField);
		
		serverLabel = new JLabel("Server:");
		serverLabel.setBounds(560, 260, 130, 25);
		c.add(serverLabel);
		
		serverField = new JTextField("sektao.sheheitthey.com");
		serverField.setBounds(560, 290, 130, 20);
		c.add(serverField);
		
		portLabel = new JLabel("Port:");
		portLabel.setBounds(560, 330, 130, 25);
		c.add(portLabel);
		
		portField = new JTextField("7584");
		portField.setBounds(560, 360, 130, 20);
		c.add(portField);
		
		timestampCheckbox = new JCheckBox("Timestamps", true);
		timestampCheckbox.setBounds(555, 400, 120, 25);
		timestampCheckbox.addActionListener(new TimestampCheckboxListener());
		c.add(timestampCheckbox);
		timestampCheckbox.disable();
		
		beepCheckbox = new JCheckBox("Beep on Message", false);
		beepCheckbox.setBounds(555, 440, 160, 25);
		beepCheckbox.addActionListener(new BeepCheckboxListener());
		c.add(beepCheckbox);
		beepCheckbox.disable();
		
		repaint();
	}
	
	public void disconnect() {
		System.out.println("Disconnecting...");
		try {
			out = null;
			connection.close();
			connection = null;
			System.gc();
		} catch (Exception e) {
			System.err.println("ERROR: Could not close connection to the server.");
			System.exit(1);
		}
		
		
		usernameField.enable();
		passwordField.enable();
		channelField.enable();
		serverField.enable();
		portField.enable();
		timestampCheckbox.disable();
		beepCheckbox.disable();
		
		connectButton.setText("Connect");
		connectButton.removeActionListener(connectButton.getActionListeners()[0]);
		connectButton.addActionListener(new ConnectButtonListener(this));
		
		chatBox.append("\nYou have been disconnected.\n\n");
		
		in.stop();
		in = null;
	}
	
	private class ConnectButtonListener implements ActionListener {
		private ChatroomClient parent;
		
		public ConnectButtonListener(ChatroomClient p) {
			parent = p;
		}
		
		public void actionPerformed(ActionEvent ev) {
			try { 
				connection = new Socket(serverField.getText(), Integer.parseInt(portField.getText()));
			} catch (Exception e) {
				System.err.println("ERROR: Could not create connection to the server.");
			}
			out = new OutputConnectionHandler(connection);
			in = new InputConnectionHandler(parent, connection, chatBox, getToolkit(), usernameField.getText());
			in.start();
			out.login(usernameField.getText(), new String(passwordField.getPassword()), channelField.getText());
			
			usernameField.disable();
			passwordField.disable();
			channelField.disable();
			serverField.disable();
			portField.disable();
			timestampCheckbox.enable();
			beepCheckbox.enable();
			
			connectButton.setText("Disconnect");
			connectButton.removeActionListener(connectButton.getActionListeners()[0]);
			connectButton.addActionListener(new DisconnectButtonListener());
		}
	}
	
	private class DisconnectButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			disconnect();
		}
	}
	
	private class TimestampCheckboxListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			in.toggleTimestamps();
		}
	}
	
	private class BeepCheckboxListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			in.toggleBeep();
		}
	}
	
	private class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String text = inputField.getText();
			if (text.length()==0)
				return;
			
			if (text.charAt(0) == '/')
				out.send(text);
			else
				out.say(text);
			inputField.setText("");
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public static void main(String args[]) {
		new ChatroomClient();
	}
}