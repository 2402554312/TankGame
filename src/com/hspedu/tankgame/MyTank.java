package com.hspedu.tankgame;


import java.io.Serializable;

/**
 * 玩家坦克类
 */
public class MyTank extends Tank implements Serializable {
    boolean isLive = true;

    /**
     * 构造方法，初始化坦克位置
     *
     * @param x 坦克X坐标
     * @param y 坦克Y坐标
     */
    public MyTank(int x, int y) {
        super(x, y);
        volume = new Volume();
    }

}
