package pro.hirooka.chukasa.domain.oprator;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.hirooka.chukasa.domain.operator.IEpgOperator;

import static java.util.Objects.requireNonNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EpgOperatorTest {

    @Autowired
    IEpgOperator epgOperator;

    @Ignore
    @Test
    public void test(){
        requireNonNull(epgOperator);
        epgOperator.persist();
        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
