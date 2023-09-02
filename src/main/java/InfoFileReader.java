import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class InfoFileReader {
    public static void main(String[] args) {
        try {
            readMetrics(args);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }


    public static void readMetrics(String [] args) throws Exception{
        Map<Integer,Map<Integer,MSGSentReceivedCount>> disseminationMap = new HashMap<>();
        Map<Integer,MSGSentReceivedCount> midsMap = new HashMap<>();
        //String fileName=args[0];
        //String faults = args[1];
        //int processes = Integer.parseInt(args[2]);

        File folder = new File("/home/tsunami/Desktop/thesis_projects/babelCaseStudies/Untitled Folder/babel-case-studies-main/peer-sampling/logs"); // replace with actual folder path
        int sentCount = 0;
        int receivedCount = 0;
        int infoFiles = 0;

        //System.out.println("FILES INFO "+folder.listFiles().length);
        String ss = "Sent:";
        String rr = "Received:";

        for (int i = 0; i < 2; i++) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".info")) {
                    infoFiles++;


                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (i==0 && line.contains(ss)) {
                                line = line.substring(line.indexOf(ss)).replace(ss,"");
                                String [] res = line.split(":");
                                //            logger.info("Sent: {}:{}:{}",mid,sentTime,disseminationTimeInSecond);
                                int mid = Integer.parseInt(res[0].trim());
                                long sentTime = Long.parseLong(res[1].trim());
                                int disseminationTime = Integer.parseInt(res[2].trim());
                                MSGSentReceivedCount o = midsMap.computeIfAbsent(mid, k ->new MSGSentReceivedCount(mid,0,0,sentTime,disseminationTime));
                                disseminationMap.computeIfAbsent(disseminationTime, k -> new HashMap<>()).put(mid,o);
                                o.sentCount++;
                            } else if (i==1 && line.contains(rr)) {
                                //        logger.info("Received: {}:{}",reply.hash,receicedTime);
                                line = line.substring(line.indexOf(rr)).replace(rr,"");
                                String [] res = line.split(":");
                                int mid = Integer.parseInt(res[0].trim());
                                long receivedTime = Long.parseLong(res[1].trim());
                                MSGSentReceivedCount o = midsMap.get(mid);
                                o.deliver(receivedTime);
                                System.out.println();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (Map.Entry<Integer,Map<Integer,MSGSentReceivedCount>> p : disseminationMap.entrySet()) {
            Map<Integer,MSGSentReceivedCount> l = p.getValue();
            int totalReceived = 0;
            long totalLatencies = 0;
            long minSumRTT = Long.MAX_VALUE, maxSumRTT=Long.MIN_VALUE;
            for (MSGSentReceivedCount va : l.values()) {
                totalReceived += va.getReceivedCount();
                totalLatencies += (va.sumElapsed);
                if(va.sumElapsed>maxSumRTT){
                    maxSumRTT = va.sumElapsed;
                }
                if(va.sumElapsed<minSumRTT){
                    minSumRTT = va.sumElapsed;
                }
            }
            String k = String.format("TIME: %s ; SENT_COUNT: %s ; RECEIVED_COUNT; %s ; SENT_MILLIS: %s; SUM_ELAPSED: %s ; min_ELAPSE: %s; max_ELAPSED: %s",
                    p.getKey(),l.size(),totalReceived,-1,(totalLatencies/l.size()),minSumRTT,maxSumRTT);
            System.out.println(k);
        }
    }
}