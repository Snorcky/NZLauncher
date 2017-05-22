package fr.snorcky.nostalgiaz.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.launcher.util.UsernameSaver;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;


@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener{
	
	private Image background = Swinger.getResource("background.png");
	
	private UsernameSaver saver = new UsernameSaver(Launcher.NZ_INFOS);
	
	private JTextField usernameField = new JTextField(saver.getUsername(""));
	private JTextField passwordField = new JPasswordField(saver.getUsername(""));
	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quit.png"));
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hide.png"));
	
	private SColoredBar progressBar = new SColoredBar(new Color(255, 255, 255, 15));
	private JLabel infoLabel = new JLabel("Cliquez sur Jouer !", SwingConstants.CENTER);
	public LauncherPanel(){
		this.setLayout(null);
		
		usernameField.setForeground(Color.WHITE);
		usernameField.setFont(usernameField.getFont().deriveFont(20F));
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setBounds(564, 245, 266, 39);
		this.add(usernameField);
		
		passwordField.setForeground(Color.WHITE);
		passwordField.setFont(usernameField.getFont());
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(564, 375, 266, 39);
		this.add(passwordField);
		
		playButton.setBounds(562, 464);
		playButton.addEventListener(this);
		this.add(playButton);
		
		quitButton.setBounds(920, 18);
		quitButton.addEventListener(this);
		this.add(quitButton);
		
		hideButton.setBounds(877, 18);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		
		progressBar.setBounds(12, 593, 951, 20);
		this.add(progressBar);
		
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setFont(usernameField.getFont());
		infoLabel.setBounds(12, 560, 951, 25);
		this.add(infoLabel);
	}
	
	@Override
	public void onEvent(SwingerEvent e){
		if(e.getSource() == playButton){
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length()== 0) {
				JOptionPane.showMessageDialog(this, "Erreur: Veuillez entrer un pseudo et un mot de passe valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
			Thread t = new Thread(){
				@Override
				public void run(){
					try {
					Launcher.auth(usernameField.getText(), passwordField.getText());
					} catch (AuthenticationException e){
						JOptionPane.showMessageDialog(LauncherPanel.this, "Impossible de se connecter : " + e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						return;
					}
					
					try {
						Launcher.update();
						} catch (Exception e){
							Launcher.interruptThread();
							JOptionPane.showMessageDialog(LauncherPanel.this, "Impossible de mettre à jour le jeu: " + e, "Erreur", JOptionPane.ERROR_MESSAGE);
							setFieldsEnabled(true);
							return;
						}
					try {
						Launcher.launch();
						} catch (IOException e){
							JOptionPane.showMessageDialog(LauncherPanel.this, "Impossible de lancer le jeu: " + e, "Erreur", JOptionPane.ERROR_MESSAGE);
							setFieldsEnabled(true);
						}
					
					
				}
			};
			
			t.start();
			
		} else if(e.getSource() == quitButton)
			System.exit(0);
		else if(e.getSource() == hideButton)
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
		
	}
	
	private void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar(){
		return progressBar; 
	}
	
	public void setInfoText(String text){
		infoLabel.setText(text);
	}

}
