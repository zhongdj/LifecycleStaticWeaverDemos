package net.imadz.demo.process;

import net.imadz.lifecycle.AbsStateMachineRegistry;
import net.imadz.demo.process.IDownloadProcess.StateEnum;
import net.imadz.lifecycle.meta.object.EventObject;
import net.imadz.lifecycle.meta.object.StateMachineObject;
import net.imadz.lifecycle.meta.type.EventMetadata;
import net.imadz.lifecycle.meta.type.StateMachineMetadata;
import net.imadz.lifecycle.meta.type.StateMetadata;
import net.imadz.verification.VerificationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Tracy on 06/01/2017.
 */
public class RecoverMaster {

    public static final Logger LOGGER = Logger.getLogger(RecoverMaster.class.getName());

    final StateMachineMetadata machineMetaData = AbsStateMachineRegistry.getInstance().loadStateMachineMetadata(DownloadProcessLifecycleDescription.class);
    final StateMachineObject machineObject = AbsStateMachineRegistry.getInstance().loadStateMachineObject(DownloadProcessImpl.class);

    public RecoverMaster() throws VerificationException {
    }


    public void corrupt() {
        final DownloadProcessRecoverableIterator iterator = new DownloadProcessRecoverableIterator(machineMetaData);
        IDownloadProcess downloadProcess = null;
        final ArrayList<IDownloadProcess> allDownloadProcess = new ArrayList<>();
        while (iterator.hasNext()) {
            downloadProcess = iterator.next();
            allDownloadProcess.add(downloadProcess);
            StateEnum state = downloadProcess.getState();
            LOGGER.info("Corrupting download process " + downloadProcess + " from " + state);
            StateMetadata StateMetadata = machineMetaData.getState(state.name());
            EventMetadata corruptEventMetadata = StateMetadata.getCorruptEvent();
            if (null != corruptEventMetadata) {
                EventObject corruptEventObject = machineObject.getEvent(corruptEventMetadata.getPrimaryKey());
                final Method corruptMethod = corruptEventObject.getEventMethod();
                try {
                    corruptMethod.invoke(downloadProcess);
                    state = downloadProcess.getState();
                    LOGGER.info("Corrupted download process " + downloadProcess + " to " + state);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Corrupt Process Failed");
                }
            }
        }
        StoreHelper.save(allDownloadProcess);
    }

    public void recoverAllCorruptedProcesses() {
        final DownloadProcessRecoverableIterator iterator = new DownloadProcessRecoverableIterator(machineMetaData);
        IDownloadProcess downloadProcess = null;
        final ArrayList<IDownloadProcess> allDownloadProcess = new ArrayList<>();
        while (iterator.hasNext()) {
            downloadProcess = iterator.next();
            allDownloadProcess.add(downloadProcess);
            StateEnum state = downloadProcess.getState();
            LOGGER.info("Recovering download process " + downloadProcess + " from " + state);
            StateMetadata StateMetadata = machineMetaData.getState(state.name());
            EventMetadata recoverEvent = StateMetadata.getRecoverEvent();
            if (null != recoverEvent) {
                EventObject recoverEventObject = machineObject.getEvent(recoverEvent.getPrimaryKey());
                final Method recoverMethod = recoverEventObject.getEventMethod();
                try {
                    recoverMethod.invoke(downloadProcess);
                    state = downloadProcess.getState();
                    LOGGER.info("Recovered download process " + downloadProcess + " to " + state);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Recover Process Failed");
                }
            }
        }
        StoreHelper.save(allDownloadProcess);
    }



    public void redoAllCorruptedProcesses() {
        final DownloadProcessRecoverableIterator iterator = new DownloadProcessRecoverableIterator(machineMetaData);
        IDownloadProcess downloadProcess = null;
        final ArrayList<IDownloadProcess> allDownloadProcess = new ArrayList<>();
        while (iterator.hasNext()) {
            downloadProcess = iterator.next();
            allDownloadProcess.add(downloadProcess);
            StateEnum state = downloadProcess.getState();
            final StateMetadata stateMetadata = machineMetaData.getState(state.name());
            final EventMetadata theEvent = stateMetadata.getRedoEvent();
            if (null != theEvent) {
                EventObject theEventObject = machineObject.getEvent(theEvent.getPrimaryKey());
                final Method theMethod = theEventObject.getEventMethod();
                try {
                    LOGGER.info("redoing download process " + downloadProcess + " from " + state);
                    theMethod.invoke(downloadProcess);
                    state = downloadProcess.getState();
                    LOGGER.info("finishing redo download process " + downloadProcess + " to " + state);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Redo Process Failed");
                }
            }
        }
        StoreHelper.save(allDownloadProcess);
    }

}
