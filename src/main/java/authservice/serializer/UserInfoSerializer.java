package authservice.serializer;

import authservice.model.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;


// A custom serializer for UserInfoDto objects to be used with Kafka. As Kafka
// requires data to be in byte array format, this serializer converts UserInfoDto
public class UserInfoSerializer implements Serializer<UserInfoDto> {
    @Override
    public void configure(Map configs, boolean isKey) {
//        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoDto o) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(o).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public void close() {
//        Serializer.super.close();
    }
}
