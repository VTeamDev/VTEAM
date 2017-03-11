'use strict';

const globalHooks = require('../../../hooks');
const hooks = require('feathers-hooks');

exports.before = {
  all: [hooks.remove('url')],
  find: [],
  get: [],
  create: [],
  update: [],
  patch: [],
  remove: []
};

exports.after = {
  all: [hooks.remove('url')],
  find: [],
  get: [],
  create: [],
  update: [],
  patch: [],
  remove: []
};
