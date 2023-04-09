package com.hspedu.tankgame;


import java.io.Serializable;
import java.util.Vector;

/**
 * 坦克父类
 */
public class Tank implements Serializable {
    //坦克本体坐标
    private int x;
    private int y;

    //坦克速度
    private int speed = 1;

    //坦克朝向方向
    private Direction dic = Direction.UP;

    //坦克所属阵营
    private Troops trp = Troops.WE;


    /**
     * 坦克体积
     */
    class Volume implements Serializable{
        int top; //上边界
        int right; //右边界
        int down; //下边界
        int left; //左边界

        /**
         * 更新坦克碰撞范围
         */
        public void setVolume() {
            //根据坦克朝向设置碰撞范围
            switch (dic) {
                case UP:
                case DOWN:
                    top = y;
                    right = x + 40;
                    down = y + 60;
                    left = x;
                    break;
                case RIGHT:
                case LEFT:
                    top = y;
                    right = x + 60;
                    down = y + 40;
                    left = x;
                    break;
            }
        }


        @Override
        public String toString() {
            return "Volume{" +
                    "top=" + top +
                    ", right=" + right +
                    ", down=" + down +
                    ", left=" + left +
                    '}';
        }
    }
    Volume volume = null;


    /**坦克旋转方向(上下左右)
     */
    enum Direction {UP, DOWN, RIGHT, LEFT}


    /**坦克所属阵营类别，WE(我方)， ENEMY(敌方)
     */
    enum Troops {WE, ENEMY}

    /**存放子弹集合
     */
    Vector<Bullet> bullets = new Vector<>();


    /**
     * 构造方法，初始化坦克位置
     *
     * @param x x
     * @param y y
     */
    protected Tank(int x, int y) {
        this.x = x;
        this.y = y;
        volume = new Volume();
        volume.setVolume();
    }

    protected Tank(){}



    /**
     * 射击方法，坦克发射子弹
     */
    public void shot() {
        switch (getDic()) {
            case UP:
                bullets.add(new Bullet(getX() + 15, getY() - 5, Direction.UP));
                break;
            case RIGHT:
                bullets.add(new Bullet(getX() + 65, getY() + 25, Direction.RIGHT));
                break;
            case DOWN:
                bullets.add(new Bullet(getX() + 15, getY() + 55, Direction.DOWN));
                break;
            case LEFT:
                bullets.add(new Bullet(getX() - 5, getY() + 25, Direction.LEFT));
                break;
        }

        //为每一颗子弹启动射击线程
        new Thread(bullets.lastElement()).start();
    }



    /**
     * 坦克移动方法
     */
    public void moveUP() {
        dic = Direction.UP;
        y -= speed;
        volume.setVolume();
    }

    public void moveRight() {
        dic = Direction.RIGHT;
        x += speed;
        volume.setVolume();
    }

    public void moveDown() {
        dic = Direction.DOWN;
        y += speed;
        volume.setVolume();
    }

    public void moveLeft() {
        dic = Direction.LEFT;
        x -= speed;
        volume.setVolume();
    }




    /**
     * get/set 方法，用于获取和设置坦克数据
     */
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDic() {
        return dic;
    }

    public void setDic(Direction dic) {
        this.dic = dic;
    }

    public Troops getTrp() {
        return trp;
    }

    public void setTrp(Troops trp) {
        this.trp = trp;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Tank{" +
                "x=" + x +
                ", y=" + y +
                ", speed=" + speed +
                ", dic=" + dic +
                ", trp=" + trp +
                ", volume=" + volume +
                '}';
    }
}

