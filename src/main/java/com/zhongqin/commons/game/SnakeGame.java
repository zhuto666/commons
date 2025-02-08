package com.zhongqin.commons.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * SnakeGame类是贪吃蛇游戏的主窗口类，继承自JFrame。
 * 它创建了一个GamePanel实例并设置了窗口的基本属性。
 *
 * @author Kevin
 */
public class SnakeGame extends JFrame {

    /**
     * SnakeGame类的构造函数，初始化游戏窗口。
     */
    public SnakeGame() {
        this.add(new GamePanel());
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }

}

/**
 * GamePanel类是贪吃蛇游戏的主面板，继承自JPanel并实现了ActionListener接口。
 * 它负责游戏的逻辑处理和绘制。
 */
class GamePanel extends JPanel implements ActionListener {

    /**
     * 窗口宽度
     */
    private static final int WIDTH = 600;

    /**
     * 窗口高度
     */
    private static final int HEIGHT = 600;

    /**
     * 每个单元格的大小
     */
    private static final int UNIT_SIZE = 20;

    /**
     * 初始延迟时间
     */
    private static final int INITIAL_DELAY = 150;

    /**
     * 蛇的x坐标数组，用于存储蛇身体各部分的x坐标。
     */
    private final int[] x = new int[WIDTH * HEIGHT / UNIT_SIZE];

    /**
     * 蛇的y坐标数组，用于存储蛇身体各部分的y坐标。
     */
    private final int[] y = new int[WIDTH * HEIGHT / UNIT_SIZE];

    /**
     * 蛇的身体部分数量
     */
    private int bodyParts = 3;

    /**
     * 吃掉的苹果数量
     */
    private int applesEaten;

    /**
     * 苹果的x坐标
     */
    private int appleX;

    /**
     * 苹果的y坐标
     */
    private int appleY;

    /**
     * 蛇的移动方向
     */
    private char direction = 'R';

    /**
     * 游戏是否运行
     */
    private boolean running = false;

    /**
     * 定时器
     */
    private Timer timer;

    /**
     * 延迟时间
     */
    private int delay = INITIAL_DELAY;

    public GamePanel() {
        // 初始化游戏面板
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // 设置背景颜色为黑色
        this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        // 设置最小尺寸为窗口尺寸
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        // 设置面板为焦点可获得
        this.setBackground(Color.BLACK);
        // 设置键盘监听器
        this.setFocusable(true);
        // 添加键盘监听器
        this.addKeyListener(new MyKeyAdapter());
        // 启动游戏
        startGame();
    }

    /**
     * 开始游戏的方法，初始化蛇的位置、生成新的苹果、设置游戏状态并启动定时器。
     */
    public void startGame() {
        // 初始化蛇的位置
        x[0] = 100;
        y[0] = 100;
        x[1] = 80;
        y[1] = 100;
        x[2] = 60;
        y[2] = 100;
        // 生成新的苹果
        newApple();
        // 设置游戏状态
        running = true;
        timer = new Timer(delay, this);
        timer.start();
    }

    /**
     * 移动蛇的方法，根据当前方向更新蛇身体各部分的坐标。
     */
    public void move() {
        // 更新蛇身体各部分的坐标
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        // 根据方向更新蛇头的坐标
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    /**
     * 检查蛇是否吃到苹果的方法，如果吃到则增加蛇的长度、分数并生成新的苹果。
     */
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            // 蛇每吃一个苹果，速度增加
            delay = Math.max(INITIAL_DELAY - applesEaten * 5, 50); // 最小延迟时间设为50毫秒
            timer.setDelay(delay);
        }
    }

    /**
     * 生成新的苹果的方法，随机生成苹果的位置。
     */
    public void newApple() {
        Random random = new Random();
        appleX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    /**
     * 检查蛇是否发生碰撞的方法，包括撞到自身和边界。
     */
    public void checkCollisions() {
        // 检查蛇头是否撞到身体
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // 检查蛇头是否撞到边界
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        // 如果游戏结束，停止定时器
        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        // 调用父类的paintComponent方法
        super.paintComponent(g);
        // 绘制游戏界面
        draw(g);
    }

    /**
     * 绘制游戏界面的方法，包括蛇、苹果和分数。
     */
    public void draw(Graphics g) {
        if (running) {
            // 绘制苹果
            g.setColor(Color.RED);
            g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            // 绘制蛇
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // 绘制分数
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            // 游戏结束时绘制游戏结束界面
            gameOver(g);
        }
    }

    /**
     * 绘制游戏结束界面的方法，显示“Game Over”和最终分数。
     */
    public void gameOver(Graphics g) {
        // 绘制游戏结束文本
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics1.stringWidth("Game Over")) / 2, HEIGHT / 2);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    /**
     * 定时器触发的方法，每过一段时间调用一次，用于更新游戏状态。
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // 检查游戏状态
        move();
        // 检查是否吃到苹果
        checkApple();
        // 检查是否发生碰撞
        checkCollisions();
        // 重绘界面
        repaint();
    }

    /**
     * 键盘事件适配器类，用于处理键盘输入。
     */
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}