package sBot;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.Main;
import gui.Player;

public class NI extends Player {

	private JFrame frame;
	private JPanel panel;
	private JButton fold, call, bet;
	private JSlider betAmount;
	private JLabel status, betAmountLabel;
	
	private boolean pressedFold = false, pressedCall = false, pressedBet = false;
	private static int handId;
	
	private double[][] winningPercentages = {
			{0.852, 0.610, 0.572, 0.537, 0.501, 0.457, 0.427, 0.401, 0.385, 0.389, 0.380, 0.371, 0.363},
			{0.590, 0.726, 0.435, 0.424, 0.413, 0.390, 0.375, 0.370, 0.372, 0.366, 0.359, 0.351, 0.344},
			{0.551, 0.405, 0.690, 0.414, 0.410, 0.391, 0.376, 0.359, 0.364, 0.357, 0.350, 0.343, 0.335},
			{0.512, 0.392, 0.386, 0.657, 0.414, 0.395, 0.380, 0.363, 0.355, 0.351, 0.344, 0.337, 0.329},
			{0.474, 0.380, 0.378, 0.382, 0.624, 0.400, 0.382, 0.369, 0.360, 0.344, 0.339, 0.332, 0.324},
			{0.427, 0.356, 0.358, 0.362, 0.368, 0.587, 0.388, 0.373, 0.364, 0.348, 0.330, 0.326, 0.318},
			{0.395, 0.340, 0.341, 0.346, 0.352, 0.355, 0.558, 0.377, 0.369, 0.353, 0.335, 0.317, 0.312},
			{0.367, 0.336, 0.324, 0.329, 0.335, 0.339, 0.344, 0.527, 0.372, 0.356, 0.338, 0.321, 0.303},
			{0.350, 0.337, 0.328, 0.319, 0.325, 0.330, 0.335, 0.339, 0.503, 0.367, 0.349, 0.332, 0.314},
			{0.354, 0.330, 0.322, 0.316, 0.308, 0.312, 0.318, 0.322, 0.333, 0.481, 0.356, 0.339, 0.321},
			{0.344, 0.323, 0.314, 0.308, 0.303, 0.294, 0.299, 0.303, 0.314, 0.322, 0.455, 0.331, 0.313},
			{0.335, 0.315, 0.306, 0.300, 0.295, 0.289, 0.280, 0.284, 0.296, 0.303, 0.295, 0.430, 0.304},
			{0.326, 0.307, 0.298, 0.292, 0.287, 0.281, 0.275, 0.265, 0.277, 0.284, 0.276, 0.267, 0.405},
	};
	
	public NI() {
		frame = new JFrame();
		frame.setLocationByPlatform(true);
		frame.setSize(360, 230);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false); // TODO
		
		panel = new JPanel(null);
		frame.add(panel);
		
		status = new JLabel();
		status.setText("Warte auf Gegner ...");
		status.setBounds(10, 30, 340, 90);
		status.setFont(new Font("Consolas", 0, 12));
		panel.add(status);
		
		betAmount = new JSlider();
		betAmount.setBounds(120, 130, 100, 20);
		betAmount.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				betAmountLabel.setText(betAmount.getValue() + "$");
			}
		});
		panel.add(betAmount);
		
		betAmountLabel = new JLabel();
		betAmountLabel.setBounds(270, 130, 60, 20);
		panel.add(betAmountLabel);
		
		fold = new JButton("fold/check");
		fold.setBounds(10, 160, 100, 20);
		fold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedFold = true;
			}
		});
		panel.add(fold);
		
		call = new JButton("call/check");
		call.setBounds(120, 160, 100, 20);
		call.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedCall = true;
			}
		});
		panel.add(call);
		
		bet = new JButton("bet");
		bet.setBounds(230, 160, 100, 20);
		bet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedBet = true;
			}
		});
		panel.add(bet);
		
		setButtonsEnabled(false);
		
		frame.repaint();
	}
	
	public int getAction(int[] features) {

		ai.DataPoint dp = new ai.DataPoint(features);
		
		pressedFold = false;
		pressedBet = false;
		pressedCall = false;
		updateStatus(true, features);
		betAmount.setMinimum(dp.getInput(false) + ai.DataPoint.getBB());
		betAmount.setValue(dp.getInput(false) + ai.DataPoint.getBB());
		betAmount.setMaximum(dp.getChips(true));
		setButtonsEnabled(true);
		
		//TODO false -> ai.AI.READY
		while(false && !pressedFold && !pressedCall && !pressedBet) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		setButtonsEnabled(false);
		updateStatus(false, features);
		
		int[] hand = {dp.getHand(true), dp.getHand(false)};
		int c1 = Math.max(hand[0], hand[1]) / 4;
		int c2 = Math.min(hand[0], hand[1]) / 4;
		if(hand[0]%4 == hand[1]%4)
			handId = -1; //TODO (12-c1) * 13 + (12-c2);
		else
			handId = (12-c2) * 13 + (12-c1);
		
		int[] community = {dp.getCommCard(0), dp.getCommCard(1), dp.getCommCard(2), dp.getCommCard(3), dp.getCommCard(4)};
		double score = gui.Main.calcScore(hand, community);
		if(community[0] == -1 && score > 0.7 || score > 2)
			return betAmount.getValue();
		else
			return dp.getInput(false);
	}
	
	private void updateStatus(boolean waitingForAction, int[] features) {
		
		ai.DataPoint dp = new ai.DataPoint(features);
		
		int[] hand = {dp.getHand(true), dp.getHand(false)};
		int[] community = {dp.getCommCard(0), dp.getCommCard(1), dp.getCommCard(2), dp.getCommCard(3), dp.getCommCard(4)};
		
		String statusString = "<html>"
				+ (!waitingForAction ? "Warte auf Gegner ...<br/>" : "Wähle eine Aktion!<br/>")
				+ "BB: " + ai.DataPoint.getBB() + "$<br/>"
				+ "IN: " + (int)dp.getInput(false) + "$/" + dp.getChips(false) + "$ [" + dp.getInput(true) + "$/" + dp.getChips(true) + "$]<br/>"
				+ "HD: " + Main.cardsToStr(hand) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + Main.cardsToStr(community).replace(" ", "&nbsp;") + "<br/>"
				+ "</html>";
		status.setText(statusString);
	}
	
	private void setButtonsEnabled(boolean enabled) {
		fold.setEnabled(enabled);
		call.setEnabled(enabled);
		bet.setEnabled(enabled);
	}

	public void addRoundEndState(int[] features) {}

	public String getName() {
		return "NI";
	}

	public static int getHandId() {
		return handId;
	}
}