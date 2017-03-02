(function () {
    angular.module('CommonFrontend')
        .factory('DataProviderService', DataProviderService);

    DataProviderService.$inject = ['$http', '$rootScope', '$timeout'];

    /**
     * Provides access to backend API.
     */
    function DataProviderService($http, $rootScope, $timeout) {
        /**
         * Session key for requests that need authorization.
         */
        var sessionKey = null;

        /**
         * Configuration for data provider.
         */
        var providerConfig = null;

        var storage = {};

        var jsonFolder = "test_data";

        var relatedEntityParameters = null;

        var actions = {
            'read': getEntityData,
            'create': createEntity,
            'update': updateEntity,
            'delete': deleteEntity,
            'validators': getValidators,
            'readRelatedEntities': getRelatedEntityData
        };

        return {
            getEntityData: getEntityData,
            create: createEntity,
            update: updateEntity,
            delete: deleteEntity,
            executeAction: executeAction,
            setSessionKey: setSessionKey,
            init: init
        };


        function executeAction(entityName, action, data) {
            return actions[action](entityName, data);
        }

        /**
         * Action functions.
         * Begin
         */
        function getEntityData(entityName, data) {
            var req = createReadRequest(entityName, data);
            var loaded = storage[entityName] ? getPromiseWithScopeDigest() : loadEntitiesFromJson(entityName);
            return loaded.then(() => getPreparedEntityDataForReadAction(req, entityName));
        }

        function getRelatedEntityData(entityName, data) {
            var req = createReadRequest(entityName, data);
            var loaded = storage[entityName] ? getPromiseWithScopeDigest() : loadEntitiesFromJson(entityName);
            return loaded.then(() => getPreparedEntityDataForRelatedReadAction(req, entityName));
        }

        function getValidators(entityName) {
            return getPromiseWithScopeDigest(() => getEmptyDataWithTotalCount(entityName));
        }

        function createEntity(entityName, record) {
            record = angular.isDefined(record.entity) ? record.entity : record;
            addToStoredEntities(record, entityName);
            return fillRelatedEntities(entityName)
                .then(() => getEmptyDataWithTotalCount(entityName));
        }

        function updateEntity(entityName, records) {
            records = angular.isDefined(records.entities) ? records.entities : records;
            updateStoredEntities(records, entityName);
            return fillRelatedEntities(entityName)
                .then(() => getEmptyDataWithTotalCount(entityName));
        }

        function deleteEntity(entityName, records) {
            records = angular.isDefined(records.entities) ? records.entities : records;
            deleteFromStoredEntities(records, entityName);
            return getPromiseWithScopeDigest(() => getEmptyDataWithTotalCount(entityName));
        }

        /**
         * Action functions.
         * End
         */

        function loadEntitiesFromJson(entityName) {
            return $http
                .get(jsonFolder + '/' + entityName + '.json')
                .then(response => {
                    storage[entityName] = response.data;
                    return fillRelatedEntities(entityName);
                });
        }

        function createReadRequest(entityName, data) {
            var req = {
                entityName: entityName,
                sessionKey: sessionKey,
                data: data
            };

            req = angular.copy(req);
            if (req.data && req.data.filter) {
                for (var i = 0; i < req.data.filter.length; i++) {
                    var val = req.data.filter[i].value;
                    if (val) {
                        try {
                            var newVal = dateTimeToIso8601(val);
                            req.data.filter[i].value = newVal;
                        } catch (e) { }
                    }
                }
            }
            return req;
        }

        function fillRelatedEntities(entityName) {
            var promices = [];
            var relationships = storage[entityName].relationships;
            relationships.forEach((relationship, i, arr) => {
                promices.push(getEntityData(relationship.relatedEntity, {})
                    .then(result => {
                        relatedEntities = result.data;
                        return storage[entityName].records.forEach((entity, i, arr) => {
                            entity[relationship.relatedEntity] = relatedEntities.find((relatedEntity, index, array) =>
                                entity[relationship.field] == relatedEntity[relationship.relatedEntityField]);
                        });
                    }));
            });
            return Promise.all(promices);
        }

        function getPromiseWithScopeDigest(resolve) {
            return $timeout(resolve);
        }

        function getPreparedEntityDataForReadAction(req, entityName) {
            var data = getEmptyDataWithTotalCount(entityName);
            data.data = getRequestedEntities(req, entityName);
            return data;
        }

        function getPreparedEntityDataForRelatedReadAction(req, entityName) {
            var data = getEmptyDataWithTotalCount(entityName);
            data.data = getFilteredEntitiesFrom(storage[entityName].records, [req.data])
                .map(entity => entity.id);
            return data;
        }

        function getEmptyDataWithTotalCount(entityName) {
            var data = {};
            data.totalCounter = storage[entityName].records.length;
            return data;
        }

        function getRequestedEntities(req, entityName) {
            var newEntities = storage[entityName].records;
            if (req.data.filter) {
                newEntities = getFilteredEntitiesFrom(newEntities, req.data.filter);
            }
            newEntities = getPagedEntitiesFrom(newEntities, req.data.startIndex, req.data.startIndex + req.data.count);
            return newEntities;
        }

        function getFilteredEntitiesFrom(oldEntities, filters) {
            var newEntities = angular.copy(oldEntities);

            filters.forEach((filter, i, arr) => {

                var field = filter.field;
                var operation = filter.operation;
                var value = filter.value;

                if (operation == 'sort') {

                    var isNestedEntity = field.split('.').length > 1;
                    var firstField;
                    var nestedField;
                    if (isNestedEntity) {
                        firstField = field.split('.')[0];
                        nestedField = field.split('.')[1];
                    }

                    newEntities.sort((a, b) => {
                        if (isNestedEntity) {
                            a = a[firstField][nestedField];
                            b = b[firstField][nestedField];
                        } else {
                            a = a[field];
                            b = b[field];
                        }
                        var result = 0;
                        if (a > b) {
                            result = 1;
                        } else if (a < b) {
                            result = -1;
                        }
                        return value ? result : -result;
                    });


                } else if (operation == 'like' && value) {
                    var likeValue = value;
                    if (!Array.isArray(likeValue)) {
                        likeValue = [likeValue];
                    }
                    var filteredEntities = [];
                    likeValue.forEach((v, i, arr) => {
                        v = v.trim();
                        if (v) {
                            filteredEntities = filteredEntities
                                .concat(newEntities.filter(entity => entity[field].search(v) != -1
                                ));
                        }
                    });
                    newEntities = filteredEntities;

                } else if (operation == 'in' && value) {
                    newEntities = newEntities.filter(entity =>
                        value.find((relatedEntityValue, index, array) =>
                            entity[field] == relatedEntityValue
                        ) != undefined
                    );

                } else if (operation == 'eq' && value) {
                    newEntities = newEntities.filter(entity => entity[field] == value);
                }
            });
            return newEntities;
        }

        function getPagedEntitiesFrom(oldEntities, fromIndex, toIndex) {
            if (fromIndex < toIndex) {
                return oldEntities.slice(fromIndex, toIndex);
            } else {
                return oldEntities;
            }

        }

        function deleteFromStoredEntities(entitiesToDelete, entityName) {
            storage[entityName].records = storage[entityName].records.filter(e =>
                entitiesToDelete.find((entityToBeDeleted, index, array) => e.id == entityToBeDeleted.id) == undefined);
        }

        function addToStoredEntities(record, entityName) {
            var newEntity = angular.copy(record);
            var entities = storage[entityName].records;
            newEntity.id = entities[entities.length - 1].id + 1;
            entities.push(newEntity);
        }

        function updateStoredEntities(entitiesToUpdate, entityName) {
            var records = storage[entityName].records;
            entitiesToUpdate.forEach(modifiedEntity => {
                var index = records.findIndex((entity, index, array) => entity.id == modifiedEntity.id);
                if (index != -1) {
                    records[index] = modifiedEntity;
                }
            });
        }

        function setSessionKey(key) {
            sessionKey = key;
        }

        function init(config) {
            providerConfig = config;
        }

        function dateTimeToIso8601(dateTime) {

            var tzOffset = 0 - dateTime.getTimezoneOffset();
            var sign = tzOffset >= 0 ? '+' : '-';
            tzOffset = Math.abs(tzOffset);
            var offsetHours = parseInt(tzOffset / 60);
            var offsetMinutes = tzOffset % 60;

            var formattedDateTime = moment(dateTime).format('YYYY-MM-DDTHH:mm:ss');
            formattedDateTime = formattedDateTime + sign + ("0" + offsetHours).slice(-2) + ":" + ("0" + offsetMinutes).slice(-2);

            return formattedDateTime;
        }
    }
})();
