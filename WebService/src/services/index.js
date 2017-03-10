'use strict';
const authentication = require('./authentication');
const user = require('./user');
const mongoose = require('mongoose');


//List manga API
const listManga = require('./listManga');


module.exports = function() {
  const app = this;

  mongoose.connect(app.get('mongodb'));
  mongoose.Promise = global.Promise;

  app.configure(listManga);
  app.configure(authentication);
  app.configure(user);
};
