'use strict'

var path = require('path'),
  queryFilters = require(path.resolve('./src/utils/queryFilters')),
  repository = require(path.resolve('./src/dal/repository')),
  listMangaRepository = new repository.Repository('listManga');

exports.list = function(params) {
  var qf = queryFilters.create(params.query || {});
  return listMangaRepository.findAll(qf.query, qf.filters, null)
}

exports.getItem = function(id) {
  return listMangaRepository.findById(id, null)
}
