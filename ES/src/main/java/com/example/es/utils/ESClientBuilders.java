package com.example.es.utils;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * @author gb
 * @version 1.0
 * description:
 * @date 2021/6/26 23:38
 */

public class ESClientBuilders {
    private static final String HOSTS = "localhost:9200,localhost:9201,localhost:9202";

    public static void main(String[] args) {

    }

    /**
     * 初始化 clientBuilder的详细说明
     *
     * @return
     */
    public static RestClientBuilder getClientBuilder() {


        String[] hostNamesPort = HOSTS.split(",");

        String host;
        int port;
        String[] temp;

        RestClientBuilder restClientBuilder = null;

        /*restClient 初始化*/
        if (0 != hostNamesPort.length) {
            for (String hostPort : hostNamesPort) {
                temp = hostPort.split(":");
                host = temp[0].trim();
                port = Integer.parseInt(temp[1].trim());
                restClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
            }
        }

        /*RestClientBuilder 在构建 RestClient 实例时可以设置以下的可选配置参数*/

        /*1.设置请求头，避免每个请求都必须指定*/
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("header", "application/json")
        };
        restClientBuilder.setDefaultHeaders(defaultHeaders);

        /*2.设置在同一请求进行多次尝试时应该遵守的超时时间。默认值为30秒，与默认`socket`超时相同。
            如果自定义设置了`socket`超时，则应该相应地调整最大重试超时。*/


        /*3.设置每次节点发生故障时收到通知的侦听器。内部嗅探到故障时被启用。*/
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                super.onFailure(node);
            }
        });

        /*4.设置修改默认请求配置的回调（例如：请求超时，认证，或者其他
         设置）。
         */
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(10000));

        /*5.//设置修改 http 客户端配置的回调（例如：ssl 加密通讯，线程IO的配置，或其他任何         设置）*/


        // 简单的身份认证
//        final CredentialsProvider credentialsProvider =
//                new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "elaser"));
//
//
//        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//
//            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
//                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                //线程设置
//                httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(10).build());
//                return httpAsyncClientBuilder;
//            }
//        });

        return restClientBuilder;
    }
}