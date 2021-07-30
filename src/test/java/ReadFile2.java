import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReadFile2 {
//    private static List<String> list = new ArrayList<>();
    private static List<String> list = new CopyOnWriteArrayList<>();
    private static final String file = "D:\\test.txt";
    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始");
        Thread thread = new Thread(() -> {
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String line = null;
                while ((line = br.readLine()) != null) {
//                    list.add(line);
                    System.out.println(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        System.out.println("结束");
        System.out.println(list);
    }
}
