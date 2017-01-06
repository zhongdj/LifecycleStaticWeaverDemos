package net.imadz.demo.process;

import java.io.*;
import java.util.List;

public class StoreHelper {

    public static final String DATA_STORE_PATH = "dataStore";

    public static List<IDownloadProcess> list() {
        ObjectInputStream ois = null;
        try {
            final File persistent = new File(DATA_STORE_PATH);
            System.out.println(persistent.getAbsolutePath());
            ois = new ObjectInputStream(new FileInputStream(persistent));
            Object result = ois.readObject();
            return (List<IDownloadProcess>) result;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (null != ois) {
                try {
                    ois.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void save(List<IDownloadProcess> list) {
        final File persistent = new File(DATA_STORE_PATH);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(persistent));
            oos.writeObject(list);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (null != oos) {
                try {
                    oos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
