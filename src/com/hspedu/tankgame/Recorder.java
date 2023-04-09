package com.hspedu.tankgame;

import java.io.*;
import java.util.Vector;


/**
 * 记录器，用于记录游戏相关信息、和文件交互
 */
public class Recorder {
    //定义击败坦克数
    private int defeatEnemyTankNum = 0;

    //定义敌方坦克集合，用于保存游戏结束时的敌方坦克数据
    private Vector<EnemyTank> enemyTanks = null;
    //定义我方坦克，用于保存游戏数据
    private MyTank myTank = null;

    //定义IO对象流，用于将游戏数据序列化写入文件
    private ObjectOutputStream oos = null;
    //定义IO对象流，用于反序列化保存的游戏数据
    private ObjectInputStream ois = null;

    //定位保存游戏数据文件
    private String recordFile = "./src/game_data.dat";


    //唯一记录器实例，可通过get方法获取
    private static Recorder recorder = new Recorder();


    /**
     * 记录器获取方法，用于获取唯一记录器
     *
     * @return {@link Recorder}
     */
    public static Recorder getRecorder() {
        return recorder;
    }


    /**
     * get方法，用于获取击败坦克数
     *
     * @return int 返回击败坦克数
     */
    public int getDefeatEnemyTankNum() {
        return defeatEnemyTankNum;
    }


    /**
     * 增加击败坦克数方法
     */
    public void addDefeatEnemyTankNum() {
        defeatEnemyTankNum++;
    }


    /**
     * 保存数据方法
     *
     * @return boolean 保存成功返回true，失败返回false
     */
    public boolean saveRecord() {
        try {
            try {
                //当击败数等于地方所有坦克数时，不保存游戏数据，返回false
                if (defeatEnemyTankNum == 3) {
                    System.out.println("游戏结束，不保存");
                    return false;
                }

                oos = new ObjectOutputStream(new FileOutputStream(recordFile));

                //将结束时敌方所有存活坦克信息写入文件
                oos.writeObject(enemyTanks);
                //将结束时我方坦克信息写入文件
                oos.writeObject(myTank);
                //将击败坦克数写入文件
                oos.writeInt(defeatEnemyTankNum);
                System.out.println("文件已保存");
            } finally {
                //关闭流资源
                if (oos != null) {
                    oos.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return true;
    }


    /**
     * 更新敌方坦克集合类方法
     */
    public void addEnemyTanks(Vector<EnemyTank> enemyTanks) {
        this.enemyTanks = enemyTanks;
    }


    /**
     * 保存我方坦克数据方法
     *
     * @param myTank 我方坦克
     */
    public void addMyTank(MyTank myTank) {
        this.myTank = myTank;
    }


    /**
     * 获取上一局保存的敌方坦克游戏信息
     *
     * @return {@link Vector}<{@link EnemyTank}> 敌方坦克集合
     */
    public Vector<EnemyTank> getEnemyTanks() {
        return enemyTanks;
    }


    /**
     * 获取上一局保存的我方坦克游戏信息
     *
     * @return {@link MyTank}
     */
    public MyTank getMyTank() {
        return myTank;
    }


    /**
     * 读取上局保存的游戏数据方法
     *
     * @return boolean 读取到上局数据返回true，上局打完了或者未有数据返回false
     */
    public boolean readPreGameData() {
        try {
            try {
                //判断游戏数据文件是否为空，为空返回false
                File file = new File(recordFile);
                if (!file.exists()) {
                    System.out.println("未找到游戏数据");
                    return false;
                } else if (file.length() == 0) {
                    System.out.println("未保存上局游戏数据");
                    return false;
                }

                //判断上局是否打完，打完返回false
                if (defeatEnemyTankNum == 3) {
                    System.out.println("上局游戏已结束");
                    return false;
                }

                ois = new ObjectInputStream(new FileInputStream(recordFile));

                System.out.println("11111111111");
                //读取上局保存的坦克数据
                enemyTanks = (Vector<EnemyTank>) (ois.readObject());
                myTank = (MyTank) ois.readObject();
                defeatEnemyTankNum = ois.readInt();

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("未找到游戏存储文件");
            } finally {
                if (ois != null) {
                    ois.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return true;
    }
}
