package cn.itcast.hotel;


import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class HotelSearchTest {
    private static RestHighLevelClient client;

    @Test
    void testMatchAll() throws IOException {
//        1、准备Request
        SearchRequest request = new SearchRequest("hotel");

//        2、准备DSL
        request.source().query(QueryBuilders.matchAllQuery());

//        3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

//        4、解析响应
        SearchHits searchHits = response.getHits();
//        4.1获取总条数
        long total = searchHits.getTotalHits().value;

        System.out.println("共搜索到：" + total +"条数据");
//        4.2 文档数组
        SearchHit[] hits = searchHits.getHits();

//        4.3 遍历
        for (SearchHit hit : hits) {
//        获取source
            String json = hit.getSourceAsString();
//         反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            System.out.println("hotelDoc = " + hotelDoc);
        }
        System.out.println(response);
    }


    @Test
    void testMatch() throws IOException {
//        1、准备Request
        SearchRequest request = new SearchRequest("hotel");

//        2、准备DSL
        request.source().query(QueryBuilders.matchQuery("all","如家"));

//        3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
        System.out.println(response);
    }

    @Test
    void testBool() throws IOException {
//        1、准备Request
        SearchRequest request = new SearchRequest("hotel");

//        2、准备DSL
//        2.1 准备BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        2.2 添加term
        boolQuery.must(QueryBuilders.termQuery("city","深圳"));
//        2.3 添加range
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(250));
        request.source().query(boolQuery);

//        3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
        System.out.println(response);
    }


    @Test
    void testPageAndSort() throws IOException {
//        页码 ， 每页大小
        int page = 2, size = 5;

//        1、准备Request
        SearchRequest request = new SearchRequest("hotel");

//        2、准备DSL
//        2.1 query
        request.source().query(QueryBuilders.matchAllQuery());

//        2.2 排序 sort
        request.source().sort("price", SortOrder.ASC);
//        2.3 分页 from 、 size
        request.source().from((page - 1) * size).size(5);


//        3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
        System.out.println(response);
    }

    @Test
    void tesHighlight() throws IOException {
//        1、准备Request
        SearchRequest request = new SearchRequest("hotel");

//        2、准备DSL
//        2.1 query
        request.source().query(QueryBuilders.matchQuery("all","如家"));
//        2.2 高亮
        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));


//        3、发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
        System.out.println(response);
    }

    private static void handleResponse(SearchResponse response) {
//        4、解析响应
        SearchHits searchHits = response.getHits();
//        4.1获取总条数
        long total = searchHits.getTotalHits().value;

        System.out.println("共搜索到：" + total +"条数据");
//        4.2 文档数组
        SearchHit[] hits = searchHits.getHits();

//        4.3 遍历
        for (SearchHit hit : hits) {
//        获取source
            String json = hit.getSourceAsString();
//         反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

//            获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if ( !CollectionUtils.isEmpty(highlightFields)){
//            根据字段名称获取高亮结果
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null){
//            获取高亮值
                    String name = highlightField.getFragments()[0].string();
//            覆盖非高亮结果
                    hotelDoc.setName(name);
                }
            }
            System.out.println("hotelDoc = " + hotelDoc);
        }
    }

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.96.130:9200")));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}
