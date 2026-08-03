package dev.muliroz.orderhandler.repository.utils;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class SqlFunctionContributor implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        var typeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();

        functionContributions.getFunctionRegistry().registerPattern(
                "fts_match",
                "?1 @@ ?2",
                typeRegistry.resolve(StandardBasicTypes.BOOLEAN)
        );

        functionContributions.getFunctionRegistry().registerPattern(
                "tsvector_concat",
                "?1 || ?2",
                typeRegistry.resolve(StandardBasicTypes.OBJECT_TYPE)
        );

        functionContributions.getFunctionRegistry().registerPattern(
                "ts_rank",
                "ts_rank(?1, ?2)",
                typeRegistry.resolve(StandardBasicTypes.DOUBLE)
        );

        functionContributions.getFunctionRegistry().registerPattern(
                "setweight",
                "setweight(?1, ?2)",
                typeRegistry.resolve(StandardBasicTypes.OBJECT_TYPE)
        );
    }
}
