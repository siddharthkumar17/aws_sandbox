package com.sidkumar17.lambdas;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class AddUserHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONParser parser = new JSONParser();
        JSONObject event = null;
        String response = "Success";

        String TABLE_NAME = "userTable";
        JSONObject params = null;
        try {
            event = (JSONObject) parser.parse(reader);
            params = (JSONObject) event.get("queryStringParameters");
        } catch (Exception e) {
            System.out.println("Error" + e.getLocalizedMessage());
        }
        // Write to DynamoDB

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        DynamoDB dynamoDb = new DynamoDB(client);
        try {
            PutItemSpec put = new PutItemSpec()
                    .withItem(new Item()
                            .withString("user_id", params.get("user_id").toString() + System.currentTimeMillis())
                            .withString("name", params.get("name").toString())
                            .withString("location", params.get("location").toString()));

            dynamoDb.getTable(TABLE_NAME).putItem(put);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(response);
        writer.close();
    }
}
