'use strict'

var path = require('path'),
  queryFilters = require(path.resolve('./src/utils/queryFilters')),
  repository = require(path.resolve('./src/dal/repository')),
  mangaRepository = new repository.Repository('manga');

exports.list = function(params) {
  var qf = queryFilters.create(params.query || {});
  return mangaRepository.findAll(qf.query, qf.filters, null)
}

exports.getItem = function(id) {
  return mangaRepository.findById(id, null)
}
