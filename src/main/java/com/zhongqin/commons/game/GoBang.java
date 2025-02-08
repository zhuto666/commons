package com.zhongqin.commons.game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Kevin
 * @version 1.0
 * @date 2025/2/8 9:52 星期六
 */
public class GoBang extends JFrame {
    // 棋盘大小
    private final int BOARD_SIZE = 15;
    // 格子大小
    private final int CELL_SIZE = 40;
    // 棋盘数据
    private final int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    // 当前玩家（黑方先手）
    private boolean isBlack = true;
    // 游戏是否结束
    private boolean gameOver = false;
    // 显示当前玩家的标签
    private final JLabel currentPlayerLabel;

    /**
     * 构造函数，初始化五子棋游戏窗口
     */
    public GoBang() {
        // 设置窗口标题
        setTitle("五子棋");
        // 设置窗口大小，增加宽度和高度以容纳边框和标签
        setSize(BOARD_SIZE * CELL_SIZE + 20, BOARD_SIZE * CELL_SIZE + 100);
        // 设置关闭操作
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        // 使用 BorderLayout 管理布局
        setLayout(new BorderLayout());
        // 创建一个 JPanel 作为棋盘的容器，并设置边框和间距
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 调用绘制棋盘的方法
                drawBoard(g);
            }
        };
        // 设置棋盘面板的首选大小
        boardPanel.setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
        // 添加边框
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        // 添加间距
        boardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // 初始化当前玩家标签
        currentPlayerLabel = new JLabel("当前玩家: 黑方");
        // 设置标签居中对齐
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // 使用支持中文的字体
        currentPlayerLabel.setFont(new Font("宋体", Font.BOLD, 16));
        // 将棋盘面板添加到窗口的中心
        add(boardPanel, BorderLayout.CENTER);
        // 将当前玩家标签添加到窗口的底部
        add(currentPlayerLabel, BorderLayout.SOUTH);
        // 添加鼠标监听器
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 如果游戏结束，直接返回
                if (gameOver) {
                    return;
                }
                // 计算点击的格子横坐标
                int x = e.getX() / CELL_SIZE;
                // 计算点击的格子纵坐标
                int y = e.getY() / CELL_SIZE;
                // 检查点击位置是否在棋盘范围内且该位置为空
                if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE && board[x][y] == 0) {
                    // 根据当前玩家设置棋子
                    board[x][y] = isBlack ? 1 : 2;
                    // 重绘棋盘
                    boardPanel.repaint();
                    // 检查是否获胜
                    if (checkWin(x, y)) {
                        // 设置游戏结束标志
                        gameOver = true;
                        // 显示获胜信息
                        JOptionPane.showMessageDialog(null, (isBlack ? "黑方" : "白方") + "获胜！");
                    } else {
                        // 切换玩家
                        isBlack = !isBlack;
                        // 更新当前玩家标签
                        currentPlayerLabel.setText("当前玩家: " + (isBlack ? "黑方" : "白方"));
                    }
                }
            }
        });
    }

    /**
     * 检查是否获胜
     *
     * @param x 点击的格子横坐标
     * @param y 点击的格子纵坐标
     * @return 如果获胜返回 true，否则返回 false
     */
    private boolean checkWin(int x, int y) {
        // 四个检查方向：水平、垂直、正对角线、反对角线
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        // 当前棋子
        int current = board[x][y];
        for (int[] dir : directions) {
            // 当前方向上的连续棋子数
            int count = 1;
            // 方向向量
            int dx = dir[0], dy = dir[1];
            // 正方向检查
            for (int i = 1; ; i++) {
                // 计算下一个格子的坐标
                int nx = x + dx * i, ny = y + dy * i;
                // 如果超出棋盘范围，停止检查
                if (nx < 0 || nx >= BOARD_SIZE || ny < 0 || ny >= BOARD_SIZE) {
                    break;
                }
                // 如果棋子不同，停止检查
                if (board[nx][ny] != current) {

                    break;
                }
                // 连续棋子数加1
                count++;
            }
            // 反方向检查
            for (int i = 1; ; i++) {
                // 计算下一个格子的坐标
                int nx = x - dx * i, ny = y - dy * i;
                // 如果超出棋盘范围，停止检查
                if (nx < 0 || nx >= BOARD_SIZE || ny < 0 || ny >= BOARD_SIZE) {
                    break;
                }
                // 如果棋子不同，停止检查
                if (board[nx][ny] != current) {
                    break;
                }
                // 连续棋子数加1
                count++;
            }
            // 如果连续棋子数达到5，返回 true
            if (count >= 5) {
                return true;
            }
        }
        // 如果没有获胜，返回 false // 如果没有获胜，返回 false
        return false;
    }

    /**
     * 绘制棋盘和棋子
     *
     * @param g Graphics 对象
     */
    private void drawBoard(Graphics g) {
        // 绘制棋盘
        for (int i = 0; i < BOARD_SIZE; i++) {
            // 绘制水平线
            g.drawLine(CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE,
                    (BOARD_SIZE - 1) * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE);
            // 绘制垂直线
            g.drawLine(CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2,
                    CELL_SIZE / 2 + i * CELL_SIZE, (BOARD_SIZE - 1) * CELL_SIZE + CELL_SIZE / 2);
        }
        // 绘制棋子
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // 棋子大小
                int chessSize = 16;
                // 计算棋子的横坐标
                int x = i * CELL_SIZE + CELL_SIZE / 2 - chessSize / 2;
                // 计算棋子的纵坐标
                int y = j * CELL_SIZE + CELL_SIZE / 2 - chessSize / 2;
                // 如果该位置有棋子
                if (board[i][j] == 1) {
                    // 设置棋子颜色为黑色
                    g.setColor(Color.BLACK);
                    // 绘制黑色棋子
                    g.fillOval(x, y, chessSize, chessSize);
                } else if (board[i][j] == 2) {
                    // 设置棋子颜色为白色
                    g.setColor(Color.WHITE);
                    // 绘制白色棋子
                    g.fillOval(x, y, chessSize, chessSize);
                }
            }
        }
    }

    /**
     * 主方法，程序入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 创建并显示五子棋窗口
            new GoBang().setVisible(true);
        });
    }

}
