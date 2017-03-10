'use strict'

var path = require('path'),
  queryFilters = require(path.resolve('./src/utils/queryFilters')),
  repository = require(path.resolve('./src/dal/repository')),
  mangaInfoRepository = new repository.Repository('mangaInfo');

exports.list = function(params) {
  var qf = queryFilters.create(params.query || {});
  return mangaInfoRepository.findAll(qf.query, qf.filters, null)
}

exports.getItem = function(id) {
  return mangaInfoRepository.findById(id, null)
}
