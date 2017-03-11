'use strict';

const realtime = require('./models/realtime.model');
const controller = require('./controllers/realtime.controller');
const hooks = require('./hooks');

module.exports = function() {
  const app = this;

  const options = {};

  // Initialize our service with any options it requires
  app.use('/api/v1/realtime', new controller(options));

  // Get our initialize service to that we can bind hooks
  const realtimeService = app.service('/api/v1/realtime');

  // Set up our before hooks
  realtimeService.before(hooks.before);

  // Set up our after hooks
  realtimeService.after(hooks.after);
};
