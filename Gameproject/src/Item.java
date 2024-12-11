import java.awt.*;

public class Item {
    // 아이템의 위치 좌표
    private int x, y;
    // 아이템의 크기 (픽셀 단위)
    private static final int SIZE = 20;
    
    /**
     * Item 생성자
     * @param x 아이템의 x 좌표
     * @param y 아이템의 y 좌표
     */
    public Item(int x, int y, String type) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * 아이템을 화면에 그리는 메소드
     * HEALTH: 빨간색 원
     */
    public void draw(Graphics g) {
        g.setColor(Color.RED);    // 체력 회복 아이템은 빨간색
        g.fillOval(x, y, SIZE, SIZE);    // 원형으로 아이템 그리기
    }
    
    /**
     * 아이템의 충돌 범위를 반환하는 메소드
     * @return Rectangle 아이템의 충돌 범위
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }
    
    /**
     * 아이템 수집 시 효과를 적용하는 메소드
     * HEALTH: 체력 30 회복
     * @param player 아이템을 수집한 커비
     */
    public void collect(Kirby player) {
        player.heal(30);          // 체력 30 회복
    }
} 