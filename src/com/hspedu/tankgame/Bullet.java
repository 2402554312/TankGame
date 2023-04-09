package com.hspedu.tankgame;

import java.io.Serializable;

/**
 * 子弹类
 */
public class Bullet implements Runnable , Serializable {
    int x; //子弹X坐标
    int y; //子弹Y坐标
    Tank.Direction direct; //子弹飞行方向
    int speed = 1; //子弹飞行速度
    boolean isLive = true; //子弹是否毁灭

    public Bullet(int x, int y, Tank.Direction direct) {
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    /**
     * 射击线程，每一颗子弹有一个独立的线程
     */
    @Override
    public void run() {
        //不断更新子弹坐标（飞行）
        while (true) {
            //根据坦克朝向朝不同方向发射子弹
            switch (direct) {
                case UP:
                    y -= speed;
                    break;
                case DOWN:
                    y += speed;
                    break;
                case RIGHT:
                    x += speed;
                    break;
                case LEFT:
                    x -= speed;
                    break;
            }

            //休眠子弹线程，稳定坐标更新速度
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //判断子弹是否越界，如果是则结束线程
            if (!(x >= 0 && x <= 1000 && y >= 0 && y <= 750)) {
                System.out.println("子弹出界，生命周期结束");
                isLive = false; //修改生命状态
                break;
            }

            //判断子弹是否存活（子弹击中目标生命周期结束）
            if (!isLive){
                break;
            }
        }
    }
}
