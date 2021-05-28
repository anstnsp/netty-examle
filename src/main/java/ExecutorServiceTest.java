import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

    public static void main(String[] args) throws InterruptedException {
        //Executors는 ExecutorService 객체를 생성하며, 다음 메소드를 제공하여 쓰레드 풀을 개수 및 종류를 정할 수 있습니다.
        //newCachedThreadPool(): 필요할 때, 필요한 만큼 쓰레드풀을 생성합니다. 이미 생성된 쓰레드를 재활용할 수 있기 때문에 성능상의 이점이 있을 수 있습니다.
        ExecutorService executor = Executors.newCachedThreadPool();

        Runnable task1 = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("job1 : " + threadName);
        };

        Runnable task2 = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("job2 : " + threadName);
        };

        Runnable task3 = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("job3 : " + threadName);
        };
        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();  //shutdown()은 더 이상 쓰레드풀에 작업을 추가하지 못하도록 합니다. 그리고 처리 중인 Task가 모두 완료되면 쓰레드풀을 종료시킵니다.

        /**
         * awaitTermination()은 이미 수행 중인 Task가 지정된 시간동안 끝나기를 기다립니다.
         * 지정된 시간 내에 끝나지 않으면 false를 리턴하며, 이 때 shutdownNow()를 호출하면 실행 중인 Task를 모두 강제로 종료시킬 수 있습니다.
         */
        if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println(LocalTime.now() + "All jobs are terminated");
        } else {
            System.out.println(LocalTime.now() + "some jobs are not terminated");
            executor.shutdownNow();
        }
        System.out.println("end");
    }
}
