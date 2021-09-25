package com.example.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;


@SpringBootTest(classes = EsApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
class EsApplicationTests {
    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http"),
                    new HttpHost("localhost", 9201, "http"),
                    new HttpHost("localhost", 9202, "http")

            ));


    @Test
    public void getIndex() throws IOException {


        GetIndexRequest test_index = new GetIndexRequest("*");
        GetIndexResponse response = client.indices().get(test_index, RequestOptions.DEFAULT);
        String[] indices = response.getIndices();
        for (String index : indices) {
            System.out.println("索引名字:" + index);
        }
        client.close();
    }

    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        if (delete.isAcknowledged()) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }

    }

    @Test
    public void getById() throws IOException {
        GetRequest product2 = new GetRequest("product2", "2");
        String[] includes = {"name", "desc"};
        String[] excludes = {"tags"};
        FetchSourceContext sourceContext = new FetchSourceContext(true, includes, excludes);
//        product2.fetchSourceContext(sourceContext);
        GetResponse response = client.get(product2, RequestOptions.DEFAULT);
        System.out.println(response);
        client.close();
    }






}


