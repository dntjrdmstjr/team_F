import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class GameStart extends JFrame {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 800;
    private JPanel mainContainer;
    private JPanel mainMenuPanel;
    private GamePanel gamePanel;
    private boolean gameStarted = false;
    private Kirby player;
    private Timer gameTimer;
    public static ArrayList<Platform> platforms = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    private static int score = 0;
    private int currentStage=1;
    private static List<Item> items = new ArrayList<>();  // static으로 변경
    private static List<Projectile> projectiles = new ArrayList<>();
    private static final int MAX_STAGE=5; 
    
    public GameStart() {
        System.out.println("GameMain 초기화 중...");  // 실행 확인용
        
        // 기본 프레임 설정
        setTitle("CyberPunk Kirby");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new BorderLayout());
        
        mainContainer = new JPanel(new CardLayout());
        add(mainContainer, BorderLayout.CENTER);
        
        setupMainMenu();
        
        setupGamePanel();
        
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
       
        
        // 컨트롤 및 게임 루프 설정
        setupControls();
        setupGameLoop();
        
        mainContainer.add(mainMenuPanel, "MainMenu");
        mainContainer.add(gamePanel, "Game");
        
        // 프레임 마무리
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupMainMenu() {
        mainMenuPanel = new JPanel() {
            private Image background = new ImageIcon("img/industrial-background.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };

        mainMenuPanel.setLayout(new GridBagLayout()); // 중앙 배치를 위한 GridBagLayout 사용
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0); // 컴포넌트 간 여백 설정
        gbc.gridx = 0; // 컴포넌트 X축 중앙 정렬
        gbc.gridy = 0; // 첫 번째 행

        // 게임 제목 라벨
        JLabel titleLabel = new JLabel("CyberPunk Kirby", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        mainMenuPanel.add(titleLabel, gbc);

        // 버튼 배치
        gbc.gridy = 1; // 두 번째 행
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));
        startButton.setBackground(Color.BLACK); // 버튼 배경색 변경
        startButton.setForeground(Color.WHITE); // 버튼 텍스트 색상 변경
        startButton.setFocusPainted(false); // 버튼 포커스 테두리 제거
        startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // 버튼 테두리 설정
        startButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainContainer.getLayout();
            cl.show(mainContainer, "Game"); // 게임 화면으로 전환
            gameStarted = true;
        });

        mainMenuPanel.add(startButton, gbc);
    }


    private void setupGamePanel() {
        player = new Kirby(100, 400); // Kirby 초기화
        gamePanel = new GamePanel(); // 게임 패널 설정
        setupControls(); // 키보드 컨트롤 설정
        setupGameLoop(); // 게임 루프 설정
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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0, false), "SHOOT");
        
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
        
        am.put("SHOOT", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                player.shootHeldEnemy();
            }
        });
    }
    
    private void setupGameLoop() {
    	if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();  // 기존 타이머를 멈춤
        }

        gameTimer = new Timer(1000 / 60, e -> {  // 정확히 60FPS
            updateGame();
        });
        gameTimer.start();
    }
    
    private void updateGame() {
        player.update();
        
        player.updateInvincibility(); //무적 상태 업그레이드
        
        if(!player.isAlive()) {
            gameTimer.stop();
            showGameOverDialog();
            return;
        }
        for(Enemy enemy : enemies) {
        	if (!player.isInvincible() && player.getBounds().intersects(enemy.getBounds())) {
        		player.takeDamage(10);
        		System.out.println("Current HP: " + player.getHp());
        	}
        }
        
        // 적 업데이트
        enemies.removeIf(enemy -> !enemy.isAlive());
        for (Enemy enemy : enemies) {
            enemy.update();
            
            // 흡입 체이거나 적을 머금고 있을 때는 데미지를 받지 않음
            if (player.isInhaling() || player.isHoldingEnemy()) {
                player.copyAbility(enemy);
            } else if (player.getBounds().intersects(enemy.getBounds())) {
                // 일반 상태에서만 데미지
                player.takeDamage(10);
                System.out.println("Current HP: " + player.getHp());
            }
        }
        for (Item item : items) {
        	if (player.getBounds().intersects(item.getBounds())) {
        		item.collect(player);
        	}
        }
        projectiles.removeIf(p -> {
            p.update(); // 발사체 위치 업데이트

            for (Enemy enemy : enemies) {
                if (p.isActive() && enemy.isAlive() && p.getBounds().intersects(enemy.getBounds())) {
                    enemy.takeDamage(50); // 적에게 50의 데미지
                    return true; // 발사체 제거
                }
            }
            return !p.isActive();
        });
        if (enemies.isEmpty()) {
            nextStage();  // 다음 스테이지로 진행
        }
        items.removeIf(item -> {
            if (player.getBounds().intersects(item.getBounds())) {
                item.collect(player);  // Kirby에게 아이템 효과 적용
                return true;  // 충돌한 아이템 제거
            }
            return false;
        });
        
        
        // 발사체 업데이트
        projectiles.removeIf(p -> !p.isActive());
        for (Projectile p : projectiles) {
            p.update();
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
    	
    	enemies.clear();
    	platforms.clear();
    	items.clear();
    	
        // 초기화된 상태로 새 게임 시작
        this.dispose();  // 현재 창 닫기
        SwingUtilities.invokeLater(() -> {
            GameStart newGame = new GameStart();
            newGame.setVisible(true);
        });
    }
    private void nextStage() {
    	if (currentStage >= MAX_STAGE) {
    		showGameClearDialog();
    		return;
    	}
        // 적 리스트와 Kirby 초기화
        enemies.clear();  
        platforms.clear();  

        // 다음 스테이지 데이터 설정
        setupStage(currentStage + 1);  
        currentStage++;  
        System.out.println("Proceeding to Stage: " + currentStage);
        
        player.startInvincibility(2000); //2초간 무적
    }
    public static void spawnItem(int x, int y) {
        double random = Math.random();
        if (random < 0.3) {
            items.add(new Item(x, y, "HEALTH"));
        }
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
                items.add(new Item(250, 430, "ENERGY"));
                break;

            case 3:
                // 스테이지 3 설정
                platforms.add(new Platform(150, 500, 150, 20));
                platforms.add(new Platform(450, 400, 300, 20));
                enemies.add(new Enemy(300, 200, "FIRE", true));
                enemies.add(new Enemy(600, 300, "DARK"));
                items.add(new Item(250, 450, "HEALTH"));
                break;
            case 4:
                // 스테이지 4 설정
                platforms.add(new Platform(100, 400, 300, 20));
                platforms.add(new Platform(500, 300, 300, 20));
                platforms.add(new Platform(900, 500, 200, 20));
                enemies.add(new Enemy(200, 250, "ICE", true));
                enemies.add(new Enemy(800, 250, "FIRE"));
                items.add(new Item(550, 280, "HEALTH"));
                break;

            case 5:
                // 스테이지 5 설정
                platforms.add(new Platform(250, 450, 200, 20));
                platforms.add(new Platform(600, 350, 250, 20));
                platforms.add(new Platform(850, 450, 150, 20));
                enemies.add(new Enemy(300, 300, "FIRE"));
                enemies.add(new Enemy(700, 150, "ICE", true));
                enemies.add(new Enemy(950, 300, "DARK", true));
                items.add(new Item(650, 330, "ENERGY"));
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
    
    public static void addScore(int points) {
    	score+=points;
    }
    public static void addProjectile(Projectile p) {
    	projectiles.add(p);
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
            
            // 아이템 그리기
            for (Item item : items) {
                item.draw(g);
            }
            
            // 발사체 그리기
            for (Projectile p : projectiles) {
                p.draw(g);
            }
            
            // 점수 표시
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 20, 30);
        }
    }
} 