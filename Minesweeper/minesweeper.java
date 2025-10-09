import java.util.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class minesweeper 
{
	int []pos;//number at that position
	int []mines;//index in pos of mines
	int rowSize;
	int colSize;
	int level;//current level
	int currentRevealed=0;// keep track of the number of reveled track for win condition
	JButton[] buttonArray;// all the buttons in the grid
	boolean[] visited;// to prevent opening an already opened button
	JButton restart;// global so that win and loss method can change its text
	javax.swing.Timer gameTimer;// global so that win and loss method can change its text
	/*
	(-1,-1) (-1,0) (-1,+1)
	( 0,-1)        ( 0,+1)
	(+1,-1) (+1,0) (+1,+1)
	*/
	static final int[] dRow = {-1,-1,-1, 0, 0, 1, 1, 1};
	static final int[] dCol = {-1, 0, 1,-1, 1,-1, 0, 1};
	//Constructor 
	minesweeper(int level)
	{
		if(level==2)
		{
			this.pos=new int[256];
			this.visited=new boolean[256];
			this.mines=new int[40];
			this.level=level;
			this.rowSize=16;
			this.colSize=16;
		}
		else if(level==3)
		{
			this.pos=new int[480];
			this.visited=new boolean[480];
			this.mines=new int[99];
			this.level=level;
			this.rowSize=30;
			this.colSize=16;
		}
		else
		{
			this.pos=new int[81];
			this.visited=new boolean[81];
			this.mines=new int[10];
			this.level=1;
			this.rowSize=9;
			this.colSize=9;
		}
	}
	// main
	public static void main(String[] args)
	{
		go(1);
	}
	// runs all the setups 
	static void go(int level)
	{
		minesweeper game=new minesweeper(level);
		game.setUp();
		for(int i=0;i<game.rowSize;i++)
		{
			for(int j=0;j<game.colSize;j++)
			{
				System.out.print(game.pos[i*game.colSize+j]+" ");
			}
			System.out.println("");
		}
		System.out.println("");
		game.setupBoard(game.rowSize, game.colSize);
	}
	//GUI set up 
    public void setupBoard(int X, int Y) 
    {
    	JFrame frame = new JFrame("Board " + X + "x" + Y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.buttonArray = new JButton[X * Y]; // store all buttons in global array
        
		 //display items (topPanel)
		JPanel topPanel=new JPanel();
		restart = new JButton("🙂");// start a new game with the same level
		restart.addMouseListener(new MouseAdapter() {
		      @Override
		      public void mouseClicked(MouseEvent e) {
		      	frame.dispose();
		          go(level);
		      }
		  });
		JLabel timerLabel = new JLabel("Time: 0");
		final int[] elapsed = {0}; // seconds
		final boolean[] started = {false}; // track if timer started
		final int[] flagsRemaining = {mines.length}; // track remaining flags
		
		gameTimer = new javax.swing.Timer(1000, e -> {
		    elapsed[0]++;
		    timerLabel.setText("Time: " + elapsed[0]);
		});
		JLabel flagLabel = new JLabel("Flags: " + mines.length);//Flag counter
		topPanel.add(BorderLayout.WEST,flagLabel);
		topPanel.add(BorderLayout.CENTER,restart);
		topPanel.add(BorderLayout.EAST,timerLabel);
        
        //create the grid(mainPanel)
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new GridLayout(X, Y));
        for (int i = 0; i < X * Y; i++) {
            JButton btn = new JButton(".");
            this.buttonArray[i] = btn;
            btn.putClientProperty("index", i);

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                	if(!started[0])// start the timer in the first click
                	{
                		gameTimer.start();
                		started[0]=true;
                	}
                	
                	int idx=(int) ((JComponent) e.getSource()).getClientProperty("index");
                	
                    if (SwingUtilities.isLeftMouseButton(e)) {// reveal the selected button
                        reveale(idx);
                    } 
                    else if (SwingUtilities.isRightMouseButton(e)) {// remove or add flag to a not reveled button
                    	if(buttonArray[idx].getText().equals("."))
                    		{
                    			buttonArray[idx].setText("🚩");
                    			flagsRemaining[0]--;
                    			flagLabel.setText("Flags: "+flagsRemaining[0]);
                    		}
                    	else if(buttonArray[idx].getText().equals("🚩"))
                    		{
                    			buttonArray[idx].setText(".");
                    			flagsRemaining[0]++;
                    			flagLabel.setText("Flags: "+flagsRemaining[0]);
                    		}
                    }
                }
            });

            mainPanel.add(btn);
        }
        
        //Menu bar 
        JMenuBar menuBar = new JMenuBar();// level selector 
		JMenu fileMenu = new JMenu("Levels");
		JMenuItem easyLevel = new JMenuItem("Easy");
		easyLevel.addActionListener(e -> { if (frame != null) frame.dispose(); go(1); });
		JMenuItem mediumLevel = new JMenuItem("Medium");
		mediumLevel.addActionListener(e -> { if (frame != null) frame.dispose(); go(2); });
		JMenuItem hardLevel = new JMenuItem("Hard");
		hardLevel.addActionListener(e -> { if (frame != null) frame.dispose(); go(3); });
		
		fileMenu.add(easyLevel);
		fileMenu.add(mediumLevel);
		fileMenu.add(hardLevel);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
	    
	    //add panels to the frame
        frame.getContentPane().add(BorderLayout.NORTH, topPanel );
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(60 * Y, 60 * X); // scale frame size
        frame.setVisible(true);
    }
	//generate a random set of mine pos index and give numbers in all the other pos index
	void setUp()
	{
		Set<Integer> mineSet = new HashSet<>();
		Random gen=new Random();
		for(int i=0;i < this.mines.length;i++)
		{
			mineSet.add(gen.nextInt(this.pos.length));
		}
		while (mineSet.size() < this.mines.length) 
		{
		    mineSet.add(gen.nextInt(this.pos.length));
		}
		this.mines = mineSet.stream().mapToInt(Integer::intValue).toArray();
		for(int minePos:this.mines)
		{
			int row=minePos/this.colSize;
			int col=minePos%this.colSize;
			for (int k = 0; k < 8; k++) 
			{
			    int nr = row + dRow[k];
			    int nc = col + dCol[k];
			    if (nr >= 0 && nr < rowSize && nc >= 0 && nc < colSize) 
			    {
			        this.pos[nr*colSize + nc]++;
			    }
			}
		}
		
		for(int minePos:mines)
		{
			this.pos[minePos]=9;
		}
		
	}
	// decide what to do based on the current label of that button  
	void reveale (int x)
	{
		if(buttonArray[x].getText().equals("."))// normal 
		{
			flodFlow(x);
		}
		else if(buttonArray[x].getText().equals("🚩"))// don't reveal the flagged button
		{
			return;
		}
		else if(isSatisfied(x))// has the required number of flag around it(already revealed button)
		{
			int row=x/colSize;
			int col=x%colSize;
			for (int k = 0; k < 8; k++) 
			{
			    int nr = row + dRow[k];
			    int nc = col + dCol[k];
			    int nx = nr*colSize + nc;
			    if (nr >= 0 && nr < rowSize && nc >= 0 && nc < colSize ) 
			    {
			    	if(!buttonArray[nx].getText().equals("🚩"))// don't reveal the flagged button
			    		{
			    			if(!flodFlow(nx))// stop propagation if game ended
			    				return;
			    		}
			    }
			}
		}
		
	}
	// actually do the revealing 
	boolean flodFlow(int x)
	{
		if(visited[x])// DP
			return true;
		
		visited[x]=true;
		currentRevealed++;
		
		if(pos[x]==0)// also reveal all its surrounding buttons
		{
			buttonArray[x].setText("");
			buttonArray[x].setEnabled(false);
			buttonArray[x].setBackground(Color.GREEN);
			int row=x/colSize;
			int col=x%colSize;
			for (int k = 0; k < 8; k++) 
			{
			    int nr = row + dRow[k];
			    int nc = col + dCol[k];
			    int nx = nr*colSize + nc;
			    if (nr >= 0 && nr < rowSize && nc >= 0 && nc < colSize ) 
			    {
			    	if(!flodFlow(nx))// stop propagation if game ended
	    				return false;
			    }
			}
		}
		else if(pos[x] == 9)
		{
			loss();// reveled bomb
			return false;
		}
		else
		{
			buttonArray[x].setText(""+this.pos[x]);// simple reveal
		}
		
		// win condition
		if(currentRevealed==pos.length-mines.length)
			{
				win();
				return false;
			}
		return true;
	}
	// loss condition reached 
	void loss()
	{
	    for (int i = 0; i < buttonArray.length; i++) {
	        if (pos[i] == 9) {  
	            // it's a mine
	            if (buttonArray[i].getText().equals("🚩")) {
	                buttonArray[i].setText("🎯"); // correctly flagged mine
	            } else {
	                buttonArray[i].setText("💣"); // mine not flagged
	            }
	        } 
	        else {
	            // safe cell but wrongly flagged
	            if (buttonArray[i].getText().equals("🚩")) {
	                buttonArray[i].setText("❌");
	            }
	        }
	        buttonArray[i].setEnabled(false);
	    }
	    gameTimer.stop();
	    restart.setText("☹️");
	}
	// win condition reached
	void win()
	{
		for(JButton btn:buttonArray)
		{
			btn.setEnabled(false);
		}
		for(int minePos: mines)
		{
			buttonArray[minePos].setText("👑");
		}
		gameTimer.stop();
		restart.setText("😎");
	}
	//helpers
	boolean isSatisfied(int x)
	{
		int count=0;
		int row=x/colSize;
		int col=x%colSize;
		for (int k = 0; k < 8; k++) 
		{
		    int nr = row + dRow[k];
		    int nc = col + dCol[k];
		    int nx = nr*colSize + nc;
		    if (nr >= 0 && nr < rowSize && nc >= 0 && nc < colSize) 
		    {
		    	if(buttonArray[nx].getText().equals("🚩"))
		    		count++;
		    }
		}
		if(count == pos[x])
			return true;
		else
			return false;
	}
}

