package com.hspedu.tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.Vector;

/**
 * 坦克组件，用于构造游戏内容
 */
public class TankPanel extends JPanel implements KeyListener, Runnable {
    //获取游戏信息记录器
    Recorder recorder = Recorder.getRecorder();

    MyTank mt = null; //自己的坦克

    //敌人的坦克
    //因为敌人坦克有很多，而且涉及到多线程，需要线程安全的集合来囊括
    Vector<EnemyTank> enemyTanks = new Vector<>();

    //爆炸集合，用于存放并播出爆炸效果
    Vector<Boom> booms = new Vector<>();

    //定义三张图片用于显示爆炸效果
    Image boomImage1 = null;
    Image boomImage2 = null;
    Image boomImage3 = null;


    /**
     * 构造方法，用于初始化坦克组件
     */
    public TankPanel() {
        //初始化双方坦克
        initialize();

        //将敌方坦克信息传入游戏记录器
        recorder.addEnemyTanks(enemyTanks);
        //将我方坦克信息传入游戏记录器
        recorder.addMyTank(mt);

        //初始化爆炸效果
        boomImage();
    }


    /**
     * 初始化坦克方法
     */
    public void initialize() {
        boolean loop = true;
        int res = 0;
        Scanner scanner = new Scanner(System.in);

        while (loop) {
            System.out.println("输入1新开始游戏，输入2继续上局游戏");
            res = scanner.nextInt();
            if (res == 1 || res == 2) {
                loop = false;
            }
        }

        switch (res) {
            //新开始游戏
            case 1:
                //初始化自己的坦克
                mt = new MyTank(100, 100);
                mt.setSpeed(10); //设置坦克速度

                //初始化敌人的坦克
                int enemyTankSize = 3; //敌方坦克数量
                for (int i = 0; i < enemyTankSize; i++) {
                    EnemyTank enemyTank = new EnemyTank((100 * (i + 1)), 0);
                    new Thread(enemyTank).start(); //为每一个敌方坦克启动一个单独的线程
                    enemyTanks.add(enemyTank); //将坦克加入敌方坦克集合
                    enemyTanks.get(i).setSpeed(2);//设置敌人坦克速度
                    enemyTanks.get(i).setDic(Tank.Direction.DOWN);//设置敌方坦克初始化方向朝下
                    enemyTanks.get(i).setTrp(Tank.Troops.ENEMY);//设置敌方坦克阵营为：敌方
                }

                //敌方坦克共享阵营信息（获取所有敌方坦克信息，用于碰撞检测）
                for (int i = 0; i < enemyTankSize; i++) {
                    enemyTanks.get(i).enemyTanksInfor = enemyTanks;
                }
                break;

            //继续上局游戏
            case 2:
                //检测是否存在上一局，或上一局是否已经结束
                //若存在，则继续游戏
                if (recorder.readPreGameData()) {
                    mt = recorder.getMyTank();
                    enemyTanks = recorder.getEnemyTanks();

                    //为上一局结束时还在飞行被保存下来的子弹启动独立线程，用于保持子弹继续飞行
                    for (EnemyTank enemyTank : enemyTanks) {
                        new Thread(enemyTank).start(); //为每一个敌方坦克启动一个单独的线程
                        for (Bullet bullet : enemyTank.bullets) {
                            new Thread(bullet).start();
                        }

                    }


                    //敌方坦克共享阵营信息（获取所有敌方坦克信息，用于碰撞检测）
                    for (int i = 0; i < enemyTanks.size(); i++) {
                        enemyTanks.get(i).enemyTanksInfor = enemyTanks;
                    }
                    break;
                }
                //若不存在，则重新选择
                else {
                    System.out.println("没有游戏存档，请选择开始新游戏");
                    initialize();
                }
        }
    }


    /**
     * 用于操作爆炸效果相关图片数据的方法
     */
    public void boomImage() {
        //加载爆炸图片
        boomImage1 = Toolkit.getDefaultToolkit().getImage(TankPanel.class.getClassLoader().getResource("com/hspedu/imagedata/1.png"));
        boomImage2 = Toolkit.getDefaultToolkit().getImage(TankPanel.class.getClassLoader().getResource("com/hspedu/imagedata/2.png"));
        boomImage3 = Toolkit.getDefaultToolkit().getImage(TankPanel.class.getClassLoader().getResource("com/hspedu/imagedata/3.png"));
        //有bug第一次不爆炸，加入提前量
        booms.add(new Boom(0, 0));
    }


