import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main {
	public static void main(String[] args){
		JFrame frame = new JFrame("ExtracteMpstatData");
		JPanel north = new JPanel(new BorderLayout());
		frame.add(north,BorderLayout.NORTH);
		
		final JTextField tfCPU =  new JTextField("all",10);
		final JTextField tfRow = new JTextField(10);
		final JTextField tfPath =  new JTextField("");
		final JTextArea taOutputData = new JTextArea(20, 40);
		JButton btSubmit = null;
		
		{
			//레이아웃 상단 중앙
			JPanel northCenter = new JPanel(new GridLayout(0, 1));
			north.add(northCenter, BorderLayout.CENTER);
			JLabel lbCPU = new JLabel("CPU Name : ");
			
			
			{
				JPanel pnColumnSetting = new JPanel(new BorderLayout());
				pnColumnSetting.add(lbCPU,BorderLayout.WEST);
				pnColumnSetting.add(tfCPU,BorderLayout.CENTER);
				northCenter.add(pnColumnSetting);
			}
			
			JLabel lbRow = new JLabel("Row Name : ");
			northCenter.add(lbRow);
			northCenter.add(tfRow);
			
			{
				JPanel pnRowSetting = new JPanel(new BorderLayout());
				pnRowSetting.add(lbRow,BorderLayout.WEST);
				pnRowSetting.add(tfRow,BorderLayout.CENTER);
				northCenter.add(pnRowSetting);
			}
			
			JLabel lbPathName = new JLabel("Path : ");
			JButton btPath = new JButton("Open Dir");
			tfPath.setEditable(false);
			btPath.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JFileChooser fcDialogDirectorySelect = new JFileChooser(tfPath.getText()); 
					fcDialogDirectorySelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					int returnVal = fcDialogDirectorySelect.showSaveDialog(frame);
					if(returnVal == JFileChooser.APPROVE_OPTION){
						tfPath.setText( fcDialogDirectorySelect.getSelectedFile().getPath());
					}
				}
			});
			{
				JPanel northCenterBottom = new JPanel(new BorderLayout());
				northCenterBottom.add(lbPathName,BorderLayout.WEST);
				northCenterBottom.add(tfPath,BorderLayout.CENTER);
				northCenterBottom.add(btPath,BorderLayout.EAST);
				
				northCenter.add(northCenterBottom);
			}
			
			north.add(northCenter,BorderLayout.CENTER);
		}
		
		{
			//레이아웃 상단 오른쪽 
			btSubmit = new JButton("Process");
			north.add(btSubmit,BorderLayout.EAST);
			
			btSubmit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JComponent component = (JComponent) e.getSource();
					component.setEnabled(false);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String path = tfPath.getText();
							boolean isError = false;
							String message = null;
							String title = null;
							if(path.equals("")){
								message = "Select directory";
								title = "Error";
								isError=true;
							}else{
								File f = new File(path);
								if(!f.isDirectory()){
									message = "The selected object isn't directory";
									title = "Error";
									isError=true;
								}else if(!f.exists()){
									message = "The selected directory isn't Exist";
									title = "Error";
									isError=true;
								}
								File[] files = f.listFiles();
								StringBuilder sb = new StringBuilder();
								for(File file : files){
									FileParser parser = new FileParser(file);
									String extractWord = parser.getValue(tfCPU.getText(), tfRow.getText());
									if(extractWord==null){
										extractWord="";
									}
									sb.append(extractWord).append("\r\n");
								}
								taOutputData.setText(sb.toString());
							}
							
							
							if(isError){
								JOptionPane.showMessageDialog(frame, message,title,JOptionPane.ERROR_MESSAGE);
								component.setEnabled(true);
							}else{
								
								
								
								component.setEnabled(true);
							}
						}
					}).start();
				}
			});
		}
		
		{
			JScrollPane scrollOutputData = new JScrollPane(taOutputData);
			scrollOutputData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			frame.add(scrollOutputData,BorderLayout.CENTER);
		}
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
