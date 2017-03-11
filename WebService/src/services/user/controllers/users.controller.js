'use strict';

const path = require('path');
const generatePassword = require('password-generator');
const service = require('../services/users.service');

module.exports = function(options) {
    const self = this;

    self.options = options || {};
    self.find = find;
    self.get = get;

    function find(params) {
        return service.list(params);
    }

    function get(id, params){
        return service.getItem(id);
    }
};
