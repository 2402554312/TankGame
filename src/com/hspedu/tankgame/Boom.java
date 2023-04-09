package com.hspedu.tankgame;

/**
 * 爆炸类，用于实现坦克爆炸效果
 */
public class Boom {
    int x;  //爆炸效果生成坐标
    int y;

    boolean isLive = true; //生命状态
    int live = 6; //爆炸帧率，用于生成连贯的爆炸效果

    public Boom(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 减少生命周期方法，用于连贯绘制炸弹
     */
    public void reLive() {
        if (live > 0) {
            live--;
        }else {
            isLive = false;
        }
    }
}
