package kitchenpos.dao;

import static kitchenpos.dao.DomainCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import kitchenpos.domain.Product;

@JdbcTest
class JdbcTemplateProductDaoTest {
    private JdbcTemplateProductDao productDao;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:delete.sql")
            .addScript("classpath:initialize.sql")
            .build();
        productDao = new JdbcTemplateProductDao(dataSource);
    }

    @Test
    void save() {
        Product product = createProduct("product", BigDecimal.valueOf(1000));
        Product savedProduct = productDao.save(product);

        assertAll(
            () -> assertThat(savedProduct.getId()).isNotNull(),
            () -> assertThat(savedProduct.getName()).isEqualTo(product.getName()),
            () -> assertThat(savedProduct.getPrice().toBigInteger()).isEqualTo(product.getPrice().toBigInteger())
        );
    }

    @Test
    void findById() {
        Product product = productDao.save(createProduct("product", BigDecimal.valueOf(16000)));
        Product expectedProduct = productDao.findById(product.getId()).get();

        assertAll(
            () -> assertThat(expectedProduct.getId()).isEqualTo(product.getId()),
            () -> assertThat(expectedProduct.getName()).isEqualTo(product.getName()),
            () -> assertThat(expectedProduct.getPrice().toBigInteger()).isEqualTo(product.getPrice().toBigInteger())
        );
    }

    @Test
    void findAll() {
        productDao.save(createProduct("product", BigDecimal.valueOf(16000)));
        productDao.save(createProduct("product", BigDecimal.valueOf(16000)));

        List<Product> products = productDao.findAll();

        assertThat(products.size()).isEqualTo(2);
    }
}
