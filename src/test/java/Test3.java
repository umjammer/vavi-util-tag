/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import vavi.util.box.Box;


/**
 * Test3. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 080313 nsano initial version <br>
 */
public class Test3 {

    static class BoxTest extends Box {
        public static final long qtTimeToLong2(int qtTime) {
            return qtTimeToLong(qtTime);
        }
        public static final int longToQtTime2(long time) {
            return longToQtTime(time);
        }
    }

    /**
     * @param args 0: date, 1: days
     */
    public static void main(String[] args) throws Exception {
        Test3 test = new Test3();
System.err.println("---- t1 ----");
        test.test1(args);
System.err.println("---- t2 ----");
        test.test2(args);
System.err.println("---- t3 ----");
        test.test3(args);
    }

    void test3(String[] args) throws Exception {
        int days = Integer.parseInt(args[1]);
        // times: 4, days: 2, date: 1, off: 0
        int type = 2;
        // times: 1, days: 1, date: 1, off: 0
        int transportFlag = 1;
        int dateLimit = 0;
        int daysLimit = days;
        int timesLimit = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(type);
        dos.writeInt(transportFlag);
        dos.writeInt(dateLimit);
        dos.writeInt(daysLimit);
        dos.writeInt(timesLimit);
        byte[] bytes = baos.toByteArray();
        for (byte b : bytes) {
            System.err.printf("%%%02X", b);
        }
        System.err.println();
System.err.println(days + " days");
    }

    void test2(String[] args) throws Exception {
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(args[0]);
        // times: 4, days: 2, date: 1, off: 0
        int type = 1;
        // times: 1, days: 1, date: 1, off: 0
        int transportFlag = 1;
        int dateLimit = BoxTest.longToQtTime2(date.getTime());
        int daysLimit = 0;
        int timesLimit = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(type);
        dos.writeInt(transportFlag);
        dos.writeInt(dateLimit);
        dos.writeInt(daysLimit);
        dos.writeInt(timesLimit);
        byte[] bytes = baos.toByteArray();
        for (byte b : bytes) {
            System.err.printf("%%%02X", b);
        }
        System.err.println();
long time = BoxTest.qtTimeToLong2(dateLimit);
System.err.printf("%1$tF %1$tT\n", time);
    }

    void test1(String[] args) {
        long now = System.currentTimeMillis();
        System.err.printf("%1$tF %1$tT\n", now);
        int qtTime = BoxTest.longToQtTime2(now);
        System.err.printf("%d\n", qtTime);
        long time = BoxTest.qtTimeToLong2(qtTime);
        System.err.printf("%1$tF %1$tT\n", time);
    }
}

/* */
