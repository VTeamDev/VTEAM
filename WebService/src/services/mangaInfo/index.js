'use strict';
const mangaInfo = require('./models/mangaInfo.model');
const hooks = require('./hooks');
const controller = require('./controllers/mangaInfo.controller');

module.exports = function() {
  const app = this;

  const options = {

  };

  // Initialize our service with any options it requires
  app.use('/api/v1/info', new controller(options));

  // Get our initialize service to that we can bind hooks
  const mangaInfoService = app.service('/api/v1/info');

  // Set up our before hooks
  mangaInfoService.before(hooks.before);

  // Set up our after hooks
  mangaInfoService.after(hooks.after);
}
