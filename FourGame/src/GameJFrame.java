

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

// 游戏主界面类，继承JFrame，并实现KeyListener和ActionListener接口
public class GameJFrame extends JFrame implements KeyListener, ActionListener {
    // 拼图数据
    int[][] data;
    // 空白块的位置
    int x = 0;
    int y = 0;

    // 胜利条件
    int[][] win = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    int step = 0; // 步数计数器

    int timeRemaining = 300; // 倒计时时间，单位为秒（300秒 = 5分钟）
    JLabel timerLabel = new JLabel("剩余时间: 05:00"); // 显示倒计时的标签
    Timer timer; // 定时器对象

    // 菜单项
    JMenuItem replayItem = new JMenuItem("重新游戏");
    JMenu difficultyMenu = new JMenu("难度");
    JMenuItem easyItem = new JMenuItem("简单 (3x3)");
    JMenuItem hardItem = new JMenuItem("困难 (4x4)");
    JMenuItem reLoginItem = new JMenuItem("重新登录");
    JMenuItem closeItem = new JMenuItem("关闭游戏");
    JMenuItem accountItem = new JMenuItem("一键通关");

    int currentDifficulty = 4; // 默认难度为 4x4
    private final String IMAGE_DIR_3X3 = "D:\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\picture2\\";
    private final String IMAGE_DIR_4X4 = "D:\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\picture1\\";
    // 构造方法，初始化界面
    public GameJFrame() {
        initJFrame();
        initJMenubar();//菜单栏
        initData();//数据
        initImage();//图片界面
        startTimer();//倒计时
        this.setVisible(true);
    }

    // 初始化数据
    private void initData() {
        // 创建一个包含0到15的整数数组，其中0代表空格位置
        int size = currentDifficulty;
        data = new int[size][size];
        int[] tempArr = new int[size * size];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = i;
        }

        // 使用do-while循环来随机打乱数组，直到得到一个可解的拼图状态
        do {
            // 调用shuffleArray方法来随机打乱数组
            shuffleArray(tempArr);
        } while (!isSolvable(tempArr)); // 如果当前打乱后的数组不是一个可解的拼图状态，则继续打乱

