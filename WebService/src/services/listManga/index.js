'use strict';
const listManga = require('./models/listManga.model');
const hooks = require('./hooks');
const controller = require('./controllers/listManga.controller');
const mangaSorted = require('./controllers/MangaSorted.controller');
const searchManga = require('./controllers/searchManga.controller');

module.exports = function() {
  const app = this;

  const options = {

  };

  // Initialize our service with any options it requires
  app.get('/api/v1/list/latest', mangaSorted.latest);
  app.get('/api/v1/list/top', mangaSorted.top);
  app.get('/api/v1/list/recommend', mangaSorted.recommend);
  app.get('/api/v1/list/search', searchManga.search);

  app.use('/api/v1/list', new controller(options));


  // Get our initialize service to that we can bind hooks
  const listMangaService = app.service('/api/v1/list');

  // Set up our before hooks
  listMangaService.before(hooks.before);

  // Set up our after hooks
  listMangaService.after(hooks.after);
}
