'use strict';

var service = require('../services/listManga.service');

module.exports = function(options) {
  var self = this;

  self.options = options || {};
  self.find = find;
  self.get = get;

  function find(params) {
    console.log(params.query)
    return Promise.resolve(service.list(params))
  }

  function get(id){
    return Promise.resolve(service.getItem(id));
  }
}

