const mongoose = require('mongoose');
const moment = require('moment');
const listManga = mongoose.model('listManga');

const select = ['manga_id', 'thumbnail', 'title'];

const skipTop = 90;
const skipLate = 20;

function genSkip(skip, res) {
  listManga.find({}, {_id: 0}).lean(true)
    .select(select)
    .skip(skip)
    .limit(20)
    .then(function(data) {
      res.send({data});
    })
}

exports.latest = function(req, res, next) {
  genSkip(skipLate, res);
}

exports.top = function(req, res, next) {
  genSkip(skipTop, res);
}

exports.recommend = function(req, res, next) {
  genSkip(moment().millisecond()* 2, res);
}

