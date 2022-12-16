package org.example.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import org.example.Main;
import org.example.model.ProductInfo;
import org.example.repositories.ProductInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class)
@ContextConfiguration(classes = {ProductInfoRepository.class})
@ActiveProfiles("local")
@TestPropertySource(properties = {"amazon.dynamodb.endpoint=http://localhost:8000/", "amazon.aws.accesskey=test1", "amazon.aws.secretkey=test231"})
public class ProductInfoRepositoryTest {

    @RegisterExtension
    public static LocalDbCreationExtension dynamoDB = new LocalDbCreationExtension();

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    ProductInfoRepository repository;

    private static final String EXPECTED_COST = "20";
    private static final String EXPECTED_PRICE = "50";

    @BeforeEach
    public void setup() throws Exception {

        try {
            dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(ProductInfo.class);

            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

            amazonDynamoDB.createTable(tableRequest);
        } catch (ResourceInUseException e) {
            // Do nothing, table already created
        }

        // TODO How to handle different environments. i.e. AVOID deleting all entries in ProductInfo on table
        dynamoDBMapper.batchDelete((List<ProductInfo>) repository.findAll());
    }

    @Test
    public void givenItemWithExpectedCost_whenRunFindAll_thenItemIsFound() {

        ProductInfo productInfo = new ProductInfo(EXPECTED_COST, EXPECTED_PRICE);
        repository.save(productInfo);

        List<ProductInfo> result = (List<ProductInfo>) repository.findAll();
        assertThat(result.size(), is(greaterThan(0)));
        assertThat(result.get(0).getCost(), is(equalTo(EXPECTED_COST)));
    }
}
