package api2;

import java.awt.EventQueue;

public class mainGUI {

	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		startJframeHTTP();
	}
	
	private static void startJframeHTTP(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JframeVaribalseLocGlob frame = new JframeVaribalseLocGlob();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
