package io.manasobi.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.KryoObjectOutput;
import io.manasobi.utils.DateUtils;
import io.manasobi.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import tv.anypoint.domain.ImpressionLog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by twjang on 15. 10. 22.
 */
@Slf4j
public class DataSetWriter {

    public void write(List<ImpressionLog> obj, String fileName) {

        String fileDir = System.getProperty("user.dir") + "/src/test/resources/dataset/" + DateUtils.getCurrentDateAsString() + "/";

        if (FileUtils.notExistsDir(fileDir)) {
            FileUtils.createDir(fileDir);
        }


        try(FileOutputStream fos = new FileOutputStream(fileDir + fileName);
            ByteBufferOutput output = new ByteBufferOutput(fos)) {

            Kryo kryo = new Kryo();

            KryoObjectOutput objectOutput = new KryoObjectOutput(kryo, output);

            objectOutput.writeObject(obj);

        } catch(IOException e) {

            log.error(e.getMessage());
        }
    }

}