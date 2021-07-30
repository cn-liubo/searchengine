import com.liu.util.ThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ReadFile {

    private static List<String> list = new CopyOnWriteArrayList<>();
    private static final String file = "D:\\raw_data1.txt";

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = ThreadPool.createThreadPool();
//        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 2; i++) {
            pool.execute(new MyRunnable(14));
        }

        pool.shutdown();
        Thread.sleep(1000);
//        read(new File(file));
        System.out.println(list);
        System.out.println(list.size());
    }

    public static void read(File file) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line);
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
    }

    public static class MyRunnable implements Runnable {

        private int line;

        public MyRunnable(int line) {
            this.line = line;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(new File(file));
                br = new BufferedReader(fr);
                String line = null;
                int count = 0;
                while (((line = br.readLine()) != null) && count != this.line) {
                    list.add(line);
                    count++;
//                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("end");
        }
    }
}