    /**
     * 显示目前游戏进度信息
     *
     * @param g 画笔工具
     */
    public void showInfor(Graphics g) {
        //设置信息颜色：黑色
        g.setColor(Color.black);
        //设置字体：宋体，加粗，25号大小
        g.setFont(new Font("宋体", Font.BOLD, 25));
        //显示标题信息
        g.drawString("您已摧毁敌方坦克", 1020, 30);

        //显示已经击败坦克数
        g.drawString("×", 1070, 100);
        g.drawString(recorder.getDefeatEnemyTankNum() + "", 1100, 100);
        //绘制坦克图标
        drawTank(1020, 60, g, Tank.Direction.UP, Tank.Troops.ENEMY);
    }


    /**
     * 绘制方法，用于绘制游戏内容
     *
     * @param g 绘制工具
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //绘制游戏区域，坦克大战背景，黑色
        //区域大小长1000，宽750
        g.fillRect(0, 0, 1000, 750);

        //显示游戏进度信息
        showInfor(g);


        //绘制我方坦克——封装成方法
        if (mt.isLive) {
            drawTank(mt.getX(), mt.getY(), g, mt.getDic(), mt.getTrp());
        }

        //绘制敌方坦克
        for (EnemyTank enemyTank : enemyTanks) {
            drawTank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDic(), enemyTank.getTrp());
        }

        //绘制坦克子弹
        drawBullet(g);

        //绘制爆炸效果
        drawBoom(g);
    }


    /**
     * 绘制坦克方法，用于绘制静态的坦克，包含不同的阵营，以及旋转后朝向
     *
     * @param x         坦克坐标（定位于左上角）
     * @param y         坦克坐标
     * @param g         绘制工具
     * @param direction 朝向
     * @param troop     阵营
     */
    public void drawTank(int x, int y, Graphics g, Tank.Direction direction, Tank.Troops troop) {

        //根据坦克阵营的设置不同颜色
        switch (troop) {
            case WE:
                g.setColor(Color.cyan);
                break;
            case ENEMY:
                g.setColor(Color.yellow);
                break;
        }

        //绘制旋转后的坦克
        switch (direction) {
            case UP:
                g.fill3DRect(x, y, 10, 60, false);//坦克左边履带
                g.fill3DRect(x + 30, y, 10, 60, false);//坦克右边履带
                g.fill3DRect(x + 10, y + 10, 20, 40, false);//坦克身体
                g.fillOval(x + 10, y + 20, 20, 20);//坦克盖子（圆形）
                g.drawLine(x + 20, y + 30, x + 20, y);
                break;
            case RIGHT:
                g.fill3DRect(x - 10, y + 10, 60, 10, false);
                g.fill3DRect(x, y + 20, 40, 20, false);
                g.fill3DRect(x - 10, y + 40, 60, 10, false);
                g.fillOval(x + 10, y + 20, 20, 20);//坦克盖子（圆形）
                g.drawLine(x + 20, y + 30, x + 50, y + 30);
                break;
            case DOWN:
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y + 60);
                break;
            case LEFT:
                g.fill3DRect(x - 10, y + 10, 60, 10, false);
                g.fill3DRect(x, y + 20, 40, 20, false);
                g.fill3DRect(x - 10, y + 40, 60, 10, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x - 10, y + 30);
                break;
        }
    }


    /**
     * 绘制子弹方法
     *
     * @param g 绘制工具
     */
    public void drawBullet(Graphics g) {
        //判断我方子弹集合是否为空，如是则不绘制
        if (!(mt.bullets.isEmpty())) {
            for (Bullet bullet : mt.bullets) {
                //判断子弹生命状态
                if (bullet.isLive) {
                    g.setColor(Color.cyan);
                    g.fillOval(bullet.x, bullet.y, 10, 10);
                }
            }
        }

        //判断敌方子弹集合是否为空，如是则不绘制
        for (EnemyTank enemyTank : enemyTanks) {
            if (!(enemyTank.bullets.isEmpty())) {
                for (Bullet bullet : enemyTank.bullets) {
                    //判断子弹生命状态
                    if (bullet.isLive) {
                        g.setColor(Color.yellow);
                        g.fillOval(bullet.x, bullet.y, 10, 10);
                    }
                }
            }
        }

        //清理已死亡子弹
        mt.bullets.removeIf(bullet -> !bullet.isLive);
        for (EnemyTank enemyTank : enemyTanks) {
            enemyTank.bullets.removeIf(bullet -> !bullet.isLive);
        }
    }


    /**
     * 子弹碰撞判断方法
     */
    public void judgeBang() {
        //判断敌方坦克是否存在
        if (!enemyTanks.isEmpty()) {
            //获取敌方每一辆坦克
            for (int i = 0; i < enemyTanks.size(); i++) {
                EnemyTank enemyTank = enemyTanks.get(i);
                //被击中：敌方阵营
                //判断我方是否发射子弹
                if (!mt.bullets.isEmpty()) {
                    for (Bullet bullet : mt.bullets)
                        //子弹进入敌方坦克碰撞体积范围内
                        if (bullet.x > enemyTank.volume.left && bullet.x < enemyTank.volume.right && bullet.y > enemyTank.volume.top && bullet.y < enemyTank.volume.down) {
                            //我方子弹生命周期结束
                            bullet.isLive = false;
                            System.out.println("打中了！！！！！！！！！！！！");
                            //生成爆炸类，存放于爆炸集合，便于绘制方法绘制爆炸效果
                            booms.add(new Boom(enemyTank.getX(), enemyTank.getY()));
//                            System.out.println("生成爆炸类");
                            //被击中坦克死亡，更新生命状态，移出集合，遍历下一辆坦克
                            enemyTank.isLive = false;
                            enemyTanks.remove(enemyTank);
                            //游戏信息：击败坦克数加一
                            recorder.addDefeatEnemyTankNum();
                            break;
                        }
                    //被击中：我方阵营
                    //判断敌方坦克是否发射子弹
                } else if (!enemyTank.bullets.isEmpty()) {
                    //遍历地方所有存在子弹
                    for (Bullet bullet : enemyTank.bullets) {
                        //如果敌方子弹进入我方碰撞范围
                        if (bullet.x > mt.volume.left && bullet.x < mt.volume.right
                                && bullet.y > mt.volume.top && bullet.y < mt.volume.down) {
                            //敌方子弹生命周期结束
                            bullet.isLive = false;
                            //提示我方坦克被打败
                            System.out.println("你被打败了！！！！！");
                            //生成爆炸效果
                            booms.add(new Boom(mt.getX(), mt.getY()));
                            //更新我方坦克状态：死亡
                            mt.isLive = false;
                            break;
                        }
                    }
                }
            }
        }

    }


    /**
     * 绘制爆炸方法
     */
    public void drawBoom(Graphics g) {
        if (booms.size() == 0) {
            return;
        }
        //从爆炸集合拿出所有需要绘制的爆炸效果
        for (int i = 0; i < booms.size(); i++) {
            Boom boom = booms.get(i);
            //如果爆炸效果已经绘制过，则删除此任务
            if (!boom.isLive) {
                booms.remove(boom);
                return;
            }
            //绘制爆炸效果
            for (int j = 0; j < 6; j++) {
                if (boom.live > 3) {
                    g.drawImage(boomImage1, boom.x, boom.y, 60, 60, this);
                } else if (boom.live > 0) {
                    g.drawImage(boomImage2, boom.x, boom.y, 60, 60, this);
                } else {
                    g.drawImage(boomImage3, boom.x, boom.y, 60, 60, this);
                }
                boom.reLive();
            }
        }
    }


    /**
     * 键盘监听方法，用于监听键盘按下事件
     * 通过W/S/A/D 触发坦克旋转和移动
     *
     * @param e 键盘事件
     */
    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("抓到键盘操作:" + (char) e.getKeyCode());

        //移动坦克，超出游戏范围不能进行移动
        if (e.getKeyCode() == KeyEvent.VK_W && mt.getY() - mt.getSpeed() >= 0) {
            mt.moveUP();
        } else if (e.getKeyCode() == KeyEvent.VK_D && mt.getX() + 50 + mt.getSpeed() <= 1000) {
            mt.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_S && mt.getY() + 60 + mt.getSpeed() <= 750) {
            mt.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_A && mt.getX() - 10 - mt.getSpeed() >= 0) {
            mt.moveLeft();
        }

        //发射子弹
        if (e.getKeyCode() == KeyEvent.VK_J) {
            mt.shot();
        }

        //刷新游戏面板
        this.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


    /**
     * 自动重绘面板线程
     */
    @Override
    public void run() {
        //间隔100毫秒重绘一次面板
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //刷新面板
            this.repaint();


            //检测子弹是否碰撞到敌方坦克
            judgeBang();
        }
    }
}
