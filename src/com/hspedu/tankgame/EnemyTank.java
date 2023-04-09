package com.hspedu.tankgame;


import java.io.Serializable;
import java.util.Vector;

/**
 * 敌人坦克类
 */
public class EnemyTank extends Tank implements Runnable, Serializable {
    boolean isLive = true; //敌方坦克生命状态

    //获取其他坦克信息，用于碰撞检测
    Vector<EnemyTank> enemyTanksInfor = null;


    /**
     * 无参构造器，用于反序列化EnemyTank类
     */
    public EnemyTank(){}


    /**
     * 构造方法，初始化敌方坦克坐标
     *
     * @param x x
     * @param y y
     */
    public EnemyTank(int x, int y) {
        super(x, y);
        volume = new Volume();
    }


    /**
     * 判断坦克是否重叠
     *
     * @return boolean 重叠返回false，未重叠返回true
     */
    public boolean judgeOverlap() {
        //遍历所有其他敌方坦克
        for (int i = 0; i < enemyTanksInfor.size(); i++) {
            //跳过自己与自己的判断
            if (enemyTanksInfor.get(i) == this) {
                continue;
            }

            //获取判断的对方坦克
            EnemyTank enemyTank = enemyTanksInfor.get(i);

            //重叠判断
            switch (getDic()) {
                //朝上移动
                case UP:
                    //上边界重叠
                    if ((this.volume.top - getSpeed() * 10 <= enemyTank.volume.down && this.volume.top - getSpeed() * 10 >= enemyTank.volume.top)
                            //左边界重叠
                            && ((this.volume.left >= enemyTank.volume.left && this.volume.left <= enemyTank.volume.right)
                            //或者右边界重叠
                            || (this.volume.right <= enemyTank.volume.right && this.volume.right >= enemyTank.volume.left))) {
                        //若检测到要发生重叠，随机调转坦克方向
                        randomDic();
                        return false;
                    }
                    //朝右移动
                case RIGHT:
                    //右边界重叠
                    if ((this.volume.right + getSpeed() * 10 >= enemyTank.volume.left && this.volume.right + getSpeed() * 10 <= enemyTank.volume.right)
                            //上边界重叠
                            && ((this.volume.top >= enemyTank.volume.top && this.volume.top <= enemyTank.volume.down)
                            //或者下边界重叠
                            || (this.volume.down <= enemyTank.volume.down && this.volume.down >= enemyTank.volume.top))) {
                        randomDic();
                        return false;
                    }
                    //朝左移动
                case LEFT:
                    //左边界重叠
                    if ((this.volume.left - getSpeed() * 10 >= enemyTank.volume.left && this.volume.left - getSpeed() * 10 <= enemyTank.volume.right)
                            //上边界重叠
                            && ((this.volume.top >= enemyTank.volume.top && this.volume.top <= enemyTank.volume.down)
                            //或者下边界重叠
                            || (this.volume.down >= enemyTank.volume.top && this.volume.down <= enemyTank.volume.down))) {
                        randomDic();
                        return false;
                    }
                    //朝下移动
                case DOWN:
                    //下边界重叠
                    if ((this.volume.down + getSpeed() * 10 >= enemyTank.volume.top && this.volume.down + getSpeed() * 10 <= enemyTank.volume.down)
                            //左边界重叠
                            && ((this.volume.left >= enemyTank.volume.left && this.volume.left <= enemyTank.volume.right)
                            //或者有边界重叠
                            || this.volume.right <= enemyTank.volume.right && this.volume.right >= enemyTank.volume.left)) {
                        randomDic();
                        return false;
                    }
            }
        }
        return true;
    }


    /**
     * 通过随机数控制敌方坦克随机移动方向
     */
    public void randomDic() {
        int direct = (int) (Math.random() * 4);
        //上
        if (direct == 0) {
            setDic(Direction.UP);
        }
        //右
        else if (direct == 1) {
            setDic(Direction.RIGHT);
        }
        //下
        else if (direct == 2) {
            setDic(Direction.DOWN);
        }
        //左
        else if (direct == 3) {
            setDic(Direction.LEFT);
        }
    }


    /**
     * 每一个敌方坦克都有一个单独的线程，用于自动移动和发射子弹
     */
    @Override
    public void run() {
        while (true) {
            //敌方坦克随机移动
            randomDic();
            try {
                for (int i = 0; i < 10; i++) {
                    switch (getDic()) {
                        case UP:
                            if (judgeOverlap()) {
                                if (getY() - getSpeed() >= 0) {
                                    moveUP();
                                    Thread.sleep(100);
                                }
                            }
                            break;
                        case RIGHT:
                            if (judgeOverlap()) {
                                if (getX() + 50 + getSpeed() <= 1000) {
                                    moveRight();
                                    Thread.sleep(100);
                                }
                            }
                            break;
                        case DOWN:
                            if (judgeOverlap()) {
                                if (getY() + 60 + getSpeed() <= 750) {
                                    moveDown();
                                    Thread.sleep(100);
                                }
                            }
                            break;
                        case LEFT:
                            if (judgeOverlap()) {
                                if (getX() - 10 - getSpeed() >= 0) {
                                    moveLeft();
                                    Thread.sleep(100);
                                }
                            }
                            break;
                    }
                }
            } catch (InterruptedException ignored) {
                System.out.println("异常");
            }


            //随机数控制开火，随机数小于0.1开火
            double time = Math.random();
            if (time < 0.3) {
                shot();
            }


            //若坦克已死亡，则结束线程
            if (!isLive) {
                return;
            }


            //线程休眠500ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
