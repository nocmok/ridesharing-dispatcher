package com.nocmok.orp.solver.kt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

public class TreeScheduleDeserializerTest {

    private ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Test
    public void testDeserializeSimpleSchedule() throws Exception {
        String json = "{\"schedule\":" +
                "[{\"deadline\":1651609889.895000000,\"load\":1,\"nodeId\":\"1234\",\"latitude\":56.4324,\"longitude\":37.5353,\"kind\":\"PICKUP\",\"orderId\":\"1234\"}," +
                "{\"deadline\":1651609889.895000000,\"load\":1,\"nodeId\":\"33523\",\"latitude\":56.542234,\"longitude\":37.234523,\"kind\":\"DROPOFF\",\"orderId\":\"1234\"}]," +
                "\"tree\":[[0,1]]}," +
                "\"@class\":\"com.nocmok.orp.solver.kt.TreeSchedule\"";

        var schedule = objectMapper.readValue(json, TreeSchedule.class);
    }

    @Test
    public void testDeserializeComplexSchedule() throws Exception {
        String json = "{\"schedule\":" +
                "[{\"deadline\":1651609889.895000000,\"load\":1,\"nodeId\":\"1234\",\"latitude\":56.4324,\"longitude\":37.5353,\"kind\":\"PICKUP\",\"orderId\":\"1\"}," +
                "{\"deadline\":1651609889.895000000,\"load\":1,\"nodeId\":\"33523\",\"latitude\":56.542234,\"longitude\":37.234523,\"kind\":\"DROPOFF\",\"orderId\":\"1\"}," +
                "{\"deadline\":1651609889.895000000,\"load\":1,\"nodeId\":\"132134\",\"latitude\":56.543324,\"longitude\":37.545353,\"kind\":\"PICKUP\",\"orderId\":\"2\"}," +
                "{\"deadline\":1651609889.895000000,\"load\":1,\"nodeId\":\"333223\",\"latitude\":56.5423434,\"longitude\":37.22345523,\"kind\":\"DROPOFF\",\"orderId\":\"2\"}]," +
                "\"tree\":[[0,1,2,3],[0,2,1,3],[0,2,3,1],[2,0,1,3],[2,0,3,1],[2,3,0,1]]}," +
                "\"@class\":\"com.nocmok.orp.solver.kt.TreeSchedule\"";

        var schedule = objectMapper.readValue(json, TreeSchedule.class);
        schedule.empty();
    }
}
