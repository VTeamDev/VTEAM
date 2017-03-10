'use strict';

const manga = require('./models/manga.model');
const controller = require('./controllers/manga.controller');
const hooks = require('./hooks');

module.exports = function() {
  const app = this;

  const options = {
    paginate: {
      default: 5,
      max: 25,
    }
  };

  // Initialize our service with any options it requires
  app.use('/api/v1/manga', new controller(options));

  // Get our initialize service to that we can bind hooks
  const mangaService = app.service('/api/v1/manga');

  // Set up our before hooks
  mangaService.before(hooks.before);

  // Set up our after hooks
  mangaService.after(hooks.after);
}
