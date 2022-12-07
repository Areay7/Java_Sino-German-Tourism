package cn.itcast.hotel;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@MapperScan("cn.itcast.hotel.mapper")
@SpringBootApplication
@Slf4j
public class HotelDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelDemoApplication.class, args);
                log.info("项目启动成功...");
    }

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.96.130:9200")));
    }
}
