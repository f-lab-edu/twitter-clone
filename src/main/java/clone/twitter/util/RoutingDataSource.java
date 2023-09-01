package clone.twitter.util;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        boolean isCurrentTransactionReadOnly
                = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        return isCurrentTransactionReadOnly
                ? DataSourceConstant.REPLICA_MYSQL
                : DataSourceConstant.PRIMARY_MYSQL;
    }
}
