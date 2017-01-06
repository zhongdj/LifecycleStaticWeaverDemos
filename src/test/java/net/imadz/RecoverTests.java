package net.imadz;

import junit.framework.Assert;
import net.imadz.demo.process.DownloadProcessImpl;
import net.imadz.demo.process.IDownloadProcess;
import net.imadz.demo.process.RecoverMaster;
import net.imadz.demo.process.StoreHelper;
import net.imadz.verification.VerificationException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.imadz.demo.process.DownloadProcessImpl.DownloadRequest;

/**
 * Created by Tracy on 06/01/2017.
 */
public class RecoverTests {


    public IDownloadProcess createSampleProcess() {
        DownloadRequest r = new DownloadRequest(" https://download.jetbrains.com/idea/ideaIC-2016.3.2.dmg", "", "ideaIC-2016.3.2.dmg");
        final DownloadProcessImpl process = new DownloadProcessImpl(r, 3);
        final List<IDownloadProcess> list = new ArrayList<IDownloadProcess>();
        list.add(process);
        StoreHelper.save(list);
        return process;
    }

    @Test
    public void should_change_state() throws VerificationException {

        DownloadRequest r = new DownloadRequest(" https://download.jetbrains.com/idea/ideaIC-2016.3.2.dmg", "", "ideaIC-2016.3.2.dmg");
        final DownloadProcessImpl process = new DownloadProcessImpl(r, 3);
        final List<IDownloadProcess> list = new ArrayList<IDownloadProcess>();
        list.add(process);

        //when
        process.prepare();

        //then
        Assert.assertEquals(IDownloadProcess.StateEnum.Queued, process.getState());

        //when
        process.start();

        StoreHelper.save(list);


    }

    @Test
    public void should_recover() throws VerificationException {
        final RecoverMaster master = new RecoverMaster();

        master.corrupt();

        master.recoverAllCorruptedProcesses();
    }

}
