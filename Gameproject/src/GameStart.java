import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameStart extends JFrame {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 800;
    private GamePanel gamePanel;
    private Kirby player;
    private Timer gameTimer;
    public static ArrayList<Platform> platforms = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private int score = 0;
    private int currentStage=1;
    
    public GameStart() {
        System.out.println("GameMain 초기화 중...");  // 실행 확인용
        
        // 기본 프레임 설정
        setTitle("Kirby Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        
        // 게임 요소 초기화
        player = new Kirby(100, 400);
        
        // 플랫폼 초기화
        platforms.add(new Platform(300, 500, 200, 20));
        platforms.add(new Platform(600, 400, 200, 20));
        
        // 적 초기화
        enemies.add(new Enemy(400, 300, "FIRE"));
        enemies.add(new Enemy(700, 200, "ICE"));
        enemies.add(new Enemy(200, 400, "DARK"));
        
        // 게임 패널 설정
        gamePanel = new GamePanel();
        add(gamePanel);
        
        // 컨트롤 및 게임 루프 설정
        setupControls();
        setupGameLoop();
        
        // 프레임 마무리
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupControls() {
        InputMap im = gamePanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamePanel.getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "LEFT_PRESSED");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "LEFT_RELEASED");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "RIGHT_PRESSED");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "RIGHT_RELEASED");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "JUMP");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, false), "START_INHALE");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, true), "STOP_INHALE");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0, false), "USE_ABILITY");
        
        am.put("LEFT_PRESSED", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.setMovingLeft(true);
            }
        });
        
        am.put("LEFT_RELEASED", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.setMovingLeft(false);
            }
        });
        
        am.put("RIGHT_PRESSED", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.setMovingRight(true);
            }
        });
        
        am.put("RIGHT_RELEASED", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.setMovingRight(false);
            }
        });
        
        am.put("JUMP", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.jump();
            }
        });
        
        am.put("START_INHALE", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.startInhale();
            }
        });
        
        am.put("STOP_INHALE", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.stopInhale();
            }
        });
        
        am.put("USE_ABILITY", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.useAbility();
            }
        });
    }
    
    private void setupGameLoop() {
        gameTimer = new Timer(16, e -> {  // 약 60FPS
            updateGame();
        });
        gameTimer.start();
    }
    
    private void updateGame() {
        player.update();
        
        if(!player.isAlive()) {
        	gameTimer.stop();
        	showGameOverDialog();
        	return;
        }
        
        
        // 적 업데이트
        enemies.removeIf(enemy -> !enemy.isAlive());
        for (Enemy enemy : enemies) {
            enemy.update();
            // 충돌 체크
            
            if (player.getBounds().intersects(enemy.getBounds())) {
                if (player.isInhaling()) {
                	//흡입 중이라면
                    player.copyAbility(enemy);
                    score += 100;  
                }else {
                	//흡입 중이 아니라면               
                	player.takeDamage(10);
                	System.out.println("Current HP"+player.getHp());
                	
                }           
                
            }
        }
        if (enemies.isEmpty()) {
            nextStage();  // 다음 스테이지로 진행
        }
        
        gamePanel.repaint();
    }
    private void showGameOverDialog() {
        // 종료 창 생성
        JFrame gameOverFrame = new JFrame("Game Over");
        gameOverFrame.setSize(400, 300);
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setLayout(new BorderLayout());

        // 메시지 패널
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(2, 1));
        JLabel messageLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        JLabel scoreLabel = new JLabel("Your Score: " + score, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        messagePanel.add(messageLabel);
        messagePanel.add(scoreLabel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Restart Game");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        // 버튼 동작 설정
        restartButton.addActionListener(e -> {
            gameOverFrame.dispose();  // 창 닫기
            restartGame();  // 게임 재시작
        });

        exitButton.addActionListener(e -> System.exit(0));  // 프로그램 종료

        // 창에 추가
        gameOverFrame.add(messagePanel, BorderLayout.CENTER);
        gameOverFrame.add(buttonPanel, BorderLayout.SOUTH);

        gameOverFrame.setLocationRelativeTo(null);  // 화면 중앙에 표시
        gameOverFrame.setVisible(true);
    }
    private void restartGame() {
        // 초기화된 상태로 새 게임 시작
        this.dispose();  // 현재 창 닫기
        SwingUtilities.invokeLater(() -> {
            GameStart newGame = new GameStart();
            newGame.setVisible(true);
        });
    }
    private void nextStage() {
        // 적 리스트와 Kirby 초기화
        enemies.clear();  
        platforms.clear();  

        // 다음 스테이지 데이터 설정
        setupStage(currentStage + 1);  
        currentStage++;  
        System.out.println("Proceeding to Stage: " + currentStage);
    }
    private void setupStage(int stageNumber) {
        switch (stageNumber) {
            case 1:
                // 스테이지 1 설정
                platforms.add(new Platform(300, 500, 200, 20));
                platforms.add(new Platform(600, 400, 200, 20));
                enemies.add(new Enemy(400, 300, "FIRE"));
                enemies.add(new Enemy(700, 200, "ICE"));
                break;

            case 2:
                // 스테이지 2 설정
                platforms.add(new Platform(200, 450, 250, 20));
                platforms.add(new Platform(500, 350, 250, 20));
                platforms.add(new Platform(800, 500, 150, 20));
                enemies.add(new Enemy(300, 200, "DARK"));
                enemies.add(new Enemy(700, 150, "ICE", true));
                enemies.add(new Enemy(900, 300, "FIRE"));
                break;

            case 3:
                // 스테이지 3 설정
                platforms.add(new Platform(150, 500, 150, 20));
                platforms.add(new Platform(450, 400, 300, 20));
                enemies.add(new Enemy(300, 200, "FIRE", true));
                enemies.add(new Enemy(600, 300, "DARK"));
                break;

            default:
                // 마지막 스테이지 클리어
                showGameClearDialog();  // 게임 클리어 창 표시
                break;
        }
    }
    private void showGameClearDialog() {
        JFrame gameClearFrame = new JFrame("Game Clear!");
        gameClearFrame.setSize(400, 300);
        gameClearFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameClearFrame.setLayout(new BorderLayout());

        // 메시지 패널
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(2, 1));
        JLabel messageLabel = new JLabel("Congratulations! You cleared the game!", SwingConstants.CENTER);
        JLabel scoreLabel = new JLabel("Your Score: " + score, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messagePanel.add(messageLabel);
        messagePanel.add(scoreLabel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Restart Game");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        // 버튼 동작 설정
        restartButton.addActionListener(e -> {
            gameClearFrame.dispose();
            restartGame();
        });

        exitButton.addActionListener(e -> System.exit(0));

        gameClearFrame.add(messagePanel, BorderLayout.CENTER);
        gameClearFrame.add(buttonPanel, BorderLayout.SOUTH);
        gameClearFrame.setLocationRelativeTo(null);
        gameClearFrame.setVisible(true);
    }





    
    public static void main(String[] args) {
        System.out.println("게임 시작...");  // 실행 확인용
        SwingUtilities.invokeLater(() -> {
            System.out.println("창 생성 중...");  // 실행 확인용
            GameStart game = new GameStart();
            game.setVisible(true);
        });
    }
    
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // 배경 그리기
            g.setColor(new Color(135, 206, 235));  // 하늘색 배경
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // 바닥 그리기
            g.setColor(Color.GREEN);
            g.fillRect(0, 600, getWidth(), 200);
            
            // 플레이어 그리기
            player.draw(g);
            
            // 플랫폼 그리기
            for (Platform platform : platforms) {
                platform.draw(g);
            }
            
            // 적 그리기
            for (Enemy enemy : enemies) {
                enemy.draw(g);
            }
            
            // 점수 표시
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 20, 30);
        }
    }
} 