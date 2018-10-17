
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




class RapidRoll extends JFrame implements Runnable, KeyListener {

    JLabel b1, b2, b3, b4, b5, life, lb1, ready, Gameover;
    JPanel p1, p2;
    JTextField t1, t2;
    int x = 300, bx1, bx2, bx3, bx4, bx5, lifex;
    int y = 250, by1 = 0, by2 = 66, by3 = 130, by4 = 196, by5 = by3, lifey;
    int score, lifecounter, scroll = 15, move = 4;
    Random r = new Random();
    boolean goup = false, dead = false, givelife = false, glf = true, lcf = true, cf = false, wait = true;
    Thread myt;
    JMenuBar mymbar;
    JMenu game, help, level, rank;
    JCheckBoxMenuItem easy, med, hard;
    ButtonGroup bg;
    ImageIcon ic1, ic2, ic3, iclife, icgameover, icready;
    
    
    String playerName = "Player";
    
    
    public Connection dbConnect() {    
        Connection conn = null;
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/rapidroll";
            String username = "root";
            String password = "";
            Class.forName(driver);            
            conn = DriverManager.getConnection(url, username, password);
                    
            System.out.println("Connected na");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        return conn;
        
    }
    
    public void getPlayerName() {
//        Create dialog where you can create player name
        JTextField name = new JTextField();
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Player Name"),
                name
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Your Name", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println("Player name: " + name.getText());
            playerName = name.getText();
               
        } else {
            System.out.println("User canceled / closed the dialog, result = " + result);
        }
    
    }
    
    
