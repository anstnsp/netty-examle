import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceTest4 {
    public static void main(String[] args) {
        ParallelExecugtorService service = new ParallelExecugtorService();
        service.submit("job1");
        service.submit("job2");
        service.submit("job3");
        service.submit("job4");

        for (int i=0; i<4; i++) {
            String result = service.take();
            System.out.println(result);
        }

        System.out.println("end");
        service.close();

    }


    private static class ParallelExecugtorService {
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        public ParallelExecugtorService(){}

        public void submit(String job) {
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("finished " + job);
                String result = job + " , " + threadName;
                try {
                    queue.put(result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        public String take() {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }

        public void close() {
            List<Runnable> unfinishedTasks = executor.shutdownNow();
            if (!unfinishedTasks.isEmpty()) {
                System.out.println("Not all tasks finisied before calling close : " + unfinishedTasks);
            }
        }
    }
}
