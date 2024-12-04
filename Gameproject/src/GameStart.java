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
        
        // 적 업데이트
        for (Enemy enemy : enemies) {
            enemy.update();
            // 충돌 체크
            if (player.getBounds().intersects(enemy.getBounds())) {
                if (player.isInhaling()) {
                    player.copyAbility(enemy);
                    score += 100;
                }
            }
        }
        
        gamePanel.repaint();
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