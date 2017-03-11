'use strict';

var service = require('../services/realtime.service');
var listManga = require('../../listManga/models/listManga.model');
var path = require('path'),
  repository = require(path.resolve('./src/dal/repository')),
  listMangaRepository = new repository.Repository('listManga');

module.exports = function(options) {
    var self = this;

    self.options = options || {};
    self.find = find;
    self.get = get;
    self.create = create;
    self.update = update;
    self.remove = remove;

    function find(params) {
        return Promise.resolve(service.list(params));
    }

    function get(id){
        return Promise.resolve(service.getItem(id));
    }

    function create(data) {
        return Promise.resolve(service.create(data));
    }

    function update(id, data, param) {
        return Promise.resolve(service.update(id, data));
    }

    function remove(id, params) {
        return Promise.resolve(service.remove(id));
    }
};
