/*
 * Copyright 2019, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.core.reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import gyro.core.GyroException;
import gyro.core.Type;
import gyro.core.finder.Finder;
import gyro.core.finder.FinderField;
import gyro.core.finder.FinderSettings;
import gyro.core.finder.FinderType;
import gyro.core.resource.DiffableInternals;
import gyro.core.resource.Resource;
import gyro.core.scope.Scope;
import gyro.lang.ast.value.ReferenceNode;

@Type("external-query")
public class FinderReferenceResolver extends ReferenceResolver {

    @Override
    public Object resolve(ReferenceNode node, Scope scope) {
        validateArguments(node, 1, 2);
        validateOptionArguments(node, "credentials", 0, 1);

        List<Object> arguments = getArguments(scope, node, Object.class);

        String type = (String) arguments.remove(0);

        Class<? extends Finder<Resource>> finderClass = scope.getRootScope()
            .getSettings(FinderSettings.class)
            .getFinderClasses()
            .get(type);

        if (finderClass == null) {
            throw new GyroException(String.format(
                "@|bold %s|@ type doesn't support external queries!",
                type));
        }

        FinderType<? extends Finder<Resource>> finderType = FinderType.getInstance(finderClass);
        Finder<Resource> finder = finderType.newInstance(scope);
        Optional.ofNullable(getOptionArgument(scope, node, "credentials", String.class, 0))
            .ifPresent(finder::setCredentials);
        List<Resource> resources = null;

        if (!arguments.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> filters = (Map<String, Object>) arguments.remove(0);

            if (!filters.isEmpty()) {
                filters = getTranslatedFilters(filters, finderType);
                resources = finder.find(filters);
            }
        }

        if (resources == null) {
            resources = finder.findAll();
        }

        resources.forEach(r -> DiffableInternals.update(r));

        return resources;
    }

    // Translates the filter keys from the query into custom key names If @Filter() annotation present on the key in its finder implementation.
    private Map<String, Object> getTranslatedFilters(Map<String, Object> argumentFilters, FinderType<? extends Finder<Resource>> finderType) {
        Map<String, String> fieldNameMap = finderType.getFields()
            .stream()
            .filter(o -> !o.getFilterName().equals(o.getGyroName()))
            .collect(Collectors.toMap(FinderField::getGyroName, FinderField::getFilterName));

        Map<String, Object> filter = new HashMap<>();
        for (String fieldName : argumentFilters.keySet()) {
            String updatedFieldName = fieldNameMap.getOrDefault(fieldName, fieldName);
            filter.put(updatedFieldName, argumentFilters.get(fieldName));
        }

        return filter;
    }

}
