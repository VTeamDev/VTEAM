'use strict';

const sortChapters = require('./sortChapters');

const globalHooks = require('../../../hooks');
const hooks = require('feathers-hooks');


exports.before = {
  all: [hooks.remove('_id', 'url')],
  find: [],
  get: [],
  create: [],
  update: [],
  patch: [],
  remove: []
};

exports.after = {
  all: [hooks.remove('_id', 'url')],
  find: [],
  get: [sortChapters()],
  create: [],
  update: [],
  patch: [],
  remove: []
};
