package com.hspedu.tankgame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 游戏容器（窗口），用于容纳并控制坦克大战游戏组件
 */
public class TankGame extends JFrame{
    TankPanel tp = null; //声明坦克游戏绘制组件

    //获取游戏信息记录器
    Recorder recorder = Recorder.getRecorder();

    public static void main(String[] args) {
        TankGame game = new TankGame(); //加载容器及容器内组件
    }

    /**
     * 构造方法，用于控制容器内组件状态
     */
    public TankGame() {
        tp = new TankPanel(); //加载坦克组件

        new Thread(tp).start(); //开启自动重绘线程，用于定时重绘面板

        this.add(tp); //加载坦克组件到容器
        this.setSize(1300, 750);//设置容器大小：1300*750
        this.setVisible(true);//设置容器可见
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置关闭窗口后结束程序
        this.addKeyListener(tp);//将键盘监听器加入容器

        //设置关闭游戏保存数据功能
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //当检测到关闭窗口事件时，调用保存记录方法
                recorder.saveRecord();
            }
        });
    }
}


