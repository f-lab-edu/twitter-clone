// 테스트 v.3.1: mysql replication 제거
//
//package clone.twitter.config;
//
//import java.util.Properties;
//import javax.sql.DataSource;
//import lombok.RequiredArgsConstructor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//@MapperScan(basePackages = "clone.twitter.repository")
//public class MyBatisConfig {
//
//    private final ApplicationContext applicationContext;
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(
//            @Qualifier(value = "routingDataSource") DataSource dataSource) throws Exception {
//
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//
//        Properties myBatisConfigurationProperties = new Properties();
//        myBatisConfigurationProperties.setProperty("mapUnderscoreToCamelCase", "true");
//
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setConfigurationProperties(myBatisConfigurationProperties);
//        sqlSessionFactoryBean.setTypeAliasesPackage("clone.twitter.domain");
//        sqlSessionFactoryBean.setMapperLocations(
//                applicationContext.getResources("classpath:mapper/**/*.xml"));
//
//        return sqlSessionFactoryBean.getObject();
//    }
//
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}
//