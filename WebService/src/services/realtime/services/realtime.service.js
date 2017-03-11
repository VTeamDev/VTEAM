'use strict';

var path = require('path'),
  queryFilters = require(path.resolve('./src/utils/queryFilters')),
  repository = require(path.resolve('./src/dal/repository')),
  realtimeRepository = new repository.Repository('realtime');

exports.create = function(entity) {
    return new Promise(function(resolve, reject) {
      realtimeRepository.create(entity)
      .then(function(res){
        resolve(res);
      })
      .catch(function(err){
        realtimeRepository.findOne({user: entity.user})
          .then(function(item){
            return item._id;
          })
          .then(function(itemId){
            return realtimeRepository.update(itemId, entity);
          })
          .then(function(response){
            resolve(response);
          })
          .catch(function(err){
            reject(err)
          })
      })
    })
};

exports.update = function(id, entity) {
    return realtimeRepository.update(id, entity);
};

exports.remove = function(id) {
    return realtimeRepository.remove(id);
};

exports.list = function(params) {
    var qf = queryFilters.create(params.query || {});
    return realtimeRepository.findAll(qf.query, qf.filters, [
      {
        path: 'manga',
        select: 'title',
      },
      'user',
    ]);
};

exports.getItem = function(id) {
    return realtimeRepository.findOne({_id: id}, [
      {
        path: 'manga',
        select: 'title',
      },
      {
        path: 'user',
      }
    ]);
};

exports.getDouble = function(userId) {
  return realtimeRepository.findOne({user: userId});
}
