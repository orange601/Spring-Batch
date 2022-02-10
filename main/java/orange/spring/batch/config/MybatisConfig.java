package orange.spring.batch.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan(basePackages = "orange.spring.batch.mapper")
public class MybatisConfig {
    @Bean
    public  SqlSessionFactory sqlSessionFactory(DataSource dataSource)throws Exception {
        final SqlSessionFactoryBean sessionFactory =new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver =new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCallSettersOnNulls(true);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(null);
        sessionFactory.setConfiguration(configuration);
        //sessionFactory.setTypeAliasesPackage("com.iaan.dtd.core.dto.resultMap");
        
        return sessionFactory.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)throws Exception {
      final SqlSessionTemplate sqlSessionTemplate =new SqlSessionTemplate(sqlSessionFactory);
      return sqlSessionTemplate;
    }
 

}
