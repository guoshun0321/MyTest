package com.gogo.es;

import com.gogo.util.SafeDataFormat;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHits;

import java.util.Date;

/**
 * Created by 80107436 on 2015-05-11.
 */
public class ESMappingBiz {

    private Client client;

    public void buildIndexSysDm() throws Exception {
        //在本例中主要得注意,ttl及timestamp如何用java ,这些字段的具体含义,请去到es官网查看
        CreateIndexRequestBuilder cib = client.admin().indices().prepareCreate("productindex");
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
//                .startObject("productindex")//

                .startObject("_ttl")//有了这个设置,就等于在这个给索引的记录增加了失效时间,
                        //ttl的使用地方如在分布式下,web系统用户登录状态的维护.
                .field("enabled", true)//默认的false的
                .field("default", "5m")//默认的失效时间,d/h/m/s 即天/小时/分钟/秒
                .field("store", "yes")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("_timestamp")//这个字段为时间戳字段.即你添加一条索引记录后,自动给该记录增加个时间字段(记录的创建时间),搜索中可以直接搜索该字段.
                .field("enabled", true)
                .field("store", "no")
                .field("index", "not_analyzed")
                .endObject()

                .startObject("properties")//properties下定义的title等等就是属于我们需要的自定义字段了,相当于数据库中的表字段 ,此处相当于创建数据库表
                .startObject("title").field("type", "string").field("store", "yes").endObject()
                .startObject("description").field("type", "string").field("index", "not_analyzed").endObject()
                .startObject("price").field("type", "double").endObject()
                .startObject("onSale").field("type", "boolean").endObject()
                .startObject("type").field("type", "integer").endObject()
                .startObject("createDate").field("type", "date").field("format", "YYYYMMddhhMMSS").endObject()
                .endObject()

//                .endObject()
                .endObject();
        cib.addMapping("prindextype", mapping);
        cib.execute().actionGet();
    }

    /**
     * 该方法为增加索引记录
     *
     * @throws Exception
     */
    public void buildIndexSysDm22() throws Exception {
        // productindex为上个方法中定义的索引,prindextype为类型.jk8231为id,以此可以代替memchche来进行数据的缓存
        IndexResponse response = client.prepareIndex("productindex", "prindextype", "jk8231")
                .setSource(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "abcd1")//该字段在上面的方法中mapping定义了,所以该字段就有了自定义的属性,比如 type等
                                .field("description", "中国人3")
                                .field("price", 232)
                                .field("onSale", true)
                                .field("type", 2)
                                .field("createDate", SafeDataFormat.format(new Date(), "YYYYMMddhhMMSS"))
                                .field("dfsfs", "哈哈")//该字段在上面方法中的mapping中没有定义,所以该字段的属性使用es默认的.
                                .endObject()
                )
                .setTTL(8000)//这样就等于单独设定了该条记录的失效时间,单位是毫秒,必须在mapping中打开_ttl的设置开关
                .execute()
                .actionGet();
        IndexResponse response2 = client.prepareIndex("productindex", "prindextype", "jk8234")
                .setSource(XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "abcd2")
                                .field("description", "中国人2")
                                .field("price", 232)
                                .field("onSale", true)
                                .field("type", 22)
                                .field("createDate", SafeDataFormat.format(new Date(), "YYYYMMddhhMMSS"))
                                .endObject()
                )
                .execute()
                .actionGet();

    }

    /**
     * 搜索的使用
     */
    public void exm() {
        System.out.println("删除");
        DeleteResponse responsedd = client.prepareDelete("productindex", "prindextype", "34dds1")
                .setOperationThreaded(false)
                .execute()
                .actionGet();
        System.out.println("根据主键搜索得到值");
        GetResponse responsere = client.prepareGet("productindex", "prindextype", "oht-Sp87SA6J5l3yo9dagw")
                .execute()
                .actionGet();
        System.out.println("完成读取--" + responsere.getSourceAsString());
        System.out.println("搜索");
        SearchRequestBuilder builder = client.prepareSearch("productindex")  //搜索productindex,prepareSearch(String... indices)注意该方法的参数,可以搜索多个索引
                .setTypes("prindextype")
                .setSearchType(SearchType.DEFAULT)
                .setFrom(0)
                .setSize(50);
        QueryBuilder qb2 = QueryBuilders.boolQuery() //  boolQuery() 就相当于 sql中的and
                .must(new QueryStringQueryBuilder("万d 里").field("description"))//QueryStringQueryBuilder是单个字段的搜索条件,相当于组织sql的  where后面的   字段名=字段值
                .should(new QueryStringQueryBuilder("3").field("dfsfs"))
                .must(QueryBuilders.termQuery("dfsfs", "里"));//关于QueryStringQueryBuilder及termQuery等的使用可以使用es插件head来进行操作体会个中query的不同
        builder.setQuery(qb2);
        SearchResponse responsesearch = builder.execute().actionGet();
        System.out.println("" + responsesearch);
        try {
            String jsondata = responsesearch.getHits().getHits()[0].getSourceAsString();
            System.out.println("搜索出来的数据jsondata-- " + jsondata);
        } catch (Exception es) {

        }
    }

    public void queryById() {
//        QueryBuilder qb = QueryBuilders.termQuery("empname", "emp1");
//        SearchResponse resp = client.prepareSearch("dept").setQuery(qb).execute().actionGet();
//        SearchHits hits = resp.getHits();
//        for(int i = 0; i < hits.totalHits(); i++) {
//            System.out.println(hits.getAt(i).getSource());
//        }

        GetResponse responsere = client.prepareGet("productindex", "prindextype", "jk8234")
                .execute()
                .actionGet();
        System.out.println("完成读取--" + responsere.getSourceAsString());
    }

    /**
     * 得到访问es的客户端,我们使用Transport Client
     *
     * @return
     */
    public Client buildClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
//                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch").build();
        Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        return client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public static void main(String[] dfd) {
        ESMappingBiz esm = new ESMappingBiz();
        esm.setClient(esm.buildClient());
        try {
//            esm.buildIndexSysDm();
//            esm.buildIndexSysDm22();
            esm.queryById();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            esm.getClient().close();
        }
    }

}
