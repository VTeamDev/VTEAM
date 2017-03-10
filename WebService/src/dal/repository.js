const omit = require('lodash.omit');
const PROPERTIES = ['$sort', '$limit', '$skip', '$select', '$populate'];
const errors = require('feathers-errors');

var mongoose = require('mongoose'),
    _ = require('lodash');

var repository = function (modelName) {

    var self = this;

    self.Model = mongoose.model(modelName);

    self.findById = function (id, subDocuments) {
        return self.findOne({
            manga_id: id
        }, subDocuments);
    };

    self.findOne =  function(params, subDocuments) {
        return self._findOne(params, subDocuments, true);
    };

    self.findAll = function (query, filters, subDocuments) {
        const result = self._find(query, filters, subDocuments, true);

        if (!filters.$limit) {
            return result.then(page => page.data);
        }

        return result;
    };

    self.create = function (obj) {
        return self.Model.create(obj);
    };

    self.update = function (id, entity) {
        return self._findOne({ manga_id: id }).then(oldEntity => {
            oldEntity = _.extend(oldEntity, entity);
            return oldEntity.save();
        });
    };

    self.remove = function (id) {
        return self._findOne({ manga_id: id }).then(oldEntity => {
            return oldEntity.remove();
        });
    };

    self._findOne = function(params, subDocuments, lean) {
        var query = self.Model.findOne(params);

        if (subDocuments)
        {
            subDocuments.forEach(function(item) {
                query.populate(item);
            });
        }

        return query.lean(lean).exec().then(data => {
            if (!data) {
                throw new errors.NotFound('No record found');
            }

            return data;
        });
    };

    self._find = function(query, filters, subDocuments, lean) {
        const q = self.Model.find(query).lean(lean);

        if (filters.$select && filters.$select.length) {
            var fields = {};

            for (var key of filters.$select) {
                fields[key] = 1;
            }

            q.select(fields);
        }

        if (filters.$limit) {
            q.limit(filters.$limit);
        }

        if (filters.$skip) {
            q.skip(filters.$skip);
        }

        if (subDocuments) {
            subDocuments.forEach(function(item) {
                q.populate(item);
            });
        }

        if (filters.$populate){
            q.populate(filters.$populate);
        }

        const executeQuery = total => {
            return q.exec().then(data => {
                return {
                    total,
                    limit: filters.$limit,
                    skip: filters.$skip || 0,
                    data
                    };
                });
        };

        if(filters.$limit) {
            return self.Model.where(query).count().exec().then(executeQuery);
        }

        return executeQuery();
    };
};

repository.getModel = function (modelName) {
    return mongoose.model(modelName);
};

module.exports.Repository = repository;

module.exports.isValidId = function (id) {
    return mongoose.Types.ObjectId.isValid(id);
};
