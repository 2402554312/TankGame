import com.hspedu.tankgame.EnemyTank;
import com.hspedu.tankgame.MyTank;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream ob = new ObjectInputStream(new FileInputStream("./src/game_data.dat"));
        Vector<EnemyTank> enemyTanks = (Vector) ob.readObject();
        for (EnemyTank enemyTank :enemyTanks) {
            System.out.println(enemyTank);
        }
        MyTank mt = (MyTank) ob.readObject();
        System.out.println(mt);

        System.out.println(ob.readInt());

    }
}