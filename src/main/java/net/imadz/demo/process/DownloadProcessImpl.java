package net.imadz.demo.process;

import net.imadz.lifecycle.annotations.Event;
import net.imadz.lifecycle.annotations.LifecycleMeta;
import net.imadz.lifecycle.annotations.StateIndicator;
import net.imadz.lifecycle.annotations.state.Converter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@LifecycleMeta(DownloadProcessLifecycleDescription.class)
public class DownloadProcessImpl implements IDownloadProcess {

    private static final class DemoRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (this) {
                        wait(50000L);
                    }
                }
            } catch (InterruptedException ex) {

            }

        }
    }

    private static final class Segment implements Serializable {

        private static final long serialVersionUID = 6637203548006150257L;
        /* package */ long startOffset;
        /* package */ long endOffset;
        /* package */ long wroteBytes;

        /* package */Segment(long startOffset, long endOffset) {
            super();
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public long getStartOffset() {
            return startOffset;
        }

        public long getEndOffset() {
            return endOffset;
        }

        public long getWroteOffset() {
            return wroteBytes;
        }

        public void writtenBytes(long bytes) {
            if (startOffset + wroteBytes == endOffset) {
                throw new IllegalStateException("This segment receiving bytes after been finished");
            }
            if (startOffset + wroteBytes > endOffset) {
                throw new IllegalStateException("Overwrite happened.");
            }
            wroteBytes += bytes;
        }
    }

    public static class DownloadRequest implements Serializable {

        private static final long serialVersionUID = 821976542154139230L;
        /* package */final String url;
        /* package */final String referenceUrl;
        /* package */final String localFileName;

        public DownloadRequest(String url, String referenceUrl, String localFileName) {
            super();
            this.url = url;
            this.referenceUrl = referenceUrl;
            this.localFileName = localFileName;
        }

        public String getUrl() {
            return url;
        }

        public String getReferenceUrl() {
            return referenceUrl;
        }

        public String getLocalFileName() {
            return localFileName;
        }

    }


    private static final long serialVersionUID = -2206411843392592595L;

    private final DownloadRequest request;
    private StateEnum state;
    private int id;
    private long contentLength;
    private final List<Segment> segments = new ArrayList<Segment>();

    private int numberOfThreads = 1;
    private transient ExecutorService threadPool = null;

    public DownloadProcessImpl(DownloadRequest request) {
        this(request, 1);
    }

    public DownloadProcessImpl(DownloadRequest request, int numberOfThreads) {
        super();
        this.request = request;
        this.numberOfThreads = numberOfThreads;
        this.state = StateEnum.New;
    }

    @Override
    @StateIndicator
    @Converter(DownloadProcessStateConverter.class)
    public StateEnum getState() {
        return this.state;
    }

    private void setState(StateEnum state) {
        this.state = state;
    }


    public void activate() {
        initializeThreadPool();
    }

    @Event
    @Override
    public void activateInactiveQueued() {
        activate();
    }

    @Event
    @Override
    public void activateInactiveStarted() {
        activate();
    }

    @Override
    @Event(DownloadProcessLifecycleDescription.Events.Deactive.class)
    public void inactivate() {
        System.out.println("inactivate");
    }

    @Override
    @Event
    public void start() {
        // tasking with segments
        threadPool.submit(new DemoRunnable());
        threadPool.submit(new DemoRunnable());
    }

    @Override
    @Event
    public void resume() {
        prepare();
    }

    @Override
    @Event
    public void pause() {
        stop();
    }

    @Override
    @Event
    public void finish() {
        threadPool.shutdownNow();
    }

    @Override
    @Event
    public void err() {
        stop();
    }

    @Override
    @Event
    public void receive(long bytes) {

    }

    @Override
    @Event
    public void restart() {
        stop();
        prepare();
    }

    @Override
    @Event
    public void remove(boolean both) {
        stop();
        if (both) {
            deleteFile();
        }
    }

    @Override
    @Event
    public void prepare() {

        initializeThreadPool();

        // Create segments
    }

    private void initializeThreadPool() {
        if (null != threadPool) {
            threadPool.shutdownNow();
        }

        threadPool = Executors.newFixedThreadPool(2, new ThreadFactory() {

            private int counter = 1;

            @Override
            public Thread newThread(Runnable runnable) {
                final File target = new File(getLocalFileName());
                StringBuilder sb = new StringBuilder();
                sb.append(target.getName()).append(" Downloading Thread-").append(counter++);
                return new Thread(runnable, sb.toString());
            }
        });
    }

    private void deleteFile() {
        final File downloadedFile = new File(request.localFileName);
        if (downloadedFile.exists()) {
            if (!downloadedFile.delete()) {
                throw new IllegalStateException("Cannot delete file: " + request.localFileName);
            }
        }
    }

    private void stop() {

        if (null != threadPool) {
            if (!threadPool.isShutdown() && !threadPool.isTerminated()) {
                threadPool.shutdownNow();
            }
            threadPool = null;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getUrl() {
        return request.url;
    }

    @Override
    public String getReferenceUrl() {
        return request.referenceUrl;
    }

    @Override
    public String getLocalFileName() {
        return request.localFileName;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

}