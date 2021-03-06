import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener
{
	//declare final variables
	private final String DEFAULT_TIME_LIMIT = "5", DEFAULT_NUM_ANSWERS = "0";

	//declare instance variables
	private JButton executeButton, saveCodeAsButton, clearCodeAreaButton,
	saveOutputButton, openExistingFileButton, saveCodeButton, saveOutputAsButton,
	clearOutputAreaButton;
	private JTextField numSolutionsField, timeLimitField;
	private JTextArea notificationArea;
	private ArrayList<AnswerSet> answerSets;


	public ControlPanel()
	{
		initComponents();
	}	//end ControlPanel constructor

	private void initComponents()
	{	
		//set up buttons
		clearOutputAreaButton = new JButton("Clear Output");
		clearOutputAreaButton.addActionListener(this);

		saveCodeButton = new JButton("Save Code");
		saveCodeButton.addActionListener(this);

		openExistingFileButton = new JButton("Open Existing File");
		openExistingFileButton.addActionListener(this);

		saveOutputButton = new JButton("Save Output");
		saveOutputButton.addActionListener(this);

		clearCodeAreaButton = new JButton("Clear Code");
		clearCodeAreaButton.addActionListener(this);

		saveOutputAsButton = new JButton("Save Output As...");
		saveOutputAsButton.addActionListener(this);

		saveCodeAsButton = new JButton("Save Code As...");
		saveCodeAsButton.addActionListener(this);

		executeButton = new JButton("Execute");
		executeButton.addActionListener(this);

		//set up text areas
		notificationArea = new JTextArea();
		notificationArea.setLineWrap(true);
		notificationArea.setBackground(this.getBackground());
		notificationArea.setForeground(Color.red);
		notificationArea.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		//set up text fields
		timeLimitField = new JTextField();
		timeLimitField.setText(DEFAULT_TIME_LIMIT);
		timeLimitField.setColumns(5);

		numSolutionsField = new JTextField();
		numSolutionsField.setText(DEFAULT_NUM_ANSWERS);
		numSolutionsField.setColumns(5);

		//set up panels
		JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		westPanel.add(new JLabel("Number of Solutions:"));
		westPanel.add(numSolutionsField);
		westPanel.add(new JLabel("Time Limit (seconds): "));
		westPanel.add(timeLimitField);
		westPanel.add(executeButton);
		westPanel.add(clearCodeAreaButton);
		westPanel.add(clearOutputAreaButton);

		JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		eastPanel.setAlignmentX(LEFT_ALIGNMENT);
		eastPanel.add(openExistingFileButton);
		eastPanel.add(saveCodeButton);
		eastPanel.add(saveCodeAsButton);
		eastPanel.add(saveOutputButton);
		eastPanel.add(saveOutputAsButton);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2));
		mainPanel.add(westPanel);
		mainPanel.add(eastPanel);

		//set up ControlPanel
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.add(new JLabel("Controls:"), BorderLayout.NORTH);
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(notificationArea, BorderLayout.SOUTH);
	}

	private void openExistingFile()
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);

		if(fc.showOpenDialog(MainFrame.currFrame) == JFileChooser.APPROVE_OPTION)
		{
			MainFrame.currCodeFile = fc.getSelectedFile();

			try 
			{
				String textAreaString = "";
				String line = null;
				BufferedReader br = new BufferedReader(new FileReader(MainFrame.currCodeFile));
				while((line = br.readLine()) != null)
				{
					textAreaString += line + "\n";
				}

				br.close();
				MainFrame.codePanel.codeArea.setText(textAreaString);
				notificationArea.setText("Opened:\n" + MainFrame.currCodeFile.getPath() + "\nsuccessfully.");

			} catch (IOException e) {
				e.printStackTrace();
				notificationArea.setText("File at\n" + MainFrame.currCodeFile.getPath() + "\nnot found.");
			}
		}
	}	//end openExistingFile

	private void saveOutput()
	{
		FileWriter fw;
		try 
		{
			if(MainFrame.currOutputFile == null)
			{
				MainFrame.currOutputFile = new File(System.getProperty("user.home") + File.separator
						+ "Desktop" + File.separator + "temp_output_filename.txt");
			}

			if(MainFrame.currOutputFile.getName().contains(".txt"))
			{
				fw = new FileWriter(MainFrame.currOutputFile);

				fw.write(MainFrame.oPanel.outputArea.getText());
				fw.close();

				notificationArea.setText("Saved code to:\n" + MainFrame.currOutputFile.getPath() + "");
			}
			else
				notificationArea.setText("Did not save to:\n" + MainFrame.currOutputFile.getPath() 
						+ "\nOnly \".txt\" files can be saved.");

		} catch (IOException e1) {
			e1.printStackTrace();
			notificationArea.setText("Unable to save code to\n" + MainFrame.currOutputFile.getPath() + "");
		}
	}	//end saveOutput

	private void saveOutputAs()
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);

		if(MainFrame.currOutputFile == null)
			fc.setSelectedFile(new File("temp_output_filename.txt"));
		else
			fc.setSelectedFile(MainFrame.currOutputFile);

		if(fc.showSaveDialog(MainFrame.currFrame) == JFileChooser.APPROVE_OPTION)
		{
			MainFrame.currOutputFile = fc.getSelectedFile();	
			saveCode();
		}
	}	//end saveOutputAs

	private void saveCode()
	{
		FileWriter fw;
		try 
		{
			if(MainFrame.currCodeFile == null)
			{
				MainFrame.currCodeFile = new File(System.getProperty("user.home") + File.separator
						+ "Desktop" + File.separator + "temp_code_filename.txt");
			}

			if(MainFrame.currCodeFile.getName().contains(".txt"))
			{
				fw = new FileWriter(MainFrame.currCodeFile);

				fw.write(MainFrame.codePanel.codeArea.getText());
				fw.close();

				notificationArea.setText("Saved code to:\n" + MainFrame.currCodeFile.getPath() + "");
			}
			else
				notificationArea.setText("Did not save code to:\n" + MainFrame.currCodeFile.getPath() 
						+ "\nOnly \".txt\" files can be saved.");

		} catch (IOException e1) {
			e1.printStackTrace();
			notificationArea.setText("Unable to save code to\n" + MainFrame.currCodeFile.getPath() + "");
		}
	}	//end saveFile

	private void saveCodeAs()
	{
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);

		if(MainFrame.currCodeFile == null)
			fc.setSelectedFile(new File("temp_code_filename.txt"));
		else
			fc.setSelectedFile(MainFrame.currCodeFile);

		if(fc.showSaveDialog(MainFrame.currFrame) == JFileChooser.APPROVE_OPTION)
		{
			MainFrame.currCodeFile = fc.getSelectedFile();	
			saveCode();
		}
	}	//end saveFileAs

	private void executeCode()
	{			
		boolean errCaught = false, satisfiable = false;

		MainFrame.oPanel.answerBox.removeAllItems();
		AnswerSet.resetHiddenKeys();
		findHiddenKeys();

		long startTime = System.currentTimeMillis();

		String errMsg = "";
		int numAnswers, timeLimit;

		try
		{
			numAnswers = Integer.parseInt(numSolutionsField.getText());
		}
		catch(NumberFormatException e)
		{
			errMsg = "\n\nPlease enter an integer for number of solutions.\n"
					+ "Defaulting to all solutions.";
			numAnswers = 0;
			errCaught = true;
		}

		try
		{
			timeLimit = Integer.parseInt(timeLimitField.getText());
		}
		catch(NumberFormatException e)
		{
			if(errCaught)
				errMsg = "\n\nPlease enter an integer for number of solutions and time limit.\n"
						+ "Defaulting to all solution and 5 second time limit.";
			else
				errMsg = "\n\nPlease enter an integer for time limit.\n"
						+ "Defaulting to 5 seconds.";
			timeLimit = 5;
		}

		File tempFile = null;
		BufferedReader br = null;
		ProcessBuilder pb = null;
		Process proc = null;
		BufferedWriter writer = null;
		AnswerSet currSet = null;
		answerSets = new ArrayList<AnswerSet>();

		try 
		{
			tempFile = File.createTempFile("temp", ".txt");
			writer = new BufferedWriter(new FileWriter(tempFile));
			writer.write(MainFrame.codePanel.codeArea.getText());
			writer.close();

			String[] command = {"clingo", tempFile.getPath(), "-n " + Integer.toString(numAnswers),
					"--time-limit=" + timeLimit};

			pb = new ProcessBuilder(command);
			proc = pb.start();
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String line = null;
			String[] answers;
			boolean answerSetFound = false;

			while((line = br.readLine()) != null)
			{
				if(!satisfiable && line.toLowerCase().equals("satisfiable"))
					satisfiable = true;

				String lowerLine = line.toLowerCase();

				if(answerSetFound)
				{
					answers = line.split(" ");
					Collections.sort(Arrays.asList(answers));
					String answerKey = null;

					for(int i = 0; i < answers.length; i++)
					{
						String answer = answers[i];

						if(answer.contains("("))
						{
							if(answerKey == null)
							{
								answerKey = answer.split("\\(")[0];
								currSet.addKey(answerKey);
							}
							else
							{
								if(!answer.split("\\(")[0].equals(answerKey))
								{
									answerKey = answer.split("\\(")[0];
									currSet.addKey(answerKey);
								}
							}
						}

						if(answerKey != null)
							currSet.addToList(answerKey, answer);
						else
							currSet.addToList(AnswerSet.NO_CATEGORY, answer);
					}

					answerSetFound = false;
				}
				else if(!lowerLine.contains("version") && !lowerLine.contains("solving") && !lowerLine.contains("reading"))
				{
					if(lowerLine.contains("answer"))
					{
						currSet = new AnswerSet(line);
						answerSets.add(currSet);
						answerSetFound = true;	
					}

				}
			}

			long elapsedTime = System.currentTimeMillis() - startTime;

			if(satisfiable)
			{
				if(answerSets.size() > 1)
				{
					for(AnswerSet set : answerSets)
						MainFrame.oPanel.answerBox.addItem(set.getName());
				}
				
				MainFrame.oPanel.answerBox.addItem("All");
				MainFrame.oPanel.answerBox.setSelectedItem("All");
			}
			else
				MainFrame.oPanel.outputArea.setText("Your program is not satisfiable.\nCheck your syntax");

			notificationArea.setText("Completed executing code in " + convertTime(elapsedTime)
					+ "\n" + answerSets.size() + " Answers found" + errMsg);
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		finally
		{
			try 
			{
				Files.delete(tempFile.toPath());
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}	//end executeCode

	private String convertTime(long time)
	{
		String retVal = null;

		retVal = String.format("%ds.", 
				TimeUnit.MILLISECONDS.toSeconds(time) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
				);

		if(retVal.equals("0s."))
			retVal = time + "ms.";

		return retVal;
	}	//end convertTime

	private void findHiddenKeys()
	{		
		String[] text = MainFrame.codePanel.codeArea.getText().split("\n");

		for(String line : text)
		{	
			if(line.toLowerCase().contains("%hide"))
			{
				String keyToHide = line.split(" ")[1];
				AnswerSet.addToHiddenKeys(keyToHide);
			}
		}
	}

	public ArrayList<AnswerSet> getAnswerSets()
	{
		return answerSets;
	}
	//
	//Implement ActionListener methods
	//

	public void actionPerformed(ActionEvent e) 
	{
		JButton source = (JButton)e.getSource();

		if(source.equals(executeButton))
			executeCode();
		else if(source.equals(saveCodeAsButton))
			saveCodeAs();
		else if(source.equals(saveCodeButton))
			saveCode();
		else if(source.equals(openExistingFileButton))
			openExistingFile();
		else if(source.equals(saveOutputButton))
			saveOutput();
		else if (source.equals(saveOutputAsButton))
			saveOutputAs();
		else if(source.equals(clearCodeAreaButton))
		{
			MainFrame.currCodeFile = null;
			MainFrame.codePanel.codeArea.setText("");
			notificationArea.setText("Cleared code area.");
		}
		else if(source.equals(clearOutputAreaButton))
		{
			MainFrame.currOutputFile = null;
			MainFrame.oPanel.outputArea.setText("");
			notificationArea.setText("Cleared output area.");
		}		
	}
}	//end ControlPanel
