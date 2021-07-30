import org.junit.Test;

import java.util.concurrent.*;

public class TestThread {
    @Test
    public void test1() {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.AbortPolicy());


        for (int i = 0; i < 20; i++) {
            final int j = i;
            //线程池执行任务：execute、submit ----> 提交执行一个任务
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(j);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Test
    public void test2() {

    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.execute(new MyRunnable("第0号线程"));

        threadPoolExecutor.shutdown();
//        for (int i = 0; i < 20; i++) {
//            try {
//                threadPoolExecutor.execute(new MyRunnable("第" + (i + 1) + "号线程"));
//            } catch (Throwable e) {
//                e.printStackTrace();
//                System.out.println("丢弃任务：" + (i + 1));
//            }
//        }
    }
    static class MyRunnable implements Runnable {
        private String name;
        public MyRunnable(String name) {
            this.name = name;
        }
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ":" +name);
//            while (true) {
//                //让线程一直运行
//            }
        }
    }
}
