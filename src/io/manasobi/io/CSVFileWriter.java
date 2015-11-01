package io.manasobi.io;

import lombok.extern.slf4j.Slf4j;
import tv.anypoint.domain.ImpressionLog;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by twjang on 15. 10. 21.
 */
@Slf4j
public class CSVFileWriter {

    public void write(String fileName, List<ImpressionLog> messageList) {

        try (FileWriter writer = new FileWriter(fileName, false)) {

            StringBuilder sb = new StringBuilder();

            for (ImpressionLog message : messageList) {

                sb.append(message.getId() + ",");
                sb.append(message.getAsset() + ",");
                sb.append(message.getCampaign() + ",");
                sb.append(message.getCpv() + ",");
                sb.append(message.getCueOwner() + ",");
                sb.append(message.getDevice() + ",");
                sb.append(message.getImpressionTime() + ",");
                sb.append(message.isError() + ",");
                sb.append(message.getPlayTime() + ",");
                sb.append(message.getProgramProvider() + ",");
                sb.append(message.getRegion1() + ",");
                sb.append(message.getRegion2() + ",");
                sb.append(message.getRegion3() + ",");
                sb.append(message.getRegion4() + ",");
                sb.append(message.getServiceOperator() + ",");
                sb.append(message.getVtr() + ",");
                sb.append(message.getZipCode());
                sb.append("\n");
            }

            writer.write(sb.toString());

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /*public static void main(String[] args) {
        String s = "INsert into value/n(#!123)/n,(#!4567)/n,(#!8910)";
        Pattern p = Pattern.compile("/n");
        Matcher m = p.matcher(s);
        int count = 0;
        for( int i = 0; m.find(i); i = m.end())
            count++;

        System.out.println(count); //특정문자열(Pattern)의 갯수
    }*/

}