        // 遍历打乱后的数组，将元素分配到二维数组data中
        for (int i = 0; i < tempArr.length; i++) {
            // 检查当前元素是否为0（即空格位置）
            if (tempArr[i] == 0) {
                // 如果是空格位置，则计算出它在二维数组中的x和y坐标
                // i / 4 是因为数组是4x4的，所以每4个元素换一行
                x = i / size;
                y = i % size; // i % 4 用来计算当前元素在当前行的哪一列
            }
            // 将tempArr中的元素放入data二维数组中
            data[i / size][i % size] = tempArr[i];
        }
    }

    //打乱数组
    private void shuffleArray(int[] array) {
        // 创建一个新的Random对象来生成随机数
        Random r = new Random();

        // 从数组的最后一个元素开始向前遍历 ，方便添加其他模式
        for (int i = array.length - 1; i > 0; i--) {
            // 生成一个从0到i（包括i）的随机整数
            int j = r.nextInt(i + 1);
            // 交换当前元素array[i]和随机选中的元素array[j]
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        // 循环结束后，数组中的元素将被随机打乱
    }

    // 判断是否可解
    private boolean isSolvable(int[] array) {
        int inversions = 0; // 记录逆序对的数量
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                // 如果前面的数字大于后面的数字，并且这两个数字都不是0
                if (array[i] > array[j] && array[i] != 0 && array[j] != 0) {
                    inversions++; // 增加逆序对的数量
                }
            }
        }
        int size = currentDifficulty;
        int row = (findZeroPosition(array) / size) + 1; // 找到0的位置，并转换为1开始的行索引
        // 拼图是否可解的判断依据：逆序对的数量是偶数，并且空格所在的行索引也是奇数，或者逆序对的数量是奇数，并且空格所在的行索引也是偶数
        return (inversions % 2 == 0) == (row % 2 != 0);
    }

    // 找到数组中0的位置
    private int findZeroPosition(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                return i; // 返回0的位置索引
            }
        }
        return -1; // 如果没有找到0，则返回-1
    }

    // 初始化图片界面
    private void initImage() {
        this.getContentPane().removeAll(); // 移除容器中的所有组件
        if (victory()) { // 如果游戏胜利
            showVictoryDialog(); // 显示胜利对话框
            return; // 退出方法
        }

        JLabel stepCount = new JLabel("步数: " + step); // 创建显示步数的标签
        stepCount.setBounds(50, 30, 100, 20); // 设置标签的位置和大小
        this.getContentPane().add(stepCount); // 将标签添加到容器中

        int size = currentDifficulty;
        int tileSize = 178; // 调整这个值以适应不同大小的拼图
        String imageDir = (currentDifficulty == 3) ? IMAGE_DIR_3X3 : IMAGE_DIR_4X4;

        timerLabel.setBounds(150, 30, 200, 20); // 假设timerLabel是之前定义好的一个标签，这里设置其位置和大小
        this.getContentPane().add(timerLabel); // 将timerLabel添加到容器中

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = data[i][j];
                JLabel jLabel = new JLabel(new ImageIcon(imageDir + number + ".jpg"));
                jLabel.setBounds(tileSize * j + 70, tileSize * i + 85, tileSize, tileSize);
                jLabel.setBorder(new BevelBorder(0));
                this.getContentPane().add(jLabel);
            }
        }
        this.getContentPane().repaint();
    }

    // 初始化菜单栏
    private void initJMenubar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu functionJMenu = new JMenu("功能");
        JMenu aboutJMenu = new JMenu("一键通关");

        functionJMenu.add(replayItem);
        functionJMenu.add(difficultyMenu);
        functionJMenu.add(reLoginItem);
        functionJMenu.add(closeItem);

        difficultyMenu.add(easyItem);
        difficultyMenu.add(hardItem);

        aboutJMenu.add(accountItem);

        replayItem.addActionListener(this);
        easyItem.addActionListener(this);
        hardItem.addActionListener(this);
        reLoginItem.addActionListener(this);
        closeItem.addActionListener(this);
        accountItem.addActionListener(this);

        jMenuBar.add(functionJMenu);
        jMenuBar.add(aboutJMenu);
        this.setJMenuBar(jMenuBar);
    }

    // 初始化主界面
    private void initJFrame() {
        this.setSize(820, 880);
        this.setTitle("拼图单机1.0");
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.addKeyListener(this);
    }

    // 开始计时器
    private void startTimer() {
        // 创建一个新的计时器，每隔1000毫秒（即1秒）触发一次事件，并绑定一个动作监听器
        timer = new Timer(1000, new ActionListener() {
            // 当计时器触发事件时执行该方法
            @Override
            public void actionPerformed(ActionEvent e) {
                // 剩余时间减一
                timeRemaining--;
                // 更新倒计时标签
                updateTimerLabel();

                // 如果剩余时间小于等于0
                if (timeRemaining <= 0) {
                    // 停止计时器
                    timer.stop();
                    // 显示游戏结束对话框
                    showGameOverDialog();
                }
            }
        });
        // 启动计时器
        timer.start();
    }

    // 更新倒计时标签的显示内容
    private void updateTimerLabel() {
        // 计算剩余分钟数
        int minutes = timeRemaining / 60;
        // 计算剩余秒数（取模运算）
        int seconds = timeRemaining % 60;
        // 使用String.format方法格式化字符串，并设置到timerLabel上
        // %02d表示整数格式，不足两位时前面补0
        timerLabel.setText(String.format("剩余时间: %02d:%02d", minutes, seconds));
    }

    // 显示胜利对话框
    private void showVictoryDialog() {
        JOptionPane.showMessageDialog(this, "胜利！", "胜利", JOptionPane.INFORMATION_MESSAGE);
        step = 0;
        timer.stop();
        initData();
        initImage();
        timeRemaining = 300;
        startTimer();
    }

    // 显示游戏结束对话框
    private void showGameOverDialog() {
        JOptionPane.showMessageDialog(this, "时间到了，游戏结束！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
        step = 0;
        initData();
        initImage();
        timeRemaining = 300;
        startTimer();
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "一键通关", true);
        aboutDialog.setSize(250, 250);
        aboutDialog.setLocationRelativeTo(this);
        JLabel jLabel = new JLabel("扫码一键通关");
        jLabel.setBounds(30, 0, 100, 50);
        aboutDialog.getContentPane().add(jLabel);
        JLabel QRcodeJLabel=new JLabel(new ImageIcon("D:\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\QRcode\\QRcode.jpg"));
        aboutDialog.getContentPane().add(QRcodeJLabel);
        aboutDialog.setVisible(true);
    }


    // 判断是否胜利
    public boolean victory() {
        int size = currentDifficulty;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (data[i][j] != (i * size + j + 1) % (size * size)) {
                    return false;
                }
            }
        }
        return true;
    }


    // 键盘事件处理
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
//    按下A时触发显示全部图片
    // 定义一个公共方法，用于处理键盘按键事件
    public void keyPressed(KeyEvent e) {
        // 从KeyEvent对象中获取按键的编码
        int code = e.getKeyCode();

        // 判断按键编码是否为A
        if (code == 65) {
            // 清除当前窗口内容面板上的所有组件
            this.getContentPane().removeAll();
            String imageDir = (currentDifficulty == 3) ? IMAGE_DIR_3X3 : IMAGE_DIR_4X4;
            // 创建一个新的JLabel对象，并为其设置一个ImageIcon作为图标
            JLabel all = new JLabel(new ImageIcon(imageDir+"all.jpg"));

            // 设置JLabel的位置和大小
            // 这里使用了setBounds方法，该方法需要四个参数：x坐标，y坐标，宽度，高度
            all.setBounds(70, 85, 710, 710);

            // 将新创建的JLabel添加到当前窗口的内容面板上
            this.getContentPane().add(all);

            // 强制内容面板重绘，以显示新添加的JLabel
            this.getContentPane().repaint();
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        // 如果游戏已经胜利，则直接返回，不执行后续操作
        if (victory()) {
            return;
        }
        int code = e.getKeyCode();
        System.out.println(code);

        int size = currentDifficulty;
        if (code == 37 && y < size - 1) { // 左
            data[x][y] = data[x][y + 1];
            data[x][y + 1] = 0;
            y++;
            step++;
            initImage();
        } else if (code == 38 && x < size - 1) { // 上
            data[x][y] = data[x + 1][y];
            data[x + 1][y] = 0;
            x++;
            step++;
            initImage();
        } else if (code == 39 && y > 0) { // 右
            data[x][y] = data[x][y - 1];
            data[x][y - 1] = 0;
            y--;
            step++;
            initImage();
        } else if (code == 40 && x > 0) { // 下
            data[x][y] = data[x - 1][y];
            data[x - 1][y] = 0;
            x--;
            step++;
            initImage();
        } else if (code == 65) { // 显示全图
            initImage();
        } else if (code == 87) { // 按下W键直接进入胜利状态
            data = new int[][]{
                    {1, 2, 3, 4},
                    {5, 6, 7, 8},
                    {9, 10, 11, 12},
                    {13, 14, 15, 0}
            };
            initImage();
        }
    }


    // 菜单项事件处理
    @Override
    public void actionPerformed(ActionEvent e) {
        Object object = e.getSource();
        if (object == replayItem) {
            System.out.println("重新游戏");
            step = 0;
            timer.stop();
            initData();
            initImage();
            timeRemaining = 300; // 重置时间
            startTimer();
        } else if (object == easyItem) {
            System.out.println("选择难度一 (3x3)");
            currentDifficulty = 3;
            step = 0;
            timer.stop();
            initData();
            initImage();
            timeRemaining = 300; // 重置时间
            startTimer();
        } else if (object == hardItem) {
            System.out.println("选择难度二 (4x4)");
            currentDifficulty = 4;
            step = 0;
            timer.stop();
            initData();
            initImage();
            timeRemaining = 300; // 重置时间
            startTimer();
        } else if (object == reLoginItem) {
            System.out.println("重新登录");
            this.setVisible(false);
            timer.stop();
            new LoginJFrame();
        } else if (object == closeItem) {
            System.out.println("关闭游戏");
            System.exit(0);
        } else if (object == accountItem) {
            System.out.println("公众号");
            showAboutDialog();
        }
    }
}