//    TAWAGON NI AFTER MA GAME OVER
    
    public void insertScore(){
        try {
            Connection conn = dbConnect();
            String query = "INSERT INTO scores(player, score) VALUES(?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, playerName);
            preparedStmt.setInt(2, score);
            preparedStmt.execute();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void valueassaign() {
        bx1 = 10 + r.nextInt(1);
        bx2 = 50 + r.nextInt(1);
        bx3 = 100 + r.nextInt(1);
        bx4 = 140 + r.nextInt(1);
        bx5 = bx3 + 35;
        lifex = -10;

        by1 = 0;
        by2 = 66;
        by3 = 130;
        by4 = 196;
        by5 = by3;
        lifey = -10;
        score = 0;
        lifecounter = 1;
    }

    RapidRoll() {
        super("Rapid Roll");
        setSize(x, y + 65);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.BLUE);

        creatbar();
        valueassaign();
        

        b1 = new JLabel();
        b2 = new JLabel();
        b3 = new JLabel();
        b4 = new JLabel();
        b5 = new JLabel();
        life = new JLabel();
        lb1 = new JLabel();
        ready = new JLabel("img\\ready");
        Gameover = new JLabel("img\\GAMEOVER");

        ic1 = new ImageIcon("img\\1.gif");
        ic2 = new ImageIcon("img\\2.gif");
        ic3 = new ImageIcon("img\\3.gif");
        iclife = new ImageIcon("img\\life.gif");
        icgameover = new ImageIcon("img\\gameover.gif");
        icready = new ImageIcon("img\\ready.gif");

        b1.setIcon(ic1);
        b2.setIcon(ic1);
        b3.setIcon(ic1);
        b4.setIcon(ic1);
        b5.setIcon(ic2);
        life.setIcon(iclife);
        lb1.setIcon(ic3);
        Gameover.setIcon(icgameover);
        ready.setIcon(icready);
        ready.setBounds(0, 50, 300, 50);

        lb1.setBounds(0, 0, 300, 15);

        t1 = new JTextField("Life==>	" + lifecounter, 10);
        t2 = new JTextField("Score==>	" + score, 10);

        t1.setBackground(Color.BLACK);
        t2.setBackground(Color.BLACK);

        t1.setEnabled(false);
        t2.setEnabled(false);

        p1 = new JPanel();
        p2 = new JPanel();

        p1.setBackground(Color.LIGHT_GRAY);
        p1.setBounds(0, 0, x, 20);
        p1.setLayout(new GridLayout(0, 2));
        p1.add(t1);
        p1.add(t2);

        p2.setBackground(Color.CYAN);
        p2.setBounds(0, 20, x, y);
        p2.setLayout(null);
        s_l();

        p2.add(ready);
        p2.add(lb1);
        p2.add(b1);
        p2.add(b2);
        p2.add(b3);
        p2.add(b4);
        p2.add(b5);


        getContentPane().add(p1);
        getContentPane().add(p2);

        show();
        addKeyListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        getPlayerName();
        
        myt = new Thread(this);
        myt.start();
    }

    public void creatbar() {
        mymbar = new JMenuBar();

        game = new JMenu("Game");

        JMenuItem newgame = new JMenuItem("New Game");
        JMenuItem exit = new JMenuItem("Exit");

        newgame.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        reset();
                    }
                });

        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        game.add(newgame);
        game.addSeparator();
        game.add(exit);

        mymbar.add(game);

        level = new JMenu("Level");

        easy = new JCheckBoxMenuItem("Easy");
        med = new JCheckBoxMenuItem("Medium");
        hard = new JCheckBoxMenuItem("Hard");

        bg = new ButtonGroup();
        bg.add(easy);
        bg.add(med);
        bg.add(hard);

        easy.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        scroll = 30;
                        move = 3;
                        reset();
                    }
                });

        med.setSelected(true);
        med.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        scroll = 15;
                        move = 4;
                        reset();
                    }
                });

        hard.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        scroll = 10;
                        move = 8;
                        reset();
                    }
                });

        level.add(easy);
        level.add(med);
        level.add(hard);

        mymbar.add(level);


        help = new JMenu("Help");

        JMenuItem creator = new JMenuItem("Creator");
        JMenuItem instruction = new JMenuItem("How to Play");

        creator.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(p2, "Name :Abdullah al hasb\nRollno :2006331093\nSub :cse\ninstitute :sust");
            }
        });
        
        instruction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(p2, "Don't fall. Keep moving. Get hearts. Mao ra na.");
            }
        });
        
        help.add(creator);
        help.add(instruction);
        mymbar.add(help);
        
        // ******************************************************
        // BUHAT LEADER BOARD
        // *******************************************************
        
        
        
        
        rank = new JMenu("Rank");
        
        JMenuItem leaderboard = new JMenuItem("Leader Board");
        JMenuItem best = new JMenuItem("Best Player");

        leaderboard.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getRank(1); // gusto nako makita tanan so 1
                
                JOptionPane.showMessageDialog(p2, "All Players");
            }
        });
        best.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String bp = getRank(2); // get one best player
                
                JOptionPane.showMessageDialog(p2, "Best Player \n " + bp);
            }
        });
        
        rank.add(leaderboard);
        rank.add(best);
        mymbar.add(rank);
        

        setJMenuBar(mymbar);
    }
    
    public String getRank(int type) {
        Connection conn = dbConnect();
            // ang type 1 ang all the players list
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = null;
                if (type == 1) {
                     rs = stmt.executeQuery("SELECT player, score FROM scores");
                     System.out.println("SELECTED 1");
                    while (rs.next()) {
                        String plyr = rs.getString("player");
                        String scr = rs.getString("score");
                        System.out.println(plyr + " :  " + scr);
                    }
                }
               
                else if(type == 2) {                    
                    rs = stmt.executeQuery("SELECT player, score FROM scores WHERE score = (SELECT max(score) FROM scores)");
                    
                    while (rs.next()) {
                        String plyr = rs.getString("player");
                        String scr = rs.getString("score");
                        System.out.println(plyr + " :  " + scr);
                        return plyr + " " + scr;
                    }
                    
                }
  
            }catch(SQLException e) {
                System.out.println(e);
            }
        return null;
    }
    
    

    public void reset() {
        myt.stop();
        wait = true;
        myt = new Thread(this);
        valueassaign();
        getPlayerName();
        myt.start();
        p2.remove(life);
        lifex = -20;
        lifey = -20;

        t1.setText("Life==>	" + lifecounter);
        t2.setText("Life==>	" + score);

        p2.remove(Gameover);
        p2.add(ready);
    }

    public void s_l() {
        b1.setBounds(bx1, by1 + 10, 70, 10);
        b2.setBounds(bx2, by2 + 10, 70, 10);
        b3.setBounds(bx3, by3 + 10, 70, 10);
        b4.setBounds(bx4, by4 + 10, 70, 10);
        b5.setBounds(bx5, by5, 10, 10);
        life.setBounds(lifex, lifey, 7, 7);

        if (bx5 <= 15 || bx5 >= x - 7 || by5 >= y - 5 || by5 <= 0) {
            dead = true;

            if (lcf == true && lifecounter >= 0) {
                lifecounter--;
                lcf = false;
            }
            t1.setText("Life==>	" + lifecounter);
        }

        if (lifecounter == -1) {
//            Kung ma game over na, tawagon si insertScore para ma sulod sa database ang scores
//            with player name
            System.out.println("GAME OVER: FINAL SCORE: " + score);
            insertScore();
            t1.setText(">>>>>GAME OVER<<<<<");
            p2.add(Gameover);
            Gameover.setBounds(-2, 50, 300, 50);
            try {
                myt.join();
            } catch (InterruptedException e) {
            }
        }

        if (((by5 == by1) && (bx1 - 10 < bx5 && bx5 < (bx1 + 70))) || ((by5 == by2) && (bx2 - 10 < bx5 && bx5 < (bx2 + 70)))
                || ((by5 == by3) && (bx3 - 10 < bx5 && bx5 < (bx3 + 70))) || ((by5 == by4) && (bx4 - 10 < bx5 && bx5 < (bx4 + 70)))) {
            goup = true;
        } else {
            goup = false;
            cf = true;
        }

        if (by1 <= 0) {
            by1 = 250;
            bx1 = 5 + r.nextInt(x - 70);
            if (dead == true) {
                bx5 = bx1 + 36;
                by5 = by1 - 12;
                //b5.setBounds(bx5,by5,7,7);
                dead = false;
                lcf = true;
            }
            if (givelife == true) {
                lifex = bx1 + (10 + r.nextInt(50));
                lifey = by1;
                life.setBounds(lifex, lifey, 7, 7);
                givelife = false;
            }
        } else if (by2 <= 0) {
            by2 = 250;
            bx2 = 5 + r.nextInt(x - 70);
            if (dead == true) {
                bx5 = bx2 + 36;
                by5 = by2 - 12;
                //b5.setBounds(bx5,by5,7,7);
                dead = false;
                lcf = true;
                goup = true;
            }

            if (givelife == true) {
                lifex = bx2 + (10 + r.nextInt(50));
                lifey = by2;
                life.setBounds(lifex, lifey, 7, 7);
                givelife = false;
            }
        } else if (by3 <= 0) {
            by3 = 250;
            bx3 = 5 + r.nextInt(x - 70);
            if (dead == true) {
                bx5 = bx3 + 36;
                by5 = by3 - 12;
                //b5.setBounds(bx5,by5,10,10);
                dead = false;
                lcf = true;
            }

            if (givelife == true) {
                lifex = bx3 + (10 + r.nextInt(50));
                lifey = by3;
                life.setBounds(lifex, lifey, 7, 7);
                givelife = false;
            }
        } else if (by4 <= 0) {
            by4 = 250;
            bx4 = 5 + r.nextInt(x - 70);
            if (dead == true) {
                bx5 = bx4 + 36;
                by5 = by4 - 12;
                //b5.setBounds(bx5,by5,10,10);
                dead = false;
                lcf = true;
            }
            if (givelife == true) {
                lifex = bx4 + (10 + r.nextInt(50));
                lifey = by4;
                life.setBounds(lifex, lifey, 5, 5);
                givelife = false;
            }
        }

        if (lifey <= 10) {
            getContentPane().remove(life);
            givelife = false;
            glf = true;
        }

        by1 -= 1;
        by2 -= 1;
        by3 -= 1;
        by4 -= 1;
        lifey -= 1;

        if (goup == false) {
            by5 += 1;

            if (by5 == by1 || by5 == by2 || by5 == by3 || by5 == by4
                    || by5 == by1 - 1 || by5 == by2 - 1 || by5 == by3 - 1 || by5 == by4 - 1) {
                score += (move * 5);
                //System.out.println(score+"	"+givelife+"	"+lifecounter+"	dead="+dead);
//                int m = move * 5;
//                int s = score - m;
                
                t2.setText("Score==>	" + score );
            }
        }

        if (goup == true) {
            by5--;
        }

        if (glf == true) {
            if (score % 250 == 0) {
                p2.add(life);
                givelife = true;
                glf = false;
            }
        }

        repaint();
        show();
    }

    public void run() {
        for (;;) {
            s_l();
            try {
                if (wait == true) {

                    Thread.sleep(2000);
                    wait = false;
                    p2.remove(ready);
                } else {
                    Thread.sleep(scroll);
                }
            } catch (Exception e) {
            }
        }
    }

    public void keyPressed(KeyEvent ke) {
        if (wait == false) {
            if (ke.getKeyCode() == 37) {
                bx5 -= move;
            }

            if (ke.getKeyCode() == 39) {
                bx5 += move;
            }
        }
        b5.setBounds(bx5, by5, 10, 10);

        if (bx5 + 10 >= lifex && bx5 <= lifex + 4 && by5 + 10 >= lifey && by5 <= lifey + 4) {
            if (cf == true && lifecounter < 6) {
                lifecounter++;
                score += (move * 10);
                cf = false;
            }
            p2.remove(life);
            givelife = false;

            t1.setText("Life==>	" + lifecounter);
        }

        repaint();
        show();
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void keyTyped(KeyEvent ke) {
    }
}
