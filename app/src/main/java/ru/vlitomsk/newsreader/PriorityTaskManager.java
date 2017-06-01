package ru.vlitomsk.newsreader;

import java.util.*;

public class PriorityTaskManager {
    private boolean running = false;
    private static class PQueueTask {
        private Runnable runnable;
        private int priority;

        public PQueueTask(Runnable runnable, int priority) {
            this.runnable = runnable;
            this.priority = priority;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public int getPriority() {
            return priority;
        }
    }
    private PriorityQueue<PQueueTask> taskQueue = new PriorityQueue<>(new PQueueComparator());

    private static class PQueueComparator implements Comparator<PQueueTask> {
        @Override
        public int compare(PQueueTask l, PQueueTask r) {
            if (l.getPriority() < r.getPriority()) {
                return 1;
            }
            if (l.getPriority() > r.getPriority()) {
                return -1;
            }
            return 0;
        }
    }

    public static int HI_PRIO = Integer.MAX_VALUE;
    private final long lowPriorityTasksMillisTimeout;

    private class TaskProcessor implements Runnable {
        private ArrayList<Thread> threadsToJoin = new ArrayList<>();
        private ArrayList<Thread> diedThreads = new ArrayList<>();
        private void joinThreads(long millis) {
            diedThreads.clear();
            for (Thread thread : threadsToJoin) {
                try {
                    thread.join(millis + (int)(Math.random() * (millis / 2)));
                    if (!thread.isAlive()) {
                        synchronized (diedThreads) {
                            diedThreads.add(thread);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            threadsToJoin.removeAll(diedThreads);
        }

        @Override
        public void run() {
            while (running) {
                synchronized (taskQueue) {
                    try {
                        taskQueue.wait(lowPriorityTasksMillisTimeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (taskQueue.isEmpty()) {
                        continue;
                    }
                    PQueueTask task = taskQueue.remove();
                    Thread thread = new Thread(task.getRunnable());
                    threadsToJoin.add(thread);
                    thread.start();
                }

                final int JOIN_WAIT_MILLIS = 10;
                joinThreads(JOIN_WAIT_MILLIS);
            }
            joinThreads(0);
        }
    }

    private Thread taskProcessorThread;

    public synchronized void start() {
        running = true;
        taskProcessorThread = new Thread(new TaskProcessor());
        taskProcessorThread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            taskProcessorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void schedule(int priority, Runnable task) {
        synchronized (taskQueue) {
            taskQueue.add(new PQueueTask(task, priority));
            if (priority == HI_PRIO) {
                taskQueue.notifyAll();
            }
        }
    }

    public synchronized void clear() {
        synchronized (taskQueue) {
            taskQueue.clear();
        }
    }

    public PriorityTaskManager() {
        this.lowPriorityTasksMillisTimeout = 1000;
    }

    public PriorityTaskManager(long lowPriorityTasksMillisTimeout) {
        if (lowPriorityTasksMillisTimeout == 0) {
            throw new RuntimeException("lowPriorityTasksMillisTimeout == 0");
        }
        this.lowPriorityTasksMillisTimeout = lowPriorityTasksMillisTimeout;
    }

    public static final PriorityTaskManager DOWNLOAD_MANAGER;
    static {
        DOWNLOAD_MANAGER = new PriorityTaskManager();
        DOWNLOAD_MANAGER.start();
    }
}
