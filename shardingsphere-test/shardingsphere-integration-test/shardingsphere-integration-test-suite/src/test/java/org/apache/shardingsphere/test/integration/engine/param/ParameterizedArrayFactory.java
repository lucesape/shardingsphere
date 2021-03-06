/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.integration.engine.param;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.infra.database.type.DatabaseTypeRegistry;
import org.apache.shardingsphere.test.integration.cases.IntegrationTestCaseContext;
import org.apache.shardingsphere.test.integration.cases.IntegrationTestCasesLoader;
import org.apache.shardingsphere.test.integration.cases.SQLCommandType;
import org.apache.shardingsphere.test.integration.cases.assertion.IntegrationTestCaseAssertion;
import org.apache.shardingsphere.test.integration.engine.param.model.AssertionParameterizedArray;
import org.apache.shardingsphere.test.integration.engine.param.model.CaseParameterizedArray;
import org.apache.shardingsphere.test.integration.engine.param.model.ParameterizedArray;
import org.apache.shardingsphere.test.integration.env.IntegrationTestEnvironment;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parameterized array factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ParameterizedArrayFactory {
    
    private static final IntegrationTestCasesLoader TEST_CASES_LOADER = IntegrationTestCasesLoader.getInstance();
    
    private static final IntegrationTestEnvironment ENV = IntegrationTestEnvironment.getInstance();
    
    /**
     * Get assertion parameterized array.
     * 
     * @param sqlCommandType SQL command type
     * @return assertion parameterized array
     */
    public static Collection<Object[]> getAssertionParameterizedArray(final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (IntegrationTestCaseContext each : TEST_CASES_LOADER.getTestCaseContexts(sqlCommandType)) {
            result.addAll(getAssertionParameterizedArray(each, sqlCommandType));
        }
        return toArrays(result);
    }
    
    private static Collection<ParameterizedArray> getAssertionParameterizedArray(final IntegrationTestCaseContext testCaseContext, final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (DatabaseType each : getDatabaseTypes(testCaseContext.getTestCase().getDbTypes())) {
            if (IntegrationTestEnvironment.getInstance().getDataSourceEnvironments().containsKey(each)) {
                result.addAll(getAssertionParameterizedArray(testCaseContext, each, sqlCommandType));
            }
        }
        return result;
    }
    
    private static Collection<ParameterizedArray> getAssertionParameterizedArray(final IntegrationTestCaseContext testCaseContext,
                                                                                 final DatabaseType databaseType, final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (SQLExecuteType each : SQLExecuteType.values()) {
            result.addAll(getAssertionParameterizedArray(testCaseContext, databaseType, each, sqlCommandType));
        }
        return result;
    }
    
    private static Collection<ParameterizedArray> getAssertionParameterizedArray(final IntegrationTestCaseContext testCaseContext,
                                                                                 final DatabaseType databaseType, final SQLExecuteType sqlExecuteType, final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (IntegrationTestCaseAssertion each : testCaseContext.getTestCase().getAssertions()) {
            result.addAll(getAssertionParameterizedArray(testCaseContext, databaseType, sqlExecuteType, each, sqlCommandType));
        }
        return result;
    }
    
    private static Collection<ParameterizedArray> getAssertionParameterizedArray(final IntegrationTestCaseContext testCaseContext, 
                                                                                 final DatabaseType databaseType, final SQLExecuteType sqlExecuteType,
                                                                                 final IntegrationTestCaseAssertion assertion, final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (String adapter : ENV.getAdapters()) {
            result.addAll(getAssertionParameterizedArray(testCaseContext, assertion, adapter, databaseType, sqlExecuteType, sqlCommandType));
        }
        return result;
    }
    
    private static Collection<ParameterizedArray> getAssertionParameterizedArray(final IntegrationTestCaseContext testCaseContext, final IntegrationTestCaseAssertion assertion,
                                                                                 final String adapter, final DatabaseType databaseType,
                                                                                 final SQLExecuteType sqlExecuteType, final SQLCommandType sqlCommandType) {
        return ENV.getScenarios().stream().map(
            each -> new AssertionParameterizedArray(testCaseContext, assertion, adapter, each, databaseType, sqlExecuteType, sqlCommandType)).collect(Collectors.toList());
    }
    
    /**
     * Get case parameterized array.
     *
     * @param sqlCommandType SQL command type
     * @return case parameterized array
     */
    public static Collection<Object[]> getCaseParameterizedArray(final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (IntegrationTestCaseContext each : TEST_CASES_LOADER.getTestCaseContexts(sqlCommandType)) {
            result.addAll(getCaseParameterizedArray(each, sqlCommandType));
        }
        return toArrays(result);
    }
    
    private static Collection<ParameterizedArray> getCaseParameterizedArray(final IntegrationTestCaseContext testCaseContext, final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (DatabaseType each : getDatabaseTypes(testCaseContext.getTestCase().getDbTypes())) {
            if (IntegrationTestEnvironment.getInstance().getDataSourceEnvironments().containsKey(each)) {
                result.addAll(getCaseParameterizedArray(testCaseContext, each, sqlCommandType));
            }
        }
        return result;
    }
    
    private static Collection<ParameterizedArray> getCaseParameterizedArray(final IntegrationTestCaseContext testCaseContext, final DatabaseType databaseType,
                                                                            final SQLCommandType sqlCommandType) {
        Collection<ParameterizedArray> result = new LinkedList<>();
        for (String adapter : ENV.getAdapters()) {
            result.addAll(getCaseParameterizedArray(testCaseContext, adapter, databaseType, sqlCommandType));
        }
        return result;
    }
    
    private static Collection<ParameterizedArray> getCaseParameterizedArray(final IntegrationTestCaseContext testCaseContext, final String adapter,
                                                                            final DatabaseType databaseType, final SQLCommandType sqlCommandType) {
        return ENV.getScenarios().stream().map(each -> new CaseParameterizedArray(testCaseContext, adapter, each, databaseType, sqlCommandType)).collect(Collectors.toList());
    }
    
    private static Collection<DatabaseType> getDatabaseTypes(final String databaseTypes) {
        String candidates = Strings.isNullOrEmpty(databaseTypes) ? "H2,MySQL,Oracle,SQLServer,PostgreSQL" : databaseTypes;
        return Splitter.on(',').trimResults().splitToList(candidates).stream().map(DatabaseTypeRegistry::getActualDatabaseType).collect(Collectors.toList());
    }
    
    private static List<Object[]> toArrays(final Collection<ParameterizedArray> parameterizedArrays) {
        return parameterizedArrays.stream().map(each -> new Object[]{each}).collect(Collectors.toList());
    }
}
