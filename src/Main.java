import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int length = 80;
        int time = 100000;
        byte[][] mapInfo = new byte[length][length];
        // 障碍物
        mapInfo[1][1] = 1;

        long begin = System.currentTimeMillis();
        System.out.println("begin:" + begin);
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < time; i++) {
            nodes = AStarService.searchWay(1, 1, length - 1, length - 1, mapInfo);
        }
        long end = System.currentTimeMillis();
        System.out.println("end:" + end);
        System.out.println(String.format("%d*%d地图，%d次耗时：%d ms", length, length, time, end - begin));

        // 路径打印
        byte[][] draw = new byte[length][length];
        for (Node node : nodes) {
            draw[node.coord.y][node.coord.x] = 1;
        }
        for (byte[] bytes : draw) {
            for (byte b : bytes) {
                if (b == 1) {
                    System.out.print("\033[31;4m〇\033[0m");
                } else {
                    System.out.print("〇");
                }
            }
            System.out.println();
        }


    }


}
