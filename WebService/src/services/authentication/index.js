'use strict';

const axios = require('axios');
const passport = require('passport'),
  FacebookStrategy = require('passport-facebook').Strategy;
const FacebookTokenStrategy = require('passport-facebook-token');
const userModel = require('../user/models/user.model');
const mongoose = require('mongoose'), User = mongoose.model('user');
const generatePassword = require('password-generator');
const _ = require('lodash');

module.exports = function() {
  const app = this;

  app.use(passport.initialize());
  let config = app.get('auth');

  passport.use(new FacebookStrategy(config.facebook, function(accessToken, refreshToken, profile, done) {
    console.log(accessToken);
    axios.get('https://graph.facebook.com/me?fields=id,name,email&access_token=' + accessToken)
    .then(function(res){
      const data = res.data;
      User.findOne({"facebookId": data.id}, function(err, res){
        if (err) return handleError(err);
        if (res !== null) {
          const user = _.assignIn(res, { access_token: accessToken });
          console.log({ access_token: accessToken });
          return done(null, {user});
        }
        User.create({ facebookId: data.id, name: data.name, email: data.email, password: generatePassword(12, false) })
          .then(function(val){
            val.access_token = accessToken;

            done(null, {user: val});
          })
      })
    })
    .catch(function(err){
      console.error(err);
    })
  }));

  passport.use(new FacebookTokenStrategy(config.facebook, function(accessToken, refreshToken, profile, done) {
    const data = profile._json;
    User.findOne({"facebookId": data.id}, {_id: 0, password: 0}, function(err, res){
        if (err) return handleError(err);
        if (res !== null) {
          const user = _.assignIn(res, { access_token: accessToken });
          console.log({ access_token: accessToken });
          return done(null, user);
        }
        User.create({ facebookId: data.id, name: data.name, email: data.email, password: generatePassword(12, false) })
          .then(function(val){
            val.access_token = accessToken;

            done(null, val);
          })
      })
  }))

  app.get('/auth/facebook', passport.authenticate('facebook', { scope: ['public_profile', 'email'] }));

  app.get('/auth/facebook/callback', passport.authenticate('facebook', {session: false}), function(req, res) {
    res.send(req.user);
  });
  app.get('/auth/facebook/token', passport.authenticate('facebook-token', { session: false }), function (req, res) {
    res.send(req.user);
  });
  app.get('/auth/success', function(req, res){
    res.send(req);
  })
  app.get('/auth/fail', function(req, res) {
    res.send("Lỗi mẹ rồi");
  })
};
