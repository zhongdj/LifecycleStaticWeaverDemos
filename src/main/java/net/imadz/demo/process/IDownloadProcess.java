package net.imadz.demo.process;


import net.imadz.lifecycle.StateConverter;
import net.imadz.lifecycle.annotations.Event;
import net.imadz.lifecycle.annotations.LifecycleMeta;
import net.imadz.lifecycle.annotations.StateIndicator;
import net.imadz.lifecycle.annotations.state.Converter;

import java.io.Serializable;


@LifecycleMeta(DownloadProcessLifecycleDescription.class)
public interface IDownloadProcess extends Serializable {
    
    /**
     * Rebuild lost states from incorrect persisted state and Enqueue
     */
    @Event
    void activateInactiveQueued();

    @Event
    void activateInactiveStarted();

    /**
     * Expected Precondition: No resource enlisted Any enlisted resource should
     * be delisted
     */
    @Event(DownloadProcessLifecycleDescription.Events.Deactive.class)
    void inactivate();

    /**
     * Initialize states and Enqueue
     */
    @Event
    void prepare();

    /**
     * Living thread allocated
     */
    @Event
    void start();

    /**
     * Rebuild states from correct persisted or in-memory state and Enqueue
     */
    @Event
    void resume();

    /**
     * Deallocate Thread resource, Persist correct states
     */
    @Event
    void pause();

    /**
     * Thread die naturally, persist correct states and recycle all resources
     * enlisted.
     */
    @Event
    void finish();

    /**
     * Process aborted unexpected, persist current states and recycle all
     * resources enlisted
     */
    @Event
    void err();

    /**
     * While processing, update working progress.
     * 
     * @param bytes
     *            received
     */
    @Event
    void receive(long bytes);

    /**
     * Roll back all information change after create, Re-initialize states and
     * Enqueue
     */
    @Event
    void restart();

    /**
     * Make sure enlisted resource has been delisted if there is, such as
     * thread, connection, memory, and persisted information and files.
     * 
     * @param both
     *            downloaded file and the download request/task
     */
    @Event
    void remove(boolean both);

    @StateIndicator
    @Converter(DownloadProcessStateConverter.class)
    StateEnum getState();

    int getId();

    String getUrl();

    String getReferenceUrl();

    String getLocalFileName();

    long getContentLength();


    public static enum StateEnum  {
        /**
         * Preconditions:
         * <p>
         * 1. Request Validation Passed
         * <p>
         * 1.1 URL Format legal
         * <p>
         * 1.2 File Path legal
         * <p>
         * 1.2.1 File does not exist
         * <p>
         * 1.2.2 File can be created (write) under it's directory
         * <p>
         * 1.2.2.1 File's directory (exists and can be written OR does not exist but can be created under it's parent directory)
         * <p>
         * Postconditions:
         * <p>
         * 1. Download Task Meta-data file is created.
         * <p>
         * 2. URL, Folder, filename, thread number, state = "New" are stored in the Meta-data file.
         */
        New,

        /**
         * Preconditions:
         * <p>
         * 1. Download Task Meta-data file exists.
         * <p>
         * 2. Meta-information in the meta-data file are properly set.
         * <p>
         * 2.1. URL format is legal.
         * <p>
         * 2.2.Folder and filename is legal, as defined in New.Postconditions.
         * <p>
         * 2.3.Thread Number is greater than 0 and less than 20
         * <p>
         * 2.4.State is set legally (New, Inactive)
         * <p>
         * Postconditions:
         * <p>
         * 1. Following information should be reset and set within the meta-data file.
         * <p>
         * 1.1. Total length is set
         * <p>
         * 1.2. resumable is set
         * <p>
         * 1.3. segments number is set
         * <p>
         * 1.4. state is set to "Prepared".
         * <p>
         * 2. Download Task data file is created
         * or re-created with deleting the pre-existing file (application aborted before update status to Prepared).
         */
        Queued,

        /**
         * Preconditions:
         * <p>
         * 1. All necessary meta information had been set, as is mentioned above, such as: total length, resumable flag and etc.
         * <p>
         * 2. Target data file is created.
         * <p>
         * Postconditions:
         * <p>
         * 1. Resources required by down load had been allocated to this downloadProcess.
         * <p>
         * 1.1. Download Worker Threads (stands for IO/CPU/MEM/NETWORK)
         * <p>
         * 2.Download Task State is set to Started.
         */
        Started,
        InactiveQueued,
        InactiveStarted,
        Paused,
        Failed,
        Finished,
        Removed;
    }

    public static final class DownloadProcessStateConverter implements StateConverter<StateEnum> {

        @Override
        public String toState(StateEnum stateEnum) {
            return stateEnum.name();
        }

        @Override
        public StateEnum fromState(String stateName) {
            return StateEnum.valueOf(stateName);
        }
    }

}
