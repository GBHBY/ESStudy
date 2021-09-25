package com.msb.es;

import com.google.gson.Gson;
import com.msb.es.entity.Product;
import com.msb.es.service.ProductService;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SpringBootTest
class EsApplicationTests {
    @Resource
    private ProductService service;

    @Test
    @SneakyThrows
    void esCRUD() {


        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();
        TransportClient client = new PreBuiltTransportClient(settings)
//        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))//通讯端口  而不是服务端口
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));
        //导入数据
//        create(client);
        //查询
        //get(client);
        //getAll(client);
//        update(client);
        //delete(client);
        client.close();
        System.out.println(client);
        //Add transport addresses and do something with the client...
    }

//    region create
   /*     @SneakyThrows
    private void create(TransportClient client) {
        IndexResponse response = client.prepareIndex("product2", "_doc", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "nfc phone")
                        .field("desc", "shouji zhong de hongzhaji")
                        .field("price", 2999)
                        .field("date", "2020-05-19")
                        .field("tags", new String[]{
                                "xingjiabi", "fashao2", "menjinka"
                        })
                        .endObject())
                .get();
        System.out.println(response.getResult());
    }*/
    //endregion

    //region create
    @SneakyThrows
    private void create(TransportClient client) {
        List<Product> list = service.list();
        for (Product item : list) {
            System.out.println(item.getCreateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            IndexResponse response = client.prepareIndex("product2", "_doc", item.getId().toString())
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("name", item.getName())
                            .field("desc", item.getDesc())
                            .field("price", item.getPrice())
                            .field("date", item.getCreateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .field("tags", item.getTags().split(","))
                            .endObject())
                    .get();
            System.out.println(response.getResult());
        }
    }
    //endregion

    //region get
    /*
     * 功能描述: <br>
     * 〈〉
     * @Param: [client]
     * @Return: void
     * @Author: wulei
     * @Date: 2020/6/16 23:28
     */
    @SneakyThrows
    private void get(TransportClient client) {
        GetResponse response = client.prepareGet("product2", "_doc", "1").get();
        String index = response.getIndex();//获取索引名称
        String type = response.getType();//获取索引类型
        String id = response.getId();//获取索引id
        System.out.println("index:" + index);
        System.out.println("type:" + type);
        System.out.println("id:" + id);
        System.out.println(response.getSourceAsString());
    }
    //endregion

    //region getAll
    private void getAll(TransportClient client) {
        SearchResponse response = client.prepareSearch("product2")
                .get();
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String res = hit.getSourceAsString();
            System.out.println("res" + res);
        }
    }
    //endregion

    //region update
    @SneakyThrows
    private void update(TransportClient client) {
        UpdateResponse response = client.prepareUpdate("product2", "_doc", "2")
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "update name")
                        .endObject())
                .get();
        System.out.println(response.getResult());
    }
    //endregion

    //region delete
    @SneakyThrows
    private void delete(TransportClient client) {
        DeleteResponse response = client.prepareDelete("product2", "_doc", "2").get();
        System.out.println(response.getResult());
    }
    //endregion

    //region multiSearch
    /*
     * 功能描述: <br>
     * 〈多条件查找〉
     * @Param: []
     * @Return: void
     * @Author: wulei
     * @Date: 2020/6/17 10:02
     */
    @Test
    @SneakyThrows
    void multiSearch() {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));

        SearchResponse response = client.prepareSearch("product2")
                .setQuery(QueryBuilders.termQuery("name", "xiaomi"))                 // Query
                .setPostFilter(QueryBuilders.rangeQuery("price").from(0).to(4000))
                .setFrom(1).setSize(3)
                .get();
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String res = hit.getSourceAsString();
            System.out.println("res" + res);
        }
        client.close();
    }
    //endregion

    //region 聚合查询
    /*
     * 功能描述: <br>
     * 〈多条件查找〉
     * @Param: []
     * @Return: void
     * @Author: wulei
     * @Date: 2020/6/17 10:02
     */
    @Test
    @SneakyThrows
    void aggSearch() {
        //region 1->创建客户端连接
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));
        //endregion

        //region 2->计算并返回聚合分析response对象
        SearchResponse response = client.prepareSearch("product2")
                .addAggregation(
                        AggregationBuilders.dateHistogram("group_by_month")
                                .field("date")
                                .calendarInterval(DateHistogramInterval.MONTH)
//                                .dateHistogramInterval(DateHistogramInterval.MONTH)
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("by_tag")
                                                .field("tags.keyword")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .avg("avg_price")
                                                                .field("price")
                                                )
                                )
                )
                .execute().actionGet();
        //endregion


        //region 3->输出结果信息
        SearchHit[] hits = response.getHits().getHits();
        Map<String, Aggregation> map = response.getAggregations().asMap();
        Aggregation group_by_month = map.get("group_by_month");
        Histogram dates = (Histogram) group_by_month;
        Iterator<Histogram.Bucket> buckets = (Iterator<Histogram.Bucket>) dates.getBuckets().iterator();

        while (buckets.hasNext()) {
            Histogram.Bucket dateBucket = buckets.next();
            System.out.println("\n\n月份：" + dateBucket.getKeyAsString() + "\n计数：" + dateBucket.getDocCount());
            Aggregation group_by_tag = dateBucket.getAggregations().asMap().get("by_tag");
            StringTerms terms = (StringTerms) group_by_tag;
            Iterator<StringTerms.Bucket> tagsBucket = terms.getBuckets().iterator();
            while (tagsBucket.hasNext()) {
                StringTerms.Bucket tagBucket = tagsBucket.next();
                System.out.println("\t标签名称：" + tagBucket.getKey() + "\n\t数量：" + tagBucket.getDocCount());
                Aggregation avg_price = tagBucket.getAggregations().get("avg_price");
                Avg avg = (Avg) avg_price;
                System.out.println("\t平均价格：" + avg.getValue() + "\n");
            }
        }
        //endregion

        client.close();


    }
    //endregion


    //*******************************************************!!!!!!!!!!!!!!!***************************************************************
    @Test
    @SneakyThrows
    public void createIndex() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        CreateIndexRequest request = new CreateIndexRequest("test_index");

        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        if (createIndexResponse.isAcknowledged()) {
            System.out.println("创建index成功!");
        } else {
            System.out.println("创建index失败!");
        }

        client.close();
    }

    @Test
    @SneakyThrows
    public void getIndex() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        GetIndexRequest request = new GetIndexRequest("test_index*");
        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
        String[] indices = response.getIndices();
        for (String indexName : indices) {
            System.out.println("index name:" + indexName);
        }
        client.close();
    }

    @Test
    @SneakyThrows
    public void delIndex() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        DeleteIndexRequest request = new DeleteIndexRequest("test_index");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        if (response.isAcknowledged()) {
            System.out.println("删除index成功!");
        } else {
            System.out.println("删除index失败!");
        }
        client.close();
    }

    @Test
    @SneakyThrows
    public void insertData() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        List<Product> list = service.list();

        //插入数据，index不存在则自动根据匹配到的template创建。index没必要每天创建一个，如果是为了灵活管理，最低建议每月一个 yyyyMM。
        IndexRequest request = new IndexRequest("test_index");
        //最好不要自定义id 会影响插入速度。
        Product product = list.get(0);
        Gson gson = new Gson();
        request.id(product.getId().toString());
        request.source(gson.toJson(product)
                , XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response);
        client.close();
    }
    @Test
    @SneakyThrows
    public void batchInsertData() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        //批量插入数据，更新和删除同理
        BulkRequest request = new BulkRequest("test_index");
        Gson gson = new Gson();
        Product product = new Product();
        product.setPrice(3999.00);
        product.setDesc("xioami");
        for (int i = 0; i < 10; i++) {
            product.setName("name" + i);
            request.add(new IndexRequest().source(gson.toJson(product)
                    , XContentType.JSON));
        }
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println("数量:" + response.getItems().length);
        client.close();
    }

    @Test
    @SneakyThrows
    public void getById() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        //注意 这里查询使用的是别名。
        GetRequest request = new GetRequest("test_index", "PPWhwnIBRwX67j4bTmV1");
        String[] includes = {"name", "price"};
        String[] excludes = {"desc"};
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        //只查询特定字段。如果需要查询所有字段则不设置该项。
        request.fetchSourceContext(fetchSourceContext);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response);
        client.close();

    }

    @Test
    public void delById() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        DeleteRequest request = new DeleteRequest("test_index", "1");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
        client.close();
    }

    @Test
    public void multiGetById() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        //多个根据id查询
        MultiGetRequest request = new MultiGetRequest();
        request.add("test_index", "PPWhwnIBRwX67j4bTmV1");
        //两种写法
        request.add(new MultiGetRequest.Item(
                "test_index",
                "PfWhwnIBRwX67j4bTmV1"));
        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
        for (MultiGetItemResponse itemResponse : response) {
            System.out.println(itemResponse.getResponse().getSourceAsString());
        }
        client.close();
    }

    @Test
    public void updateByQuery() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        UpdateByQueryRequest request = new UpdateByQueryRequest("test_index");
        //默认情况下，版本冲突会中止 UpdateByQueryRequest 进程，但是你可以用以下命令来代替
        //设置版本冲突继续
//        request.setConflicts("proceed");
        //设置更新条件
        request.setQuery(QueryBuilders.matchQuery("name","name1 name3"));
//        //限制更新条数
//        request.setMaxDocs(10);
        request.setScript(
                new Script(ScriptType.INLINE, "painless", "ctx._source.desc+='#';", Collections.emptyMap()));
        BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
        System.out.println(response);
        client.close();
    }

}
